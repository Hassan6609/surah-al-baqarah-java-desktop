import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MainFrame = the window (GUI) made with Java Swing.
 * It shows the Arabic text and the Urdu translation of the selected ayat,
 * and has the buttons to move between ayat and to play the audio.
 */
public class MainFrame extends JFrame {

    private static final Color PAGE_COLOR = new Color(0xF4F1EA);
    private static final Color GREEN = new Color(0x0B5D4B);

    private final AyatManager manager;
    private final AudioPlayer audioPlayer = new AudioPlayer();

    private final JComboBox<Integer> ayatSelector = new JComboBox<>();
    private final JTextArea arabicArea = new JTextArea(4, 40);
    private final JTextArea urduArea = new JTextArea(5, 40);
    private final JLabel statusLabel = new JLabel(" ");

    private final JButton previousButton = new JButton("Previous");
    private final JButton nextButton = new JButton("Next");
    private final JButton playAyatButton = new JButton("Play This Ayat");
    private final JButton playSurahButton = new JButton("Play Full Surah");

    private boolean updatingSelector = false;   // stops the combo box from firing twice

    public MainFrame(AyatManager manager) {
        super("Surah Al-Baqarah \u2013 Urdu Translation");
        this.manager = manager;

        buildScreen();
        connectButtons();
        fillSelector();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);        // open in the center of the screen
    }

    // ---------- Build the screen ----------
    private void buildScreen() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(PAGE_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        setContentPane(root);

        // Top: title bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        top.setBackground(GREEN);
        JLabel title = new JLabel("Surah Al-Baqarah");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        JLabel numberLabel = new JLabel("Ayat No:");
        numberLabel.setForeground(Color.WHITE);
        top.add(title);
        top.add(numberLabel);
        top.add(ayatSelector);
        root.add(top, BorderLayout.NORTH);

        // Middle: Arabic and Urdu text areas
        setupTextArea(arabicArea, pickFont(34, "Scheherazade New", "Traditional Arabic",
                "Amiri", "Noto Naskh Arabic", "Arial"));
        setupTextArea(urduArea, pickFont(24, "Jameel Noori Nastaleeq", "Noto Nastaliq Urdu",
                "Urdu Typesetting", "Noto Naskh Arabic", "Arial"));

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setOpaque(false);
        center.add(makeScroll(arabicArea, "Arabic"));
        center.add(makeScroll(urduArea, "Urdu Translation"));
        root.add(center, BorderLayout.CENTER);

        // Bottom: buttons + status
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        buttons.setOpaque(false);
        styleButton(previousButton, false);
        styleButton(nextButton, false);
        styleButton(playAyatButton, true);
        styleButton(playSurahButton, true);
        buttons.add(previousButton);
        buttons.add(nextButton);
        buttons.add(playAyatButton);
        buttons.add(playSurahButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(new Color(0x555555));
        bottom.add(statusLabel, BorderLayout.SOUTH);
        root.add(bottom, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, boolean main) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setMargin(new Insets(8, 16, 8, 16));
        if (main) {
            button.setBackground(GREEN);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
        }
    }

    private void setupTextArea(JTextArea area, Font font) {
        area.setFont(font);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(8, 10, 8, 10));
        area.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);  // Arabic/Urdu go right to left
    }

    private JScrollPane makeScroll(JTextArea area, String title) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        return scroll;
    }

    /** Uses the first font that is installed on the computer. */
    private Font pickFont(int size, String... names) {
        String[] installed = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        for (String wanted : names) {
            for (String name : installed) {
                if (name.equalsIgnoreCase(wanted)) {
                    return new Font(name, Font.PLAIN, size);
                }
            }
        }
        return new Font("SansSerif", Font.PLAIN, size);
    }

    // ---------- Buttons ----------
    private void connectButtons() {
        ayatSelector.addActionListener(e -> {
            if (!updatingSelector && ayatSelector.getSelectedItem() != null) {
                showAyat((Integer) ayatSelector.getSelectedItem());
            }
        });
        previousButton.addActionListener(this::onPrevious);
        nextButton.addActionListener(this::onNext);
        playAyatButton.addActionListener(this::onPlayAyat);
        playSurahButton.addActionListener(this::onPlaySurah);
    }

    private void onPrevious(ActionEvent e) {
        int previous = manager.getPreviousNumber(currentNumber());
        if (previous == -1) {
            setStatus("This is the first ayat");
        } else {
            showAyat(previous);
        }
    }

    private void onNext(ActionEvent e) {
        int next = manager.getNextNumber(currentNumber());
        if (next == -1) {
            setStatus("This is the last ayat");
        } else {
            showAyat(next);
        }
    }

    private void onPlayAyat(ActionEvent e) {
        Ayat ayat = manager.getAyat(currentNumber());
        if (ayat == null) {
            setStatus("Please select a valid ayat");
            return;
        }
        try {
            audioPlayer.playOne(ayat);
            setStatus("Audio page opened in your web browser. Internet is needed. "
                    + "If it does not start, press Play there.");
        } catch (IOException ex) {
            setStatus("Could not open the audio: " + ex.getMessage());
        }
    }

    private void onPlaySurah(ActionEvent e) {
        List<Ayat> all = new ArrayList<>();
        for (int number : manager.getAllNumbers()) {
            all.add(manager.getAyat(number));
        }
        if (all.isEmpty()) {
            setStatus("No ayat loaded");
            return;
        }
        try {
            audioPlayer.playMany(all);
            setStatus("Full Surah opened in your web browser. It plays ayat after ayat. "
                    + "If it does not start, press Play there.");
        } catch (IOException ex) {
            setStatus("Could not open the audio: " + ex.getMessage());
        }
    }

    // ---------- Show data ----------
    private void fillSelector() {
        updatingSelector = true;
        ayatSelector.removeAllItems();
        for (int number : manager.getAllNumbers()) {
            ayatSelector.addItem(number);
        }
        updatingSelector = false;
        if (ayatSelector.getItemCount() > 0) {
            showAyat(ayatSelector.getItemAt(0));
        } else {
            setStatus("No ayat found. Please check the data folder (see README).");
        }
    }

    private int currentNumber() {
        Object selected = ayatSelector.getSelectedItem();
        return selected == null ? -1 : (Integer) selected;
    }

    private void showAyat(int number) {
        Ayat ayat = manager.getAyat(number);
        if (ayat == null) {
            setStatus("Ayat " + number + " is not available");
            return;
        }
        updatingSelector = true;
        ayatSelector.setSelectedItem(number);
        updatingSelector = false;

        arabicArea.setText(ayat.getArabicText());
        urduArea.setText(ayat.getUrduTranslation());
        arabicArea.setCaretPosition(0);
        urduArea.setCaretPosition(0);
        setStatus("Showing ayat " + number + " of " + manager.getCount());
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }
}
