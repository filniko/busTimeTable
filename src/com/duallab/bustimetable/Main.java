package com.duallab.bustimetable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public Main() {
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Main mainBusService = new Main();
        mainBusService.application(input);
    }

    private void application(Scanner input) {
            FileReader fileReader = null;

            try {
                File file = null;

                String outputLocation;
                for(int tryAgain = 5; tryAgain > 0; --tryAgain) {
                    System.out.println("Please enter the Original Timetable file location");
                    outputLocation = input.nextLine();
                    if (outputLocation != null) {
                        outputLocation = outputLocation.replaceAll("\"", "");
                    }

                    Path path = Paths.get(outputLocation);
                    file = path != null ? path.toFile() : null;
                    if (file != null && file.isFile()) {
                        break;
                    }

                    System.out.println("Invalid File Location");
                }

                if (file == null || !file.exists() || !file.isFile()) {
                    System.out.println("Please try it some other time. Thank You");
                    return;
                }

                fileReader = new FileReader(file);
                System.out.println("Please enter the valid Output Timetable location (Entering blank or invalid path will save the output in the Original timetable directory)");
                outputLocation = input.nextLine();
                File outputFile = new File(outputLocation);
                if (outputFile == null || !outputFile.exists() || !outputFile.isDirectory()) {
                    outputLocation = file.getParent() + "/";
                }

                if (outputLocation != null) {
                    outputLocation = outputLocation.replaceAll("\"", "");
                }

                TimeTable busTimeTable = new TimeTable();
                busTimeTable.generateTimeTable(fileReader, outputLocation);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileReader != null) {
                        fileReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                input.close();
            }

    }
}
