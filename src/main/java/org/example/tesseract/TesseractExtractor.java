package org.example.tesseract;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.util.logging.Logger;

public class TesseractExtractor {
    private static final Logger log = Logger.getLogger(TesseractExtractor.class.getName());

    public static void main(String[] args) {
        String result = recognizeText("src/main/resources/2345.jpg");
        System.out.println(result);
    }

    public static String recognizeText(String imagePath) {
        log.info("Recognizing text from image: " + imagePath);
        String result;

        try {
            File image = new File(imagePath);
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("src/main/resources/tessdata");
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED);

            result = tesseract.doOCR(image);
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
