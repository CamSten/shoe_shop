package Model;
import java.sql.*;

import Control.ApplicationManager;
import Control.Event;
import Control.Subscriber;
import Model.Repositories.AdminRepo;
import Model.Repositories.CustomerRepo;
import Model.Repositories.OrderRepo;
import Model.Repositories.ProductRepo;

public class DatabaseRelay implements Subscriber {
    private static Connection c;
    private static ApplicationManager applicationManager;
    private static AdminRepo adminRepo;
    private static CustomerRepo customerRepo;
    private static ProductRepo productRepo;
    private static OrderRepo orderRepo;
    private int customerId;

    public DatabaseRelay(ApplicationManager applicationManager) throws SQLException {
        this.applicationManager = applicationManager;
        System.out.println("connection:  url:" + PropertyRetriever.getUrl() +" user: " + PropertyRetriever.getUser() + " pass: "+ PropertyRetriever.getPassword());
        this.c = DriverManager.getConnection(PropertyRetriever.getUrl(), PropertyRetriever.getUser(), PropertyRetriever.getPassword());
        this.adminRepo = new AdminRepo(this, c);
        this.customerRepo = new CustomerRepo(this, c);
        this.orderRepo = new OrderRepo(this, c);
        this.productRepo = new ProductRepo(this, c, orderRepo);
    }

    public void Relay(Event event) throws SQLException, ClassNotFoundException {

        applicationManager.Update(event);
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN DBR IS REACHED");

        switch(event.getSubject()){
            case ADMIN -> {
                adminRepo.Update(event);
            }
            case CUSTOMER -> {
                customerRepo.Update(event);
            }
            case SHOE -> {
                productRepo.Update(event);
            }
            case CART -> {
                orderRepo.Update(event);
            }
        }
    }

    public int getCustomerId(){
        return customerId;
    }
    private void setCustomerId(int id){
        this.customerId = id;
    }
}
