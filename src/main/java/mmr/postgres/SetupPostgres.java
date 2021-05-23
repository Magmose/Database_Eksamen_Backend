package mmr.postgres;

public class SetupPostgres {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost/movieusers";
        String userPass = "softdb";


        UserDBImpl dbImpl = new UserDBImpl(url, userPass, userPass);
        dbImpl.createUser("Magnuz", "magnus", "klitmose", "test@test.dk", "1234", 4321);
        System.out.println(dbImpl.getAllUsers());
    }


    //String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
    //Connection conn = DriverManager.getConnection(url);
}
