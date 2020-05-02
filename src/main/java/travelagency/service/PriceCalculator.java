package travelagency.service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.temporal.ChronoUnit.DAYS;

public class PriceCalculator {
    public BigDecimal calculatePrice(Travel travel) {
        int durationOfTrip = getDurationOfTrip(travel);
        BigDecimal cityHotelPricePerNight = getCityHotelPrice(travel.getDestination());
        return cityHotelPricePerNight.multiply(BigDecimal.valueOf(travel.getNumberOfPeople())).multiply(BigDecimal.valueOf(durationOfTrip));
    }

    private BigDecimal getCityHotelPrice(String destination) {
        int randomPrice = ThreadLocalRandom.current().nextInt(100, 200);
        return BigDecimal.valueOf(randomPrice);
    }

    private int getDurationOfTrip(Travel travel) {
        return (int) DAYS.between(travel.getStartDate().toInstant(), travel.getEndDate().toInstant());

    }
}
