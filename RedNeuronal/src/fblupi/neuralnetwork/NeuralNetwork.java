package fblupi.neuralnetwork;

import java.util.Random;

/**
 * 
 * @author fblupi
 */
public class NeuralNetwork {
    private final int IMAGE_SIZE = 28;
    
    private final int INPUT_LAYER_SIZE = IMAGE_SIZE * IMAGE_SIZE;
    private final int HIDDEN_LAYER_SIZE = 256;
    private final int OUTPUT_LAYER_SIZE = 10;
    private final int OUTPUT_ARRAY_SIZE = 3;
    private final int WEIGHT_ARRAY_SIZE = 2;
    private final int BIAS_ARRAY_SIZE = 3;
    private final int INPUT_LAYER_INDEX = 0;
    private final int HIDDEN_LAYER_INDEX = 1;
    private final int OUTPUT_LAYER_INDEX = 2;
    private final int HIDDEN_WEIGHT_INDEX = 0;
    private final int OUTPUT_WEIGHT_INDEX = 1;
    
    private final double RANDOM_MIN = -.1;
    private final double RANDOM_MAX =  .1;
    private final double RANDOM_INTERVAL = RANDOM_MAX - RANDOM_MIN;
    
    private final double LEARNING_RATE = .1;
    
    private double[][] outputs;
    private double[][][] weights;
    private double[][] bias;
    
    private double[] deltaOutput;
    private double[][] errorSignals;
    
    public NeuralNetwork() {
        initializeArrays();
        initializeWeightValues();
    }
    
    public void train(float[][][] images, int[] results) {
        for (int iteration = 0; iteration < 10; iteration++)
            for (int i = 0; i < images.length; i++) {
                System.out.println("Iteration: " + iteration + ", Training " + (i + 1) + "/" + images.length);
                trainSingleImage(images[i], results[i]);
            }
    }
    
    private void trainSingleImage(float[][] image, int result) {
        addInputFromImage(image);
        feedForwardPropagation();
        backPropagation(result);
    }
    
    private void backPropagationOutputLayer() {
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            errorSignals[OUTPUT_WEIGHT_INDEX][i] = deltaOutput[i] * outputs[OUTPUT_LAYER_INDEX][i] * (1 - outputs[OUTPUT_LAYER_INDEX][i]);
            for (int j = 0; j < HIDDEN_LAYER_SIZE; j++) {
                weights[OUTPUT_WEIGHT_INDEX][i][j] += LEARNING_RATE * errorSignals[OUTPUT_WEIGHT_INDEX][i] * outputs[HIDDEN_LAYER_INDEX][j];
            }
            bias[OUTPUT_WEIGHT_INDEX][i] += LEARNING_RATE * errorSignals[OUTPUT_WEIGHT_INDEX][i];
        }
    }
    
    private void backPropagationHiddenLayer() {
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            errorSignals[HIDDEN_WEIGHT_INDEX][i] = 0;
            for (int j = 0; j < OUTPUT_LAYER_SIZE; j++) {
                errorSignals[HIDDEN_WEIGHT_INDEX][i] += errorSignals[OUTPUT_WEIGHT_INDEX][j] * weights[OUTPUT_WEIGHT_INDEX][j][i];
            }
            for (int j = 0; j < INPUT_LAYER_SIZE; j++) {
                weights[HIDDEN_WEIGHT_INDEX][i][j] += LEARNING_RATE * errorSignals[HIDDEN_WEIGHT_INDEX][i] * outputs[INPUT_LAYER_INDEX][j];
            }
            bias[HIDDEN_WEIGHT_INDEX][i] += LEARNING_RATE * errorSignals[HIDDEN_WEIGHT_INDEX][i];
        }
    }
    
    private void backPropagation(int result) {
        calculateDeltaOutput(result);
        backPropagationOutputLayer();
        backPropagationHiddenLayer();
    }
    
    private void calculateDeltaOutput(int result) {
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            if (i == result) {
                deltaOutput[i] = 1.0 - outputs[OUTPUT_LAYER_INDEX][i];
            } else {
                deltaOutput[i] = 0.0 - outputs[OUTPUT_LAYER_INDEX][i];
            }
        }
    }
    
    public float test(float[][][] images, int[] results) {
        int error = 0;
        for (int i = 0; i < images.length; i++) {
            System.out.println("Testing " + (i + 1) + "/" + images.length);
            if (!testSingleImage(images[i], results[i])) {
                error++;
            }
        }
        return (float) error / images.length * 100;
    }
    
    public boolean testSingleImage(float[][] image, int result) {
        addInputFromImage(image);
        feedForwardPropagation();
        
        int max = 0;
        double maxValue = outputs[OUTPUT_LAYER_INDEX][0];
        
        for (int i = 1; i < OUTPUT_LAYER_SIZE; i++) {
            if (outputs[OUTPUT_LAYER_INDEX][i] > maxValue) {
                maxValue = outputs[OUTPUT_LAYER_INDEX][i];
                max = i;
            }
        }
        
        System.out.println("Expected result: " + result + ", my result: " + max);
        
        return max == result;
    }
    
    private void feedForwardPropagation() {
        for (int k = 1; k < OUTPUT_ARRAY_SIZE; k++) {
            for (int i = 0; i < outputs[k].length; i++) {
                double sum = 0;
                for (int j = 0; j < outputs[k - 1].length; j++) {
                    sum += weights[k - 1][i][j] * outputs[k - 1][j] + bias[k][i];
                }
                outputs[k][i] = sigmoid(sum);
            }
        }
    }
    
    private void addInputFromImage(float[][] image) {
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                outputs[0][i * IMAGE_SIZE + j] = image[i][j];
            }
        }
    }
    
    private void initializeWeightValues() {
        Random random = new Random();
        
        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) { // First layer has no weights
            for (int i = 0; i < weights[k].length; i++) {
                for (int j = 0; j < weights[k][i].length; j++) {
                    weights[k][i][j] = random.nextDouble() * RANDOM_INTERVAL + RANDOM_MIN;
                }
            }
        }
    }
    
    private void initializeOutputArrays() {
        outputs = new double[OUTPUT_ARRAY_SIZE][]; // outputs for input, hidden and output layers
        
        outputs[INPUT_LAYER_INDEX] = new double[INPUT_LAYER_SIZE]; // input layer has input size
        outputs[HIDDEN_LAYER_INDEX] = new double[HIDDEN_LAYER_SIZE]; // hidden layer has hidden layer size
        outputs[OUTPUT_LAYER_INDEX] = new double[OUTPUT_LAYER_SIZE]; // Output layer has 10 possible outputs
    }
    
    private void initializeWeightArrays() {
        weights = new double[WEIGHT_ARRAY_SIZE][][]; // weights for hidden and output layers
        
        weights[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE][];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            weights[HIDDEN_WEIGHT_INDEX][i] = new double[INPUT_LAYER_SIZE]; // hidden layer has as weights as input size
        }
        
        weights[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE][];
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            weights[OUTPUT_WEIGHT_INDEX][i] = new double[HIDDEN_LAYER_SIZE]; // output layer has as weights as hidden size
        }
    }
    
    private void initializeBiasArrays() {
        bias = new double[BIAS_ARRAY_SIZE][];
        
        bias[INPUT_LAYER_INDEX] = new double[INPUT_LAYER_SIZE]; // input layer has input size
        bias[HIDDEN_LAYER_INDEX] = new double[HIDDEN_LAYER_SIZE]; // hidden layer has hidden layer size
        bias[OUTPUT_LAYER_INDEX] = new double[OUTPUT_LAYER_SIZE]; // Output layer has 10 possible outputs
    }
    
    private void initializeOutputErrorArrays() {
        deltaOutput = new double[OUTPUT_LAYER_SIZE];
    }
    
    private void initializeErrorSigntalArrays() {
        errorSignals = new double[WEIGHT_ARRAY_SIZE][];
        
        errorSignals[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
        errorSignals[OUTPUT_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
    }
    
    private void initializeArrays() {
        initializeOutputArrays();
        initializeWeightArrays();
        initializeBiasArrays();
        initializeOutputErrorArrays();
        initializeErrorSigntalArrays();
    }
    
    private double sigmoid(double x) {
        return (1.0 / (1.0 + Math.exp(-x)));
    }
}
