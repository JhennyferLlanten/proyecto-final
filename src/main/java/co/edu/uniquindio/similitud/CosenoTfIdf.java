package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class CosenoTfIdf {

    public static int contarDocumentos(ArrayList<ArrayList<String>> documentos, String palabra) {           //df: en cuantos docs aparece la palabra
        int cantidad = 0;
        for (int d = 0; d < documentos.size(); d++) {
            if (Vocabulario.posicion(documentos.get(d), palabra) != -1) {                                    //compara y devuelve la posición de la palabra si es igual
                cantidad++;
            }
        }
        return cantidad;
    }

    public static double[] calcularIdf(ArrayList<ArrayList<String>> documentos, ArrayList<String> vocabulario) {        //idf por cada palabra del vocabulario (inusual)
        double[] idf = new double[vocabulario.size()];
        for (int k = 0; k < vocabulario.size(); k++) {
            int cantidad = contarDocumentos(documentos, vocabulario.get(k));
            idf[k] = Math.log((double) documentos.size() / cantidad);                                                   //ln(N / df)
        }
        return idf;
    }

    public static double[] calcularVector(ArrayList<String> palabras, ArrayList<String> vocabulario, double[] idf) {
        double[] vector = new double[vocabulario.size()];
        for (int i = 0; i < palabras.size(); i++) {                                                             //tf: Cuántas veces aparece la palabra
            int k = Vocabulario.posicion(vocabulario, palabras.get(i));                                         //posicion en el vocabulario
            if (k != -1) {
                vector[k] = vector[k] + 1;
            }
        }
        for (int k = 0; k < vector.length; k++) {                                                               //peso: tf × idf
            vector[k] = vector[k] * idf[k];
        }
        return vector;
    }

    public static double calcularCoseno(double[] u, double[] v) {
        double productoPunto = 0;
        double sumaU = 0;
        double sumaV = 0;
        for (int k = 0; k < u.length; k++) {
            productoPunto = productoPunto + u[k] * v[k];
            sumaU = sumaU + u[k] * u[k];
            sumaV = sumaV + v[k] * v[k];
        }
        if (sumaU == 0 || sumaV == 0) {
            return 0.0;
        }
        return productoPunto / (Math.sqrt(sumaU) * Math.sqrt(sumaV));                           //similitud de una pareja de abstract
    }
}
