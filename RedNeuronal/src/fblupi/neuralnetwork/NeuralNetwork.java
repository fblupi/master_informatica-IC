/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fblupi.neuralnetwork;

import java.util.Random;

/**
 *
 * @author fboli
 */
public class NeuralNetwork {
    private final int IMAGE_SIZE = 28;
    private final int INPUT_SIZE = IMAGE_SIZE * IMAGE_SIZE;
    private final int HIDDEN_LAYER_SIZE = INPUT_SIZE;
    private final int POSSIBLE_OUTPUTS = 10;
    private final double RANDOM_MIN = -1.7165;
    private final double RANDOM_MAX =  1.7165;
    private final double RANDOM_INTERVAL = RANDOM_MAX - RANDOM_MIN;
    private final double LEARNING_RATE = 0.7;
    private final int OUTPUT_LAYER_INDEX;
    
    private double[][] outputs;
    private double[][][] weights;
    
    public NeuralNetwork(int nLayers) {
        OUTPUT_LAYER_INDEX = nLayers - 1;
        initializeArrays(nLayers);
        initializeWeightValues();
    }
    
    public void train(float[][][] images, int[] results) {
        for (int i = 0; i < images.length; i++) {
            System.out.println("Training " + (i + 1) + "/" + images.length);
            trainSingleImage(images[i], results[i]);
        }
    }
    
    private void trainSingleImage(float[][] image, int result) {
        addInputFromImage(image);
        forwardPropagation();
        for (int i = 0; i < POSSIBLE_OUTPUTS; i++) {
            if (i == result) {
                double error = 1.0 - outputs[OUTPUT_LAYER_INDEX][i];
                backPropagation(error);
            } else {
                double error = 0.0 - outputs[OUTPUT_LAYER_INDEX][i];
                backPropagation(error);
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
        forwardPropagation();
        
        int max = 0;
        double maxValue = outputs[OUTPUT_LAYER_INDEX][0];
        
        for (int i = 1; i < POSSIBLE_OUTPUTS; i++) {
            if (outputs[OUTPUT_LAYER_INDEX][i] > maxValue) {
                maxValue = outputs[OUTPUT_LAYER_INDEX][i];
                max = i;
            }
        }
        
        System.out.println("Expected result: " + result + ", my result: " + max);
        
        return max == result;
    }
    
    private void backPropagation(double error) {
        for (int k = OUTPUT_LAYER_INDEX; k > 0; k--) {
            for (int i = 0; i < weights[k].length; i++) {
                for (int j = 0; j < weights[k][i].length; j++) {
                    weights[k][i][j] += LEARNING_RATE * error * outputs[k - 1][j] * outputs[k][i] * (1 - outputs[k][i]);
                }
            }
        }
    }
    
    private void forwardPropagation() {
        for (int k = 1; k < outputs.length; k++) {
            for (int i = 0; i < outputs[k].length; i++) {
                double sum = 0;
                for (int j = 0; j < outputs[k - 1].length; j++) {
                    sum += weights[k][i][j] * outputs[k - 1][j];
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
        
        for (int k = 1; k < weights.length; k++) { // First layer has no weights
            for (int i = 0; i < weights[k].length; i++) {
                for (int j = 0; j < weights[k][i].length; j++) {
                    weights[k][i][j] = random.nextDouble() * RANDOM_INTERVAL + RANDOM_MIN;
                }
            }
        }
    }
    
    private void initializeOutputArrays(int nLayers) {
        outputs = new double[nLayers][];
        
        outputs[0] = new double[INPUT_SIZE]; // Input layer has input size
        
        for (int i = 1; i < OUTPUT_LAYER_INDEX; i++) { // Hidden layers have hidden layer size
            outputs[i] = new double[HIDDEN_LAYER_SIZE];
        }
        
        outputs[OUTPUT_LAYER_INDEX] = new double[POSSIBLE_OUTPUTS]; // Output layer has an specific number of possible outputs
    }
    
    private void initializeWeightArrays(int nLayers) {
        weights = new double[nLayers][][];
        
        weights[0] = null; // Input layer has no weights
        weights[1] = new double[HIDDEN_LAYER_SIZE][];
        for (int i = 0; i < weights[1].length; i++) {
            weights[1][i] = new double[INPUT_SIZE]; // First hidden layer has as weights as input size
        }
        
        for (int i = 1; i < nLayers; i++) {
            if (i == OUTPUT_LAYER_INDEX) { // Output layer could have a different size than hidden layers
                weights[i] = new double[POSSIBLE_OUTPUTS][]; 
            } else {
                weights[i] = new double[HIDDEN_LAYER_SIZE][];
            }
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = new double[HIDDEN_LAYER_SIZE];
            }
        }
    }
    
    private void initializeArrays(int nLayers) {
        initializeOutputArrays(nLayers);
        initializeWeightArrays(nLayers);
        
        //printArraysSize();
    }
    
    private void printArraysSize() {
        System.out.println("OUTPUT length: " + outputs.length);
        for (int i = 0; i < outputs.length; i++) {
            System.out.println("\tOUTPUT (" + i + ") length: " + outputs[i].length);
        }
        
        System.out.println("WEIGHT length: " + weights.length);
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] != null) {
                System.out.println("\tWEIGHT (" + i + ") length: " + weights[i].length);
                for (int j = 0; j < weights[i].length; j++) {
                    System.out.println("\t\tWEIGHT (" + i + "," + j + ") length: " + weights[i][j].length);
                }
            }
        }
    }
    
    private double sigmoid(double x) {
        return (1.0 / (1.0 + Math.exp(-x)));
    }
}
