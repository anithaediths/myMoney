package com.example.geektrust;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static List<String> createMonths() {
        List<String> months = new LinkedList<>();

        months.add("JANUARY");
        months.add("FEBRUARY");
        months.add("MARCH");
        months.add("APRIL");
        months.add("MAY");
        months.add("JUNE");
        months.add("JULY");
        months.add("AUGUST");
        months.add("SEPTEMBER");
        months.add("OCTOBER");
        months.add("NOVEMBER");
        months.add("DECEMBER");

        return months;
    }

    public static void main(String[] args) {

        List<String> months = createMonths();
        CommandProcessor commandProcessor = new CommandProcessor();

        try (Stream<String> fileLines = Files.lines(new File(args[0]).toPath())) {
            List<String> lines = fileLines
                    .map(String::trim)
                    .filter(s -> !s.matches(" "))
                    .collect(Collectors.toList());

            commandProcessor.readAndProcessCommand(months, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
