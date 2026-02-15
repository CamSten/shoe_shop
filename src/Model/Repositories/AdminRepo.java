package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DatabaseRelay;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminRepo implements Subscriber {
    private DatabaseRelay databaseRelay;
    private Connection c;

    public AdminRepo (DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }

    private void getOrders(){

    }
    private void getInventory(){
        PreparedStatement s = c.prepareStatement("select * from order_Inventory");
        ResultSet rs = s.executeQuery();
    }
    private void getSales(){

    }
    private void assessIfAdmin(Event event) throws SQLException, ClassNotFoundException {
        List<String> userInput = new ArrayList<>();
        boolean isAdmin = false;
        Event.Outcome outcome = Event.Outcome.NOT_FOUND;
        if (event.getContents() instanceof List list){
            if (!list.isEmpty() && list.getFirst() instanceof String){
                userInput = (List<String>) event.getContents();
                String email = userInput.get(0).trim();
                String userPassword = userInput.get(1).trim();

                CallableStatement s = c.prepareCall("CALL checkIfAdmin(?, ?, ?)");
                s.setString(1, email);
                s.setString(2, userPassword);
                ResultSet rs = s.executeQuery();
                isAdmin = s.getBoolean(3);
            }
        }
        if (isAdmin){
            outcome = Event.Outcome.OK;
        }
        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.ADMIN, Event.Origin.LOGIC, outcome, userInput, null));
    }

    private void relay(Event event) throws SQLException, ClassNotFoundException {
        databaseRelay.Relay(event);
    }
    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        switch (event.getSubject()){
            case SALES -> {
                getSales();
            }
            case STOCK -> {
                getInventory();
            }
            case CART -> {
                getOrders();
            }
            case ADMIN -> {
                assessIfAdmin(event);
            }
        }

    }
}
