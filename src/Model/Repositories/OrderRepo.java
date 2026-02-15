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

    public OrderRepo(DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }
    private void executeOrderQuery(int customerId) throws SQLException, ClassNotFoundException {
        System.out.println("executeOrderQuery in DBR reached for customerId: " + customerId);

        List<OrderPost> orders = new ArrayList<>();

        PreparedStatement s = c.prepareStatement("SELECT * FROM order_inventory WHERE customerId = ?");
        s.setInt(1, customerId);
//customer.id AS 'customerId', customer.firstName, customer.surname, product.name as 'product', product.brand as 'brand', shoeInventory.size AS 'size', orderPost.orderedQuantity as 'buyQuantity', shoeInventory.color as 'color', product.price, orderingDate as 'date'
        ResultSet rs = s.executeQuery();
        while (rs.next()) {
            String productName = rs.getString("product");
            String brand = rs.getString("brand");
            String color = rs.getString("color");
            int size = rs.getInt("size");
            int price = rs.getInt("price");
            int buyQuantity = rs.getInt("buyQuantity");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            OrderPost post = new OrderPost(customerId, brand, productName, color, size, buyQuantity, price, date);
            orders.add(post);
        }

        System.out.println("Orders fetched: " + orders.size());
        databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.CART, Event.Origin.LOGIC, Event.Outcome.OK, orders, null
        ));
    }
    protected boolean callCheckInventory(int productId, int size, String color, int buyQuantity) throws SQLException {
//create procedure checkShoeInventory(int, OUT shoeExists boolean, OUT sizeExists boolean, OUT sizeAndColorExists boolean, OUT sufficientStock boolean)
        System.out.println("callCheckInventory is reached in DBR");
        CallableStatement s = c.prepareCall("CALL checkShoeInventory(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        s.setInt(1, productId);
        s.setInt(2, size);
        s.setString(3, color);
        s.setInt(4, buyQuantity);
        s.registerOutParameter(5, Types.BOOLEAN);
        s.registerOutParameter(6, Types.BOOLEAN);
        s.registerOutParameter(7, Types.BOOLEAN);
        s.registerOutParameter(8, Types.BOOLEAN);
        s.registerOutParameter(9, Types.INTEGER);
        s.execute();
        boolean shoeExists = s.getBoolean(5);
        boolean sizeExists = s.getBoolean(6);
        boolean sizeAndColorExists = s.getBoolean(7);
        boolean sufficientStock = s.getBoolean(8);
        int inventoryStock = s.getInt(9);
        System.out.println("in OrderRepo checkInventory, shoeExists is: " + shoeExists);
        System.out.println("in OrderRepo checkInventory, sizeExists is: " + sizeExists);
        System.out.println("in OrderRepo checkInventory, sizeAndColorExists is: " + sizeAndColorExists);
        System.out.println("in OrderRepo checkInventory, sufficientStock is: " + sufficientStock);
        System.out.println("in OrderRepo checkInventory, stock is: " + inventoryStock);
        return sufficientStock;
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        if (event.getContents() instanceof Integer){
            int customerId = (Integer) event.getContents();
            System.out.println("customerId:" + customerId);
            executeOrderQuery(customerId);
        }
    }
}
