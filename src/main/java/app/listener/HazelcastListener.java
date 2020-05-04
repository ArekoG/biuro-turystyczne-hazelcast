package app.listener;

import app.common.Constants;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import app.common.PriceCalculatorService;
import app.travelagency.Travel;

import java.io.Serializable;
import java.util.Objects;

public class HazelcastListener implements EntryAddedListener<Long, Travel>, EntryUpdatedListener<Long, Travel>, EntryRemovedListener<Long, Travel>, Serializable {
    private static final long serialVersionUID = 2L;
    private transient HazelcastInstance instance;
    private final PriceCalculatorService priceCalculatorService = new PriceCalculatorService();

    public HazelcastListener(HazelcastInstance instance) {
        this.instance = instance;
    }

    @Override
    public void entryAdded(EntryEvent<Long, Travel> travel) {
        if (Objects.isNull(travel.getValue().getPrice())) {
            travel.getValue().setPrice(priceCalculatorService.calculatePrice(travel.getValue()));
            instance.getMap(Constants.TRAVEL).put(travel.getKey(), travel.getValue());
        }
    }


    @Override
    public void entryRemoved(EntryEvent<Long, Travel> travelEntryEvent) {
        System.out.println("Usunięto podróż o id:" + travelEntryEvent.getKey());

    }

    @Override
    public void entryUpdated(EntryEvent<Long, Travel> travelEntryEvent) {
        System.out.println("Zaktualizowano podróż o id:" + travelEntryEvent.getKey());
    }

}
