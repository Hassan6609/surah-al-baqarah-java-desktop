import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/** Main = the starting point of the program. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  // looks like a normal Windows program
        } catch (Exception e) {
            // keep the default look if this fails
        }
        Path dataDir = findAppFolder().toPath().resolve("data");

        // First time only: ask for the data files (no problem if they already exist).
        if (!DataSetup.prepare(dataDir)) {
            return;
        }

        AyatManager manager = new AyatManager();
        try {
            manager.load(dataDir.resolve("arabic.txt").toString(),
                         dataDir.resolve("urdu.txt").toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not read the data files.\n" + e.getMessage(),
                    "Data file error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new MainFrame(manager).setVisible(true));
    }

    /** The folder of the .jar file (double click), or the project folder (when run from an IDE). */
    private static File findAppFolder() {
        try {
            File location = new File(Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (location.isFile() && location.getName().toLowerCase().endsWith(".jar")) {
                return location.getParentFile();
            }
        } catch (Exception e) {
            // use the fallback below
        }
        return new File(System.getProperty("user.dir"));
    }
}
