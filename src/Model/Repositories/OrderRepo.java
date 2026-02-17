package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DatabaseRelay;
import Model.DataHandling.OrderPost;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo implements Subscriber {
    private DatabaseRelay databaseRelay;
    private Connection c;
    private Event currentEvent;
    private Event.Action action;
    private boolean lastInStock = false;

    public OrderRepo(DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }
    private void executeOrderQuery(int customerId, boolean admin) throws SQLException, ClassNotFoundException {
        System.out.println("executeOrderQuery in DBR reached for customerId: " + customerId);
        List<OrderPost> orders = new ArrayList<>();
        Event.Outcome outcome = Event.Outcome.OK;
        ResultSet rs = null;
        if (admin){
            PreparedStatement s = c.prepareStatement("SELECT * FROM order_inventory");
            rs = s.executeQuery();
        }
        else {
            PreparedStatement s = c.prepareStatement("SELECT * FROM order_inventory WHERE customerId = ?");
            s.setInt(1, customerId);
            rs = s.executeQuery();
        }
        while (rs.next()) {
            if (admin){
                customerId = rs.getInt("customerId");
            }
            int productId = rs.getInt("productId");
            String productName = rs.getString("product");
            String brand = rs.getString("brand");
            String color = rs.getString("color");
            int size = rs.getInt("size");
            int price = rs.getInt("price");
            int buyQuantity = rs.getInt("buyQuantity");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            OrderPost post = new OrderPost(customerId, productId, brand, productName, color, size, buyQuantity, price, date);
            orders.add(post);
        }
        System.out.println("Orders fetched: " + orders.size());
        if (orders.isEmpty()){
            outcome = Event.Outcome.FAILURE;
        }
        databaseRelay.Relay(new Event(Event.Phase.DISPLAY, action, Event.Subject.CART, Event.Origin.LOGIC, outcome, orders, null
        ));
    }

    private void purchaseActions(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("PurchaseActions in DBR was reached");
        OrderPost thisOrder = null;
        Event.Outcome outcome = Event.Outcome.OK;
        int customerId = databaseRelay.getCustomerId();

        if (event.getContents() instanceof OrderPost){
            thisOrder = (OrderPost) event.getContents();
            boolean result = callAddToCart(customerId, thisOrder.getName(), thisOrder.getProductId(), thisOrder.getSize(), thisOrder.getColor(), thisOrder.getQuantity());
            if (!result) {
                outcome = Event.Outcome.FAILURE;
            }
        }
        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.PURCHASE, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, thisOrder, lastInStock));
    }

    private boolean callAddToCart(int customerId, String productName, int productId, int size, String color, int buyQuantity) throws ClassNotFoundException, SQLException {
        System.out.println("callAddToCart is reached in DBR, customerId is: " + customerId + " productId is: " + productId + " size is: " + size + " color is: " + color + " buyQuantity is: " + buyQuantity);
        this.lastInStock = false;
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        CallableStatement s = c.prepareCall("CALL addToCart(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        s.setInt(1, customerId);
        s.setString(2, productName);
        s.setInt(3, productId);
        s.setInt(4, size);
        s.setString(5, color);
        s.setInt(6, buyQuantity);
        s.setTimestamp(7, t);
        s.registerOutParameter(8, Types.BOOLEAN);
        s.registerOutParameter(9, Types.INTEGER);
        s.execute();
        boolean success = s.getBoolean(8);
        System.out.println("success is: " + success);
        int updatedStock = s.getInt(9);
        System.out.println("updated stock: " + updatedStock);
        if (updatedStock < 1){
            this.lastInStock = true;
        }
        return success;
    }
    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        this.currentEvent = event;
        this.action = currentEvent.getAction();
        boolean admin = false;
        int customerId = -1;
        if (event.getAction() == Event.Action.PURCHASE) {
            purchaseActions(event);
        }
        else {
            if (event.getExtraContents() instanceof Event.Subject subject && subject == Event.Subject.ADMIN) {
                admin = true;
            }
            else if (event.getContents() instanceof Integer) {
                customerId = (Integer) event.getContents();
                System.out.println("customerId:" + customerId);
            }
            executeOrderQuery(customerId, admin);
        }
    }
}