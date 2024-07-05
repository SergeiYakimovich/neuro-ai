package org.example.neuroph;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@Data
@AllArgsConstructor
public class MyNeuroph implements LearningEventListener {
    private static final String FILE_NAME = "src/main/resources/MyNeuralNet.nnet";
    private int inputsCount;
    private int outputsCount;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Neural Network
//        NeuralNetwork network = handleAssembleNeuralNetwork();
//        network = trainXOR(network);
//        testNeuralNetwork(network, 0, 0);
//        testNeuralNetwork(network, 0, 1);
//        testNeuralNetwork(network, 1, 0);
//        testNeuralNetwork(network, 1, 1);

//        MyNeuroph myNeuroph = new MyNeuroph(2, 1);
//        myNeuroph.run();

        MyNeuroph myNeuroph = new MyNeuroph(2, 1);
        NeuralNetwork neuralNetwork = MultiLayerPerceptron.createFromFile(FILE_NAME);
        DataSet dataSet = myNeuroph.getDataSet(2, 1);
        myNeuroph.testNeuralNetwork(neuralNetwork, dataSet);

    }

    public void run() {
        System.out.println("Creating training set...");
        DataSet dataSet = getDataSet(inputsCount, outputsCount);

        System.out.println("Creating neural network...");
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(inputsCount, 10, 10, 10,outputsCount);

        // attach listener to learning rule
        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
        learningRule.addListener(this);

        // set learning rate and max error
        learningRule.setLearningRate(0.2);
        learningRule.setMaxError(0.000001);

        System.out.println("Training network...");
        neuralNet.learn(dataSet);

        System.out.println("Training completed.");
        System.out.println("Testing network...");

        testNeuralNetwork(neuralNet, dataSet);

        System.out.println("Saving network");
        neuralNet.save(FILE_NAME);

        System.out.println("Done");
    }

    public void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {
        double diffSum = 0;
        double diffMax = 0;
//        double diffMaxInPercent = 0;
        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();

            System.out.print("Input: " + Arrays.toString(testSetRow.getInput()));
            System.out.print(" Wait: " + testSetRow.getDesiredOutput()[0]);
            System.out.print(" Output: " + neuralNet.getOutput()[0]);

            double diff = Math.abs(testSetRow.getDesiredOutput()[0] - neuralNet.getOutput()[0]);
            System.out.println(" Diff: " + diff);
            diffSum += diff;
            diffMax = Math.max(diffMax, diff);
//            diffMaxInPercent = Math.max(diffMaxInPercent, absDiff / wait * 100);
        }
        System.out.println("Diff average: " + diffSum / testSet.getRows().size());
        System.out.println("Diff max: " + diffMax);
//        System.out.println("Diff max in percent: " + diffMaxInPercent);
    }

    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
    }

    public DataSet getDataSet(int inputCount, int outputCount) {
//        String trainingSetFileName = "data_sets/forest_fires_data.txt";
        // create training set from file
//        DataSet dataSet = DataSet.createFromFile(trainingSetFileName, inputsCount, outputsCount, ",", false);

        DataSet ds = new DataSet(inputCount, outputCount);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            double d1 = random.nextDouble();
            double d2 = random.nextDouble();
            double result = testFunction(d1, d2);
            DataSetRow dataSetRow = new DataSetRow(new double[] { d1, d2 }, new double[] { result });
            ds.addRow(dataSetRow);
        }

        return ds;
    }

    private double testFunction(double x, double y) {
        return x * y;
    }

}
