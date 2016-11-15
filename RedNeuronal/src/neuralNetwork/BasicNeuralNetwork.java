package neuralNetwork;

/**
 *
 * @author fblupi
 */
public class BasicNeuralNetwork {
    private final int NUMBERS = 10;
    private final int SIZE = 28;
    private int[][][] counter;
    private int[] results;
    
    public BasicNeuralNetwork() {
        initializeCounter();
    }
    
    private void initializeCounter() {
        counter = new int[NUMBERS][SIZE][SIZE]; 
        for (int i = 0; i < NUMBERS; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < SIZE; k++) {
                    counter[i][j][k] = 0;
                }
            }
        }
    }
    
    public int[][][] getCounter() {
        return counter;
    }
    
    public int[] getResults() {
        return results;
    }
    
    public void train(int[][][] images, int[] labels) {
        for (int i = 0; i < images.length; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < SIZE; k++) {
                    if (images[i][j][k] > 0) {
                        counter[labels[i]][j][k]++;
                    } else {
                        counter[labels[i]][j][k]--;
                    }
                }
            }
        }
    }
    
    public void test(int[][][] images) {
        results = new int[images.length];
        for (int i = 0; i < images.length; i++) {
            int[] votes = new int[NUMBERS];
            for (int j = 0; j < NUMBERS; j++) {
                votes[j] = 0;
            }
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < SIZE; k++) {
                    if (images[i][j][k] > 0) {
                        for (int l = 0; l < NUMBERS; l++) {
                            if (counter[l][j][k] > 0) {
                                votes[l]++;
                            }
                        }
                    }
                }
            }
            int result = 0;
            int resultValue = votes[0];
            for (int j = 1; j < NUMBERS; j++) {
                if (votes[j] > resultValue) {
                    result = j;
                    resultValue = votes[j];
                }
            }
            results[i] = result;
        }
    }
    
    public float getErrorRate(int[] labels) {
        int error = 0;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != results[i]) {
                error++;
            }
        }
        return (float) error / labels.length * 100;
    }
    
}
