package co.edu.uniquindio.pruebas;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.patrones.KMP;
import co.edu.uniquindio.procesamientoTexto.BuscadorTexto;

import java.io.IOException;

public class CasoEstudioPatrones {

    public static void main(String[] args) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        Articulo articulo = articulos[10];
        int finPrimeraFrase = BuscadorTexto.buscarPatron(articulo.resumen, ". ", 0);
        int finSegundaFrase = BuscadorTexto.buscarPatron(articulo.resumen, ". ", finPrimeraFrase + 1);
        String texto = BuscadorTexto.textoEnMinuscula(articulo.resumen.substring(0, finSegundaFrase + 1));
        String patron = "intelligence";

        System.out.println("Articulo " + articulo.numero + ": " + articulo.titulo);
        System.out.println("Texto (n = " + texto.length() + "): " + texto);
        System.out.println("Patron (m = " + patron.length() + "): " + patron);

        mostrarKMP(texto, patron);
    }

    private static void mostrarKMP(String texto, String patron) {
        System.out.println();
        System.out.println("===== KMP =====");
        int[] tabla = KMP.calcularTablaPrefijos(patron);
        System.out.println("Tabla de prefijos:");
        for (int i = 0; i < patron.length(); i++) {
            System.out.println("  i = " + i + "  '" + patron.charAt(i) + "'  tabla = " + tabla[i]);
        }

        System.out.println("Recorrido (solo los saltos y las coincidencias):");
        int i = 0;
        int j = 0;
        int comparaciones = 0;
        while (i < texto.length()) {
            comparaciones++;
            if (texto.charAt(i) == patron.charAt(j)) {
                i++;
                j++;
                if (j == patron.length()) {
                    System.out.println("  Coincidencia en la posicion " + (i - j) + "; j pasa de " + j + " a " + tabla[j - 1]);
                    j = tabla[j - 1];
                }
            } else if (j > 0) {
                System.out.println("  Falla en texto[" + i + "] = '" + texto.charAt(i) + "' con " + j + " letras iguales; j pasa a tabla[" + (j - 1) + "] = " + tabla[j - 1] );
                j = tabla[j - 1];
            } else {
                i++;
            }
        }
        System.out.println("Comparaciones: " + comparaciones);
    }
}
