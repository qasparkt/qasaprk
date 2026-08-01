package com.qaspark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QASparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(QASparkApplication.class, args);
        System.out.println("\n⚡ QASpark Java Web Application started successfully at: http://localhost:8080 ⚡\n");
    }
}
