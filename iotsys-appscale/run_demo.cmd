@ECHO OFF

SET DEMO=%1
SET PORT=%2

SET DIRECTORY=%~dp0

SET SERVER_LOCATION="test\appengine-java-sdk-repacked\bin\dev_appserver.cmd"
SET DEMO_DIRECTORY="demos"

IF [%DEMO%] == [] (
	ECHO "usage: <file> <demo-name> [port]" 
	EXIT /B
)

IF [%PORT%] == [] (
	CALL %SERVER_LOCATION% "%DIRECTORY%\%DEMO_DIRECTORY%\%DEMO%\war"
) ELSE (
	CALL %SERVER_LOCATION% --port=%PORT% "%DIRECTORY%\%DEMO_DIRECTORY%\%DEMO%\war"
)
