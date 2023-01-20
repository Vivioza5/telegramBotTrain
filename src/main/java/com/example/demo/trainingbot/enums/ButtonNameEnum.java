package com.example.demo.trainingbot.enums;

public enum ButtonNameEnum {
    YES_BUTTON("Yes"),
    NO_BUTTON("No"),
    GET_DICTIONARY_BUTTON("Скачать словарь"),
    UPLOAD_DICTIONARY_BUTTON("Загрузить мой словарь"),
    HELP_BUTTON("Помощь");

    private final String buttonName;

    ButtonNameEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }
}
