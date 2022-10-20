package com.example.geektrust;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testCreateMonths() {
        List<String> months = Main.createMonths();
        assertNotNull(months);
    }

}