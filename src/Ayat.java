/**
 * Ayat = ek ayat (verse).
 * Is class mein sirf data hai: number, Arabic text, Urdu translation.
 * Fields private hain aur getter methods se parhi jati hain (ENCAPSULATION).
 */
public class Ayat {
    private final int ayatNumber;
    private final String arabicText;
    private final String urduTranslation;

    public Ayat(int ayatNumber, String arabicText, String urduTranslation) {
        this.ayatNumber = ayatNumber;
        this.arabicText = arabicText;
        this.urduTranslation = urduTranslation;
    }

    public int getAyatNumber() {
        return ayatNumber;
    }

    public String getArabicText() {
        return arabicText;
    }

    public String getUrduTranslation() {
        return urduTranslation;
    }
}
