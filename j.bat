@echo off
@echo ---------------------------------------------------------
@echo Converting non-script text files to data files
@echo ---------------------------------------------------------
@echo.
@textconv
@fix_custom_text text_custom_text.bin
@echo.
@echo.
@echo ---------------------------------------------------------
@echo Copying base ROM to the new translated ROM
@echo ---------------------------------------------------------
@echo.
@copy mother3_jp.gba mother3_fr.gba
@echo.
@echo.
@echo ---------------------------------------------------------
@echo Converting audio .snd files to data files
@echo ---------------------------------------------------------
@echo.
@soundconv readysetgo.snd lookoverthere_eng.snd
@echo.
@echo.
@echo ---------------------------------------------------------
@echo Creating pre-welded cast of characters + sleep mode text
@echo ---------------------------------------------------------
@echo.
@m3preweld
@echo.
@echo.
@echo ---------------------------------------------------------
@echo Checking overlap of the files that will be used
@echo ---------------------------------------------------------
@echo.
@python check_overlap.py m3hack.asm
@echo.
@echo.
@echo ---------------------------------------------------------
@echo Compiling .asm files and inserting all new data files
@echo ---------------------------------------------------------
@echo.
@xkas mother3_fr.gba m3hack.asm
@echo.
@echo.
@echo COMPLETE!
@echo.