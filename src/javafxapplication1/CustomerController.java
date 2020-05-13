/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.sql.DataSource;
import utils.DBConnection;

/**
 *
 * @author Fred
 */
import javafxapplication1.model.customerRecord;
import javafxapplication1.model.user;
public class CustomerController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private TableView customerRecords;
    
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
    private Button saveChangesButton;
    
    @FXML
    TableColumn<String, customerRecord> column1 = new TableColumn<>("customerId");
    
    @FXML
    TableColumn<String, customerRecord> column2 = new TableColumn<>("customerName");
    
    Connection conn = DBConnection.startConnection();
    
    @FXML
    private void selectCustomerRecord(MouseEvent event) throws SQLException {
        customerRecord cr = (customerRecord) customerRecords.getSelectionModel().getSelectedItem();
        Statement stmt = conn.createStatement();
        String customerId = Integer.toString(cr.getCustomerId());
        ResultSet rs = stmt.executeQuery("SELECT customerName, address, address2, postalCode, phone, city, country FROM customer "
                + "join address on customer.addressId = address.addressId "
                + "join city on address.cityId = city.cityId "
                + "join country on city.countryId = country.countryId WHERE customerId = " + customerId);
        rs.next();
        String name  =rs.getString(1);
        String address1 = rs.getString(2);
        String address2 = rs.getString(3);
        String postal = rs.getString(4);
        String phoneNumber = rs.getString(5);
        String city = rs.getString(6);
        String country = rs.getString(7);
        
        nameField.setText(name);
        phoneField.setText(phoneNumber);
        addressField.setText(address1);
        address2Field.setText(address2);
        postalField.setText(postal);
        cityField.setValue(city);
        countryField.setText(country);
        
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
    
    @FXML
    private void appointmentsButton(ActionEvent event) throws IOException {
        Scene appointmentsScene = new Scene(FXMLLoader.load(getClass().getResource("appointment.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(appointmentsScene);
        window.show();
    }
    
    @FXML
    private void saveChanges(ActionEvent event) throws SQLException {
        customerRecord cr = (customerRecord) customerRecords.getSelectionModel().getSelectedItem();
        Statement stmt = conn.createStatement();
        String customerId = Integer.toString(cr.getCustomerId());
        ResultSet rs = stmt.executeQuery("SELECT addressId FROM customer WHERE customerId = " + customerId);
        rs.next();
        String addressId = rs.getString(1);
        
        String name = nameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String address2 = address2Field.getText();
        String postal = postalField.getText();
        String city = cityField.getValue().toString();
        String country = countryField.getText();
        
        rs = stmt.executeQuery("SELECT cityId FROM city WHERE city = \""+city+"\"");
        rs.next();
        String cityId = rs.getString(1);
        
        stmt.executeUpdate("UPDATE address SET address = \""+address+"\", address2 = \""+address2+"\", phone = \""+phone+"\", cityId = \""+cityId+"\", postalCode = \""+postal+"\" WHERE addressId = "+addressId);
        stmt.executeUpdate("UPDATE customer SET customerName = \""+name+"\" WHERE customerId = "+customerId);
        
        
    }
    
    @FXML
    private void deleteButton(ActionEvent event) throws SQLException {
        customerRecord cr = (customerRecord) customerRecords.getSelectionModel().getSelectedItem();
        String customerId = Integer.toString(cr.getCustomerId());
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM customer WHERE customerId = "+customerId);
        populateTable();
    }
    
    @FXML
    private void addCustomerButton(ActionEvent event) throws IOException {
        Scene addCustomerScene = new Scene(FXMLLoader.load(getClass().getResource("AddCustomer.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addCustomerScene);
        window.show();
    }
    
    private void populateTable() throws SQLException {
        customerRecords.getItems().clear();
        nameField.clear();
        phoneField.clear();
        addressField.clear();
        address2Field.clear();
        postalField.clear();
        countryField.setText("");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from customer");
        while(rs.next()){
            int id = rs.getInt(1);
            String name = rs.getString(2);
            int addressId = rs.getInt(3);
            
            customerRecords.getItems().add(new customerRecord(id,name,addressId));
        }
        
        rs = stmt.executeQuery("select city from city;");
        
        while(rs.next()){
            cityField.getItems().add(rs.getString(1));
        }
        
    }
    
    
    //checks if there is an appointment within the next 15 minutes
    private void checkAppointments() throws SQLException{
        
        Long offset = OffsetDateTime.now().getOffset().getLong(ChronoField.OFFSET_SECONDS);
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT start, customerName, type FROM appointment JOIN customer ON customer.customerId = appointment.customerId WHERE userId = " + Integer.toString(user.userId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime now = LocalDateTime.now();
        while(rs.next()){
            LocalDateTime time = LocalDateTime.parse(rs.getString(1), formatter).plusSeconds(offset);
            if (time.isBefore(now.plusMinutes(15)) && time.isAfter(now) ){
                
                    String name = rs.getString(2);
                    String type = rs.getString(3);
                            
                    
                    System.out.println("test");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Notification");
                    alert.setHeaderText("You have an appointment soon");
                    alert.setContentText(type + " with " + name + " at " + time.toLocalTime());
                    
                     Optional<ButtonType> result = alert.showAndWait();

                }
            
        }
        
        
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        column1.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        column2.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        try {
            checkAppointments();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            populateTable();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }    

}
