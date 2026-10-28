package co.edu.uniquindio.patrones;

import java.util.ArrayList;

public class FilaArticulo {

    public int numero;
    public String titulo;
    public int ocurrenciasKMP;
    public int ocurrenciasBoyerMoore;
    public int ocurrenciasRabinKarp;
    public ArrayList<Integer> posiciones;

    public FilaArticulo(int numero, String titulo, int ocurrenciasKMP, int ocurrenciasBoyerMoore, int ocurrenciasRabinKarp, ArrayList<Integer> posiciones) {
        this.numero = numero;
        this.titulo = titulo;
        this.ocurrenciasKMP = ocurrenciasKMP;
        this.ocurrenciasBoyerMoore = ocurrenciasBoyerMoore;
        this.ocurrenciasRabinKarp = ocurrenciasRabinKarp;
        this.posiciones = posiciones;
    }
}
