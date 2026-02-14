package Model;

import Control.ApplicationManager;
import Control.Event;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo {
   private DatabaseRelay databaseRelay;
    public OrderRepo(DatabaseRelay databaseRelay ){
        this.databaseRelay = databaseRelay;
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
    private void callCheckInventory(int productId, int size, String color, int buyQuantity) throws SQLException {
        //checkShoeInventory(IN productId int, IN size int, IN color VARCHAR(15), IN removeQuantity int, OUT doesExist boolean)

        System.out.println("callCheckInventory is reached in DBR");
        CallableStatement s = c.prepareCall("CALL checkShoeInventory(?, ?, ?, ?, ?)");
        s.setInt(1, productId);
        s.setInt(2, size);
        s.setString(3, color);
        s.setInt(4, buyQuantity);
        s.executeUpdate();
        boolean found = s.getBoolean(5);
        System.out.println("found is: " + found);
    }
}
