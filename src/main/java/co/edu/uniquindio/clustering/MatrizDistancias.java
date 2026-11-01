package co.edu.uniquindio.clustering;

public class MatrizDistancias {

    public static double[][] calcularMatrizDistancia(double[][] similitud) {
        int total = similitud.length;
        double[][] distancias = new double[total][total];
        for (int i = 0; i < total; i++) {
            for (int j = 0; j < total; j++) {
                if (i != j) {
                    distancias[i][j] = Math.max(0, 1 - similitud[i][j]);                //convierte el coseno en distancia
                }
            }
        }
        return distancias;
    }
}
