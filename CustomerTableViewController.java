/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Customer;
import Model.DBConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author WillA
 */
public class CustomerTableViewController implements Initializable {
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    Stage stage;
    Parent scene;
    @FXML
    private TableView<Customer> customerTableview;

    @FXML
    private TableColumn<?, ?> customerIDField;

    @FXML
    private TableColumn<?, ?> customerNameField;

    @FXML
    private TableColumn<?, ?> customerAddressField;

     @FXML
    private TableColumn<?, ?> customerActiveField;

    @FXML
    private TableColumn<?, ?> customerCreateDateField;

    @FXML
    private Label customerListLabel;

    @FXML
    private Button addCustomerButton;

    @FXML
    private Button updateCustomerButton;

    @FXML
    private Button deleteCustomerButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button logoutButton;

    @FXML
    void onActionAddCustomer(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/AddCustomer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionDeleteCustomer(ActionEvent event) throws SQLException {
        //get selected appt and call remove method, then reload tableview with new appt list
        if(!customerTableview.getSelectionModel().isEmpty()){
            Customer customer = this.customerTableview.getSelectionModel().getSelectedItem();
            customer.removeCustomerFromDB();
            this.customers.remove(customer);
            customerTableview.setItems(customers);
            customerTableview.refresh();   
        }
    }

    @FXML
    void onActionLogout(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionOpenCalendarScreen(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/ApptCalendar.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionUpdateCustomer(ActionEvent event) throws IOException, ClassNotFoundException {
        //set new scene and load, but also call preloadData function for sending appt info, as well as changing the scene name to fit new usage
        if(!customerTableview.getSelectionModel().isEmpty()){
            try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View_Controller/AddCustomer.fxml"));
            loader.load();
            //loads controller for modify part scene and calls intitializer method
            AddCustomerController acc  = loader.getController();
            acc.preloadData(customerTableview.getSelectionModel().getSelectedItem());
            //loads scene
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = loader.getRoot();
            //change title from add appt to edit appt
            stage.setTitle("Edit Customer");
            stage.setScene(new Scene(scene));
            stage.show();
            }
            catch(NullPointerException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void loadCustomers() throws SQLException{
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a statement object
            statement = DBConnection.getConn().createStatement();            
            //create the sql query
            resultSet = statement.executeQuery("SELECT * FROM U06dWx.customer");
            while(resultSet.next()){  
                //int customerId, int addressId, int active, String customerName, LocalDate createDate, String createdBy, LocalDate lastUpdate, String lastUpdateBy
                Customer newCustomer = new Customer(resultSet.getInt("customerId"),
                                                             resultSet.getInt("addressId"),
                                                             resultSet.getInt("active"),        
                                                             resultSet.getString("customerName"),
                                                             resultSet.getDate("createDate").toLocalDate(),
                                                             resultSet.getString("createdBy"),
                                                             resultSet.getDate("lastUpdate").toLocalDate(),
                                                             resultSet.getString("lastUpdateBy"));
                customers.add(newCustomer);
            }
            customerTableview.getItems().addAll(customers);
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        finally{
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
            if(statement != null)
                statement.close();
            if(resultSet != null)
                resultSet.close();
        }
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set values for tableview
        customerIDField.setCellValueFactory(new PropertyValueFactory<> ("customerId"));
        customerNameField.setCellValueFactory(new PropertyValueFactory<> ("customerName"));
        customerAddressField.setCellValueFactory(new PropertyValueFactory<> ("addressId"));
        customerActiveField.setCellValueFactory(new PropertyValueFactory<> ("active"));
        customerCreateDateField.setCellValueFactory(new PropertyValueFactory<> ("createDate"));
        //load customers from DB using loadCustomers() method
        try {
            loadCustomers();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerTableViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
