package app.query.impl;

import app.query.IQuery;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Scanner;

public class HazelcastQuery implements IQuery {
    private final Scanner scanner = new Scanner(System.in);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public StringBuilder getQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder query = new StringBuilder();
        stringBuilder.append("SELECT * FROM TRAVEL WHERE ");
        int searchChoice;
        while (true) {
            System.out.println("Wybierz po jakim polu chcesz szukać:\n[1]Miejsce podróży\n" +
                    "[2]Data ropoczęcia podróży\n[3]Data końca podróży\n[4]Liczba osób\n[5]Własne zapytanie\n[6]Szukaj");
            searchChoice = scanner.nextInt();
            scanner.nextLine();
            switch (searchChoice) {
                case 1:
                    System.out.println("Podaj miejsce podróży");
                    String destination = scanner.nextLine();
                    query.append(" destination='" + destination + "' AND ");

                    break;
                case 2:
                    System.out.println("Podaj date rozpoczęcia podróży");
                    query.append(" startDate='" + getDate() + "' AND ");
                    break;
                case 3:
                    System.out.println("Podaj date końca podróży");
                    query.append(" endDate='" + getDate() + "' AND ");
                    break;
                case 4:
                    System.out.println("Podaj liczbę osób");
                    int numberOfPeople = scanner.nextInt();
                    scanner.nextLine();
                    query.append(" numberOfPeople=" + numberOfPeople + " AND ");
                    break;
                case 5:
                    System.out.println("Wpiasz własne zapytanie w formie np: destination='Miasto' and numberOfPeople in (2,3,4)");
                    System.out.println("Select * from travel WHERE");
                    return new StringBuilder(scanner.nextLine() + "    ");
                case 6:
                    return query;
                default:

            }

        }
    }

    private Timestamp getDate() {
        String startDate = scanner.nextLine();
        try {
            return new Timestamp(formatter.parse(startDate).getTime());
        } catch (ParseException e) {
            System.out.println("Niepoprawny format daty");
            return Timestamp.from(Instant.now());
        }

    }
}
