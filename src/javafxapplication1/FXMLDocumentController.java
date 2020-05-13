/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxapplication1.model.user;
import utils.DBConnection;

/**
 *
 * @author Fred
 */
public class FXMLDocumentController {
    
    @FXML
    private Label username;
    
    @FXML
    private Label password;
    
    @FXML
    private Label welcome;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private ResourceBundle rb = ResourceBundle.getBundle("javafxapplication1/login",Locale.getDefault());
    
    Connection conn = DBConnection.startConnection();
    
    //Replaced incorrect password error code with lambda expression
    Runnable r = () -> errorLabel.setText(rb.getString("wrongPass"));
    
    public void loginButtonClick(ActionEvent event) throws IOException, SQLException {
        Statement stmt = conn.createStatement();
        
        try{
            user.user = usernameField.getText();
            ResultSet rs = stmt.executeQuery("SELECT password, userId FROM user WHERE userName = '" + user.user+ "'");
            rs.next();
            String pass = rs.getString(1);
            user.userId = rs.getInt(2);
            
            if (pass.equals(passField.getText())){
                
                //record login in log file
                FileWriter fileWriter = new FileWriter("log.txt", true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println("Login by "+user.user+" at "+ZonedDateTime.now().toString());
                printWriter.close();
                
                Scene addPartScene = new Scene(FXMLLoader.load(getClass().getResource("Customer.fxml")));
        
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

                window.setScene(addPartScene);
                window.show();
            } else{
                r.run();
            }
            
        } catch (SQLException e) {
            errorLabel.setText(rb.getString("noUser"));
        }

    }
    
    
    
    public void initialize() {
        // TODO
        username.setText(rb.getString("username"));
        password.setText(rb.getString("password"));
        welcome.setText(rb.getString("welcome"));
        loginButton.setText(rb.getString("login"));
        
    }    
    
}
