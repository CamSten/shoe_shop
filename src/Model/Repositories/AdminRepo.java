package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DataHandling.InventoryPost;
import Model.DataHandling.OrderPost;
import Model.DataHandling.SalesPost;
import Model.DatabaseRelay;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminRepo implements Subscriber {
    private DatabaseRelay databaseRelay;
    private Connection c;
    private Event event;
    Event.Outcome outcome = Event.Outcome.OK;

    public AdminRepo (DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }

    private void getOrders() throws SQLException, ClassNotFoundException {
        List<OrderPost> allPosts = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("select * from order_Inventory");
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            int customerId = rs.getInt("customerId");
            String brand = rs.getString("brand");
            String name  = rs.getString("name");
            String color = rs.getString("color");
            int size = rs.getInt("size");
            int buyQuantity = rs.getInt("buyQuantity");
            int price = rs.getInt("price");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            OrderPost post = new OrderPost(customerId, brand, name, color, size, buyQuantity, price, date);
            allPosts.add(post);
        }
        if (allPosts.isEmpty()){
            outcome = Event.Outcome.NOT_FOUND;
        }
        relay(Event.returnAdminInfo(event.getSubject(), outcome, allPosts));
    }
    private void getInventory() throws SQLException, ClassNotFoundException {
        List<InventoryPost> allPosts = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("select * from shoe_view");
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            String category = rs.getString("category");
            String brand = rs.getString("brand");
            String name  = rs.getString("productName");
            String color = rs.getString("color");
            int size = rs.getInt("size");
            int invQuantity = rs.getInt("quantity");
            int price = rs.getInt("price");
            InventoryPost post = new InventoryPost(category, brand, name, color, size, price, invQuantity);
            allPosts.add(post);
        }
        if (allPosts.isEmpty()){
            outcome = Event.Outcome.NOT_FOUND;
        }
        relay(Event.returnAdminInfo(event.getSubject(), outcome, allPosts));
    }
    private void getSales() throws SQLException, ClassNotFoundException {
        List<SalesPost> topSold = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("select * from get_most_sold");
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            String brand = rs.getString("Brand");
            String name = rs.getString("Product");
            int quantity = rs.getInt("Quantity");
            SalesPost post = new SalesPost(brand, name, quantity);
            topSold.add(post);
        }
        if (topSold.isEmpty()){
            outcome = Event.Outcome.NOT_FOUND;
        }
        relay(Event.returnAdminInfo(event.getSubject(), outcome, topSold));
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
        relay(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.ADMIN, Event.Origin.LOGIC, outcome, userInput, null));
    }

    private void relay(Event event) throws SQLException, ClassNotFoundException {
        event.setExtraContents(Event.Subject.ADMIN);
        databaseRelay.Relay(event);
    }
    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        this.event = event;
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
