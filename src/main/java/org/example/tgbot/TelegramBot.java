package org.example.tgbot;

import lombok.extern.slf4j.Slf4j;
import org.example.tesseract.TesseractExtractor;
import org.example.yandexgpt.YandexGptTaskSender;
import org.example.yandexgpt.YandexGptTranslateSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.example.tgbot.BotCommands.*;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    // @BotFather
    // t.me/SergeiYakimovichBot
    private final String botName = "SergeiYakimovichBot";
    private final String token = "6872664139:AAHTlfBNB8u5EnITEtmnknVSi39b7ykLFmc";
    public static final String OUTPUT_DIR = "tmp/";

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public TelegramBot() {
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.info("Error creating bot " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            handleMessagePhoto(update.getMessage());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();
            String messageText = update.getMessage().getText();

            handleMessageText(chatId, userName, messageText);
            return;
        }

        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String userName = update.getCallbackQuery().getFrom().getFirstName();
            String messageText = update.getCallbackQuery().getData();

            handleMessageText(chatId, userName, messageText);
        }
    }

    private void handleMessagePhoto(Message message) {
        try {
            long chatId = message.getChatId();
            PhotoSize photo = message.getPhoto().get(0);
            GetFile getFile = new GetFile(photo.getFileId());
            File file = execute(getFile);
            String fileName = OUTPUT_DIR + file.getFilePath().split("/")[1];
            downloadFile(file, new java.io.File(fileName));

//            String result = YandexGptImageSender.sendImageMessageToGPT(fileName);

//            String result = AsposeExtractor.recognizeText(fileName);

            String result = TesseractExtractor.recognizeText(fileName);

            sendTextToTelegram(chatId, "На картинке изображено: " + result);
            Files.delete(Path.of(fileName));
        } catch (Exception e) {
            log.error("Error handling photo " + e.getMessage());
        }
    }

    private void handleMessageText(long chatId, String userName, String messageText) {
        if (messageText.startsWith(START_COMMAND)) {
            sendTextToTelegram(chatId, START_TEXT.formatted(userName));
            return;
        }
        if (messageText.startsWith(HELP_COMMAND)) {
            sendTextToTelegram(chatId, HELP_TEXT);
            return;
        }
        if (messageText.startsWith(FOTO_COMMAND)) {
            sendPhotoToTelegram(chatId);
            return;
        }
        if (messageText.startsWith(RU_COMMAND)) {
            sendTextToTelegram(chatId, YandexGptTranslateSender.sendTranslateMessageToGPT("ru",
                    List.of(messageText.replaceFirst(RU_COMMAND, ""))));
            return;
        }
        if (messageText.startsWith(EN_COMMAND)) {
            sendTextToTelegram(chatId, YandexGptTranslateSender.sendTranslateMessageToGPT("en",
                    List.of(messageText.replaceFirst(EN_COMMAND, ""))));
            return;
        }

        sendTextToTelegram(chatId, YandexGptTaskSender.sendTaskMessageToGPT("Ответь на вопрос", messageText));
    }

    private void sendPhotoToTelegram(long chatId) {
        log.info("Sending photo to Telegram");
        try {
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(new java.io.File(OUTPUT_DIR + "rose.bmp")))
                    .build();
            execute(sendPhoto);
        } catch (Exception e) {
            log.error("Error while sending photo to Telegram " + e.getMessage());
        }
    }

    private void sendTextToTelegram(Long chatId, String textToSend) {
        log.info("Sending message to Telegram: "+ textToSend);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(Buttons.inlineMarkup());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to Telegram "+ e.getMessage());
        }
    }

}
