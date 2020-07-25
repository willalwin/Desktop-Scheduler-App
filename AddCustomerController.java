/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Address;
import Model.City;
import Model.Country;
import Model.Customer;
import Model.DBConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author WillA
 */
public class AddCustomerController implements Initializable {
    ObservableList<City> cities = FXCollections.observableArrayList();
    ObservableList<Country> countries = FXCollections.observableArrayList();
    private Address address;
    private Customer customer;
    Stage stage;
    Parent scene;
    @FXML
    private Label addCustomerLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField postalCodeTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private ComboBox<String> cityComboBox;
    
    @FXML
    private TextField countryTextField;

    @FXML
    private Label errorMsgLabel;

    @FXML
    private Button saveCustomerButton;

    @FXML
    private Button cancelButton;

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/CustomerTableView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionSaveCustomer(ActionEvent event) {
        if(validateCustomer()){
            try{ 
                updateAddress();
                if(customer != null){//we need to edit an existing appointment
                    customer.setCustomerName(nameTextField.getText());
                    customer.updateCustomerInDB();
                }
                else{//create new appointment
                    loadAddress();
                    customer = new Customer(address.getAddressId(), nameTextField.getText());
                    customer.insertIntoDB();
                }
                //set new scene and load
                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/View_Controller/CustomerTableView.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
            catch(Exception e){
                errorMsgLabel.setText(e.getMessage());
            }            
        }
    } 
    
    @FXML
    void onCityChangedUpdateCountry(ActionEvent event) {
        //if city combobox changed check cities countryId against all countries and update coutnry combobox accordingly using lambda expressions to compress loops
        cities.stream().filter((city) -> (city.getCity().equals(cityComboBox.getValue()))).forEachOrdered((city) -> {
            countries.stream().filter((country) -> (city.getCountryId() == country.getCountryId())).forEachOrdered((country) -> {
                countryTextField.setText(country.getCountry());
            });
        });
    }
    
    void preloadData(Customer customer) throws ClassNotFoundException {
        addCustomerLabel.setText("Edit Customer");
        //create local appt 
        this.customer = customer;
            loadAddress();
        //load imcoming values for customer/user
        nameTextField.setText(this.customer.getCustomerName());
        addressTextField.setText(address.getAddress());
        postalCodeTextField.setText(address.getPostalCode());
        phoneNumberTextField.setText(address.getPhone());
        //fill city and country name by checking address' cityId, and cities countryId, also using lambda expressions in order to compress loops
        cities.stream().filter((city) -> (address.getCityId() == city.getCityId())).map((city) -> {
            cityComboBox.setValue(city.getCity());
            return city;
        }).forEachOrdered((city) -> {
            countries.stream().filter((country) -> (city.getCountryId() == country.getCountryId())).forEachOrdered((country) -> {
                countryTextField.setText(country.getCountry());
            });
        });
        
        
    }
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            loadCities();
            loadCountries();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        } 
        //populate values for custumer/user combo boxes
        getAllCities().forEach((city) -> {
            cityComboBox.getItems().add(city.getCity());
        });

//        getAllCountries().forEach((country) -> {
//            countryComboBox.getItems().add(country.getCountry());
//        });
        errorMsgLabel.setText("");
    }    

    private void loadAddress() throws ClassNotFoundException{
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try{
            if(customer != null){
                //connect to db
                DBConnection.makeConnection();
                //create a query string with ? used instead of the values given by the user
                String sql = "SELECT * FROM U06dWx.address WHERE addressId = ?";
                //prepare statement
                ps = DBConnection.getConn().prepareStatement(sql);
                //bind the addressId to the ?
                ps.setInt(1, customer.getAddressId());
            }
            else{
                //connect to db
                DBConnection.makeConnection();
                //create a query string with ? used instead of the values given by the user
                String sql = "SELECT * FROM U06dWx.address WHERE address = ?";
                //prepare statement
                ps = DBConnection.getConn().prepareStatement(sql);
                //bind the addressId to the ?
                ps.setString(1, addressTextField.getText());
            }
            //execute query
            resultSet = ps.executeQuery();
            Address newAddress = null;
            while(resultSet.next()){
                newAddress = new Address(resultSet.getInt("addressId"),
                                      resultSet.getInt("cityId"),
                                      resultSet.getString("address"),
                                      resultSet.getString("address2"),
                                      resultSet.getString("postalCode"),        
                                      resultSet.getString("phone"),
                                      resultSet.getString("createdBy"),
                                      resultSet.getString("lastUpdateBy"),
                                      resultSet.getDate("createDate").toLocalDate(),
                                      resultSet.getDate("lastUpdate").toLocalDate());    
                address = newAddress; 
            }
        }    
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    private void loadCities() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a statement object
            statement = DBConnection.getConn().createStatement();
            //create the sql query
            resultSet = statement.executeQuery("SELECT * FROM U06dWx.city");
               
            while(resultSet.next()){
                //int cityId, int countryId, String city, String createdBy, String lastUpdateBy, LocalDate createDate, LocalDate lastUpdate
                City newCity = new City(resultSet.getInt("cityId"),
                                        resultSet.getInt("countryId"),
                                        resultSet.getString("city"),
                                        resultSet.getString("createdBy"),
                                        resultSet.getString("lastUpdateBy"),
                                        resultSet.getDate("createDate").toLocalDate(),
                                        resultSet.getDate("lastUpdate").toLocalDate());
                cities.add(newCity);
            }    
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

    private void loadCountries() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a statement object
            statement = DBConnection.getConn().createStatement();
            //create the sql query
            resultSet = statement.executeQuery("SELECT * FROM U06dWx.country");
               
            while(resultSet.next()){
                Country newCountry = new Country(resultSet.getInt("countryId"),
                                                 resultSet.getString("country"),
                                                 resultSet.getString("createdBy"),
                                                 resultSet.getString("lastUpdateBy"),
                                                 resultSet.getDate("createDate").toLocalDate(),
                                                 resultSet.getDate("lastUpdate").toLocalDate());
                countries.add(newCountry);
            }    
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

    private ObservableList<City> getAllCities() {
        return cities;
    }

    private ObservableList<Country> getAllCountries() {
        return countries;
    }

    private boolean validateCustomer() {
        //error checking for empty fields
        if(nameTextField.getText().isEmpty()){
            errorMsgLabel.setText("Name field must be populated.");
            return false;
        }
        if(phoneNumberTextField.getText().isEmpty()){
            errorMsgLabel.setText("Phone number field must be populated.");
            return false;
        }
        //restricting phone numbers to numbers and symbols
        if(Pattern.matches("[a-zA-Z]+", phoneNumberTextField.getText()) == true){
            errorMsgLabel.setText("Phone number field must contain only numbers and symbols i.e.(no alphabetic characters).");
            return false;
        }
        if(addressTextField.getText().isEmpty()){
            errorMsgLabel.setText("Address field must be populated.");
            return false;
        } 
        if(postalCodeTextField.getText().isEmpty()){
            errorMsgLabel.setText("Postal code field must be populated.");
            return false;
        }  
        if(countryTextField.getText().isEmpty()){
            errorMsgLabel.setText("City and country fields must be populated.");
            return false;
        }
        else
            return true;
    }

    private void updateAddress() throws SQLException {
        int cityID = 0;
        for(City city: cities){
            if(city.getCity().equals(cityComboBox.getValue())){
                cityID = city.getCityId();
            }
        } 
        if(address != null){
            if(customer.getAddressId() == address.getAddressId()){
                address.setCityId(cityID);
                address.setAddress(addressTextField.getText());
                address.setPostalCode(postalCodeTextField.getText());
                address.setPhone(phoneNumberTextField.getText());  
            }
            address.updateAddressInDB();
        }
        else{
            address = new Address(cityID, addressTextField.getText(), postalCodeTextField.getText(), phoneNumberTextField.getText());
            address.insertIntoDB();
        }
    }
}
