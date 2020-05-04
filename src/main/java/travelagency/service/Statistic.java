package travelagency.service;

import java.io.Serializable;
import java.math.BigDecimal;

public class Statistic implements Serializable {
    private static final long serialVersionUID = 3L;

    private BigDecimal averagePrice;
    private int averageDurationTime;
    private String topCity;
    private boolean newestData;

    public Statistic(BigDecimal averagePrice, int averageDurationTime, String topCity) {
        this.averagePrice = averagePrice;
        this.averageDurationTime = averageDurationTime;
        this.topCity = topCity;
    }

    public Statistic() {

    }


    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void setAverageDurationTime(int averageDurationTime) {
        this.averageDurationTime = averageDurationTime;
    }

    public void setTopCity(String topCity) {
        this.topCity = topCity;
    }

    public void setNewestData(boolean newestData) {
        this.newestData = newestData;
    }


    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public int getAverageDurationTime() {
        return averageDurationTime;
    }

    public String getTopCity() {
        return topCity;
    }

    public boolean isNewestData() {
        return newestData;
    }

    @Override
    public String toString() {
        return "Statystyki:" +
                "\nśrednia cena wydawana na podróże=" + averagePrice +
                "zł\nśredni czas trwania podróży=" + averageDurationTime + " dni" +
                "\nnajczęściej wybierane miasto=" + topCity;
    }
}
