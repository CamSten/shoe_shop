package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DatabaseRelay;
import Model.DataHandling.ProductTerm;
import Model.DataHandling.Product;
import Model.DataHandling.ShoeSpecification;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProductRepo implements Subscriber {
    private static DatabaseRelay databaseRelay;
    private Connection c;
    private OrderRepo orderRepo;


    public ProductRepo(DatabaseRelay databaseRelay, Connection c,  OrderRepo orderRepo){
        this.orderRepo = orderRepo;
        this.c = c;
        this.databaseRelay = databaseRelay;
    }

    private void executeShoeQuery(ProductTerm productTerm, String choice, int id, PreparedStatement p) throws SQLException, ClassNotFoundException {
        System.out.println("executeShoeQuery in DBR is reached, choice is: " + choice);
        List<Product> foundShoes = new ArrayList<>();
        List<Product> shoesToSend = new ArrayList<>();
        Event.Outcome outcome = Event.Outcome.OK;
        ProductTerm thisProductTerm = productTerm;

        if (productTerm == null) {
            thisProductTerm = ProductTerm.Name;
        } else if (!choice.equals("")) {
            System.out.println("choice is: " + choice);
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
            shoesToSend = getUniqueProductValues(foundShoes);
            System.out.println("shoesToSend.size: " + shoesToSend.size());
        }
        if (shoesToSend.size() == 1) {
            Product shoe = shoesToSend.get(0);
            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoe, thisProductTerm));
        } else {
            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoesToSend, thisProductTerm));
        }
    }

    public void getShoesFromDB(Event event, String choice) throws SQLException, ClassNotFoundException {
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

            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, outputList, productTerm)
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

    private static Product createShoe(int id, String name, String brand, String color, String description, int size, int quantity, int price) {
        Product newShoe = new Product(id, name, brand, description, price);
        ShoeSpecification sc = new ShoeSpecification(size, color, quantity);
        newShoe.addSpecification(sc);
        return newShoe;
    }
    private boolean callAddToCart(int customerId, int productId, int size, String color, int buyQuantity) throws ClassNotFoundException, SQLException {
        System.out.println("callAddToCart is reached in DBR");
        orderRepo.callCheckInventory(productId, size, color, buyQuantity);
        CallableStatement s = c.prepareCall("CALL addToCart(?, ?, ?, ?, ?, ?, ?)");
        s.setInt(1, customerId);
        s.setInt(2, productId);
        s.setInt(3, size);
        s.setString(4, color);
        s.setInt(5, buyQuantity);
        s.executeUpdate();
        boolean success = s.getBoolean(6);
        boolean found = s.getBoolean(7);
        System.out.println("found is: " + found);
        System.out.println("success is: " + success);
        return success;
    }

    private static List<Product> getUniqueProductValues(List<Product> allShoes) {
        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : allShoes) {
            boolean exists = false;
            if (uniqueProducts.isEmpty()) {
                uniqueProducts.add(p);
            } else {
                for (Product uProduct : uniqueProducts) {
                    if (uProduct.getProductId() == p.getProductId()) {
                        exists = true;
                        List<ShoeSpecification> thisSp = p.getShoeSpecifications();
                        List<ShoeSpecification> uSp = uProduct.getShoeSpecifications();
                        for (ShoeSpecification sp : thisSp) {
                            boolean same = false;
                            for (ShoeSpecification up : uSp) {
                                if (sp.getColor().equals(up.getColor()) && sp.getSize() == up.getSize()) {
                                    same = true;
                                }
                            }
                            if (!same) {
                                uProduct.addSpecification(sp);
                            }
                        }
                    }
                    if (!exists) {
                        uniqueProducts.add(p);
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
        Event.Outcome outcome = Event.Outcome.OK;
        int customerId = databaseRelay.getCustomerId();

        if (event.getContents() instanceof Product) {

            product = (Product) event.getContents();
            productId = product.getProductId();
            System.out.println("in purchaseActions, product is: " + product.getName());

            if (event.getExtraContents() instanceof ShoeSpecification sc) {
                size = sc.getSize();
                quantity = sc.getBuyQuantity();
                color = sc.getColor();
            } else {
                size = product.getSize();
                quantity = product.getBuyQuantity();
                color = product.getColor();
            }
            boolean result = callAddToCart(customerId, productId, size, color, quantity);
            if (!result)
                outcome = Event.Outcome.FAILURE;
        }
        databaseRelay.Relay(Event.confirmComplete(Event.Action.PURCHASE, Event.Subject.SHOE, outcome, product));
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        if (event.getAction() == Event.Action.PURCHASE) {
            purchaseActions(event);
        } else {
            String choice = "";
            if (event.getExtraContents() instanceof String extra) choice = extra;
            getShoesFromDB(event, choice);
        }
    }
}
