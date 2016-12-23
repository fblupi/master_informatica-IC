package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * QAP Solver with Evolutionary Algorithms
 */
public class QAPSolver {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Reading matrices...");
        QAPMatrices qap = new QAPMatrices(new File("data/qap/bur26a.dat"));
    }
}
