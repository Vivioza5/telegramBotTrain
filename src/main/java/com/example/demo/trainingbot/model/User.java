package com.example.demo.trainingbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


import java.sql.Timestamp;

@Data
@Entity(name="usersDataTable")
public class User {
    @Id
    private Long chatid;
    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp startTalk;
    private boolean embededJoke;
    private String phoneNumber;
    private Timestamp registeredAt;


}
