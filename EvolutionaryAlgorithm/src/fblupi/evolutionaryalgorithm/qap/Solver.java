package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * QAP Solver with Evolutionary Algorithms
 */
public class Solver {
    public static void main(String[] args) throws FileNotFoundException {
        long iniStandard, finStandard, iniBaldwinian, finBaldwinian, iniLamarckian, finLamarckian;

        System.out.println("Reading matrices...");
        Matrices matrices = new Matrices(new File("data/qap/tai256c.dat"));

        EA standard = new EA(EAType.STANDARD, matrices);
        System.out.println("Executing standard algorithm...");
        iniStandard = System.currentTimeMillis();
        standard.execute();
        finStandard = System.currentTimeMillis();

        System.out.println("*******************************************************************************");

        EA baldwinian = new EA(EAType.BALDWINIAN, matrices);
        System.out.println("Executing baldwinian algorithm...");
        iniBaldwinian = System.currentTimeMillis();
        //baldwinian.execute();
        finBaldwinian = System.currentTimeMillis();

        System.out.println("*******************************************************************************");

        EA lamarckian = new EA(EAType.LAMARCKIAN, matrices);
        System.out.println("Executing lamarckian algorithm...");
        iniLamarckian = System.currentTimeMillis();
        lamarckian.execute();
        finLamarckian = System.currentTimeMillis();

        System.out.println("*******************************************************************************");

        System.out.println("STANDARD");
        System.out.print("Time: " + (finStandard - iniStandard) / 1000);
        System.out.println(Arrays.toString(standard.getFittest().getSolution())
                + " -> " + standard.getFittest().getFitness());

        System.out.println("*******************************************************************************");

        System.out.println("BALDWINIAN");
        System.out.print("Time: " + (finBaldwinian - iniBaldwinian) / 1000);
        System.out.println(Arrays.toString(baldwinian.getFittest().getSolution())
                + " -> " + baldwinian.getFittest().getFitness());

        System.out.println("*******************************************************************************");

        System.out.println("LAMARCKIAN");
        System.out.print("Time: " + (finLamarckian - iniLamarckian) / 1000);
        System.out.println(Arrays.toString(lamarckian.getFittest().getSolution())
                + " -> " + lamarckian.getFittest().getFitness());

    }
}
