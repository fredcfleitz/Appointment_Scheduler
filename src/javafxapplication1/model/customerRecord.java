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
public class customerRecord {
    
    private int customerId = 0;
    private String customerName = null;
    private String customerAddress = null;
    private int addressId = 0;
    
    public customerRecord(int customerId, String customerName, int addressId){
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        //this.customerAddress = customerAddress;
    }
    public int getCustomerId(){
        return customerId;
    }
    
    public String getCustomerName(){
        return customerName;
    }
    
    public int getAddressId(){
        return addressId;
    }
    
}
