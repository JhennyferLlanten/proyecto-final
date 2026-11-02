package co.edu.uniquindio.clustering;

import java.util.ArrayList;

public class ResultadoCriterio {
                                                                                //Resultado del criterio por cada k
    public String nombre;
    public Union[] uniones;
    public double[] siluetas;
    public int mejorGrupos;
    public double mejorSilueta;
    public ArrayList<String> gruposMejorCorte;

    public ResultadoCriterio(String nombre, Union[] uniones, double[] siluetas) {
        this.nombre = nombre;
        this.uniones = uniones;
        this.siluetas = siluetas;
        this.gruposMejorCorte = new ArrayList<>();
    }
}
