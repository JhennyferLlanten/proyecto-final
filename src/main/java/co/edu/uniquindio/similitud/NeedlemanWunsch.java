package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class NeedlemanWunsch {

    public static final int COINCIDENCIA = 1;
    public static final int DIFERENCIA = -1;
    public static final int HUECO = -1;

    private static int puntajeEntre(String palabraA, String palabraB) {
        if (palabraA.equals(palabraB)) {                            //compara y devuelve el puntaje
            return COINCIDENCIA;
        }
        return DIFERENCIA;
    }

    public static int[][] construirTabla(ArrayList<String> a, ArrayList<String> b) {
        int n = a.size();
        int m = b.size();
        int[][] tabla = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            tabla[i][0] = i * HUECO;
        }
        for (int j = 0; j <= m; j++) {
            tabla[0][j] = j * HUECO;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int diagonal = tabla[i - 1][j - 1] + puntajeEntre(a.get(i - 1), b.get(j - 1));  //primero compara la diagonal
                int arriba = tabla[i - 1][j] + HUECO;
                int izquierda = tabla[i][j - 1] + HUECO;
                tabla[i][j] = Math.max(diagonal, Math.max(arriba, izquierda));
            }
        }
        return tabla;
    }

    public static double calcularSimilitud(int puntaje, int n, int m) {
        int menor = Math.min(n, m);
        int mayor = Math.max(n, m);
        if (menor == 0) {
            if (mayor == 0) {
                return 1.0;
            }
            return 0.0;
        }
        return (double) (puntaje + mayor) / (2 * mayor);
    }
}
