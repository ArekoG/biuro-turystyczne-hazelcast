package travelagency.service.aerospike;


import com.aerospike.client.*;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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
    Collection<TravelDTO> recordList;

    @Override
    public Long add(Travel travel) throws IOException {
        EventPolicy eventPolicy = new EventPolicy();
        EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        EventLoops eventLoops = new NettyEventLoops(eventPolicy, group);

        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.eventLoops = eventLoops;

        AerospikeClient client = new AerospikeClient(clientPolicy, "172.28.128.4", 3000);
        long id = ThreadLocalRandom.current().nextLong(0, 10000000);
        Key key = new Key("test", "travel", id);
        Bin bin = new Bin("travelobject", Value.getAsBlob(travel));

        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        EventLoop next = eventLoops.next();
        client.put(next, new AerostrikeWriteListener(client), writePolicy, key, bin);
        client.close();
        return id;
    }

    @Override
    public void update(Long id, Timestamp newStartDate, Timestamp newEndDate) {
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        Key key = new Key("test", "travel", id);
        Record record = client.get(null, key);
        Travel travel = (Travel) record.bins.get("travelobject");
        travel.setStartDate(newStartDate);
        travel.setEndDate(newEndDate);
        Bin bin = new Bin("travelobject", Value.getAsBlob(travel));
        client.put(writePolicy, key, bin);
        client.close();
    }

    @Override
    public void remove(Long id) {
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        Key key = new Key("test", "travel", id);
        client.delete(null, key);
        client.close();

    }

    @Override
    public Optional<Collection<TravelDTO>> findByQuery(StringBuilder localDate) {
        return Optional.empty();
    }

    @Override
    public void perform() {

    }

    @Override
    public Optional<TravelDTO> getById(Long id) {
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        Key key = new Key("test", "travel", id);
        Record record = client.get(null, key);
        if (Objects.nonNull(record))
            return Optional.ofNullable(TravelDTO.map(id, (Travel) record.bins.get("travelobject")));
        return Optional.empty();
    }

    @Override
    public Collection<TravelDTO> getAll() {
        recordList = new ArrayList();
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.sendKey = true;
        client.scanAll(scanPolicy, "test", "travel", this);
        client.close();
        return recordList;
    }

    @Override
    public boolean isTravelExists(Long id) {
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        Key key = new Key("test", "travel", id);
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
