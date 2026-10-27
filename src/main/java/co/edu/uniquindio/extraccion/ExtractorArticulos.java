package co.edu.uniquindio.extraccion;

import co.edu.uniquindio.procesamientoTexto.BuscadorTexto;

public class ExtractorArticulos {

    public static <string> int encontrarInicioAbstract(String texto) {
        String[] marcas = {"\nEthical considerations", "Abstract", "A B S T R A C T", };      //La primera solo existe en el articulo 7 (antes del texto del abstract)
        int posicionMarca = -1;
        int tamanoMarca = 0;

        for (int i = 0; i < marcas.length && posicionMarca == -1; i++) {                    //se detiene en cuanto encuentra una marca
            int posicion = BuscadorTexto.buscarPatron(texto, marcas[i], 0);
            if (posicion != -1) {
                posicionMarca = posicion;
                tamanoMarca = marcas[i].length();                                            //guarda el largo de la marca que se encontro
            }
        }

        if (posicionMarca == -1) {                                                           //ninguna marca esta
            return -1;
        }

        int inicio = posicionMarca + tamanoMarca;                                            //Se ubica al terminar la marca
        while (inicio < texto.length() && esSeparador(texto.charAt(inicio))) {
            inicio++;
        }
        return inicio;
    }

    public static int encontrarFinAbstract(String texto, int inicioAbstract) {
        String[] marcasFin = {"\nKeywords", "\nK E Y W O R D S", "1. Introduction", "\nSince Alan Turing"};      //Keywords debe empezar una linea nueva. La ultima solo existe en el articulo 9 (primera frase del texto que sigue al abstract)
        int posicionFin = -1;

        for (int i = 0; i < marcasFin.length; i++) {
            int posicionMarca = BuscadorTexto.buscarPatron(texto, marcasFin[i], inicioAbstract);
            if (posicionMarca != -1 && (posicionFin == -1 || posicionMarca < posicionFin)) {    //marca existe y (no hay posicion guardada o la nueva marca esta primero)
                posicionFin = posicionMarca;                                                    //Guarda la posición donde termina el texto del abstract
            }
        }
        return posicionFin;
    }

    public static String extraerAbstract(String texto) {
        int inicio = encontrarInicioAbstract(texto);
        if (inicio == -1) {                 //no esta la palabra Abstract
            return "";
        }

        int fin = encontrarFinAbstract(texto, inicio);
        if (fin == -1) {                    //no hay marca de final
            return "";
        }

        while (fin > inicio && esSeparador(texto.charAt(fin - 1))) {        //quita los separadores del final
            fin--;
        }
        return limpiarAbstract(texto.substring(inicio, fin));
    }

    public static String limpiarAbstract(String abstractCrudo) {
        StringBuilder limpio = new StringBuilder();         //aqui se van agregando las letras que se quedan
        int tamano = abstractCrudo.length();
        int i = 0;

        while (i < tamano) {
            char c = abstractCrudo.charAt(i);

            if (c == '-' && i + 1 < tamano && esSaltoDeLinea(abstractCrudo.charAt(i + 1))) {
                i++;                                                                                 //salta el guion
                while (i < tamano && esSaltoDeLinea(abstractCrudo.charAt(i))) {
                    i++;                                                                            //salta el salto de linea: la palabra queda unida
                }
            } else if (esInvisible(c)) {                                                            //letra que no se ve: se descarta
                i++;
            } else if (esEspacioBlanco(c)) {                                                         // si la nueva letra es un espacio, tabulador o un salto de línea
                if (limpio.length() > 0 && limpio.charAt(limpio.length() - 1) != ' ') {             //si hay texto y la ultima letra no es espacio
                    limpio.append(' ');                                                             //un solo espacio, sin repetir
                }
                i++;
            } else {
                limpio.append(c);
                i++;
            }
        }
        return limpio.toString();
    }

    public static int obtenerNumero(String nombreArchivo) {
        int numero = 0;
        int i = 0;

        while (i < nombreArchivo.length() && nombreArchivo.charAt(i) >= '0' && nombreArchivo.charAt(i) <= '9') {
            numero = numero * 10 + (nombreArchivo.charAt(i) - '0');         //Convierte la letra del dígito en el número y lo agrega a la derecha del numero
            i++;
        }
        return numero;
    }

    // Se corrigen manualmente ya que son casos especiales que solo se ven despues de leer el pdf
    public static String corregirPalabrasPartidas(String abstractLimpio) {
        String[] partidas = {"practi tioners", "re lationships", "pro grams", "tech nological", "edu cation", "devel opment", "in clusion"};   //como salen del PDF
        String[] correctas = {"practitioners", "relationships", "programs", "technological", "education", "development", "inclusion"};       //como deben quedar

        String texto = abstractLimpio;
        for (int i = 0; i < partidas.length; i++) {                             //una vez por cada palabra partida
            int posicion = BuscadorTexto.buscarPatron(texto, partidas[i], 0);
            while (posicion != -1) {                                            //mientras la encuentre
                texto = texto.substring(0, posicion) + correctas[i] + texto.substring(posicion + partidas[i].length());         //antes + correcta + despues
                posicion = BuscadorTexto.buscarPatron(texto, partidas[i], posicion + correctas[i].length());                   //sigue buscando despues de lo corregido
            }
        }
        return texto;
    }

    public static String obtenerTitulo(String tituloMetadatos, String nombreArchivo) {
        if (contarPalabras(tituloMetadatos) < 4) {                                                                      //el metadato no es un titulo esta vacio o es un codigo
            return tituloDelNombre(nombreArchivo);
        }
        return tituloMetadatos;
    }

    public static int contarPalabras(String texto) {
        int palabras = 0;
        boolean dentroDePalabra = false;
        for (int i = 0; i < texto.length(); i++) {
            if (esEspacioBlanco(texto.charAt(i))) {
                dentroDePalabra = false;                    //un espacio termina la palabra
            } else if (dentroDePalabra == false) {          //si es una letra y no esta dentro de la palabra
                dentroDePalabra = true;                     //empieza una palabra nueva
                palabras++;
            }
        }
        return palabras;
    }

    public static String tituloDelNombre(String nombreArchivo) {
        int inicio = 0;
        while (inicio < nombreArchivo.length() && nombreArchivo.charAt(inicio) >= '0' && nombreArchivo.charAt(inicio) <= '9') {
            inicio++;                                       //salta el numero
        }
        inicio = inicio + 2;                                //salta el punto y el espacio
        int fin = nombreArchivo.length() - 4;               //quita ".pdf"
        return nombreArchivo.substring(inicio, fin);
    }

    public static boolean esSaltoDeLinea(char c) {
        return c == '\n' || c == '\r';
    }

    public static boolean esEspacioBlanco(char c) {
        return c == ' ' || c == '\t' || esSaltoDeLinea(c)
                || c == ' ' || c == ' ' || c == ' ' || c == ' ';        //otros espacios que traen algunos PDF, \u00A0, \u2002, \u2003, \u200A
    }

    public static boolean esInvisible(char c) {
        return c == '​' || c == '﻿';                                       //espacio de ancho cero y marca de inicio de archivo, \u200B, \uFEFF
    }

    public static boolean esSeparador(char c) {
        return c == ':' || esEspacioBlanco(c) || esInvisible(c);
    }
}
