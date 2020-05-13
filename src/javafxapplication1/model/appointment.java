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
public class appointment {
    
    private int appointmentId = 0;
    private int customerId = 0;
    private int userId = 0;
    private String type = null;
    private String start = null;
    private String end = null;
    private String customerName = null;
    
    public appointment(int appointmentId, int customerId, int userId, String type, String start, String end, String customerName){
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerName = customerName;
    }
    
    public int getAppointmentId(){
        return appointmentId;
    }
    
    public int getCustomerId(){
        return customerId;
    }
    
    public int getUserId(){
        return userId;
    }
    
    public String getType(){
        return type;
    }
    
    public String getStart(){
        return start;
    }
    
    public String getEnd(){
        return end;
    }
    
    public String getCustomerName(){
        return customerName;
    }
    
}
