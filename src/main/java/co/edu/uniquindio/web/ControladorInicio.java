package co.edu.uniquindio.web;

import co.edu.uniquindio.modelo.Articulo;
import co.edu.uniquindio.modelo.LectorDatos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class ControladorInicio {

    @GetMapping("/")
    public String mostrarInicio(Model modelo) throws IOException {
        Articulo[] articulos = LectorDatos.leerArticulos();
        modelo.addAttribute("articulos", articulos);
        return "inicio";
    }
}
