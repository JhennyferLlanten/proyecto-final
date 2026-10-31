package co.edu.uniquindio.similitud;

public class FilaSimilitud {

    public int numeroA;
    public int numeroB;
    public double jaccard;
    public double coseno;
    public double levenshtein;
    public double needlemanWunsch;
    public double word2vec;
    public double word2vecEuclidiana;

    public FilaSimilitud(int numeroA, int numeroB, double jaccard, double coseno, double levenshtein, double needlemanWunsch, double word2vec, double word2vecEuclidiana) {
        this.numeroA = numeroA;
        this.numeroB = numeroB;
        this.jaccard = jaccard;
        this.coseno = coseno;
        this.levenshtein = levenshtein;
        this.needlemanWunsch = needlemanWunsch;
        this.word2vec = word2vec;
        this.word2vecEuclidiana = word2vecEuclidiana;
    }
}
