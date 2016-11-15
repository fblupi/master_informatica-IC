package sandbox.mnist;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import neuralNetwork.*;

/**
 * MNIST database utilities.
 * 
 * @author Fernando Berzal (berzal@acm.org)
 */
public class MNISTDatabase {
    // MNIST URL
    private static final String MNIST_URL = "http://yann.lecun.com/exdb/mnist/";

    // Training data
    private static final String TRAINING_IMAGES = "train-images-idx3-ubyte.gz";
    private static final String TRAINING_LABELS = "train-labels-idx1-ubyte.gz";

    // Test data
    private static final String TEST_IMAGES = "t10k-images-idx3-ubyte.gz";
    private static final String TEST_LABELS = "t10k-labels-idx1-ubyte.gz";

    // Logger
    protected static final Logger LOG = Logger.getLogger(MNISTDatabase.class.getName());

    // Download files

    /**
     * Download URL to file using Java NIO.
     * 
     * @param urlString Source URL
     * @param filename Destination file name
     * @throws java.io.IOException
     */
    public static void download (String urlString, String filename) throws IOException {
        URL url = new URL(urlString);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(filename);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    /**
     * Download MNIST database.
     * 
     * @param directory Destination folder/directory.
     * @throws IOException
     */
    public static void downloadMNIST (String directory) throws IOException {
        File baseDir = new File(directory);

        if (!(baseDir.isDirectory() || baseDir.mkdir())) {
            throw new IOException("Unable to create destination folder " + baseDir);
        }

        LOG.info("Downloading MNIST database...");

        download(MNIST_URL + TRAINING_IMAGES, directory + TRAINING_IMAGES);
        download(MNIST_URL + TRAINING_LABELS, directory + TRAINING_LABELS);
        download(MNIST_URL + TEST_IMAGES, directory + TEST_IMAGES);
        download(MNIST_URL + TEST_LABELS, directory + TEST_LABELS);

        LOG.info("MNIST database downloaded into " + directory);
    }

    // Read data from files

    /**
     * Read MNIST image data.
     * 
     * @param filename File name
     * @return 3D int array
     * @throws IOException
     */
    public static int[][][] readImages (String filename) throws IOException {
        FileInputStream file = null;
        InputStream gzip = null;
        DataInputStream data = null;
        int images[][][] = null;
        try {
            file = new FileInputStream(filename);
            gzip = new GZIPInputStream(file);
            data = new DataInputStream(gzip);

            LOG.info("Reading MNIST data...");

            int magicNumber = data.readInt();

            if (magicNumber != 2051) // 0x00000801 == 08 (unsigned byte) + 03 (3D tensor, i.e. multiple 2D images)
                throw new IOException("Error while reading MNIST data from " + filename);

            int size = data.readInt();
            int rows = data.readInt();
            int columns = data.readInt();

            images = new int[size][rows][columns];

            LOG.info("Reading " + size + " " + rows + "x" + columns + " images...");

            for (int i = 0; i < size; i++)
                for (int j = 0; j < rows; j++)
                    for (int k = 0; k < columns; k++)
                        images[i][j][k] = data.readUnsignedByte();

            LOG.info("MNIST images read from " + filename);
        } finally {
            if (data != null)
                data.close();
            if (gzip != null)
                gzip.close();
            if (file != null)
                file.close();
        }

        return images;
    }

    /**
     * Read MNIST labels
     * 
     * @param filename File name
     * @return Label array
     * @throws IOException
     */
    public static int[] readLabels (String filename) throws IOException {
        FileInputStream file = null;
        InputStream gzip = null;
        DataInputStream data = null;
        int labels[] = null;

        try {
            file = new FileInputStream(filename);
            gzip = new GZIPInputStream(file);
            data = new DataInputStream(gzip);

            LOG.info("Reading MNIST labels...");

            int magicNumber = data.readInt();

            if (magicNumber != 2049) // 0x00000801 == 08 (unsigned byte) + 01 (vector)
                throw new IOException("Error while reading MNIST labels from " + filename);

            int size = data.readInt();

            labels = new int[size];

            for (int i = 0; i < size; i++)
                labels[i] = data.readUnsignedByte();

            LOG.info("MNIST labels read from " + filename);
        } finally {
            if (data != null)
                data.close();
            if (gzip != null)
                gzip.close();
            if (file != null)
                file.close();
        }

        return labels;
    }

    /**
     * Normalize raw image data, i.e. convert to floating-point and rescale to [0,1].
     * 
     * @param image Raw image data
     * @return Floating-point 2D array
     */
    public static float[][] normalize (int image[][]) {
        int rows = image.length;
        int columns = image[0].length;
        float data[][] = new float[rows][columns];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < rows; j++)
                data[i][j] = (float)image[i][j] / 255f;

        return data;
    }

    // Standard I/O

    public static String toString (int label) {
        return Integer.toString(label);
    }

    public static String toString (int image[][]) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                String hex = Integer.toHexString(image[i][j]);
                if (hex.length() == 1) 
                    builder.append("0");
                builder.append(hex);
                builder.append(' ');				
            }
            builder.append('\n');
        }

        return builder.toString();
    }

    public static String toString (float image[][]) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                builder.append(String.format(Locale.US, "%.3f ", image[i][j]));
            }
            builder.append('\n');
        }

        return builder.toString();
    }

    // Test program

    public static void main (String[] args) throws IOException {
        // downloadMNIST("data/mnist/");

        int[][][] trainingImages, testImages;
        trainingImages = readImages("data/mnist/" + TRAINING_IMAGES);
        testImages = readImages("data/mnist/" + TEST_IMAGES);

        int[] trainingLabels, testLabels;
        trainingLabels = readLabels("data/mnist/" + TRAINING_LABELS);
        testLabels = readLabels("data/mnist/" + TEST_LABELS);
        
        BasicNeuralNetwork neuralNetwork = new BasicNeuralNetwork();
        neuralNetwork.train(trainingImages, trainingLabels);
        neuralNetwork.test(testImages);
        
        System.out.println("Error rate: " + neuralNetwork.getErrorRate(testLabels) + "%");

        //System.out.println("Raw image data:");
        //System.out.println(toString(trainingImages[0]));

        // Normalize image data
        //float[][] trainingData = normalize(trainingImages[0]);

        //System.out.println("Normalized image:");
        //System.out.println(toString(trainingData));

        //System.out.println("Image label:");
        //System.out.println(trainingLabels[0]);
    }

}
