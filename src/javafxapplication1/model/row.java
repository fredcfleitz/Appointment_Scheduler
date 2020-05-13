/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1.model;

/**
 *
 * @author Fred
 */

//A generic row class for generating different types of reports using the same table
public class row {
    
    String value1 = null;
    String value2 = null;
    String value3 = null;
    
    public row(String value1, String value2, String value3){
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
    
    public String getValue1(){
        return value1;
    }
    
    public String getValue2(){
        return value2;
    }
    
    public String getValue3(){
        return value3;
    }
    
}
