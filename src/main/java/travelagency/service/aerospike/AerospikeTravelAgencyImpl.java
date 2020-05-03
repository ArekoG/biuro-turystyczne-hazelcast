package travelagency.service.aerospike;


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
import travelagency.common.Constants;
import travelagency.service.ITravelAgency;
import travelagency.service.Travel;
import travelagency.service.dto.TravelDTO;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class AerospikeTravelAgencyImpl implements ITravelAgency, ScanCallback {
    private Collection<TravelDTO> recordList;

    @Override
    public Long add(Travel travel) throws IOException {
        EventPolicy eventPolicy = new EventPolicy();
        EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        EventLoops eventLoops = new NettyEventLoops(eventPolicy, group);

        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.eventLoops = eventLoops;

        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        long id = ThreadLocalRandom.current().nextLong(0, 10000000);
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Bin[] bins = getBins(travel);

        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        EventLoop next = eventLoops.next();
        client.put(next, new AerostrikeWriteListener(client), writePolicy, key, bins);
        client.close();
        return id;
    }

    private Bin[] getBins(Travel travel) {
        Bin bin = new Bin("travelobject", Value.getAsBlob(travel));
        Bin dest = new Bin("destination", travel.getDestination());
        Bin startDate = new Bin("startDate", travel.getStartDate());
        Bin endDate = new Bin("endDate", travel.getEndDate());
        Bin numberOfPeople = new Bin("numberOfPeople", travel.getNumberOfPeople());
        Bin[] bins = {bin, dest, startDate, endDate, numberOfPeople};
        return bins;
    }

    @Override
    public void update(Long id, Timestamp newStartDate, Timestamp newEndDate) {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Record record = client.get(null, key);
        Travel travel = (Travel) record.bins.get("travelobject");
        Bin startDate = new Bin("startDate", newStartDate);
        Bin endDate = new Bin("endDate", newEndDate);
        travel.setStartDate(newStartDate);
        travel.setEndDate(newEndDate);
        Bin bin = new Bin("travelobject", Value.getAsBlob(travel));
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
        stmt.setBinNames("travelobject");
        stmt.setFilter(Filter.equal("destination", query.toString()));
        RecordSet records = client.query(null, stmt);
        while (records.next()) {
            Travel travel = (Travel) records.getRecord().bins.get("travelobject");
            recordList.add(TravelDTO.map(travel));
        }
        return Optional.of(recordList);
    }

    @Override
    public void perform() {

    }

    @Override
    public Optional<TravelDTO> getById(Long id) {
        AerospikeClient client = new AerospikeClient(Constants.IP_ADDRESS, Constants.PORT);
        Key key = new Key(Constants.NAMESPACE, Constants.TRAVEL, id);
        Record record = client.get(null, key);
        if (Objects.nonNull(record))
            return Optional.ofNullable(TravelDTO.map(id, (Travel) record.bins.get("travelobject")));
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
    public void scanCallback(Key key, Record record) throws AerospikeException {
/*        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        client.delete(null, key);*/

        if (Objects.nonNull(key.userKey)) {
            TravelDTO travelobject = TravelDTO.map(key.userKey.toLong(), (Travel) record.bins.get("travelobject"));
            recordList.add(travelobject);
        }

    }
}
