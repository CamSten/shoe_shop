package Model.DataHandling;

public class ShoeSpecification {
    private int size;
    private String color;
    private int invQuantity;
    private int buyQuantity;

    public ShoeSpecification(int size, String color, int invQuantity) {
        this.size = size;
        this.color = color;
        this.invQuantity = invQuantity;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public int getInvQuantity() {
        return invQuantity;
    }

    public void setInvQuantity(int invQuantity) {
        this.invQuantity = invQuantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
