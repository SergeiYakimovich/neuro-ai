package org.example.tgbot;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    String START_COMMAND = "/start";
    String HELP_COMMAND = "/help";
    String FOTO_COMMAND = "/foto";
    String RU_COMMAND = "/ru";
    String EN_COMMAND = "/en";
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand(START_COMMAND, "start the bot"),
            new BotCommand(HELP_COMMAND, "bot info")
    );
    String START_TEXT = "Hi, %s, nice to meet you!";
    String HELP_TEXT = """
            The following commands are available to you:
            /start - start the bot
            /help - help menu
            /foto - get photo from the bot
            /ru text - translate text to Russian
            /en text - translate text to English
            
            You may ask questions to the bot
            or send image to get text from it
            """;

}
