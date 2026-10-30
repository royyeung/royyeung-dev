package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HelloControllerTest {

    @Test
    void helloMentionsJenkins() {
        assertTrue(new HelloController().hello().contains("Jenkins"));
    }
}
