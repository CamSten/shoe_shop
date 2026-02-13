package Control;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import Model.OrderPost;
import Model.Product;
import Model.ProductTerm;
import Model.ShoeSpecification;

public class DatabaseRelay implements Subscriber {

    static String DBuserId = "dbtj-user";
    static String DBpassword = "newPassword";
    private static Connection c;
    private static ApplicationManager applicationManager;

    public DatabaseRelay(ApplicationManager applicationManager) throws SQLException {
        DatabaseRelay.applicationManager = applicationManager;
        DatabaseRelay.c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                DBuserId,
                DBpassword
        );
    }

    public static void getShoesFromDB(Event event, String choice) throws SQLException, ClassNotFoundException {
        System.out.println("GETSHOES IN DBR IS REACHED");
        Event.Outcome outcome = Event.Outcome.OK;
        List<String> outputList = new ArrayList<>();
        Set<String> results = new LinkedHashSet<>();
        ProductTerm productTerm = null;

        if (event.getContents() instanceof ProductTerm) {
            productTerm = (ProductTerm) event.getContents();
        }

        if (event.getContents() instanceof ProductTerm && choice != "") {

            productTerm = (ProductTerm) event.getContents();
            System.out.println(" in GETSHOES, productTerm is: " + productTerm);
            switch (productTerm) {
                case Category -> {
                    System.out.println("case CATEGORY is reached");
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where category = ? and quantity > ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case Brand -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ? and quantity > ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case Color -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ? and quantity > ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
            }
        } else if (event.getContents() instanceof Product) {
            Product product = (Product) event.getContents();
            System.out.println("contents instance of integer");
            PreparedStatement p = c.prepareStatement("SELECT * from shoe_view where productId = ? and quantity > ?");
            executeShoeQuery(null, "", product.getProductId(), p);
        } else if (choice.equals("")) {
            if (productTerm == null) {
                productTerm = ProductTerm.Category;
            }
            System.out.println("choice is empty");
            String term = productTerm.toString();
            System.out.println("term: " + term);
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT * from shoe_view");
            while (r.next()) {
                String output = r.getString(term);
                results.add(output);
            }
            outputList.addAll(results);
            System.out.println("outputList.size: " + outputList.size());
            if (outputList.isEmpty()) {
                outcome = Event.Outcome.FAILURE;
            }

            applicationManager.Update(
                    new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, outputList, productTerm)
            );
        } else if (productTerm != null) {
            switch (productTerm) {
                case Category -> {
                    System.out.println("CATEGORY is reached");
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where category = ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case Brand -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case Color -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
            }
        }


    }

    private static void executeShoeQuery(ProductTerm productTerm, String choice, int id, PreparedStatement p) throws SQLException, ClassNotFoundException {
        System.out.println("executeShoeQuery in DBR is reached, choice is: " + choice);
        List<Product> foundShoes = new ArrayList<>();
        List<Product> shoesToSend = new ArrayList<>();
        Event.Outcome outcome = Event.Outcome.OK;
        ProductTerm thisProductTerm = productTerm;

        if (productTerm == null) {
            thisProductTerm = ProductTerm.Name;
             }
        else if (!choice.equals("")){
            System.out.println("choice is: " + choice) ;
            p.setString(1, choice);
            p.setInt(2, 0);
        }

        ResultSet r = p.executeQuery();
        while (r.next()) {
            int thisId = r.getInt("productId");
            System.out.println("productId: " + thisId);
            String name = r.getString("productName");
            String brand = r.getString("brand");
            String color = r.getString("color");
            int size = r.getInt("size");
            int quantity = r.getInt("quantity");
            String description = r.getString("description");
            int price = r.getInt("price");
            foundShoes.add(createShoe(thisId, name, brand, color, description, size, quantity, price));
        }
        if (foundShoes.isEmpty()) {
            outcome = Event.Outcome.FAILURE;
        } else {
            shoesToSend = getUniqueValues(foundShoes);
            System.out.println("shoesToSend.size: " + shoesToSend.size());
        }
        if (shoesToSend.size() == 1) {
            Product shoe = shoesToSend.get(0);
            applicationManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoe, thisProductTerm));
        } else {
            applicationManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoesToSend, thisProductTerm));
        }
    }

    private static Product createShoe(int id, String name, String brand, String color, String description, int size, int quantity, int price) {
        Product newShoe = new Product(id, name, brand, description, price);
        ShoeSpecification sc = new ShoeSpecification(size, color, quantity);
        newShoe.addSpecification(sc);
        return newShoe;
    }

    private boolean callAddToCart(int customerId, int productId, int size, String color, int newQuantity) throws ClassNotFoundException, SQLException {
        System.out.println("callAddToCart is reached in DBR");
        CallableStatement s = c.prepareCall("CALL addToCart(?, ?, ?, ?, ?, ?)");
        s.setInt(1, customerId);
        s.setInt(2, productId);
        s.setInt(3, size);
        s.setString(4, color);
        s.setInt(5, newQuantity);
        s.executeUpdate();
        boolean success = s.getBoolean(6);
        System.out.println("Success is: " + success);
        return success;
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

        applicationManager.Update(Event.confirmComplete(Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, outcome, newId));
    }

    public static void checkCustomer(List<String> userInput) throws ClassNotFoundException, SQLException {
        String email = userInput.get(0);
        String userPassword = userInput.get(1).trim();

        boolean exists;
        boolean validLogin;
        int foundId;
        Event.Outcome outcome = Event.Outcome.OK;

        CallableStatement s = c.prepareCall("CALL getCustomer(?, ?, ?, ?, ?, ?)");
        s.setInt(1, -1);
        s.setString(2, email);
        s.setString(3, userPassword);
        s.execute();

        exists = s.getBoolean(4);
        validLogin = s.getBoolean(5);
        foundId = s.getInt(6);

        boolean[] results = new boolean[]{exists, validLogin};

        if (!exists) outcome = Event.Outcome.NOT_FOUND;
        else if (!validLogin) outcome = Event.Outcome.INVALID_INPUT;

        applicationManager.Update(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, outcome, results, foundId));
    }


    private static List<Product> getUniqueValues(List<Product> allShoes) {
        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : allShoes) {
            Product existingProduct = uniqueProducts.stream()
                    .filter(up -> up.getProductId() == p.getProductId())
                    .findFirst()
                    .orElse(null);

            if (existingProduct == null) {
                uniqueProducts.add(p);
            } else {
                for (ShoeSpecification sp : p.getShoeSpecifications()) {
                    boolean specExists = existingProduct.getShoeSpecifications().stream()
                            .anyMatch(up -> up.getColor().equals(sp.getColor()) && up.getSize() == sp.getSize());
                    if (!specExists) {
                        existingProduct.addSpecification(sp);
                    }
                }
            }
        }
        return uniqueProducts;
    }

    private void purchaseActions(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("PurchaseActions in DBR was reached");
        Product product = null;
        int productId = -1;
        int size = -1;
        int quantity = -1;
        String color = "";
        int customerId = applicationManager.getCustomerId();

        if (event.getContents() instanceof Product) {
            product = (Product) event.getContents();
            productId = product.getProductId();

            if (event.getExtraContents() instanceof ShoeSpecification sc) {
                size = sc.getSize();
                quantity = sc.getInvQuantity();
                color = sc.getColor();
            }

            Event.Outcome outcome = Event.Outcome.OK;
            boolean result = callAddToCart(customerId, productId, size, color, quantity);
            if (!result) outcome = Event.Outcome.FAILURE;
            else {
                product.setColor(color);
                product.setSize(size);
                product.setInvQuantity(quantity);
            }

            applicationManager.Update(Event.confirmComplete(Event.Action.PURCHASE, Event.Subject.SHOE, outcome, product));
        }
    }
    private void executeOrderQuery(int customerId) throws SQLException, ClassNotFoundException {
        System.out.println("executeOrderQuery in DBR reached for customerId: " + customerId);

        List<List<OrderPost>> orders = new ArrayList<>();

        PreparedStatement s = c.prepareStatement("SELECT firstname, surname, name, brand, size, buyQuantity FROM order_inventory WHERE customerId = ?");
        {
            s.setInt(1, customerId);

            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                OrderPost row = new ArrayList<>();
                int customerId = rs.getInt("customerId");
                String firstName = rs.getString("firstName");
                String surname = rs.getString("surname");
              String productName = rs.getString("productName");
                String brand = rs.getString("brand");
                String color = rs.getString("color");
                int size = rs.getInt("size");
                int prixe = rs.getInt("price");
                int buyQuantity = rs.getInt("orderedQuantity");
                LocalDateTime date = rs.getTimestamp("date");
                OrderPost post = new OrderPost(customerId, brand, firstName, color, size, buyQuantity, price, date);
                orders.add(post);
            }

            System.out.println("Orders fetched: " + orders.size());
            applicationManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.CART, Event.Origin.LOGIC, Event.Outcome.OK, orders, null
            ));
        }
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN DBR IS REACHED");

        switch (event.getSubject()) {
            case CUSTOMER -> {
                if (event.getAction() == Event.Action.VALIDATE && event.getContents() instanceof List list) {
                    checkCustomer((List<String>) list);
                } else if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getContents() instanceof List list) {
                    addNewCustomer((List<String>) list);
                }
            }
            case SHOE -> {
                if (event.getAction() == Event.Action.PURCHASE) {
                    purchaseActions(event);
                } else {
                    String choice = "";
                    if (event.getExtraContents() instanceof String extra) choice = extra;
                    getShoesFromDB(event, choice);
                }
            }
            case CART -> {

            }
        }
    }
}
