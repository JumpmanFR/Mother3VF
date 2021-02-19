@echo off
@echo ---------------------------------------------------------
@echo Building java file...
@echo ---------------------------------------------------------
@javac -encoding utf8 CheckRepeats.java
@echo ---------------------------------------------------------
@echo Checking repeats in script
@echo ---------------------------------------------------------
@java CheckRepeats script.txt