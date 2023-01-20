package com.example.demo.trainingbot.enums;

public enum CommandNameEnum {
    START_COMMAND("/start"),
    GET_MY_DATA_COMMAND("/mydata"),
    DELETE_MY_DATA_COMMAND("/deletedata"),
    SETTINGS_COMMAND("/settings"),
    REGISTER_COMMAND("/register"),
    SEND_MESSAGE_TO_ALL_COMMAND("/send"),
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
