# Mother3VF

Ce dépôt contient l’ensemble des sources du projet de traduction de MOTHER 3 en français, et des outils ayant servi à la réalisation de ce projet. Certains fichiers sont des éléments, modifiés ou non pour nos propres besoins, de la traduction anglaise (http://mother3.fobby.net/tools/).

Vous trouverez notamment :
– l’intégralité des textes français
– des éléments graphiques traduits
– des polices bitmaps
– des données d’agencement graphique (tilemaps, arrangements)
– des éléments audio traduits
– des routines de hack en langage assembleur ASM THUMB pour GBA
– des tables de données et des routines dans le langage interne game logic
– des outils de prévisualisation, édition de polices, conversion de textes et de sons, assemblage ASM et génération de ROM

Vous pouvez modifier ces fichiers à votre guise. Pour modifiez les fichiers textes, utilisez un éditeur de texte comme Notepad++. Le script.txt et les fichiers 0-8E.txt à 0-42.txt doivent être enregistrés au format UTF-8, les autres fichiers textes au format Windows Latin 1 (référez-vous aux réglages de votre éditeur de texte).
Vos modifications vous permettront ensuite de générer une ROM adaptée à vos besoins.

Pour cela, il vous faut :
– une ROM japonaise originale de MOTHER 3
– un PC équipé de Windows et de .NET Framework

Pour générer la ROM :
– clonez le dépôt (ou téléchargez l’archive) dans un dossier de votre choix
– placez la ROM japonaise dans ce même dossier et renommez-la « mother3_jp.gba »
– ouvrez TextInjector.exe, décochez « Encode the script » dans le menu « Encoding », cliquez sur « Load script… » et ouvrez « script.txt », puis cliquez sur « Dump to binfile » et enregistrez avec le nom par défaut
– lancez j.bat, de préférence depuis l’invite de commandes Windows (si vous avez les connaissances requises), ou bien depuis l’Explorateur Windows
– attendez que le script se termine (environ 30 secondes), et votre ROM traduite sera alors générée sous le nom « mother3_fr.gba »
