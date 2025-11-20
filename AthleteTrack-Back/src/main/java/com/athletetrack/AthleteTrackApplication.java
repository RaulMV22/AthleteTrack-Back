package com.athletetrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AthleteTrackApplication {
    public static void main(String[] args) {
        SpringApplication.run(AthleteTrackApplication.class, args);
    }
}
