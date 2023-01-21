package com.example.demo.trainingbot.enums;

public enum CommandNameEnum {
    START_COMMAND("/start"),
    SETTINGS_COMMAND("/settings"),
    JOKE_COMMAND("/joke"),
    HELP_COMMAND("/help");


    private final String commandName;

    CommandNameEnum(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public String toString() {
        return this.commandName;
    }
}
