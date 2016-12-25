package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Matrices matrix reader
 * @author fblupi
 */
public class Matrices {
    /**
     * Number of installations
     */
    private static int n;

    /**
     * Distances between each installation
     */
    private static int[][] d;

    /**
     * Material flow between each installation
     */
    private static int[][] w;

    /**
     * et input file with the data of the matrices
     * @param file file with the data of the matrices
     * @throws FileNotFoundException
     */
    public static void setInput(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        n = scanner.nextInt();
        d = new int[n][n];
        w = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = scanner.nextInt();
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                w[i][j] = scanner.nextInt();
            }
        }
    }

    /**
     * Get number of installations
     * @return number of installations
     */
    public static int getSize() {
        return n;
    }

    /**
     * Get distance matrix
     * @return distance matrix
     */
    public static int[][] getDistanceMatrix() {
        return d;
    }

    /**
     * Get material flow matrix
     * @return material flow matrix
     */
    public static int[][] getFlowMatrix() {
        return w;
    }
}
