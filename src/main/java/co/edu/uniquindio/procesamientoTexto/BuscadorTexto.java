package co.edu.uniquindio.procesamientoTexto;

public class BuscadorTexto {

    public static int buscarPatron(String texto, String patron, int posicionInicial) {
        int tamanoTexto = texto.length();
        int tamanoPatron = patron.length();

        for (int i = posicionInicial; i <= tamanoTexto - tamanoPatron; i++) {
            int j = 0;                                                                          //cantidad de letras que coinciden
            while (j < tamanoPatron && aMinuscula(texto.charAt(i + j)) == aMinuscula(patron.charAt(j))) {
                j++;
            }
            if (j == tamanoPatron) {
                return i;
            }
        }
        return -1;
    }

    public static char aMinuscula(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + 32);
        }
        return c;
    }
}
