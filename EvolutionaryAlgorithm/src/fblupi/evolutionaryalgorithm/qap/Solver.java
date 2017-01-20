package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * QAP Solver with Evolutionary Algorithms
 * @author fblupi
 */
public class Solver {
    public static void main(String[] args) throws FileNotFoundException {
        boolean todoStandard = false;
        boolean todoBaldwinian = false;
        boolean todoLamarckian = true;

        long iniStandard, finStandard, iniBaldwinian, finBaldwinian, iniLamarckian, finLamarckian;

        EA standard, baldwinian, lamarckian;

        System.out.println("Reading matrices...");
        Matrices.setInput(new File("data/qap/tai256c.dat"));

        if (todoStandard) {
            System.out.println("*****************************************************************************************");

            System.out.println("Building standard algorithm...");
            standard = new EA(EAType.STANDARD);
            System.out.println("Executing standard algorithm...");
            iniStandard = System.currentTimeMillis();
            standard.execute();
            finStandard = System.currentTimeMillis();

            System.out.println("*****************************************************************************************");

            System.out.println("STANDARD");
            System.out.println("Time: " + (finStandard - iniStandard) / 1000);
            System.out.println("Solution: " + Arrays.toString(standard.getFittest().getSolution()));
            System.out.println("Fitness: " + standard.getFittest().getFitness());
        }

        if (todoBaldwinian) {
            System.out.println("*****************************************************************************************");

            System.out.println("Building baldwinian algorithm...");
            baldwinian = new EA(EAType.BALDWINIAN);
            System.out.println("Executing baldwinian algorithm...");
            iniBaldwinian = System.currentTimeMillis();
            baldwinian.execute();
            finBaldwinian = System.currentTimeMillis();

            System.out.println("*****************************************************************************************");

            System.out.println("BALDWINIAN");
            System.out.println("Time: " + (finBaldwinian - iniBaldwinian) / 1000);
            System.out.println("Solution: " + Arrays.toString(baldwinian.getFittest().getSolution()));
            System.out.println("Fitness: " + baldwinian.getFittest().getFitness());
        }

        if (todoLamarckian) {
            System.out.println("*****************************************************************************************");

            System.out.println("Building lamarckian algorithm...");
            lamarckian = new EA(EAType.LAMARCKIAN);
            System.out.println("Executing lamarckian algorithm...");
            iniLamarckian = System.currentTimeMillis();
            lamarckian.execute();
            finLamarckian = System.currentTimeMillis();

            System.out.println("LAMARCKIAN");
            System.out.println("Time: " + (finLamarckian - iniLamarckian) / 1000);
            System.out.println("Solution: " + Arrays.toString(lamarckian.getFittest().getSolution()));
            System.out.println("Fitness: " + lamarckian.getFittest().getFitness());
        }
    }
}
