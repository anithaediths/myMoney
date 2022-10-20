package com.example.geektrust;

import com.example.geektrust.helper.Constants;
import com.example.geektrust.model.Months;
import com.example.geektrust.processor.CommandProcessor;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    static List<String> createMonths() {
        return Stream.of(Months.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        try (Stream<String> linesOfFile = Files.lines(new File(args[0]).toPath())) {
            readAndProcessInputFile(createMonths(), linesOfFile);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    private static void readAndProcessInputFile(List<String> months, Stream<String> fileLines) {
        CommandProcessor commandProcessor = new CommandProcessor();
        List<String> lines = fileLines
                .map(String::trim)
                .filter(s -> !s.matches(Constants.SPACE))
                .collect(Collectors.toList());

        commandProcessor.readAndProcessCommand(months, lines);
    }


}
