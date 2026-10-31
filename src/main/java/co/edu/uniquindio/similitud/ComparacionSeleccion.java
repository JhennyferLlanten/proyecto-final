package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class ComparacionSeleccion {

    public ArrayList<FilaSimilitud> filas = new ArrayList<>();
    public ResultadoSimilitud[] totales = {
            new ResultadoSimilitud("Jaccard"),
            new ResultadoSimilitud("Coseno con TF-IDF"),
            new ResultadoSimilitud("Levenshtein por palabras"),
            new ResultadoSimilitud("Needleman-Wunsch por palabras")
    };

    public ComparacionSeleccion(double milisegundosModelo) {
        totales[1].milisegundosPreprocesamiento = milisegundosModelo;
    }

    public void agregar(FilaSimilitud fila, ResultadoSimilitud jaccard, ResultadoSimilitud coseno,
                        ResultadoSimilitud levenshtein, ResultadoSimilitud needlemanWunsch) {
        filas.add(fila);
        totales[0].milisegundosPreprocesamiento = totales[0].milisegundosPreprocesamiento + jaccard.milisegundosPreprocesamiento;
        totales[0].milisegundosCalculo = totales[0].milisegundosCalculo + jaccard.milisegundosCalculo;
        totales[1].milisegundosCalculo = totales[1].milisegundosCalculo + coseno.milisegundosCalculo;
        totales[2].milisegundosCalculo = totales[2].milisegundosCalculo + levenshtein.milisegundosCalculo;
        totales[3].milisegundosCalculo = totales[3].milisegundosCalculo + needlemanWunsch.milisegundosCalculo;
    }
}
