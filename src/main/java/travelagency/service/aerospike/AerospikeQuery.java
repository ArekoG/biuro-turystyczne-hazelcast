package travelagency.service.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.query.Statement;
import travelagency.service.IQuery;

public class AerospikeQuery implements IQuery {
    @Override
    public StringBuilder getQuery() {
        AerospikeClient client = new AerospikeClient("172.28.128.4", 3000);
        Statement statement = new Statement();
        statement.setNamespace("test");
        statement.setSetName("travel");
        statement.setIndexName("idx_query");

        //    statement.setBinNames("travelobject");
        //  statement.setFilter(Filter.equal("desination", "Barcelona"));
        client.query(null, statement);
        return null;
    }
}
