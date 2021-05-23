package mmr.noe4j;

import org.neo4j.driver.*;

public class Neo4j implements AutoCloseable {
    private final Driver driver;
    private final String uri = "bolt://localhost:7687";
    private final String user = "neo4j";
    private final String password ="password";

    public Neo4j() {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
