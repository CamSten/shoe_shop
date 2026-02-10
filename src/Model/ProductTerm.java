package Model;

public enum ProductTerm {
    Name("'name'"), Brand("'brand'"), Color("'color'"), Size("'size'"), None ("'None'");
    private final String productTerm;
    ProductTerm(String productTerm){
        this.productTerm = productTerm;
    }
    @Override
    public String toString() {
        return productTerm;
    }
}
