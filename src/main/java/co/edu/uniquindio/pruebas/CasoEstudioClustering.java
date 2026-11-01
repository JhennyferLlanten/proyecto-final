package co.edu.uniquindio.pruebas;

import co.edu.uniquindio.clustering.Agrupamiento;
import co.edu.uniquindio.clustering.MatrizDistancias;
import co.edu.uniquindio.clustering.Union;
import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.similitud.EjecutorSimilitud;
import co.edu.uniquindio.similitud.ModeloTfIdf;

import java.io.IOException;
import java.util.ArrayList;

public class CasoEstudioClustering {

    public static void main(String[] args) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        ArrayList<ArrayList<String>> palabras = EjecutorSimilitud.obtenerPalabras(articulos);
        ModeloTfIdf modelo = new ModeloTfIdf(palabras);
        double[][] similitud = modelo.calcularMatriz();
        double[][] distancias = MatrizDistancias.calcularMatrizDistancia(similitud);

        System.out.println("===== PASO 1: MATRIZ DE DISTANCIAS =====");
        System.out.println("Tamano de la matriz: " + distancias.length + " x " + distancias[0].length);
        System.out.println();

        System.out.println("Articulo 2 y articulo 20:");
        System.out.println("  similitud (coseno TF-IDF) = " + formato(similitud[1][19]));
        System.out.println("  distancia = 1 - coseno    = " + formato(distancias[1][19]));
        System.out.println();

        double menor = Double.MAX_VALUE;
        double mayor = -1;
        int menorI = 0;
        int menorJ = 0;
        int mayorI = 0;
        int mayorJ = 0;
        boolean simetrica = true;
        boolean diagonalCero = true;
        for (int i = 0; i < distancias.length; i++) {
            if (distancias[i][i] != 0) {
                diagonalCero = false;
            }
            for (int j = 0; j < distancias.length; j++) {
                if (distancias[i][j] != distancias[j][i]) {
                    simetrica = false;
                }
                if (i < j) {
                    if (distancias[i][j] < menor) {
                        menor = distancias[i][j];
                        menorI = i;
                        menorJ = j;
                    }
                    if (distancias[i][j] > mayor) {
                        mayor = distancias[i][j];
                        mayorI = i;
                        mayorJ = j;
                    }
                }
            }
        }

        System.out.println("Distancia menor entre dos articulos: " + formato(menor)
                + " (articulos " + articulos[menorI].numero + " y " + articulos[menorJ].numero + ")");
        System.out.println("Distancia mayor entre dos articulos: " + formato(mayor)
                + " (articulos " + articulos[mayorI].numero + " y " + articulos[mayorJ].numero + ")");
        System.out.println("Matriz simetrica: " + simetrica);
        System.out.println("Diagonal en 0: " + diagonalCero);

        mostrarArbol("SINGLE LINKAGE", Agrupamiento.SINGLE, distancias, articulos);
        mostrarArbol("COMPLETE LINKAGE", Agrupamiento.COMPLETE, distancias, articulos);
        mostrarArbol("AVERAGE LINKAGE", Agrupamiento.AVERAGE, distancias, articulos);
    }

    private static void mostrarArbol(String nombre, int criterio, double[][] distancias, Articulo[] articulos) {
        System.out.println();
        System.out.println("===== ARBOL CON " + nombre + " =====");
        Union[] uniones = Agrupamiento.construirArbol(distancias, criterio);
        System.out.println("Uniones: " + uniones.length);
        boolean alturasOrdenadas = true;
        for (int numeroUnion = 0; numeroUnion < uniones.length; numeroUnion++) {
            Union union = uniones[numeroUnion];
            System.out.println("Union " + (numeroUnion + 1) + ": " + nombreGrupo(union.grupoIzquierdo, articulos)
                    + " + " + nombreGrupo(union.grupoDerecho, articulos)
                    + " | altura = " + formato(union.altura)
                    + " | tamano = " + union.tamano
                    + " | nuevo grupo = G" + (articulos.length + numeroUnion));
            if (numeroUnion > 0 && union.altura < uniones[numeroUnion - 1].altura) {
                alturasOrdenadas = false;
            }
        }
        System.out.println("Alturas de menor a mayor: " + alturasOrdenadas);
    }

    private static String nombreGrupo(int grupo, Articulo[] articulos) {
        if (grupo < articulos.length) {
            return "articulo " + articulos[grupo].numero;
        }
        return "G" + grupo;
    }

    private static String formato(double valor) {
        return String.format("%.4f", valor).replace('.', ',');
    }
}
