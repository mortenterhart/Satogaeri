#!/bin/bash

program_name="Satogaeri";
main_class_java_path="main.Main";
main_class_file_path="src/main/Main.java";
source_folder="src";
output_folder="build";

if ! type javac > /dev/null 2>&1; then
    printf "error: No Java installation was found! Make sure Java 8 or newer is installed correctly\n" >&2;
    printf "error: and visible inside your PATH variable.\n" >&2;

    exit 2;
fi

if [ -f "${main_class_file_path}" ]; then
    printf "[%s]: Compiling Java sources ...\n" "${program_name}";

    javac -d "${output_folder}" -classpath "${source_folder}" "${main_class_file_path}";
    if [ $? -eq 0 ]; then
        printf "[%s]: Compiled bytecode was moved to '%s' folder.\n\n" "${program_name}" "${output_folder}";
        printf "[%s]: Executing %s\n" "${program_name}" "${program_name}";

        java -classpath "${source_folder}:${output_folder}" "${main_class_java_path}";
        exit $?;
    else
        printf "[%s]: Failed to compile the Java sources! Exiting ...\n";
        exit 1;
    fi
else
    printf "error: You are not in the correct root directory. Please execute this script from\n" >&2;
    printf "error: %s\n" "$(dirname "$0")" >&2;
    exit 2;
fi
