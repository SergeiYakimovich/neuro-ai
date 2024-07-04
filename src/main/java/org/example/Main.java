package org.example;

import org.example.tgbot.BotInitializer;
import org.example.tgbot.TelegramBot;

import java.io.IOException;

public class Main {
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

//        NeuralNetwork neuralNetwork = MultiLayerPerceptron.createFromFile("MyNeuralNet.nnet");
//        DataSet dataSet = myNeuroph.getDataSet(2, 1);
//        myNeuroph.testNeuralNetwork(neuralNetwork, dataSet);


        //TG Bot
        new BotInitializer(new TelegramBot()).init();

        // Yandex GPT
//        String result = YandexGptSender.sendMessage("Переведи текст","To be, or not to be: that is the question.");
//        System.out.println(result);

    }

}