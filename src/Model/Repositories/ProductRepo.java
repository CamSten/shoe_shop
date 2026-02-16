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
    private static Connection c;
    private OrderRepo orderRepo;


    public ProductRepo(DatabaseRelay databaseRelay, Connection c, OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
        this.c = c;
        this.databaseRelay = databaseRelay;
    }

    public void getShoesFromDB(Event event, String choice) throws SQLException, ClassNotFoundException {
        ProductTerm productTerm = null;
        if (event.getContents() instanceof ProductTerm p && p != null) {
            productTerm = (ProductTerm) event.getContents();
        } else {
            productTerm = ProductTerm.Category;
        }
        switch (productTerm) {
            case Category -> DBRetrievalOnCategory(productTerm, choice);
            case Brand -> DBRetrievalOnBrand(productTerm, choice);
            case Color -> DBRetrievalOnColor(productTerm, choice);
        }
    }
    private List<Product> executeShoeQuery(ProductTerm productTerm, String choice, int id, PreparedStatement p)
            throws SQLException, ClassNotFoundException {

        List<Product> foundShoes = new ArrayList<>();

        if (productTerm == null) {
            productTerm = ProductTerm.Name;
        } else if (!choice.equals("")) {
            p.setString(1, choice);
        } else {
            p.setInt(1, id);
        }

        ResultSet r = p.executeQuery();
        while (r.next()) {
            int thisId = r.getInt("productId");
            String name = r.getString("productName");
            String brand = r.getString("brand");
            String color = r.getString("color");
            int size = r.getInt("size");
            int invQuantity = r.getInt("invQuantity");
            String description = r.getString("description");
            int price = r.getInt("price");

            getUniqueProductValues(foundShoes,
                    createShoe(thisId, name, brand, color, description, size, invQuantity, price));
        }

        return foundShoes;
    }

//    private List<Product> executeShoeQuery(ProductTerm productTerm, String choice, int id, PreparedStatement p) throws SQLException, ClassNotFoundException {
//        System.out.println("executeShoeQuery in DBR is reached, choice is: " + choice);
//        List<Integer> productIds = new ArrayList<>();
//        List<Product> foundShoes = new ArrayList<>();
//        List<Product> shoesToSend = new ArrayList<>();
//        Event.Outcome outcome = Event.Outcome.OK;
//        ProductTerm thisProductTerm = productTerm;
//
//        if (productTerm == null) {
//            thisProductTerm = ProductTerm.Name;
//        } else if (!choice.equals("")) {
//            System.out.println("choice is: " + choice);
//            p.setString(1, choice);
//            System.out.println(p.toString());
//        } else {
//            p.setInt(1, id);
//        }
//        ResultSet r = p.executeQuery();
//        while (r.next()) {
//            int thisId = r.getInt("productId");
//            System.out.println("productId: " + thisId);
//            String name = r.getString("productName");
//            System.out.println("IN EXECUTE QUERY, NAME IS: " + name);
//            String brand = r.getString("brand");
//            String color = r.getString("color");
//            System.out.println("IN EXECUTE QUERY, COLOR IS: " + color);
//            int size = r.getInt("size");
//            System.out.println("IN EXECUTE QUERY, SIZE IS: " + size);
//            int invQuantity = r.getInt("invQuantity");
//            System.out.println("IN EXECUTE QUERY, INVQUANTITY IS:" + invQuantity);
//            String description = r.getString("description");
//            int price = r.getInt("price");
//            getUniqueProductValues(foundShoes, createShoe(thisId, name, brand, color, description, size, invQuantity, price));
//
//        }
//        if (foundShoes.isEmpty()) {
//            outcome = Event.Outcome.FAILURE;
//        } else {
//            shoesToSend = foundShoes;
//
//            System.out.println("shoesToSend.size: " + shoesToSend.size());
//        }
//        if (shoesToSend.size() == 1) {
//            Product shoe = shoesToSend.get(0);
//            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoe, thisProductTerm));
//        } else {
//            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoesToSend, thisProductTerm));
//        }
//    }

    private static Product createShoe(int id, String name, String brand, String color, String description, int size, int quantity, int price) {
        Product newShoe = new Product(id, name, brand, description, price);
        ShoeSpecification sc = new ShoeSpecification(size, color, quantity);
        newShoe.addSpecification(sc);
        return newShoe;
    }

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
        s.registerOutParameter(2, Types.INTEGER);
        s.execute();
        int orderId = s.getInt(2);
        System.out.println("in callGetProductOrder, orderId is: " + orderId);
    }

    private void getUniqueProductValues(List<Product> allShoes, Product p) throws SQLException {
        Product existingProduct = null;
        if (allShoes.isEmpty()) {
            allShoes.add(p);
        } else {
            for (Product u : allShoes) {
                if (u.getProductId() == p.getProductId()) {
                    System.out.println("u.Id: " + u.getProductId() + " p.name: " + p.getProductId());
                    existingProduct = u;
                    break;
                }
            }
            if (existingProduct == null) {
                allShoes.add(p);
                System.out.println("ADDED SHOE: " + p.getProductId());
            } else {
                for (ShoeSpecification spec : p.getShoeSpecifications()) {
                    boolean exists = false;
                    for (ShoeSpecification s : existingProduct.getShoeSpecifications()) {
                        if (s.getSize() == spec.getSize() &&
                                s.getColor().equals(spec.getColor())) {
                            exists = true;
                            s.setInvQuantity(spec.getInvQuantity());
                            break;
                        }
                    }
                    if (!exists) {
                        existingProduct.addSpecification(spec);
                    }
                }
            }

        }
    }

    private void DBRetrievalOnCategory(ProductTerm productTerm, String choice) throws SQLException, ClassNotFoundException {
        Event.Outcome outcome = Event.Outcome.OK;
        if (!choice.equals("")) {
            List<Integer> productIds = new ArrayList<>();
            List<Product> allProducts = new ArrayList<>();
            PreparedStatement s = c.prepareStatement("SELECT * from category_view where category = ?");
            s.setString(1, choice);
            ResultSet r = s.executeQuery();

            while (r.next()) {
                productIds.add(r.getInt("id"));
            }
            for (int i : productIds) {
                PreparedStatement p = c.prepareStatement("SELECT * from shoe_view where productId = ?");
                List<Product> productsFromQuery = executeShoeQuery(productTerm, "", i, p);
                allProducts.addAll(productsFromQuery);
            }
            if (allProducts.isEmpty()) {
                outcome = Event.Outcome.FAILURE;
            }
            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, allProducts, productTerm));

        } else {
            List<String> outputList = new ArrayList<>();
            Set<String> results = new LinkedHashSet<>();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT category from category_view");
            while (r.next()) {
                results.add(r.getString("category"));
            }
            outputList.addAll(results);
            if (outputList.isEmpty()) {
                outcome = Event.Outcome.FAILURE;
            }
            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, outputList, productTerm));
        }
    }
    private void DBRetrievalOnBrand(ProductTerm productTerm, String choice) throws SQLException, ClassNotFoundException {
        Event.Outcome outcome = Event.Outcome.OK;
        PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ?");
        List<Product> allProducts = executeShoeQuery(productTerm, choice, -1, s);
        if(allProducts.isEmpty()){
            outcome = Event.Outcome.FAILURE;
        }
        databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, allProducts, productTerm));
    }

    private void DBRetrievalOnColor(ProductTerm productTerm, String choice) throws SQLException, ClassNotFoundException {
        Event.Outcome outcome = Event.Outcome.OK;
        PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ?");
        List<Product> allProducts = executeShoeQuery(productTerm, choice, -1, s);
        if(allProducts.isEmpty()){
            outcome = Event.Outcome.FAILURE;
        }
        databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, allProducts, productTerm));
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
//                size = product.getSize();
//                quantity = product.getBuyQuantity();
//                color = product.getColor();
            }
            boolean result = callAddToCart(customerId, product.getName(), productId, size, color, quantity);
            if (!result) {
                outcome = Event.Outcome.FAILURE;
            }
            else {
                product.setBoughtSpecification(new ShoeSpecification(quantity, size, color));
            }
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

//    private static void getProductVariations(Product product) throws SQLException {
//        String productName = product.getName();
//        PreparedStatement s = c.prepareStatement("select * from get_product_variations WHERE name = ?");
//        s.setString(1, productName);
//        ResultSet rs =  s.executeQuery();
//        while(rs.next()){
//            String color = rs.getString("color");
//            int size = rs.getInt("size");
//            int invQuantity = rs.getInt("quantity");
//            ShoeSpecification spec = new ShoeSpecification(size, color, invQuantity);
//            product.addSpecification(spec);
//            System.out.print("\nnew specification for " + product.getName() + "\n color: " + color + "\n size: " + size + "\n quantity: " + invQuantity);
//        }
//
//    }
//

//        System.out.println("GETSHOES IN DBR IS REACHED");
//        Event.Outcome outcome = Event.Outcome.OK;
//        List<String> outputList = new ArrayList<>();
//        Set<String> results = new LinkedHashSet<>();
//        ProductTerm productTerm = null;
//
//        if (event.getContents() instanceof ProductTerm) {
//            productTerm = (ProductTerm) event.getContents();
//        }
//
//        if (event.getContents() instanceof ProductTerm && choice != "") {
//            productTerm = (ProductTerm) event.getContents();
//            System.out.println(" in GETSHOES, productTerm is: " + productTerm);
//            switch (productTerm) {
//                case Category -> {
//                    System.out.println("case CATEGORY is reached");
//
//                    List<Integer> productIds = new ArrayList<>();
//                    PreparedStatement s = c.prepareStatement("SELECT * from category_view where category = ?");
//                    System.out.println("choice is: " + choice);
//                    s.setString(1, choice);
//                    ResultSet r = s.executeQuery();
//                    while (r.next()) {
//                        System.out.println("while loop is reached");
//                        productIds.add(r.getInt("id"));
//                    }
//                    for (int i : productIds) {
//                        PreparedStatement p = c.prepareStatement("SELECT * from shoe_view where productId = ?");
//                        executeShoeQuery(productTerm, "", i, p);
//                    }
//                }
//                case Brand -> {
//                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ?");
//                    executeShoeQuery(productTerm, choice, -1, s);
//                }
//                case Color -> {
//                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ?");
//                    executeShoeQuery(productTerm, choice, -1, s);
//                }
//            }
//        } else if (event.getContents() instanceof Product) {
//            Product product = (Product) event.getContents();
//            System.out.println("contents instance of integer");
//            PreparedStatement p = c.prepareStatement("SELECT * from shoe_view where productId = ? ");
//            executeShoeQuery(null, "", product.getProductId(), p);
//        } else if (choice.equals("")) {
//            if (productTerm == null) {
//                productTerm = ProductTerm.Category;
//            }
//            System.out.println("productTerm is: " + productTerm);
//            String term = productTerm.toString();
//            Statement s = c.createStatement();
//
//            if (productTerm == ProductTerm.Category) {
//                ResultSet r = s.executeQuery("SELECT category from category_view");
//                while (r.next()) {
//                    String output = r.getString("category");
//                    results.add(output);
//                }
//            } else {
//                ResultSet r = s.executeQuery("SELECT * from shoe_view");
//                System.out.println("choice is empty");
//                System.out.println("term: " + term);
//                while (r.next()) {
//                    String output = r.getString(term);
//                    results.add(output);
//                }
//            }
//
//            outputList.addAll(results);
//            System.out.println("outputList.size: " + outputList.size());
//            if (outputList.isEmpty()) {
//                outcome = Event.Outcome.FAILURE;
//            }
//
//            databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, outputList, productTerm)
//            );
//        } else if (productTerm != null) {
//            switch (productTerm) {
//                case Category -> {
//                    System.out.println("CATEGORY is reached");
//                    PreparedStatement s = c.prepareStatement("SELECT * from category_view where category = ?");
//                    executeShoeQuery(productTerm, choice, -1, s);
//                }
//                case Brand -> {
//                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ?");
//                    executeShoeQuery(productTerm, choice, -1, s);
//                }
//                case Color -> {
//                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ?");
//                    executeShoeQuery(productTerm, choice, -1, s);
//                }
//            }
//        }
//    }
