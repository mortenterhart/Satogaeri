@echo off

SET "program_name=Satogaeri"
SET "main_class_java_path=main.Main"
SET "main_class_file_path=src\main\Main.java"
SET "output_folder=build"
SET "script_directory=%~dp0"

WHERE /Q javac
IF ERRORLEVEL 1 (
    ECHO error: No Java installation was found! Make sure Java 8 or newer is installed correctly >&2
    ECHO error: and visible inside your PATH variable. >&2

    PAUSE
    EXIT 2
)

IF NOT EXIST "src" (
    ECHO error: You are not in the correct root directory. Please execute this script from >&2
    ECHO error: %script_directory% >&2

    PAUSE
    EXIT 2
)

ECHO [%program_name%]: Compiling Java sources ...
javac -d "%output_folder%" -classpath "src" "%main_class_file_path%"

IF ERRORLEVEL 0 IF NOT ERRORLEVEL 1 (
    ECHO [%program_name%]: Compiled bytecode was moved to 'build' folder.
    ECHO.

    ECHO [%program_name%]: Executing %program_name%
    java -classpath "%output_folder%" "%main_class_java_path%"

    PAUSE
    EXIT %ERRORLEVEL%
) ELSE (
    ECHO [%program_name%]: Failed to compile the Java sources! Exiting ...
    PAUSE
    EXIT 1
)
