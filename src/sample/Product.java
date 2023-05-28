package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Contains product fields and methods
 *
 * @author Long Tran
 * */
public class Product{

    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    public Product(int id, String name, double price, int stock, int min, int max){
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }
    /**
     * @param id the product ID to be set
     * */
    public void setId (int id){
        this.id = id;
    }
    /**
     * @param name the product name to be set
     * */
    public void setName (String name){
        this.name = name;
    }
    /**
     * @param price the product's price to be set
     * */
    public void setPrice (double price){
        this.price = price;
    }
    /**
     * @param stock the amount of the product available in inventory
     * */
    public void setStock (int stock){
        this.stock = stock;
    }
    /**
     * @param min the minimum amount of the product in inventory
     * */
    public void setMin (int min){
        this.min = min;
    }
    /**
     * @param max the maximum amount of the product in inventory
     * */
    public void setMax (int max){
        this.max = max;
    }
    /**
     * @return the product ID
     * */
    public int getId(){
        return id;
    }
    /**
     * @return the product name
     * */
    public String getName(){
        return name;
    }
    /**
     * @return the product price
     */
    public double getPrice(){
        return price;
    }
    /**
     * @return the available product stock in inventory
     * */
    public int getStock(){
        return stock;
    }
    /**
     * @return the minimum product stock in inventory
     * */
    public int getMin(){
        return min;
    }
    /**
     * @return the maximum product stock in inventory
     * */
    public int getMax(){
        return max;
    }
    /**
     * @param part the part to be associated with the product
     * */
    public void addAssociatedPart(Part part){
        getAllAssociatedParts().add(part);
    }
    /**
     * @param selectedAssociatedPart the part associated with the product
     * @return if the selected part matches with the product's associated part*/
    public boolean deleteAssociatedPart(Part selectedAssociatedPart){
        getAllAssociatedParts().remove(selectedAssociatedPart);
        return true;
    }
    /**
     * @return all parts associated with products
     * */
    public ObservableList<Part> getAllAssociatedParts(){
        return associatedParts;
    }


}