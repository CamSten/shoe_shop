package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DataHandling.InventoryPost;
import Model.DatabaseRelay;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminRepo implements Subscriber {
    private DatabaseRelay databaseRelay;
    private Connection c;
    private Event event;
    Event.Outcome outcome = Outcome.OK;

    public AdminRepo (DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }

    private void getOrders(){
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
            outcome = Outcome.NOT_FOUND;
        }
        relay(Event.returnAdminInfo(event.getSubject(), outcome, allPosts));
        //    public OrderPost(int customerId, String brand, String name, String color, int size, int quantity, int price, LocalDateTime time) {
        //'customerId', customer.firstName, customer.surname, product.name as 'product', product.brand as 'brand', shoeInventory.size AS 'size', orderPost.orderedQuantity as 'buyQuantity', shoeInventory.color as 'color', product.price, orderingDate as 'date' from productOrder
        //inner join customer on productOrder.customerId = customer.id and productOrder.status = 'Active'
        //inner join orderPost on orderPost.productOrderId = productOrder.id
        //inner join product on orderPost.productId = product.id
        //inner join shoeInventory on product.id = shoeInventory.productId and orderPost.color = shoeInventory.color and orderPost.size = shoeInventory.size
        //order by customer.surname asc;
    }
    private void getInventory() throws SQLException {
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
            outcome = Outcome.NOT_FOUND;
        }
        relay(Event.returnAdminInfo(event.getSubject(), outcome, allPosts));
    }
    private void getSales(){
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
        //create view get_most_sold as SELECT product.name as 'Product', product.brand as 'Brand', sum(orderedQuantity) as 'Quantity' from productOrder
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
        databaseRelay.Relay();
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
