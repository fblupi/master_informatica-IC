package fblupi.evolutionaryalgorithm.qap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * QAPMatrices matrix reader
 * @author fblupi
 */
public class QAPMatrices {
    /**
     * Number of installations
     */
    private int n;

    /**
     * Distances between each installation
     */
    private int[][] d;

    /**
     * Material flow between each installation
     */
    private int[][] w;

    /**
     * Constructor. Set input file with the data of the matrices
     * @param file file with the data of the matrices
     * @throws FileNotFoundException
     */
    public QAPMatrices(File file) throws FileNotFoundException {
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
    public int getSize() {
        return n;
    }

    /**
     * Get distance matrix
     * @return distance matrix
     */
    public int[][] getDistanceMatrix() {
        return d;
    }

    /**
     * Get material flow matrix
     * @return material flow matrix
     */
    public int[][] getFlowMatrix() {
        return w;
    }
}
