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
public class Neurona {
    private ArrayList<Neurona> listaSinapsis;
    private ArrayList<Double> pesos;
    private double valor;
    private double umbral;
    
    public Neurona() {
        listaSinapsis = new ArrayList<>();
        pesos = new ArrayList<>();
        Random random = new Random();
        umbral = random.nextDouble();
    }
    
    private double sigmoide(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    public void calcularValor() {
        double sumatoria = 0;
        
        for (int i = 0; i < listaSinapsis.size(); i++) {
            sumatoria += listaSinapsis.get(i).getValor() * pesos.get(i);
        }
        
        sumatoria -= umbral;
        
        valor = sigmoide(sumatoria);
        
        //System.out.println(valor);
    }
    
    public void addSinapsis(Neurona neurona, double peso) {
        listaSinapsis.add(neurona);
        pesos.add(peso);
    }
    
    public Neurona getSinapsis(int i) {
        return listaSinapsis.get(i);
    }
    
    public double getPeso(int i) {
        return pesos.get(i);
    }
    
    public void setPeso(int i, double peso) {
        pesos.set(i, peso);
    }
    
    public double getUmbral() {
        return umbral;
    }

    public double getValor() {
        return valor;
    }

    public void setUmbral(double umbral) {
        this.umbral = umbral;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
