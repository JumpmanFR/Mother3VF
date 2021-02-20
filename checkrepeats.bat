@echo off
@echo ---------------------------------------------------------
@echo G‚n‚ration du binaire Java...
@echo ---------------------------------------------------------
@javac -encoding utf8 CheckRepeats.java
@echo ---------------------------------------------------------
@echo V‚rification des r‚p‚titions dans le script
@echo ---------------------------------------------------------
@java CheckRepeats script.txt