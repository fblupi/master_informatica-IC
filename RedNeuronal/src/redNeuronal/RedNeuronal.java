/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redNeuronal;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author fboli
 */
public class RedNeuronal {
    private final int TAMA_IMAGEN = 28;
    private final int NUM_ENTRADAS = TAMA_IMAGEN * TAMA_IMAGEN;    
    private final double RATIO_APRENDIZAJE = 0.25;
    
    private Random random;
    private ArrayList<Neurona> capaEntrada;
    private ArrayList<Neurona> capaOculta;
    private Neurona neuronaSalida;
    
    public RedNeuronal() {
        random = new Random();
        capaEntrada = new ArrayList<>(NUM_ENTRADAS);
        capaOculta = new ArrayList<>(NUM_ENTRADAS);
        
        for (int i = 0; i < NUM_ENTRADAS; i++) {
            Neurona neurona = new Neurona();
            capaEntrada.add(neurona);
        }
        
        for (int i = 0; i < NUM_ENTRADAS; i++) {
            Neurona neurona = new Neurona();
            for (int j = 0; j < NUM_ENTRADAS; j++) {
                neurona.addSinapsis(capaEntrada.get(j), random.nextDouble());
            }
            capaOculta.add(neurona);
        }
        
        neuronaSalida = new Neurona();
        for (int i = 0; i < NUM_ENTRADAS; i++) {
            neuronaSalida.addSinapsis(capaOculta.get(i), random.nextDouble());
        }
    }
    
    public float testImagenes(float[][][] imagenes, int[] resultados) {
        int error = 0;
        
        for (int i = 0; i < imagenes.length; i++) {
            System.out.println("Evaluando " + (i + 1) + "/" + imagenes.length);
            if (!testImagen(imagenes[i], resultados[i])) {
                error++;
            }
        }
        
        return (float) error / imagenes.length * 100;
    }
    
    public boolean testImagen(float[][] imagen, int resultado) {
        for (int i = 0; i < TAMA_IMAGEN; i++) {
            for (int j = 0; j < TAMA_IMAGEN; j++) {
                capaEntrada.get(i * TAMA_IMAGEN + j).setValor(imagen[i][j]);
            }
        }
        
        for (int i = 0; i < NUM_ENTRADAS; i++) {
            capaOculta.get(i).calcularValor();
        }
        neuronaSalida.calcularValor();
        double miResultado = neuronaSalida.getValor();
        System.out.println("Mi resultado: " + miResultado + " vs. " + resultado);
        return resultado == (int) miResultado;
    }
    
    public void entrenarImagenes(float[][][] imagenes, int[] resultados) {
        for (int i = 0; i < imagenes.length; i++) {
            System.out.println("Entrenando " + (i + 1) + "/" + imagenes.length);
            entrenarImagen(imagenes[i], resultados[i]);
        }
    }
    
    public void entrenarImagen(float[][] imagen, int resultado) {
        for (int i = 0; i < TAMA_IMAGEN; i++) {
            for (int j = 0; j < TAMA_IMAGEN; j++) {
                capaEntrada.get(i * TAMA_IMAGEN + j).setValor(imagen[i][j]);
            }
        }
        
        for (int i = 0; i < NUM_ENTRADAS; i++) {
            capaOculta.get(i).calcularValor();
        }
        neuronaSalida.calcularValor();
        
        double error = (double) resultado - neuronaSalida.getValor();
        //System.out.println("RESULTADO ESPERADO: " + resultado + ", ERROR: " + error);
        
        backpropagation(neuronaSalida, error);
        for (Neurona neurona: capaOculta) {
            backpropagation(neurona, error);
        }
    }
    
    private void backpropagation(Neurona neurona, double error) {
        for (int i = 0; i < NUM_ENTRADAS; i++) {
            double salida = neurona.getValor();
            double pesoOld = neurona.getPeso(i);
            double salidaOrigen = neurona.getSinapsis(i).getValor();
            double variacion = RATIO_APRENDIZAJE * error * salidaOrigen * salida * (1 - salida);
            neurona.setPeso(i, pesoOld + variacion);
            neurona.setUmbral(neurona.getUmbral() + variacion);
        }
    }
}
