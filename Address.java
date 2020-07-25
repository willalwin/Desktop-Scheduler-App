/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author WillA
 */
public class Address {
    private int addressId,
        cityId;
    private String address,
           address2,
           postalCode,
           phone,
           createdBy,
           lastUpdateBy;
    private LocalDate createDate,
                      lastUpdate;
    //constructor for addresses of new customers

    public Address(int cityId, String address, String postalCode, String phone) {
        this.cityId = cityId;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    
    
    //constructor for addresses of existing customers
    public Address(int addressId, int cityId, String address, String address2, String postalCode, String phone, String createdBy, String lastUpdateBy, LocalDate createDate, LocalDate lastUpdate) {
        this.addressId = addressId;
        this.cityId = cityId;
        this.address = address;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createdBy = createdBy;
        this.lastUpdateBy = lastUpdateBy;
        this.createDate = createDate;
        this.lastUpdate = lastUpdate;
    }
    
    public void insertIntoDB() throws SQLException {
        //variables for dateTime conversion
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());
        String dateTime  = date + " " + time;
        PreparedStatement preparedStatement = null;        
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "INSERT INTO U06dWx.address (cityId, address, address2, postalCode, phone, createdBy, lastUpdateBy, createDate, lastUpdate)"
                    + "VALUES (?,?,?,?,?,?,?,?,?)";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the values to the parameters
            preparedStatement.setInt(1, cityId);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, "");
            preparedStatement.setString(4, postalCode);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setString(7, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setString(8, dateTime);
            preparedStatement.setString(9, dateTime);
            
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
    
    public void updateAddressInDB() throws SQLException {
        //variables for dateTime conversion
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());
        String dateTime  = date + " " + time;
        
        PreparedStatement preparedStatement = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "UPDATE U06dWx.address SET cityId = ?, address = ?, postalCode = ?, phone = ?, lastUpdateBy = ?, lastUpdate = ? "
                    + "WHERE addressId = ?";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql); 
            //bind the values to the parameters
            preparedStatement.setInt(1, cityId);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, postalCode);
            preparedStatement.setString(4, phone);
            preparedStatement.setString(5, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setString(6, dateTime);
            preparedStatement.setInt(7, addressId);

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
    
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
