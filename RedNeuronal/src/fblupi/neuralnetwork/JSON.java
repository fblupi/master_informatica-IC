/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fblupi.neuralnetwork;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author fboli
 */
public class JSON {
    public static void writeWeightFile(String filename, double[][][] weight) throws IOException {
        JSONObject obj = new JSONObject();
        
        for (int k = 0; k < weight.length; k++) {
            JSONObject layer = new JSONObject();
            for (int i = 0; i < weight[k].length; i++) {
                JSONObject neuron = new JSONObject();
                for (int j = 0; j < weight[k][i].length; j++) {
                    neuron.put(j, weight[k][i][j]);
                }
                layer.put(i, neuron);
            }
            obj.put(k, layer);
        }
        
        FileWriter file = new FileWriter(filename);
        file.write(obj.toJSONString());
    }
    
    public static void writeBiasWeightFile(String filename, double[][] bias) throws IOException {
        JSONObject obj = new JSONObject();
        
        for (int k = 0; k < bias.length; k++) {
            JSONObject layer = new JSONObject();
            for (int i = 0; i < bias[k].length; i++) {
                layer.put(i, bias[k][i]);
            }
            obj.put(k, layer);
        }
        
        FileWriter file = new FileWriter(filename);
        file.write(obj.toJSONString());
    }
    
    public static double[][][] readWeightFile(String filename) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        double[][][] weight;
        
        JSONObject obj = (JSONObject) parser.parse(new FileReader(filename));
        weight = new double[obj.size()][][];
        
        JSONObject hidden = (JSONObject) obj.get(0);
        weight[0] = new double[hidden.size()][];
        
        for (int i = 0; i < hidden.size(); i++) {
            JSONObject neuron = (JSONObject) hidden.get(i);
            weight[0][i] = new double[neuron.size()];
            for (int j = 0; j < neuron.size(); j++) {
                weight[0][i][j] = (double) neuron.get(j);
            }
        }
        
        JSONObject output = (JSONObject) obj.get(1);
        weight[1] = new double[output.size()][];
        
        for (int i = 0; i < output.size(); i++) {
            JSONObject neuron = (JSONObject) output.get(i);
            weight[1][i] = new double[neuron.size()];
            for (int j = 0; j < neuron.size(); j++) {
                weight[1][i][j] = (double) neuron.get(j);
            }
        }
        
        return weight;
    }
    
    public static double[][] readBiasWeightFile(String filename) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        double[][] bias;
        
        JSONObject obj = (JSONObject) parser.parse(new FileReader(filename));
        bias = new double[obj.size()][];
        
        JSONObject hidden = (JSONObject) obj.get(0);
        bias[0] = new double[hidden.size()];
        
        for (int i = 0; i < hidden.size(); i++) {
            JSONObject neuron = (JSONObject) hidden.get(i);
            bias[0][i] = (double) neuron.get(i);
        }
        
        JSONObject output = (JSONObject) obj.get(1);
        bias[1] = new double[output.size()];
        
        for (int i = 0; i < output.size(); i++) {
            JSONObject neuron = (JSONObject) output.get(i);
            bias[1][i] = (double) neuron.get(i);
        }
        
        return bias;
    }
}
