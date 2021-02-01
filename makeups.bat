@echo off
@echo ---------------------------------------------------------
@echo Creating UPS file (mother3vf.ups)
@echo ---------------------------------------------------------
@echo.
@ups diff -b mother3_jp.gba -m mother3_fr.gba -o mother3vf.ups
@pause
@echo.