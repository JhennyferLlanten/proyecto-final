package co.edu.uniquindio.procesamientoTexto;

public class PalabrasVacias {

    private static String[] lista = {
            "about", "above", "across", "after", "all", "also", "among", "an", "and", "any",
            "are", "as", "at", "be", "because", "been", "being", "between", "both", "but",
            "by", "can", "could", "did", "do", "does", "during", "each", "few", "for",
            "from", "had", "has", "have", "he", "her", "how", "however", "his", "if",
            "in", "into", "is", "it", "its", "many", "may", "might", "more", "most",
            "much", "no", "not", "of", "on", "one", "only", "or", "other", "our",
            "out", "over", "per", "same", "she", "should", "so", "some", "such", "than",
            "that", "the", "their", "them", "then", "there", "therefore", "these", "they", "this",
            "those", "through", "thus", "to", "too", "toward", "towards", "two", "under", "up",
            "upon", "very", "via", "was", "we", "were", "what", "when", "where", "whether",
            "which", "while", "who", "whom", "why", "will", "with", "within", "without", "would",
            "you", "your"
    };

    public static boolean esPalabraVacia(String palabra) {
        for (int i = 0; i < lista.length; i++) {
            if (lista[i].equals(palabra)) {
                return true;
            }
        }
        return false;
    }
}
