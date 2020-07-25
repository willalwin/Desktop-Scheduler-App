/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.DBConnection;
import Model.User;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author WillA
 */
public class LoginScreenController implements Initializable {

    Stage stage;
    Parent scene;
    Locale myLocale = Locale.getDefault();
    String fileName = "src/Files/userLog.txt", userLogText;    
    @FXML
    private Label desktopSchedulerLabel;

    @FXML
    private PasswordField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button enterButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label loginLabel;
    
    @FXML
    private Label errorMsgLabel;

    @FXML
    void onActionEnterScheduler(ActionEvent event) throws IOException, ClassNotFoundException {
        //query the database with the username provided, and get password stored in the database
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String userName = usernameTextField.getText();

        try{
            //connect to db
            DBConnection.makeConnection();
            //create a query string with ? used instead of the values given by the user
            String sql = "SELECT * FROM user WHERE userName = ?";
            //prepare statement
            ps = DBConnection.getConn().prepareStatement(sql);
            //bind the userName to the ?
            ps.setString(1, userName);
            //execute query
            resultSet = ps.executeQuery();
            //extract password from the db
            String dbPassword = null;

            User user = null;
            while(resultSet.next()){
                dbPassword = resultSet.getString("password");

                user = new User(resultSet.getInt("userId"),
                                resultSet.getInt("active"),
                                resultSet.getString("userName"),
                                resultSet.getString("password"),
                                resultSet.getDate("createDate").toLocalDate(),        
                                resultSet.getString("createdBy"),
                                resultSet.getDate("lastUpdate").toLocalDate(),
                                resultSet.getString("lastUpdateBy"));
            }
            
            if(passwordTextField.getText().equals(dbPassword)){
                try{
                    DBConnection.setLoggedInUser(user);
                    Calendar myCalendar = Calendar.getInstance();
                    checkForUpcomingAppts();
                    FileWriter fWriter = new FileWriter(fileName, true);
                    PrintWriter outputFile = new PrintWriter(fWriter);
                    userLogText = "User ID: " + user.getUserId() + "\nLogged in: " + myCalendar.getTime();
                    outputFile.println(userLogText);
                    outputFile.close();
                     //set new scene and load   
                    stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/View_Controller/ApptCalendar.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                } 
                catch(IOException e){
                    System.out.println("File not found!");
                }
            }            
            else{
                //if user regional language is spanish display spanish text
                if(myLocale.getDisplayLanguage().equals("español")){
                errorMsgLabel.setText("Nombre de usuario y contraseña no coinciden.");
                }
                //else display in english
                else{
                    errorMsgLabel.setText("User name and password do not match.");
                }
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
    }

    private void checkForUpcomingAppts() throws ClassNotFoundException {
        //query the database with the userid, and get associated appts
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        Time time1 = Time.valueOf(LocalTime.now());
        LocalDate date = LocalDate.now();

        try{
            //connect to db
            DBConnection.makeConnection();
            //create a query string with ? used instead of the values given by the user
            String sql = "SELECT start FROM U06dWx.appointment WHERE userId = ?";
            //prepare statement
            ps = DBConnection.getConn().prepareStatement(sql);
            //bind the volunteerID to the ?
            ps.setInt(1, DBConnection.getLoggedInUser().getUserId());
            //execute query
            resultSet = ps.executeQuery();
            //extract start times from appts
            while(resultSet.next()){
                //parsing db values for appt checking
                String start = resultSet.getString("start");
                int year = Integer.parseInt(start.substring(0, 4));
                int month = Integer.parseInt(start.substring(5, 7));
                int day = Integer.parseInt(start.substring(8, 10));
                int startHour = Integer.parseInt(start.substring(11, 13));
                int startMinute = Integer.parseInt(start.substring(14, 16));
                int startSecond = Integer.parseInt(start.substring(17, 19)); 
                //set ZDT with incoming values and specify UTC time
                ZonedDateTime startZdt = ZonedDateTime.of(year, month, day, startHour, startMinute, startSecond, 0, ZoneId.of("UTC")); 
                //change ZDT to local zone using DB values and system default zone
                ZonedDateTime startLocalZdt = startZdt.withZoneSameInstant(ZoneOffset.systemDefault());
                Time time2 = Time.valueOf(startLocalZdt.toLocalTime());
                LocalDate date2 = startLocalZdt.toLocalDate();
                if(date.equals(date2)){
                    if(time1.getTime() <= time2.getTime() && time2.getTime() < (time1.getTime() + 900000)){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have an appt scheduled in the next 15 minutes", ButtonType.OK);
                    alert.showAndWait();
                    }  
                }                    
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //check for Spanish language locale and update text
        if(myLocale.getDisplayLanguage().equals("español")){
            desktopSchedulerLabel.setText("Programador de escritorio");
            loginLabel.setText("Iniciar sesión");
            usernameLabel.setText("Nombre de usuario");
            passwordLabel.setText("Contraseña");
            enterButton.setText("Entrar");
        }
        errorMsgLabel.setText("");
    }        
}
