package app.common;

import app.query.IQuery;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputService {
    private final Scanner scanner = new Scanner(System.in);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public int getOption(String squadName) {
        int option = 0;

        do {
            System.out.println("----------------------BIURO TURYSTYCZNE - SKŁAD: "+squadName+"----------------------");
            System.out.println("Wybierz opcje:");
            System.out.println("[1]Dodaj podróż");
            System.out.println("[2]Aktualizuj date podróży");
            System.out.println("[3]Usuń podróż");
            System.out.println("[4]Pobierz podróż po id");
            System.out.println("[5]Pobierz liste wszystkich podróży");
            System.out.println("[6]Zaawansowane wyszukiwanie");
            System.out.println("[7]Wygeneruj raport o podróżach");
            System.out.println("[8]Wyjdź");
            option = getInt(option);
        } while (option <= 0);
        return option;
    }

    public Timestamp getStartDate() throws ParseException {
        String date;
        do {
            System.out.println("Podaj date początku podróży w formacie YYYY-MM-DD:");
            date = scanner.nextLine();
        } while (date.isEmpty() || !isValid(date));
        return new Timestamp(formatter.parse(date).getTime());
    }

    public Timestamp getEndDate(Timestamp startDate) throws ParseException {
        String date;
        do {
            System.out.println("Podaj date końca podróży w formacie YYYY-MM-DD:");
            date = scanner.nextLine();
        } while (date.isEmpty() || !isValid(date) || validEndDate(startDate, date));

        return new Timestamp(formatter.parse(date).getTime());
    }

    private boolean validEndDate(Timestamp startDate, String date) throws ParseException {
        return startDate.after(new Timestamp(formatter.parse(date).getTime())) || startDate.equals(new Timestamp(formatter.parse(date).getTime()));

    }

    public boolean isValid(String dateStr) {
        try {
            new Timestamp(formatter.parse(dateStr).getTime());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public int getInt(String message) {
        int numberOfPeople = 0;

        do {
            System.out.println(message);
            numberOfPeople = getInt(numberOfPeople);
        } while (numberOfPeople <= 0);
        return numberOfPeople;
    }

    private int getInt(int numberOfPeople) {
        try {
            numberOfPeople = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            scanner.nextLine();
        }
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


    public StringBuilder getQueryField(IQuery iQuery) {
        return iQuery.getQuery();
    }


}
