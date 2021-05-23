package mmr.neo4j;

import mmr.dto.Movie;
import org.neo4j.driver.*;

import java.util.ArrayList;

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


    public int createUser(final int id) {
        try (Session session = driver.session()) {
            int idDB = session.writeTransaction(new TransactionWork<Integer>() {
                @Override
                public Integer execute(Transaction tx) {
                    Result result = tx.run("create (n:User {id: $id}) return n",
                            parameters("id", id));
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
                    Result result = tx.run("match (user:User{id: $userId})-[:FOLLOWS]-(followedUser)-[:LIKES]-(movies) return movies",
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


    public void setupData(String setupMovieData){
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
