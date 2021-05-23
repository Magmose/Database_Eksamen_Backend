package mmr.neo4j;

public class SetupNeo4j {

    public static void main(String[] args) throws Exception {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password ="123";
        Neo4j nj = new Neo4j(uri, user, password);


        System.out.println("HALLOP" + nj.getUser(123));

        nj.close();
    }
}
