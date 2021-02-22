certutil -f -encodehex text_custom_text.bin text_custom_text.bin.txt
fart text_custom_text.bin.txt "00 00" "FF FF"
certutil -f -decodehex text_custom_text.bin.txt text_custom_text.bin
del text_custom_text.bin.txt