package co.edu.uniquindio.similitud;

import java.util.ArrayList;

public class Jaccard {

    public static int contarComunes(ArrayList<String> conjuntoA, ArrayList<String> conjuntoB) {
        int comunes = 0;                                                                                        //realiza la intersección
        for (int i = 0; i < conjuntoA.size(); i++) {
            for (int j = 0; j < conjuntoB.size(); j++) {
                if (conjuntoA.get(i).equals(conjuntoB.get(j))) {
                    comunes++;
                }
            }
        }
        return comunes;
    }

    public static double calcularSimilitud(int comunes, int tamanoA, int tamanoB) {
        int union = tamanoA + tamanoB - comunes;                                                                //realiza la union de los conjuntos
        if (union == 0) {
            return 1.0;
        }
        return (double) comunes / union;                                                                        //indice Jaccard
    }
}
