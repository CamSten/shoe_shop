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
            System.out.println("IN EXECUTE QUERY, NAME IS: " + name);
            String brand = r.getString("brand");
            String color = r.getString("color");
            System.out.println("IN EXECUTE QUERY, COLOR IS: " + color);
            int size = r.getInt("size");
            System.out.println("IN EXECUTE QUERY, SIZE IS: " + size);
            int invQuantity = r.getInt("quantity");
            System.out.println("IN EXECUTE QUERY, INVQUANTITY IS:" + invQuantity);
            String description = r.getString("description");
            int price = r.getInt("price");
            foundShoes.add(createShoe(thisId, name, brand, color, description, size, invQuantity, price));
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
    }// out foundOrder boolean, out foundPost boolean, out success boolean)

    private boolean callAddToCart(int customerId, String productName, int productId, int size, String color, int buyQuantity) throws ClassNotFoundException, SQLException {
        System.out.println("callAddToCart is reached in DBR, customerId is: " + customerId + " productId is: " + productId + " size is: " + size + " color is: " + color + " buyQuantity is: " + buyQuantity);
        boolean success = false;
        boolean validPurchase = orderRepo.callCheckInventory(productName, size, color, buyQuantity);
        callGetProductOrder(customerId);
        if (validPurchase) {
            System.out.println("purchase is valid");
// (thisCustomerId int 1, thisProductName VARCHAR(30) 2, thisProductId int 3, thisSize int 4, thisColor VARCHAR(15) 5, thisNewQuantity int 6, out foundOrder boolean, out foundPost boolean, out success boolean)
            CallableStatement s = c.prepareCall("CALL addToCart(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            s.setInt(1, customerId);
            s.setString(2, productName);
            s.setInt(3, productId);
            s.setInt(4, size);
            s.setString(5, color);
            s.setInt(6, buyQuantity);
            s.registerOutParameter(7, Types.BOOLEAN);
            s.registerOutParameter(8, Types.BOOLEAN);
            s.registerOutParameter(9, Types.BOOLEAN);
            s.execute();
            boolean foundOrder = s.getBoolean(7);
            System.out.println("foundOrder is: " + foundOrder);
            boolean foundPost = s.getBoolean(8);
            System.out.println("foundPost is: " + foundPost);
            success = s.getBoolean(9);
            System.out.println("success is: " + success);
        }
        return success;
    }
private void callGetProductOrder(int customerId) throws SQLException {
        System.out.println("callGetProductOrder is reached, customerId is: " + customerId);
    CallableStatement s = c.prepareCall("CALL getProductOrder(?, ?)");
    s.setInt(1, customerId);
    s.execute();
    int orderId = s.getInt(2);
    System.out.println("in callGetProductOrder, orderId is: " + orderId);
}
    private static List<Product> getUniqueProductValues(List<Product> allShoes) {
        List<Product> uniqueProducts = new ArrayList<>();

        for (Product p : allShoes) {
            Product existingProduct = null;
            if(uniqueProducts.isEmpty()){
                uniqueProducts.add(p);
            }
            for (Product u : uniqueProducts) {
                 if (u.getName().equals(p.getName())) {
                     System.out.println("u.name: " + u.getName() + " p.name: " + p.getName());
                    existingProduct = u;
                }
            }
            if (existingProduct == null) {
                uniqueProducts.add(p);
                System.out.println("ADDED SHOE: " + p.getName() + " with ID: " + p.getProductId());
            }
            else {
                for (ShoeSpecification sp : p.getShoeSpecifications()) {
                    boolean specExists = false;
                    for (ShoeSpecification usp : existingProduct.getShoeSpecifications()) {
                        if (usp.getSize() == sp.getSize() &&
                                usp.getColor().equals(sp.getColor())) {
                            usp.setInvQuantity(sp.getInvQuantity());
                            specExists = true;
                            break;
                        }
                    }
                    if (!specExists) {
                        System.out.println("added specification: " + sp.getColor() + " " + sp.getSize() + " quantity: " + sp.getInvQuantity());
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
            boolean result = callAddToCart(customerId, product.getName(), productId, size, color, quantity);
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
