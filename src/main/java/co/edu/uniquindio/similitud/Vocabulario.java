package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class Vocabulario {

    public static int posicion(ArrayList<String> lista, String palabra) {                         //compara y devuelve la posición de la palabra si es igual
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).equals(palabra)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<String> distintas(ArrayList<String> palabras) {                        //guarda las palabras sin repetir
        ArrayList<String> distintas = new ArrayList<>();
        for (int i = 0; i < palabras.size(); i++) {
            if (posicion(distintas, palabras.get(i)) == -1) {
                distintas.add(palabras.get(i));
            }
        }
        return distintas;
    }

    public static ArrayList<String> construir(ArrayList<ArrayList<String>> documentos) {
        ArrayList<String> vocabulario = new ArrayList<>();
        for (int d = 0; d < documentos.size(); d++) {
            ArrayList<String> palabras = documentos.get(d);
            for (int i = 0; i < palabras.size(); i++) {
                if (posicion(vocabulario, palabras.get(i)) == -1) {
                    vocabulario.add(palabras.get(i));
                }
            }
        }
        return vocabulario;
    }
}
