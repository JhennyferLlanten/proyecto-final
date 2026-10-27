package co.edu.uniquindio.patrones;

public class KMP {

    public static int[] calcularTablaPrefijos(String patron) {
        int tamanoPatron = patron.length();
        int[] tabla = new int[tamanoPatron];
        int longitud = 0;                                               //Letras que van coincidiendo
        int i = 1;                                                      //la posición del patrón que se está calculando.
        while (i < tamanoPatron) {
            if (patron.charAt(i) == patron.charAt(longitud)) {
                longitud++;
                tabla[i] = longitud;
                i++;
            } else if (longitud > 0) {                                  //La coincidencia se rompe
                longitud = tabla[longitud - 1];
            } else {                                                    //Cuando las letras son distintas y longitud ya es 0
                tabla[i] = 0;
                i++;
            }
        }
        return tabla;
    }

    public static ResultadoBusqueda buscar(String texto, String patron, int[] tabla) {
        ResultadoBusqueda resultado = new ResultadoBusqueda();
        int tamanoTexto = texto.length();
        int tamanoPatron = patron.length();
        int i = 0;
        int j = 0;
        while (i < tamanoTexto) {
            resultado.comparaciones++;
            if (texto.charAt(i) == patron.charAt(j)) {                  //compara
                i++;
                j++;
                if (j == tamanoPatron) {                                 //coincide el patron y guarda posicion inicial
                    resultado.posiciones.add(i - j);
                    j = tabla[j - 1];                                    //sigue buscando
                }
            } else if (j > 0) {                                         //cuando deja de coincidir
                j = tabla[j - 1];                                       //conserva la posición actuar para seguir la busqueda
            } else {
                i++;
            }
        }
        return resultado;
    }
}
