package com.example.springwebfluxdemo;

import org.springframework.boot.SpringApplication;

public class TestSpringWebfluxDemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringWebfluxDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
