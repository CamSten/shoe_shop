package Control;
import Model.OrderPost;
import Model.Product;
import Model.ProductTerm;
import Model.ShoeSpecification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseRelay implements Subscriber {
    static String DBuserId = "dbtj-user";
    static String DBpassword = "newPassword";
    private static Connection c;
    private static ApplicationManager applicationManager;

    public DatabaseRelay(ApplicationManager applicationManager) throws SQLException {
        this.applicationManager = applicationManager;
        this.c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Webshop8?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                DBuserId,
                DBpassword);
    }
    private static void getShoesFromDB(Event event, String choice) throws SQLException, ClassNotFoundException {
        System.out.println("GETSHOES IN DBR IS REACHED");
        Event.Outcome outcome = Event.Outcome.OK;
        List<String> outputList = new ArrayList<>();
        Set<String> results = new LinkedHashSet<>();
        ProductTerm productTerm = null;
        if (event.getContents() instanceof ProductTerm) {
            productTerm = (ProductTerm) event.getContents();
        }
        if (event.getContents() instanceof ProductTerm) {
            productTerm = (ProductTerm) event.getContents();
            System.out.println(" in GETSHOES, productTerm is: " + productTerm);
            switch (productTerm) {
                case ProductTerm.Category -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where category = ? and quantity > ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case ProductTerm.Brand -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ? and quantity > ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case ProductTerm.Color -> {
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
        } else if (productTerm != null) {
            switch (productTerm) {
                case ProductTerm.Category -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where category = ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case ProductTerm.Brand -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where brand = ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
                case ProductTerm.Color -> {
                    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where color = ?");
                    executeShoeQuery(productTerm, choice, -1, s);
                }
            }
        }
        System.out.println("outputList.size: " + outputList.size());
        if (outputList.isEmpty()) {
            outcome = Event.Outcome.FAILURE;
        }
        applicationManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, outputList, productTerm));
    }
    // customer.firstName, customer.surname, product.name as 'Product', product.brand as 'Brand', shoeInventory.size AS 'size', shoeInventory.quantity as 'Quantity', shoeInventory.color as 'Color' from productOrder
    private static void executeOrderQuery(int customerId) throws SQLException, ClassNotFoundException {
        List<OrderPost> orders = new ArrayList<>();
        Event.Outcome outcome = Event.Outcome.OK;
        PreparedStatement s = c.prepareStatement("SELECT * from order_Inventory where customerId = ?");
        s.setInt(1, customerId);
        ResultSet result = s.executeQuery();
        while (result.next()){
            String brand = result.getString("brand");
            String name = result.getString("product");
            String color = result.getString("color");
            int size = result.getInt("size");
            int quantity = result.getInt("buyQuantity");
            int price = result.getInt("price");
            LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
            OrderPost order = new OrderPost(customerId, brand, name, color, size, quantity, price, date);
            orders.add(order);
        }
        if (orders.isEmpty()){
            outcome = Event.Outcome.NOT_FOUND;
        }
        applicationManager.Update(new Event(Event.Phase.COMPLETE, Event.Action.VIEW, Event.Subject.CART, Event.Origin.LOGIC, outcome, orders, null));
    }
    private static void executeShoeQuery(ProductTerm productTerm, String choice, int id, PreparedStatement p) throws SQLException, ClassNotFoundException {
        System.out.println("getResultList in DBR is reached");
        List<Product> foundShoes = new ArrayList<>();
        List<Product> shoesToSend = new ArrayList<>();
        Event.Outcome outcome = Event.Outcome.OK;
        ProductTerm thisProductTerm = productTerm;

        if (productTerm == null) {
            thisProductTerm = ProductTerm.Name;
            p.setInt(1, id);
        } else {
            p.setString(1, choice);
        }
        p.setInt(2, 0);
        ResultSet r = p.executeQuery();
        while (r.next()) {
            int thisId = r.getInt("productId");
            System.out.println("productId: " + r.getInt("productId"));
            String  name = r.getString("productName");
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
        }
        if (shoesToSend.size() == 1) {
            Product shoe = shoesToSend.getFirst();
            applicationManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoe, thisProductTerm));
        } else {
            applicationManager.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, outcome, shoesToSend, thisProductTerm));
        }
    }
    private static Product createShoe(int id, String name, String brand, String color, String description, int size, int quantity, int price){
        Product newShoe = new Product(id, name, brand, description, price);
        ShoeSpecification sc = new ShoeSpecification(size, color, quantity);
        newShoe.addSpecification(sc);
        return newShoe;
    }

    private boolean callAddToCart(int customerId, int productId, int size, String color, int buyQuantity) throws ClassNotFoundException, SQLException {
        System.out.println("callAddToCart is reached in DBR");
        CallableStatement s = c.prepareCall("CALL addToCart(?, ?, ?, ?, ?, ?)");
        s.setInt(1, customerId);
        s.setInt(2, productId);
        s.setInt(3, size);
        s.setString(4, color);
        s.setInt(5, buyQuantity);
        s.executeUpdate();
        boolean success = s.getBoolean(6);
        System.out.println("Success is: " + success);
        return success;
    }

    private static void addNewCustomer(List<String> userInput) throws SQLException, ClassNotFoundException {
        String newFirstname = userInput.getFirst();
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
        if (newId == -1) {
            outcome = Event.Outcome.FAILURE;
        }
        if (alreadyExists || newId == 0) {
            outcome = Event.Outcome.ALREADY_EXISTS;
        }
        applicationManager.Update(new Event(Event.Phase.COMPLETE, Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, Event.Origin.LOGIC, outcome, newId, firstName));
     //   applicationManager.Update(Event.confirmComplete(Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, outcome, newId));
    }

    public static void checkCustomer(List<String> userInput) throws ClassNotFoundException, SQLException {
        String email = userInput.getFirst();
        String userPassword = userInput.getLast().trim();
        System.out.println("userPassword is: " + userPassword);
        boolean exists = false;
        boolean validLogin = false;
        int foundId;
        Event.Outcome outcome = Event.Outcome.OK;
        CallableStatement s = c.prepareCall("CALL getCustomer(?, ?, ?, ?, ?, ?)");
        System.out.println("checkCustomer callableStatement s is reached");
        s.setInt(1, -1);
        s.setString(2, email);
        s.setString(3, userPassword);
        s.execute();
        System.out.println("s.execute is reached");
        exists = s.getBoolean(4);
        System.out.println("EXISTS IS: " + exists);
        validLogin = s.getBoolean(5);
        System.out.println("VALIDLOGIN IS: " + validLogin);
        foundId = s.getInt(6);
        boolean[] results = new boolean[]{exists, validLogin};
        if (!exists) {
            outcome = Event.Outcome.NOT_FOUND;
            System.out.println("id is -1");
        } else if (!validLogin) {
            outcome = Event.Outcome.INVALID_INPUT;
        }
        System.out.println("in checkCustomer, outcome is: " + outcome);
        System.out.println("in CheckCustomer, foundId is: " + foundId);
        applicationManager.Update(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, outcome, results, foundId));
    }

    private static List<Product> getUniqueProductValues(List<Product> allShoes) {
        List<Product> uniqueProducts = new ArrayList<>();
        for (Product p : allShoes) {
            boolean exists = false;
            if (uniqueProducts.isEmpty()){
                uniqueProducts.add(p);
            }
            else {
                for (int i = 0; i < uniqueProducts.size(); i++) {

                    Product uProduct = uniqueProducts.get(i);
                    if (uProduct.getProductId() == p.getProductId()) {
                        exists = true;
                        List<ShoeSpecification> thisSp = p.getShoeSpecifications();
                        List<ShoeSpecification> uSp = uProduct.getShoeSpecifications();
                        ShoeSpecification sp = null;
                        for (int j = 0; j < thisSp.size(); j++) {
                            boolean same = false;
                            sp = thisSp.get(j);
                            for (int k = 0; k < uSp.size(); k++){
                                ShoeSpecification up = uSp.get(k);
                                if (sp.getColor().equals(up.getColor()) && sp.getSize() == up.getSize()){
                                    same = true;
                                }
                            }
                            if (!same){
                                uProduct.addSpecification(sp);
                            }
                        }
                    }
                }
                if(!exists){
                    uniqueProducts.add(p);
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
            if (event.getExtraContents() instanceof ShoeSpecification) {
                System.out.println("Contents instance of product and extracontents instance of sizeColor");
                ShoeSpecification sc = (ShoeSpecification) event.getExtraContents();
                size = sc.getSize();
                quantity = sc.getBuyQuantity();
                System.out.println("BUYQUANTITY IS: " +  sc.getBuyQuantity());
                color = sc.getColor();
            }
            Event.Outcome outcome = Event.Outcome.OK;
            boolean result = callAddToCart(customerId, productId, size, color, quantity);
            if (!result) {
                outcome = Event.Outcome.FAILURE;
            } else {
                product.setColor(color);
                product.setSize(size);
                product.setInvQuantity(quantity);
            }
            applicationManager.Update(Event.confirmComplete(Event.Action.PURCHASE, Event.Subject.SHOE, outcome, product));
        }
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN DBR IS REACHED");
        if (event.getContents() != null) {
            System.out.println("in DBR UPDATE, CONTENTS INSTANCE OF " + event.getContents().getClass());
        }
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
                } else if (event.getContents() == null) {
                    System.out.println("contents are null");
                    getShoesFromDB(event, "");
                } else if (event.getContents() instanceof ProductTerm term || event.getContents() instanceof Integer integer || event.getContents() instanceof Product) {
                    String choice = "";
                    System.out.println("event.getContentents instance of: " + event.getContents().getClass());
                    if (event.getExtraContents() != null && event.getExtraContents() instanceof String) {
                        choice = (String) event.getExtraContents();
                    }
                    getShoesFromDB(event, choice);
                }
            }
            case CART -> {
                if (event.getAction() == Event.Action.VIEW)
                    if (event.getContents() instanceof Integer) {
                        int customerId = (Integer) event.getContents();
                        executeOrderQuery(customerId);
                    }
                }
            }
        }
    }

//private static List<Product> getShoeSubset(List<Product> allShoes, List<Integer> productIds) {
//    System.out.println("in getShoeSubset, allShoes.size is: " + allShoes.size());
//    List<Product> shoesToSend = new ArrayList<>();
//    Set<Integer> usedProductIds = new LinkedHashSet<>();
//    for (int id : productIds) {
//        for (int j = 0; j < allShoes.size(); j++) {
//            if (allShoes.get(j).getProductId() == id) {
//                if (usedProductIds.isEmpty()) {
//                    System.out.println("shoe name added to usedNames");
//                    usedProductIds.add(allShoes.get(j).getProductId());
//                    shoesToSend.add(allShoes.get(j));
//                } else {
//                    boolean toSend = true;
//                    for (int i : usedProductIds) {
//                        if (allShoes.get(j).getProductId() == i) {
//                            System.out.println(" shoe name: " + allShoes.get(j).getName() + " usedId: " + i);
//                            toSend = false;
//                        }
//                    }
//                    if (toSend) {
//                        System.out.println("shoe to send: " + allShoes.get(j).getName());
//                        usedProductIds.add(allShoes.get(j).getProductId());
//                        shoesToSend.add(allShoes.get(j));
//                        System.out.println("id: " + allShoes.get(j).getProductId() + " color: " + allShoes.get(j).getColor());
//                    }
//                }
//            }
//        }
//    }
//    System.out.println("shoesToSend.size is: " + shoesToSend.size());
//    return shoesToSend;
//}

//    private static List<Product> returnShoes(List<Integer> productIds, List<String> names, List<String> brands, List<String> colors, List<Integer> sizes, List<Integer> quantities, List<String> descriptions, List<Integer> prices) throws SQLException, ClassNotFoundException {
//        System.out.println("returnShoes in DBR is reached");
//        List<Product> allShoes = new ArrayList<>();
//        System.out.println("productIds.size is: " + productIds.size());
//        System.out.println("names.size is: " + names.size());
//        System.out.println("colors.size is: " + colors.size());
//        System.out.println("quantities.size is: " + quantities.size());
//        for (int i = 0; i < productIds.size(); i++) {
//            Product newShoe = new Product(productIds.get(i), names.get(i), brands.get(i), descriptions.get(i), prices.get(i));
//            newShoe.setColor(colors.get(i));
//            newShoe.setSize(sizes.get(i));
//            newShoe.setQuantity(quantities.get(i));
//            allShoes.add(newShoe);
//        }

// return (getShoeSubset(allShoes, productIds));
//        return getUniqueValues(allShoes, productIds, colors, sizes, quantities);
//    }

//    private static void getShoeInventoryData(Product product) throws SQLException {
//    System.out.println("getShoeInventoryData is reached");
//    String nameTerm = product.getName();
//    String colorTerm = product.getColor();
//    List<Integer> sizes = new ArrayList<>();
//    List<String> colors = new ArrayList<>();
//    List<Integer> quantities = new ArrayList<>();
//    List<ShoeSpecification> specs = new ArrayList<>();
//    PreparedStatement s = c.prepareStatement("SELECT * from shoe_view where productName = ? and quantity > ?");
//    s.setString(1, nameTerm);
//    s.setInt(2, 0);
//    ResultSet r = s.executeQuery();
//    while (r.next()) {
//        sizes.add(r.getInt("size"));
//        colors.add(r.getString("color"));
//        quantities.add(r.getInt("quantity"));
//    }
//    for (int i = 0; i < sizes.size(); i++) {
//        ShoeSpecification spec = new ShoeSpecification(sizes.get(i), colors.get(i), quantities.get(i));
//        specs.add(spec);
//    }
//    product.setSpecifications(specs);
//}
//public static List<Object> runSelect(int choice, String tableName, List<String> columns) throws ClassNotFoundException, SQLException {
//    List<Object> databaseOutput = new ArrayList<>();
//    String columnNames = "";
//    String prompt = "";
//    if (columns.size() == 1 && columns.getFirst().equals("*")) {
//        prompt = "select * from " + tableName;
//        System.out.println("column is: " + columns.getFirst());
//    } else if (!columns.isEmpty()) {
//        for (int i = 0; i < columns.size() - 1; i++) {
//            columnNames = columnNames + columns.get(i) + ", ";
//        }
//        columnNames = columnNames + columns.getLast();
//        System.out.println("String columnNames: " + columnNames);
//        prompt = "select " + columnNames + " from " + tableName;
//    }
//
//    try (Connection c = DriverManager.getConnection(
//            "jdbc:mysql://localhost:3306/Webshop5?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
//            DBuserId,
//            DBpassword);
//         Statement s = c.createStatement()) {
//        try (ResultSet r = s.executeQuery(prompt)) {
//            while (r.next()) {
//                switch (choice) {
//                    case 0: {
//                        for (String name : columns) {
//                            int thisId = r.getInt(name);
//                            databaseOutput.add(thisId + " ");
//                            System.out.println("ADDING: [" + thisId + "]");
//                        }
//                        break;
//                    }
//                    case 1: {
//                        for (String name : columns) {
//                            String thisName = r.getString(name);
//                            databaseOutput.add(thisName + " ");
//                            System.out.println("ADDING: [" + thisName + "]");
//                        }
//                        break;
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    return databaseOutput;
//}
//public static int runUpdate(ApplicationManager.Action action, String tableName, List<String> columns, String values) throws ClassNotFoundException, SQLException {
//    int databaseOutput = -1;
//    String userId = "dbtj-user";
//    String password = "newPassword";
//    String columnNames = "";
//    for (int i = 0; i < columns.size()-1; i++){
//        columnNames = columnNames + columns.get(i) + ", ";
//    }
//    columnNames = columnNames + columns.getLast();
//    System.out.println("String columnNames: " + columnNames);
//    try (Connection c = DriverManager.getConnection(
//            "jdbc:mysql://localhost:3306/Webshop5?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
//            DBuserId,
//            DBpassword);
//         Statement s = c.createStatement()) {
//        String prompt = action.toString() + tableName + "(" + columnNames+")" + " VALUES" + values;
//        System.out.println("TRY is reached in runUpdate. prompt is: " +  prompt);
//        databaseOutput = s.executeUpdate(prompt);
//        System.out.println(databaseOutput);
//    }
//    return databaseOutput;
//}
//private void sendFeedback(int r) throws SQLException, ClassNotFoundException {
//    if (r == 1){
//        applicationManager.Update(new Event(Event.Phase.COMPLETE, null, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.OK, null, null));
//    }
//    else {
//        applicationManager.Update(new Event(Event.Phase.COMPLETE, null, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.FAILURE, null, null));
//
//    }
//}
//
