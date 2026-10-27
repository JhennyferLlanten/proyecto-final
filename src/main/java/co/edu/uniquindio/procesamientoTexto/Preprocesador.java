package co.edu.uniquindio.procesamientoTexto;

import java.util.ArrayList;

public class Preprocesador {

    public static ArrayList<String> obtenerPalabras(String texto) {
        ArrayList<String> palabras = new ArrayList<>();         //tiene las palabras que sirven
        StringBuilder actual = new StringBuilder();             //la palabra que se esta armando

        for (int i = 0; i < texto.length(); i++) {
            char c = BuscadorTexto.aMinuscula(texto.charAt(i));
            if (c >= 'a' && c <= 'z') {
                actual.append(c);                               //es una letra: sigue armando la palabra
            } else {
                guardarPalabra(actual, palabras);               //no es letra: la palabra termino
            }
        }
        guardarPalabra(actual, palabras);                       //la linea termina en una letra, guarda la ultima palabra encontrada
        return palabras;
    }

    private static void guardarPalabra(StringBuilder actual, ArrayList<String> palabras) {
        if (actual.length() >= 2 && !PalabrasVacias.esPalabraVacia(actual.toString())) {
            palabras.add(actual.toString());
        }
        actual.setLength(0);                                    //vacia la palabra para empezar otra
    }
}
