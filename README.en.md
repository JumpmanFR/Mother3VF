# Mother3VF

[🇫🇷 En français](README.md)

This repository contains all the sources for the Mother 3 French fan translation project, along with tools we used during this project. Some files come from [the tools that were provided by the English fan translation 1.2](http://mother3.fobby.net/tools), and we have edited them for our own needs in a few instances.

Here you’ll find:
* the French text in its entirety
* our translated graphics
* our bitmap fonts
* our tilemaps and arrangements for graphics
* the translated audio clips
* our GBA ASM THUMB assembly routines, and those provided by the English team and contributors
* our data tables, and a few routines in the internal Game Logic language
* preview and editing tools, text/audio conversion tools, assembly and ROM building tools

Compared to the tools provided by Tomato’s team, here you may find more graphical elements (especially those which were already in English in the Japanese version and that the English team didn’t need to translate), newer hacking routines (at least compared to the English 1.2, things may be subject to change), and, obviously, our French text.

We’ve been working on these files since 2015, but they’ve only been deployed to GitHub in 2021. Which means you’ll only have access of a small part of the entire history of the project.

Feel free to clone the project and edit anything you want. To edit the text files, use a text editor such as Notepad++. *script.txt* as well as files from *0-8E.txt* to *0-42.txt* must be saved using UTF-8 encoding, whereas all other text files must be saved using Windows-1252 encoding (please refer to the parameters in your text editor).
Your changes will then allow you to build a ROM adapted to your needs.

To achieve this, you’ll need:
* an unmodified Japanese ROM of MOTHER 3
* a PC with Windows and the [.NET Framework](https://docs.microsoft.com/fr-fr/dotnet/framework/install/on-windows-10) (you’ll also need Python and Java to run some of the additional tools)

To generate the ROM:
* clone the repository (download the zip archive) to a local folder
* put the Japanese ROM in the same folder and rename it to *mother3_jp.gba*
* open TextInjector.exe, uncheck *Encode the script* in the *Encoding* menu, click *Load script…* and select *script.txt*. Then click *Dump to binfile* and keep the save file name as default
* run j.bat, ideally from the Windows command prompt (if you’re familiar with it), or from Windows Explorer otherwise
* wait until the entire process is complete (approximately 30 seconds), and your translated ROM will then be generated as *mother3_fr.gba*

If you want more details, feel free to reach out [on Twitter](https://twitter.com/jumpmanfr) ou join [our Discord server](http://mother3vf.free.fr/discord).
