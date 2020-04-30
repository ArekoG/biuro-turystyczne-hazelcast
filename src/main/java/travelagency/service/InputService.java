package travelagency.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputService {
    private final Scanner scanner = new Scanner(System.in);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LocalDate getDate(String message) {
        String date;
        do {
            System.out.println(message);
            date = scanner.nextLine();
        } while (date.isEmpty() || !isValid(date));
        return LocalDate.parse(date, formatter);
    }

    public boolean isValid(String dateStr) {
        try {
            LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public int getNumberOfPeople() {
        System.out.println("Podaj liczbe osób:");
        while (!scanner.hasNextInt()) {
            System.out.println("Podaj liczbe osób:");
            scanner.next();
            scanner.nextLine();
        }
        int numberOfPeople = scanner.nextInt();
        scanner.nextLine();
        return numberOfPeople;
    }

    public String getDestination() {
        String destination;
        do {
            System.out.println("Podaj cel podróży:");
            destination = scanner.nextLine();
        } while (destination.isEmpty());
        return destination;
    }

    public Long getId() {
        System.out.println("Podaj id podróży:");
        while (!scanner.hasNextLong()) {
            System.out.println("Podaj id podróży:");

            scanner.next();
            scanner.nextLine();
        }
        Long numberOfPeople = scanner.nextLong();
        scanner.nextLine();
        return numberOfPeople;
    }
}
