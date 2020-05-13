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
public class EmptyFieldException extends Exception {
    
    public EmptyFieldException(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error: empty field");
        alert.setContentText("Fields must not be empty");
        Optional<ButtonType> result = alert.showAndWait();
    }
    
}
