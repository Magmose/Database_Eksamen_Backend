package mmr.neo4j;

import mmr.dto.CentralityTuple;
import mmr.dto.Movie;
import mmr.dto.Person;
import mmr.dto.SimilarityPair;
import mmr.dto.redis.RedisUser;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


    public int createUser(final int id, String username) {
        try (Session session = driver.session()) {
            int idDB = session.writeTransaction(new TransactionWork<Integer>() {
                @Override
                public Integer execute(Transaction tx) {
                    Result result = tx.run("create (n:User {id: $id,username: $username}) return n",
                            parameters("id", id, "username", username));
                    return result.single().get(0).get("id").asInt();
                }
            });
            return idDB;
        }
    }

    public int followUser(final int userId, final int userIdToFollow) {
        try (Session session = driver.session()) {
            int idDB = session.writeTransaction(new TransactionWork<>() {
                @Override
                public Integer execute(Transaction tx) {
                    Result result = tx.run("match (user:User {id: $userId})\n" +
                                    "match (userToFollow:User {id: $userIdToFollow})\n" +
                                    "\n" +
                                    "create (user)-[:FOLLOWS]->(userToFollow)",
                            parameters("userId", userId, "userIdToFollow", userIdToFollow));
                    return 1;
                }
            });
            return idDB;
        }
    }

    public int getUser(final int id) {
        try (Session session = driver.session()) {
            int idDB = session.writeTransaction(new TransactionWork<Integer>() {
                @Override
                public Integer execute(Transaction tx) {
                    Result result = tx.run("match (n {id: $id}) return n",
                            parameters("id", id));
                    return result.single().get(0).get("id").asInt();
                }
            });
            return idDB;
        }
    }

    public ArrayList<Movie> getAllMovies() {
        try (Session session = driver.session()) {
            ArrayList<Movie> movies = session.writeTransaction(new TransactionWork<ArrayList<Movie>>() {
                @Override
                public ArrayList<Movie> execute(Transaction tx) {
                    Result result = tx.run("match (n:Movie) return n");
                    ArrayList<Movie> movies = new ArrayList<>();
                    while (result.hasNext()) {
                        Value movieValues = result.next().get(0);
                        Movie movie = new Movie(movieValues.get("title").toString(), movieValues.get("released").toString(), movieValues.get("tagline").toString());
                        movies.add(movie);

                    }
                    return movies;
                }
            });
            return movies;
        }
    }


    public int userLikesMovie(int userId, String movieTitle) {
        try (Session session = driver.session()) {
            String idDB = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run("match (user:User {id: $userId})\n" +
                                    "match (movie:Movie {title: $movieTitle})\n" +
                                    "\n" +
                                    "create (user)-[:LIKES]->(movie)",
                            parameters("userId", userId, "movieTitle", movieTitle));
                    return "";
                }
            });
            return 1;
        }
    }


    public ArrayList<Movie> getMoviesLikedByFollowed(int userId) {
        try (Session session = driver.session()) {
            ArrayList<Movie> movies = session.writeTransaction(new TransactionWork<ArrayList<Movie>>() {
                @Override
                public ArrayList<Movie> execute(Transaction tx) {
                    Result result = tx.run("match (user:User{id: $userId})-[:FOLLOWS]-(followedUser:User)-[:LIKES]-(movies:Movie) return DISTINCT movies",
                            parameters("userId", userId));
                    ArrayList<Movie> movies = new ArrayList<>();
                    while (result.hasNext()) {
                        Value movieValues = result.next().get(0);
                        Movie movie = new Movie(movieValues.get("title").toString(), movieValues.get("released").toString(), movieValues.get("tagline").toString());
                        movies.add(movie);

                    }
                    return movies;
                }
            });
            return movies;
        }
    }

    public int userLikesPerson(int userId, String personName) {
        try (Session session = driver.session()) {
            String idDB = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run("match (user:User {id: $userId})\n" +
                                    "match (person:Person {name: $personName})\n" +
                                    "\n" +
                                    "create (user)-[:LIKES]->(person)",
                            parameters("userId", userId, "personName", personName));
                    return "";
                }
            });
            return 1;
        }
    }

    public ArrayList<Person> getPersonsLikedByFollowed(int userId) {
        try (Session session = driver.session()) {
            ArrayList<Person> person = session.writeTransaction(new TransactionWork<ArrayList<Person>>() {
                @Override
                public ArrayList<Person> execute(Transaction tx) {
                    Result result = tx.run("match (user:User{id: $userId})-[:FOLLOWS]-(followedUser:User)-[:LIKES]-(person:Person) return DISTINCT person",
                            parameters("userId", userId));
                    ArrayList<Person> persons = new ArrayList<>();
                    while (result.hasNext()) {
                        Value personValues = result.next().get(0);
                        Person person = new Person(personValues.get("name").toString(), personValues.get("born").asInt());
                        persons.add(person);

                    }
                    return persons;
                }
            });
            return person;
        }
    }

    public ArrayList<SimilarityPair> getSimiliarNodes() {
        try (Session session = driver.session()) {
            ArrayList<SimilarityPair> pairs = session.writeTransaction(new TransactionWork<ArrayList<SimilarityPair>>() {
                @Override
                public ArrayList<SimilarityPair> execute(Transaction tx) {

                    //drop graph with silient fail -> add graph to catalogue
                    System.out.println("--------------- prøver at lave graph i gds");
                    Result setup = tx.run("call gds.graph.drop(\"myGraph2\", false) yield graphName;");

                    System.out.println("--------------- resultat fra at drop  graph i gds" + setup.consume());

                    Result result = tx.run(
                            "CALL gds.graph.create(\n" +
                            "    'myGraph2',\n" +
                            "    ['User', 'Movie'],\n" +
                            "    {\n" +
                            "        LIKES: {\n" +
                            "            type: 'LIKES'\n" +
                            "        }\n" +
                            "    }\n" +
                            ");");
                    System.out.println("--------------- resultat fra at lave graph i gds" + result.consume());


                    Result result2 = tx.run("CALL gds.nodeSimilarity.stream('myGraph2')\n" +
                            "YIELD node1, node2, similarity\n" +
                            "RETURN gds.util.asNode(node1).id AS user1, gds.util.asNode(node2).id AS user2, similarity AS score\n" +
                            "ORDER BY score DESCENDING, user1, user2");


                    ArrayList<SimilarityPair> pairs = new ArrayList<>();
                    while (result2.hasNext()) {
                        Record pairValues = result2.next();
                        SimilarityPair pair = new SimilarityPair(pairValues.get("user1").asInt(), pairValues.get("user2").asInt(), pairValues.get("score").asDouble());
                        pairs.add(pair);
                    }
                    return pairs;
                }
            });
            return pairs;
        }

    }

    public ArrayList<CentralityTuple> getCentralNodes() {
        try (Session session = driver.session()) {
            ArrayList<CentralityTuple> pairs = session.writeTransaction(new TransactionWork<ArrayList<CentralityTuple>>() {
                @Override
                public ArrayList<CentralityTuple> execute(Transaction tx) {

                    //drop graph with silient fail -> add graph to catalogue
                    System.out.println("--------------- prøver at lave graph i gds");
                    Result setup = tx.run("call gds.graph.drop(\"centralityGraph\", false) yield graphName;");

                    System.out.println("--------------- resultat fra at drop  graph i gds" + setup.consume());

                    Result result = tx.run(
                            "CALL gds.graph.create(\n" +
                                    "  'centralityGraph',\n" +
                                    "  'User',\n" +
                                    "  'FOLLOWS'\n" +
                                    ")");
                    System.out.println("--------------- resultat fra at lave graph i gds" + result.consume());


                    Result result2 = tx.run("CALL gds.pageRank.stream('centralityGraph')\n" +
                            "YIELD nodeId, score\n" +
                            "RETURN gds.util.asNode(nodeId).username AS name, score\n" +
                            "ORDER BY score DESC, name ASC");


                    ArrayList<CentralityTuple> pairs = new ArrayList<>();
                    while (result2.hasNext()) {
                        Record pairValues = result2.next();
                        CentralityTuple pair = new CentralityTuple(pairValues.get("name").asString(), pairValues.get("score").asDouble());
                        pairs.add(pair);
                    }
                    return pairs;
                }
            });
            return pairs;
        }

    }

    public List<RedisUser> getTopFollowed() {
        try (Session session = driver.session()) {
            List<RedisUser> result = session.writeTransaction(new TransactionWork<List<RedisUser>>() {
                @Override
                public List<RedisUser> execute(Transaction tx) {
                    return tx.run("match (befollowed:User)<-[follows:FOLLOWS]-(user:User) return befollowed.id as id,count(follows) as score, befollowed.username as username ORDER BY COUNT(follows) DESC\n" +
                            "LIMIT 10").stream().map(response -> {
                        System.out.println(response);
                        return new RedisUser(response.get("id").toString(),
                                response.get("username").asString(),
                                response.get("score").asInt());
                    }).collect(Collectors.toList());
                }
            });
            return result;
        }
    }


    private void clearData() {
        try (Session session = driver.session()) {
            session.writeTransaction(new TransactionWork<>() {
                @Override
                public Void execute(Transaction tx) {
                    tx.run("match (n) detach delete n");
                    return null;
                }
            });
        }
    }


    public void setupData(String setupMovieData) {
        clearData();
        try (Session session = driver.session()) {
            session.writeTransaction(new TransactionWork<>() {
                @Override
                public Void execute(Transaction tx) {
                    tx.run(setupMovieData);
                    return null;
                }
            });
        }

    }


}
