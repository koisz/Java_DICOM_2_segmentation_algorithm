import javax.swing.*;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SourceImage;
import com.pixelmed.display.SingleImagePanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;


/**
 * Klasa graficznego interfejsu użytkownika
 */
public class GUI extends JFrame implements ActionListener, MouseListener {
    /**
     * Pole zawierające informacje dotyczące właściwości obrazu
     */
    private SourceImage DICOM;
    /**
     * Panel na obraz umożliwiający jego wyświetlenie w GUI
     */
    private SingleImagePanel DICOMObraz;
    /**
     * Obiekt klasy Holder
     */
    private Holder holder;
    /**
     * Obiekt klasy INOUT
     */
    private INOUT wejścieWyjście;
    /**
     * Panel pomagający rozmieścić przycisk 1 w GUI
     */
    private JPanel panelNaPrzycisk1 = new JPanel();
    /**
     * Panel pomagający rozmieścić przycisk 2 w GUI
     */
    private JPanel panelNaPrzycisk2 = new JPanel();
    /**
     * Panel umieszczający dwa przyciski w GUI
     */
    private JPanel panelNaDwaPrzyciski = new JPanel();
    /**
     * Panel umieszczający wiadomość w GUI
     */
    private JPanel panelNaWiadomość = new JPanel();
    /**
     * Wyświetlana wiadomość po kliknięciu przycisku 1
     */
    private JLabel wiadomość = new JLabel("Wybierz za pomocą przycisków 0 lub 1 jakie wartości będą poniżej progu.");
    /**
     * Panel na przycisku wybierające rodzaj progowania
     */
    private JPanel panelNa0i1 = new JPanel();
    /**
     * Przycisk do algorytmu 1
     */
    private JButton przycisk1 = new JButton("Algorytm 1");
    /**
     * Przycisk do algorytmu 2
     */
    private JButton przycisk2 = new JButton("Algorytm 2");
    /**
     * Przycisk, który powoduje, że wszystkie piksele poniżej progu są zerami
     */
    private JButton przyciskNaZero = new JButton("0");
    /**
     * Przycisk, który powoduje, że wszystkie piksele poniżej progu są max wartością
     */
    private JButton przyciskNaJeden = new JButton("1");
    /**
     * Flaga czekająca na kliknięcie myszą
     */
    private boolean czekamNaKlik = false;
    /**
     *  Flaga czekająca na kliknięcie przycisku algorytm 1
     */
    private boolean czekamNaProgi = false;
    /**
     * Menu
     */
    private JMenu fileMenu = new JMenu("Plik");
    /**
     * Item umożliwiający zmianę obrazu
     */
    private JMenuItem wczytajPlik = new JMenuItem("Wczytaj nowy obraz.");
    /**
     *  Pasek menu
     */
    private JMenuBar menuBar = new JMenuBar();

    /**
     * Konstruktor klasy GUI
     * @param dicom obiekt zawierający informacje o obrazie
     * @param w obiekt zawierający referencje na klasę Holder
     * @param ww obiekt zawierający referencje na klasę INOUT
     */
    public GUI(SourceImage dicom, Holder w, INOUT ww) {
        holder = w;
        wejścieWyjście = ww;
        DICOM = dicom;
        this.setTitle("GUI");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(dicom.getWidth() + 130,dicom.getHeight() + 25);
        utwórzObraz(DICOM);
        przyciskiDoAlgorytmów();
        wczytajPlik.addActionListener(this);
        fileMenu.add(wczytajPlik);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Metoda umożliwiająca wyświetlenie obrazu po wczytaniu nowego obrazu
     * @param dicom obiekt zawierający informacje o obrazie
     */
    public void wyswietlNowyObraz(SourceImage dicom) {
        this.remove(DICOMObraz);
        utwórzObraz(dicom);
        this.revalidate();
        this.repaint();
    }

    /**
     * Metoda generująca obraz po użyciu algorytmu
     * @param dicom obiekt zawierający informacje o obrazie
     */
    public void wyświetlPoZmianie(SourceImage dicom) {
        JFrame oknoNaKopię = new JFrame("Obraz DICOM.");
        SingleImagePanel dicomKopiaObraz = new SingleImagePanel(dicom);
        oknoNaKopię.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        oknoNaKopię.setSize(dicom.getWidth(),dicom.getHeight());
        oknoNaKopię.add(dicomKopiaObraz);
        oknoNaKopię.setVisible(true);
    }

    /**
     * Metoda wyświetlająca obraz.
     */
    public void utwórzObraz(SourceImage dicom) {
        try {
            DICOMObraz = new SingleImagePanel(dicom);
            DICOMObraz.setPreferredSize(new Dimension(DICOMObraz.getWidth(), DICOMObraz.getHeight()));
            this.add(DICOMObraz, BorderLayout.CENTER);
            DICOMObraz.addMouseListener(this);
        } catch (Exception e) {

        }
    }

    /**
     * Metoda dodająca przyciski do GUI.
     */
    private void przyciskiDoAlgorytmów() {
        przycisk1.setFont(new Font("Comic Sans MS",Font.BOLD,12));
        przycisk2.setFont(new Font("Comic Sans MS",Font.BOLD,12));
        przyciskNaZero.setFont(new Font("Comic Sans MS",Font.BOLD,12));
        przyciskNaJeden.setFont(new Font("Comic Sans MS",Font.BOLD,12));
        wiadomość.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
        przycisk1.addActionListener(this);
        przycisk2.addActionListener(this);
        przyciskNaZero.addActionListener(this);
        przyciskNaJeden.addActionListener(this);
        panelNaPrzycisk1.add(przycisk1);
        panelNaPrzycisk1.setPreferredSize(new Dimension(100,125));
        panelNaDwaPrzyciski.add(panelNaPrzycisk1, BorderLayout.NORTH);
        panelNaWiadomość.add(wiadomość);
        panelNaWiadomość.setVisible(false);
        this.add(panelNaWiadomość, BorderLayout.NORTH);
        panelNa0i1.add(przyciskNaZero);
        panelNa0i1.add(przyciskNaJeden);
        panelNa0i1.setVisible(false);
        panelNaDwaPrzyciski.add(panelNa0i1, BorderLayout.NORTH);
        panelNaPrzycisk2.add(przycisk2);
        panelNaDwaPrzyciski.add(panelNaPrzycisk2, BorderLayout.SOUTH);
        panelNaDwaPrzyciski.setPreferredSize(new Dimension(125,125));
        this.add(panelNaDwaPrzyciski,BorderLayout.EAST);
        this.setVisible(true);
    }

    /**
     * Metoda wywołująca algorytm zależny od przycisku jaki zostanie wciśnięty.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == przycisk1) {
            czekamNaProgi = true;
            panelNaWiadomość.setVisible(true);
            panelNa0i1.setVisible(true);
        } else if (e.getSource() == przyciskNaZero || e.getSource() == przyciskNaJeden) {

            if (czekamNaProgi) {
                short wartość = 0;
                if (e.getSource() == przyciskNaZero) {
                    wartość = 0;
                } else if (e.getSource() == przyciskNaJeden) {
                    wartość = 1;
                }
                    holder.algorytm1(wartość);
                    panelNaWiadomość.setVisible(false);
                    panelNa0i1.setVisible(false);
                    czekamNaProgi = false;
            }

        } else if(e.getSource() == przycisk2) {
            int odpowiedź = JOptionPane.showConfirmDialog(this,
                    "Kliknij myszą na wybrany obszar!", "Działanie", JOptionPane.OK_CANCEL_OPTION);

            if (odpowiedź != JOptionPane.OK_OPTION) {
                czekamNaKlik = false;
            } else {
                czekamNaKlik = true;
            }

        } else if(e.getSource() == wczytajPlik) {
            holder.wczytajNowyObraz();
            czekamNaKlik = false;
        }

    }

    /**
     * Metoda reagująca na kliknięcie myszką
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            if (czekamNaKlik) {
                int x = e.getX();
                int y = e.getY();
                if(x >= 0 && x < DICOM.getWidth() && y >= 0 && y < DICOM.getHeight()) {
                    int numerPiksela = y * DICOM.getWidth() + x;
                    System.out.printf("Pobrano piksel o współrzędnych x = %d y= %d%n", x, y);
                    holder.algorytm2(numerPiksela);
                    czekamNaKlik = false;

                    if(Arrays.asList(getMouseListeners()).contains(this)) {
                        removeMouseListener(this);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Kliknięto na obraz poza obszarem! " +
                            "Wybierz ponownie.", "Błąd!", JOptionPane.ERROR_MESSAGE);
                    czekamNaKlik = true;
                }
            }

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(null, "Błąd podczas wykonania algorytmu 2.",
                    "Błąd!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param e the event to be processed
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {}

    /**
     * @param e the event to be processed
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e)  {}

}
