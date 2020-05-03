package travelagency.service.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.WritePolicy;
import travelagency.service.PriceCalculator;
import travelagency.service.Travel;

public class AerostrikeWriteListener implements WriteListener {
    private final AerospikeClient client;
    private final PriceCalculator priceCalculator = new PriceCalculator();

    public AerostrikeWriteListener(AerospikeClient client) {
        this.client = client;
    }

    @Override
    public void onSuccess(Key key) {
        Travel travel = (Travel) client.get(null, key).bins.get("travelobject");
        travel.setPrice(priceCalculator.calculatePrice(travel));
        Bin bin = new Bin("travelobject", Value.getAsBlob(travel));
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        client.put(writePolicy, key, bin);

    }

    @Override
    public void onFailure(AerospikeException e) {

    }
}
