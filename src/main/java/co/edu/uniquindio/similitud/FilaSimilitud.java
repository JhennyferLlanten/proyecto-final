package co.edu.uniquindio.similitud;

public class FilaSimilitud {

    public int numeroA;
    public int numeroB;
    public double jaccard;
    public double coseno;
    public double levenshtein;
    public double needlemanWunsch;

    public FilaSimilitud(int numeroA, int numeroB, double jaccard, double coseno, double levenshtein, double needlemanWunsch) {
        this.numeroA = numeroA;
        this.numeroB = numeroB;
        this.jaccard = jaccard;
        this.coseno = coseno;
        this.levenshtein = levenshtein;
        this.needlemanWunsch = needlemanWunsch;
    }
}
