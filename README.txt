SURAH AL-BAQARAH - ARABIC + URDU TRANSLATION + AUDIO   (Java Swing, OOP project)
================================================================================

WHAT IT DOES
- Shows any ayat of Surah Al-Baqarah: Arabic text + Urdu translation
- Previous / Next buttons and an ayat drop-down
- "Play This Ayat"  : plays the Arabic recitation, then the Urdu translation of that ayat
- "Play Full Surah" : plays ayat 1 to the last ayat, one after another
  (audio opens in your web browser from the internet, so internet is needed)
- On the audio page a menu lets you choose: Arabic + Urdu / Arabic only / Urdu only

HOW TO RUN  (you need Java 11 or newer, nothing else)
1. Unzip the folder. Keep all files together.
2. Double-click  SurahAlBaqarah.jar
   (if nothing happens, double-click START.bat instead)
3. FIRST TIME ONLY: the program asks for the Arabic and the Urdu text files.
   - Press "Open website" and download the file from Tanzil.net
       Arabic: choose a Quran text type, Output format "Text (with aya numbers)", Download
       Urdu  : choose the Urdu translation (e.g. Jalandhry) as a Text file
   - Press "I have the file - choose it" and select the downloaded .txt file.
   The program copies it into the data folder. It never asks again, and the
   folder can be given to friends as it is - it just works.

Do NOT edit the Quran text. Tanzil allows copying it only without changes, and the
copyright lines inside the downloaded files must stay there.

HOW THE AUDIO WORKS
Java Swing cannot play MP3 files without extra libraries, so the program creates a small
web page and opens it in your browser. The page plays the Arabic recitation (Sheikh Mishary Alafasy) and the Urdu translation audio from everyayah.com and also
shows the Arabic and Urdu text. If the browser blocks auto-play, press Play on that page.
If the audio ever stops working, only two lines must be changed: AUDIO_BASE_URL (Urdu) and
ARABIC_AUDIO_BASE_URL (Arabic) in src/AudioPlayer.java (then compile again).

OOP CLASSES (src/)
- Ayat         : one ayat (number, Arabic, Urdu)        -> encapsulation
- AyatManager  : loads the data files, gives ayat by number, next / previous
- AudioPlayer  : builds the audio page and opens the browser
- DataSetup    : first-time help to copy the data files
- MainFrame    : the Swing window
- Main         : starts the program

TO COMPILE YOURSELF (JDK 11+):
    javac -encoding UTF-8 -d out src/*.java
    java -cp out Main            (run from the project folder)

CREDITS (keep when you publish)
- Arabic text and Urdu translation: Tanzil.net (Creative Commons Attribution 3.0) - http://tanzil.net
- Audio: everyayah.com - Arabic recitation by Mishary Rashid Alafasy, and Urdu translation recitation. Please check their terms before re-sharing audio.
