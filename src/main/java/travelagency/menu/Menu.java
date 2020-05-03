package travelagency.menu;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.task.IndexTask;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import travelagency.service.IQuery;
import travelagency.service.ITravelAgency;
import travelagency.service.InputService;
import travelagency.service.Travel;
import travelagency.service.aerospike.AerospikeQuery;
import travelagency.service.aerospike.AerospikeTravelAgencyImpl;
import travelagency.service.hazelcast.HazelcastListener;
import travelagency.service.hazelcast.HazelcastQuery;
import travelagency.service.hazelcast.HazelcastTravelAgencyImpl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;

public class Menu {
    private ITravelAgency travelAgency;
    private IQuery iQuery;
    private String squadName = null;
    private final InputService inputService = new InputService();


    public int start() throws ParseException, IOException {
        int databaseChoice = inputService.getInt("Wybierz skład którego chcesz użyć:\n[1]Hazelcast\n[2]Areospike");
        if (databaseChoice == 1) {
            setUpHazelcast();
        } else if (databaseChoice == 2) {
           AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
          //   IndexTask indexTask = client.createIndex(null, "test", "travel", "idx_query", "travelobject", IndexType.GEO2DSPHERE);
         //  indexTask.waitTillComplete();
           // client.dropIndex(null,"test","travel","idx_query");

            client.close();
            setUpAerospike();
        }

        while (true) {
            switch (inputService.getOption(squadName)) {

                case 1:
                    String destination = inputService.getDestination();
                    int numberOfPeople = inputService.getInt("Podaj liczbe osób:");
                    Timestamp startDate = inputService.getStartDate();
                    Timestamp endDate = inputService.getEndDate(startDate);

                    Long id = travelAgency.add(new Travel(destination, startDate, endDate, numberOfPeople));
                    System.out.println("Dodano nową podróż:\nId:" + id + "\nLiczba osób:" + numberOfPeople + "\nData rozpoczęcia:" + startDate + "\nData końca:" + endDate);
                    System.out.println("Łączna cena za podróż zostanie obliczona wkrótce!");
                    break;
                case 2:
                    Long travelId = inputService.getId();
                    if (travelAgency.isTravelExists(travelId)) {
                        Timestamp newStartDate = inputService.getStartDate();
                        Timestamp newEndDate = inputService.getEndDate(newStartDate);
                        travelAgency.update(travelId, newStartDate, newEndDate);
                    } else {
                        System.out.println("Podróż o id " + travelId + " nie istnieje!");
                    }
                    break;
                case 3:
                    Long travelIdToRemove = inputService.getId();
                    if (travelAgency.isTravelExists(travelIdToRemove)) {
                        travelAgency.remove(travelIdToRemove);
                    } else {
                        System.out.println("Podróż o id " + travelIdToRemove + " nie istnieje!");
                    }
                    break;
                case 4:
                    travelId = inputService.getId();
                    travelAgency.getById(travelId).ifPresent(System.out::println);
                    break;
                case 5:
                    travelAgency.getAll().forEach(System.out::println);
                    break;
                case 6:
                    travelAgency.findByQuery(inputService.getQueryField(iQuery)).ifPresent(System.out::println);
                    break;
                case 7:

                    break;
                case 8:
                    System.exit(200);
                    break;
                default:
                    System.out.println("Nie ma takiej opcji");


            }


        }

    }

    private void setUpAerospike() {
        squadName = "Aerospike";
        iQuery = new AerospikeQuery();
        travelAgency = new AerospikeTravelAgencyImpl();
    }

    private void setUpHazelcast() {
        travelAgency = new HazelcastTravelAgencyImpl();
        iQuery = new HazelcastQuery();
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        setListener(hazelcastInstance);
        squadName = "Hazelcast";
    }


    private void setListener(HazelcastInstance hazelcastInstance) {
        hazelcastInstance.getMap("travel").addEntryListener(new HazelcastListener(hazelcastInstance), true);
    }

}
