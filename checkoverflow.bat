@echo off
@echo ---------------------------------------------------------
@echo G‚n‚ration du binaire Java...
@echo ---------------------------------------------------------
@javac -encoding utf8 CheckOverflow.java
@echo ---------------------------------------------------------
@echo V‚rification des d‚passements dans le script
@echo ---------------------------------------------------------
@java CheckOverflow script.txt %1