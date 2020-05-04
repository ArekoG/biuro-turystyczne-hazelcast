package app.statistic;

import app.common.TravelDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class StatisticService {
    public Statistic getTravelStatistic(Collection<TravelDTO> allTravel) {
        String topCity = getTopCity(allTravel);
        BigDecimal averagePrice = getAveragePrice(allTravel);
        double averageDurationTime = getAverageDurationTime(allTravel);
        return new Statistic(averagePrice, (int) averageDurationTime, topCity);
    }

    private double getAverageDurationTime(Collection<TravelDTO> allTravel) {
        OptionalDouble average = allTravel.stream()
                .map(travelDTO -> getDurationOfTrip(travelDTO.getStartDate(), travelDTO.getEndDate()))
                .mapToInt(value -> (int) value)
                .average();

        return average.isPresent() ? average.getAsDouble() : 0;
    }


    private String getTopCity(Collection<TravelDTO> allTravel) {
        return allTravel.stream()
                .collect(Collectors.groupingBy(TravelDTO::getDestination, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    private BigDecimal getAveragePrice(Collection<TravelDTO> allTravel) {
        Collection<BigDecimal> priceList = getPriceList(allTravel);
        BigDecimal sum = sum(priceList);
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        if (sum.equals(BigDecimal.ZERO))
            return BigDecimal.valueOf(0);
        return sum.divide(new BigDecimal(priceList.size()), mc);

    }

    private Collection<BigDecimal> getPriceList(Collection<TravelDTO> allTravel) {
        return allTravel.stream()
                .map(TravelDTO::getPrice)
                .collect(Collectors.toList());

    }

    private BigDecimal sum(Collection<BigDecimal> priceList) {
        return priceList.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private Object getDurationOfTrip(Timestamp startDate, Timestamp endDate) {
        return (int) DAYS.between(startDate.toInstant(), endDate.toInstant());

    }
}
