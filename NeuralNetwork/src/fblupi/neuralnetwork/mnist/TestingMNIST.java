package fblupi.neuralnetwork.mnist;

import org.json.simple.parser.ParseException;
import sandbox.mnist.MNISTDatabase;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Test images from MNIST
 *
 * @author fblupi
 */
public class TestingMNIST {

    // Training data
    private static final String TRAINING_IMAGES = "train-images-idx3-ubyte.gz";
    private static final String TRAINING_LABELS = "train-labels-idx1-ubyte.gz";

    // Test data
    private static final String TEST_IMAGES = "t10k-images-idx3-ubyte.gz";
    private static final String TEST_LABELS = "t10k-labels-idx1-ubyte.gz";

    public static void main (String[] args) throws IOException, ParseException {
        boolean train = false;
        int epochs = 50;
        boolean getPreviousWeights = true;
        float previousWeightsError = 1.68f;
        int[][][] trainingImages, testImages;
        float[][][] trainingImagesNormalized, testImagesNormalized;
        int[] trainingLabels, testLabels;

//        System.out.println("Downloading images...");
//        MNISTDatabase.downloadMNIST("data/mnist/");

        System.out.println("Reading images...");

        trainingImages = MNISTDatabase.readImages("data/mnist/" + TRAINING_IMAGES);
        testImages = MNISTDatabase.readImages("data/mnist/" + TEST_IMAGES);
        trainingImagesNormalized = new float[trainingImages.length][28][28];
        testImagesNormalized = new float[testImages.length][28][28];

        System.out.println("Normalizing...");

        for (int i = 0; i < trainingImages.length; i++) {
            trainingImagesNormalized[i] = MNISTDatabase.normalize(trainingImages[i]);
        }

        for (int i = 0; i < testImages.length; i++) {
            testImagesNormalized[i] = MNISTDatabase.normalize(testImages[i]);
        }

        System.out.println("Reading labels...");

        trainingLabels = MNISTDatabase.readLabels("data/mnist/" + TRAINING_LABELS);
        testLabels = MNISTDatabase.readLabels("data/mnist/" + TEST_LABELS);

        System.out.println("Creating neural network...");

        NeuralNetwork nn = new NeuralNetwork();

        if (getPreviousWeights) {
            System.out.println("Reading weights...");

            double[][][] weight = JSON.readWeightFile("data/results/weights-" + previousWeightsError + ".json");
            double[][] bias = JSON.readBiasWeightFile("data/results/bias-" + previousWeightsError + ".json");

            nn.setWeight(weight);
            nn.setBias(bias);
        }

        if (train) {
            System.out.println("Training...");

            nn.trainAndTest(trainingImagesNormalized, trainingLabels, epochs, testImagesNormalized, testLabels);
        }

        System.out.println("Testing...");

        float errorRate = nn.test(testImagesNormalized, testLabels);
        System.out.println("ERROR RATE: " + errorRate + "%");

        FileWriter file = new FileWriter("data/results/test-" + errorRate + ".txt");
        String results = "NEURON DATA:\n";
        results += "Random min: " + nn.getRandomMin() + "\n";
        results += "Random max: " + nn.getRandomMax() + "\n";
        results += "Learning rate: " + nn.getLearningRate() + "\n";
        results += "Momentum: " + nn.getMomentum() + "\n";
        results += "Number of hidden layers: " + nn.getHiddenLayerSize() + "\n";
        results += "RESULTS:\n";
        results += nn.getResults();
        file.write(results);

        JSON.writeWeightFile("data/results/weights-" + errorRate + ".json", nn.getWeight());
        JSON.writeBiasWeightFile("data/results/bias-" + errorRate + ".json", nn.getBias());

        System.out.println("Writing result...");
    }

}
