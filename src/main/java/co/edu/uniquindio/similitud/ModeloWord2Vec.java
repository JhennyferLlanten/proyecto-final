package co.edu.uniquindio.similitud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ModeloWord2Vec {

    public static final int DIMENSION = 300;                                                    //numeros de cada vector

    public ArrayList<String> vocabulario = new ArrayList<>();                                   //palabras con vector
    public ArrayList<double[]> vectoresPalabras = new ArrayList<>();
    public double[][] vectores;                                                                 //tabla de vectores por abstract
    public double milisegundos;

    public ModeloWord2Vec(ArrayList<ArrayList<String>> documentos) throws IOException {
        long inicio = System.nanoTime();
        cargarVectores();                                                                       //lee los vectores
        vectores = new double[documentos.size()][];                                             //lista de palabras por abstract
        for (int d = 0; d < documentos.size(); d++) {                                           //por cada abstract calcula el promedio
            vectores[d] = calcularPromedio(documentos.get(d));
        }
        long fin = System.nanoTime();
        milisegundos = (fin - inicio) / 1000000.0;
    }

    private void cargarVectores() throws IOException {
        InputStream archivo = ModeloWord2Vec.class.getResourceAsStream("/vectores/word2vec.txt");        //Abre el archivo con los vectores del modelo
        BufferedReader lector = new BufferedReader(new InputStreamReader(archivo, StandardCharsets.UTF_8));
        String linea = lector.readLine();
        while (linea != null) {
            String[] valores = linea.split(" "); //                                           //separa cada valor
            double[] vector = new double[DIMENSION];                                                //crea el vector
            for (int k = 0; k < DIMENSION; k++) {                                                   //convierte el valor en numero
                vector[k] = Double.parseDouble(valores[k + 1]);
            }
            vocabulario.add(valores[0]);                                                             //guarda la palabra
            vectoresPalabras.add(vector);                                                           //guarda su vector
            linea = lector.readLine();
        }
        lector.close();
    }

    public double[] calcularPromedio(ArrayList<String> palabras) {
        double[] promedio = new double[DIMENSION];
        int encontradas = 0;
        for (int i = 0; i < palabras.size(); i++) {                                                     //recorre las palabras del texto
            int k = Vocabulario.posicion(vocabulario, palabras.get(i));                                 //compara y devuelve la posicion si la palabra tiene vector
            if (k != -1) {
                double[] vector = vectoresPalabras.get(k);
                for (int j = 0; j < DIMENSION; j++) {                                                   //recorre y suma las posiciones de cada vector
                    promedio[j] = promedio[j] + vector[j];
                }
                encontradas++;
            }
        }
        if (encontradas > 0) {                                                                          //sca el promedio de cada posicion del vector
            for (int j = 0; j < DIMENSION; j++) {
                promedio[j] = promedio[j] / encontradas;
            }
        }
        return promedio;
    }
}
