/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author WillA
 */
public class Appointment {
    private int appointmentId,
                customerId,
                userId;
    private String title,
                   description,
                   location,
                   contact,
                   type,
                   url,
                   createdBy,                 
                   lastUpdateBy;      
    private LocalDate createDate,
                      lastUpdate,
                      startDate,
                      endDate;
    private LocalTime startTime,
                     endTime;
    
    private Calendar myCalendar = Calendar.getInstance();
   
    //constructor for appts created in gui to be sent to DB 
    public Appointment(int customerId, int userId, String type, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.customerId = customerId;
        this.userId = userId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    

    //constructor for appts pulled from DB, appointmentId included
    public Appointment(int appointmentId, int customerId, int userId, String title, String description, String location, String contact, String type, String url, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, LocalDate createDate, String createdBy, LocalDate lastUpdate, String lastUpdateBy) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public void insertIntoDB() throws SQLException {
        //variables for dateTime conversion
        String stDate = startDate.toString();
        String stTime = startTime.toString();
        String eDate = endDate.toString();
        String eTime = endTime.toString();
        String start = stDate + " " + stTime;
        String end = eDate + " " + eTime;
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());
        String dateTime  = date + " " + time;
        //new values
        //gets UTC dateTime from DB and converts to localDateTime
        LocalDateTime startLdt = LocalDateTime.of(startDate, startTime);
        //converts ldt to ZonedDateTime
        ZonedDateTime startZdt = ZonedDateTime.of(startLdt, ZoneId.systemDefault());
        //gets UTC time from zdt
        ZonedDateTime startUtcZdt = startZdt.withZoneSameInstant(ZoneOffset.UTC);
        //gets UTC dateTime from DB and converts to localDateTime
        LocalDateTime endLdt = LocalDateTime.of(endDate, endTime);
        //converts ldt to ZonedDateTime
        ZonedDateTime endZdt = ZonedDateTime.of(endLdt, ZoneId.systemDefault());
        //gets UTC time from zdt
        ZonedDateTime endUtcZdt = endZdt.withZoneSameInstant(ZoneOffset.UTC);
        //formats zonedDateTime value
        DateTimeFormatter customFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        PreparedStatement preparedStatement = null;        
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "INSERT INTO U06dWx.appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the values to the parameters
            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, "not needed");
            preparedStatement.setString(4, "not needed");
            preparedStatement.setString(5, "not needed");
            preparedStatement.setString(6, "not needed");
            preparedStatement.setString(7, type);
            preparedStatement.setString(8, "not needed");
            preparedStatement.setString(9, customFormat.format(startUtcZdt));
            preparedStatement.setString(10, customFormat.format(endUtcZdt));
            preparedStatement.setString(11, dateTime);
            preparedStatement.setString(12, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setString(13, dateTime);
            preparedStatement.setString(14, DBConnection.getLoggedInUser().getUserName());
            
            preparedStatement.executeUpdate();   
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        finally{
            if(preparedStatement != null)
                preparedStatement.close();
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
        }
    }    
   
    public void updateAppointmentInDB() throws SQLException {               
        //variables for dateTime conversion
        String stDate = startDate.toString();
        String stTime = startTime.toString();
        String eDate = endDate.toString();
        String eTime = endTime.toString();
        String start = stDate + " " + stTime;
        String end = eDate + " " + eTime; 
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());
        String dateTime  = date + " " + time;
        //new values
        //gets UTC dateTime from DB and converts to localDateTime
        LocalDateTime startLdt = LocalDateTime.of(startDate, startTime);
        //converts ldt to ZonedDateTime
        ZonedDateTime startZdt = ZonedDateTime.of(startLdt, ZoneId.systemDefault());
        //gets UTC time from zdt
        ZonedDateTime startUtcZdt = startZdt.withZoneSameInstant(ZoneOffset.UTC);
        //gets UTC dateTime from DB and converts to localDateTime
        LocalDateTime endLdt = LocalDateTime.of(endDate, endTime);
        //converts ldt to ZonedDateTime
        ZonedDateTime endZdt = ZonedDateTime.of(endLdt, ZoneId.systemDefault());
        //gets UTC time from zdt
        ZonedDateTime endUtcZdt = endZdt.withZoneSameInstant(ZoneOffset.UTC);
        //formats zonedDateTime value
        DateTimeFormatter customFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        PreparedStatement preparedStatement = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "UPDATE U06dWx.appointment SET userId = ?, type = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? "
                    + "WHERE appointmentId = ?";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
           
            //bind the values to the parameters
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, customFormat.format(startUtcZdt));
            preparedStatement.setString(4, customFormat.format(endUtcZdt));
            preparedStatement.setString(5, dateTime);
            preparedStatement.setString(6, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setInt(7, appointmentId);

            preparedStatement.executeUpdate();            
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        finally{
            if(preparedStatement != null)
                preparedStatement.close();
            
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
        }
    }

    public void removeApptFromDB() throws SQLException {
        PreparedStatement preparedStatement = null;
        System.out.println("inside removeApptFromDB()");
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "DELETE FROM U06dWx.appointment WHERE appointmentId = ? ";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the value to the parameter
            preparedStatement.setInt(1, appointmentId);

            preparedStatement.executeUpdate();            
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        finally{
            if(preparedStatement != null)
                preparedStatement.close();
            
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
        }
    }
    
    //setters and getters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    } 
    
    public Calendar getMyCalendar() {
        return myCalendar;
    }

    public void setMyCalendar(int year, int month, int day, int hour, int min, int sec) {
        this.myCalendar.set(year, month, day, hour, min, sec);
    }
}
