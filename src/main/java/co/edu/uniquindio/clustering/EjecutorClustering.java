package co.edu.uniquindio.clustering;

import co.edu.uniquindio.modelo.Articulo;

public class EjecutorClustering {

    public static final int GRUPOS_MINIMO = 2;
    public static final int GRUPOS_MAXIMO = 6;

    public static ResultadoClustering ejecutar(double[][] distancias, Articulo[] articulos) {
        String[] nombres = {"Single linkage", "Complete linkage", "Average linkage"};
        int[] criterios = {Agrupamiento.SINGLE, Agrupamiento.COMPLETE, Agrupamiento.AVERAGE};

        ResultadoCriterio[] resultados = new ResultadoCriterio[3];
        int posicionGanador = 0;
        for (int i = 0; i < 3; i++) {
            resultados[i] = evaluarCriterio(nombres[i], criterios[i], distancias, articulos);                   //evalua cada criterio
            if (resultados[i].mejorSilueta > resultados[posicionGanador].mejorSilueta) {                        //determina el ganador
                posicionGanador = i;
            }
        }
        return new ResultadoClustering(resultados, posicionGanador);
    }

    private static ResultadoCriterio evaluarCriterio(String nombre, int criterio, double[][] distancias, Articulo[] articulos) {
        int total = distancias.length;
        Union[] uniones = Agrupamiento.construirArbol(distancias, criterio);                                    //crea el arbol

        double[] siluetas = new double[GRUPOS_MAXIMO - GRUPOS_MINIMO + 1];
        int mejorGrupos = GRUPOS_MINIMO;
        double mejorSilueta = -2;
        for (int numeroGrupos = GRUPOS_MINIMO; numeroGrupos <= GRUPOS_MAXIMO; numeroGrupos++) {                  //corta arbol 5 veces determina la mejor silueta
            int[] etiquetas = Agrupamiento.cortarArbol(uniones, total, numeroGrupos);
            double silueta = Silueta.calcularSilueta(distancias, etiquetas, numeroGrupos);                       //calcula la silueta
            siluetas[numeroGrupos - GRUPOS_MINIMO] = silueta;
            if (silueta > mejorSilueta) {
                mejorSilueta = silueta;
                mejorGrupos = numeroGrupos;
            }
        }

        ResultadoCriterio resultado = new ResultadoCriterio(nombre, uniones, siluetas);
        resultado.mejorGrupos = mejorGrupos;
        resultado.mejorSilueta = mejorSilueta;

        int[] etiquetasMejor = Agrupamiento.cortarArbol(uniones, total, mejorGrupos);
        for (int grupo = 0; grupo < mejorGrupos; grupo++) {
            String miembros = "";
            for (int i = 0; i < total; i++) {
                if (etiquetasMejor[i] == grupo) {
                    miembros = miembros + articulos[i].numero + " ";
                }
            }
            resultado.gruposMejorCorte.add(miembros);
        }
        return resultado;
    }
}
