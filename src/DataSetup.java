import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * DataSetup helps the user the FIRST time only.
 * If data/arabic.txt or data/urdu.txt is missing, it asks the user to choose the
 * downloaded file and copies it into the data folder. After that it never asks again.
 */
public class DataSetup {

    /** Returns true when both data files are ready. */
    public static boolean prepare(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            // if this fails, the file copy below will show an error
        }
        boolean arabicOk = ensureFile(dataDir.resolve("arabic.txt"),
                "ARABIC text of the Quran",
                "https://tanzil.net/download",
                "On the website: choose any Quran text type, set Output format to\n"
                + "\"Text (with aya numbers)\", then press Download.");
        if (!arabicOk) return false;

        return ensureFile(dataDir.resolve("urdu.txt"),
                "URDU translation of the Quran",
                "https://tanzil.net/trans",
                "On the website: choose Urdu (for example Fateh Muhammad Jalandhry),\n"
                + "download it as a Text file (format: surah|ayat|text).");
    }

    private static boolean ensureFile(Path target, String what, String url, String help) {
        if (isValid(target)) return true;

        while (true) {
            String[] options = {"Open website", "I have the file - choose it", "Cancel"};
            int choice = JOptionPane.showOptionDialog(null,
                    "First time setup (only once).\n\n"
                    + "The program needs the " + what + ".\n"
                    + "It is free from Tanzil.net.\n\n" + help + "\n\n"
                    + "Then press \"I have the file\" and select the downloaded file.",
                    "Setup - " + what, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (choice == 0) {
                openWebsite(url);
            } else if (choice == 1) {
                if (chooseAndCopy(target)) return true;
            } else {
                return false;       // Cancel or window closed
            }
        }
    }

    private static boolean chooseAndCopy(Path target) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select the downloaded text file");
        chooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return false;

        File chosen = chooser.getSelectedFile();
        try {
            Files.copy(chosen.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not copy the file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!isValid(target)) {
            try { Files.deleteIfExists(target); } catch (Exception ignored) { }
            JOptionPane.showMessageDialog(null,
                    "This file has no Surah Al-Baqarah lines in the format  2|1|text .\n"
                    + "Please download the file again using the format\n"
                    + "\"Text (with aya numbers)\" and try again.",
                    "Wrong file format", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /** A file is valid if it exists and contains at least one ayat of Surah 2. */
    private static boolean isValid(Path file) {
        try {
            return Files.exists(file) && !AyatManager.readFile(file).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static void openWebsite(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please open this website yourself:\n" + url);
        }
    }
}
