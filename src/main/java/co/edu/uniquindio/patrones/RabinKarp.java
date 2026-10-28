package co.edu.uniquindio.patrones;

public class RabinKarp {

    public static final int BASE = 256;
    public static final int PRIMO = 101;

    public static long calcularHash(String cadena, int tamano) {                                            //Calcula el hash
        long hash = 0;
        for (int i = 0; i < tamano; i++) {
            hash = (BASE * hash + cadena.charAt(i)) % PRIMO;
        }
        return hash;
    }

    public static long calcularPotencia(int tamanoPatron) {                                                   //el peso de la primera letra de una ventana
        long potencia = 1;
        for (int i = 0; i < tamanoPatron - 1; i++) {
            potencia = (potencia * BASE) % PRIMO;
        }
        return potencia;
    }

    public static ResultadoBusqueda buscar(String texto, String patron, long hashPatron, long potencia) {
        ResultadoBusqueda resultado = new ResultadoBusqueda();
        int tamanoTexto = texto.length();
        int tamanoPatron = patron.length();
        if (tamanoTexto < tamanoPatron) {                                                                           //Si el texto es menor al patrón no puede haber coincidencia
            return resultado;
        }
        long hashVentana = calcularHash(texto, tamanoPatron);                                                       //El hash de la primera ventana (las primeras m letras del texto).
        for (int desplazamiento = 0; desplazamiento <= tamanoTexto - tamanoPatron; desplazamiento++) {              //mueve la ventana por el texto
            if (hashVentana == hashPatron) {
                int j = 0;
                boolean coincide = true;
                while (j < tamanoPatron && coincide) {
                    resultado.comparaciones++;
                    if (texto.charAt(desplazamiento + j) == patron.charAt(j)) {                                     //compara letra por letra
                        j++;
                    } else {
                        coincide = false;
                    }
                }
                if (coincide) {
                    resultado.posiciones.add(desplazamiento);                                                       //si todas coinciden guarda la posicion
                } else {
                    resultado.colisiones++;
                }
            }
            if (desplazamiento < tamanoTexto - tamanoPatron) {                                                       //Calcula el hash de la ventana siguiente a partir del actual
                hashVentana = (BASE * (hashVentana - texto.charAt(desplazamiento) * potencia) + texto.charAt(desplazamiento + tamanoPatron)) % PRIMO;
                if (hashVentana < 0) {
                    hashVentana = hashVentana + PRIMO;                                                               //Se le suma primo para que el hash siempre este entre 0 y 100.
                }
            }
        }
        return resultado;
    }
}
