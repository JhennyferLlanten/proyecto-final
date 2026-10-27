package co.edu.uniquindio.modelo;

public class TablaFrecuencias {

    public String[] palabras;
    public int[] frecuencias;
    public int cantidad;                    //cuantas palabras distintas hay

    public TablaFrecuencias(String[] palabras, int[] frecuencias, int cantidad) {
        this.palabras = palabras;
        this.frecuencias = frecuencias;
        this.cantidad = cantidad;
    }

    public int frecuenciaDe(String palabra) {
        for (int i = 0; i < cantidad; i++) {
            if (palabras[i].equals(palabra)) {
                return frecuencias[i];                     // retorna la frecuencia de una palabra
            }
        }
        return 0;
    }
}
