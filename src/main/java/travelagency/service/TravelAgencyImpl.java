package travelagency.service;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import travelagency.dto.TravelDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class TravelAgencyImpl implements ITravelAgency {
    private final Random keyGenerator = new Random(System.currentTimeMillis());
    private final InputService inputService = new InputService();

    @Override
    public Long add(Travel travel) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travelMap = client.getMap("travel");


        Long id = keyGenerator.nextLong();
        travelMap.put(id, travel);
        return id;
        //obliczenie ceny - przetwarzanie po stronie serwera
    }


    @Override
    public void update(Long id, LocalDate newStartDate, LocalDate newEndDate) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travelMap = client.getMap("travel");
        if (isValidate(travelMap, id)) {
            Travel updatedTravel = travelMap.get(id);
            updatedTravel.setStartDate(newStartDate);
            updatedTravel.setEndDate(newEndDate);
            travelMap.put(id, updatedTravel);
            System.out.println("Zaktualizowano podróż o id:" + id);
        }

    }

    @Override
    public void remove(Long id) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travel = client.getMap("travel");
        if (isValidate(travel, id)) {
            travel.remove(id);
            System.out.println("Usunięto podróż o id:" + id);
        }
    }

    @Override
    public Optional<Collection<TravelDTO>> findByDate(LocalDate localDate) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travel = client.getMap("travel");
        Predicate<?, ?> datePredicate = Predicates.equal("startDate", localDate);
        return Optional.of(travel.values(Predicates.and(datePredicate)).stream()
                .map(TravelDTO::map)
                .collect(Collectors.toList()));

    }

    @Override
    public void perform() {

    }

    @Override
    public Optional<TravelDTO> getById(Long id) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travel = client.getMap("travel");
        if (!isValidate(travel, id)) {
            return Optional.empty();
        }

        return Optional.of(TravelDTO.map(id, travel.get(id)));
    }

    private boolean isValidate(IMap<Long, Travel> travel, Long id) {
        if (Objects.isNull(travel)) {
            System.out.println("Nie zapisano jeszcze żadnej podróży!");
            return false;
        }
        if (Objects.isNull(travel.get(id))) {
            System.out.println("Nie znaleziono podróży o id:" + id);
            return false;
        }
        return true;
    }

    @Override
    public Collection<TravelDTO> getAll() {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        return client.getMap("travel").entrySet().stream()
                .map(trav -> TravelDTO.map((Long) trav.getKey(), (Travel) trav.getValue()))
                .collect(Collectors.toList());

    }
}
