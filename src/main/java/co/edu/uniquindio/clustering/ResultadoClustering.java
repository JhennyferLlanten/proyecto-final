package co.edu.uniquindio.clustering;

public class ResultadoClustering {
                                                                                //ganador de los 3 criterios
    public ResultadoCriterio[] criterios;
    public int posicionGanador;

    public ResultadoClustering(ResultadoCriterio[] criterios, int posicionGanador) {
        this.criterios = criterios;
        this.posicionGanador = posicionGanador;
    }

    public ResultadoCriterio getGanador() {
        return criterios[posicionGanador];
    }
}
