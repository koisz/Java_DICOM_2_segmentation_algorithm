import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SourceImage;

import javax.swing.*;

/**
 * Klasa Holder jest główną klasą programu, która zarządza wszystkimi działaniami,
 * gromadzi dane i w niej uruchamia się program.
 */
public class Holder {
    /**
     * Pole zawierające informacje dotyczące właściwości obrazu
     */
    private SourceImage DICOM;
    /**
     * Lista TAG DICOM
     */
    private AttributeList DICOMDane = new AttributeList();
    /**
     * Obiekt graficznego interfejsu użytkownika
     */
    private GUI gui;

    /**
     * Konstruktor tworzący obiekt Holder.
     */
    public Holder() {
        try {
            INOUT wejścieWyjście = new INOUT();
            DICOMDane = wejścieWyjście.getDICOMDane();
            DICOM = new SourceImage(DICOMDane);
            gui = new GUI(DICOM,this, wejścieWyjście);
        } catch (Exception e) {
            System.exit(0);
        }
    }

    /**
     * Metoda wczytująca nowy obraz
     */
    public void wczytajNowyObraz() {
        try {
            INOUT wejścieWyjście = new INOUT();
            DICOMDane = null;
            DICOMDane = wejścieWyjście.getDICOMDane();
            SourceImage dicom = new SourceImage(DICOMDane);
            gui.wyswietlNowyObraz(dicom);
        } catch (DicomException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania nowego obrazu.",
                    "Błąd!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }


    /**
     * Metoda wywołującja algorytm progowania
     * @param wartość piksela, dla której zachodzi progowanie
     */
    public void algorytm1(short wartość) {
        Algorytmy a1 = new Algorytmy();
        a1.algorytm1(DICOMDane, wartość, gui);
        a1 = null;
    }


    /**
     * Metoda wywołującja algorytm rozrostu regionu
     * @param numerPiksela , od którego zaczyna się rozrost
     */
    public void algorytm2(int numerPiksela) {
        Algorytmy a2 = new Algorytmy();
        a2.algorytm2(DICOMDane, numerPiksela, gui);
        a2 = null;
    }

    /**
     * Funkcja odpalająca program
     * @param args nie przyjmuje argumentów.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Holder a = new Holder();
            a.gui.setVisible(true);
        });
    }

}
