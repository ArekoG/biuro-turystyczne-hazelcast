package travelagency.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Travel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String destination;
    private BigDecimal price;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfPeople;


    public Travel(String destination, LocalDate startDate, LocalDate endDate, int numberOfPeople) {
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfPeople = numberOfPeople;
    }


    public String getDestination() {
        return destination;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
}
