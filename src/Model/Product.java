package Model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Product {
    private int productId;
    private String name;
    private String brand;
    private String description;
    private List<String> colors;
    private List<Integer> sizes;
    private List<Integer> invQuantities;
    private String color;
    private int size;
    private int invQuantity;
    private int buyQuantity;
    private List<ShoeSpecification> shoeSpecifications;
    private int price;


    public Product(int productId, String name, String brand, String description, int price) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.colors = new ArrayList<>();
        this.sizes = new ArrayList<>();
        this.shoeSpecifications = new ArrayList<>();
    }
    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }


    public void setInvQuantities(List<Integer> invQuantities) {
        this.invQuantities = invQuantities;
    }

    public List<ShoeSpecification> getShoeSpecifications() {
        return shoeSpecifications;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getInvQuantity() {
        return invQuantity;
    }

    public void setInvQuantity(int quantity) {
        this.invQuantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public List<String> getColors() {
//        Set<String> everyColor = new LinkedHashSet<>();
//        if (!shoeSpecifications.isEmpty()){
//            for (ShoeSpecification sc : shoeSpecifications){
//                everyColor.add(sc.getColor());
//            }
//        }
//        System.out.println("In product.getColors, size is: " + colors.size());
//        colors.clear();
//        colors.addAll(everyColor);
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<Integer> getSizes() {
//        Set<Integer> everySize = new LinkedHashSet<>();
//        if (!shoeSpecifications.isEmpty()){
//            for (int i = 0; i < shoeSpecifications.size(); i++){
//                everySize.add(shoeSpecifications.get(i).getSize());
//            }
//        }
//        sizes.clear();
//        sizes.addAll(everySize);
        return sizes;
    }

    public void setSizes(List<Integer> sizes) {
        this.sizes = sizes;
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

//    public List<Integer> getInvQuantities() {
//        Set<Integer> everyQuantity = new LinkedHashSet<>();
//        if (!shoeSpecifications.isEmpty()){
//            for (int i = 0; i < shoeSpecifications.size(); i++){
//                everyQuantity.add(shoeSpecifications.get(i).getQuantity());
//            }
//        }
//        invQuantities.clear();
//        invQuantities.addAll(everyQuantity);
//
//        return invQuantities;
//    }

}
