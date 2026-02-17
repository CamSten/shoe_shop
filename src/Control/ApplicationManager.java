package Control;
import GUI.AdminGUI.AdminInfoPanel;
import GUI.AdminGUI.AdminMenuPanel;
import GUI.LoginPanel;
import GUI.MainFrame;
import Model.DataHandling.ProductTerm;
import Model.DatabaseRelay;

import java.sql.*;
import java.util.*;

public class ApplicationManager implements Subscriber {
    private MainFrame mainFrame;
    private LoginPanel loginPanel;
    private DatabaseRelay databaseRelay;
    private int customerId;
    private String adminId = "admin";
    private boolean admin;

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
        admin = false;
        Event.Origin origin = event.getOrigin();
        System.out.println("in APPMANAGER UPDATE event.Action is: " + event.getAction() + " Phase is: " + event.getPhase() + " subject is: " + event.getSubject() + " outcome is: " + event.getOutcome() + " origin is: " + event.getOrigin());
        if (event.getContents() != null) {
            System.out.println("contents instance of: " + event.getContents().getClass());
        }
        if (event.getExtraContents() != null) {
            System.out.println("extra contents instance of: " + event.getExtraContents().getClass());
            if (event.getExtraContents() instanceof ProductTerm pt) {
                System.out.println("productTerm in AppManager.update is: " + pt);
            }
            if (event.getExtraContents() instanceof Event.Subject subject && subject == Event.Subject.ADMIN) {
                admin = true;
            }
        }
        switch (origin) {
            case GUI -> {
                switch (event.getAction()) {
                    case LOG_OUT -> {
                        logOutActions();
                    }
                    case CREATE_ACCOUNT -> {
                        createNewAccount(event);
                    }
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
                        if (event.getPhase() == Event.Phase.AWAIT_INPUT && event.getSubject() == Event.Subject.NONE || (event.getSubject() == Event.Subject.ADMIN && event.getOutcome() == Event.Outcome.OK)){
                            mainFrame.Update(event);
                            break;
                        }
                        switch (event.getOutcome()) {
                            case NOT_FOUND -> {
                                if (event.getSubject() == Event.Subject.ADMIN) {

                                } else {
                                    promptCreateNewAccount();
                                }
                            }
                            case INVALID_INPUT -> promptWrongPassword();
                            case OK -> {
                                System.out.println("in AppManager: case VALIDATE, OK, is reached");
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
                    case VIEW, PURCHASE -> {
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
            databaseRelay.setCustomerId(customerId);
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
        if (emailInput.equals(adminId)){
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

    private void logOutActions(){
        this.customerId = -1;
        admin = false;
        databaseRelay.setCustomerId(customerId);
    }
}