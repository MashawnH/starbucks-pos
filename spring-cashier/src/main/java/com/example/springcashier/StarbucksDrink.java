package com.example.springcashier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Random;

@Entity
@Table(name = "STARBUCKS_DRINK")
@Data
@RequiredArgsConstructor
public class StarbucksDrink {

    private @Id
    @GeneratedValue
    @JsonIgnore  /* https://www.baeldung.com/jackson-ignore-properties-on-serialization */
            Long id;
    @Column(nullable = false)
    private String drink;
    @Column(nullable = false)
    private String milk;
    @Column(nullable = false)
    private String size;
    private String drinkid;


    //create id from 1 to 1000
    public void generateDrinkID(){
        Random rand = new Random();
        drinkid = "" + rand.nextInt(1000 - 1) + 1;
    }
}

