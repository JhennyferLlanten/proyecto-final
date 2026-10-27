package co.edu.uniquindio.patrones;

public class BoyerMoore {

    public static final int TAMANO_ALFABETO = 65536;

    public static int[] calcularTablaCaracterMalo(String patron) {
        int[] tabla = new int[TAMANO_ALFABETO];                                                    //una casilla por cada carácter
        for (int c = 0; c < TAMANO_ALFABETO; c++) {
            tabla[c] = -1;                                                                         //-1: la letra no está en el patrón
        }
        for (int i = 0; i < patron.length(); i++) {
            tabla[patron.charAt(i)] = i;                                                           //guarda la última posición de cada letra del patrón
        }
        return tabla;
    }

    public static ResultadoBusqueda buscar(String texto, String patron, int[] tabla) {
        ResultadoBusqueda resultado = new ResultadoBusqueda();
        int tamanoTexto = texto.length();
        int tamanoPatron = patron.length();
        int desplazamiento = 0;                                                                                             //posición del texto donde está colocado el inicio del patrón
        while (desplazamiento <= tamanoTexto - tamanoPatron) {                                                               //mientras el patrón quepa en lo que queda del texto
            int j = tamanoPatron - 1;                                                                                        //empieza por la última letra del patrón
            boolean coincide = true;
            while (j >= 0 && coincide) {                                                                                    //compara de derecha a izquierda
                resultado.comparaciones++;
                if (patron.charAt(j) == texto.charAt(desplazamiento + j)) {                                                 //coincide: pasa a la letra anterior
                    j--;
                } else {
                    coincide = false;                                                                                       //deja de comparar
                }
            }
            if (j < 0) {                                                                                                   //j = -1: coincidieron
                resultado.posiciones.add(desplazamiento);                                                                  //guarda dónde empieza la coincidencia
                if (desplazamiento + tamanoPatron < tamanoTexto) {                                                         //queda texto después de la coincidencia
                    desplazamiento = desplazamiento + tamanoPatron - tabla[texto.charAt(desplazamiento + tamanoPatron)];    //salto según esa letra
                } else {
                    desplazamiento = desplazamiento + 1;                                                                    //no hay letra siguiente: avanza 1
                }
            } else {
                desplazamiento = desplazamiento + Math.max(1, j - tabla[texto.charAt(desplazamiento + j)]);             //corre el patrón hasta alinear la letra mala con su última aparición; mínimo 1 para no retroceder
            }
        }
        return resultado;
    }
}
