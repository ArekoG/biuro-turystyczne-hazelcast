package app.travelagency.impl;


import app.common.Constants;
import app.common.TravelDTO;
import app.listener.AerostrikeWriteListener;
import app.statistic.Statistic;
import app.statistic.StatisticService;
import app.travelagency.ITravelAgency;
import app.travelagency.Travel;
import com.aerospike.client.*;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class AerospikeTravelAgencyImpl implements ITravelAgency, ScanCallback {
    private Collection<TravelDTO> recordList;
    private final StatisticService statisticService = new StatisticService();

    @Override
    public Long add(Travel travel) {
        EventPolicy eventPolicy = new EventPolicy();
        EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        EventLoops eventLoops = new NettyEventLoops(eventPolicy, group);
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.eventLoops = eventLoops;
        AerospikeClient client = new AerospikeClient(clientPolicy, Constants.IP_ADDRESS, Constants.PORT);
        setNewestDataToFalseIfExists(client);
        long id = ThreadLocalRandom.current().nextLong(1, 10000000);
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Bin[] bins = getBins(travel);

        WritePolicy writePolicy = getWritePolicy();
        EventLoop next = eventLoops.next();
        client.put(next, new AerostrikeWriteListener(client), writePolicy, key, bins);
        client.close();
        return id;
    }

    private void setNewestDataToFalseIfExists(AerospikeClient client) {
        Record statisticById = getStatisticById(Constants.STATISTIC_ID, client);
        if (Objects.nonNull(statisticById)) {
            Statistic statistic = (Statistic) statisticById.bins.get("stat");
            statistic.setNewestData(false);
            Key key = getStatisticKey(Constants.STATISTIC_ID);
            createOrUpdateStatisticRecord(key, statistic, client);
        }
    }

    private Bin[] getBins(Travel travel) {
        Bin bin = new Bin(Constants.TRAVEL_OBJECT, Value.getAsBlob(travel));
        Bin dest = new Bin("destination", travel.getDestination());
        Bin startDate = new Bin("startDate", travel.getStartDate());
        Bin endDate = new Bin("endDate", travel.getEndDate());
        Bin numberOfPeople = new Bin("numberOfPeople", travel.getNumberOfPeople());
        return new Bin[]{bin, dest, startDate, endDate, numberOfPeople};
    }

    @Override
    public void update(Long id, Timestamp newStartDate, Timestamp newEndDate) {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        setNewestDataToFalseIfExists(client);
        WritePolicy writePolicy = getWritePolicy();
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Record record = client.get(null, key);
        Travel travel = (Travel) record.bins.get(Constants.TRAVEL_OBJECT);
        Bin startDate = new Bin("startDate", newStartDate);
        Bin endDate = new Bin("endDate", newEndDate);
        travel.setStartDate(newStartDate);
        travel.setEndDate(newEndDate);
        Bin bin = new Bin(Constants.TRAVEL_OBJECT, Value.getAsBlob(travel));
        client.put(writePolicy, key, bin, startDate, endDate);
        client.close();
    }

    @Override
    public void remove(Long id) {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        client.delete(null, key);
        client.close();

    }

    @Override
    public Optional<Collection<TravelDTO>> findByQuery(StringBuilder query) {
        recordList = new ArrayList<>();
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        Statement stmt = new Statement();
        stmt.setNamespace(Constants.NAMESPACE);
        stmt.setSetName(Constants.TRAVEL);
        stmt.setIndexName("idx_dest");
        stmt.setBinNames(Constants.TRAVEL_OBJECT);
        stmt.setFilter(Filter.equal("destination", query.toString()));
        RecordSet records = client.query(null, stmt);
        while (records.next()) {
            Travel travel = (Travel) records.getRecord().bins.get(Constants.TRAVEL_OBJECT);
            recordList.add(TravelDTO.map(travel));
        }
        client.close();
        return Optional.of(recordList);
    }

    @Override
    public Statistic perform() {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        Collection<TravelDTO> allTravel = getAll();
        if (Objects.isNull(allTravel)) {
            client.close();
            return new Statistic();
        }
        Key key = getStatisticKey(Constants.STATISTIC_ID);
        Record record = client.get(null, key);
        Statistic statisticRecord = createOrUpdateStatisticRecord(client, allTravel, key, record);
        client.close();
        return statisticRecord;
    }

    private Key getStatisticKey(String statisticId) {
        return new Key(Constants.NAMESPACE, Constants.STATISTIC, statisticId);
    }

    private Record getStatisticById(String id, AerospikeClient client) {
        Key key = getStatisticKey(id);
        return client.get(null, key);
    }

    private Statistic createOrUpdateStatisticRecord(AerospikeClient client, Collection<TravelDTO> allTravel, Key key, Record record) {
        if (Objects.isNull(record)) {
            Statistic travelStatistic = statisticService.getTravelStatistic(allTravel);
            return createOrUpdateStatisticRecord(key, travelStatistic, client);
        }
        Statistic statistic = (Statistic) record.bins.get("stat");
        if (statistic.isNewestData())
            return statistic;
        Statistic travelStatistic = statisticService.getTravelStatistic(allTravel);
        statistic.setAverageDurationTime(travelStatistic.getAverageDurationTime());
        statistic.setAveragePrice(travelStatistic.getAveragePrice());
        statistic.setTopCity(travelStatistic.getTopCity());
        statistic.setNewestData(true);
        return createOrUpdateStatisticRecord(key, statistic, client);
    }

    private Statistic createOrUpdateStatisticRecord(Key key, Statistic travelStatistic, AerospikeClient client) {
        WritePolicy writePolicy = getWritePolicy();
        Bin bin = new Bin("stat", Value.getAsBlob(travelStatistic));
        client.put(writePolicy, key, bin);
        return travelStatistic;
    }

    private WritePolicy getWritePolicy() {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        return writePolicy;
    }

    @Override
    public Optional<TravelDTO> getById(Long id) {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Record record = client.get(null, key);
        if (Objects.nonNull(record)) {
            client.close();
            return Optional.ofNullable(TravelDTO.map(id, (Travel) record.bins.get(Constants.TRAVEL_OBJECT)));
        }
        client.close();
        return Optional.empty();
    }

    @Override
    public Collection<TravelDTO> getAll() {
        recordList = new ArrayList();
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.sendKey = true;
        client.scanAll(scanPolicy, Constants.NAMESPACE, Constants.TRAVEL, this);
        client.close();
        return recordList;
    }

    @Override
    public boolean isTravelExists(Long id) {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Record record = client.get(null, key);
        client.close();
        return Objects.nonNull(record);
    }

    @Override
    public void scanCallback(Key key, Record record) {
/*
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);

        client.delete(null, key1);
*/


        if (Objects.nonNull(key.userKey)) {
            TravelDTO travelobject = TravelDTO.map(key.userKey.toLong(), (Travel) record.bins.get(Constants.TRAVEL_OBJECT));
            recordList.add(travelobject);
        }

    }
}
