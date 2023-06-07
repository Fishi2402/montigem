@echo off

REM Check if the number of parameters is less than 2 or if the first two parameters are empty
if "%~2"=="" (
    echo Usage: %~nx0 ^<user^> ^<pass^> [folder]
    exit /b 1
)

REM Check if the third parameter is provided, otherwise set it to the home directory
set "folder=%~3"
if "%folder%"=="" set "folder=%USERPROFILE%"

REM Create the output file path
set "output_file=%folder%\.npmrc"

REM Extract the first and second parameters
set "user=%~1"
set "pass=%~2"

REM Concatenate the parameters with a colon separator
set "concatenated_params=%user%:%pass%"

REM Encode the concatenated parameters using base64 encoding
powershell -Command "[System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes('%concatenated_params%'))" > encoded.txt
set /p "encoded_params=" < encoded.txt
del encoded.txt

REM Write the parameters to the output file
echo @monticore:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/ > "%output_file%"
echo @umlp:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots >> "%output_file%"
echo _auth="%encoded_params%" >> "%output_file%"

REM Print the location of the output file
echo Output file created at: %output_file%

