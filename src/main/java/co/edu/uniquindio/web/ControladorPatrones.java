package co.edu.uniquindio.web;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import co.edu.uniquindio.patrones.EjecutorBusqueda;
import co.edu.uniquindio.patrones.ResultadoAlgoritmo;
import co.edu.uniquindio.procesamientoTexto.BuscadorTexto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class ControladorPatrones {

    @GetMapping("/patrones")
    public String mostrarPatrones(@RequestParam(name = "patron", required = false) String patron, Model modelo) throws IOException {
        modelo.addAttribute("patron", patron);
        if (patron != null && patron.length() > 0) {
            Articulo[] articulos = LectorDatos.leerArticulos();
            String[] textos = EjecutorBusqueda.obtenerTextosEnMinuscula(articulos);
            String patronMinuscula = BuscadorTexto.textoEnMinuscula(patron);

            ResultadoAlgoritmo kmp = EjecutorBusqueda.ejecutarKMP(textos, patronMinuscula);
            ResultadoAlgoritmo boyerMoore = EjecutorBusqueda.ejecutarBoyerMoore(textos, patronMinuscula);
            ResultadoAlgoritmo rabinKarp = EjecutorBusqueda.ejecutarRabinKarp(textos, patronMinuscula);

            ResultadoAlgoritmo[] resultados = {kmp, boyerMoore, rabinKarp};
            modelo.addAttribute("resultados", resultados);
            modelo.addAttribute("filas", EjecutorBusqueda.armarFilas(articulos, kmp, boyerMoore, rabinKarp));
        }
        return "patrones";
    }
}
