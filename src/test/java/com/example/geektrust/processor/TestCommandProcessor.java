package com.example.geektrust.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TestCommandProcessor {

    CommandProcessor commandProcessor = new CommandProcessor();
    List<String> months = new ArrayList<>();
    @BeforeEach
    void setup() {
        months.add("JANUARY");
        months.add("FEBRUARY");
        months.add("MARCH");
    }

    @Test
    void testReadAndProcessCommand() {
        List<String> instructions = new ArrayList<>();
        instructions.add("ALLOCATE 8000 6000 3500");
        instructions.add("SIP 3000 2000 1000");
        instructions.add("CHANGE 11.00% 9.00% 4.00% JANUARY");

        Assertions.assertDoesNotThrow(() -> commandProcessor.readAndProcessCommand(months, instructions));
    }
}
