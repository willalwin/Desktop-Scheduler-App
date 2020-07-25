/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.Customer;
import Model.DBConnection;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author WillA
 */
public class AddAppointmentController implements Initializable {
    
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    ObservableList<User> users = FXCollections.observableArrayList();
    private Appointment appointment;
    String startTime, endTime, startMinute, startHour, endMinute, endHour;
    LocalTime adjustedStartTime, adjustedEndTime, startResultTime, endResultTime;
    LocalDate resultDate;
    int apptId;
    boolean trigger = false;
    Stage stage;
    Parent scene;
    
    @FXML
    private Label addApptLabel;

    @FXML
    private DatePicker apptDatePicker;

    @FXML
    private Label errorMsgLabel;
    
    @FXML
    private ComboBox<String> consultantNameComboBox;

    @FXML
    private ComboBox<String> customerNameComboBox;

    @FXML
    private ComboBox<String> apptTypeComboBox;     

    @FXML
    private ComboBox<String> hourStartComboBox;

    @FXML
    private ComboBox<String> minuteStartComboBox;

    @FXML
    private ComboBox<String> hourEndComboBox;

    @FXML
    private ComboBox<String> minuteEndComboBox;   

    @FXML
    private Button saveApptButton;

    @FXML
    private Button cancelButton;

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/ApptCalendar.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionSaveAppt(ActionEvent event) throws IOException, SQLException {

        if(validateAppt()){
            try{ 
                if(appointment != null){//we need to edit an existing appointment
                    updateAppointment();
                    appointment.updateAppointmentInDB();
                }
                else{
                    int customerId = 0, userId = 0;
                    for(Customer customer: customers){
                        if(customer.getCustomerName().equals(customerNameComboBox.getValue()))
                            customerId = customer.getCustomerId();
                    }
                    for(User user: users){
                        if(user.getUserName().equals(consultantNameComboBox.getValue()))
                           userId = user.getUserId();
                    }
                    appointment = new Appointment(customerId, userId, apptTypeComboBox.getValue(), apptDatePicker.getValue(), apptDatePicker.getValue(), adjustedStartTime, adjustedEndTime);
                    appointment.insertIntoDB();
                } 
                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/View_Controller/ApptCalendar.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
            catch(Exception e){
                errorMsgLabel.setText(e.getMessage());
            }            
        }
    }
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //call methods to retrieve customers/users names from db
        try{
            getCustomerNames();
            getUserNames();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }            
        //populate values for custumer/user combo boxes
        getAllCustomers().forEach((customer) -> {
            customerNameComboBox.getItems().add(customer.getCustomerName());
        });

        getAllUsers().forEach((user) -> {
            consultantNameComboBox.getItems().add(user.getUserName());
        });
        errorMsgLabel.setText("");
        //set values for date/time combo boxes
        for (int i = 8; i < 17; i++) {
            hourStartComboBox.getItems().add(Integer.toString(i));
            hourEndComboBox.getItems().add(Integer.toString(i));
        }
        minuteStartComboBox.getItems().add("00");
        minuteStartComboBox.getItems().add("15");
        minuteStartComboBox.getItems().add("30");
        minuteStartComboBox.getItems().add("45");
        minuteEndComboBox.getItems().add("00");
        minuteEndComboBox.getItems().add("15");
        minuteEndComboBox.getItems().add("30");
        minuteEndComboBox.getItems().add("45");
        //set appointment types for type combo box
        apptTypeComboBox.getItems().add("Scrum");
        apptTypeComboBox.getItems().add("Presentation");
    }    

    void preloadData(Appointment appointment) {
        //create local appt 
        this.appointment = appointment;
        //load imcoming values for customer/user
        getAllCustomers().stream().filter((customer) -> (customer.getCustomerId() == this.appointment.getCustomerId())).forEachOrdered((customer) -> {
            customerNameComboBox.setValue(customer.getCustomerName());
        });
        
        getAllUsers().stream().filter((user) -> (user.getUserId() == this.appointment.getUserId())).forEachOrdered((user) -> {
            consultantNameComboBox.setValue(user.getUserName());
        });
        //change label for edit appt use
        addApptLabel.setText("Edit Appointment");
        //set values for predefined fields
        apptId = appointment.getAppointmentId();       
        apptTypeComboBox.setValue(appointment.getType());
        apptDatePicker.setValue(appointment.getStartDate());
        //convert db start and end times to combo box strings
        startTime = appointment.getStartTime().toString();
        if(startTime.startsWith("0"))
            startHour = startTime.substring(1, 2);
        else
            startHour = startTime.substring(0, 2);
        startMinute = startTime.substring(3, 5);
        hourStartComboBox.setValue(startHour);
        minuteStartComboBox.setValue(startMinute);
        
        endTime = appointment.getEndTime().toString();
        if(endTime.startsWith("0"))
            endHour = endTime.substring(1, 2);
        else
            endHour = endTime.substring(0, 2);
        endMinute = endTime.substring(3, 5);
        hourEndComboBox.setValue(endHour);
        minuteEndComboBox.setValue(endMinute);
        //set predefined times in combo boxes
        hourStartComboBox.getSelectionModel().select(startHour);
        hourEndComboBox.getSelectionModel().select(endHour);
        minuteStartComboBox.getSelectionModel().select(startMinute);
        minuteEndComboBox.getSelectionModel().select(endMinute);       
    }

    private void updateAppointment() {
        appointment.setStartDate(apptDatePicker.getValue());
        appointment.setEndDate(apptDatePicker.getValue());
        appointment.setStartTime(adjustedStartTime);
        appointment.setEndTime(adjustedEndTime);
        appointment.setType(apptTypeComboBox.getValue());
        appointment.setLastUpdateBy(DBConnection.getLoggedInUser().getUserName());       
    }

    private boolean validateAppt() throws SQLException {
        
            //error checking for field population
            if(customerNameComboBox.getValue() == null){
                errorMsgLabel.setText("Customer name field must be populated.");
                return false;
            }
            if(consultantNameComboBox.getSelectionModel().isEmpty()){
                errorMsgLabel.setText("Consultant name field must be populated.");
                return false;
            } 
            if(apptDatePicker.getValue() == null){
                errorMsgLabel.setText("You must pick a date.");
                return false;
            }
            //error checking for appts scheduled in the past
            if(apptDatePicker.getValue().isBefore(LocalDate.now())){
                errorMsgLabel.setText("Appointment cannot be in the past.");
                return false;
            }
            if(apptTypeComboBox.getSelectionModel().isEmpty()){
                errorMsgLabel.setText("Appointment type field must be populated.");
                return false;
            }        
            if(hourStartComboBox.getSelectionModel().isEmpty() || minuteStartComboBox.getSelectionModel().isEmpty() || hourEndComboBox.getSelectionModel().isEmpty() || minuteEndComboBox.getSelectionModel().isEmpty()){
                errorMsgLabel.setText("All time fields must be populated.");
                return false;
            }
            
            //create variables with values from combo boxes
            adjustedStartTime = LocalTime.of(Integer.parseInt(hourStartComboBox.getValue()), Integer.parseInt(minuteStartComboBox.getValue()));
            adjustedEndTime = LocalTime.of(Integer.parseInt(hourEndComboBox.getValue()), Integer.parseInt(minuteEndComboBox.getValue()));
            //error checking for appts with invalid start/end times
            if(!adjustedStartTime.isBefore(adjustedEndTime) || adjustedStartTime.equals(adjustedEndTime)){
                errorMsgLabel.setText("Appointment end time must be later than start time.");
                return false;            
            }
            //checking database for overlapping appts
            else{
                Connection conn = null;
                Statement statement = null;
                ResultSet resultSet = null;
                try{
                    //connect to DB
                    conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U06dWx", "U06dWx", "53688737209");
                    //create a statement object
                    statement = conn.createStatement();
                    //create the sql query
                    resultSet = statement.executeQuery("SELECT appointmentId, start, end, customerId, userId FROM appointment");

                    while(resultSet.next()){
                        //variables to hold db values
                        String start = resultSet.getString("start");
                        String end = resultSet.getString("end");
                        int year = Integer.parseInt(start.substring(0, 4));
                        int month = Integer.parseInt(start.substring(5, 7));
                        int day = Integer.parseInt(start.substring(8, 10));
                        int startHour = Integer.parseInt(start.substring(11, 13));
                        int startMinute = Integer.parseInt(start.substring(14, 16));
                        int startSecond = Integer.parseInt(start.substring(17, 19)); 
                        int endHour = Integer.parseInt(end.substring(11, 13));
                        int endMinute = Integer.parseInt(end.substring(14, 16));
                        int endSecond = Integer.parseInt(end.substring(17, 19));
                        ZonedDateTime startZdt = ZonedDateTime.of(year, month, day, startHour, startMinute, startSecond, 0, ZoneId.of("UTC"));
                        ZonedDateTime startLocalZdt = startZdt.withZoneSameInstant(ZoneOffset.systemDefault());
                        ZonedDateTime endZdt = ZonedDateTime.of(year, month, day, endHour, endMinute, endSecond, 0, ZoneId.of("UTC"));
                        ZonedDateTime endLocalZdt = endZdt.withZoneSameInstant(ZoneOffset.systemDefault());
                        resultDate = startLocalZdt.toLocalDate();
                        startResultTime = startLocalZdt.toLocalTime();
                        endResultTime = endLocalZdt.toLocalTime();
                        //checking for appts on the same date as new appt
                        if(apptDatePicker.getValue().isEqual(resultDate)){
                            //validating appt is not scheduled at same tome as existing appt, unless the apptID matches
                            if(apptId != resultSet.getInt("appointmentId") && (adjustedStartTime.equals(startResultTime) || (adjustedStartTime.isBefore(startResultTime) && adjustedEndTime.isAfter(startResultTime)) || (adjustedStartTime.isAfter(startResultTime) && adjustedStartTime.isBefore(endResultTime)))){
                                errorMsgLabel.setText("Appointment conflicts with another appt on this date.");
                                return false;
                            }                            
                        }  
                    }

                }
                catch(Exception e){
                    System.err.println(e.getMessage());
                }
                finally{
                    if(conn != null)
                        conn.close();
                    if(statement != null)
                        statement.close();
                    if(resultSet != null)
                        resultSet.close();
                }            
            }
        return true;
    }

    private void getCustomerNames() throws SQLException {
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
        }
        catch(ClassNotFoundException | SQLException e){
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

    private void getUserNames() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a statement object
            statement = DBConnection.getConn().createStatement();
            //create the sql query
            resultSet = statement.executeQuery("SELECT * FROM U06dWx.user");
             //int userId, int active, String userName, String password, LocalDate createDate, String createdBy, LocalDate lastUpdate, String lastUpdateBy  
            while(resultSet.next()){
                User newUser = new User(resultSet.getInt("userId"),
                                        resultSet.getInt("active"),
                                        resultSet.getString("userName"),
                                        resultSet.getString("password"),
                                        resultSet.getDate("createDate").toLocalDate(),
                                        resultSet.getString("createdBy"),
                                        resultSet.getDate("lastUpdate").toLocalDate(),
                                        resultSet.getString("lastUpdateBy"));
                users.add(newUser);
            }    
        }
        catch(ClassNotFoundException | SQLException e){
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
    
    public ObservableList<Customer> getAllCustomers(){
        return customers;
    }
    
    public ObservableList<User> getAllUsers(){
        return users;
    }
}
