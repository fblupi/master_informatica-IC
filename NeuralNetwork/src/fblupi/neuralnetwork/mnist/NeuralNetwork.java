package fblupi.neuralnetwork.mnist;

import java.util.Random;

/**
 * Neural Network for solving MNIST problem
 *
 * @author fblupi
 */
public class NeuralNetwork {

    private final int OUTPUT_ARRAY_SIZE = 3;
    private final int WEIGHT_ARRAY_SIZE = 2;
    private final int INPUT_LAYER_INDEX = 0;
    private final int HIDDEN_LAYER_INDEX = 1;
    private final int OUTPUT_LAYER_INDEX = 2;
    private final int HIDDEN_WEIGHT_INDEX = 0;
    private final int OUTPUT_WEIGHT_INDEX = 1;

    /**
     * Width and height size of the image
     */
    private final int IMAGE_SIZE = 28;

    /**
     * Size of the input layer = IMAGE_SIZE * IMAGE_SIZE
     */
    private final int INPUT_LAYER_SIZE = IMAGE_SIZE * IMAGE_SIZE;

    /**
     * Size of the hidden layer
     */
    private final int HIDDEN_LAYER_SIZE = 256;

    /**
     * Size of the output layer = 10. One neuron for each possible result
     */
    private final int OUTPUT_LAYER_SIZE = 10;

    /**
     * Minimum random number
     */
    private final double RANDOM_MIN = -.1;

    /**
     * Maximum random number
     */
    private final double RANDOM_MAX =  .1;

    /**
     * Learning rate used during back propagation
     */
    private final double LEARNING_RATE = .017;

    /**
     * Momentum value used during back propagation
     */
    private final double MOMENTUM = .9;

    /**
     * Outputs of each single neuron in each layer
     */
    private double[][] output;

    /**
     * Weights of every connection between neurons
     */
    private double[][][] weight;

    /**
     * Bias weight of every single neuron in hidden and output layers
     */
    private double[][] bias;

    /**
     * Error of every output neuron (target value - obtained value)
     */
    private double[] deltaOutput;

    /**
     * Error signal of every neuron in hidden and output layers
     */
    private double[][] delta;

    /**
     * Bias weight variation of every single neuron in hidden and output layers
     */
    private double[][] deltaBias;

    /**
     * Weight variation of every connection between neurons
     */
    private double[][][] deltaWeight;

    /**
     * Results obtained with the test images
     */
    private String results;

    /**
     * Sigmoid function as activation function
     *
     * @param x input value
     * @return number between 0 and 1
     */
    private double sigmoid(double x) {
        return (1.0 / (1.0 + Math.exp(-x)));
    }

    /**
     * Constructor: Initialize and assign values to the arrays
     */
    public NeuralNetwork() {
        initializeArrays();
        initializeArraysValues();
    }

    /**
     * Training with the images given as many times as specified with epochs value. Each 5
     * epochs shows the error with the test images
     *
     * @param trainingImages 3D array of 2D array of 28x28
     * @param trainingResults array with the label of each image
     * @param testImages 3D array of 2D array of 28x28
     * @param testResults array with the label of each image
     * @param epochs number of epochs made during the training
     */
    public void trainAndTest(float[][][] trainingImages, int[] trainingResults, int epochs, float[][][] testImages,
                             int[] testResults) {
        for (int e = 0; e < epochs; e++) {
            System.out.println("Epoch " + (e + 1) + "/" + epochs + "...");
            for (int i = 0; i < trainingImages.length; i++) {
                trainSingleImage(trainingImages[i], trainingResults[i]);
            }
            if ((e + 1) % 5 == 0) {
                System.out.println("Testing...");
                System.out.println("ERROR RATE: " + test(testImages, testResults) + "%");
            }
        }
    }

    /**
     * Training with the images given as many times as specified with epochs value
     *
     * @param images 3D array of 2D array of 28x28
     * @param results array with the label of each image
     * @param epochs number of epochs made during the training
     */
    public void train(float[][][] images, int[] results, int epochs) {
        for (int e = 0; e < epochs; e++) {
            System.out.println("Epoch " + (e + 1) + "/" + epochs + "...");
            for (int i = 0; i < images.length; i++) {
                trainSingleImage(images[i], results[i]);
            }
        }
    }

    /**
     * Training with a single image. First of all, read the image and update input layer of the neural network, then
     * uses feed forward propagation for obtain the result and finally uses back propagation for updating weights
     *
     * @param image 2D array of 28x28
     * @param result label of the image
     */
    private void trainSingleImage(float[][] image, int result) {
        addInputFromImage(image);
        feedForwardPropagation();
        backPropagation(result);
    }

    /**
     * Test the neural network with the images given and compare the results
     *
     * @param images 3D array of 2D array of 28x28
     * @param results array with the label of each image
     * @return percentage of error
     */
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

    /**
     * Test with a single image. First of all, read the image and update input layer of the neural network, then
     * uses feed forward propagation for obtain the result and finally compare the result with the expected number
     *
     * @param image 2D array of 28x28
     * @param result label of the image
     * @return the number obtained is or not the number expected
     */
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

    /**
     * Back propagation in the output layer. Update weights of the connections with other neurons of every neuron in the
     * output layer and their bias weight:
     *
     * error_i = (t_i - o_i) * o_i * (1 - o_i)
     *
     * deltaW_ij = LR * error_i * o_j + M * deltaW_ij
     * w_ij += deltaW_ij
     *
     * deltaB_i = LR * error_i + M * deltaB_i
     * b_ij += deltaB_i
     */
    private void backPropagationOutputLayer() {
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            delta[OUTPUT_WEIGHT_INDEX][i] = deltaOutput[i] * output[OUTPUT_LAYER_INDEX][i]
                    * (1 - output[OUTPUT_LAYER_INDEX][i]);

            for (int j = 0; j < HIDDEN_LAYER_SIZE; j++) {
                deltaWeight[OUTPUT_WEIGHT_INDEX][i][j] = (LEARNING_RATE * delta[OUTPUT_WEIGHT_INDEX][i]
                        * output[HIDDEN_LAYER_INDEX][j]) + (MOMENTUM * deltaWeight[OUTPUT_WEIGHT_INDEX][i][j]);

                weight[OUTPUT_WEIGHT_INDEX][i][j] += deltaWeight[OUTPUT_WEIGHT_INDEX][i][j];
            }

            deltaBias[OUTPUT_WEIGHT_INDEX][i] = (LEARNING_RATE * delta[OUTPUT_WEIGHT_INDEX][i])
                    + (MOMENTUM * deltaBias[OUTPUT_WEIGHT_INDEX][i]);

            bias[OUTPUT_WEIGHT_INDEX][i] += deltaBias[OUTPUT_WEIGHT_INDEX][i];
        }
    }

    /**
     * Back propagation in the hidden layer. Update weights of the connections with other neurons of every neuron in the
     * hidden layer and their bias weight:
     *
     * error_i = sum(j=0->outputSize)(errorOutput_j * wOutput_ji) * oHidden_i * (1 - oHidden_i)
     *
     * deltaW_ij = LR * error_i * o_j + M * deltaW_ij
     * w_ij += deltaW_ij
     *
     * deltaB_i = LR * error_i + M * deltaB_i
     * b_ij += deltaB_i
     */
    private void backPropagationHiddenLayer() {
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            double sum = 0;
            for (int j = 0; j < OUTPUT_LAYER_SIZE; j++) {
                sum += delta[OUTPUT_WEIGHT_INDEX][j] * weight[OUTPUT_WEIGHT_INDEX][j][i];
            }
            delta[HIDDEN_WEIGHT_INDEX][i] = sum * output[HIDDEN_LAYER_INDEX][i] * (1 - output[HIDDEN_LAYER_INDEX][i]);
            for (int j = 0; j < INPUT_LAYER_SIZE; j++) {
                deltaWeight[HIDDEN_WEIGHT_INDEX][i][j] = (LEARNING_RATE * delta[HIDDEN_WEIGHT_INDEX][i]
                        * output[INPUT_LAYER_INDEX][j]) + (MOMENTUM * deltaWeight[HIDDEN_WEIGHT_INDEX][i][j]);

                weight[HIDDEN_WEIGHT_INDEX][i][j] += deltaWeight[HIDDEN_WEIGHT_INDEX][i][j];
            }
            deltaBias[HIDDEN_WEIGHT_INDEX][i] = (LEARNING_RATE * delta[HIDDEN_WEIGHT_INDEX][i])
                    + (MOMENTUM * deltaBias[HIDDEN_WEIGHT_INDEX][i]);

            bias[HIDDEN_WEIGHT_INDEX][i] += deltaBias[HIDDEN_WEIGHT_INDEX][i];
        }
    }

    /**
     * Calculate error in each neuron of the output layer
     *
     * @param result label of the image
     */
    private void calculateDeltaOutput(int result) {
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            if (i == result) {
                deltaOutput[i] = 1.0 - output[OUTPUT_LAYER_INDEX][i];
            } else {
                deltaOutput[i] = 0.0 - output[OUTPUT_LAYER_INDEX][i];
            }
        }
    }

    /**
     * Full back propagation process: calculate error in output neurons, back propagation in output layer and back
     * propagation in hidden layer
     *
     * @param result label of the image
     */
    private void backPropagation(int result) {
        calculateDeltaOutput(result);
        backPropagationOutputLayer();
        backPropagationHiddenLayer();
    }

    /**
     * Feed forward propagation for obtaining results in output layers
     *
     * o_i = sigmoid(sum(j=0->prevLayerSize)(oPrevLayer_j * w_ij) + b_i)
     */
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

    /**
     * Update values of input layer with the values of every pixel of the image
     *
     * @param image 2D array of 28x28
     */
    private void addInputFromImage(float[][] image) {
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                output[0][i * IMAGE_SIZE + j] = image[i][j];
            }
        }
    }

    /**
     * Memory reservation for every array
     */
    private void initializeArrays() {
        initializeOutputArrays();
        initializeWeightArrays();
        initializeBiasArrays();
        initializeDeltaOutputArrays();
        initializeDeltaArrays();
        initializeDeltaWeightArrays();
        initializeDeltaBiasArrays();
    }

    /**
     * Initialize values of every array
     */
    private void initializeArraysValues() {
        initializeRandomValues();
        initializeDeltaValues();
    }

    /**
     * Initialize weight and bias arrays values to a random value between RANDOM_MIN and RANDOM_MAX
     */
    private void initializeRandomValues() {
        initializeWeightValues();
        initializeBiasValues();
    }

    /**
     * Initialize delta arrays to zeros
     */
    private void initializeDeltaValues() {
        initializeDeltaWeightValues();
        initializeDeltaBiasValues();
    }

    /**
     * Initialize weight array values to a random value between RANDOM_MIN and RANDOM_MAX
     */
    private void initializeWeightValues() {
        Random random = new Random();

        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) { // First layer has no weights
            for (int i = 0; i < weight[k].length; i++) {
                for (int j = 0; j < weight[k][i].length; j++) {
                    weight[k][i][j] = random.nextDouble() * (RANDOM_MAX - RANDOM_MIN) + RANDOM_MIN;
                }
            }
        }
    }

    /**
     * Initialize bias array values to a random value between RANDOM_MIN and RANDOM_MAX
     */
    private void initializeBiasValues() {
        Random random = new Random();

        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) {
            for (int i = 0; i < bias[k].length; i++) {
                bias[k][i] = random.nextDouble() * (RANDOM_MAX - RANDOM_MIN) + RANDOM_MIN;
            }
        }
    }

    /**
     * Initialize delta weight array values with zeros
     */
    private void initializeDeltaWeightValues() {
        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) {
            for (int i = 0; i < deltaWeight[k].length; i++) {
                for (int j = 0; j < deltaWeight[k][i].length; j++) {
                    deltaWeight[k][i][j] = .0;
                }
            }
        }
    }

    /**
     * Initialize delta bias array values with zeros
     */
    private void initializeDeltaBiasValues() {
        for (int k = 0; k < WEIGHT_ARRAY_SIZE; k++) {
            for (int i = 0; i < deltaBias[k].length; i++) {
                deltaBias[k][i] = .0;
            }
        }
    }

    /**
     * Memory reservation of weight 3D array:
     * - 1st dimension size = 2
     * - 2nd dimension size = hidden layer size for hidden layer and output layer size for output layer
     * - 3rd dimension size = input layer size for hidden neurons and hidden layer size for output neurons
     */
    private void initializeWeightArrays() {
        weight = new double[WEIGHT_ARRAY_SIZE][][];

        weight[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE][];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            weight[HIDDEN_WEIGHT_INDEX][i] = new double[INPUT_LAYER_SIZE];
        }

        weight[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE][];
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            weight[OUTPUT_WEIGHT_INDEX][i] = new double[HIDDEN_LAYER_SIZE];
        }
    }

    /**
     * Memory reservation of output 2D array:
     * - 1st dimension size = 3
     * - 2nd dimension size = input layer size for input layer, hidden layer size for hidden layer and output layer size
     *                        for output layer
     */
    private void initializeOutputArrays() {
        output = new double[OUTPUT_ARRAY_SIZE][]; // outputs for input, hidden and output layers

        output[INPUT_LAYER_INDEX] = new double[INPUT_LAYER_SIZE]; // input layer has input size
        output[HIDDEN_LAYER_INDEX] = new double[HIDDEN_LAYER_SIZE]; // hidden layer has hidden layer size
        output[OUTPUT_LAYER_INDEX] = new double[OUTPUT_LAYER_SIZE]; // Output layer has 10 possible outputs
    }

    /**
     * Memory reservation of bias 2D array:
     * - 1st dimension size = 2
     * - 2nd dimension size = hidden layer size for hidden layer and output layer size for output layer
     */
    private void initializeBiasArrays() {
        bias = new double[WEIGHT_ARRAY_SIZE][];

        bias[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
        bias[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE];
    }

    /**
     * Memory reservation of delta output 1D array with output layer size
     */
    private void initializeDeltaOutputArrays() {
        deltaOutput = new double[OUTPUT_LAYER_SIZE];
    }

    /**
     * Memory reservation of delta 2D array:
     * - 1st dimension size = 2
     * - 2nd dimension size = hidden layer size for hidden layer and output layer size for output layer
     */
    private void initializeDeltaArrays() {
        delta = new double[WEIGHT_ARRAY_SIZE][];

        delta[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
        delta[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE];
    }

    /**
     * Memory reservation of weight 3D array:
     * - 1st dimension size = 2
     * - 2nd dimension size = hidden layer size for hidden layer and output layer size for output layer
     * - 3rd dimension size = input layer size for hidden neurons and hidden layer size for output neurons
     */
    private void initializeDeltaWeightArrays() {
        deltaWeight = new double[WEIGHT_ARRAY_SIZE][][];

        deltaWeight[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE][];
        for (int i = 0; i < HIDDEN_LAYER_SIZE; i++) {
            deltaWeight[HIDDEN_WEIGHT_INDEX][i] = new double[INPUT_LAYER_SIZE];
        }

        deltaWeight[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE][];
        for (int i = 0; i < OUTPUT_LAYER_SIZE; i++) {
            deltaWeight[OUTPUT_WEIGHT_INDEX][i] = new double[HIDDEN_LAYER_SIZE];
        }
    }

    /**
     * Memory reservation of delta bias 2D array:
     * - 1st dimension size = 2
     * - 2nd dimension size = hidden layer size for hidden layer and output layer size for output layer
     */
    private void initializeDeltaBiasArrays() {
        deltaBias = new double[WEIGHT_ARRAY_SIZE][];

        deltaBias[HIDDEN_WEIGHT_INDEX] = new double[HIDDEN_LAYER_SIZE];
        deltaBias[OUTPUT_WEIGHT_INDEX] = new double[OUTPUT_LAYER_SIZE];
    }

    /**
     * String with all the results obtained with the test images
     *
     * @return string with all the results obtained one after another
     */
    public String getResults() {
        return results;
    }

    /**
     * Get weight 3D array with the weights of every connection between neurons of the neural network
     *
     * @return 3D array with weights
     */
    public double[][][] getWeight() {
        return weight;
    }

    /**
     * Get bias 2D array with the weights of the bias of each neuron of the neural network
     *
     * @return 2D array with weights
     */
    public double[][] getBias() {
        return bias;
    }

    /**
     * Set weights of every connection between neurons of the neural network
     *
     * @param weight 3D array with weights
     */
    public void setWeight(double[][][] weight) {
        for (int k = 0; k < this.weight.length; k++) {
            for (int i = 0; i < this.weight[k].length; i++) {
                System.arraycopy(weight[k][i], 0, this.weight[k][i], 0, this.weight[k][i].length);
            }
        }
    }

    /**
     * Set weights of the bias of each neuron of the neural network
     *
     * @param bias 2D array with weights
     */
    public void setBias(double[][] bias) {
        for (int k = 0; k < this.bias.length; k++) {
            System.arraycopy(bias[k], 0, this.bias[k], 0, this.bias[k].length);
        }
    }

    /**
     * Get minimum value for randomly initializing weights values
     *
     * @return minimum value
     */
    public double getRandomMin() {
        return RANDOM_MIN;
    }

    /**
     * Get maximum value for randomly initializing weights values
     *
     * @return maximum value
     */
    public double getRandomMax() {
        return RANDOM_MAX;
    }

    /**
     * Get learning rate used during training
     *
     * @return learning rate
     */
    public double getLearningRate() {
        return LEARNING_RATE;
    }

    /**
     * Get momentum used during training
     *
     * @return learning rate
     */
    public double getMomentum() {
        return MOMENTUM;
    }

    /**
     * Get number of hidden layers used by the neural network
     *
     * @return number of hidden layers
     */
    public int getHiddenLayerSize() {
        return HIDDEN_LAYER_SIZE;
    }

}
