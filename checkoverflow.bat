@echo off
@echo ---------------------------------------------------------
@echo Building java file...
@echo ---------------------------------------------------------
@javac -encoding utf8 CheckOverflow.java
@echo ---------------------------------------------------------
@echo Checking overflows in script
@echo ---------------------------------------------------------
@java CheckOverflow script.txt %1