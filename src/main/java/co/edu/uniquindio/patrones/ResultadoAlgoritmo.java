package co.edu.uniquindio.patrones;

public class ResultadoAlgoritmo {

    public String nombre;
    public double milisegundosPreprocesamiento;
    public double milisegundosBusqueda;
    public int totalOcurrencias;
    public long totalComparaciones;
    public int totalColisiones;
    public ResultadoBusqueda[] porArticulo;

    public ResultadoAlgoritmo(String nombre, int cantidadArticulos) {
        this.nombre = nombre;
        this.porArticulo = new ResultadoBusqueda[cantidadArticulos];
    }

    public void sumarTotales() {
        for (int i = 0; i < porArticulo.length; i++) {
            totalOcurrencias = totalOcurrencias + porArticulo[i].posiciones.size();
            totalComparaciones = totalComparaciones + porArticulo[i].comparaciones;
            totalColisiones = totalColisiones + porArticulo[i].colisiones;
        }
    }
}
