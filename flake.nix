{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs = {
    self,
    flake-parts,
    ...
  } @ inputs:
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = ["x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin"];

      perSystem = {
        config,
        lib,
        pkgs,
        system,
        ...
      }: let
        javaVersion = 21;

        jdk = pkgs."temurin-bin-${toString javaVersion}";
        gradle = pkgs.gradle.override { java = jdk; };
        profilers = with pkgs; [ async-profiler ];
       in {
         devShells.default = pkgs.mkShell {
           name = "hope";
           packages = with pkgs; [git gradle jdk ] ++ profilers;

           shellHook = ''
             export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:${lib.makeLibraryPath profilers}"
             gradle setup
           '';
         };
       };
    };
}