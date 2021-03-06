/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fblupi.neuralnetwork.mnist;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Export/import weights arrays to/from JSON
 *
 * @author fblupi
 * @author Jogua
 */
public class JSON {

    /**
     * Export 3D array with weights to JSON file
     *
     * @param filename JSON file name
     * @param weight 3D array with weights
     * @throws IOException cannot export file
     */
    public static void writeWeightFile(String filename, double[][][] weight) throws IOException {
        FileWriter file = new FileWriter(filename);
        file.write("\n[");
        for (int k = 0; k < weight.length; k++) {
            if (k > 0) {
                file.write(',');
            }
            file.write("\n\t[");
            for (int i = 0; i < weight[k].length; i++) {
                if (i > 0) {
                    file.write(',');
                }
                file.write("\n\t\t[\n\t\t\t");
                for (int j = 0; j < weight[k][i].length; j++) {
                    if (j > 0) {
                        file.write(',');
                    }
                    file.write(String.valueOf(weight[k][i][j]));
                }
                file.write("\n\t\t]");
                file.flush();
            }
            file.write("\n\t]");
        }
        file.write("\n]");
        file.flush();
    }

    /**
     * Export 2D array with bias weights to JSON file
     *
     * @param filename JSON file name
     * @param bias 3D array with bias weights
     * @throws IOException cannot export file
     */
    public static void writeBiasWeightFile(String filename, double[][] bias) throws IOException {
        FileWriter file = new FileWriter(filename);
        file.write("\n[");
        for (int k = 0; k < bias.length; k++) {
            if (k > 0) {
                file.write(',');
            }
            file.write("\n\t[");
            for (int i = 0; i < bias[k].length; i++) {
                if (i > 0) {
                    file.write(',');
                }
                file.write(String.valueOf(bias[k][i]));
                file.flush();
            }
            file.write("\n\t]");
        }
        file.write("\n]");
        file.flush();
    }

    /**
     * Import 3D array with weights from JSON file
     *
     * @param filename JSON file name
     * @return weight 3D array with weights
     * @throws IOException cannot import file
     * @throws ParseException cannot parse file content to JSON
     */
    public static double[][][] readWeightFile(String filename) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        double[][][] weight;
        
        JSONArray array = (JSONArray) parser.parse(new FileReader(filename));
        weight = new double[array.size()][][];
        
        JSONArray hidden = (JSONArray) array.get(0);
        weight[0] = new double[hidden.size()][];
        
        for (int i = 0; i < hidden.size(); i++) {
            JSONArray neuron = (JSONArray) hidden.get(i);
            weight[0][i] = new double[neuron.size()];
            for (int j = 0; j < neuron.size(); j++) {
                weight[0][i][j] = (double) neuron.get(j);
            }
        }
        
        JSONArray output = (JSONArray) array.get(1);
        weight[1] = new double[output.size()][];
        
        for (int i = 0; i < output.size(); i++) {
            JSONArray neuron = (JSONArray) output.get(i);
            weight[1][i] = new double[neuron.size()];
            for (int j = 0; j < neuron.size(); j++) {
                weight[1][i][j] = (double) neuron.get(j);
            }
        }
        
        return weight;
    }

    /**
     * Import 2D array with bias weights from JSON file
     *
     * @param filename JSON file name
     * @return weight 2D array with bias weights
     * @throws IOException cannot import file
     * @throws ParseException cannot parse file content to JSON
     */
    public static double[][] readBiasWeightFile(String filename) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        double[][] bias;
        
        JSONArray array = (JSONArray) parser.parse(new FileReader(filename));
        
        bias = new double[array.size()][];
        
        JSONArray hidden = (JSONArray) array.get(0);
        bias[0] = new double[hidden.size()];
        
        for (int i = 0; i < hidden.size(); i++) {
            bias[0][i] = (double) hidden.get(i);
        }
        
        JSONArray output = (JSONArray) array.get(1);
        bias[1] = new double[output.size()];
        
        for (int i = 0; i < output.size(); i++) {
            bias[1][i] = (double) output.get(i);
        }
        
        return bias;
    }

}
