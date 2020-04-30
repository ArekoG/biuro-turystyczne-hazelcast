package menu;

import com.hazelcast.core.Hazelcast;
import travelagency.service.ITravelAgency;
import travelagency.service.InputService;
import travelagency.service.Travel;
import travelagency.service.TravelAgencyImpl;

import java.time.LocalDate;
import java.util.Scanner;

public class Menu {
    private ITravelAgency travelAgency = new TravelAgencyImpl();
    private final InputService inputService = new InputService();

    public void start() {
        Hazelcast.newHazelcastInstance();
        Scanner userChoice = new Scanner(System.in);

        while (true) {
            System.out.println("----------------------BIURO TURYSTYCZNE - SKŁAD: HAZELCAST----------------------");
            System.out.println("Wybierz opcje:");
            System.out.println("[1]Dodaj podróż");
            System.out.println("[2]Aktualizuj date podróży");
            System.out.println("[3]Usuń podróż");
            System.out.println("[4]Pobierz podróż po id");
            System.out.println("[5]Pobierz liste wszystkich podróży");
            System.out.println("[6]Znajdź podróż po dacie");
            System.out.println("[7]Coś z przetwarzaniem tu bedzie(po stronie klienta)");
            System.out.println("[8]Wyjdź");
            switch (userChoice.nextInt()) {

                case 1:
                    String destination = inputService.getDestination();
                    int numberOfPeople = inputService.getNumberOfPeople();
                    LocalDate startDate = inputService.getDate("Podaj date początku podróży w formacie YYYY-MM-DD:");
                    LocalDate endDate = inputService.getDate("Podaj date końca podróży w formacie YYYY-MM-DD:");

                    Long id = travelAgency.add(new Travel(destination, startDate, endDate, numberOfPeople));
                    System.out.println("Dodano nową podróż:\nId:" + id + "\nLiczba osób:" + numberOfPeople + "\nData rozpoczęcia:" + startDate + "\nData końca:" + endDate);

                    break;
                case 2:
                    Long travelId = inputService.getId();
                    LocalDate newStartDate = inputService.getDate("Podaj nową date początku podróży w formacie YYYY-MM-DD:");
                    LocalDate newEndDate = inputService.getDate("Podaj nową date końca podróży w formacie YYYY-MM-DD:");
                    travelAgency.update(travelId, newStartDate, newEndDate);
                    break;
                case 3:
                    Long travelIdToRemove = inputService.getId();
                    travelAgency.remove(travelIdToRemove);
                    break;
                case 4:
                    travelId = inputService.getId();
                    travelAgency.getById(travelId).ifPresent(System.out::println);
                    break;
                case 5:
                    travelAgency.getAll().forEach(System.out::println);
                    break;
                case 6:
                    LocalDate searchDate = inputService.getDate("Znajdz podróż po dacie. Podaj date w formacie YYYY-MM-DD:");
                    travelAgency.findByDate(searchDate).ifPresent(System.out::println);
                    break;
                case 7:

                    break;
                case 8:
                    System.exit(200);
                    break;
                default:
                    System.out.println("Nie ma takiej opcji");

            }


        }
    }
}
