import com.pixelmed.dicom.AttributeList;
import javax.swing.*;

/**
 * Klasa odczytująca pliki DICOM
 */
public class INOUT extends JFrame {
    /**
     * Ścieżka do pliku DICOM
     */
    private String ścieżkaDoPliku;
    /**
     * Lista TAG DICOM
     */
    private AttributeList DICOMDane;

    /**
     * getter pola DICOMDane
     * @return pole DICOMDane
     */
    public AttributeList getDICOMDane() {
        return DICOMDane;
    }

    /**
     * Konstruktor klasy INOUT
     */
    public INOUT() {
        wczytajObraz();
    }

    /**
     * Metoda umożliwiająca wczytanie pliku DICOM
     */
    public void wczytajObraz() {
        JFileChooser wybórPliku = new JFileChooser();
        int odpowiedź = wybórPliku.showOpenDialog(null);
        if (odpowiedź == JFileChooser.APPROVE_OPTION) {
            ścieżkaDoPliku= wybórPliku.getSelectedFile().getAbsolutePath();
        }
        zmianaŚcieżkiNaDane();
    }

    /**
     * Metoda odczytująca dane DICOM z podanej ścieżki do pliku
     */
    public void zmianaŚcieżkiNaDane() {
        try {
            DICOMDane = new AttributeList();
            DICOMDane.read(ścieżkaDoPliku);
        } catch (Exception e) {
            int zamknięcie = JOptionPane.showConfirmDialog(this, "Błąd wczytywania pliku DICOM. " +
                    "Czy chcesz spróbować wybrać ponownie?", "Błąd", JOptionPane.OK_CANCEL_OPTION);
            if(zamknięcie == JOptionPane.CANCEL_OPTION) {
                DICOMDane = null;
                System.exit(0);
            } else {
                wczytajObraz();
            }
        }

    }

}
