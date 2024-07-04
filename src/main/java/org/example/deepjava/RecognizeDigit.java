package org.example.deepjava;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.example.deepjava.TrainMnist.OUTPUT_DIR;

public class RecognizeDigit {
    public static void main(String[] args) throws IOException, TranslateException, MalformedModelException {
//        var img = ImageFactory.getInstance().fromFile(Path.of("src/main/resources/5.jpg"));
//        var img = ImageFactory.getInstance().fromUrl("https://resources.djl.ai/images/0.png");

        BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/7.png"));
        BufferedImage resizeImage = resizeImage(bufferedImage, 28, 28);
        Image img = ImageFactory.getInstance().fromImage(resizeImage);
        img.getWrappedImage();

        Predictor<Image, Classifications> predictor = getPredictor();

        var classifications = predictor.predict(img);
        System.out.println(classifications);
        Optional<Classifications.Classification> max = classifications.items().stream()
                .max(Comparator.comparingDouble(Classifications.Classification::getProbability));
        System.out.println(max.get().getClassName());
    }

    public static Predictor<Image, Classifications> getPredictor() throws MalformedModelException, IOException {
        Model model = Model.newInstance("mlp");
        model.setBlock(new Mlp(28 * 28, 10, new int[]{128, 64}));
        model.load(Paths.get(OUTPUT_DIR));
        System.out.println(model);

        Translator<Image, Classifications> translator = new Translator<Image, Classifications>() {
            @Override
            public NDList processInput(TranslatorContext ctx, Image input) {
                // Convert Image to NDArray
                NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.GRAYSCALE);
                return new NDList(NDImageUtils.toTensor(array));
            }

            @Override
            public Classifications processOutput(TranslatorContext ctx, NDList list) {
                // Create a Classifications with the output probabilities
                NDArray probabilities = list.singletonOrThrow().softmax(0);
                List<String> classNames = IntStream.range(0, 10).mapToObj(String::valueOf).collect(Collectors.toList());
                return new Classifications(classNames, probabilities);
            }

            @Override
            public Batchifier getBatchifier() {
                // The Batchifier describes how to combine a batch together
                // Stacking, the most common batchifier, takes N [X1, X2, ...] arrays to a single [N, X1, X2, ...] array
                return Batchifier.STACK;
            }
        };
        Predictor<Image, Classifications> predictor = model.newPredictor(translator);
        return predictor;
    }

    static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

}