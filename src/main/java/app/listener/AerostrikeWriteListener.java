package app.listener;

import com.aerospike.client.*;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.WritePolicy;
import app.common.PriceCalculatorService;
import app.travelagency.Travel;

public class AerostrikeWriteListener implements WriteListener {
    private final AerospikeClient client;
    private final PriceCalculatorService priceCalculatorService = new PriceCalculatorService();

    public AerostrikeWriteListener(AerospikeClient client) {
        this.client = client;
    }

    @Override
    public void onSuccess(Key key) {
        Travel travel = (Travel) client.get(null, key).bins.get("travelobject");
        travel.setPrice(priceCalculatorService.calculatePrice(travel));
        Bin bin = new Bin("travelobject", Value.getAsBlob(travel));
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.sendKey = true;
        client.put(writePolicy, key, bin);

    }

    @Override
    public void onFailure(AerospikeException e) {

    }
}
