package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * QAP Solver with Evolutionary Algorithms
 */
public class QAPSolver {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Reading matrices...");
        QAPMatrices matrices = new QAPMatrices(new File("data/qap/tai256c.dat"));
        BasicAlgorithm algorithm = new BasicAlgorithm(matrices);
        System.out.println("Executing basic algorithm...");
        algorithm.execute();
        System.out.println(Arrays.toString(algorithm.getPopulation().getFittest().getSolution())
                + " -> " + algorithm.getPopulation().getFittest().getFitness());
    }
}
