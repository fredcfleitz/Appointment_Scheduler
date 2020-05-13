/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxapplication1.model.OverlapException;
import javafxapplication1.model.WeekendException;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author Fred
 */
public class AddAppointmentController implements Initializable {

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
    
    Connection conn = DBConnection.startConnection();
    
    Long offset = OffsetDateTime.now().getOffset().getLong(ChronoField.OFFSET_SECONDS);
    
    @FXML
    private void populateFields() throws SQLException{
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select customerName from customer;");
        
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
    private void addAppointment(ActionEvent event) throws IOException, SQLException, WeekendException, OverlapException {
        Statement stmt = conn.createStatement();
        String customerName = customerNameField.getValue().toString();
        ResultSet rs = stmt.executeQuery("select customerId from customer WHERE customerName = \"" + customerName + "\"");
        rs.next();
        String customerId = rs.getString(1);
        
        java.util.Date date = new Date();
        Object now = new java.sql.Timestamp(date.getTime());
        
        LocalTime startTime = LocalTime.parse(startTimeBox.getValue().toString());
        LocalDate startDate = startDateField.getValue();
        LocalDateTime startDateTime = startTime.atDate(startDate).minusSeconds(offset);
        
        LocalTime endTime = LocalTime.parse(endTimeBox.getValue().toString());
        LocalDateTime endDateTime = endTime.atDate(startDate).minusSeconds(offset);
        
        String type = typeField.getText();
        
        int day = startDate.getDayOfWeek().getValue();
        
        if(day == 6 || day == 7){
            throw new WeekendException();
        }
        
        rs = stmt.executeQuery("SELECT start, end FROM appointment");
        while(rs.next()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime start = LocalDateTime.parse(rs.getString(1), formatter);
            LocalDateTime end = LocalDateTime.parse(rs.getString(2), formatter);
            
            if(startDateTime.isBefore(start) && endDateTime.isAfter(start) || startDateTime.isBefore(end) && endDateTime.isAfter(end)){
                throw new OverlapException();
            }
            
        }
        
        
        stmt.executeUpdate("INSERT INTO appointment (customerId, userId, type, start, end, createDate, title, description, location, contact, url, createdBy, lastUpdateBy) "
                + "VALUES ('"+customerId+"','"+"1"+"','"+type+"','"+startDateTime+"','"+endDateTime+"','"+now+"','null','null','null','null','null','test','test')");
        
        //Return to the appointments screen
        Scene appointmentsScene = new Scene(FXMLLoader.load(getClass().getResource("appointment.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(appointmentsScene);
        window.show();
    }
    
    @FXML
    private void cancelButton(ActionEvent event) throws IOException {
        Scene appointmentsScene = new Scene(FXMLLoader.load(getClass().getResource("appointment.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(appointmentsScene);
        window.show();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         try {
             populateFields();
         } catch (SQLException ex) {
             Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
         }
    }    
    
}
