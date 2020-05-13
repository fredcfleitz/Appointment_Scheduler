/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1.model;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Fred
 */
public class WeekendException extends Exception {
    
    public WeekendException(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error: outside of business hours");
        alert.setContentText("Appointments may not be made on weekends");
        Optional<ButtonType> result = alert.showAndWait();
    }
    
}
