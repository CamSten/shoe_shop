package Model.DataHandling;

public class SalesPost {
    private String brand;
    private String name;
    private int soldQuantity;

    public SalesPost(String brand, String name, int soldQuantity) {
        this.brand = brand;
        this.name = name;
        this.soldQuantity = soldQuantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
}