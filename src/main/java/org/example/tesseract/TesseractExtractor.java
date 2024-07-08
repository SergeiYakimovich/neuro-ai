package org.example.tesseract;

import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TesseractExtractor {
    private static final Logger log = Logger.getLogger(TesseractExtractor.class.getName());
    private static final String TESSERACT_DATA_PATH = "tessdata";
    public static final String LANGUAGE = "rus";
    private static final String TYPE_WRITTEN_FILES_PATH = "src/main/resources/typewritten/";
    public static final String HAND_WRITTEN_FILES_PATH = "src/main/resources/handwritten/";
    private static final String FILES_EXTENSION = ".jpg";
    private static final List<String> TYPE_WRITTEN_FILE_NAMES = List.of("12345", "1234567890", "tel1", "tel2",
            "Классификация", "Регрессия", "1249439392102302");

    public static void main(String[] args) {
        AtomicInteger count = new AtomicInteger();
        TYPE_WRITTEN_FILE_NAMES.forEach(fileName -> {
            String result = recognizeText(TYPE_WRITTEN_FILES_PATH + fileName + FILES_EXTENSION);
            if(result.equals(fileName)) {
                count.getAndIncrement();
            }
            System.out.printf("Expected=%s, Received=%s\n", fileName,result);
        });
        System.out.printf("Size=%s, Equals=%s\n", TYPE_WRITTEN_FILE_NAMES.size(), count.get());
    }

    public static String recognizeText(String imagePath) {
        log.info("Recognizing text from image: " + imagePath);
        String result;

        try {
            File image = new File(imagePath);

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(TESSERACT_DATA_PATH);
            tesseract.setLanguage(LANGUAGE);
//            tesseract.setPageSegMode(1);
//            tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_LSTM_COMBINED);

            result = tesseract.doOCR(image).replaceAll("\n", " ").trim();
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
