package org.example;

import org.example.neuroph.MyNeuroph;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.util.Random;

import static org.example.neuroph.BinaryOperationsNeuroph.*;

public class Main {
    public static void main(String[] args) {
//        NeuralNetwork network = handleAssembleNeuralNetwork();
//        network = trainXOR(network);
//        testNeuralNetwork(network, 0, 0);
//        testNeuralNetwork(network, 0, 1);
//        testNeuralNetwork(network, 1, 0);
//        testNeuralNetwork(network, 1, 1);

        MyNeuroph myNeuroph = new MyNeuroph(2, 1);
//        myNeuroph.run();

        NeuralNetwork neuralNetwork = MultiLayerPerceptron.createFromFile("MyNeuralNet.nnet");
        DataSet dataSet = myNeuroph.getDataSet(2, 1);
        myNeuroph.testNeuralNetwork(neuralNetwork, dataSet);

    }

}