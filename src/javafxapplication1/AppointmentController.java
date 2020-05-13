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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafxapplication1.model.appointment;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author Fred
 */
public class AppointmentController implements Initializable {

    
    @FXML
    private TableView appointments;
    
    @FXML
    TableColumn<String, appointment> column1 = new TableColumn<>("appointmentId");
    
    @FXML
    TableColumn<String, appointment> column2 = new TableColumn<>("customerName");
    
    @FXML
    TableColumn<String, appointment> column3 = new TableColumn<>("type");
    
    @FXML
    TableColumn<String, appointment> column4 = new TableColumn<>("start");
    
    @FXML
    private ComboBox customerNameField;
    
    @FXML
    private TextField typeField;
    
    @FXML
    private DatePicker startDateField;
    
    @FXML
    private ComboBox startTimeBox;
    
    @FXML
    private ComboBox endTimeBox;
    
    @FXML
    private RadioButton all;
    
    @FXML
    private RadioButton nextWeek;
    
    @FXML
    private RadioButton nextMonth;
    
    private ToggleGroup timeGroup;
    
    Connection conn = DBConnection.startConnection();
    
    Long offset = OffsetDateTime.now().getOffset().getLong(ChronoField.OFFSET_SECONDS);
    
    @FXML
    private void populateTable() throws SQLException {
        
        appointments.getItems().clear();
        customerNameField.getItems().clear();
        startTimeBox.getItems().clear();
        endTimeBox.getItems().clear();
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT appointmentId, customer.customerId, userId, type, start, end, customerName "
                + "FROM appointment JOIN customer ON customer.customerId = appointment.customerId");
        while(rs.next()){
            int appointmentId = rs.getInt(1);
            int customerId = rs.getInt(2);
            int userId = rs.getInt(3);
            String type = rs.getString(4);
            String customerName = rs.getString(7);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime time = LocalDateTime.parse(rs.getString(5), formatter).plusSeconds(offset);
            LocalDateTime now = LocalDateTime.now();
           
            LocalDateTime endTime = LocalDateTime.parse(rs.getString(6), formatter).plusSeconds(offset);
            
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String start = time.format(formatter);
            String end = endTime.format(formatter);
            
            if(all.isSelected()){
                appointments.getItems().add(new appointment(appointmentId, customerId, userId, type, start, end, customerName));
            } else if (nextWeek.isSelected()){
                if (time.isBefore(now.plusWeeks(1)) && time.isAfter(now) ){
                    appointments.getItems().add(new appointment(appointmentId, customerId, userId, type, start, end, customerName));
                }
            } else if (nextMonth.isSelected()){
                if (time.isBefore(now.plusMonths(1)) && time.isAfter(now)){
                    appointments.getItems().add(new appointment(appointmentId, customerId, userId, type, start, end, customerName));
                }
            }
            
        }
        rs = stmt.executeQuery("select customerName from customer;");
        
        while(rs.next()){
            customerNameField.getItems().add(rs.getString(1));
        }
        
        int openingTime = 10;
        int closingTime = 18;
        
        
        for(int x = openingTime; x < closingTime; x++ ){
            startTimeBox.getItems().add(Integer.toString(x)+":00");
            startTimeBox.getItems().add(Integer.toString(x)+":15");
            startTimeBox.getItems().add(Integer.toString(x)+":30");
            startTimeBox.getItems().add(Integer.toString(x)+":45");
            endTimeBox.getItems().add(Integer.toString(x)+":00");
            endTimeBox.getItems().add(Integer.toString(x)+":15");
            endTimeBox.getItems().add(Integer.toString(x)+":30");
            endTimeBox.getItems().add(Integer.toString(x)+":45");
        }
        
    }
    
    @FXML
    private void toggleToggle(ActionEvent event) throws SQLException{
        populateTable();
    }
    
    @FXML
    private void selectAppointment(MouseEvent event) throws SQLException {
        appointment ap = (appointment) appointments.getSelectionModel().getSelectedItem();
        
        customerNameField.setValue(ap.getCustomerName());
        typeField.setText(ap.getType());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(ap.getStart(),formatter);
        LocalDate startDate = startDateTime.toLocalDate();
        String startTime = startDateTime.toLocalTime().toString();
        startDateField.setValue(startDate);
        startTimeBox.setValue(startTime);
        
        LocalDateTime endDateTime = LocalDateTime.parse(ap.getEnd(),formatter);
        String endTime = endDateTime.toLocalTime().toString();
        endTimeBox.setValue(endTime);
        
    }
    

        
    @FXML
    private void saveChanges(ActionEvent event) throws SQLException {
        appointment ap = (appointment) appointments.getSelectionModel().getSelectedItem();
        Statement stmt = conn.createStatement();
        String appointmentId = Integer.toString(ap.getAppointmentId());
        
        LocalTime startTime = LocalTime.parse(startTimeBox.getValue().toString());
        LocalDate startDate = startDateField.getValue();
        LocalDateTime startDateTime = startTime.atDate(startDate).minusSeconds(offset);
        
        LocalTime endTime = LocalTime.parse(endTimeBox.getValue().toString());
        LocalDateTime endDateTime = endTime.atDate(startDate).minusSeconds(offset);
        
        String customerName = customerNameField.getValue().toString();
        ResultSet rs = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = \"" + customerName +"\"");
        rs.next();
        String customerId = rs.getString(1);
        String type = typeField.getText();
        
        stmt.executeUpdate("UPDATE appointment SET start = \"" + startDateTime + "\", end = \"" + endDateTime + "\", "
                + "customerId = " + customerId + ", type = \"" + type + "\" WHERE appointmentId = " + appointmentId);
        
        populateTable();
    }
    
    @FXML
    private void deleteButton(ActionEvent event) throws SQLException {
        appointment ap = (appointment) appointments.getSelectionModel().getSelectedItem();
        String appointmentId = Integer.toString(ap.getAppointmentId());
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM appointment WHERE appointmentId = "+appointmentId);
        populateTable();
    }
    
    @FXML
    private void addAppointment(ActionEvent event) throws IOException {
        Scene addAppointmentScene = new Scene(FXMLLoader.load(getClass().getResource("AddAppointment.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(addAppointmentScene);
        window.show();
    }
    
    @FXML
    private void customersButton(ActionEvent event) throws IOException {
        Scene customersScene = new Scene(FXMLLoader.load(getClass().getResource("Customer.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(customersScene);
        window.show();
    }
    
    @FXML
    private void reportsButton(ActionEvent event) throws IOException {
        Scene reportsScene = new Scene(FXMLLoader.load(getClass().getResource("Reports.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(reportsScene);
        window.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        column1.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        column2.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        column3.setCellValueFactory(new PropertyValueFactory<>("type"));
        column4.setCellValueFactory(new PropertyValueFactory<>("start"));
        
        timeGroup = new ToggleGroup();
        this.all.setToggleGroup(timeGroup);
        this.nextMonth.setToggleGroup(timeGroup);
        this.nextWeek.setToggleGroup(timeGroup);
        this.all.setSelected(true);
        
        try {
            populateTable();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
