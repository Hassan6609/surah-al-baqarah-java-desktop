import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * AyatManager keeps all the Ayat of the Surah and gives them to the GUI.
 *
 * It reads two simple text files (UTF-8) in the Tanzil.net format:
 *      surah|ayat|text
 * Example line:   2|255|.....
 * Only lines of Surah number 2 (Al-Baqarah) are used, so you can even put
 * the whole Quran file in data/ and it will still work.
 */
public class AyatManager {
    public static final int SURAH_NUMBER = 2;   // Al-Baqarah

    // TreeMap keeps the ayat sorted by number (1, 2, 3 ...)
    private final Map<Integer, Ayat> ayatMap = new TreeMap<>();

    /** Loads Arabic + Urdu files. Throws IOException if a file is missing. */
    public void load(String arabicFile, String urduFile) throws IOException {
        Map<Integer, String> arabic = readFile(Paths.get(arabicFile));
        Map<Integer, String> urdu = readFile(Paths.get(urduFile));

        ayatMap.clear();
        for (Map.Entry<Integer, String> entry : arabic.entrySet()) {
            int number = entry.getKey();
            String urduText = urdu.getOrDefault(number, "(Urdu translation not found for this ayat)");
            ayatMap.put(number, new Ayat(number, entry.getValue(), urduText));
        }
    }

    /** Reads one Tanzil style file and returns ayatNumber -> text (only Surah 2). */
    public static Map<Integer, String> readFile(Path path) throws IOException {
        Map<Integer, String> result = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();      // remove BOM and spaces
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;                                   // skip comments
                }
                String[] parts = line.split("\\|", 3);
                if (parts.length < 3) {
                    continue;                                   // not a valid line
                }
                try {
                    int surah = Integer.parseInt(parts[0].trim());
                    int ayat = Integer.parseInt(parts[1].trim());
                    if (surah == SURAH_NUMBER) {
                        result.put(ayat, parts[2].trim());
                    }
                } catch (NumberFormatException e) {
                    // ignore bad lines, do not crash
                }
            }
        }
        return result;
    }

    /** Returns the Ayat, or null if it does not exist. */
    public Ayat getAyat(int number) {
        return ayatMap.get(number);
    }

    public boolean hasAyat(int number) {
        return ayatMap.containsKey(number);
    }

    public List<Integer> getAllNumbers() {
        return new ArrayList<>(ayatMap.keySet());
    }

    public int getCount() {
        return ayatMap.size();
    }

    /** Next available ayat number after 'current', or -1 if there is none. */
    public int getNextNumber(int current) {
        for (int n : ayatMap.keySet()) {
            if (n > current) return n;
        }
        return -1;
    }

    /** Previous available ayat number before 'current', or -1 if there is none. */
    public int getPreviousNumber(int current) {
        int previous = -1;
        for (int n : ayatMap.keySet()) {
            if (n >= current) break;
            previous = n;
        }
        return previous;
    }
}
