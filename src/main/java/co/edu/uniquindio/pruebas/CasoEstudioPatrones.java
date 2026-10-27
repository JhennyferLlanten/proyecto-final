package co.edu.uniquindio.pruebas;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.patrones.BoyerMoore;
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
        mostrarBoyerMoore(texto, patron);
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

    private static void mostrarBoyerMoore(String texto, String patron) {
        System.out.println();
        System.out.println("===== BOYER-MOORE (caracter malo) =====");
        int[] tabla = BoyerMoore.calcularTablaCaracterMalo(patron);
        System.out.println("Tabla de caracter malo (ultima posicion de cada letra del patron; las demas valen -1):");
        for (int i = 0; i < patron.length(); i++) {
            char c = patron.charAt(i);
            if (tabla[c] == i) {
                System.out.println("  '" + c + "' = " + tabla[c]);
            }
        }

        System.out.println("Iteraciones:");
        int n = texto.length();
        int m = patron.length();
        int desplazamiento = 0;
        int comparaciones = 0;
        int iteracion = 1;
        while (desplazamiento <= n - m) {
            int j = m - 1;
            boolean coincide = true;
            while (j >= 0 && coincide) {
                comparaciones++;
                if (patron.charAt(j) == texto.charAt(desplazamiento + j)) {
                    j--;
                } else {
                    coincide = false;
                }
            }
            String ventana = texto.substring(desplazamiento, desplazamiento + m);
            if (j < 0) {
                int salto = 1;
                if (desplazamiento + m < n) {
                    salto = m - tabla[texto.charAt(desplazamiento + m)];
                }
                System.out.println("  " + iteracion + ") s = " + desplazamiento + "  [" + ventana + "]  COINCIDENCIA; avanza " + salto);
                desplazamiento = desplazamiento + salto;
            } else {
                char malo = texto.charAt(desplazamiento + j);
                int salto = Math.max(1, j - tabla[malo]);
                System.out.println("  " + iteracion + ") s = " + desplazamiento + "  [" + ventana + "]  falla en j = " + j + " con '" + malo + "', tabla = " + tabla[malo] + "; avanza max(1, " + j + " - (" + tabla[malo] + ")) = " + salto);
                desplazamiento = desplazamiento + salto;
            }
            iteracion++;
        }
        System.out.println("Comparaciones: " + comparaciones);
    }
}
