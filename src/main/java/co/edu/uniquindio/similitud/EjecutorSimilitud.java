package co.edu.uniquindio.similitud;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.procesamientoTexto.Preprocesador;

import java.util.ArrayList;

public class EjecutorSimilitud {

    public static ArrayList<ArrayList<String>> obtenerPalabras(Articulo[] articulos) {
        ArrayList<ArrayList<String>> palabras = new ArrayList<>();
        for (int i = 0; i < articulos.length; i++) {
            palabras.add(Preprocesador.obtenerPalabrasCompletas(articulos[i].resumen));
        }
        return palabras;
    }

    public static ComparacionSeleccion compararSeleccion(Articulo[] articulos, ArrayList<ArrayList<String>> palabras,
                                                         ModeloTfIdf modelo, ModeloWord2Vec modeloWord2Vec, ArrayList<Integer> posiciones) {
        ComparacionSeleccion comparacion = new ComparacionSeleccion(modelo.milisegundos, modeloWord2Vec.milisegundos);
        for (int i = 0; i < posiciones.size(); i++) {
            for (int j = i + 1; j < posiciones.size(); j++) {
                int posicionA = posiciones.get(i);
                int posicionB = posiciones.get(j);
                ArrayList<String> a = palabras.get(posicionA);
                ArrayList<String> b = palabras.get(posicionB);
                ResultadoSimilitud jaccard = ejecutarJaccard(a, b);
                ResultadoSimilitud coseno = ejecutarCoseno(modelo, posicionA, posicionB);
                ResultadoSimilitud levenshtein = ejecutarLevenshtein(a, b);
                ResultadoSimilitud needlemanWunsch = ejecutarNeedlemanWunsch(a, b);
                ResultadoSimilitud word2vec = ejecutarWord2Vec(modeloWord2Vec, posicionA, posicionB);
                ResultadoSimilitud word2vecEuclidiana = ejecutarWord2VecEuclidiana(modeloWord2Vec, posicionA, posicionB);
                FilaSimilitud fila = new FilaSimilitud(articulos[posicionA].numero, articulos[posicionB].numero,
                        jaccard.similitud, coseno.similitud, levenshtein.similitud, needlemanWunsch.similitud, word2vec.similitud, word2vecEuclidiana.similitud);
                comparacion.agregar(fila, jaccard, coseno, levenshtein, needlemanWunsch, word2vec, word2vecEuclidiana);
            }
        }
        return comparacion;
    }

    public static ResultadoSimilitud ejecutarJaccard(ArrayList<String> a, ArrayList<String> b) {
        ResultadoSimilitud resultado = new ResultadoSimilitud("Jaccard");

        long inicio = System.nanoTime();
        ArrayList<String> conjuntoA = Vocabulario.distintas(a);
        ArrayList<String> conjuntoB = Vocabulario.distintas(b);
        long fin = System.nanoTime();
        resultado.milisegundosPreprocesamiento = (fin - inicio) / 1000000.0;

        inicio = System.nanoTime();
        int comunes = Jaccard.contarComunes(conjuntoA, conjuntoB);
        resultado.similitud = Jaccard.calcularSimilitud(comunes, conjuntoA.size(), conjuntoB.size());
        fin = System.nanoTime();
        resultado.milisegundosCalculo = (fin - inicio) / 1000000.0;

        int union = conjuntoA.size() + conjuntoB.size() - comunes;
        resultado.detalle = comunes + " palabras en comun de " + union + " distintas en total";
        return resultado;
    }

    public static ResultadoSimilitud ejecutarCoseno(ModeloTfIdf modelo, int posicionA, int posicionB) {
        ResultadoSimilitud resultado = new ResultadoSimilitud("Coseno con TF-IDF");
        resultado.milisegundosPreprocesamiento = modelo.milisegundos;

        long inicio = System.nanoTime();
        resultado.similitud = CosenoTfIdf.calcularCoseno(modelo.vectores[posicionA], modelo.vectores[posicionB]);
        long fin = System.nanoTime();
        resultado.milisegundosCalculo = (fin - inicio) / 1000000.0;

        resultado.detalle = "vectores de " + modelo.vocabulario.size() + " palabras del corpus";
        return resultado;
    }

    public static ResultadoSimilitud ejecutarWord2Vec(ModeloWord2Vec modelo, int posicionA, int posicionB) {
        ResultadoSimilitud resultado = new ResultadoSimilitud("Word2Vec (coseno)");
        resultado.milisegundosPreprocesamiento = modelo.milisegundos;

        long inicio = System.nanoTime();
        resultado.similitud = CosenoTfIdf.calcularCoseno(modelo.vectores[posicionA], modelo.vectores[posicionB]);
        long fin = System.nanoTime();
        resultado.milisegundosCalculo = (fin - inicio) / 1000000.0;

        resultado.detalle = "vectores promedio de " + ModeloWord2Vec.DIMENSION + " dimensiones";
        return resultado;
    }

    public static ResultadoSimilitud ejecutarWord2VecEuclidiana(ModeloWord2Vec modelo, int posicionA, int posicionB) {
        ResultadoSimilitud resultado = new ResultadoSimilitud("Word2Vec (distancia euclidiana)");
        resultado.milisegundosPreprocesamiento = modelo.milisegundos;

        long inicio = System.nanoTime();
        double distancia = DistanciaEuclidiana.calcularDistancia(modelo.vectores[posicionA], modelo.vectores[posicionB]);
        resultado.similitud = DistanciaEuclidiana.calcularSimilitud(distancia);
        long fin = System.nanoTime();
        resultado.milisegundosCalculo = (fin - inicio) / 1000000.0;

        resultado.detalle = "distancia euclidiana = " + distancia;
        return resultado;
    }

    public static ResultadoSimilitud ejecutarLevenshtein(ArrayList<String> a, ArrayList<String> b) {
        ResultadoSimilitud resultado = new ResultadoSimilitud("Levenshtein por palabras");

        long inicio = System.nanoTime();
        int[][] tabla = Levenshtein.construirTabla(a, b);
        int distancia = tabla[a.size()][b.size()];
        resultado.similitud = Levenshtein.calcularSimilitud(distancia, a.size(), b.size());
        long fin = System.nanoTime();
        resultado.milisegundosCalculo = (fin - inicio) / 1000000.0;

        resultado.detalle = "distancia de edicion = " + distancia + " (tabla de " + (a.size() + 1) + " x " + (b.size() + 1) + ")";
        return resultado;
    }

    public static ResultadoSimilitud ejecutarNeedlemanWunsch(ArrayList<String> a, ArrayList<String> b) {
        ResultadoSimilitud resultado = new ResultadoSimilitud("Needleman-Wunsch por palabras");

        long inicio = System.nanoTime();
        int[][] tabla = NeedlemanWunsch.construirTabla(a, b);
        int puntaje = tabla[a.size()][b.size()];
        resultado.similitud = NeedlemanWunsch.calcularSimilitud(puntaje, a.size(), b.size());
        long fin = System.nanoTime();
        resultado.milisegundosCalculo = (fin - inicio) / 1000000.0;

        resultado.detalle = "puntaje del alineamiento = " + puntaje + " (tabla de " + (a.size() + 1) + " x " + (b.size() + 1) + ")";
        return resultado;
    }
}
