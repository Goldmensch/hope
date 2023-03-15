/*
 *     Hope - A minecraft server reimplementation
 *     Copyright (C) 2023 Nick Hensel and contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.madethoughts.hope.processor.configuration;

import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generates toml configuration implementations for interfaces annotated with @Configuration.
 * Each abstract method corresponds to a toml value of pattern: path.method_name_as_snake_case.
 * If a method returns another interface it will be implemented equally, but the method name is appended to the path
 * in snake case before. All generated methods have a fallback to the default config that is passed to the
 * implementation.
 */
@SupportedAnnotationTypes("io.github.madethoughts.hope.configuration.Configuration")
@SupportedSourceVersion(SourceVersion.RELEASE_19)
public class ConfigProcessor extends AbstractProcessor {

    private final Map<Element, JavaFile> generatedClasses = new ConcurrentHashMap<>();

    private Messager messager;
    private Types types;
    private Elements elements;

    private Filer filer;

    private String currentPath;
    private ConfigWriter currentWriter;

    public static String camelToSnake(String str) {
        var regex = "([a-z])([A-Z]+)";
        return str
                .replaceAll(regex, "$1_$2")
                .toLowerCase();
    }

    private static <T> T unsupportedTypeException(TypeMirror mirror) {
        throw new UnsupportedOperationException(
                "The type %s is unsupported as a return type".formatted(mirror));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.messager = processingEnv.getMessager();
        this.types = processingEnv.getTypeUtils();
        this.elements = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;
        TypeElement configAnnotation = annotations.iterator().next(); // must have one annotation

        for (var element : roundEnv.getElementsAnnotatedWith(configAnnotation)) {
            if (element.getKind() != ElementKind.INTERFACE) {
                messager.printError("Only interfaces can be annotated.", element);
                return true;
            }
            var javaFile = processRoot((TypeElement) element, null);
            if (javaFile == null) return true; // if error occurs exit
        }

        return true;
    }

    /**
     * Generates a new interface implementation
     *
     * @param element The interface
     * @param path    the curren path, null if root (@Configuration annotated class)
     * @return the generated JavaFile
     */
    private JavaFile processRoot(TypeElement element, String path) {
        var oldPath = currentPath;
        var oldWriter = currentWriter;

        currentPath = path;
        currentWriter = new ConfigWriter(element, elements);

        var generated = generatedClasses.computeIfAbsent(element, key -> {

            element.getEnclosedElements()
                   .forEach(this::processMethod);

            try {
                return currentWriter.generate(filer);
            } catch (IOException e) {
                messager.printError("Failed to save generated file: %s".formatted(e.toString()));
                return null;
            }
        });

        currentPath = oldPath;
        currentWriter = oldWriter;
        return generated;
    }

    /**
     * Processes an interface's method and generated an implementation according to the description above.
     *
     * @param element the method
     */
    private void processMethod(Element element) {
        var modifiers = element.getModifiers();
        if (element instanceof ExecutableElement method && !modifiers.contains(Modifier.STATIC) &&
            !modifiers.contains(Modifier.DEFAULT)) {
            var returnType = method.getReturnType();
            var returnElement = types.asElement(returnType);
            var methodName = camelToSnake(method.getSimpleName().toString());
            var name = currentPath != null
                       ? "%s.%s".formatted(currentPath, methodName)
                       : methodName;

            var tomlType = tomlKind(returnType);
            if (tomlType != null) {
                currentWriter.addProperty(new PropertyDescriptor(name, method, tomlType));
            } else if (returnElement.getKind() == ElementKind.INTERFACE) {
                var javaFile = processRoot((TypeElement) returnElement, name);
                if (javaFile == null) return; // exit if error
                currentWriter.addDelegate(method, javaFile);
            }
        }
    }

    /**
     * Maps a TypeMirror to it's corresponding toml data type. Currently, not all types are supported.
     *
     * @param typeMirror the TypeMirror
     * @return the toml data type
     */
    private TomlKind tomlKind(TypeMirror typeMirror) {
        var kind = typeMirror.getKind();
        return switch (kind) {
            case BYTE, INT, SHORT, LONG -> TomlKind.INTEGER;
            case FLOAT, DOUBLE -> TomlKind.FLOAT;
            case DECLARED -> {
                if (isString(typeMirror)) yield TomlKind.STRING;
                yield null;
            }
            default -> unsupportedTypeException(typeMirror);
        };
    }

    /**
     * Tests if this TypeMirror is a String
     *
     * @param mirror the TypeMirror
     * @return whether it's a String
     */
    private boolean isString(TypeMirror mirror) {
        var stringElement = elements.getTypeElement(String.class.getCanonicalName()).asType();
        return types.isSameType(stringElement, mirror);
    }

}
