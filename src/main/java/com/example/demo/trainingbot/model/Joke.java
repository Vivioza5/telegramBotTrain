package com.example.demo.trainingbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "jokeTable")
public class Joke {
    private double rating;
    @Id
    private int id;
    @Column(length = 25500)
    private String body;
    private String category;

}
