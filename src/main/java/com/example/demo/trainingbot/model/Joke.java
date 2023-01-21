package com.example.demo.trainingbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name="jokeTable")
public class Joke {



    private String body;
    private String category;
    @Id
    private Integer id;
    private double raiting;


}
