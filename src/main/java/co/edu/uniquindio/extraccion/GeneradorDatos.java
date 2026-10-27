package co.edu.uniquindio.extraccion;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class GeneradorDatos {

    public static void main(String[] args) throws IOException {
        File[] archivos = new File("articulosProyecto").listFiles();                    //lista de archivos

        File[] numeracion = new File[archivos.length + 1];                                       //crea lista de la enumeración para guardar archivos en orden
        for (int i = 0; i < archivos.length; i++) {
            int numero = ExtractorArticulos.obtenerNumero(archivos[i].getName());
            numeracion[numero] = archivos[i];
        }

        PrintWriter escritor = new PrintWriter("src/main/resources/articulos.txt", "UTF-8");  //reescribe el archivo articulos.txt donde se van a guardar los datos
        int total = 0;
        for (int numero = 1; numero <= archivos.length; numero++) {
            String ruta = numeracion[numero].getPath();
            String titulo = ExtractorArticulos.obtenerTitulo(LectorPdf.leerTitulo(ruta), numeracion[numero].getName());
            String resumen = ExtractorArticulos.corregirPalabrasPartidas(ExtractorArticulos.extraerAbstract(LectorPdf.leerPrimeraPagina(ruta)));

            if (resumen.length() == 0) {
                System.out.println("ATENCION: no se pudo extraer el abstract del articulo " + numero);
            }
            escritor.println(numero + "\t" + titulo + "\t" + resumen);
            total++;
        }
        escritor.close();
        System.out.println("Listo: se guardaron " + total + " articulos en src/main/resources/articulos.txt");
    }
}
