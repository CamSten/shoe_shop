package Model.DataHandling;

public class InventoryPost {
    private String productBrand;
    private String productName;
    private String productColor;
    private int productSize;
    private int price;
    private int stockQuantity;

    public InventoryPost( String productBrand, String productName, String productColor, int productSize, int price, int stockQuantity) {
        this.productBrand = productBrand;
        this.productName = productName;
        this.productColor = productColor;
        this.productSize = productSize;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getProductSize() {
        return productSize;
    }

    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }
}