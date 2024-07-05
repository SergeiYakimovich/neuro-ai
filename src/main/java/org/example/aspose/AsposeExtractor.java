package org.example.aspose;

import com.aspose.ocr.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AsposeExtractor {
    private static final Logger log = Logger.getLogger(AsposeExtractor.class.getName());

    public static void main(String[] args) {
        String result = recognizeText("src/main/resources/7890.jpg");
        System.out.println(result);
    }

    public static String recognizeText(String imagePath) {
        log.info("Recognizing text from image: " + imagePath);
        String result;
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
//            License.setLicense("Aspose.OCR.lic");
            AsposeOCR asposeOCR = new AsposeOCR();
            OcrInput ocrInput = new OcrInput(InputType.SingleImage);
            ocrInput.add(image);

            ArrayList<RecognitionResult> recognitionResults = asposeOCR.RecognizeHandwrittenText(ocrInput);

            result = recognitionResults.stream()
                    .map(x -> x.recognitionText)
                    .collect(Collectors.joining(" "));
            if (result.isBlank()) {
                result = "Не удалось определить изображение";
            }
            log.info("Recognized text from image: " + imagePath);
        } catch (Exception e) {
            result = "Error while recognizing text from image: " + imagePath;
            log.info( result + e.getMessage());
        }

        return result;
    }
}
