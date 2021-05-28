package mmr.postgres;

public class SetupPostgres {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost/movieusers";
        String userPass = "softdb";
        UserDBImpl dbImpl = new UserDBImpl(url, userPass, userPass);

        dbImpl.setupData();


        dbImpl.createPremiumUser("TEST123",
                "FORNAVN",
                "EFTERNAVN",
                "MEIL@EMAL.COM",
                "QWERTY",
                6969,
                "std",
                1242234,
                "gold",
                1233345345);

        dbImpl.createUser("TEST321",
                "FORNAVN",
                "EFTERNAVN",
                "MEAIEMAIL.COM",
                "QWERTY",
                6969,
                "std");

        System.out.println("USER ID 2");
        System.out.println(dbImpl.getUser("TEST321").toString());

        System.out.println("ALL USERNAMES");
        dbImpl.getAllUsers().forEach(user -> System.out.println(user.getUsername()));


    }


    //String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
    //Connection conn = DriverManager.getConnection(url);
}
