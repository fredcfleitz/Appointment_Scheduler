/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxapplication1.model.EmptyFieldException;
import javafxapplication1.model.user;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author Fred
 */
public class AddCustomerController implements Initializable {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private TextField address2Field;
    
    @FXML
    private ComboBox cityField;
    
    @FXML
    private TextField postalField;
    
    @FXML
    private Label countryField;
    
    @FXML
    private Button addCustomerButton;
    
    Connection conn = DBConnection.startConnection();
    
    @FXML
    private void addCustomer(ActionEvent event) throws SQLException, IOException, EmptyFieldException {
        Statement stmt = conn.createStatement();
        String name = nameField.getText();
        String address = addressField.getText();
        String address2 = address2Field.getText();
        String postalCode = postalField.getText();
        String phone = phoneField.getText();
        String city = cityField.getValue().toString();
        AtomicInteger empty = new AtomicInteger(0);
        
        List<String> fieldList = new ArrayList();
        fieldList.addAll(Arrays.asList(name, address, postalCode, phone, city));
        
        //lambda expression for efficiently testing if each field is empty
        fieldList.forEach((String f) -> {
            if("".equals(f)){
                empty.getAndSet(1);
            }
        }
        );
        
        if(empty.get() == 1){
            throw new EmptyFieldException();
        }
        
        
        
        java.util.Date date = new Date();
        Object now = new java.sql.Timestamp(date.getTime());
        ResultSet rs = stmt.executeQuery("SELECT cityId FROM city WHERE city = \""+city+"\"");
        rs.next();
        String cityId = rs.getString(1);
        stmt.executeUpdate("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) "
                + "VALUES ( \""+address+"\",\""+address2+"\",\""+cityId+"\",\""+postalCode+"\",\""+phone+"\",\""+now+"\",\""+user.user+"\",\""+user.user+"\")");
        rs = stmt.executeQuery("SELECT addressId FROM address WHERE address = \""+address+"\"");
        rs.next();
        String addressId = rs.getString(1);
        stmt.executeUpdate("INSERT INTO customer (customerName, addressId, createDate, createdBy, lastUpdateBy, active) "
                + "VALUES (\""+name+"\",\""+addressId+"\",\""+now+"\",\""+"test"+"\",\""+"test"+"\",\""+1+"\")");
        Scene customerScene = new Scene(FXMLLoader.load(getClass().getResource("Customer.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(customerScene);
        window.show();
        
    }
    
    @FXML
    private void cancelButton(ActionEvent event) throws IOException {
        Scene customerScene = new Scene(FXMLLoader.load(getClass().getResource("Customer.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(customerScene);
        window.show();
    }
    
    //Updates the country when another city is selected
    @FXML
    private void selectCity(ActionEvent event) throws SQLException {
        Statement stmt = conn.createStatement();
        String city = cityField.getValue().toString();
        ResultSet rs = stmt.executeQuery("select * from city where city = \""+city+"\"");
        rs.next();
        String countryId = rs.getString(3);
        rs = stmt.executeQuery("select * from country where countryId = " + countryId);
        rs.next();
        String country = rs.getString(2);
        countryField.setText(country);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cityField.setValue("New York");
        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select city from city;");
            while(rs.next()){
            cityField.getItems().add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    
    
}
