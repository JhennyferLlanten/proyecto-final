package co.edu.uniquindio.web;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.similitud.ComparacionSeleccion;
import co.edu.uniquindio.similitud.EjecutorSimilitud;
import co.edu.uniquindio.similitud.ModeloTfIdf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ControladorSimilitud {

    @GetMapping("/similitud")
    public String mostrarSimilitud(@RequestParam(name = "articulos", required = false) List<Integer> seleccionados,
                                   Model modelo) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        ArrayList<ArrayList<String>> palabras = EjecutorSimilitud.obtenerPalabras(articulos);
        ModeloTfIdf modeloTfIdf = new ModeloTfIdf(palabras);

        modelo.addAttribute("articulos", articulos);
        modelo.addAttribute("seleccionados", seleccionados);
        modelo.addAttribute("matriz", modeloTfIdf.calcularMatriz());

        if (seleccionados != null) {
            ArrayList<Integer> posiciones = new ArrayList<>();
            for (int i = 0; i < seleccionados.size(); i++) {
                int posicion = buscarPosicion(articulos, seleccionados.get(i));
                if (posicion != -1 && !posiciones.contains(posicion)) {
                    posiciones.add(posicion);
                }
            }
            if (posiciones.size() < 2) {
                modelo.addAttribute("mensaje", "Selecciona al menos dos artículos.");
            } else {
                ComparacionSeleccion comparacion = EjecutorSimilitud.compararSeleccion(articulos, palabras, modeloTfIdf, posiciones);
                modelo.addAttribute("comparacion", comparacion);
            }
        }
        return "similitud";
    }

    private int buscarPosicion(Articulo[] articulos, int numero) {
        for (int i = 0; i < articulos.length; i++) {
            if (articulos[i].numero == numero) {
                return i;
            }
        }
        return -1;
    }
}
