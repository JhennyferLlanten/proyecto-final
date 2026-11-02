package co.edu.uniquindio.web;

import co.edu.uniquindio.clustering.Dendrograma;
import co.edu.uniquindio.clustering.EjecutorClustering;
import co.edu.uniquindio.clustering.MatrizDistancias;
import co.edu.uniquindio.clustering.ResultadoClustering;
import co.edu.uniquindio.clustering.ResultadoCriterio;
import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.similitud.EjecutorSimilitud;
import co.edu.uniquindio.similitud.ModeloTfIdf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.ArrayList;

@Controller
public class ControladorClustering {

    @GetMapping("/clustering")
    public String mostrarClustering(Model modelo) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        ResultadoClustering resultado = calcularResultado(articulos);

        modelo.addAttribute("resultado", resultado);
        modelo.addAttribute("gruposMinimo", EjecutorClustering.GRUPOS_MINIMO);
        modelo.addAttribute("gruposMaximo", EjecutorClustering.GRUPOS_MAXIMO);
        return "clustering";
    }

    @GetMapping("/clustering/dendrograma/{posicion}")
    public String mostrarDendrograma(@PathVariable("posicion") int posicion, Model modelo) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        ResultadoClustering resultado = calcularResultado(articulos);
        if (posicion < 0 || posicion >= resultado.criterios.length) {
            return "redirect:/clustering";
        }

        ResultadoCriterio criterio = resultado.criterios[posicion];
        double alturaCorte = Dendrograma.calcularAlturaCorte(criterio.uniones, articulos.length, criterio.mejorGrupos);
        double minimoAmpliado = Dendrograma.calcularMinimoAmpliado(criterio.uniones);

        modelo.addAttribute("criterio", criterio);
        modelo.addAttribute("minimoAmpliado", minimoAmpliado);
        modelo.addAttribute("colores", Dendrograma.COLORES);
        modelo.addAttribute("figuraCompleta", Dendrograma.dibujar(criterio.uniones, articulos, alturaCorte,
                criterio.mejorGrupos, 0, 1, 0.1));
        modelo.addAttribute("figuraAmpliada", Dendrograma.dibujar(criterio.uniones, articulos, alturaCorte,
                criterio.mejorGrupos, minimoAmpliado, 1, 0.02));
        return "dendrograma";
    }

    private ResultadoClustering calcularResultado(Articulo[] articulos) {
        ArrayList<ArrayList<String>> palabras = EjecutorSimilitud.obtenerPalabras(articulos);
        ModeloTfIdf modeloTfIdf = new ModeloTfIdf(palabras);
        double[][] distancias = MatrizDistancias.calcularMatrizDistancia(modeloTfIdf.calcularMatriz());
        return EjecutorClustering.ejecutar(distancias, articulos);
    }
}
