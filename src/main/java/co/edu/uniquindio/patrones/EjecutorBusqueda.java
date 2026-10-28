package co.edu.uniquindio.patrones;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.procesamientoTexto.BuscadorTexto;

import java.util.ArrayList;

public class EjecutorBusqueda {

    public static String[] obtenerTextosEnMinuscula(Articulo[] articulos) {
        String[] textos = new String[articulos.length];
        for (int i = 0; i < articulos.length; i++) {
            textos[i] = BuscadorTexto.textoEnMinuscula(articulos[i].resumen);
        }
        return textos;
    }

    public static ResultadoAlgoritmo ejecutarKMP(String[] textos, String patron) {
        ResultadoAlgoritmo resultado = new ResultadoAlgoritmo("KMP", textos.length);

        long inicio = System.nanoTime();
        int[] tabla = KMP.calcularTablaPrefijos(patron);
        long fin = System.nanoTime();
        resultado.milisegundosPreprocesamiento = (fin - inicio) / 1000000.0;

        inicio = System.nanoTime();
        for (int i = 0; i < textos.length; i++) {
            resultado.porArticulo[i] = KMP.buscar(textos[i], patron, tabla);
        }
        fin = System.nanoTime();
        resultado.milisegundosBusqueda = (fin - inicio) / 1000000.0;

        resultado.sumarTotales();
        return resultado;
    }

    public static ResultadoAlgoritmo ejecutarBoyerMoore(String[] textos, String patron) {
        ResultadoAlgoritmo resultado = new ResultadoAlgoritmo("Boyer-Moore", textos.length);

        long inicio = System.nanoTime();
        int[] tabla = BoyerMoore.calcularTablaCaracterMalo(patron);
        long fin = System.nanoTime();
        resultado.milisegundosPreprocesamiento = (fin - inicio) / 1000000.0;

        inicio = System.nanoTime();
        for (int i = 0; i < textos.length; i++) {
            resultado.porArticulo[i] = BoyerMoore.buscar(textos[i], patron, tabla);
        }
        fin = System.nanoTime();
        resultado.milisegundosBusqueda = (fin - inicio) / 1000000.0;

        resultado.sumarTotales();
        return resultado;
    }

    public static ResultadoAlgoritmo ejecutarRabinKarp(String[] textos, String patron) {
        ResultadoAlgoritmo resultado = new ResultadoAlgoritmo("Rabin-Karp", textos.length);

        long inicio = System.nanoTime();
        long hashPatron = RabinKarp.calcularHash(patron, patron.length());
        long potencia = RabinKarp.calcularPotencia(patron.length());
        long fin = System.nanoTime();
        resultado.milisegundosPreprocesamiento = (fin - inicio) / 1000000.0;

        inicio = System.nanoTime();
        for (int i = 0; i < textos.length; i++) {
            resultado.porArticulo[i] = RabinKarp.buscar(textos[i], patron, hashPatron, potencia);
        }
        fin = System.nanoTime();
        resultado.milisegundosBusqueda = (fin - inicio) / 1000000.0;

        resultado.sumarTotales();
        return resultado;
    }

    public static ArrayList<FilaArticulo> armarFilas(Articulo[] articulos, ResultadoAlgoritmo kmp, ResultadoAlgoritmo boyerMoore, ResultadoAlgoritmo rabinKarp) {
        ArrayList<FilaArticulo> filas = new ArrayList<>();
        for (int i = 0; i < articulos.length; i++) {
            int ocurrenciasKMP = kmp.porArticulo[i].posiciones.size();
            int ocurrenciasBoyerMoore = boyerMoore.porArticulo[i].posiciones.size();
            int ocurrenciasRabinKarp = rabinKarp.porArticulo[i].posiciones.size();
            if (ocurrenciasKMP > 0 || ocurrenciasBoyerMoore > 0 || ocurrenciasRabinKarp > 0) {
                filas.add(new FilaArticulo(articulos[i].numero, articulos[i].titulo, ocurrenciasKMP, ocurrenciasBoyerMoore, ocurrenciasRabinKarp, kmp.porArticulo[i].posiciones));
            }
        }
        return filas;
    }
}
