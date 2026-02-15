package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DataHandling.Customer;
import Model.DatabaseRelay;

import java.sql.*;
import java.util.List;

public class CustomerRepo implements Subscriber {
    private static DatabaseRelay databaseRelay;
    private static Connection c;

    public CustomerRepo(DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }
    private void getCustomerInfo(int customerId) throws SQLException, ClassNotFoundException {
        Customer customer = null;
        Event.Outcome outcome = Event.Outcome.OK;
        PreparedStatement s = c.prepareStatement("SELECT * from customer where id = ?");
        s.setInt(1, customerId);
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            String firstName = rs.getString("firstName");
            String surname = rs.getString("surname");
            String streetAddress = rs.getString("streetAddress");
            String city = rs.getString("city");
            String email = rs.getString("email");
            String password = rs.getString("password");
            customer = new Customer(customerId, firstName, surname, streetAddress, city, email, password);
        }
        if (customer == null){
            outcome = Event.Outcome.NOT_FOUND;
        }
        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.VIEW, Event.Subject.CUSTOMER, Event.Origin.LOGIC, outcome, customer, null));
    }
    private void editCustomerInfo(Customer customer){


    }

    public static void checkCustomer(List<String> userInput) throws ClassNotFoundException, SQLException {
        String email = userInput.get(0).trim();
        String userPassword = userInput.get(1).trim();
        boolean exists;
        boolean validLogin;
        int foundId;
        Event.Outcome outcome = Event.Outcome.OK;

        CallableStatement s = c.prepareCall("CALL getCustomer(?, ?, ?, ?, ?, ?)");
        s.setInt(1, -1);
        s.setString(2, email);
        s.setString(3, userPassword);
        s.registerOutParameter(4, Types.BOOLEAN);
        s.registerOutParameter(5, Types.BOOLEAN);
        s.registerOutParameter(6, Types.INTEGER);
        s.execute();

        exists = s.getBoolean(4);
        validLogin = s.getBoolean(5);
        foundId = s.getInt(6);

        boolean[] results = new boolean[]{exists, validLogin};

        if (!exists) outcome = Event.Outcome.NOT_FOUND;
        else if (!validLogin) outcome = Event.Outcome.INVALID_INPUT;
        System.out.println("found id in customerRepo is: " + foundId);

        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, outcome, results, foundId));
    }

    private static void addNewCustomer(List<String> userInput) throws SQLException, ClassNotFoundException {
        String newFirstname = userInput.get(0);
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

        CallableStatement s = c.prepareCall("CALL addCustomer(?, ?, ?, ?, ?, ?, ?, ?)");
        s.setString(1, firstName);
        s.setString(2, surname);
        s.setString(3, userPassword);
        s.setString(4, streetAddress);
        s.setString(5, city);
        s.setString(6, email);
        s.execute();

        newId = s.getInt(7);
        boolean alreadyExists = s.getBoolean(8);
        System.out.println("alreadyExists is: " + alreadyExists);

        if (newId == -1 || alreadyExists) {
            outcome = Event.Outcome.ALREADY_EXISTS;
        }
        databaseRelay.Relay(Event.confirmComplete(Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, outcome, newId));
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("CustomerRepo is reached, event is: " + event.getAction());
        if (event.getAction() == Event.Action.VALIDATE && event.getContents() instanceof List list) {
            checkCustomer((List<String>) list);
        } else if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getContents() instanceof List list) {
            addNewCustomer((List<String>) list);
        }
        else if (event.getAction() == Event.Action.VIEW && event.getContents() instanceof Integer){
            getCustomerInfo((Integer) event.getContents());
        }
        else if (event.getAction() == Event.Action.EDIT && event.getContents() instanceof Customer customer){
            editCustomerInfo(customer);
        }
    }
}
