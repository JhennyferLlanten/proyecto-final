package co.edu.uniquindio.pruebas;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.procesamientoTexto.BuscadorTexto;
import co.edu.uniquindio.procesamientoTexto.Preprocesador;
import co.edu.uniquindio.similitud.CosenoTfIdf;
import co.edu.uniquindio.similitud.DistanciaEuclidiana;
import co.edu.uniquindio.similitud.EjecutorSimilitud;
import co.edu.uniquindio.similitud.Jaccard;
import co.edu.uniquindio.similitud.Levenshtein;
import co.edu.uniquindio.similitud.ModeloTfIdf;
import co.edu.uniquindio.similitud.ModeloWord2Vec;
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
        ModeloWord2Vec modeloWord2Vec = new ModeloWord2Vec(listaDe(a, b));
        double word2vec = mostrarWord2Vec(a, b, modeloWord2Vec);
        double word2vecEuclidiana = mostrarWord2VecEuclidiana(modeloWord2Vec);

        System.out.println();
        System.out.println("===== RESUMEN =====");
        System.out.println("Jaccard          = " + formato(jaccard));
        System.out.println("Coseno TF-IDF    = " + formato(coseno));
        System.out.println("Levenshtein      = " + formato(levenshtein));
        System.out.println("Needleman-Wunsch = " + formato(needlemanWunsch));
        System.out.println("Word2Vec coseno  = " + formato(word2vec));
        System.out.println("Word2Vec euclid. = " + formato(word2vecEuclidiana));
    }

    private static String primeraFrase(String resumen) {
        int fin = BuscadorTexto.buscarPatron(resumen, ". ", 0);
        return resumen.substring(0, fin + 1);
    }

    private static void mostrarAngulo(double coseno) {
        double angulo = Math.toDegrees(Math.acos(coseno));
        System.out.println("Angulo = arccos(" + formato(coseno) + ") = " + String.format("%.2f", angulo) + " grados");
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
        mostrarAngulo(similitud);
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

    private static void mostrarPalabrasSinVector(String nombre, ArrayList<String> palabras, ModeloWord2Vec modelo) {
        ArrayList<String> sinVector = new ArrayList<>();
        for (int i = 0; i < palabras.size(); i++) {
            if (Vocabulario.posicion(modelo.vocabulario, palabras.get(i)) == -1) {
                sinVector.add(palabras.get(i));
            }
        }
        System.out.println("Texto " + nombre + ": " + (palabras.size() - sinVector.size()) + " de " + palabras.size() + " palabras tienen vector. Sin vector: " + sinVector);
    }

    private static void mostrarPrimerasPosiciones(String nombre, double[] vector) {
        StringBuilder texto = new StringBuilder("Promedio " + nombre + " (primeras 5 de " + vector.length + " posiciones):");
        for (int k = 0; k < 5; k++) {
            texto.append(String.format("  %.4f", vector[k]));
        }
        System.out.println(texto);
    }

    private static double mostrarWord2Vec(ArrayList<String> a, ArrayList<String> b, ModeloWord2Vec modelo) {
        System.out.println();
        System.out.println("===== WORD2VEC (PROMEDIO Y COSENO) =====");
        System.out.println("El modelo tiene un vector de " + ModeloWord2Vec.DIMENSION + " numeros por cada una de las " + modelo.vocabulario.size() + " palabras del corpus que Word2Vec conoce");
        mostrarPalabrasSinVector("A", a, modelo);
        mostrarPalabrasSinVector("B", b, modelo);
        double[] u = modelo.vectores[0];
        double[] v = modelo.vectores[1];
        mostrarPrimerasPosiciones("A", u);
        mostrarPrimerasPosiciones("B", v);
        double productoPunto = 0;
        double sumaU = 0;
        double sumaV = 0;
        for (int k = 0; k < u.length; k++) {
            productoPunto = productoPunto + u[k] * v[k];
            sumaU = sumaU + u[k] * u[k];
            sumaV = sumaV + v[k] * v[k];
        }
        System.out.println("Producto punto = " + formato(productoPunto));
        System.out.println("Norma A = " + formato(Math.sqrt(sumaU)) + "   Norma B = " + formato(Math.sqrt(sumaV)));
        double similitud = CosenoTfIdf.calcularCoseno(u, v);
        System.out.println("Coseno = " + formato(productoPunto) + " / (" + formato(Math.sqrt(sumaU)) + " * " + formato(Math.sqrt(sumaV)) + ") = " + formato(similitud));
        mostrarAngulo(similitud);
        return similitud;
    }

    private static double mostrarWord2VecEuclidiana(ModeloWord2Vec modelo) {
        System.out.println();
        System.out.println("===== WORD2VEC (PROMEDIO Y DISTANCIA EUCLIDIANA) =====");
        double[] u = modelo.vectores[0];
        double[] v = modelo.vectores[1];
        double suma = 0;
        for (int k = 0; k < 3; k++) {
            double diferencia = u[k] - v[k];
            System.out.println("Posicion " + (k + 1) + ": (" + formato(u[k]) + " - " + formato(v[k]) + ")^2 = " + formato(diferencia * diferencia));
        }
        for (int k = 0; k < u.length; k++) {
            suma = suma + (u[k] - v[k]) * (u[k] - v[k]);
        }
        double distancia = DistanciaEuclidiana.calcularDistancia(u, v);
        double similitud = DistanciaEuclidiana.calcularSimilitud(distancia);
        System.out.println("Suma de las " + u.length + " posiciones = " + formato(suma));
        System.out.println("Distancia = raiz de " + formato(suma) + " = " + formato(distancia));
        System.out.println("Similitud = 1 / (1 + " + formato(distancia) + ") = " + formato(similitud));
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
