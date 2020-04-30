package travelagency.dto;

import travelagency.service.Travel;

import java.time.LocalDate;

public class TravelDTO {
    private Long id;
    private String destination;
    private int numberOfPeople;
    private LocalDate startDate;
    private LocalDate endDate;


    public TravelDTO(Long id, String destination, int numberOfPeople, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.destination = destination;
        this.numberOfPeople = numberOfPeople;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static TravelDTO map(Long key, Travel travel) {
        return new TravelDTO(key, travel.getDestination(), travel.getNumberOfPeople(), travel.getStartDate(), travel.getEndDate());
    }

    public static TravelDTO map(Travel travel) {
        return new TravelDTO(null, travel.getDestination(), travel.getNumberOfPeople(), travel.getStartDate(), travel.getEndDate());
    }

    @Override
    public String toString() {
        return "Podróż o id=" + id + ", liczba osób:" + numberOfPeople + ", lokalizacja:" + destination + ", data: od " + startDate + " do:" + endDate;
    }

    public Long getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
