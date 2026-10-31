package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class Levenshtein {

    public static int[][] construirTabla(ArrayList<String> a, ArrayList<String> b) {
        int n = a.size();
        int m = b.size();
        int[][] tabla = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {                                              //primera columna
            tabla[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {                                              //primer fila
            tabla[0][j] = j;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.get(i - 1).equals(b.get(j - 1))) {                            //si son iguales copia la diagonal
                    tabla[i][j] = tabla[i - 1][j - 1];
                } else {
                    int arriba = tabla[i - 1][j] + 1;
                    int izquierda = tabla[i][j - 1] + 1;
                    int diagonal = tabla[i - 1][j - 1] + 1;
                    tabla[i][j] = Math.min(arriba, Math.min(izquierda, diagonal));
                }
            }
        }
        return tabla;
    }

    public static double calcularSimilitud(int distancia, int n, int m) {
        int mayor = Math.max(n, m);
        if (mayor == 0) {
            return 1.0;
        }
        return 1.0 - (double) distancia / mayor;
    }
}
