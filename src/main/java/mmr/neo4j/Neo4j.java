package mmr.neo4j;

import mmr.dto.Movie;
import mmr.dto.Person;
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
