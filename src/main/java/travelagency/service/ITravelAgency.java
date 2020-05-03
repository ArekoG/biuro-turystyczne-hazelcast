package travelagency.service;


import travelagency.service.dto.TravelDTO;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;


public interface ITravelAgency {
    Long add(Travel travel) throws IOException;

    void update(Long id, Timestamp newStartDate, Timestamp newEndDate);

    void remove(Long id);

    Optional<Collection<TravelDTO>> findByQuery(StringBuilder query);

    void perform();

    Optional<TravelDTO> getById(Long id);

    Collection<TravelDTO> getAll();

    boolean isTravelExists(Long id);
}
