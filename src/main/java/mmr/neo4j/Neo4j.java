package mmr.neo4j;

import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4j implements AutoCloseable {
    private final Driver driver;


    public Neo4j(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
    @Override
    public void close() throws Exception {
        driver.close();
    }

    public int getUser( final int id )
    {
        try ( Session session = driver.session() )
        {
            int idDB = session.writeTransaction( new TransactionWork<Integer>()
            {
                @Override
                public Integer execute(Transaction tx )
                {
                    Result result = tx.run( "match (n {id: $id}) return n",
                            parameters( "id", id ) );
                    return result.single().get( 0 ).get("id").asInt();
                }
            } );
           return idDB;
        }
    }



}
