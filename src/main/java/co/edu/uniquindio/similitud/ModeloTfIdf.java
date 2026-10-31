package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class ModeloTfIdf {

    public ArrayList<String> vocabulario;
    public double[] idf;
    public double[][] vectores;
    public double milisegundos;

    public ModeloTfIdf(ArrayList<ArrayList<String>> documentos) {
        long inicio = System.nanoTime();
        vocabulario = Vocabulario.construir(documentos);
        idf = CosenoTfIdf.calcularIdf(documentos, vocabulario);
        vectores = new double[documentos.size()][];                                         //pesos de las palabras en cada abstract
        for (int d = 0; d < documentos.size(); d++) {
            vectores[d] = CosenoTfIdf.calcularVector(documentos.get(d), vocabulario, idf);
        }
        long fin = System.nanoTime();
        milisegundos = (fin - inicio) / 1000000.0;
    }

    public double[][] calcularMatriz() {
        int total = vectores.length;
        double[][] matriz = new double[total][total];                                           //Similitud de cada pareja
        for (int i = 0; i < total; i++) {
            for (int j = 0; j < total; j++) {
                matriz[i][j] = CosenoTfIdf.calcularCoseno(vectores[i], vectores[j]);           //similitud coseno entre una pareja abstracts
            }
        }
        return matriz;
    }
}
