package com.example.demo.trainingbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${botName}")
    String BOT_NAME;
    @Value("${botToken}")
    String BOT_TOKEN;
    @Value("${bot.owner}")
    Long BOT_OWNER;


}
