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
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxapplication1.model.row;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author Fred
 */
public class ReportsController implements Initializable {

    @FXML
    private Button appointmentsButton;
    
    @FXML
    private Button generateReportButton;
    
    @FXML
    private Label label;
    
    @FXML
    private RadioButton byMonth;
    
    @FXML
    private RadioButton byConsultant;
    
    @FXML
    private RadioButton byCity;
    
    @FXML
    private TableView reportsTable;
    
    @FXML
    TableColumn<String, row> column1 = new TableColumn<>("value1");
    
    @FXML
    TableColumn<String, row> column2 = new TableColumn<>("value2");
    
    @FXML
    TableColumn<String, row> column3 = new TableColumn<>("value3");
    
    @FXML
    private ComboBox selectionBox;
    
    private ToggleGroup toggleGroup;
    
    Connection conn = DBConnection.startConnection();
    
    @FXML
    private void appointmentsButton(ActionEvent event) throws IOException {
        Scene appointmentsScene = new Scene(FXMLLoader.load(getClass().getResource("appointment.fxml")));
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        window.setScene(appointmentsScene);
        window.show();
    }
    
    @FXML
    private void byMonthSelect(ActionEvent event) {
        label.setText("Select Month:");
        selectionBox.getItems().clear();
        for(int i = 1; i < 13; i++){
            selectionBox.getItems().add(Month.of(i));
        }
    }
    
    @FXML
    private void byConsultantSelect(ActionEvent event) throws SQLException{
        label.setText("Select Consultant:");
        selectionBox.getItems().clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT userName FROM user");
        while (rs.next()){
            selectionBox.getItems().add(rs.getString(1));
        }
    }
    
    @FXML
    private void byCitySelect(ActionEvent event) throws SQLException{
        label.setText("Select City:");
        selectionBox.getItems().clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT city FROM city");
        while (rs.next()){
            selectionBox.getItems().add(rs.getString(1));
        }
    }
    
    @FXML
    private void generateReport(ActionEvent event) throws SQLException{
        
        reportsTable.getItems().clear();
        
        if(byMonth.isSelected()){
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT start, type, customerName FROM appointment JOIN customer ON appointment.customerId = customer.customerId");
            while(rs.next()){
                String startTime = rs.getString(1) + " GMT";
                String type = rs.getString(2);
                String customer = rs.getString(3);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                
                if(LocalDateTime.parse(rs.getString(1), formatter).getMonthValue() == Month.valueOf(selectionBox.getValue().toString()).getValue()){
                    reportsTable.getItems().add(new row(startTime, type, customer));
                }
                
            }
            
        }
        
        
        if(byConsultant.isSelected()){
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT userId FROM user WHERE userName = '"+selectionBox.getValue().toString()+"'");
            rs.next();
            String userId = rs.getString(1);
            System.out.println(userId);
            rs = stmt.executeQuery("SELECT start, type, customerName FROM appointment JOIN customer ON appointment.customerId = customer.customerId WHERE userId = "+userId);
            while(rs.next()){
                String startTime = rs.getString(1) + " GMT";
                String type = rs.getString(2);
                String customer = rs.getString(3);
                
                reportsTable.getItems().add(new row(startTime, type, customer));
                
            }
            
        }
        
        if(byCity.isSelected()){
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT start, type, customerName FROM appointment "
                    + "JOIN customer ON appointment.customerId = customer.customerId "
                    + "JOIN address ON  customer.addressId = address.addressId "
                    + "JOIN city ON city.cityId = address.cityId WHERE city = '" + selectionBox.getValue().toString() + "'");
            while(rs.next()){
                String startTime = rs.getString(1) + " GMT";
                String type = rs.getString(2);
                String customer = rs.getString(3);
                
                reportsTable.getItems().add(new row(startTime, type, customer));
                
            }
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        column1.setText("Appointment Time");
        column2.setText("Appointment Type");
        column3.setText("Customer Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("value1"));
        column2.setCellValueFactory(new PropertyValueFactory<>("value2"));
        column3.setCellValueFactory(new PropertyValueFactory<>("value3"));
        toggleGroup = new ToggleGroup();
        this.byMonth.setToggleGroup(toggleGroup);
        this.byConsultant.setToggleGroup(toggleGroup);
        this.byCity.setToggleGroup(toggleGroup);
        // TODO
    }    
    
}
