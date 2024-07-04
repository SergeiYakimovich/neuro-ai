package org.example.tgbot;

import lombok.extern.slf4j.Slf4j;
import org.example.yandexgpt.YandexGptSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

import static org.example.tgbot.BotCommands.*;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    // @BotFather
    // t.me/SergeiYakimovichBot
    private final String botName = "SergeiYakimovichBot";
    private final String token = "6872664139:AAHTlfBNB8u5EnITEtmnknVSi39b7ykLFmc";
    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public TelegramBot(){
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = 0;
        String messageText = "";
        String userName = "";

        if(update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            messageText = update.getMessage().getText();
            userName = update.getMessage().getChat().getFirstName();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            messageText = update.getCallbackQuery().getData();
        }

        switch (messageText) {
            case "/start" -> sendMessage(chatId, START_TEXT.formatted(userName));
            case "/help" -> sendMessage(chatId, HELP_TEXT);
            case "/foto" -> sendFoto(chatId);
            default -> sendMessage(chatId, YandexGptSender.sendMessage("Переведи текст", messageText));
        }

    }

    private void sendFoto(long chatId) {
        try {
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(new File("src/main/resources/rose.bmp")))
                    .build();
            execute(sendPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String textToSend){
        log.info("Sending message: {}",textToSend);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(Buttons.inlineMarkup());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message {}", e.getMessage());
            e.printStackTrace();
        }
    }

}
