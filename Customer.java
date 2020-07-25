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
public class Customer {
    private int customerId,
                addressId, 
                active;
    private String customerName,  
                   createdBy,                     
                   lastUpdateBy;
    private LocalDate createDate,
                      lastUpdate;

    public Customer(int addressId, String customerName) {
        this.addressId = addressId;
        this.customerName = customerName;
    }
    
    public Customer(int customerId, int addressId, int active, String customerName, LocalDate createDate, String createdBy, LocalDate lastUpdate, String lastUpdateBy) {
        this.customerId = customerId;
        this.addressId = addressId;
        this.active = active;
        this.customerName = customerName;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
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
            String sql = "INSERT INTO U06dWx.customer (addressId, active, customerName, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES (?,?,?,?,?,?,?)";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the values to the parameters
            preparedStatement.setInt(1, addressId);
            preparedStatement.setInt(2, 1);
            preparedStatement.setString(3, customerName);
            preparedStatement.setString(4, dateTime);
            preparedStatement.setString(5, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setString(6, dateTime);
            preparedStatement.setString(7, DBConnection.getLoggedInUser().getUserName());
            
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

    public void updateCustomerInDB() throws SQLException {
        //variables for dateTime conversion
        System.out.println("inside updateCustomerInDB()");
        Date date = Date.valueOf(LocalDate.now());
        Time time = Time.valueOf(LocalTime.now());
        String dateTime  = date + " " + time;
        PreparedStatement preparedStatement = null;        
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs 
            String sql = "UPDATE U06dWx.customer SET customerName = ?, lastUpdateBy = ?, lastUpdate = ? "
                    + "WHERE customerId = ?";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the values to the parameters
            preparedStatement.setString(1, customerName);
            preparedStatement.setString(2, DBConnection.getLoggedInUser().getUserName());
            preparedStatement.setString(3, dateTime);
            preparedStatement.setInt(4, customerId);
            
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
    
    public void removeCustomerFromDB() throws SQLException {
        System.out.println("inside removeCustomerFromDB()");
        PreparedStatement preparedStatement = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "DELETE FROM U06dWx.appointment WHERE customerId = ? ";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the value to the parameter
            preparedStatement.setInt(1, customerId);

            preparedStatement.executeUpdate();            
        }
        catch(ClassNotFoundException | SQLException e){
            System.err.println(e.getMessage());
        }
        finally{
            if(preparedStatement != null)
                preparedStatement.close();
            
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
        }
        preparedStatement = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "DELETE FROM U06dWx.customer WHERE customerId = ? ";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the value to the parameter
            preparedStatement.setInt(1, customerId);

            preparedStatement.executeUpdate();            
        }
        catch(ClassNotFoundException | SQLException e){
            System.err.println(e.getMessage());
        }
        finally{
            if(preparedStatement != null)
                preparedStatement.close();
            
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
        }
        preparedStatement = null;
        try{
            //connect to DB
            DBConnection.makeConnection();
            //create a string that holds the query with ? as user inputs
            String sql = "DELETE FROM U06dWx.address WHERE addressId = ? ";
            //prepare the query
            preparedStatement = DBConnection.getConn().prepareStatement(sql);
            //bind the value to the parameter
            preparedStatement.setInt(1, addressId);

            preparedStatement.executeUpdate();            
        }
        catch(ClassNotFoundException | SQLException e){
            System.err.println(e.getMessage());
        }
        finally{
            if(preparedStatement != null)
                preparedStatement.close();
            
            if(DBConnection.getConn() != null)
                DBConnection.closeConnection();
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
