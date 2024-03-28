import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.OtherByteAttribute;
import com.pixelmed.dicom.OtherWordAttribute;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;

import javax.swing.*;
import java.util.*;

/**
 * Klasa Algorytmy zawiera implementacje dwóch algorytmów stosowanych do przetwarzania obrazów DICOM.
 */
public class Algorytmy {
    /**
     * Pole zawierające informacje dotyczące obrazu DICOM.
     */
    private SourceImage dicom1;

    /**
     * Funkcja algorytm 1 implementująca segmentację obrazu DICOM poprzez zastosowanie progowania obrazu.
     *
     * @param dicomDane Lista atrybutów DICOM, która zawiera dane obrazu w formacie DICOM.
     * @param wartość Wartość przekazywana do funkcji algorytm 1 w celu ustalenia sposobu progowania.
     * @param gui Referencja do klasy GUI służącej do wyświetlania efektów implementacji algorytmów.
     */
    public void algorytm1(AttributeList dicomDane, short wartość, GUI gui) {
        System.out.println("Algorytm 1");
        try {
            OtherWordAttribute danePikseli = (OtherWordAttribute) (dicomDane.get(TagFromName.PixelData));
            short[] piksele = danePikseli.getShortValues();
            short[] pikseleKopia = new short[piksele.length];
            System.arraycopy(piksele,0,pikseleKopia,0,pikseleKopia.length);

            int n = piksele.length;

            /**
             * Obliczenie statystyk pikseli zawartych w danych obrazu DICOM.
             */
            int[] statistics = new int[4];
            obliczStatystyki(piksele, statistics);

            int max = statistics[0];
            int min = statistics[1];
            int średnia = statistics[2];
            int odchylenieStandardowe = statistics[3];

            /**
             * Obliczenie histogramu pikseli zawartych w danych obrazu DICOM.
             */
            int[] histogram = obliczHistogram(piksele,max);
            for (int i = 0; i < histogram.length; i++) {
                System.out.println("Histogram wartość " + i + ": " + histogram[i] + " pikseli.");
            }


            System.out.println("MAX: " + max + "\n MIN: " + min);
            System.out.println("MEAN: " + średnia + "\n ODCHYLENIE STANDARDOWE: " + odchylenieStandardowe);

            /**
             * Obliczenie wartości zakresu progowania na podstawie uzyskanych danych z histogramu i wybór progowania przez użytkownika.
             */
            int[] progi = dostosujProgowanieZHistogramem(histogram);
            int dolnyProg = progi[0];
            int gornyProg = progi[1];

            int d = pobierzInput("Wpisz wartość progowania pomiędzy "
                    + dolnyProg + " a " + gornyProg + " : ", dolnyProg, gornyProg);

            /**
             * Implementacja progowania obrazu na podstawie danych wprowadzonych przez użytkownika.
             */
            if(d != 0) {

                if(wartość == (short) 0) {
                    for (int i = 0; i < n; i++) {

                        if (piksele[i] <= d) {
                            piksele[i] = wartość;
                        } else if (piksele[i] > d) {
                            piksele[i] = (short) max;
                        }

                    }
                } else if (wartość == (short) 1) {

                    for (int i = 0; i < n; i++) {
                        if (piksele[i] <= d) {
                            piksele[i] = (short) max;
                        } else if (piksele[i] > d) {
                            piksele[i] = 0;
                        }
                    }

                }

                dicom1 = new SourceImage(dicomDane);
                gui.wyświetlPoZmianie(dicom1);
                System.arraycopy(pikseleKopia, 0, piksele, 0, piksele.length);

            }

        } catch(Exception e) {
            /**
             * Obługa wyjątku spowodowanego formatem bajtowym pikseli obrazu DICOM.
             */
            try {
                OtherByteAttribute pixelAttribute = (OtherByteAttribute) dicomDane.get(TagFromName.PixelData);
                byte[] piksele = pixelAttribute.getByteValues();
                byte[] pikseleKopia = new byte[piksele.length];
                System.arraycopy(piksele,0,pikseleKopia,0,pikseleKopia.length);

                /**
                 * Obliczenie statystyk pikseli zawartych w danych obrazu DICOM.
                 */
                int max = 0;
                int n = piksele.length;

                for (int i = 0; i < n; i++) {
                    max = Math.max(max, piksele[i]);
                }

                int min = max;

                for (int i = 0; i < n; i++) {

                    if (piksele[i] > 0) {
                        min = Math.min(min, piksele[i]);
                    }

                }

                /**
                 * Ustalenie wartości zakresu progowania na podstawie statystyk pikseli i wybór progowania przez użytkownika.
                 */
                System.out.println("MAX: " + max + "\nMIN: " + min);

                int d = pobierzInput("Wpisz wartość progowania pomiędzy "
                        + min + " a " + max + " : ", min, max);

                /**
                 * Implementacja progowania obrazu na podstawie danych wprowadzonych przez użytkownika.
                 */
                if(d != 0) {

                    if(wartość == (short) 0) {

                        for (int i = 0; i < n; i++) {

                            if (piksele[i] <= d) {
                                piksele[i] = (byte) wartość;
                            } else if (piksele[i] > d) {
                                piksele[i] = (byte) max;
                            }

                        }

                    } else if (wartość == (short) 1) {

                        for (int i = 0; i < n; i++) {

                            if (piksele[i] <= d) {
                                piksele[i] = (byte) max;
                            } else if (piksele[i] > d) {
                                piksele[i] = 0;
                            }

                        }

                    }

                    dicom1 = new SourceImage(dicomDane);
                    gui.wyświetlPoZmianie(dicom1);
                    System.arraycopy(pikseleKopia, 0, piksele, 0, piksele.length);

                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Błąd podczas wykonywania algorytmu 1.",
                        "Błąd!", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    /**
     * Funkcja obliczająca histogram obrazu pikseli obrazu DICOM.
     *
     * @param piksele Tablica pikseli obrazu.
     * @param max Maksymalna wartość pikseli obrazu.
     * @return Zwraca tablicę z histogramem obrazu.
     */
    private int[] obliczHistogram(short[] piksele, int max){
        Map<Short, Integer> mapaHistogramu = new HashMap<>();

        for (short piksel : piksele) {
            mapaHistogramu.put(piksel, mapaHistogramu.getOrDefault(piksel, 0) + 1);
        }

        int[] histogram = new int[max];

        for (int i = 0; i < max; i++) {
            if (mapaHistogramu.containsKey((short) i)) {
                histogram[i] = mapaHistogramu.get((short) i);
            } else {
                histogram[i] = 0;
            }
        }
        return histogram;
    }

    /**
     * Funkcja dostosowująca zakres progowania obrazu na podstawie wyznaczonego histogramu.
     *
     * @param histogram Tablica z histogramem obrazu.
     * @return Zwraca tablicę z wyznaczonymi wartościami zakresu progowania.
     */
    private int[] dostosujProgowanieZHistogramem(int[] histogram){
        int całość = Arrays.stream(histogram).sum();
        int dolnyprocent = całość / 3; //30% histogramu poniżej jest odrzucane - 30-95% histogramu bierzemy pod uwagę w zakresie progowania
        int gornyprocent = całość * 19 / 20; //5% histogramu powyżej jest odrzucane

        int suma = 0;
        int dolnyprog = 0;
        int gornyprog = 0;

        for(int i = 0; i < histogram.length; i++){
            suma += histogram[i];

            if(dolnyprog == 0 && suma >= dolnyprocent) {
                dolnyprog = i;
            }

            if(suma >= gornyprocent) {
                gornyprog = i;
                break;
            }
        }

        return new int[]{dolnyprog, gornyprog};

    }

    /**
     * Funkcja wyznaczająca podstawowe statystyki obrazu.
     *
     * @param piksele Tablica pikseli obrazu.
     * @param statystyki Tablica przechowująca obliczone statystyki obrazu.
     */
    private void obliczStatystyki(short[] piksele, int[] statystyki) {
        int n = piksele.length;
        short max = 0;
        long suma = 0;
        double roznicaKwadratow = 0;

        for (int i = 0; i < n; i++) {
            max = (short) Math.max(max, piksele[i]);
            suma += piksele[i];
        }

        int min = max;

        for (int i = 0; i < n; i++) {

            if (piksele[i] > 0) {
                min = Math.min(min, piksele[i]);
            }

        }

        double średnia = (double) suma / n;

        for (int i = 0; i < n; i++) {
            roznicaKwadratow += Math.pow(piksele[i] - średnia, 2);
        }

        double odchylenieStandardowe = Math.sqrt(roznicaKwadratow / n);

        statystyki[0] = max;
        statystyki[1] = min;
        statystyki[2] = (int) średnia;
        statystyki[3] = (int) odchylenieStandardowe;
    }

    /**
     * Funkcja pobierająca wartość od użytkownika i sprawdzająca czy wprowadzane dane są prawidłowe.
     *
     * @param message Wiadomość wyświetlana użytkownikowi w oknie dialogowym.
     * @param min Dolna granica zakresu progowania.
     * @param max Górna granica zakresu progowania.
     * @return Zwraca wartość progowania wprowadzoną przez użytkownika.
     */
    private  int pobierzInput(String message, int min, int max) {
        int rezultat = 0;
        boolean czyDobryInput = false;

        do {
            try {
                String input = JOptionPane.showInputDialog(message);

                if(input == null) {
                    return 0;
                }

                rezultat = Integer.parseInt(input);

                if (rezultat < min || rezultat > max) {
                    JOptionPane.showMessageDialog(null, "Błąd! Wprowadź poprawne dane.",
                            "Błąd!", JOptionPane.ERROR_MESSAGE);
                } else {
                    czyDobryInput = true;
                }
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Błąd! Wprowadź poprawne dane.",
                        "Błąd!", JOptionPane.ERROR_MESSAGE);
            }

        } while (!czyDobryInput);

        return rezultat;

    }

    /**
     * Funkcja implementująca algorytm rozrostu regionu poprzez sprawdzanie wartości pikseli typu short i ich zmianę przy wykorzystaniu stosu.
     *
     * @param numerPiksela Numer piksela, od którego rozpoczynamy sprawdzanie i zmianę wartości.
     * @param dicom Pole zawierające dane obrazu DICOM.
     * @param piksele Tablica pikseli obrazu.
     * @param a Wartość aktualnego piksela.
     * @param widełki Podobieństwo rozrostu regionu.
     */
    public void ZmienianieWartościPikselaStos(int numerPiksela, SourceImage dicom,
                                              short[] piksele, short a, short widełki) {

        int szerokość = dicom.getWidth();
        Stack<Integer> stack = new Stack<>();
        stack.push(numerPiksela);

        while (!stack.isEmpty()) {
            int c = stack.pop();

            if (c >= 0 && c < piksele.length && piksele[c] != 0) {

                piksele[c] = 0;

                //sprawdzam piksel po prawej
                if (c + 1 < piksele.length && c % szerokość != szerokość - 1 && piksele[c + 1] != a &&
                        piksele[c + 1] > a - widełki && piksele[c + 1] < a + widełki) {

                    stack.push(c + 1);

                }
                //sprawdzam piksel po lewej
                if (c - 1 >= 0 && c % szerokość != 0 && piksele[c - 1] != a &&
                        piksele[c - 1] > a - widełki && piksele[c - 1] < a + widełki) {

                    stack.push(c - 1);

                }
                //sprawdzam piksel na górze
                if (c + szerokość < piksele.length && piksele[c + szerokość] != a &&
                        piksele[c + szerokość] > a - widełki && piksele[c + szerokość] < a + widełki) {

                    stack.push(c + szerokość);

                }
                //sprawdzam piksel na dole
                if (c - szerokość >= 0 && piksele[c - szerokość] != a &&
                        piksele[c - szerokość] > a - widełki && piksele[c - szerokość] < a + widełki) {

                    stack.push(c - szerokość);

                }

            }

        }

    }

    /**
     * Funkcja implementująca algorytm rozrostu regionu poprzez sprawdzanie wartości pikseli typu byte i ich zmianę przy wykorzystaniu stosu.
     *
     * @param numerPiksela Numer piksela, od którego rozpoczynamy sprawdzanie i zmianę wartości.
     * @param dicom Pole zawierające dane obrazu DICOM.
     * @param piksele Tablica pikseli obrazu.
     * @param a Wartość aktualnego piksela.
     * @param widełki Podobieństwo rozrostu regionu.
     */
    public void ZmienianieWartościPikselaStosByte(int numerPiksela, SourceImage dicom,
                                                  byte[] piksele, byte a, byte widełki) {

        int szerokosc = dicom.getWidth();
        Stack<Integer> stack = new Stack<>();
        stack.push(numerPiksela);

        while (!stack.isEmpty()) {
            int c = stack.pop();

            if (c >= 0 && c < piksele.length && piksele[c] != 0) {

                piksele[c] = 0;

                if (c + 1 < piksele.length && c % szerokosc != szerokosc - 1 && piksele[c + 1] != a &&
                        piksele[c + 1] > a - widełki && piksele[c + 1] < a + widełki) {

                    stack.push((c + 1));

                }
                if (c - 1 >= 0 && c % szerokosc != 0 && piksele[c - 1] != a &&
                        piksele[c - 1] > a - widełki && piksele[c - 1] < a + widełki) {

                    stack.push((c - 1));

                }
                if (c + szerokosc < piksele.length && piksele[c + szerokosc] != a &&
                        piksele[c + szerokosc] > a - widełki && piksele[c + szerokosc] < a + widełki) {

                    stack.push((c + szerokosc));

                }
                if (c - szerokosc >= 0 && piksele[c - szerokosc] != a &&
                        piksele[c - szerokosc] > a - widełki && piksele[c - szerokosc] < a + widełki) {

                    stack.push((c - szerokosc));

                }

            }

        }

    }

    /**
     * Funkcja algorytm 2 implementująca segmentację obrazu DICOM poprzez zastosowanie rozrostu regionu.
     *
     * @param dicomDane Lista atrybutów DICOM, która zawiera dane obrazu w formacie DICOM.
     * @param numerPiksela Piksel, od którego zaczynamy implementację algorytmu rozrostu regionu.
     * @param gui Referencja do klasy GUI służącej do wyświetlania efektów implementacji algorytmów.
     */
    public void algorytm2(AttributeList dicomDane, int numerPiksela, GUI gui) {
        System.out.println("Algorytm 2");
        try {
            SourceImage dicom = new SourceImage(dicomDane);
            OtherWordAttribute danePikseli = (OtherWordAttribute) (dicomDane.get(TagFromName.PixelData));
            short[] piksele = danePikseli.getShortValues();
            short[] pikseleKopia = new short[piksele.length];
            System.arraycopy(piksele,0,pikseleKopia,0,pikseleKopia.length);

            /**
             * Obliczam statystyki pikseli zawartych w danych obrazu DICOM.
             */
            int[] statistics = new int[4];
            obliczStatystyki(piksele, statistics);

            int max = statistics[0];
            int min = statistics[1];
            int mean = statistics[2];
            int odchylenieStandardowe = statistics[3];

            System.out.println("MAX: " + max + "\n MIN: " + min);
            System.out.println("MEAN: " + mean + "\n ODCHYLENIE: " + odchylenieStandardowe);

            double k = ustawParametr(min, max);
            int dolnyProg = 1;
            int gornyProg = (int) (k*(max - piksele[numerPiksela]));

            int widełki = pobierzInput("Wpisz wartość podobieństwa rozrostu regionu pomiędzy "
                    + dolnyProg + " a " + gornyProg + " : ", dolnyProg, gornyProg);

            short a = piksele[numerPiksela];

            if(widełki != 0) {
                ZmienianieWartościPikselaStos(numerPiksela, dicom, piksele, a, (short) widełki);
                dicom1 = new SourceImage(dicomDane);
                gui.wyświetlPoZmianie(dicom1);
                System.arraycopy(pikseleKopia, 0, piksele, 0, piksele.length);
            }

        } catch (Exception c) {
            try {
                SourceImage dicom = new SourceImage(dicomDane);
                OtherByteAttribute danePikseli = (OtherByteAttribute) (dicomDane.get(TagFromName.PixelData));
                byte[] piksele = danePikseli.getByteValues();
                byte[] pikseleKopia = new byte[piksele.length];
                System.arraycopy(piksele, 0, pikseleKopia, 0, pikseleKopia.length);

                int max = 0;
                int n = piksele.length;

                for (int i = 0; i < n; i++) {
                    max = Math.max(max, piksele[i]);
                }

                int min = max;

                for (int i = 0; i < n; i++) {
                    if (piksele[i] > 0) {
                        min = Math.min(min, piksele[i]);
                    }
                }

                int dolnyProg = 1;
                int gornyProg = max;

                System.out.println("MAX: " + max + "\nMIN: " + min);

                int widełki = pobierzInput("Wpisz wartość podobieństwa rozrostu regionu pomiędzy "
                        + dolnyProg + " a " + gornyProg + " : ", dolnyProg, gornyProg);

                byte a = piksele[numerPiksela];

                if(widełki != 0) {
                    ZmienianieWartościPikselaStosByte(numerPiksela, dicom, piksele, a, (byte) widełki);
                    dicom1 = new SourceImage(dicomDane);
                    gui.wyświetlPoZmianie(dicom1);
                    System.arraycopy(pikseleKopia, 0, piksele, 0, piksele.length);
                }

            } catch (Exception er) {
                JOptionPane.showMessageDialog(null, "Błąd podczas wykonywania algorytmu 2.",
                        "Błąd!", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    /**
     * Funkcja ustalająca parametr potrzebny do określenia zakresu podobieństwa rozrostu regionu.
     *
     * @param min Minimalna wartość pikseli obrazu.
     * @param max Maksymalna wartość pikseli obrazu.
     * @return Parametr k wykorzystywany do określenia zakresu podobieństwa.
     */
    private double ustawParametr(int min, int max) {
        int różnica = max - min;
        double k;

        if(różnica > 10000) {
            k = 0.5;
        } else if (różnica < 500) {
            k = 0.3;
        } else {
            k = 0.4;
        }
        return k;
    }

}

