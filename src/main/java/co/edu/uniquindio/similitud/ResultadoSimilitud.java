package co.edu.uniquindio.similitud;

public class ResultadoSimilitud {

    public String nombre;
    public double similitud;
    public String detalle;
    public double milisegundosPreprocesamiento;
    public double milisegundosCalculo;

    public ResultadoSimilitud(String nombre) {
        this.nombre = nombre;
    }
}
