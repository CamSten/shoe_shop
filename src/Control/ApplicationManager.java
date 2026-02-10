package Control;
import GUI.LoginPanel;
import GUI.MainFrame;
import Model.Product;
import Model.ProductTerm;
import Model.Retrieving;

import java.sql.*;
import java.util.*;

public class ApplicationManager implements Subscriber {
    private MainFrame mainFrame;
    private Retrieving retrieving;
    private LoginPanel loginPanel;
    private DatabaseRelay databaseRelay;
    private int customerId;

    public enum Action {
        select("select "), insert("insert into "), update("update ");
        private final String actionName;

        Action(String actionName) {
            this.actionName = actionName;
        }

        @Override
        public String toString() {
            return actionName;
        }
    }

    public ApplicationManager() {
        mainFrame = new MainFrame(this);
        databaseRelay = new DatabaseRelay(this);
        retrieving = new Retrieving(this, databaseRelay);

    }

    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("in APPMANAGER UPDATE event.Action is: " + event.getAction() + " Phase is: " + event.getPhase() + " outcome is: " + event.getOutcome() + " origin is: " + event.getOrigin());
        if (event.getContents() != null) {
            System.out.println("contents instance of: " + event.getContents().getClass());
        }
        if (event.getExtraContents() != null) {
            System.out.println("extra contents instance of: " + event.getExtraContents().getClass());
            if (event.getExtraContents() instanceof ProductTerm pt){
                System.out.println("productTerm in AppManager.update is: " + pt);
            }
        }
        Event.Origin origin = event.getOrigin();
        switch (origin) {
            case GUI -> {
                if (event.getAction() == Event.Action.CREATE_ACCOUNT) {
                    createNewAccount(event);
                }
                if (event.getAction() == Event.Action.VIEW) {
                    viewActions(event);
                }
            }
            case LOGIC -> {
                if (event.getAction() == Event.Action.VALIDATE && event.getOutcome() == Event.Outcome.NOT_FOUND){
                    promptCreateNewAccount();
                }
                else if (event.getAction() == Event.Action.VALIDATE && event.getOutcome() == Event.Outcome.INVALID_INPUT) {
                    promptWrongPassword();
                }
                else if (event.getAction() == Event.Action.VALIDATE && event.getOutcome() == Event.Outcome.OK){
                    saveCustomer((Integer) event.getExtraContents());
                    mainFrame.Update(event);
                }
                else if (event.getAction() == Event.Action.CREATE_ACCOUNT){
                    if (event.getOutcome() == Event.Outcome.OK){
                    saveCustomer((Integer) event.getContents());}
                    mainFrame.Update(event);
                }
            }
        }
    }
    private void saveCustomer(int foundId){
        System.out.println("saveCustomer in appManager is reached");
        if (foundId != -1){
            this.customerId = foundId;
            System.out.println("customerId is: " + customerId);
        }
    }

    private void viewActions(Event event) throws SQLException, ClassNotFoundException {
        List<String> columns = new ArrayList<>();
        List<Object> databaseOutput = new ArrayList<>();
        switch (event.getSubject()) {
            case CART -> {
                columns.add("firstname");
                columns.add("surname");
                columns.add("name");
                columns.add("brand");
                columns.add("size");
                columns.add("quantity");
                System.out.println("in AppManager viewActions, case CART is reached");
                databaseOutput = databaseRelay.runSelect(1, "order_Inventory", columns);
                handleOutput(event, databaseOutput, columns.size());
            }
            case SHOE -> {
                System.out.println("CASE SHOE in view actions is reached");
                columns.add("productId");
                columns.add("productName");
                columns.add("brand");
                columns.add("color");
                columns.add("price");
                columns.add("size");
                columns.add("quantity");
                System.out.println("in AppManager viewActions, case SHOE is reached");
//                    if (event.getExtraContents() == null) {
                databaseOutput = databaseRelay.runSelect(1, "shoe_view", columns);
                handleOutput(event, databaseOutput, columns.size());
//                    } else {
//                        columns.set(0, "id");
//                        columns.set(1, "name");
//                        System.out.println("CASE SHOE in view actions: extracontents != null");
//                        databaseOutput = databaseRelay.runSelect(1, "product", columns);
//                        handleOutput(event, databaseOutput, columns.size());
//                    }
            }
        }
    }

    private void handleOutput(Event event, List<Object> data, int numberOfColumns) throws SQLException, ClassNotFoundException {
        List<String> lines = new ArrayList<>();
        for (Object o : data) {
            String line = (String) o;
            lines.add(line);
        }
        switch (event.getSubject()) {
            case CART -> {
                {

                }
            }
            case SHOE -> {
                List<List<String>> shoes = new ArrayList<>();
                for (int i = 0; i < lines.size(); i += numberOfColumns) {
                    shoes.add(lines.subList(i, i + numberOfColumns));
                }
                getAllShoes(shoes, event);
            }
        }
    }

    private void showSubset(List<List<String>> shoes) {

    }

    public void assessQuit(int choice) throws SQLException, ClassNotFoundException {
        if (choice == 0) {
            Update(new Event(null, null, null, null, null, null, null));
        }
    }

    public void validateCustomer(String emailInput, String password) throws SQLException, ClassNotFoundException {
        System.out.println("VALIDATE USER in appManager is reached, email is: " + emailInput +  " password is: " + password);
        List<String> userInput = new ArrayList<>();
        userInput.add(emailInput);
        userInput.add(password);
        databaseRelay.Update(Event.submit(Event.Action.VALIDATE, Event.Subject.CUSTOMER, userInput, null));
        }
        private void receiveValidationReceipt(Event event){

        }

    public void assessCreateAccount(int choice) throws SQLException, ClassNotFoundException {
        if (choice == 0) {
            mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.CREATE_ACCOUNT, Event.Subject.NONE, Event.Origin.LOGIC, null, null, null));
        }
    }

    private void showMenu() throws SQLException, ClassNotFoundException {
        mainFrame.Update(new Event(Event.Phase.COMPLETE, null, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.OK, null, null));
    }

    private void promptWrongPassword() throws SQLException, ClassNotFoundException {
        mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.VALIDATE, Event.Subject.NONE, Event.Origin.LOGIC, Event.Outcome.FAILURE, null, null));
    }

    private void getAllShoes(List<List<String>> shoes, Event event) throws SQLException, ClassNotFoundException {
        List<Product> allShoes = new ArrayList<>();
        for (List<String> shoe : shoes) {
            System.out.println("START SHOE MAKING. String 0 in showShoes: " + shoe.getFirst());
            String id = shoe.getFirst();
            int idNumber = 0;
            int quantityNumber = 0;
            String name = shoe.get(1);
            System.out.println("String 1 in showShoes: " + shoe.get(1));
            String brand = shoe.get(2);
            System.out.println("String 2 in showShoes: " + shoe.get(2));
            String color = shoe.get(4);
            System.out.println("String 3 in showShoes: " + shoe.get(3));
            String price = shoe.get(3);
            System.out.println("String 4 in showShoes: " + shoe.get(4));
            String quantity = shoe.getFirst();
            System.out.println("String 5 in showShoes:  " + shoe.get(5));
            try {
                idNumber = Integer.parseInt(id.trim());
                System.out.println("in tryParse, idNumber is: " + idNumber);
                quantityNumber = Integer.parseInt(quantity.trim());
                System.out.println("in tryParse, quantityNumber is: " + quantityNumber);
            } catch (NumberFormatException e) {
                System.out.println("Parsing failed in showShoes");
            }
            Product newShoe = new Product(idNumber, name, brand, color, price);
            newShoe.setQuantity(quantityNumber);
            newShoe.setSize(shoe.get(5));
            System.out.println("String 6 in showShoes: " + shoe.get(6));
            allShoes.add(newShoe);
        }
//        if (event.getContents() == null) {
        System.out.println("in GETALLSHOES, extraContents is null");
//            List <Product> tempList = new ArrayList<>();
//            for (Product p : allShoes){
//                if (tempList.isEmpty()){
//                    tempList.add(p);
//                }
//                else {
//                    boolean isSame = false;
//                    for (Product pp : tempList){
//                        if (p.getBrand().equals(pp.getBrand())){
//                            isSame = true;
//                        }
//                        if (!isSame){
//                            tempList.add(p);
//                        }
//                    }
//                }
//            }
//            allShoes = new ArrayList<>();
//            allShoes.addAll(tempList);
//            mainFrame.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, Event.Outcome.OK, allShoes, null));
//        } else {
//            System.out.println("in GETALLSHOES, contents instance of :" + event.getContents().getClass());
        getProductSubset(allShoes, event);
    }

    private void getProductSubset(List<Product> allShoes, Event event) throws SQLException, ClassNotFoundException {
        System.out.println("GET PRODUCT SUBSET IN APPmANAGER IS REACHED");
        String term = "";
        ProductTerm pt = null;
        Set<Product> subset = new LinkedHashSet<>();
        List<Product> shoesToSend = new ArrayList<>();
        List<String> terms = new ArrayList<>();
        if (event.getContents() instanceof ProductTerm) {
            System.out.println("contents instance of product term");
            System.out.println();
            pt = (ProductTerm) event.getContents();

            if (event.getExtraContents() != null && event.getExtraContents() instanceof List) {
                System.out.println("..extra contents instance of: " + event.getExtraContents().getClass());
                terms.addAll((List<String>) event.getExtraContents());
            }
        } else if (event.getContents() instanceof List){
            terms.addAll((List<String>) event.getContents());
            if (event.getExtraContents() != null) {
                // if (event.getExtraContents() != null && event.getExtraContents() instanceof ProductTerm) {
                System.out.println("extra contents instance of: " + event.getExtraContents().getClass());
                pt = (ProductTerm) event.getExtraContents();
                terms.addAll((List<String>) event.getContents());
            }
        }
        System.out.println("IN G P S, pt IS: " + pt + "   term is: " + term);
        if (pt == null){
            pt = ProductTerm.Brand;
        }
        switch (pt) {
            case Name -> {
                String brand = terms.getFirst().trim();
                List<Product> correctBrand = new ArrayList<>();
                for (Product p : allShoes){
                    System.out.println("ALLSHOES NAME: " + p.getName().trim());
                    if (p.getBrand().trim().equals(brand)){
                        correctBrand.add(p);
                    }
                }
                List<String> foundModels = new ArrayList<>();
                for (Product product : correctBrand) {
                    System.out.println("NAME: " + product.getName().trim());
                    System.out.println("IN G P S, NAME, product is: " + product.getName() + " brand is: " + brand + "correctBrand.size is: " + correctBrand.size());
                    {
                        if (foundModels.isEmpty()){
                            foundModels.add(product.getName().trim());
                            subset.add(product);
                        }
                        else {
                            boolean alreadyAdded = false;
                            for (String name : foundModels){
                                if (product.getName().trim().equals(name)){
                                    alreadyAdded = true;
                                }
                            }
                            if (!alreadyAdded){
                                subset.add(product);
                                foundModels.add(product.getName().trim());
                            }
                        }
                    }
                }
            }

            case Brand -> {
                System.out.println("IN G P S, CASE BRAND IS REACHED");
                List<String> foundBrands = new ArrayList<>();

                for (Product product : allShoes){
                    if (foundBrands.isEmpty()){
                        foundBrands.add(product.getBrand());
                        subset.add(product);
                    }
                    else {
                        boolean alreadyAdded = false;
                        for (String brand : foundBrands){
                            if (product.getBrand().equals(brand)){
                                alreadyAdded = true;
                            }
                        }
                        if (!alreadyAdded){
                            subset.add(product);
                            foundBrands.add(product.getBrand());
                        }
                    }
                }
            }
            case Size -> {
                String brand = terms.getFirst().trim();
                String name = terms.get(1).trim();
                String color = terms.get(2).trim();
                List<Product> correctProduct = new ArrayList<>();
                for (Product product : allShoes) {
                    if (product.getBrand().trim().equals(brand) && product.getName().trim().equals(name) && product.getColor().trim().equals(color)) {
                        correctProduct.add(product);
                    }
                }
                System.out.println("IN G P S SIZE, correctProduct.size is: " + correctProduct.size());
                    List<String> foundSizes = new ArrayList<>();
                    for (Product product : correctProduct) {
                        System.out.println("IN G P S, SIZE  , product is: " + product.getName() + " size is: " + product.getSize() + " brand is: " + brand + " name is: " + name);
                        if (foundSizes.isEmpty()){
                            foundSizes.add(product.getSize().trim());
                            subset.add(product);
                        }
                        else {
                            boolean alreadyAdded = false;
                            for (String size : foundSizes){
                                if (product.getSize().trim().equals(size)){
                                    alreadyAdded = true;
                                }
                            }
                            if (!alreadyAdded){
                                subset.add(product);
                                foundSizes.add(product.getSize().trim());
                            }
                        }
                    }
                }
            case Color -> {
                System.out.println("IN G P S, CASE COLOR IS REACHED");
                String brand = terms.getFirst().trim();
                String name = terms.get(1).trim();
                List<Product> correctProduct = new ArrayList<>();
                for (Product product : allShoes) {
                    if (product.getBrand().trim().equals(brand) && product.getName().trim().equals(name)) {
                        correctProduct.add(product);
                    }
                }
                List<String> foundColors = new ArrayList<>();
                for (Product product : correctProduct) {
                    System.out.println("IN G P S, Color, product is: " + product.getName() + " size is: " + product.getSize() + " brand is: " + brand + " name is: " + name);
                    if (foundColors.isEmpty()){
                        foundColors.add(product.getColor().trim());
                        subset.add(product);
                    }
                    else {
                        boolean alreadyAdded = false;
                        for (String color : foundColors){
                            if (product.getColor().trim().equals(color)){
                                alreadyAdded = true;
                            }
                        }
                        if (!alreadyAdded){
                            subset.add(product);
                            foundColors.add(product.getColor().trim());
                        }
                    }
                }
            }
        }
        for (Product p : subset){
            shoesToSend.add(p);
        }
        System.out.println(" IN G P S shoesToSend.SIZE IS: " + shoesToSend.size());
        mainFrame.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, Event.Outcome.OK, shoesToSend, event.getExtraContents()));
    }
    private void createNewAccount(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("createNewAccount in AppManager is reached");
        List<String> userInput = new ArrayList<>();
        if (event.getContents() instanceof List list && list.getFirst() instanceof String ) {
            userInput = (List<String>) event.getContents();
            if (!userInput.isEmpty()) {
                databaseRelay.Update(new Event(Event.Phase.SUBMIT, Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.PENDING, userInput, null));
            }
        }
    }

    private void promptCreateNewAccount() throws SQLException, ClassNotFoundException {
        mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.NOT_FOUND, null, null));
    }
}

