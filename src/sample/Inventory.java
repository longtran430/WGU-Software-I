package sample;

import java.util.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Contains inventory information and methods used to add, modify, search and remove items in inventory
 *
 * @author Long Tran
 */
public class Inventory {

    /**
     * All parts in inventory
     */
    private static ObservableList<Part> allParts = FXCollections.observableArrayList(
            new InHouse(1, "Brakes", 15, 10, 1, 10, 101),
            new Outsourced(2, "Seat", 10, 10, 4, 40, "Car Parts"));

    /**
     * All products in inventory
     */
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList(
            new Product(1000, "Giant Bike", 300, 50, 10, 100),
            new Product(1001, "Tricycle", 100, 30, 10, 100)
    );

    /**
     * @param newPart a new part to be added to inventory
     * */
    public static void addPart(Part newPart) {
        allParts.addAll(newPart);
    }
    /**
     * @param newProduct a new product to be added to inventory
     * */
    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    /**
     * @param partId the id of the part to be searched
     * @return the part to be found
     * */
    public static Part lookupPart(int partId) {
        for(Part part : allParts) {
            if(partId == part.getId()) {
                return part;
            }
        }
        return null;
    }

    /**
     *
     * @param partId the id of the part to be searched
     * @param newValue the search text
     * @return if the part matches the search text
     */
    public static boolean lookupPart(String partId, String newValue){
        if(partId.contains(newValue)){
            return true;
        }
        return false;
    }

    /**
     *
     * @param productId the id of the product to be searched
     * @return the product to be found
     */
    public static Product lookupProduct(int productId) {
        for(Product product : allProducts) {
            if(productId == product.getId()) {
                return product;
            }
        }
        return null;
    }

    /**
     *
     * @param productId the id of the product to be searched
     * @param newValue the search text
     * @return if the product matches the search text
     */
    public static boolean lookupProduct(String productId, String newValue){
        if(productId.contains(newValue)){
            return true;
        }
        return false;
    }

    /**
     *
     * @param partName the name of a part to be searched
     * @return the list of parts matching the search
     */
    public static ObservableList<Part> lookupPart(String partName) {
        ObservableList <Part> searchParts = FXCollections.observableArrayList();
        if (allParts.size() >= 1){
            for (Part part : allParts){
                if (part.getName().contains(partName)){
                    searchParts.add(part);
                }
            }
            return searchParts;
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param productName the name of a product to be searched
     * @return the list of products matching the search
     */
    public static ObservableList<Product> lookupProduct(String productName) {
        ObservableList <Product> searchProducts = FXCollections.observableArrayList();
        if(allProducts.size() >= 1){
            for (Product product : allProducts){
                if (product.getName().contains(productName)){
                    searchProducts.add(product);
                }
            }
            return searchProducts;
        }
        else {
            return null;
        }
    }

    /**
     * @param index the row index of the part in the table
     * @param selectedPart the selected part in the table
     * */
    public static void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    /**
     * @param index the row index of the product in the table
     * @param newProduct the selected product in the table
     */
    public static void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    /**
     * @param selectedPart the selected part in the table
     * @return if the selected part matches the part from the table
     * */
    public static boolean deletePart(Part selectedPart) {
        getAllParts().remove(selectedPart);
        return true;
    }

    /**
     * @param selectedProduct the selected product in the table
     * @return if the product matches the product from the table
     * */
    public static boolean deleteProduct(Product selectedProduct) {
        getAllProducts().remove(selectedProduct);
        return true;
    }

    /**
     * @return every part from inventory
     * */
    public static ObservableList<Part> getAllParts() {
        return allParts;
    }

    /**
     * @return every product from inventory
     * */
    public static ObservableList<Product> getAllProducts() {
        return allProducts;
    }


}
