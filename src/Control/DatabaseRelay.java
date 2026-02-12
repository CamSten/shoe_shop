package Control;

import Model.OrderPost;
import Model.Product;
import Model.ProductTerm;
import Model.ShoeSpecification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseRelay {
    private Connection connection;
    private ApplicationManager appManager;

    public DatabaseRelay(ApplicationManager appManager) throws SQLException {
        this.appManager = appManager;
        String url = "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root"; // byt till ditt användarnamn
        String pass = "";     // byt till ditt lösenord
        connection = DriverManager.getConnection(url, user, pass);
    }

    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN DBR IS REACHED");

        if (event.getContents() != null) {
            System.out.println("in DBR UPDATE, CONTENTS INSTANCE OF " + event.getContents().getClass());
        }

        switch (event.getSubject()) {
            case CUSTOMER -> {
                if (event.getAction() == Event.Action.VALIDATE && event.getContents() instanceof List<?> list) {
                    checkCustomer((List<String>) list);
                } else if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getContents() instanceof List<?> list) {
                    addNewCustomer((List<String>) list);
                }
            }

            case SHOE -> {
                if (event.getAction() == Event.Action.PURCHASE) {
                    purchaseActions(event);
                } else if (event.getContents() == null) {
                    getShoesFromDB(event, "");
                } else if (event.getContents() instanceof ProductTerm || event.getContents() instanceof Integer || event.getContents() instanceof Product) {
                    String choice = "";
                    if (event.getExtraContents() instanceof String) choice = (String) event.getExtraContents();
                    getShoesFromDB(event, choice);
                }
            }

            case CART -> {
                if (event.getAction() == Event.Action.VIEW && event.getContents() instanceof Integer customerId) {
                    executeOrderQuery(customerId);
                }
            }
        }
    }

    private void checkCustomer(List<String> userInput) {
        // Här skriver du din tidigare checkCustomer logik
        System.out.println("checkCustomer logic not implemented yet, input: " + userInput);
    }

    private void addNewCustomer(List<String> userInput) {
        // Här skriver du din tidigare addNewCustomer logik
        System.out.println("addNewCustomer logic not implemented yet, input: " + userInput);
    }

    private void getShoesFromDB(Event event, String choice) throws SQLException, ClassNotFoundException {
        System.out.println("getShoesFromDB reached, choice: " + choice);
        List<Product> products = new ArrayList<>();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM shoe_view ORDER BY brand, name");

        while (rs.next()) {
            int productId = rs.getInt("productId");
            String name = rs.getString("name");
            String brand = rs.getString("brand");
            String description = rs.getString("description");
            int price = rs.getInt("price");
            String color = rs.getString("color");
            int size = rs.getInt("size");
            int invQuantity = rs.getInt("quantity");

            // Kolla om produkten redan finns i listan
            Product prod = null;
            for (Product p : products) {
                if (p.getProductId() == productId) {
                    prod = p;
                    break;
                }
            }
            if (prod == null) {
                prod = new Product(productId, name, brand, description, price);
                products.add(prod);
            }
            prod.addSpecification(new ShoeSpecification(size, color, invQuantity));
        }

        appManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE,
                Event.Origin.LOGIC, Event.Outcome.OK, products, null));
    }

    private void executeOrderQuery(int customerId) throws SQLException, ClassNotFoundException {
        System.out.println("executeOrderQuery in DBR reached for customerId: " + customerId);
        Event.Outcome outcome = Event.Outcome.OK;
        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery(
                "SELECT firstname, surname, name, brand, size, quantity FROM order_inventory WHERE customerId = " + customerId
        );
        List<OrderPost> orders = new ArrayList<>();
        while (result.next()) {
            String brand = result.getString("brand");
            String name = result.getString("product");
            String color = result.getString("color");
            int size = result.getInt("size");
            int quantity = result.getInt("quantity"); // invQuantity from DB
            int price = result.getInt("price");
            LocalDateTime date = result.getTimestamp("date").toLocalDateTime();

            OrderPost order = new OrderPost(customerId, brand, name, color, size, quantity, price, date);
            orders.add(order);
        }

        if (orders.isEmpty()) {
            outcome = Event.Outcome.NOT_FOUND;
        }
        appManager.Update(new Event(
                Event.Phase.COMPLETE, Event.Action.VIEW, Event.Subject.CART, Event.Origin.LOGIC,
                outcome, orders, null
        ));
    }
    private void purchaseActions(Event event) {
        System.out.println("purchaseActions reached");

        if (event.getContents() instanceof Product product) {
            for (ShoeSpecification spec : product.getSizeColors()) {
                int newInv = spec.getInvQuantity() - spec.getBuyQuantity();
                spec.setInvQuantity(newInv);
                spec.setBuyQuantity(0);
            }
        }
    }
}
