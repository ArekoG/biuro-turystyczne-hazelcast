package travelagency.service.dto;

import travelagency.service.Travel;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class TravelDTO {
    private Long id;
    private String destination;
    private int numberOfPeople;
    private Timestamp startDate;
    private Timestamp endDate;
    private BigDecimal price;


    public TravelDTO(Long id, String destination, int numberOfPeople, Timestamp startDate, Timestamp endDate, BigDecimal price) {
        this.id = id;
        this.destination = destination;
        this.numberOfPeople = numberOfPeople;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public static TravelDTO map(Long key, Travel travel) {
        return new TravelDTO(key, travel.getDestination(), travel.getNumberOfPeople(), travel.getStartDate(), travel.getEndDate(), travel.getPrice());
    }

    public static TravelDTO map(Travel travel) {
        return new TravelDTO(null, travel.getDestination(), travel.getNumberOfPeople(), travel.getStartDate(), travel.getEndDate(), travel.getPrice());
    }

    @Override
    public String toString() {
        return "Podróż o id=" + id + ", liczba osób:" + numberOfPeople + ", lokalizacja:" + destination + ", data: od " + startDate + " do:" + endDate + "" +
                ". Łączna cena wyniosła:" + price + "zł\n";
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

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }
}
