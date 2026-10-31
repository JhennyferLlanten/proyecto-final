package co.edu.uniquindio.pruebas;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.procesamientoTexto.BuscadorTexto;
import co.edu.uniquindio.procesamientoTexto.Preprocesador;
import co.edu.uniquindio.similitud.CosenoTfIdf;
import co.edu.uniquindio.similitud.EjecutorSimilitud;
import co.edu.uniquindio.similitud.Jaccard;
import co.edu.uniquindio.similitud.Levenshtein;
import co.edu.uniquindio.similitud.ModeloTfIdf;
import co.edu.uniquindio.similitud.NeedlemanWunsch;
import co.edu.uniquindio.similitud.Vocabulario;

import java.io.IOException;
import java.util.ArrayList;

public class CasoEstudioSimilitud {

    public static void main(String[] args) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        String textoA = primeraFrase(articulos[1].resumen);
        String textoB = primeraFrase(articulos[19].resumen);
        ArrayList<String> a = Preprocesador.obtenerPalabrasCompletas(textoA);
        ArrayList<String> b = Preprocesador.obtenerPalabrasCompletas(textoB);

        System.out.println("Articulo " + articulos[1].numero + ", primera frase: " + textoA);
        System.out.println("Palabras (n = " + a.size() + "): " + a);
        System.out.println("Articulo " + articulos[19].numero + ", primera frase: " + textoB);
        System.out.println("Palabras (m = " + b.size() + "): " + b);

        double jaccard = mostrarJaccard(a, b);
        double coseno = mostrarCoseno(articulos, a, b);
        double levenshtein = mostrarLevenshtein(a, b);
        double needlemanWunsch = mostrarNeedlemanWunsch(a, b);

        System.out.println();
        System.out.println("===== RESUMEN =====");
        System.out.println("Jaccard          = " + formato(jaccard));
        System.out.println("Coseno TF-IDF    = " + formato(coseno));
        System.out.println("Levenshtein      = " + formato(levenshtein));
        System.out.println("Needleman-Wunsch = " + formato(needlemanWunsch));
    }

    private static String primeraFrase(String resumen) {
        int fin = BuscadorTexto.buscarPatron(resumen, ". ", 0);
        return resumen.substring(0, fin + 1);
    }

    private static String formato(double valor) {
        return String.format("%.4f", valor);
    }

    private static double mostrarJaccard(ArrayList<String> a, ArrayList<String> b) {
        System.out.println();
        System.out.println("===== JACCARD =====");
        ArrayList<String> conjuntoA = Vocabulario.distintas(a);
        ArrayList<String> conjuntoB = Vocabulario.distintas(b);
        System.out.println("Conjunto A (" + conjuntoA.size() + " palabras distintas): " + conjuntoA);
        System.out.println("Conjunto B (" + conjuntoB.size() + " palabras distintas): " + conjuntoB);

        ArrayList<String> comunes = new ArrayList<>();
        for (int i = 0; i < conjuntoA.size(); i++) {
            if (Vocabulario.posicion(conjuntoB, conjuntoA.get(i)) != -1) {
                comunes.add(conjuntoA.get(i));
            }
        }
        int cantidadComunes = Jaccard.contarComunes(conjuntoA, conjuntoB);
        int union = conjuntoA.size() + conjuntoB.size() - cantidadComunes;
        System.out.println("Comunes (interseccion = " + cantidadComunes + "): " + comunes);
        System.out.println("Union = " + conjuntoA.size() + " + " + conjuntoB.size() + " - " + cantidadComunes + " = " + union);
        double similitud = Jaccard.calcularSimilitud(cantidadComunes, conjuntoA.size(), conjuntoB.size());
        System.out.println("Jaccard = " + cantidadComunes + " / " + union + " = " + formato(similitud));
        return similitud;
    }

    private static double mostrarCoseno(Articulo[] articulos, ArrayList<String> a, ArrayList<String> b) {
        System.out.println();
        System.out.println("===== COSENO CON TF-IDF =====");
        ArrayList<ArrayList<String>> corpus = EjecutorSimilitud.obtenerPalabras(articulos);
        ModeloTfIdf modelo = new ModeloTfIdf(corpus);
        System.out.println("El idf se calcula con los " + corpus.size() + " abstracts completos: idf = ln(" + corpus.size() + " / df)");

        ArrayList<String> palabras = Vocabulario.construir(listaDe(a, b));
        double[] u = CosenoTfIdf.calcularVector(a, modelo.vocabulario, modelo.idf);
        double[] v = CosenoTfIdf.calcularVector(b, modelo.vocabulario, modelo.idf);
        double productoPunto = 0;
        double sumaU = 0;
        double sumaV = 0;
        System.out.println(String.format("%-16s %4s %4s %4s %8s %8s %8s", "palabra", "tfA", "tfB", "df", "idf", "pesoA", "pesoB"));
        for (int i = 0; i < palabras.size(); i++) {
            String palabra = palabras.get(i);
            int k = Vocabulario.posicion(modelo.vocabulario, palabra);
            int tfA = contar(a, palabra);
            int tfB = contar(b, palabra);
            int df = CosenoTfIdf.contarDocumentos(corpus, palabra);
            System.out.println(String.format("%-16s %4d %4d %4d %8.4f %8.4f %8.4f", palabra, tfA, tfB, df, modelo.idf[k], u[k], v[k]));
            productoPunto = productoPunto + u[k] * v[k];
            sumaU = sumaU + u[k] * u[k];
            sumaV = sumaV + v[k] * v[k];
        }
        System.out.println("Producto punto = " + formato(productoPunto));
        System.out.println("Norma A = " + formato(Math.sqrt(sumaU)) + "   Norma B = " + formato(Math.sqrt(sumaV)));
        double similitud = CosenoTfIdf.calcularCoseno(u, v);
        System.out.println("Coseno = " + formato(productoPunto) + " / (" + formato(Math.sqrt(sumaU)) + " * " + formato(Math.sqrt(sumaV)) + ") = " + formato(similitud));
        return similitud;
    }

    private static ArrayList<ArrayList<String>> listaDe(ArrayList<String> a, ArrayList<String> b) {
        ArrayList<ArrayList<String>> lista = new ArrayList<>();
        lista.add(a);
        lista.add(b);
        return lista;
    }

    private static int contar(ArrayList<String> palabras, String palabra) {
        int cantidad = 0;
        for (int i = 0; i < palabras.size(); i++) {
            if (palabras.get(i).equals(palabra)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private static void mostrarTabla(int[][] tabla, ArrayList<String> a, ArrayList<String> b) {
        StringBuilder encabezado = new StringBuilder(String.format("%-14s", ""));
        encabezado.append(String.format("%4s", "-"));
        for (int j = 0; j < b.size(); j++) {
            encabezado.append(String.format("%4d", j + 1));
        }
        System.out.println(encabezado);
        for (int i = 0; i <= a.size(); i++) {
            String etiqueta = "-";
            if (i > 0) {
                etiqueta = (i) + " " + a.get(i - 1);
            }
            StringBuilder fila = new StringBuilder(String.format("%-14s", etiqueta));
            for (int j = 0; j <= b.size(); j++) {
                fila.append(String.format("%4d", tabla[i][j]));
            }
            System.out.println(fila);
        }
    }

    private static void mostrarColumnas(ArrayList<String> b) {
        System.out.print("Columnas:");
        for (int j = 0; j < b.size(); j++) {
            System.out.print("  " + (j + 1) + "=" + b.get(j));
        }
        System.out.println();
    }

    private static double mostrarLevenshtein(ArrayList<String> a, ArrayList<String> b) {
        System.out.println();
        System.out.println("===== LEVENSHTEIN POR PALABRAS =====");
        System.out.println("D[i][0] = i, D[0][j] = j. Si las palabras son iguales: D[i][j] = D[i-1][j-1]. Si son distintas: D[i][j] = 1 + min(D[i-1][j], D[i][j-1], D[i-1][j-1])");
        mostrarColumnas(b);
        int[][] tabla = Levenshtein.construirTabla(a, b);
        mostrarTabla(tabla, a, b);
        int distancia = tabla[a.size()][b.size()];
        double similitud = Levenshtein.calcularSimilitud(distancia, a.size(), b.size());
        System.out.println("Distancia = " + distancia + ", similitud = 1 - " + distancia + " / " + Math.max(a.size(), b.size()) + " = " + formato(similitud));
        return similitud;
    }

    private static double mostrarNeedlemanWunsch(ArrayList<String> a, ArrayList<String> b) {
        System.out.println();
        System.out.println("===== NEEDLEMAN-WUNSCH POR PALABRAS =====");
        System.out.println("Coincidencia = " + NeedlemanWunsch.COINCIDENCIA + ", diferencia = " + NeedlemanWunsch.DIFERENCIA + ", hueco = " + NeedlemanWunsch.HUECO);
        System.out.println("F[i][0] = i * hueco, F[0][j] = j * hueco, F[i][j] = max(F[i-1][j-1] + s, F[i-1][j] + hueco, F[i][j-1] + hueco)");
        mostrarColumnas(b);
        int[][] tabla = NeedlemanWunsch.construirTabla(a, b);
        mostrarTabla(tabla, a, b);
        int puntaje = tabla[a.size()][b.size()];
        double similitud = NeedlemanWunsch.calcularSimilitud(puntaje, a.size(), b.size());
        int menor = Math.min(a.size(), b.size());
        int mayor = Math.max(a.size(), b.size());
        System.out.println("Puntaje = " + puntaje + ", similitud = (" + puntaje + " + " + mayor + ") / (2 * " + mayor + ") = " + formato(similitud));
        return similitud;
    }
}
