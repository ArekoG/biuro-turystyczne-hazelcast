package travelagency.service.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;
import org.codehaus.plexus.util.StringUtils;
import travelagency.common.Constants;
import travelagency.service.ITravelAgency;
import travelagency.service.Statistic;
import travelagency.service.StatisticService;
import travelagency.service.Travel;
import travelagency.service.dto.TravelDTO;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HazelcastTravelAgencyImpl implements ITravelAgency {
    private final StatisticService statisticService = new StatisticService();

    @Override
    public Long add(Travel travel) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        setNewestDataToFalseIfExists(client);
        IMap<Long, Travel> travelMap = client.getMap(Constants.TRAVEL);
        Long id = ThreadLocalRandom.current().nextLong(1, 100000000);
        travelMap.put(id, travel);
        return id;
    }

    private void setNewestDataToFalseIfExists(HazelcastInstance client) {
        IMap<String, Statistic> statisticMap = client.getMap(Constants.STATISTIC);
        if (statisticMap.size() != 0) {
            Statistic statistic = statisticMap.get(Constants.STATISTIC_ID);
            statistic.setNewestData(false);
            statisticMap.put(Constants.STATISTIC_ID, statistic);
        }

    }


    @Override
    public void update(Long id, Timestamp newStartDate, Timestamp newEndDate) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travelMap = client.getMap(Constants.TRAVEL);
        if (isValidate(travelMap, id)) {
            Travel updatedTravel = travelMap.get(id);
            updatedTravel.setStartDate(newStartDate);
            updatedTravel.setEndDate(newEndDate);
            travelMap.put(id, updatedTravel);
        }

    }

    @Override
    public void remove(Long id) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travel = client.getMap(Constants.TRAVEL);
        if (isValidate(travel, id)) {
            travel.remove(id);
        }
    }

    @Override
    public Optional<Collection<TravelDTO>> findByQuery(StringBuilder query) {
        if (StringUtils.isBlank(query.toString())) {
            return Optional.empty();
        }
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travel = client.getMap(Constants.TRAVEL);
        return Optional.of(travel.values(Predicates.sql(query.toString().substring(0, query.length() - 4)))
                .stream()
                .map(TravelDTO::map)
                .collect(Collectors.toList()));

    }


    @Override
    public Statistic perform() {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IMap<Long, Travel> travel = client.getMap(Constants.TRAVEL);
        if (travel.size() == 0)
            return new Statistic();
        return getStatistic(client);
    }

    private Statistic getStatistic(HazelcastInstance client) {
        Collection<TravelDTO> allTravel = getAll();
        IMap<String, Statistic> statisticMap = client.getMap(Constants.STATISTIC);
        if (isStatisticExists(Constants.STATISTIC, client)) {
            return returnOrUpdateStatistic(allTravel, statisticMap);
        }
        Statistic travelStatistic = statisticService.getTravelStatistic(allTravel);
        statisticMap.put(Constants.STATISTIC_ID, travelStatistic);
        return travelStatistic;
    }


    private Statistic returnOrUpdateStatistic(Collection<TravelDTO> allTravel, IMap<String, Statistic> statisticMap) {
        Statistic statistic = statisticMap.get(Constants.STATISTIC_ID);
        if (statistic.isNewestData()) {
            return statistic;
        }
        Statistic travelStatistic = statisticService.getTravelStatistic(allTravel);
        statisticMap.put(Constants.STATISTIC_ID, travelStatistic);
        return travelStatistic;
    }

    private boolean isStatisticExists(String id, HazelcastInstance client) {
        return Objects.nonNull(client.getMap(Constants.STATISTIC).get(id));
    }

    @Override
    public Optional<TravelDTO> getById(Long id) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();

        IMap<Long, Travel> travel = client.getMap(Constants.TRAVEL);

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
        return client.getMap(Constants.TRAVEL).entrySet().stream()
                .map(trav -> TravelDTO.map((Long) trav.getKey(), (Travel) trav.getValue()))
                .collect(Collectors.toList());

    }

    @Override
    public boolean isTravelExists(Long id) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        return Objects.nonNull(client.getMap(Constants.TRAVEL).get(id));
    }
}
