package sample;

import java.util.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.event.*;
/**
 * An Inventory Management System that supports adding, modifying and removing manufactured parts and products
 *
 * Upcoming feature in next version update: parts associated with a product will be protected from deletion in order
 * to avoid having "phantom" or nonexistent parts used in products. Attempting to delete a part in use from the main
 * window will result in an error informing the user that the part is being used in a product.
 *
 * @author Long Tran
 * */
public class Main extends Application {
    /**
     * The auto-generated ID for new parts
     */
    public static int partID = Inventory.getAllParts().size()+1;
    /**
     * The auto-generated ID for new products
     */
    public static int productID = Inventory.getAllProducts().size()+1000;
    /**
     * The placeholder for a product to be modified
     */
    public Product product1 = new Product(0,"",0,0,0,0);

    /**
     * The table to be filled with all parts in inventory
     */
    private static TableView<Part> PartsTable = new TableView();

    /**
     * The table to be filled with parts identical to main window parts table
     */
    private static TableView<Part> PartsTable1 = new TableView();

    /**
     * The table to be filled with parts associated with a product
     */
    private static TableView<Part> PartsTable2 = new TableView();

    /**
     * The table to be filled with all products in inventory
     */
    private static TableView<Product> ProductsTable = new TableView();

    /**
     * The error message for invalid input
     */
    private static String exceptionText;

    /**
     * The error message for part form errors
     */
    private static Text partError = new Text();

    /**
     * The error message for product form errors
     */
    private static Text productError = new Text();

    /**
     * The search field for parts
     */
    private TextField SearchPart;

    /**
     * The search field for products
     */
    private TextField SearchProduct;

    /**
     * The title for the main window
     */
    private Label imsTitle;

    /**
     * The boolean to check if a window is already open
     */
    private static boolean isWindowOpen = false;

    /**
     *
     * @param primaryStage the main window
     * @throws Exception for any exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //Add Part and Product titles
        imsTitle = new Label("Inventory Management System"); //Creates a Label control.
        imsTitle.setFont(Font.font(null, FontWeight.BOLD, 14));
        Label partsTitle = new Label("Parts"); //Create a title for Parts box
        partsTitle.setFont(Font.font(null, FontWeight.BOLD, 12));
        Label productsTitle = new Label("Products"); //Create a title for Products box
        productsTitle.setFont(Font.font(null, FontWeight.BOLD, 12));

        //Create a TextField for searching a part
        SearchPart = new TextField();
        SearchPart.setPromptText("Search by Part ID or Name");
        SearchProduct = new TextField();
        //Create a TextField for searching a product
        SearchProduct.setPromptText("Search by Product ID or Name");

        //Create search function for Parts Table
        FilteredList<Part> filteredParts = new FilteredList<>(Inventory.getAllParts(), p -> true);
        SearchPart.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredParts.setPredicate(Part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (Inventory.lookupPart(Part.getName().toLowerCase(), newValue.toLowerCase())) {
                    return true;
                }
                if(Inventory.lookupPart(Integer.valueOf(Part.getId()).toString(), newValue.toLowerCase())){
                    return true;
                }
                return false;
            });
        });

        FilteredList<Product> filteredProducts = new FilteredList<>(Inventory.getAllProducts(), p -> true);
        SearchProduct.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(Product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (Inventory.lookupProduct(Product.getName().toLowerCase(), newValue.toLowerCase())) {
                    return true;
                }
                if (Inventory.lookupProduct(Integer.valueOf(Product.getId()).toString(), newValue.toLowerCase())){
                    return true;
                }
                return false;
            });
        });

        /**
         * I ran into a problem where the parts table appeared to be empty despite having items in
         * inventory. Originally when I wrote the code for the table columns I assigned fields from a custom made
         * class to the value factories and they worked at first when I used the custom class to add inventory items.
         * When I figured out how to use the Part class I switched my parts inventory over from the custom part class
         * the parts disappeared from the table. After spending some time researching the issue I realized I never
         * changed the value factories. I edited the columns to match the Part class fields and the items reappeared
         * in the table.
         */
        TableColumn PartID = new TableColumn("Part ID");
        PartID.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        TableColumn PartName = new TableColumn("Part Name");
        PartName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        TableColumn ProductID = new TableColumn("Product ID");
        ProductID.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        TableColumn ProductName = new TableColumn("Product Name");
        ProductName.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));

        TableColumn PartInvLevel = new TableColumn("Inventory Level");
        PartInvLevel.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        TableColumn ProdInvLevel = new TableColumn("Inventory Level");
        ProdInvLevel.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        TableColumn PartPCPU = new TableColumn("Price/Cost per Unit");
        PartPCPU.setMinWidth(120);
        PartPCPU.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
        TableColumn ProdPCPU = new TableColumn("Price/Cost per Unit");
        ProdPCPU.setMinWidth(120);
        ProdPCPU.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
        PartsTable.getColumns().addAll(PartID, PartName, PartInvLevel, PartPCPU);
        ProductsTable.getColumns().addAll(ProductID, ProductName, ProdInvLevel, ProdPCPU);

        //Insert data into tables
        PartsTable.setItems(filteredParts);
        PartsTable.setPlaceholder(new Label ("No part found."));
        ProductsTable.setItems(filteredProducts);
        ProductsTable.setPlaceholder(new Label ("No product found."));

        ListView partsView = new ListView < > (); //Create a ListView of Parts
        partsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListView productsView = new ListView < > (); //Create a ListView of Products
        productsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        partsView.setPrefSize(120, 100);
        productsView.setPrefSize(120, 100);

        //Text partError = new Text();
        //Text productError = new Text();

        //Create Add, Modify, and Delete buttons for Parts table
        Button addPartButton = new Button("Add");
        addPartButton.setOnAction(event -> {
            if(!isWindowOpen){
                addPartForm();
                partError.setText("");
                productError.setText("");
                isWindowOpen = true;
            }
            else if(isWindowOpen){
                partError.setText("A form window is already open.   ");
            }
        });
        Button modifyPartButton = new Button("Modify");
        modifyPartButton.setOnAction(event -> {
            if(!isWindowOpen){
                try {
                    int i = PartsTable.getSelectionModel().getSelectedItem().getId();
                    modifyPartForm();
                    partError.setText("");
                    productError.setText("");
                    isWindowOpen = true;
                } catch (Exception g) {
                    partError.setText("Please select a part to modify.     ");
                }
            }
            else if(isWindowOpen){
                partError.setText("A form window is already open.   ");
            }

        });
        Button deletePartButton = new Button("Delete");

        //Creates delete confirmation dialogue
        //Creates title
        Label title = new Label("Delete");
        title.setFont(Font.font("Arial", 14));
        title.setPadding(new Insets(10));
        //Create separator
        Separator separator = new Separator();
        //Creates delete confirmation message
        Label message = new Label();
        message.setPadding(new Insets(10));
        //Create OK and Cancel buttons
        Button okButton = new Button("OK");
        okButton.setPrefWidth(80);
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(80);
        //Organizes elements
        HBox HButtons = new HBox(10, okButton, cancelButton);
        HButtons.setAlignment(Pos.CENTER_RIGHT);
        HButtons.setPadding(new Insets(10));
        VBox VDeleteBottom = new VBox(message, HButtons);
        VBox VDeleteMain = new VBox(10, title, separator, VDeleteBottom);
        VDeleteMain.setAlignment(Pos.BOTTOM_LEFT);
        //VMain.setPadding(new Insets(10));
        Scene deleteScene = new Scene(VDeleteMain, 400, 150);
        Stage deleteStage = new Stage();
        deleteStage.setScene(deleteScene);
        deletePartButton.setOnAction(event -> {
            try {
                int i = PartsTable.getSelectionModel().getSelectedItem().getId();
                message.setText("Do you want to delete this part?");
                deleteStage.show();
                partError.setText("");
                productError.setText("");
            } catch (Exception g) {
                partError.setText("Please select a part to delete.      ");
            }
            okButton.setOnAction(e -> {
                Part selectedPart = PartsTable.getSelectionModel().getSelectedItem();
                Inventory.deletePart(selectedPart);
                deleteStage.close();});
        });

        //confirm delete
        cancelButton.setOnAction(event -> {
            deleteStage.close();});

        //Create Add, Modify, and Delete buttons for Products table
        Button addProductButton = new Button("Add");
        addProductButton.setOnAction(event -> {
            if(!isWindowOpen){
                addProductForm();
                partError.setText("");
                productError.setText("");
                isWindowOpen = true;
            } else if(isWindowOpen){
                productError.setText("A form window is already open.   ");
            }
        });
        Button modifyProductButton = new Button("Modify");
        modifyProductButton.setOnAction(event -> {
            if(!isWindowOpen){
                try {
                    int i = ProductsTable.getSelectionModel().getSelectedItem().getId();
                    modifyProductForm();
                    partError.setText("");
                    productError.setText("");
                    isWindowOpen = true;
                } catch (Exception g) {
                    productError.setText("Please select a product to modify. ");
                }
            }
            else if(isWindowOpen){
                productError.setText("A form window is already open.   ");
            }
        });
        Button deleteProductButton = new Button("Delete");
        deleteProductButton.setOnAction(event -> {
            try {
                int i = ProductsTable.getSelectionModel().getSelectedItem().getId();
                message.setText("Do you want to delete this product?");
                deleteStage.show();
                partError.setText("");
                productError.setText("");
            } catch (Exception h) {
                productError.setText("Please select a product to delete.  ");
            }
            Product product = ProductsTable.getSelectionModel().getSelectedItem();
            okButton.setOnAction(e -> {
                if (product.getAllAssociatedParts().size() < 1) {
                    partError.setText("");
                    productError.setText("");
                    Product selectedProduct = ProductsTable.getSelectionModel().getSelectedItem();
                    Inventory.deleteProduct(selectedProduct);
                }
                else {
                    productError.setText("The product has associated parts.     ");
                }
                deleteStage.close();});
        });
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> { System.exit(0);        });


        //Organizes GUI elements in window
        HBox HTitle = new HBox(imsTitle);
        HTitle.setPadding(new Insets(10));
        HBox HPartsTop = new HBox(150, partsTitle, SearchPart);
        HBox HPartsCenter = new HBox(PartsTable);
        HBox HPartsBottom = new HBox(10, partError, addPartButton, modifyPartButton, deletePartButton);
        HPartsBottom.setAlignment(Pos.CENTER_RIGHT);
        HBox HProductsTop = new HBox(150, productsTitle, SearchProduct);
        HBox HProductsCenter = new HBox(ProductsTable);
        HBox HProductsBottom = new HBox(10, productError, addProductButton, modifyProductButton, deleteProductButton);
        HProductsBottom.setAlignment(Pos.CENTER_RIGHT);

        //Creates Parts & Products boxes and organizes them
        VBox vparts = new VBox(10, HPartsTop, HPartsCenter, HPartsBottom);
        VBox vproducts = new VBox(10, HProductsTop, HProductsCenter, HProductsBottom);
        HBox HMain = new HBox(50, vparts, vproducts);
        HMain.setAlignment(Pos.CENTER); //Set the HBox's alignment to center.

        VBox VMain = new VBox(10, HTitle, HMain, exitButton);
        VMain.setAlignment(Pos.BOTTOM_RIGHT);
        VMain.setPadding(new Insets (20));
        Scene scene = new Scene(VMain, 900, 350); //Creates a Scene with the HBox as the root node.

        //Adds the Scene to the Stage.
        primaryStage.setScene(scene);
        //Shows the window.
        primaryStage.show();
    }

    /**
     * The form for adding new parts to inventory
     */
    public void addPartForm() {
        //Create title
        Label title = new Label("Add Part");

        //Create MachineID and CompanyName fields
        final Label lastLabel = new Label ("Machine ID");
        title.setFont(Font.font(null, FontWeight.BOLD, 14));
        //Create Radio Buttons for "In-House" and "Outsourced"
        ToggleGroup toggle = new ToggleGroup();
        RadioButton ihButton = new RadioButton("In-House");
        ihButton.setToggleGroup(toggle);
        ihButton.setSelected(true);
        //Changes label text based on radio button selection
        ihButton.setOnAction(event ->{
            lastLabel.setText("Machine ID");
        });
        RadioButton osButton = new RadioButton("Outsourced");
        osButton.setToggleGroup(toggle);
        //Changes label text based on radio button selection
        osButton.setOnAction(event ->{
            lastLabel.setText("Company Name");
        });
        //Creates "Save" and "Cancel" Buttons
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        //Creates text boxes
        Label idLabel = new Label("ID");
        TextField idField = new TextField();
        idField.setDisable(true);
        idField.setPromptText("Auto Gen - Disabled");
        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        Label invLabel = new Label("Inv");
        TextField invField = new TextField();
        Label pcLabel = new Label("Price/Cost");
        TextField pcField = new TextField();
        Label maxLabel = new Label("Max");
        TextField maxField = new TextField();
        Label minLabel = new Label("Min");
        TextField minField = new TextField();
        //Creates text boxes for machineID and CompanyName
        TextField lastField = new TextField();

        //Creates text for errors
        Text exceptionErrors = new Text();
        Text exceptionError = new Text();

        //Organizes elements in window
        GridPane grid = new GridPane();
        grid.getRowConstraints().add(new RowConstraints(100)); //Row 0 constraints
        grid.getColumnConstraints().add(new ColumnConstraints(120)); //Column 0 Constraints
        grid.getColumnConstraints().add(new ColumnConstraints(125)); //Column 1 Constraints
        grid.getColumnConstraints().add(new ColumnConstraints(70)); //Column 2 Constraints
        grid.getColumnConstraints().add(new ColumnConstraints(100)); //Column 3 Constraints
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets (50));
        grid.add(title, 0, 0, 2, 1);
        grid.add(ihButton, 1, 0, 1, 1);
        grid.add(osButton, 2,0, 2, 1);

        grid.add(idLabel, 0, 2, 1, 1);
        GridPane.setMargin(idLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(idField, 1, 2, 1, 1);

        grid.add(nameLabel, 0, 3, 1, 1);
        GridPane.setMargin(nameLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(nameField, 1, 3, 1, 1);

        grid.add(invLabel, 0, 4, 1, 1);
        GridPane.setMargin(invLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(invField, 1, 4, 1, 1);

        grid.add(pcLabel, 0, 5, 2, 1);
        GridPane.setMargin(pcLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(pcField, 1, 5, 1, 1);

        grid.add(maxLabel, 0, 6, 1, 1);
        GridPane.setMargin(maxLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(maxField, 1, 6, 1, 1);
        grid.add(minLabel, 2, 6, 1, 1);
        GridPane.setMargin(minLabel, new Insets(0, 20, 0, 20)); //(top, right, bottom, left)
        grid.add(minField, 3, 6, 1, 1);

        grid.add(lastLabel, 0, 7, 2, 1);
        GridPane.setMargin(lastLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(lastField, 1, 7, 1, 1);
        grid.add(saveButton, 2, 9, 1, 1);
        GridPane.setMargin(saveButton, new Insets(0, 10, 0, 10));
        grid.add(cancelButton, 3, 9, 1, 1);

        grid.add(exceptionError,0,11,2,1);
        grid.add(exceptionErrors,0,12,2,1);

        Scene scene = new Scene(grid,600, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        /*Button action gathers data from form to FormData to save Part to Parts table in main window*/
        saveButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){

                Part inPart = new InHouse(0,"",0,0,0,0,0);
                Part outPart = new Outsourced(0,"",0,0,0,0,"");
                /*Removes error messages if entered formats are correct*/
                exceptionError.setText("");
                exceptionErrors.setText("");
                exceptionText = ("");
                int max = 0;
                int min = 0;
                int inv = 0;
                boolean minMaxValid = false;
                int machineid = 0;
                //only proceed if all values are valid
                int validCheck = 0;

                /*Boolean variable checks if inventory input value is an integer*/
                boolean invIsValid = false;
                /*Boolean variables check if Min and Max input values are*/
                boolean minInput = false;
                boolean maxInput = false;
                /*Make error messages appear when incorrect format is entered*/
                try {
                    int i = nameField.getText().length();
                    int j = 1/i;
                    inPart.setName(nameField.getText());
                    outPart.setName(nameField.getText());
                    validCheck++;
                } catch ( Exception f){
                    exceptionError.setText("Exception:");
                    exceptionText = ("No data in name field.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    inv = Integer.parseInt(invField.getText());
                    invIsValid = true;
                    inPart.setStock(inv);
                    outPart.setStock(inv);
                    validCheck++;
                } catch (Exception x){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Inventory is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    invIsValid = false;
                    validCheck = 0;
                }

                try {
                    double d = Double.parseDouble(pcField.getText());
                    inPart.setPrice(d);
                    outPart.setPrice(d);
                    validCheck++;
                } catch (/*NumberFormatException*/ Exception f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Price is not a double.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    max = Integer.parseInt(maxField.getText());
                    inPart.setMax(max);
                    outPart.setMax(max);
                    maxInput = true;
                    validCheck++;
                } catch (/*NumberFormatException*/ Exception f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Max is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    minMaxValid = false;
                    validCheck = 0;
                }

                try {
                    min = Integer.parseInt(minField.getText());
                    inPart.setMin(min);
                    outPart.setMin(min);
                    minInput = true;
                    validCheck++;
                } catch (/*NumberFormatException*/ Exception f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    minMaxValid = false;
                    validCheck = 0;
                }

                if (min > max && minInput && maxInput) {
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min must be less than Max.\n");
                    exceptionErrors.setText(exceptionText);
                    //throw new IllegalArgumentException();
                    validCheck = 0;
                }

                if (min <= max && invIsValid) {
                    if (inv < min || inv > max) {
                        exceptionError.setText("Exception:");
                        exceptionText = (exceptionText + "Inv must be between Min and Max.\n");
                        exceptionErrors.setText(exceptionText);
                        //throw new IllegalArgumentException();
                        validCheck = 0;
                    }
                }

                /*Enters machineID as 0 if field is in incorrect format or left empty.*/
                if (ihButton.isSelected()) {
                    try {
                        machineid = Integer.parseInt(lastField.getText());
                    } catch (NumberFormatException f){
                        machineid = 0;
                    }
                }

                /*Halts program if name field is blank*/
                if (nameField.getLength() == 0) {
                    //throw new IllegalArgumentException();
                    validCheck = 0;
                }


                if (validCheck == 5){
                    if (ihButton.isSelected()){
                        InHouse in = (InHouse) inPart;
                        in.setId(partID);
                        in.setMachineId(machineid);
                        Inventory.addPart(in);
                    }
                    else if (osButton.isSelected()){
                        Outsourced out = (Outsourced) outPart;
                        out.setId(partID);
                        out.setCompanyName(lastField.getText());
                        Inventory.addPart(out);
                    }
                    partID++;
                    stage.close();
                    isWindowOpen = false;
                    partError.setText("");
                    productError.setText("");
                }
            }
        });
        //closes window without making changes
        cancelButton.setOnAction(event -> {
            stage.close();
            isWindowOpen = false;
            partError.setText("");
            productError.setText("");
        });
    }

    /**
     * The form for modifying parts in inventory
     */
    public void modifyPartForm() {

        //Create title
        Label title = new Label("Modify Part");
        final Label lastLabel = new Label ("Machine ID");
        title.setFont(Font.font(null, FontWeight.BOLD, 14));

        //Create Radio Buttons for "In-House" and "Outsourced" and changes label text based on button selection
        ToggleGroup toggle = new ToggleGroup();
        RadioButton ihButton = new RadioButton("In-House");
        ihButton.setToggleGroup(toggle);
        ihButton.setOnAction(event ->{
            lastLabel.setText("Machine ID");
        });

        RadioButton osButton = new RadioButton("Outsourced");
        osButton.setToggleGroup(toggle);
        osButton.setOnAction(event ->{
            lastLabel.setText("Company Name");
        });

        //Creates "Save" and "Cancel" Buttons
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        Label idLabel = new Label("ID");
        TextField idField = new TextField();
        idField.setDisable(true);

        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        Label invLabel = new Label("Inv");
        TextField invField = new TextField();
        Label pcLabel = new Label("Price/Cost");
        TextField pcField = new TextField();
        Label maxLabel = new Label("Max");
        TextField maxField = new TextField();
        Label minLabel = new Label("Min");
        TextField minField = new TextField();
        TextField lastField = new TextField();

        //Creates text for errors
        Text exceptionErrors = new Text();
        Text exceptionError = new Text();

        //Organizes elements in window
        GridPane grid = new GridPane();
        grid.getRowConstraints().add(new RowConstraints(100)); //Row 0 constraints
        grid.getColumnConstraints().add(new ColumnConstraints(120)); //Column 0 Constraints
        grid.getColumnConstraints().add(new ColumnConstraints(125)); //Column 1 Constraints
        grid.getColumnConstraints().add(new ColumnConstraints(70)); //Column 2 Constraints
        grid.getColumnConstraints().add(new ColumnConstraints(100)); //Column 3 Constraints
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets (50));
        grid.add(title, 0, 0, 2, 1);
        grid.add(ihButton, 1, 0, 1, 1);
        grid.add(osButton, 2,0, 2, 1);

        grid.add(idLabel, 0, 2, 1, 1);
        GridPane.setMargin(idLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(idField, 1, 2, 1, 1);

        grid.add(nameLabel, 0, 3, 1, 1);
        GridPane.setMargin(nameLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(nameField, 1, 3, 1, 1);

        grid.add(invLabel, 0, 4, 1, 1);
        GridPane.setMargin(invLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(invField, 1, 4, 1, 1);

        grid.add(pcLabel, 0, 5, 2, 1);
        GridPane.setMargin(pcLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(pcField, 1, 5, 1, 1);

        grid.add(maxLabel, 0, 6, 1, 1);
        GridPane.setMargin(maxLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(maxField, 1, 6, 1, 1);
        grid.add(minLabel, 2, 6, 1, 1);
        GridPane.setMargin(minLabel, new Insets(0, 20, 0, 20)); //(top, right, bottom, left)
        grid.add(minField, 3, 6, 1, 1);

        grid.add(lastLabel, 0, 7, 2, 1);
        GridPane.setMargin(lastLabel, new Insets(0, 0, 0, 20)); //(top, right, bottom, left)
        grid.add(lastField, 1, 7, 1, 1);
        grid.add(saveButton, 2, 9, 1, 1);
        GridPane.setMargin(saveButton, new Insets(0, 10, 0, 10));
        grid.add(cancelButton, 3, 9, 1, 1);

        grid.add(exceptionError,0,11,2,1);
        grid.add(exceptionErrors,0,12,2,1);

        Scene scene = new Scene(grid,600, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        //Placeholders
        int machineID = 0;
        String companyName = "";

        Part selectedInHouse = new InHouse(
                PartsTable.getSelectionModel().getSelectedItem().getId(),
                PartsTable.getSelectionModel().getSelectedItem().getName(),
                PartsTable.getSelectionModel().getSelectedItem().getPrice(),
                PartsTable.getSelectionModel().getSelectedItem().getStock(),
                PartsTable.getSelectionModel().getSelectedItem().getMin(),
                PartsTable.getSelectionModel().getSelectedItem().getMax(),
                machineID);

        Part selectedOutsourced = new Outsourced(
                PartsTable.getSelectionModel().getSelectedItem().getId(),
                PartsTable.getSelectionModel().getSelectedItem().getName(),
                PartsTable.getSelectionModel().getSelectedItem().getPrice(),
                PartsTable.getSelectionModel().getSelectedItem().getStock(),
                PartsTable.getSelectionModel().getSelectedItem().getMin(),
                PartsTable.getSelectionModel().getSelectedItem().getMax(),
                companyName);

        //create id variable to obtain ID for selected part entry and sets id field it
        int id = PartsTable.getSelectionModel().getSelectedItem().getId();
        idField.setText(String.valueOf(id));


        nameField.setText(PartsTable.getSelectionModel().getSelectedItem().getName());
        invField.setText(String.valueOf(PartsTable.getSelectionModel().getSelectedItem().getStock()));
        pcField.setText(String.valueOf(PartsTable.getSelectionModel().getSelectedItem().getPrice()));
        maxField.setText(String.valueOf(PartsTable.getSelectionModel().getSelectedItem().getMax()));
        minField.setText(String.valueOf(PartsTable.getSelectionModel().getSelectedItem().getMin()));


        Part part = PartsTable.getSelectionModel().getSelectedItem();
        //casts part (6 variables) to inhouse or outsourced (7 variables)
        /*try {
            machineID = ((InHouse) part).getMachineId();
            ihButton.setSelected(true);
            lastField.setText(String.valueOf(machineID));
        } catch (Exception t){
            companyName = ((Outsourced) part).getCompanyName();
            osButton.setSelected(true);
            lastField.setText(companyName);
        }*/
        if (part instanceof InHouse){
            ihButton.setSelected(true);
            machineID = ((InHouse) part).getMachineId();
            lastField.setText(String.valueOf(machineID));
            lastLabel.setText("Machine ID");
        } else if (part instanceof Outsourced) {
            osButton.setSelected(true);
            companyName = ((Outsourced) part).getCompanyName();
            lastField.setText(companyName);
            lastLabel.setText("Company Name");
        }
        saveButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){
                Part inPart = new InHouse(0,"",0,0,0,0,0);
                Part outPart = new Outsourced(0,"",0,0,0,0,"");
                /*Removes error messages if entered formats are correct*/
                exceptionError.setText("");
                exceptionErrors.setText("");
                exceptionText = ("");
                int max = 0;
                int min = 1;
                int inv = 0;
                int machineid = 0;

                /*Boolean variable checks if inventory input value is an integer*/
                boolean invIsValid = false;
                /*Boolean variables check if Min and Max input values are*/
                boolean minInput = false;
                boolean maxInput = false;
                //only proceed if all values are valid
                int validCheck = 0;

                /*Make error messages appear when incorrect format is entered*/
                try {
                    int i = nameField.getLength();
                    System.out.println(1/i);
                    inPart.setName(nameField.getText());
                    outPart.setName(nameField.getText());
                    validCheck++;
                } catch (ArithmeticException f){
                    exceptionError.setText("Exception:");
                    exceptionText = ("No data in name field.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    inv = Integer.parseInt(invField.getText());
                    invIsValid = true;
                    inPart.setStock(inv);
                    outPart.setStock(inv);
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Inventory is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    invIsValid = false;
                    validCheck = 0;
                }

                try {
                    double d = Double.parseDouble(pcField.getText());
                    inPart.setPrice(d);
                    outPart.setPrice(d);
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Price is not a double.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    max = Integer.parseInt(maxField.getText());
                    inPart.setMax(max);
                    outPart.setMax(max);
                    maxInput = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Max is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    maxInput = false;
                    validCheck = 0;
                }

                try {
                    min = Integer.parseInt(minField.getText());
                    inPart.setMin(min);
                    outPart.setMin(min);
                    minInput = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    minInput = false;
                    validCheck = 0;
                }

                if (min > max && minInput && maxInput) {
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min must be less than Max.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                if (min <= max && invIsValid) {
                    if (inv < min || inv > max) {
                        exceptionError.setText("Exception:");
                        exceptionText = (exceptionText + "Inv must be between Min and Max.\n");
                        exceptionErrors.setText(exceptionText);
                        validCheck = 0;
                    }
                }

                /*Enters machineID as 0 if field is left empty.*/
                if (ihButton.isSelected()) {
                    try {
                        machineid = Integer.parseInt(lastField.getText());
                    } catch (NumberFormatException f){
                        exceptionError.setText("Exception:");
                        exceptionText = (exceptionText + "Machine ID is not an integer.\n");
                        exceptionErrors.setText(exceptionText);
                        validCheck = 0;
                    }
                }

                /*Halts program if name field is blank*/
                if (nameField.getLength() == 0) {
                    validCheck = 0;
                }

                int index = PartsTable.getSelectionModel().getSelectedIndex();
                if (validCheck == 5) {
                    if (ihButton.isSelected()){
                        InHouse in = (InHouse) inPart;
                        in.setId(Integer.parseInt(idField.getText()));
                        in.setMachineId(machineid);
                        Inventory.updatePart(index, in);
                    }

                else if (osButton.isSelected()){
                        Outsourced out = (Outsourced) outPart;
                        out.setId(Integer.parseInt(idField.getText()));
                        out.setCompanyName(lastField.getText());
                        Inventory.updatePart(index, out);
                    }
                    stage.close();
                    isWindowOpen = false;
                    partError.setText("");
                    productError.setText("");
                }
            }
        });
        cancelButton.setOnAction(event -> {
            stage.close();
            isWindowOpen = false;
            partError.setText("");
            productError.setText("");
        });
    }

    /**
     * The form for adding new products to inventory
     */
    public void addProductForm(){
        //Create titles for both Add and Modify forms
        Label title = new Label("Add Product");
        title.setFont(Font.font(null, FontWeight.BOLD, 14));

        //Create tables with columns
        TableColumn PartID1 = new TableColumn("Part ID");
        PartID1.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        TableColumn PartName1 = new TableColumn("Part Name");
        PartName1.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        TableColumn PartInvLevel1 = new TableColumn("Inventory Level");
        PartInvLevel1.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        TableColumn PartPCPU1 = new TableColumn("Price/Cost per Unit");
        PartPCPU1.setMinWidth(120);
        PartPCPU1.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        TableColumn PartID2 = new TableColumn("Part ID");
        PartID2.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        TableColumn PartName2 = new TableColumn("Part Name");
        PartName2.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        TableColumn PartInvLevel2 = new TableColumn("Inventory Level");
        PartInvLevel2.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        TableColumn PartPCPU2 = new TableColumn("Price/Cost per Unit");
        PartPCPU2.setMinWidth(120);
        PartPCPU2.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        TextField searchPart = new TextField();
        searchPart.setPromptText("Search by Part ID or Name");

        FilteredList<Part> filteredParts = new FilteredList<>(Inventory.getAllParts(), p -> true);
        searchPart.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredParts.setPredicate(Part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (Inventory.lookupPart(Part.getName().toLowerCase(),newValue.toLowerCase())) {
                    return true;
                }
                if (Inventory.lookupPart(Integer.valueOf(Part.getId()).toString(),(newValue.toLowerCase()))) {
                    return true;
                }
                return false;
            });
        });

        PartsTable1.getColumns().addAll(PartID1, PartName1, PartInvLevel1, PartPCPU1);
        PartsTable1.setItems(filteredParts);
        PartsTable1.setPlaceholder(new Label("No parts found."));
        Product product = new Product(0,"",0,0,0,0);
        PartsTable2.getColumns().addAll(PartID2, PartName2, PartInvLevel2, PartPCPU2);
        PartsTable2.setItems(product.getAllAssociatedParts());
        PartsTable2.setPlaceholder(new Label("No associated parts."));


        //Creates text boxes and buttons
        Label idLabel = new Label("ID");
        TextField idField = new TextField();
        idField.setDisable(true);
        idField.setPromptText("Auto Gen - Disabled");
        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        Label invLabel = new Label("Inv");
        TextField invField = new TextField();
        Label pcLabel = new Label("Price");
        TextField pcField = new TextField();
        Label maxLabel = new Label("Max");
        TextField maxField = new TextField();
        Label minLabel = new Label("Min");
        TextField minField = new TextField();

        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove Associated Part");
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        //Creates text for errors
        Text exceptionErrors = new Text();
        Text exceptionError = new Text();

        //Organizes text fields into window
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(20);
        grid.getColumnConstraints().add(new ColumnConstraints(75));
        grid.setPadding(new Insets(30));
        grid.add(title,0,0,3,1);
        GridPane.setMargin(title, new Insets(0,0,20,0)); //(top, right, bottom, left)
        grid.add(idLabel,0,2,1,1);
        GridPane.setMargin(idLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        idLabel.setMinWidth(20);
        grid.add(idField,1,2,3,1);
        idField.setMaxWidth(150);
        grid.add(nameLabel,0,3,1,1);
        GridPane.setMargin(nameLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(nameField,1,3,3,1);
        nameField.setMaxWidth(150);
        grid.add(invLabel,0,4,1,1);
        GridPane.setMargin(invLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(invField,1,4,1,1);
        invField.setMaxWidth(100);
        grid.add(pcLabel,0,5,1,1);
        GridPane.setMargin(pcLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(pcField,1,5,1,1);
        pcField.setMaxWidth(100);
        grid.add(maxLabel,0,6,1,1);
        GridPane.setMargin(maxLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(maxField,1,6,1,1);
        maxField.setMaxWidth(100);
        grid.add(minLabel,2,6,1,1);
        GridPane.setMargin(minLabel, new Insets(0,0,0,10));
        minLabel.setMinWidth(30);
        grid.add(minField,3,6,1,1);
        minField.setMaxWidth(100);

        grid.add(exceptionError,0,8,2,1);
        grid.add(exceptionErrors,0,9,2,1);

        //organizes tables and buttons into window
        grid.add(searchPart,6,0,2,1);
        searchPart.setMaxWidth(170);
        GridPane.setMargin(searchPart, new Insets(0,0,0,200));
        grid.add(PartsTable1,4,1,4,4);
        GridPane.setMargin(PartsTable1, new Insets(0,0,0,20)); //(top, right, bottom, left)
        PartsTable1.setMaxWidth(360);
        PartsTable1.setMaxHeight(140);
        grid.add(addButton,6,5,2,1);
        GridPane.setMargin(addButton, new Insets(0,0,0,250));
        grid.add(PartsTable2,4,6,4,4);
        GridPane.setMargin(PartsTable2, new Insets(0,0,0,20));
        PartsTable2.setMaxWidth(360);
        PartsTable2.setMaxHeight(140);
        grid.add(removeButton,6,10,2,1);
        GridPane.setMargin(removeButton, new Insets(0,0,0,210));
        grid.add(saveButton, 6, 11, 1, 1);
        saveButton.setMinWidth(30);
        GridPane.setMargin(saveButton, new Insets(0,0,0,210));
        grid.add(cancelButton,7,11,1,1);
        GridPane.setMargin(cancelButton, new Insets(0,0,0,30));

        Scene scene = new Scene(grid, 850, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        addButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){
                Part part = PartsTable1.getSelectionModel().getSelectedItem();
                product.addAssociatedPart(part);
            }
        });
        //Creates delete confirmation dialogue
        //Creates title
        Label removeTitle = new Label("Delete");
        removeTitle.setFont(Font.font("Arial", 14));
        removeTitle.setPadding(new Insets(10));
        //Create separator
        Separator separator = new Separator();
        //Creates delete confirmation message
        Label message = new Label();
        message.setPadding(new Insets(10));
        //Create OK and Cancel buttons
        Button confirmButton = new Button("OK");
        confirmButton.setPrefWidth(80);
        Button removeCancelButton = new Button("Cancel");
        removeCancelButton.setPrefWidth(80);
        //Text partError = new Text();
        //Organizes elements
        HBox HButtons = new HBox(10, confirmButton, removeCancelButton);
        HButtons.setAlignment(Pos.CENTER_RIGHT);
        HButtons.setPadding(new Insets(10));
        VBox VDeleteBottom = new VBox(message, HButtons);
        VBox VDeleteMain = new VBox(10, removeTitle, separator, VDeleteBottom);
        VDeleteMain.setAlignment(Pos.BOTTOM_LEFT);
        //VMain.setPadding(new Insets(10));
        Scene removeScene = new Scene(VDeleteMain, 400, 150);
        Stage removeStage = new Stage();
        removeStage.setScene(removeScene);
        removeButton.setOnAction(event -> {
            try {
                int i = PartsTable2.getSelectionModel().getSelectedItem().getId();
                message.setText("Do you want to delete this part?");
                removeStage.show();
                exceptionError.setText("");
            } catch (Exception g) {
                exceptionError.setText("Please select a part to delete.      ");
            }
            confirmButton.setOnAction(e -> {
                Part selectedPart = PartsTable2.getSelectionModel().getSelectedItem();
                product.deleteAssociatedPart(selectedPart);
                //partID--;
                removeStage.close();});
            //calls deletePart method from Inventory
        });

        //confirm delete
        removeCancelButton.setOnAction(event -> {
            removeStage.close();});
        saveButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){

                /*Removes error messages if entered formats are correct*/
                exceptionError.setText("");
                exceptionErrors.setText("");
                exceptionText = ("");
                int max = 0;
                int min = 0;
                int inv = 0;
                boolean minMaxValid = false;
                int machineid = 0;

                /*Boolean variable checks if inventory input value is an integer*/
                boolean invIsValid = false;
                /*Boolean variables check if Min and Max input values are*/
                boolean minInput = false;
                boolean maxInput = false;
                int validCheck = 0;

                /*Make error messages appear when incorrect format is entered*/
                try {
                    int i = nameField.getText().length();
                    int j = 1/i;
                    product.setName(nameField.getText());
                    validCheck++;
                } catch (ArithmeticException f){
                    exceptionError.setText("Exception:");
                    exceptionText = ("No data in name field.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    inv = Integer.parseInt(invField.getText());
                    product.setStock(inv);
                    invIsValid = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Inventory is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    invIsValid = false;
                    validCheck = 0;
                }

                try {
                    double d = Double.parseDouble(pcField.getText());
                    product.setPrice(d);
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Price is not a double.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    max = Integer.parseInt(maxField.getText());
                    product.setMax(max);
                    maxInput = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Max is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    maxInput = false;
                    validCheck = 0;
                }

                try {
                    min = Integer.parseInt(minField.getText());
                    product.setMin(min);
                    minInput = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    minMaxValid = false;
                    minInput = false;
                    validCheck = 0;
                }

                if (min > max && minInput && maxInput ) {
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min must be less than Max.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                if (min <= max && invIsValid) {
                    if (inv < min || inv > max) {
                        exceptionError.setText("Exception:");
                        exceptionText = (exceptionText + "Inv must be between Min and Max.\n");
                        exceptionErrors.setText(exceptionText);
                        validCheck = 0;
                    }
                }

                /*Halts program if name field is blank*/
                if (nameField.getLength() == 0) {
                    validCheck = 0;
                }

                if (validCheck == 5) {
                    product.setId(productID);
                    Inventory.addProduct(product);
                    productID++;
                    stage.close();
                    isWindowOpen = false;
                    partError.setText("");
                    productError.setText("");
                }
            }
        });
        cancelButton.setOnAction(event -> {
            stage.close();
            isWindowOpen = false;
            partError.setText("");
            productError.setText("");
        });
    }

    /**
     * The form for modifying products in inventory
     */
    public void modifyProductForm(){
        //Create titles for both Add and Modify forms
        Label title = new Label("Modify Product");
        title.setFont(Font.font(null, FontWeight.BOLD, 14));

        //Create tables with columns
        TableColumn PartID1 = new TableColumn("Part ID");
        PartID1.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        TableColumn PartName1 = new TableColumn("Part Name");
        PartName1.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        TableColumn PartInvLevel1 = new TableColumn("Inventory Level");
        PartInvLevel1.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        TableColumn PartPCPU1 = new TableColumn("Price/Cost per Unit");
        PartPCPU1.setMinWidth(120);
        PartPCPU1.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        TableColumn PartID2 = new TableColumn("Part ID");
        PartID2.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        TableColumn PartName2 = new TableColumn("Part Name");
        PartName2.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        TableColumn PartInvLevel2 = new TableColumn("Inventory Level");
        PartInvLevel2.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        TableColumn PartPCPU2 = new TableColumn("Price/Cost per Unit");
        PartPCPU2.setMinWidth(120);
        PartPCPU2.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        TextField searchPart = new TextField();
        searchPart.setPromptText("Search by Part ID or Name");

        FilteredList<Part> filteredParts = new FilteredList<>(Inventory.getAllParts(), p -> true);
        searchPart.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredParts.setPredicate(Part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (Inventory.lookupPart(Part.getName().toLowerCase(),(newValue.toLowerCase()))) {
                    return true;
                }
                if (Inventory.lookupPart(Integer.valueOf(Part.getId()).toString(),(newValue.toLowerCase()))) {
                    return true;
                }
                return false;
            });
        });

        PartsTable1.getColumns().addAll(PartID1, PartName1, PartInvLevel1, PartPCPU1);
        PartsTable1.setItems(filteredParts);
        PartsTable1.setPlaceholder(new Label("No parts found."));
        product1 = ProductsTable.getSelectionModel().getSelectedItem();
        PartsTable2.getColumns().addAll(PartID2, PartName2, PartInvLevel2, PartPCPU2);
        PartsTable2.setItems(product1.getAllAssociatedParts());
        PartsTable2.setPlaceholder(new Label("No associated parts."));

        //Creates text boxes and buttons
        Label idLabel = new Label("ID");
        TextField idField = new TextField();
        idField.setDisable(true);
        int id = ProductsTable.getSelectionModel().getSelectedItem().getId();
        idField.setText(String.valueOf(id));
        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        nameField.setText(ProductsTable.getSelectionModel().getSelectedItem().getName());
        Label invLabel = new Label("Inv");
        TextField invField = new TextField();
        invField.setText(String.valueOf(ProductsTable.getSelectionModel().getSelectedItem().getStock()));
        Label pcLabel = new Label("Price");
        TextField pcField = new TextField();
        pcField.setText(String.valueOf(ProductsTable.getSelectionModel().getSelectedItem().getPrice()));
        Label maxLabel = new Label("Max");
        TextField maxField = new TextField();
        maxField.setText(String.valueOf(ProductsTable.getSelectionModel().getSelectedItem().getMax()));
        Label minLabel = new Label("Min");
        TextField minField = new TextField();
        minField.setText(String.valueOf(ProductsTable.getSelectionModel().getSelectedItem().getMin()));

        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove Associated Part");
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        //Creates text for errors
        Text exceptionErrors = new Text();
        Text exceptionError = new Text();

        //Organizes text fields into window
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(20);
        grid.getColumnConstraints().add(new ColumnConstraints(75));
        grid.setPadding(new Insets(30));
        grid.add(title,0,0,3,1);
        GridPane.setMargin(title, new Insets(0,0,20,0)); //(top, right, bottom, left)
        grid.add(idLabel,0,2,1,1);
        GridPane.setMargin(idLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        idLabel.setMinWidth(20);
        grid.add(idField,1,2,3,1);
        idField.setMaxWidth(150);
        grid.add(nameLabel,0,3,1,1);
        GridPane.setMargin(nameLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(nameField,1,3,3,1);
        nameField.setMaxWidth(150);
        grid.add(invLabel,0,4,1,1);
        GridPane.setMargin(invLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(invField,1,4,1,1);
        invField.setMaxWidth(100);
        grid.add(pcLabel,0,5,1,1);
        GridPane.setMargin(pcLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(pcField,1,5,1,1);
        pcField.setMaxWidth(100);
        grid.add(maxLabel,0,6,1,1);
        GridPane.setMargin(maxLabel, new Insets(0,0,0,30)); //(top, right, bottom, left)
        grid.add(maxField,1,6,1,1);
        maxField.setMaxWidth(100);
        grid.add(minLabel,2,6,1,1);
        GridPane.setMargin(minLabel, new Insets(0,0,0,10));
        minLabel.setMinWidth(30);
        grid.add(minField,3,6,1,1);
        minField.setMaxWidth(100);

        grid.add(exceptionError,0,8,2,1);
        grid.add(exceptionErrors,0,9,2,1);

        //organizes tables and buttons into window
        grid.add(searchPart,6,0,2,1);
        searchPart.setMaxWidth(170);
        GridPane.setMargin(searchPart, new Insets(0,0,0,200));
        grid.add(PartsTable1,4,1,4,4);
        GridPane.setMargin(PartsTable1, new Insets(0,0,0,20)); //(top, right, bottom, left)
        PartsTable1.setMaxWidth(360);
        PartsTable1.setMaxHeight(140);
        grid.add(addButton,6,5,2,1);
        GridPane.setMargin(addButton, new Insets(0,0,0,250));
        grid.add(PartsTable2,4,6,4,4);
        GridPane.setMargin(PartsTable2, new Insets(0,0,0,20));
        PartsTable2.setMaxWidth(360);
        PartsTable2.setMaxHeight(140);
        grid.add(removeButton,6,10,2,1);
        GridPane.setMargin(removeButton, new Insets(0,0,0,210));
        grid.add(saveButton, 6, 11, 1, 1);
        saveButton.setMinWidth(30);
        GridPane.setMargin(saveButton, new Insets(0,0,0,210));
        grid.add(cancelButton,7,11,1,1);
        GridPane.setMargin(cancelButton, new Insets(0,0,0,30));

        Scene scene = new Scene(grid, 850, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        addButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){
                Part part = PartsTable1.getSelectionModel().getSelectedItem();
                product1.addAssociatedPart(part);
            }
        });
        //Creates delete confirmation dialogue
        //Creates title
        Label removeTitle = new Label("Delete");
        removeTitle.setFont(Font.font("Arial", 14));
        removeTitle.setPadding(new Insets(10));
        //Create separator
        Separator separator = new Separator();
        //Creates delete confirmation message
        Label message = new Label();
        message.setPadding(new Insets(10));
        //Create OK and Cancel buttons
        Button confirmButton = new Button("OK");
        confirmButton.setPrefWidth(80);
        Button removeCancelButton = new Button("Cancel");
        removeCancelButton.setPrefWidth(80);
        //Organizes elements
        HBox HButtons = new HBox(10, confirmButton, removeCancelButton);
        HButtons.setAlignment(Pos.CENTER_RIGHT);
        HButtons.setPadding(new Insets(10));
        VBox VDeleteBottom = new VBox(message, HButtons);
        VBox VDeleteMain = new VBox(10, removeTitle, separator, VDeleteBottom);
        VDeleteMain.setAlignment(Pos.BOTTOM_LEFT);
        Scene removeScene = new Scene(VDeleteMain, 400, 150);
        Stage removeStage = new Stage();
        removeStage.setScene(removeScene);
        removeButton.setOnAction(event -> {
            try {
                int i = PartsTable2.getSelectionModel().getSelectedItem().getId();
                message.setText("Do you want to delete this part?");
                removeStage.show();
                exceptionError.setText("");
            } catch (Exception g) {
                exceptionError.setText("Please select a part to delete.      ");
            }
            confirmButton.setOnAction(e -> {
                Part selectedPart = PartsTable2.getSelectionModel().getSelectedItem();
                product1.deleteAssociatedPart(selectedPart);
                removeStage.close();});
            //calls deletePart method from Inventory
        });

        //confirm delete
        removeCancelButton.setOnAction(event -> {
            removeStage.close();});
        saveButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){

                /*Removes error messages if entered formats are correct*/
                exceptionError.setText("");
                exceptionErrors.setText("");
                exceptionText = ("");
                int max = 0;
                int min = 0;
                int inv = 0;
                boolean minMaxValid = false;
                int machineid = 0;
                int validCheck = 0;

                /*Boolean variable checks if inventory input value is an integer*/
                boolean invIsValid = false;
                /*Boolean variables check if Min and Max input values are*/
                boolean minInput = false;
                boolean maxInput = false;

                /*Make error messages appear when incorrect format is entered*/
                try {
                    int i = nameField.getText().length();
                    int j = 1/i;
                    product1.setName(nameField.getText());
                    validCheck++;
                } catch (ArithmeticException f){
                    exceptionError.setText("Exception:");
                    exceptionText = ("No data in name field.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    inv = Integer.parseInt(invField.getText());
                    product1.setStock(inv);
                    invIsValid = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Inventory is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    invIsValid = false;
                    validCheck = 0;
                }

                try {
                    double d = Double.parseDouble(pcField.getText());
                    product1.setPrice(d);
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Price is not a double.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                try {
                    max = Integer.parseInt(maxField.getText());
                    product1.setMax(max);
                    maxInput = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Max is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    maxInput = false;
                    validCheck = 0;
                }

                try {
                    min = Integer.parseInt(minField.getText());
                    product1.setMin(min);
                    minInput = true;
                    validCheck++;
                } catch (NumberFormatException f){
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min is not an integer.\n");
                    exceptionErrors.setText(exceptionText);
                    minInput = false;
                    validCheck = 0;
                }

                if (min > max && minInput && maxInput) {
                    exceptionError.setText("Exception:");
                    exceptionText = (exceptionText + "Min must be less than Max.\n");
                    exceptionErrors.setText(exceptionText);
                    validCheck = 0;
                }

                if (min <= max && invIsValid) {
                    if (inv < min || inv > max) {
                        exceptionError.setText("Exception:");
                        exceptionText = (exceptionText + "Inv must be between Min and Max.\n");
                        exceptionErrors.setText(exceptionText);
                        validCheck = 0;
                    }
                }

                /*Halts program if name field is blank*/
                if (nameField.getLength() == 0) {
                    validCheck = 0;
                }

                if (validCheck == 5){
                    product1.setId(id);
                    int index = ProductsTable.getSelectionModel().getSelectedIndex();
                    Inventory.updateProduct(index, product1);
                    stage.close();
                    isWindowOpen = false;
                    partError.setText("");
                    productError.setText("");
                }
            }
        });
        cancelButton.setOnAction(event -> {
            stage.close();
            isWindowOpen = false;
            partError.setText("");
            productError.setText("");
        });
    }

    /**
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args); //Launches the application
    }
}
