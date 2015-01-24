@echo off

set CLASS=org.jzuul.gdk.Main
set LIBRARY_PATH=lib\swt_win32

set CLASSPATH=lib\xerces.jar;lib\jdom.jar;lib\swt_win32\swt.jar;lib\swt_win32\swt-pi.jar;.\

java -cp %CLASSPATH% -Djava.library.path=%LIBRARY_PATH% %CLASS% %*