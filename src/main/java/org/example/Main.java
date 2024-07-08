package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.tgbot.BotInitializer;
import org.example.tgbot.TelegramBot;


public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        //TG Bot
        new BotInitializer(new TelegramBot()).init();

    }

}