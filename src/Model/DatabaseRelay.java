package Model;
import java.lang.classfile.CustomAttribute;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import Control.ApplicationManager;
import Control.Event;
import Control.Subscriber;
import Model.OrderPost;
import Model.Product;
import Model.Customer;
import Model.ProductTerm;
import Model.ShoeSpecification;
import com.mysql.cj.jdbc.admin.MiniAdmin;

public class DatabaseRelay implements Subscriber {
    private static Connection c;
    private static ApplicationManager applicationManager;
    private static AdminRepo adminRepo;
    private static CustomerRepo customerRepo;
    private static ProductRepo productRepo;
    private static OrderRepo orderRepo;

    public DatabaseRelay(ApplicationManager applicationManager) throws SQLException {
        this.applicationManager = applicationManager;
        this.c = DriverManager.getConnection(PropertyRetriever.getUrl(), PropertyRetriever.getUser(), PropertyRetriever.getPassword());
        this.adminRepo = new AdminRepo();
        this.customerRepo = new CustomerRepo();
        this.productRepo = new ProductRepo();
        this.orderRepo = new OrderRepo();

    }

    public void Relay(Event event){
        applicationManager.Update(event);
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN DBR IS REACHED");

        switch(event.getSubject){
            case ADMIN -> {

            }
            case CUSTOMER -> {

            }
            case PRODUCT -> {

            }

            case CART -> {

            }
        }
    }
}

//
//        switch (event.getSubject()) {
//            case ADMIN -> {
//                assessIfAdmin(event);
//            }
//            case CUSTOMER -> {
//                if (event.getAction() == Event.Action.VALIDATE && event.getContents() instanceof List list) {
//                    checkCustomer((List<String>) list);
//                } else if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getContents() instanceof List list) {
//                    addNewCustomer((List<String>) list);
//                }
//                else if (event.getAction() == Event.Action.VIEW && event.getContents() instanceof Integer){
//                    getCustomerInfo((Integer) event.getContents());
//                }
//                else if (event.getAction() == Event.Action.EDIT && event.getContents() instanceof Customer customer){
//                    editCustomerInfo(customer);
//                }
//            }
//            case SHOE -> {
//                if (event.getAction() == Event.Action.PURCHASE) {
//                    purchaseActions(event);
//                } else {
//                    String choice = "";
//                    if (event.getExtraContents() instanceof String extra) choice = extra;
//                    getShoesFromDB(event, choice);
//                }
//            }
//            case CART -> {
//                if (event.getContents() instanceof Integer){
//                    int customerId = (Integer) event.getContents();
//                    executeOrderQuery(customerId);
//                }
//            }
