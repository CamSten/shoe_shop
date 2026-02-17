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

    public ProductRepo(DatabaseRelay databaseRelay, Connection c) {
        this.c = c;
        this.databaseRelay = databaseRelay;
    }

    public void getShoesFromDB(Event event, String choice) throws SQLException, ClassNotFoundException {
        ProductTerm productTerm = null;
        System.out.println("getShoesFromDB is reached in ProductRepo.   Action=" + event.getAction() + ", Phase=" + event.getPhase() + ", Subject=" + event.getSubject() + ", Outcome=" + event.getOutcome() + ", Origin=" + event.getOrigin());

        if (event.getContents() instanceof ProductTerm p && p != null) {
            productTerm = (ProductTerm) event.getContents();
            System.out.println("productTerm is:");
        } else {
            productTerm = ProductTerm.Category;
        }
        if (event.getExtraContents()!= null){
            System.out.println("extraContents in shoeRepo instanceof: " + event.getExtraContents().getClass());
        }
        switch (productTerm) {
            case Category -> DBRetrievalOnCategory(productTerm, choice);
            case Brand -> DBRetrievalOnBrand(productTerm, choice);
            case Color -> DBRetrievalOnColor(productTerm, choice);
        }
    }
    private List<Product> executeShoeQuery(ProductTerm productTerm, String choice, int id, PreparedStatement p) throws SQLException, ClassNotFoundException {
        System.out.println("executeShoeQuery is reached");
        List<Product> foundShoes = new ArrayList<>();
        if (productTerm == null) {
            productTerm = ProductTerm.Name;
        } else if (!choice.equals("")) {
            p.setString(1, choice);
        } else {
            p.setInt(1, id);
        }
        p.setInt(2, 0);
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
            getUniqueProductValues(foundShoes, createShoe(thisId, name, brand, color, description, size, invQuantity, price));
        }
        return foundShoes;
    }

    private static Product createShoe(int id, String name, String brand, String color, String description, int size, int invQuantity, int price) {
        Product newShoe = new Product(id, name, brand, description, price);
        ShoeSpecification sc = new ShoeSpecification(size, color, invQuantity);
        newShoe.addSpecification(sc);
        return newShoe;
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
                            break;
                        }
                    }
                    if (!exists) {
                        System.out.println("adding specification: \nCOLOR: " + spec.getColor() + "\nSIZE: " + spec.getSize() + "\nINVQUANTITY: " + spec.getInvQuantity());
                        existingProduct.addSpecification(spec);
                    }
                }
            }
        }
    }
    private void DBRetrievalAllBrands()  throws SQLException, ClassNotFoundException {
        System.out.println("DBRetrievalAllBrands is reached");
        Event.Outcome outcome = Event.Outcome.OK;
        Set<String>brandSet = new LinkedHashSet<>();
        PreparedStatement s = c.prepareStatement("SELECT * from  shoe_view");
        ResultSet r = s.executeQuery();
        while (r.next()) {
            brandSet.add(r.getString("brand"));
        }
        List<String> uniqueBrands = new ArrayList<>();
        uniqueBrands.addAll(brandSet);
        if (uniqueBrands.isEmpty()){
            outcome = Event.Outcome.NOT_FOUND;
        }
        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, uniqueBrands, ProductTerm.Brand));
    }
    private void DBRetrievalAllColors()  throws SQLException, ClassNotFoundException  {
        Event.Outcome outcome = Event.Outcome.OK;
        Set<String>colorSet = new LinkedHashSet<>();
        PreparedStatement s = c.prepareStatement("SELECT * from  shoe_view");
        ResultSet r = s.executeQuery();
        while (r.next()) {
            colorSet.add(r.getString("color"));
        }
        List<String> uniqueColors = new ArrayList<>();
        uniqueColors.addAll(colorSet);
        if (uniqueColors.isEmpty()){
            outcome = Event.Outcome.NOT_FOUND;
        }
        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, uniqueColors, ProductTerm.Color));
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
                PreparedStatement p = c.prepareStatement("SELECT * from shoe_view where productId = ? and invQuantity > ?");
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
        System.out.println("DBRetrievalOnBrand is reached");
        Event.Outcome outcome = Event.Outcome.OK;
        PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ? and invQuantity > ?");
        List<Product> allProducts = executeShoeQuery(productTerm, choice, -1, s);
        if(allProducts.isEmpty()){
            outcome = Event.Outcome.FAILURE;
        }
        databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, allProducts, productTerm));
    }

    private void DBRetrievalOnColor(ProductTerm productTerm, String choice) throws SQLException, ClassNotFoundException {
        Event.Outcome outcome = Event.Outcome.OK;
        PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ? and invQuantity > ?");
        List<Product> allProducts = executeShoeQuery(productTerm, choice, -1, s);
        if(allProducts.isEmpty()){
            outcome = Event.Outcome.FAILURE;
        }
        databaseRelay.Relay(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, allProducts, productTerm));
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        String choice = "";
        if (event.getExtraContents() instanceof String extra) {
            choice = extra;
            System.out.println("choice is: " + choice);
            getShoesFromDB(event, choice);
        }
        else if (event.getContents() instanceof ProductTerm productTerm){
            switch (productTerm){
                case Category -> {
                    getShoesFromDB(event, choice);
                }
                case Brand ->  {
                    DBRetrievalAllBrands();
                }
                case Color -> {
                    DBRetrievalAllColors();
                }
            }
        }
    }
}