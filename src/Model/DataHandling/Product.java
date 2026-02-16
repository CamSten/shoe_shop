package Model.DataHandling;

import Model.DataHandling.ShoeSpecification;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productId;
    private String name;
    private String brand;
    private String description;
    private int price;
    private int buyQuantity;
    private List<ShoeSpecification> shoeSpecifications;
    private ShoeSpecification boughtSpecification;



    public Product(int productId, String name, String brand, String description, int price) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.shoeSpecifications = new ArrayList<>();
    }

    public ShoeSpecification getBoughtSpecification() {
        return boughtSpecification;
    }

    public void setBoughtSpecification(ShoeSpecification boughtSpecification) {
        this.boughtSpecification = boughtSpecification;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }


    public List<ShoeSpecification> getShoeSpecifications() {
        return shoeSpecifications;
    }


    public List<ShoeSpecification> getSizeColors() {
        return shoeSpecifications;
    }

    public void addSpecification(ShoeSpecification specification){
        shoeSpecifications.add(specification);
    }
    public void setSpecifications(List<ShoeSpecification> shoeSpecifications) {
        this.shoeSpecifications = shoeSpecifications;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
