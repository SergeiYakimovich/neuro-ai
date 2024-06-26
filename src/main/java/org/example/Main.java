package org.example;

import org.neuroph.core.NeuralNetwork;

import java.util.Arrays;

import static org.example.neuroph.NeurophXOR.*;

public class Main {
    public static void main(String[] args) {
        NeuralNetwork network = assembleNeuralNetwork();
        network = trainNeuralNetwork(network);

        testNeuralNetwork(network, 0, 0);
        testNeuralNetwork(network, 0, 1);
        testNeuralNetwork(network, 1, 0);
        testNeuralNetwork(network, 1, 1);
    }

}