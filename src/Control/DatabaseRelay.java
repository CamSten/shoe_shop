package Control;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseRelay implements Subscriber{
    static String DBuserId = "dbtj-user";
    static String DBpassword = "newPassword";
    private static ApplicationManager applicationManager;

    public DatabaseRelay(ApplicationManager applicationManager){
        this.applicationManager = applicationManager;

    }
    public static List<Object> runSelect(int choice, String tableName, List<String> columns) throws ClassNotFoundException, SQLException {
        List<Object> databaseOutput = new ArrayList<>();
        String columnNames = "";
        String prompt = "";
        if (columns.size() == 1 && columns.getFirst().equals("*")) {
            prompt = "select * from " + tableName;
            System.out.println("column is: " + columns.getFirst());}
        else if (!columns.isEmpty()){
        for (int i = 0; i < columns.size()-1; i++){
            columnNames = columnNames + columns.get(i) + ", ";
        }
        columnNames = columnNames + columns.getLast();
        System.out.println("String columnNames: " + columnNames);
           prompt = "select " + columnNames + " from " + tableName;
        }

        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                DBuserId,
                DBpassword);
             Statement s = c.createStatement()) {
            try(ResultSet r = s.executeQuery(prompt)) {
                while (r.next()) {
                    switch (choice) {
                        case 0: {
                            for (String name : columns) {
                                int thisId = r.getInt(name);
                                databaseOutput.add(thisId + " ");
                                System.out.println("ADDING: [" + thisId + "]");
                            }
                            break;
                        }
                        case 1: {
                            for (String name : columns) {
                                String thisName = r.getString(name);
                                databaseOutput.add(thisName + " ");
                                System.out.println("ADDING: [" + thisName + "]");
                            }
                            break;
                        }
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return databaseOutput;
    }
    public static boolean CallAddToCart(int customerId, int productOrderId, int productId, int size, int newQuantity) throws ClassNotFoundException, SQLException {
        boolean result = false;
        String userId = "dbtj-user";
        String password = "newPassword";

       Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                userId,
                password);
             //customerId int, productOrderId int, productId int, size int, newQuantity int
             CallableStatement s = c.prepareCall("CALL addToCart(?, ?, ?, ?, ?, ?)");
             s.setInt(1, customerId);
             s.setInt(2, productOrderId);
             s.setInt(3, productId);
             s.setInt(4, size);
             s.setInt(5, newQuantity);
             result = s.execute();

        return result;
    }
    private static void addNewCustomer(List<String> userInput) throws SQLException, ClassNotFoundException {
        String newFirstname = userInput.getFirst();
        String newSurname = userInput.get(1);
        String newPassword = userInput.get(2);
        String newStreet = userInput.get(3);
        String newCity = userInput.get(4);
        String newEmail = userInput.get(5);
        addNewCustomerToDB(newFirstname, newSurname, newPassword, newStreet, newCity, newEmail);
    }
    public static void addNewCustomerToDB(String firstName, String surname, String userPassword, String streetAddress, String city, String email) throws ClassNotFoundException, SQLException {
        System.out.println("addNewCustomerToDB in DBR is reached");
        int newId = -1;
        Event.Outcome outcome = Event.Outcome.OK;
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                DBuserId,
                DBpassword);
        CallableStatement s = c.prepareCall("CALL addCustomer(?, ?, ?, ?, ?, ?, ?, ?)");
        s.setString(1, firstName);
        s.setString(2, surname);
        s.setString(3, userPassword);
        s.setString (4, streetAddress);
        s.setString(5, city);
        s.setString(6, email);
        s.execute();
        newId = s.getInt(7);
        boolean alreadyExists = s.getBoolean(8);
        System.out.println("alreadyExists is: " + alreadyExists);
        if (newId == -1){
            outcome = Event.Outcome.FAILURE;
        }
        if (alreadyExists || newId == 0){
            outcome = Event.Outcome.ALREADY_EXISTS;
        }
        applicationManager.Update(Event.confirmComplete(Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, outcome, newId));
    }

    public static boolean[] checkCustomer(List<String> userInput) throws ClassNotFoundException, SQLException {
        String email = userInput.getFirst();
        String userPassword = userInput.getLast().trim();
        System.out.println("userPassword is: " + userPassword);
        boolean exists = false;
        boolean validLogin = false;
        int foundId = -1;
        Event.Outcome outcome = Event.Outcome.OK;

        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                DBuserId,
                DBpassword);
        //customerId int, productOrderId int, productId int, size int, newQuantity int
        CallableStatement s = c.prepareCall("CALL getCustomer(?, ?, ?, ?, ?, ?)");
        System.out.println("checkCustomer callableStatement s is reached");
        s.setInt(1, -1);
        s.setString(2, email);
        s.setString (3, userPassword);

        s.execute();
        System.out.println("s.execute is reached");
        exists = s.getBoolean(4);
        System.out.println("EXISTS IS: " + exists);
        validLogin = s.getBoolean(5);
        System.out.println("VALIDLOGIN IS: " + validLogin);
        foundId = s.getInt(6);
        boolean[] results = new boolean[] {exists, validLogin};
        if (!exists){
            outcome = Event.Outcome.NOT_FOUND;
            System.out.println("id is -1");
        }
        else if (!validLogin){
            outcome = Event.Outcome.INVALID_INPUT;
        }
        System.out.println("in checkCustomer, outcome is: " + outcome);
        applicationManager.Update(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, outcome, results, foundId));
        return results;
    }
    public static int runUpdate(ApplicationManager.Action action, String tableName, List<String> columns, String values) throws ClassNotFoundException, SQLException {
        int databaseOutput = -1;
        String userId = "dbtj-user";
        String password = "newPassword";
        String columnNames = "";
        for (int i = 0; i < columns.size()-1; i++){
            columnNames = columnNames + columns.get(i) + ", ";
        }
        columnNames = columnNames + columns.getLast();
        System.out.println("String columnNames: " + columnNames);
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                DBuserId,
                DBpassword);
             Statement s = c.createStatement()) {
            String prompt = action.toString() + tableName + "(" + columnNames+")" + " VALUES" + values;
            System.out.println("TRY is reached in runUpdate. prompt is: " +  prompt);
            databaseOutput = s.executeUpdate(prompt);
            System.out.println(databaseOutput);
        }
        return databaseOutput;
    }
    private void sendFeedback(int r) throws SQLException, ClassNotFoundException {
        if (r == 1){
            applicationManager.Update(new Event(Event.Phase.COMPLETE, null, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.OK, null, null));
        }
        else {
            applicationManager.Update(new Event(Event.Phase.COMPLETE, null, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.FAILURE, null, null));

        }
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN DBR IS REACHED");
        switch (event.getSubject()){
            case CUSTOMER -> {
                if (event.getAction() == Event.Action.VALIDATE && event.getContents() instanceof List list){
                    checkCustomer((List<String>) list);
                }
                else if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getContents() instanceof List list){
                    addNewCustomer((List<String>) list);
                }
            }
        }
    }
}
