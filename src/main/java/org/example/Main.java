package org.example;

import org.example.tgbot.BotInitializer;
import org.example.tgbot.TelegramBot;

public class Main {
    public static void main(String[] args) {
        //TG Bot
        new BotInitializer(new TelegramBot()).init();

    }

}