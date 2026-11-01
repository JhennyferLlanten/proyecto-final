package co.edu.uniquindio.clustering;

public class Agrupamiento {

    public static final int SINGLE = 0;
    public static final int COMPLETE = 1;
    public static final int AVERAGE = 2;

    public static Union[] construirArbol(double[][] distancias, int criterio) {
        int total = distancias.length;
        double[][] actuales = new double[total][total];
        boolean[] activo = new boolean[total];
        int[] numeroGrupo = new int[total];
        int[] tamano = new int[total];
        for (int i = 0; i < total; i++) {
            for (int j = 0; j < total; j++) {
                actuales[i][j] = distancias[i][j];                                      //copia la matriz
            }
            numeroGrupo[i] = i;                                                         //el número que identifica a cada grupo
            activo[i] = true;                                                           //si el grupo sigue existiendo
            tamano[i] = 1;                                                              //cuantos abstracts tiene
        }

        Union[] uniones = new Union[total - 1];
        for (int numeroUnion = 0; numeroUnion < total - 1; numeroUnion++) {
            int mejorI = -1;                                                           //la posición del grupo izquierdo del par mas cercano
            int mejorJ = -1;                                                           //la posición del grupo derecho del par mas cercano
            double menor = Double.MAX_VALUE;
            for (int i = 0; i < total; i++) {                                          //busca el grupo activo mas cercano
                if (activo[i]) {
                    for (int j = i + 1; j < total; j++) {
                        if (activo[j] && actuales[i][j] < menor) {                     //mejor distancia
                            menor = actuales[i][j];
                            mejorI = i;
                            mejorJ = j;
                        }
                    }
                }
            }

            int tamanoNuevo = tamano[mejorI] + tamano[mejorJ];
            uniones[numeroUnion] = new Union(numeroGrupo[mejorI], numeroGrupo[mejorJ], menor, tamanoNuevo);   //guarda la nueva union

            for (int k = 0; k < total; k++) {                                                   //actualiza las distancias
                if (activo[k] && k != mejorI && k != mejorJ) {
                    double nueva = actualizarDistancia(criterio, actuales[mejorI][k], actuales[mejorJ][k], tamano[mejorI], tamano[mejorJ]);          //calcula la distancia nueva con el criterio
                    actuales[mejorI][k] = nueva;                                                //guarda el nuevo grupo
                    actuales[k][mejorI] = nueva;
                }
            }
            activo[mejorJ] = false;                                                             //se desactiva el grupo derecha
            numeroGrupo[mejorI] = total + numeroUnion;
            tamano[mejorI] = tamanoNuevo;
        }
        return uniones;                                                                         //Devuelve el arbol
    }

    private static double actualizarDistancia(int criterio, double distanciaI, double distanciaJ, int tamanoI, int tamanoJ) {
        if (criterio == SINGLE) {
            return Math.min(distanciaI, distanciaJ);
        }
        if (criterio == COMPLETE) {
            return Math.max(distanciaI, distanciaJ);
        }
        return (tamanoI * distanciaI + tamanoJ * distanciaJ) / (tamanoI + tamanoJ);
    }
}
