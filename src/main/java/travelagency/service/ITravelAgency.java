package travelagency.service;

import travelagency.dto.TravelDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface ITravelAgency {
    Long add(Travel travel);

    void update(Long id, LocalDate newStartDate, LocalDate newEndDate);

    void remove(Long id);

    Optional<Collection<TravelDTO>> findByDate(LocalDate localDate);

    void perform();

    Optional<TravelDTO> getById(Long id);

    Collection<TravelDTO> getAll();
}
