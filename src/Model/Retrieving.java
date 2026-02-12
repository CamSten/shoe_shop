//package Model;
//
//import Control.ApplicationManager;
//import Control.DatabaseRelay;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Retrieving {
//    private static ApplicationManager applicationManager;
//    private static DatabaseRelay databaseRelay;
//    private void showAvailableShoes() throws SQLException, ClassNotFoundException {
////        List<Product> availableShoes = getAvailableShoes();
//    }
//    public Retrieving(ApplicationManager applicationManager, DatabaseRelay databaseRelay){
//        this.applicationManager = applicationManager;
//        this.databaseRelay = databaseRelay;
//    }
//
//    private static List<Product> getAvailableShoes() throws SQLException, ClassNotFoundException {
//        List<String> shoes = new ArrayList<>();
//        List<String> columns = new ArrayList<>();
//        List<String> productIds = new ArrayList<>();
//        List<Product> products = new ArrayList<>();
//        String tableName = "product";
//        columns.add("id");
//        columns.add("name");
//        columns.add("brand");
//        columns.add("description");
//        columns.add("color");
//
//        List<Object> databaseOutput = databaseRelay.runSelect(1, tableName, columns);
//        if (!databaseOutput.isEmpty() && databaseOutput.getFirst() instanceof String output){
//            for (Object o : databaseOutput){
//                shoes.add((String) o);
//            }
//        }
//        for (int i = 0; i < shoes.size(); i+=5){
//            String id = shoes.get(i);
//            int productId = 0;
//            try {
//                productId = Integer.parseInt(id.trim());
//            }
//            catch (NumberFormatException e){
//            }
//            String name = shoes.get(i+1);
//            String brand = shoes.get(i+2);
//            String description = shoes.get(i+3);
//            String color = shoes.get(i+4);
//            Product newShoe = new Product(productId, name, brand, description, color);
//            products.add(newShoe);
//            productIds.add(shoes.get(i+3));
//        }
//        for (Product product : products) {
//            System.out.println("product in getAvailableShoes: " + product.getProductId() + ", " + product.getName() + ", " + product.getColor());
//            getAvailableSizes(product);
//        }
//        return products;
//    }
//    private static List<String> getAvailableSizes(Product product) throws SQLException, ClassNotFoundException {
//        List<String> sizes = new ArrayList<>();
//        String tableName = "shoeInventory";
//        List<String> columns = new ArrayList<>();
//        columns.add("productId");
//        columns.add("size");
//        columns.add("quantity");
//        List<String[]> shoesInInventory = new ArrayList<>();
//        List<String[]> relevantShoes = new ArrayList<>();
//        List<Object> databaseOutput = databaseRelay.runSelect(1, tableName, columns);
//        if (!databaseOutput.isEmpty() && databaseOutput.getFirst() instanceof String output){
//            for (Object o : databaseOutput){
//                sizes.add((String) o);
//            }
//        }
//        for (int i = 0; i < sizes.size(); i+=3){
//            String[] shoe = new String[3];
//            System.out.println(sizes.get(i) + " " + sizes.get(i+1) + " " + sizes.get(i+2));
//            shoe[0] = sizes.get(i);
//            shoe[1] = sizes.get(i+1);
//            shoe[2] = sizes.get(i+2);
//            shoesInInventory.add(shoe);
//        }
//        for (int i = 0; i < shoesInInventory.size(); i++){
//            String id = shoesInInventory.get(i)[0];
//            try {
//                int productId = Integer.parseInt(id.trim());
//                if (productId == product.getProductId()){
//                    relevantShoes.add(shoesInInventory.get(i));
//                }
//            }
//            catch (NumberFormatException e){
//                System.out.println("parse failed");
//            }
//
//        }
//        if (!relevantShoes.isEmpty()){
//            System.out.println("RELEVANT SHOES IS NOT EMPTY");
//            System.out.println(product.getBrand() + " " + product.getName() + " " + product.getColor() + "is available in sizes:\n");
//            for (int i = 0; i < relevantShoes.size(); i++) {
//                String quantity = relevantShoes.get(i)[2];
//                int quantityInt = Integer.parseInt(quantity.trim());
//                if (quantityInt > 0) {
//                    System.out.println(relevantShoes.get(i)[1] + ",   " + quantity + " in stock.");
//                }
//            }
//        }
//        return sizes;
//    }
//}
