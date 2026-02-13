package Control;
import GUI.LoginPanel;
import GUI.MainFrame;
import Model.ProductTerm;
import java.sql.*;
import java.util.*;

public class ApplicationManager implements Subscriber {
    private MainFrame mainFrame;
    private LoginPanel loginPanel;
    private DatabaseRelay databaseRelay;
    private int customerId;
    private String adminId = "admin";

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
        System.out.println("appManager constructor is reached");
        this.mainFrame = new MainFrame(this);
        try {
            this.databaseRelay = new DatabaseRelay(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("in APPMANAGER UPDATE event.Action is: " + event.getAction() + " Phase is: " + event.getPhase() + " subject is: " + event.getSubject() + " outcome is: " + event.getOutcome() + " origin is: " + event.getOrigin());
        if (event.getContents() != null) {
            System.out.println("contents instance of: " + event.getContents().getClass());
        }
        if (event.getExtraContents() != null) {
            System.out.println("extra contents instance of: " + event.getExtraContents().getClass());
            if (event.getExtraContents() instanceof ProductTerm pt) {
                System.out.println("productTerm in AppManager.update is: " + pt);
            }
        }
        Event.Origin origin = event.getOrigin();

        switch (origin) {
            case GUI -> {
                switch (event.getAction()) {
                    case CREATE_ACCOUNT -> createNewAccount(event);
                    case VIEW, CHOOSE_TYPE, PURCHASE -> {
                        if (event.getAction() == Event.Action.VIEW && event.getSubject() == Event.Subject.CART){
                            event.setContents(customerId);
                        }
                        databaseRelay.Update(event);
                    }
                }
            }

            case LOGIC -> {
                switch (event.getAction()) {
                    case VALIDATE -> {
                        if (event.getPhase() == Event.Phase.AWAIT_INPUT && event.getSubject() == Event.Subject.NONE || (event.getSubject == Event.Subject.ADMIN && event.getOutcome == Event.Outcome.OK){
                            mainFrame.Update(event);
                            break;
                        }
                        switch (event.getOutcome()) {
                            case NOT_FOUND -> {
                                if (event.getSubject == Event.Subject.ADMIN) {

                                } else {
                                    promptCreateNewAccount();
                                }
                            }
                            case INVALID_INPUT -> promptWrongPassword();
                            case OK -> {
                                saveCustomer((Integer) event.getExtraContents());
                                mainFrame.Update(event);
                            }
                        }
                    }
                    case CREATE_ACCOUNT -> {
                        if (event.getOutcome() == Event.Outcome.OK) {
                            saveCustomer((Integer) event.getContents());
                        }
                        mainFrame.Update(event);
                    }
                    case VIEW -> {
                       // if (event.getPhase() == Event.Phase.DISPLAY && event.getSubject() == Event.Subject.SHOE) {
                            mainFrame.Update(event);
//                        } else if (event.getPhase() == Event.Phase.COMPLETE && event.getSubject() == Event.Subject.CART) {
//                            mainFrame.Update(event);
//                        }
                    }
                    case PURCHASE -> {
                        mainFrame.Update(event);
                    }
                }
            }
        }
    }

    private void saveCustomer(int foundId) {
        System.out.println("saveCustomer in appManager is reached");
        if (foundId != -1) {
            this.customerId = foundId;
            System.out.println("customerId is: " + customerId);
        }
    }

    private void createNewAccount(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("createNewAccount in AppManager is reached");
        List<String> userInput = new ArrayList<>();
        if (event.getContents() instanceof List list && list.getFirst() instanceof String) {
            userInput = (List<String>) event.getContents();
            if (!userInput.isEmpty()) {
                databaseRelay.Update(new Event(Event.Phase.SUBMIT, Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.PENDING, userInput, null));
            }
        }
    }
    private void promptInvalidAdminLogin(){
        loginPanel.promptInvalidAdminLogin();
    }

    private void promptCreateNewAccount() throws SQLException, ClassNotFoundException {
        mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.NOT_FOUND, null, null));
    }

    public int getCustomerId() {
        return customerId;
    }


    public void assessQuit(int choice) throws SQLException, ClassNotFoundException {
        if (choice == 0) {
            System.exit(0);
        }
    }

    public void validateCustomer(String emailInput, String password) throws SQLException, ClassNotFoundException {
        System.out.println("VALIDATE USER in appManager is reached, email is: " + emailInput + " password is: " + password);
        Event.Subject subject = Event.Subject.CUSTOMER;
        if (emailInput.equals(adminId){
            subject = Event.Subject.ADMIN;
        }
        List<String> userInput = new ArrayList<>();
        userInput.add(emailInput);
        userInput.add(password);
        databaseRelay.Update(Event.submit(Event.Action.VALIDATE, subject, userInput, null));
    }

    public void assessCreateAccount(int choice) throws SQLException, ClassNotFoundException {
        if (choice == 0) {
            mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.CREATE_ACCOUNT, Event.Subject.NONE, Event.Origin.LOGIC, null, null, null));
        }
    }

    private void promptWrongPassword() throws SQLException, ClassNotFoundException {
        mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.VALIDATE, Event.Subject.NONE, Event.Origin.LOGIC, Event.Outcome.FAILURE, null, null));
    }
}

//    private void tempSeeShoes(Event event){
//        if (event.getContents() instanceof ProductTerm term){
//
//        }
//    }

//    private void viewActions(Event event) throws SQLException, ClassNotFoundException {
//        List<String> columns = new ArrayList<>();
//        List<Object> databaseOutput = new ArrayList<>();
//        switch (event.getSubject()) {
//            case CART -> {
//                columns.add("firstname");
//                columns.add("surname");
//                columns.add("name");
//                columns.add("brand");
//                columns.add("size");
//                columns.add("quantity");
//                System.out.println("in AppManager viewActions, case CART is reached");
//                databaseOutput = databaseRelay.runSelect(1, "order_Inventory", columns);
//                handleOutput(event, databaseOutput, columns.size());
//            }
//            case SHOE -> {

//                System.out.println("CASE SHOE in view actions is reached");
//                columns.add("productId");
//                columns.add("productName");
//                columns.add("brand");
//                columns.add("color");
//                columns.add("price");
//                columns.add("size");
//                columns.add("quantity");
//                System.out.println("in AppManager viewActions, case SHOE is reached");
////                    if (event.getExtraContents() == null) {
//                databaseOutput = databaseRelay.runSelect(1, "shoe_view", columns);
//                handleOutput(event, databaseOutput, columns.size());
////                    } else {
////                        columns.set(0, "id");
////                        columns.set(1, "name");
////                        System.out.println("CASE SHOE in view actions: extracontents != null");
////                        databaseOutput = databaseRelay.runSelect(1, "product", columns);
////                        handleOutput(event, databaseOutput, columns.size());
////                    }
//            newShoe.setQuantity(quantityNumber);
//            newShoe.setSize(shoe.get(5));
//            System.out.println("String 6 in showShoes: " + shoe.get(6));
//            allShoes.add(newShoe);
//        }
////        if (event.getContents() == null) {
//        System.out.println("in GETALLSHOES, extraContents is null");
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
//        getProductSubset(allShoes, event);
//    }

//    private void getProductSubset(List<Product> allShoes, Event event) throws SQLException, ClassNotFoundException {
//        System.out.println("GET PRODUCT SUBSET IN APPmANAGER IS REACHED");
//        String term = "";
//        ProductTerm pt = null;
//        Set<Product> subset = new LinkedHashSet<>();
//        List<Product> shoesToSend = new ArrayList<>();
//        List<String> terms = new ArrayList<>();
//        if (event.getContents() instanceof ProductTerm) {
//            System.out.println("contents instance of product term");
//            System.out.println();
//            pt = (ProductTerm) event.getContents();
//
//            if (event.getExtraContents() != null && event.getExtraContents() instanceof List) {
//                System.out.println("..extra contents instance of: " + event.getExtraContents().getClass());
//                terms.addAll((List<String>) event.getExtraContents());
//            }
//        } else if (event.getContents() instanceof List){
//            terms.addAll((List<String>) event.getContents());
//            if (event.getExtraContents() != null) {
//                // if (event.getExtraContents() != null && event.getExtraContents() instanceof ProductTerm) {
//                System.out.println("extra contents instance of: " + event.getExtraContents().getClass());
//                pt = (ProductTerm) event.getExtraContents();
//                terms.addAll((List<String>) event.getContents());
//            }
//        }
//        System.out.println("IN G P S, pt IS: " + pt + "   term is: " + term);
//        if (pt == null){
//            pt = ProductTerm.Brand;
//        }
//        switch (pt) {
//            case Name -> {
//                String brand = terms.getFirst().trim();
//                List<Product> correctBrand = new ArrayList<>();
//                for (Product p : allShoes){
//                    System.out.println("ALLSHOES NAME: " + p.getName().trim());
//                    if (p.getBrand().trim().equals(brand)){
//                        correctBrand.add(p);
//                    }
//                }
//                List<String> foundModels = new ArrayList<>();
//                for (Product product : correctBrand) {
//                    System.out.println("NAME: " + product.getName().trim());
//                    System.out.println("IN G P S, NAME, product is: " + product.getName() + " brand is: " + brand + "correctBrand.size is: " + correctBrand.size());
//                    {
//                        if (foundModels.isEmpty()){
//                            foundModels.add(product.getName().trim());
//                            subset.add(product);
//                        }
//                        else {
//                            boolean alreadyAdded = false;
//                            for (String name : foundModels){
//                                if (product.getName().trim().equals(name)){
//                                    alreadyAdded = true;
//                                }
//                            }
//                            if (!alreadyAdded){
//                                subset.add(product);
//                                foundModels.add(product.getName().trim());
//                            }
//                        }
//                    }
//                }
//            }
//
//            case Brand -> {
//                System.out.println("IN G P S, CASE BRAND IS REACHED");
//                List<String> foundBrands = new ArrayList<>();
//
//                for (Product product : allShoes){
//                    if (foundBrands.isEmpty()){
//                        foundBrands.add(product.getBrand());
//                        subset.add(product);
//                    }
//                    else {
//                        boolean alreadyAdded = false;
//                        for (String brand : foundBrands){
//                            if (product.getBrand().equals(brand)){
//                                alreadyAdded = true;
//                            }
//                        }
//                        if (!alreadyAdded){
//                            subset.add(product);
//                            foundBrands.add(product.getBrand());
//                        }
//                    }
//                }
//            }
//            case Size -> {
//                String brand = terms.getFirst().trim();
//                String name = terms.get(1).trim();
//                String color = terms.get(2).trim();
//                List<Product> correctProduct = new ArrayList<>();
//                for (Product product : allShoes) {
//                    if (product.getBrand().trim().equals(brand) && product.getName().trim().equals(name)) {
//                        correctProduct.add(product);
//                    }
//                }
//                System.out.println("IN G P S SIZE, correctProduct.size is: " + correctProduct.size());
//                    List<String> foundSizes = new ArrayList<>();
//                    for (Product product : correctProduct) {
//                        System.out.println("IN G P S, SIZE  , product is: " + product.getName() + " size is: " + product.getSize() + " brand is: " + brand + " name is: " + name);
//                        if (foundSizes.isEmpty()){
//                            foundSizes.add(product.getSize().trim());
//                            subset.add(product);
//                        }
//                        else {
//                            boolean alreadyAdded = false;
//                            for (String size : foundSizes){
//                                if (product.getSize().trim().equals(size)){
//                                    alreadyAdded = true;
//                                }
//                            }
//                            if (!alreadyAdded){
//                                subset.add(product);
//                                foundSizes.add(product.getSize().trim());
//                            }
//                        }
//                    }
//                }
//            case Color -> {
//                System.out.println("IN G P S, CASE COLOR IS REACHED");
//                String brand = terms.getFirst().trim();
//                String name = terms.get(1).trim();
//                List<Product> correctProduct = new ArrayList<>();
//                for (Product product : allShoes) {
//                    if (product.getBrand().trim().equals(brand) && product.getName().trim().equals(name)) {
//                        correctProduct.add(product);
//                    }
//                }
//                List<String> foundColors = new ArrayList<>();
//                for (Product product : correctProduct) {
//                    System.out.println("IN G P S, Color, product is: " + product.getName() + " size is: " + product.getSize() + " brand is: " + brand + " name is: " + name);
//                    if (foundColors.isEmpty()){
//                        foundColors.add(product.getColor().trim());
//                        subset.add(product);
//                    }
//                    else {
//                        boolean alreadyAdded = false;
//                        for (String color : foundColors){
//                            if (product.getColor().trim().equals(color)){
//                                alreadyAdded = true;
//                            }
//                        }
//                        if (!alreadyAdded){
//                            subset.add(product);
//                            foundColors.add(product.getColor().trim());
//                        }
//                    }
//                }
//            }
//        }
//        for (Product p : subset){
//            shoesToSend.add(p);
//        }
//        System.out.println(" IN G P S shoesToSend.SIZE IS: " + shoesToSend.size());
//        mainFrame.Update(new Event(Event.Phase.DISPLAY, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.LOGIC, Event.Outcome.OK, shoesToSend, event.getExtraContents()));
//    }
