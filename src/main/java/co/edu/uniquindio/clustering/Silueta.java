package co.edu.uniquindio.clustering;

public class Silueta {

    public static double calcularSilueta(double[][] distancias, int[] etiquetas, int numeroGrupos) {
        int total = distancias.length;
        int[] tamano = new int[numeroGrupos];
        for (int i = 0; i < total; i++) {
            tamano[etiquetas[i]]++;
        }

        double suma = 0;
        for (int i = 0; i < total; i++) {
            if (tamano[etiquetas[i]] > 1) {
                double[] sumaDistancias = new double[numeroGrupos];
                for (int j = 0; j < total; j++) {
                    if (j != i) {
                        sumaDistancias[etiquetas[j]] += distancias[i][j];
                    }
                }

                double a = sumaDistancias[etiquetas[i]] / (tamano[etiquetas[i]] - 1);
                double b = Double.MAX_VALUE;
                for (int grupo = 0; grupo < numeroGrupos; grupo++) {
                    if (grupo != etiquetas[i]) {
                        double promedio = sumaDistancias[grupo] / tamano[grupo];
                        if (promedio < b) {
                            b = promedio;
                        }
                    }
                }
                suma = suma + (b - a) / Math.max(a, b);
            }
        }
        return suma / total;
    }
}
