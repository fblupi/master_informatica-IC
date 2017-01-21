package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * QAP solution's fitness checker
 * @author fblupi
 */
public class CheckSolution {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Reading matrices...");
        Matrices.setInput(new File("data/qap/tai256c.dat"));
        System.out.println("Reading solution...");
        Individual individual = new Individual(new File("data/sol/44786272.txt"));
        System.out.println("Solution: " + Arrays.toString(individual.getSolution()));
        System.out.println("Fitness: " + individual.getFitness());
    }
}
