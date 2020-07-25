/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.DBConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author WillA
 */
public class ApptCalendarController implements Initializable {

    ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    Calendar apptCalendar = Calendar.getInstance();
    String customerName, userName;
    int toggle = 0;
    Stage stage;
    Parent scene;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private TableView<Appointment> calendarTableView;

    @FXML
    private TableColumn<?, ?> apptDateField;

    @FXML
    private TableColumn<?, ?> apptTimeField;

    @FXML
    private TableColumn<?, ?> customerNameField;

    @FXML
    private TableColumn<?, ?> consultantField;
    
    @FXML
    private Button addApptButton;

    @FXML
    private Button editApptButton;
    
    @FXML
    private Button deleteApptButton;    

    @FXML
    private Button customerTableviewButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button generateReportButton;

    @FXML
    void onActionAddAppt(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionBack(ActionEvent event) {
        //clear calendar view for population with specific appts
        calendarTableView.getItems().clear();
        //check toggle value set when month/week button clicked 1 for month, -1 for week
        if(toggle == 1){
            //roll month back one month and check if any appt match month value
            apptCalendar.roll(Calendar.MONTH, false);
            //lambda expression that converts for loop and if statements into one line using available predefined methods
            appointments.stream().filter((appointment) -> (appointment.getMyCalendar().get(Calendar.MONTH) == (apptCalendar.get(Calendar.MONTH)))).forEachOrdered((appointment) -> {
                calendarTableView.getItems().add(appointment);
            });
        }    
        else{
            //roll week back one week and check if any appt match week value
            apptCalendar.roll(Calendar.WEEK_OF_YEAR, false);
            //lambda expression that converts for loop and if statements into one line using available predefined methods
            appointments.stream().filter((appointment) -> (appointment.getMyCalendar().get(Calendar.WEEK_OF_YEAR) == (apptCalendar.get(Calendar.WEEK_OF_YEAR)))).forEachOrdered((appointment) -> {
                calendarTableView.getItems().add(appointment);
            });            
        }
    }

    @FXML
    void onActionChangeSceneToCustomerTableView(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/CustomerTableView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionChangeSceneToReportTableView(ActionEvent event) throws IOException {
        //set new scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/ReportTableView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    
    @FXML
    void onActionDeleteAppt(ActionEvent event) throws SQLException {
        //get selected appt and call remove method, then reload tableview with new appt list
        if(!calendarTableView.getSelectionModel().isEmpty()){
            Appointment appt = this.calendarTableView.getSelectionModel().getSelectedItem();
            calendarTableView.getItems().remove(appt);
            appt.removeApptFromDB();
            appointments.remove(appt);
            calendarTableView.refresh(); 
        }
    }

    @FXML
    void onActionEditAppt(ActionEvent event) throws IOException {
        //set new scene and load, but also call preloadData function for sending appt info, as well as changing the scene name to fit new usage
        if(!calendarTableView.getSelectionModel().isEmpty()){
            try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View_Controller/AddAppointment.fxml"));
            loader.load();
            //loads controller for modify part scene and calls intitializer method
            AddAppointmentController aac  = loader.getController();
            aac.preloadData(calendarTableView.getSelectionModel().getSelectedItem());
            //loads scene
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = loader.getRoot();
            //change title from add appt to edit appt
            stage.setTitle("Edit Appointment");
            stage.setScene(new Scene(scene));
            stage.show();
            }
            catch(NullPointerException e){
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    void onActionLogout(ActionEvent event) throws IOException {
        //set scene and load
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionNext(ActionEvent event) {
        //clear calendar view for population with specific appts
        calendarTableView.getItems().clear();
        //check toggle value set when month/week button clicked 1 for month, -1 for week
        if(toggle == 1){
            //roll forward one month and check if any appt match month value
            apptCalendar.roll(Calendar.MONTH, true);
            //lambda expression that converts for loop and if statements into one line using available predefined methods
            appointments.stream().filter((appointment) -> (appointment.getMyCalendar().get(Calendar.MONTH) == (apptCalendar.get(Calendar.MONTH)))).forEachOrdered((appointment) -> {
                calendarTableView.getItems().add(appointment);
            });
        }    
        else{
            //roll forward one week and check if any appt match week value
            apptCalendar.roll(Calendar.WEEK_OF_YEAR, true);
            //lambda expression that converts for loop and if statements into one line using available predefined methods
            appointments.stream().filter((appointment) -> (appointment.getMyCalendar().get(Calendar.WEEK_OF_YEAR) == (apptCalendar.get(Calendar.WEEK_OF_YEAR)))).forEachOrdered((appointment) -> {
                calendarTableView.getItems().add(appointment);
            });            
        }
    }

    @FXML
    void onActionViewByMonth(ActionEvent event) throws SQLException {
        //enable back/next buttons
        nextButton.setDisable(false);
        backButton.setDisable(false);
        //set toggle value for use in back/next methods
        toggle = 1;
        //call appt load method
        loadApptsByMonth();
    }

    @FXML
    void onActionViewByWeek(ActionEvent event) throws SQLException {
        //enable back/next buttons
        nextButton.setDisable(false);
        backButton.setDisable(false);
        //set toggle value for use in back/next methods
        toggle = -1;
        //call appt load method
        loadApptsByWeek();
    } 
    
    public void loadAppointments() throws SQLException{
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
                //parsing start/end values from db to convert for time zone
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
            calendarTableView.getItems().addAll(appointments);
        }
        catch(ClassNotFoundException | NumberFormatException | SQLException e){
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

    public void loadApptsByMonth(){
        //clear calendar view for population with specific appts
        calendarTableView.getItems().clear();
        //lambda expression that converts for loop and if statements into one line using available predefined methods
        appointments.stream().filter((appointment) -> (appointment.getMyCalendar().get(Calendar.MONTH) == apptCalendar.get(Calendar.MONTH))).forEachOrdered((appointment) -> {
            calendarTableView.getItems().add(appointment);
        }); 
        calendarTableView.refresh();
    }
    
    private void loadApptsByWeek() {
        //clear calendar view for population with specific appts
        calendarTableView.getItems().clear();
        //lambda expression that converts for loop and if statements into one line using available predefined methods
        appointments.stream().filter((appointment) -> (appointment.getMyCalendar().get(Calendar.WEEK_OF_YEAR) == apptCalendar.get(Calendar.WEEK_OF_YEAR))).forEachOrdered((appointment) -> {
            calendarTableView.getItems().add(appointment);
        });
    }    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populate table view fields
        apptDateField.setCellValueFactory(new PropertyValueFactory<> ("startDate"));
        apptTimeField.setCellValueFactory(new PropertyValueFactory<> ("startTime"));
        customerNameField.setCellValueFactory(new PropertyValueFactory<> ("customerId"));
        consultantField.setCellValueFactory(new PropertyValueFactory<> ("userId"));
        backButton.setDisable(true);
        nextButton.setDisable(true);
        //load apppointments from db by calling method
        try{
            loadAppointments();
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
        
    }           
}
