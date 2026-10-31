package co.edu.uniquindio.similitud;

public class DistanciaEuclidiana {

    public static double calcularDistancia(double[] u, double[] v) {
        double suma = 0;
        for (int k = 0; k < u.length; k++) {
            double diferencia = u[k] - v[k];
            suma = suma + diferencia * diferencia;
        }
        return Math.sqrt(suma);
    }

    public static double calcularSimilitud(double distancia) {
        return 1.0 / (1.0 + distancia);
    }
}
