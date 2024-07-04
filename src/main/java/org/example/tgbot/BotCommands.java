package org.example.tgbot;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info")
    );

    String START_TEXT = "Hi, %s, nice to meet you!";
    String HELP_TEXT = "The following commands are available to you:\n\n" +
            "/start - start the bot\n" +
            "/help - help menu";
}
