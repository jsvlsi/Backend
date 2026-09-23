package com.ecoaccess.model;

import java.time.LocalDateTime;

public class Journey {
    private String id; private String passengerId; private Ticket ticket; private boolean valid; private LocalDateTime validatedAt;
    public Journey() { }
    public Journey(String id,String passengerId,Ticket ticket,boolean valid,LocalDateTime validatedAt){this.id=id;this.passengerId=passengerId;this.ticket=ticket;this.valid=valid;this.validatedAt=validatedAt;}
    public String getId(){return id;} public void setId(String value){id=value;} public String getPassengerId(){return passengerId;} public void setPassengerId(String value){passengerId=value;} public Ticket getTicket(){return ticket;} public void setTicket(Ticket value){ticket=value;} public boolean isValid(){return valid;} public void setValid(boolean value){valid=value;} public LocalDateTime getValidatedAt(){return validatedAt;} public void setValidatedAt(LocalDateTime value){validatedAt=value;}
}
