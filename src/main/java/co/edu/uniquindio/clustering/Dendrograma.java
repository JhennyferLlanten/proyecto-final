package co.edu.uniquindio.clustering;

import co.edu.uniquindio.modelo.Articulo;

import java.util.ArrayList;

public class Dendrograma {

    public static final String[] COLORES = {"#1f77b4", "#2ca02c", "#ff7f0e", "#9467bd", "#8c564b", "#17becf"};

    private static final String COLOR_NEUTRO = "#000000";
    private static final int ANCHO = 640;
    private static final int ALTO = 560;
    private static final int MARGEN_IZQUIERDO = 60;
    private static final int MARGEN_DERECHO = 20;
    private static final int MARGEN_SUPERIOR = 20;
    private static final int MARGEN_INFERIOR = 40;

    public static double calcularAlturaCorte(Union[] uniones, int total, int numeroGrupos) {
        return (uniones[total - numeroGrupos - 1].altura + uniones[total - numeroGrupos].altura) / 2;
    }

    public static double calcularMinimoAmpliado(Union[] uniones) {
        return Math.floor(uniones[0].altura * 50) / 50;
    }

    public static String dibujar(Union[] uniones, Articulo[] articulos, double alturaCorte, int numeroGrupos,
                                 double minimo, double maximo, double pasoMarcas) {
        int total = articulos.length;
        int[] etiquetas = Agrupamiento.cortarArbol(uniones, total, numeroGrupos);

        ArrayList<Integer> orden = new ArrayList<>();
        ordenarHojas(uniones, total, total + uniones.length - 1, orden);

        double separacion = (double) (ANCHO - MARGEN_IZQUIERDO - MARGEN_DERECHO) / total;
        double[] posicionX = new double[2 * total - 1];
        double[] altura = new double[2 * total - 1];
        int[] grupoNodo = new int[2 * total - 1];
        for (int i = 0; i < total; i++) {
            posicionX[orden.get(i)] = MARGEN_IZQUIERDO + separacion * i + separacion / 2;
            grupoNodo[i] = etiquetas[i];
        }
        for (int i = 0; i < uniones.length; i++) {
            Union union = uniones[i];
            int grupo = total + i;
            posicionX[grupo] = (posicionX[union.grupoIzquierdo] + posicionX[union.grupoDerecho]) / 2;
            altura[grupo] = union.altura;
            if (i < total - numeroGrupos) {
                grupoNodo[grupo] = grupoNodo[union.grupoIzquierdo];
            } else {
                grupoNodo[grupo] = -1;
            }
        }

        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + ANCHO + "\" height=\"" + ALTO
                + "\" viewBox=\"0 0 " + ANCHO + " " + ALTO + "\" font-family=\"sans-serif\" font-size=\"12\">");

        int marcas = (int) Math.round((maximo - minimo) / pasoMarcas);
        for (int i = 0; i <= marcas; i++) {
            double valor = minimo + i * pasoMarcas;
            int y = posicionY(valor, minimo, maximo);
            svg.append("<line x1=\"" + MARGEN_IZQUIERDO + "\" y1=\"" + y + "\" x2=\"" + (ANCHO - MARGEN_DERECHO)
                    + "\" y2=\"" + y + "\" stroke=\"#dddddd\"/>");
            svg.append("<text x=\"" + (MARGEN_IZQUIERDO - 8) + "\" y=\"" + (y + 4) + "\" text-anchor=\"end\">"
                    + formato(valor) + "</text>");
        }
        svg.append("<line x1=\"" + MARGEN_IZQUIERDO + "\" y1=\"" + MARGEN_SUPERIOR + "\" x2=\"" + MARGEN_IZQUIERDO
                + "\" y2=\"" + (ALTO - MARGEN_INFERIOR) + "\" stroke=\"black\"/>");
        svg.append("<text transform=\"rotate(-90 14 " + (ALTO / 2) + ")\" x=\"14\" y=\"" + (ALTO / 2)
                + "\" text-anchor=\"middle\">Distancia</text>");

        for (int i = 0; i < uniones.length; i++) {
            Union union = uniones[i];
            int xIzquierdo = (int) Math.round(posicionX[union.grupoIzquierdo]);
            int xDerecho = (int) Math.round(posicionX[union.grupoDerecho]);
            int yIzquierdo = posicionY(altura[union.grupoIzquierdo], minimo, maximo);
            int yDerecho = posicionY(altura[union.grupoDerecho], minimo, maximo);
            int yUnion = posicionY(union.altura, minimo, maximo);
            svg.append(linea(xIzquierdo, yIzquierdo, xIzquierdo, yUnion, colorDe(grupoNodo[union.grupoIzquierdo])));
            svg.append(linea(xDerecho, yDerecho, xDerecho, yUnion, colorDe(grupoNodo[union.grupoDerecho])));
            svg.append(linea(xIzquierdo, yUnion, xDerecho, yUnion, colorDe(grupoNodo[total + i])));
        }

        for (int i = 0; i < total; i++) {
            int x = (int) Math.round(posicionX[i]);
            svg.append("<text x=\"" + x + "\" y=\"" + (ALTO - MARGEN_INFERIOR + 18) + "\" text-anchor=\"middle\" fill=\""
                    + colorDe(grupoNodo[i]) + "\" font-weight=\"bold\">" + articulos[i].numero + "</text>");
        }

        int yCorte = posicionY(alturaCorte, minimo, maximo);
        svg.append("<line x1=\"" + MARGEN_IZQUIERDO + "\" y1=\"" + yCorte + "\" x2=\"" + (ANCHO - MARGEN_DERECHO)
                + "\" y2=\"" + yCorte + "\" stroke=\"red\" stroke-width=\"1.5\" stroke-dasharray=\"6 4\"/>");
        svg.append("<text x=\"" + (ANCHO - MARGEN_DERECHO - 4) + "\" y=\"" + (yCorte - 5)
                + "\" text-anchor=\"end\" fill=\"red\">corte k = " + numeroGrupos + "</text>");

        svg.append("</svg>");
        return svg.toString();
    }

    private static void ordenarHojas(Union[] uniones, int total, int grupo, ArrayList<Integer> orden) {
        if (grupo < total) {
            orden.add(grupo);
        } else {
            Union union = uniones[grupo - total];
            ordenarHojas(uniones, total, union.grupoIzquierdo, orden);
            ordenarHojas(uniones, total, union.grupoDerecho, orden);
        }
    }

    private static String linea(int x1, int y1, int x2, int y2, String color) {
        return "<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" stroke=\"" + color
                + "\" stroke-width=\"2\" stroke-linecap=\"round\"/>";
    }

    private static String colorDe(int grupo) {
        if (grupo < 0) {
            return COLOR_NEUTRO;
        }
        return COLORES[grupo];
    }

    private static int posicionY(double valor, double minimo, double maximo) {
        if (valor < minimo) {
            valor = minimo;
        }
        double alto = ALTO - MARGEN_SUPERIOR - MARGEN_INFERIOR;
        return (int) Math.round(ALTO - MARGEN_INFERIOR - (valor - minimo) / (maximo - minimo) * alto);
    }

    private static String formato(double valor) {
        return String.format("%.2f", valor).replace('.', ',');
    }
}
