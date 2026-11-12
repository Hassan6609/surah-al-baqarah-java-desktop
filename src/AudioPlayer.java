import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * AudioPlayer plays the free Urdu translation audio.
 *
 * Java Swing cannot play MP3 files without extra libraries, so this class makes a small
 * web page (HTML) and opens it in the normal web browser. The page plays the audio from
 * the internet (everyayah.com) and also shows the Arabic + Urdu text.
 * No download and no installation is needed. Internet is needed for audio.
 */
public class AudioPlayer {

    // Free audio: Urdu translation (Fateh Muhammad Jalandhry) recited by Shamshad Ali Khan.
    // Files are named like 002001.mp3 (surah 002, ayat 001).
    // If the audio does not play, only change this one line.
    private static final String AUDIO_BASE_URL =
            "https://everyayah.com/data/translations/urdu_shamshad_ali_khan_46kbps/";

    /** Plays only one ayat. */
    public void playOne(Ayat ayat) throws IOException {
        openPage(List.of(ayat), false);
    }

    /** Plays all given ayat one after another (full Surah). */
    public void playMany(List<Ayat> ayatList) throws IOException {
        openPage(ayatList, true);
    }

    // Free Arabic recitation: Sheikh Mishary Rashid Alafasy (same file naming: 002001.mp3 ...).
    private static final String ARABIC_AUDIO_BASE_URL =
            "https://everyayah.com/data/Alafasy_128kbps/";

    private String arabicAudioUrl(int ayatNumber) {
        return ARABIC_AUDIO_BASE_URL + String.format("%03d%03d.mp3", AyatManager.SURAH_NUMBER, ayatNumber);
    }

    private String audioUrl(int ayatNumber) {
        return AUDIO_BASE_URL + String.format("%03d%03d.mp3", AyatManager.SURAH_NUMBER, ayatNumber);
    }

    private void openPage(List<Ayat> ayatList, boolean continuous) throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("Cannot open the web browser on this computer.");
        }
        File page = File.createTempFile("surah_baqarah_audio_", ".html");
        page.deleteOnExit();
        Files.write(page.toPath(), buildHtml(ayatList, continuous).getBytes(StandardCharsets.UTF_8));
        Desktop.getDesktop().browse(page.toURI());
    }

    /** Builds the HTML text of the player page. */
    String buildHtml(List<Ayat> ayatList, boolean continuous) {
        StringBuilder data = new StringBuilder("[");
        for (int i = 0; i < ayatList.size(); i++) {
            Ayat a = ayatList.get(i);
            if (i > 0) data.append(",");
            data.append("{n:").append(a.getAyatNumber())
                .append(",a:").append(jsString(a.getArabicText()))
                .append(",u:").append(jsString(a.getUrduTranslation()))
                .append(",ar:").append(jsString(arabicAudioUrl(a.getAyatNumber())))
                .append(",ur:").append(jsString(audioUrl(a.getAyatNumber())))
                .append("}");
        }
        data.append("]");

        String navDisplay = continuous ? "inline-block" : "none";
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">"
            + "<title>Surah Al-Baqarah - Urdu Audio</title>"
            + "<style>"
            + "body{font-family:Arial,sans-serif;background:#f4f1ea;margin:0;padding:20px;text-align:center}"
            + ".box{max-width:850px;margin:auto;background:#fff;border-radius:12px;padding:20px;box-shadow:0 2px 8px rgba(0,0,0,.25)}"
            + "#arabic{font-size:2.2em;direction:rtl;line-height:2.1;font-family:'Scheherazade New','Traditional Arabic','Amiri',serif}"
            + "#urdu{font-size:1.6em;direction:rtl;line-height:2.2;font-family:'Jameel Noori Nastaleeq','Noto Nastaliq Urdu',serif}"
            + "button{font-size:1em;padding:8px 18px;margin:6px;cursor:pointer}"
            + "#nav{display:" + navDisplay + "}"
            + "</style></head><body><div class=\"box\">"
            + "<h2>Surah Al-Baqarah &ndash; Ayat <span id=\"num\"></span></h2>"
            + "<div id=\"arabic\"></div><hr><div id=\"urdu\"></div>"
            + "<div style=\"margin-top:14px\">Audio: <select id=\"mode\" onchange=\"changeMode()\" style=\"font-size:1em;padding:4px\">"
            + "<option value=\"both\">Arabic recitation + Urdu translation</option>"
            + "<option value=\"ar\">Arabic recitation only</option>"
            + "<option value=\"ur\">Urdu translation only</option></select></div>"
            + "<p id=\"what\" style=\"color:#0b5d4b;font-weight:bold;margin:8px\"></p>"
            + "<audio id=\"player\" controls style=\"width:100%\"></audio>"
            + "<div id=\"nav\"><button onclick=\"prev()\">Previous</button>"
            + "<button onclick=\"startAudio()\">Play</button>"
            + "<button onclick=\"next()\">Next</button></div>"
            + "<p id=\"msg\" style=\"color:#555\"></p></div>"
            + "<script>"
            + "var ayat=" + data + ";var continuous=" + continuous + ";var i=0;var part=0;var mode='both';"
            + "var player=document.getElementById('player');"
            + "function msg(t){document.getElementById('msg').textContent=t;}"
            + "function parts(){return mode==='ar'?['ar']:(mode==='ur'?['ur']:['ar','ur']);}"
            + "function setSource(){var a=ayat[i];var p=parts()[part];"
            + "player.src=(p==='ar')?a.ar:a.ur;"
            + "document.getElementById('what').textContent=(p==='ar')?'Arabic recitation':'Urdu translation';}"
            + "function show(){var a=ayat[i];part=0;"
            + "document.getElementById('num').textContent=a.n;"
            + "document.getElementById('arabic').textContent=a.a;"
            + "document.getElementById('urdu').textContent=a.u;"
            + "setSource();}"
            + "function startAudio(){var p=player.play();"
            + "if(p){p.catch(function(){msg('Press the Play button of the audio bar to start.');});}}"
            + "function changeMode(){mode=document.getElementById('mode').value;part=0;setSource();startAudio();}"
            + "function next(){if(i<ayat.length-1){i++;show();startAudio();}}"
            + "function prev(){if(i>0){i--;show();startAudio();}}"
            + "player.onended=function(){var ps=parts();"
            + "if(part<ps.length-1){part++;setSource();startAudio();}"
            + "else if(continuous&&i<ayat.length-1){next();}"
            + "else{msg('Finished.');}};"
            + "player.onerror=function(){msg('Audio could not be loaded. Please check your internet connection.');};"
            + "show();startAudio();"
            + "</script></body></html>";
    }

    /** Makes a safe JavaScript string (with quotes) from normal text. */
    private String jsString(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': break;
                case '<':  sb.append("\\u003c"); break;   // so that </script> can never appear
                case '\u2028': sb.append("\\u2028"); break;
                case '\u2029': sb.append("\\u2029"); break;
                default:   sb.append(c);
            }
        }
        return sb.append("\"").toString();
    }
}
