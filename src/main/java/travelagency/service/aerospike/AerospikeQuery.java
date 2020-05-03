package travelagency.service.aerospike;

import travelagency.service.IQuery;

import java.util.Scanner;

public class AerospikeQuery implements IQuery {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public StringBuilder getQuery() {
        return new StringBuilder(getDestination());
    }

    private String getDestination() {
        String destination;
        do {
            System.out.println("Podaj miasto po którym chcesz szukać");
            destination = scanner.nextLine();
        } while (destination.isEmpty());
        return destination;
    }
}
