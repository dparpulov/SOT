@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  webshopClient startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and WEBSHOP_CLIENT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\client.jar;%APP_HOME%\lib\serviceModel.jar;%APP_HOME%\lib\jaxrs-ri-2.28.jar;%APP_HOME%\lib\jersey-container-servlet-2.28.jar;%APP_HOME%\lib\jersey-hk2-2.28.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.28.jar;%APP_HOME%\lib\jersey-media-sse-2.28.jar;%APP_HOME%\lib\jersey-server-2.28.jar;%APP_HOME%\lib\jersey-media-jaxb-2.28.jar;%APP_HOME%\lib\jersey-media-json-binding-2.28.jar;%APP_HOME%\lib\jersey-client-2.28.jar;%APP_HOME%\lib\jersey-common-2.28.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.2.jar;%APP_HOME%\lib\jakarta.ws.rs-api-2.1.5.jar;%APP_HOME%\lib\hk2-locator-2.5.0.jar;%APP_HOME%\lib\hk2-api-2.5.0.jar;%APP_HOME%\lib\hk2-utils-2.5.0.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.4.jar;%APP_HOME%\lib\jakarta.inject-2.5.0.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\jakarta.json-1.1.5.jar;%APP_HOME%\lib\yasson-1.0.3.jar;%APP_HOME%\lib\jakarta.json.bind-api-1.0.1.jar;%APP_HOME%\lib\jakarta.json-api-1.1.5.jar;%APP_HOME%\lib\validation-api-2.0.1.Final.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.1.jar;%APP_HOME%\lib\aopalliance-repackaged-2.5.0.jar;%APP_HOME%\lib\javassist-3.22.0-CR2.jar

@rem Execute webshopClient
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %WEBSHOP_CLIENT_OPTS%  -classpath "%CLASSPATH%" Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable WEBSHOP_CLIENT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%WEBSHOP_CLIENT_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
