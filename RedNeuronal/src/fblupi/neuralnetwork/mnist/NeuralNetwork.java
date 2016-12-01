package fblupi.neuralnetwork.mnist;

import java.util.Random;

/**
 * 
 * @author fblupi
 */
public class NeuralNetwork {
    private final int IMAGE_SIZE = 28;
    
    private final int INPUT_LAYER_SIZE = IMAGE_SIZE * IMAGE_SIZE;
    private final int HIDDEN_LAYER_SIZE = 100;
    private final int OUTPUT_LAYER_SIZE = 10;
    
    private final int OUTPUT_ARRAY_SIZE = 3;
    private final int WEIGHT_ARRAY_SIZE = 2;
    
    private final int INPUT_LAYER_INDEX = 0;
    private final int HIDDEN_LAYER_INDEX = 1;
    private final int OUTPUT_LAYER_INDEX = 2;
    
    private final int HIDDEN_WEIGHT_INDEX = 0;
    private final int OUTPUT_WEIGHT_INDEX = 1;
    
    private final double RANDOM_MIN = -.1;
    private final double RANDOM_MAX =  .1;
    private final double RANDOM_INTERVAL = RANDOM_MAX - RANDOM_MIN;
    
    private final double LEARNING_RATE = .1;
    
    private double[][] output;
    private double[][][] weight;
    private double[][] bias;
    
    private double[] deltaOutput;
    private double[][] delta;
    private double[][][] deltaWeight;
    
    private String results;
    
    public NeuralNetwork() {
        initializeArrays();
        initializeRandomValues();
    }
    
    public void train(float[][][] images, int[] results, int iterations) {
        for (int iter = 0; iter < iterations; iter++) {
            System.out.println("Iteration " + (iter + 1) + "/" + iterations + "...");
            for (int i = 0; i < images.length; i++) {
                trainSingleImage(images[i], results[i]);
            }
        }
    }
    
    private void trainSingleImage(float[][] image, int result) {
        addInputFromImage(image);
        feedForwardPropagation();
        backPropagation(result);
    }
    
    public float test(float[][][] images, int[] results) {
        this.results = "";
        int error = 0;
        for (int i = 0; i < images.length; i++) {
            if (!testSingleImage(images[i], results[i])) {
                error++;
            }
        }
        return (float) error / images.length * 100;
    }
    
    private boolean testSingleImage(float[][] image, int result) {
        addInputFromImage(image);
        feedForwardPropagation();
        
        int max = 0;
        double maxValue = output[OUTPUT_LAYER_INDEX][0];
        
        for (int i = 1; i < OUTPUT_LAYER_SIZE; i++) {
            if (output[OUTPUT_LAYER_INDEX][i] > maxValue) {
                maxValue = output[OUTPUT_LAYER_INDEX][i];
                max = i;
            }
        }
        this.results += max;
        
        return max == result;
    }
    
    private void backPropagationOutputLayer() {
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            delta[OUTPUT_WEIGHT_INDEX][i] = deltaOutput[i] * output[OUTPUT_LAYER_INDEX][i] * (1 - output[OUTPUT_LAYER_INDEX][i]);
            for (int j = 0; j < HIDDEN_LAYER_SIZE; j++) {
                deltaWeight[OUTPUT_WEIGHT_INDEX][i][j] = LEARNING_RATE * delta[OUTPUT_WEIGHT_INDEX][i] * output[HIDDEN_LAYER_INDEX][j];
                weight[OUTPUT_WEIGHT_INDEX][i][j] += deltaWeight[OUTPUT_WEIGHT_INDEX][i][j];
            }
            bias[OUTPUT_WEIGHT_INDEX][i] += LEARNING_RATE * delta[OUTPUT_WEIGHT_INDEX][i];
        }
    }
    
    private void backPropagationHiddenLayer() {
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            double sum = 0;
            for (int j = 0; j < OUTPUT_LAYER_SIZE; j++) {
                sum += delta[OUTPUT_WEIGHT_INDEX][j] * weight[OUTPUT_WEIGHT_INDEX][j][i];
            }
            delta[HIDDEN_WEIGHT_INDEX][i] = output[HIDDEN_LAYER_INDEX][i] * (1 - output[HIDDEN_LAYER_INDEX][i]) * sum;
            for (int j = 0; j < INPUT_LAYER_SIZE; j++) {
                deltaWeight[HIDDEN_WEIGHT_INDEX][i][j] = LEARNING_RATE * delta[HIDDEN_WEIGHT_INDEX][i] * output[INPUT_LAYER_INDEX][j];
                weight[HIDDEN_WEIGHT_INDEX][i][j] += deltaWeight[HIDDEN_WEIGHT_INDEX][i][j];
            }
            bias[HIDDEN_WEIGHT_INDEX][i] += LEARNING_RATE * delta[HIDDEN_WEIGHT_INDEX][i];
        }
    }
    
    private void calculateDeltaOutput(int result) {
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            if (i == result) {
                deltaOutput[i] = 1.0 - output[OUTPUT_LAYER_INDEX][i];
            } else {
                deltaOutput[i] = 0.0 - output[OUTPUT_LAYER_INDEX][i];
            }
        }
    }
    
    private void backPropagation(int result) {
        calculateDeltaOutput(result);
        backPropagationOutputLayer();
        backPropagationHiddenLayer();
    }
    
    private void feedForwardPropagation() {
        for (int k = 1; k < OUTPUT_ARRAY_SIZE; k++) {
            for (int i = 0; i < output[k].length; i++) {
                double sum = bias[k - 1][i];
                for (int j = 0; j < output[k - 1].length; j++) {
                    sum += weight[k - 1][i][j] * output[k - 1][j];
                }
                output[k][i] = sigmoid(sum);
            }
        }
    }
    
    private void addInputFromImage(float[][] image) {
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                output[0][i * IMAGE_SIZE + j] = image[i][j];
            }
        }
    }
    
    private void initializeWeightValues() {
        Random random = new Random();
        
        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) { // First layer has no weights
            for (int i = 0; i < weight[k].length; i++) {
                for (int j = 0; j < weight[k][i].length; j++) {
                    weight[k][i][j] = random.nextDouble() * RANDOM_INTERVAL + RANDOM_MIN;
                }
            }
        }
    }
    
    private void initializeBiasValues() {
        Random random = new Random();
        
        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) {
            for (int i = 0; i < bias[k].length; i++) {
                bias[k][i] = random.nextDouble() * RANDOM_INTERVAL + RANDOM_MIN;
            }
        }
    }
    
    private void initializeRandomValues() {
        initializeWeightValues();
        initializeBiasValues();
    }
    
    private void initializeWeightArrays() {
        weight = new double[WEIGHT_ARRAY_SIZE][][]; // weights for hidden and output layers
        
        weight[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE][];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            weight[HIDDEN_WEIGHT_INDEX][i] = new double[INPUT_LAYER_SIZE]; // hidden layer has as weights as input size
        }
        
        weight[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE][];
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            weight[OUTPUT_WEIGHT_INDEX][i] = new double[HIDDEN_LAYER_SIZE]; // output layer has as weights as hidden size
        }
    }
    
    private void initializeOutputArrays() {
        output = new double[OUTPUT_ARRAY_SIZE][]; // outputs for input, hidden and output layers
        
        output[INPUT_LAYER_INDEX] = new double[INPUT_LAYER_SIZE]; // input layer has input size
        output[HIDDEN_LAYER_INDEX] = new double[HIDDEN_LAYER_SIZE]; // hidden layer has hidden layer size
        output[OUTPUT_LAYER_INDEX] = new double[OUTPUT_LAYER_SIZE]; // Output layer has 10 possible outputs
    }
    
    private void initializeBiasArrays() {
        bias = new double[WEIGHT_ARRAY_SIZE][];
        
        bias[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
        bias[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE];
    }
    
    private void initializeDeltaOutputArrays() {
        deltaOutput = new double[OUTPUT_LAYER_SIZE];
    }
    
    private void initializeDeltaArrays() {
        delta = new double[WEIGHT_ARRAY_SIZE][];
        
        delta[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
        delta[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE];
    }
    
    private void initializeDeltaWeightArrays() {
        deltaWeight = new double[WEIGHT_ARRAY_SIZE][][]; // weights for hidden and output layers
        
        deltaWeight[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE][];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            deltaWeight[HIDDEN_WEIGHT_INDEX][i] = new double[INPUT_LAYER_SIZE]; // hidden layer has as weights as input size
        }
        
        deltaWeight[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE][];
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            deltaWeight[OUTPUT_WEIGHT_INDEX][i] = new double[HIDDEN_LAYER_SIZE]; // output layer has as weights as hidden size
        }
    }
    
    private void initializeArrays() {
        initializeOutputArrays();
        initializeWeightArrays();
        initializeBiasArrays();
        initializeDeltaOutputArrays();
        initializeDeltaArrays();
        initializeDeltaWeightArrays();
    }
    
    private double sigmoid(double x) {
        return (1.0 / (1.0 + Math.exp(-x)));
    }
    
    public String getResults() {
        return results;
    }
    
    public double[][][] getWeight() {
        return weight;
    }
    
    public double[][] getBias() {
        return bias;
    }
    
    public void setWeight(double[][][] weight) {
        for (int k = 0; k < this.weight.length; k++) {
            for (int i = 0; i < this.weight[k].length; i++) {
                for (int j = 0; j < this.weight[k][i].length; j++) {
                    this.weight[k][i][j] = weight[k][i][j];
                }
            }
        }
    }
    
    public void setBias(double[][] bias) {
        for (int k = 0; k < this.bias.length; k++) {
            for (int i = 0; i < this.bias[k].length; i++) {
                this.bias[k][i] = bias[k][i];
            }
        }
    }
}
