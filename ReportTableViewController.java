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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author WillA
 */
public class ReportTableViewController implements Initializable {
    ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    ObservableList<User> users = FXCollections.observableArrayList();
    List<String> apptsPerMonth = new ArrayList<>();    
    int trigger = 0;
    Stage stage;
    Parent scene;
    @FXML
    private Button apptsPerMonthButton;

    @FXML
    private Button ViewConsultantButton;

    @FXML
    private Button ApptsByCustomerButton;

    @FXML
    private Label comboBoxLabel;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TableView<Appointment> reportTableView;
    
    @FXML
    private TableColumn<?, ?> nameMonthColumn;

    @FXML
    private TableColumn<?, ?> dateCountColumn;

    @FXML
    private TableColumn<?, ?> startColumn;

    @FXML
    private TableColumn<?, ?> endColumn;

    @FXML
    private TableColumn<?, ?> typeColumn;
    
    @FXML
    private TextArea textArea;
    
    @FXML
    void onActionExit(ActionEvent event) throws IOException {
        //set scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/ApptCalendar.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionUpdateTableViewComboBox(ActionEvent event) {
        reportTableView.getItems().clear();
        if(trigger == 1){
            for(User user: users){
                if(user.getUserName().equals(comboBox.getValue())){
                    for(Appointment appt: appointments){
                        if(appt.getUserId() == user.getUserId())
                            reportTableView.getItems().add(appt);
                    }        
                }
            }   
        }
        else{
            for(Customer customer: customers){
                if(customer.getCustomerName().equals(comboBox.getValue())){
                    for(Appointment appt: appointments){
                        if(appt.getCustomerId() == customer.getCustomerId())
                            reportTableView.getItems().add(appt);
                    }        
                }
            }  
        }
    }

    @FXML
    void onActionViewApptsByCustomer(ActionEvent event) {
        textArea.setVisible(false);
        reportTableView.setVisible(true);
        textArea.clear();
        trigger = -1;
        comboBox.getItems().clear();
        nameMonthColumn.setText("Consultant ID");
        dateCountColumn.setText("Date");
        startColumn.setText("Start Time");
        endColumn.setText("End Time");
        typeColumn.setText("Type");
        nameMonthColumn.setCellValueFactory(new PropertyValueFactory<> ("userId"));
        dateCountColumn.setCellValueFactory(new PropertyValueFactory<> ("startDate"));
        startColumn.setCellValueFactory(new PropertyValueFactory<> ("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<> ("endTime"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<> ("type"));
        comboBoxLabel.setText("Customer");
        comboBox.setDisable(false);
        //lambda expression on for loop to condense code
        customers.forEach((customer) -> {comboBox.getItems().add(customer.getCustomerName());});
    }

    @FXML
    void onActionViewApptsPerMonth(ActionEvent event) {
        reportTableView.setVisible(false);
        textArea.setVisible(true);
        textArea.setText("---Appointment Types Per Month---\n\n");
        int scrum = 0, presentation = 0;
        comboBox.getItems().clear();
        comboBox.setDisable(true);
      
        for(int i = 0; i < 12; i++){
            for(Appointment appt: appointments){
                if(appt.getMyCalendar().get(Calendar.MONTH) == i){
                    if(appt.getType().equalsIgnoreCase("Scrum"))
                        scrum++;
                    if(appt.getType().equalsIgnoreCase("Presentation"))
                        presentation++;
                }
            }
            textArea.appendText("Month: " + (i + 1) +  "\nScrum: " + scrum + "\n" + "Presentation: " + presentation + "\n\n");
            scrum = 0;
            presentation = 0;
        }
        
    }

    @FXML
    void onActionViewConsultantSchedule(ActionEvent event) {
        textArea.setVisible(false);
        reportTableView.setVisible(true);
        textArea.clear();
        trigger = 1;
        comboBox.getItems().clear();
        nameMonthColumn.setText("Customer ID");
        dateCountColumn.setText("Date");
        startColumn.setText("Start Time");
        endColumn.setText("End Time");
        typeColumn.setText("Type");
        nameMonthColumn.setCellValueFactory(new PropertyValueFactory<> ("customerId"));
        dateCountColumn.setCellValueFactory(new PropertyValueFactory<> ("startDate"));
        startColumn.setCellValueFactory(new PropertyValueFactory<> ("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<> ("endTime"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<> ("type"));
        comboBoxLabel.setText("Consultant");
        comboBox.setDisable(false);
        for(User user: users){
            comboBox.getItems().add(user.getUserName());
        }
    }
    
    public void getAppointments() throws SQLException{
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a statement object
            statement = DBConnection.getConn().createStatement();            
            //create the sql query
            resultSet = statement.executeQuery("SELECT * FROM U06dWx.appointment");
            while(resultSet.next()){  
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
                //set ZDT with incoming values and specify UTC time
                ZonedDateTime startZdt = ZonedDateTime.of(year, month, day, startHour, startMinute, startSecond, 0, ZoneId.of("UTC"));
                ZonedDateTime endZdt = ZonedDateTime.of(year, month, day, endHour, endMinute, endSecond, 0, ZoneId.of("UTC"));
                //change ZDT to local zone using DB values and system default zone
                ZonedDateTime startLocalZdt = startZdt.withZoneSameInstant(ZoneOffset.systemDefault());
                ZonedDateTime endLocalZdt = endZdt.withZoneSameInstant(ZoneOffset.systemDefault());
                Appointment newAppointment = new Appointment(resultSet.getInt("appointmentId"),
                                                             resultSet.getInt("customerId"),
                                                             resultSet.getInt("userId"),        
                                                             resultSet.getString("title"),
                                                             resultSet.getString("description"),
                                                             resultSet.getString("location"),
                                                             resultSet.getString("contact"),
                                                             resultSet.getString("type"),
                                                             resultSet.getString("url"),
                                                             startLocalZdt.toLocalDate(),
                                                             endLocalZdt.toLocalDate(),
                                                             startLocalZdt.toLocalTime(),
                                                             endLocalZdt.toLocalTime(),
                                                             resultSet.getDate("createDate").toLocalDate(),
                                                             resultSet.getString("createdBy"),
                                                             resultSet.getDate("lastUpdate").toLocalDate(),
                                                             resultSet.getString("lastUpdateBy"));
                //creating Calendar from appt start values              
                newAppointment.setMyCalendar(year, month - 1, day, startHour, startMinute, startSecond);
                appointments.add(newAppointment);
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
    
    private void getCustomers() throws SQLException {
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

    private void getUsers() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a statement object
            statement = DBConnection.getConn().createStatement();
            //create the sql query
            resultSet = statement.executeQuery("SELECT * FROM U06dWx.user");  
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
        reportTableView.setVisible(false);
        textArea.setEditable(false);
        textArea.setVisible(false);
        comboBox.setDisable(true);
        comboBoxLabel.setText("");
        try {
            getAppointments();
            getCustomers();
            getUsers();
        } catch (SQLException ex) {
            Logger.getLogger(ReportTableViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
}
