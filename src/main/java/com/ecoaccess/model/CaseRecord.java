package com.ecoaccess.model;

import java.time.LocalDateTime; import com.ecoaccess.model.Enums.ComplaintType;

public class CaseRecord {
    private String id; private ComplaintType type; private String passengerId; private String passenger; private String bookingId; private int rating; private String subject; private String description; private String status; private LocalDateTime createdAt;
    public CaseRecord() { }
    public CaseRecord(String id,ComplaintType type,String passengerId,String passenger,String bookingId,int rating,String subject,String description,String status,LocalDateTime createdAt){this.id=id;this.type=type;this.passengerId=passengerId;this.passenger=passenger;this.bookingId=bookingId;this.rating=rating;this.subject=subject;this.description=description;this.status=status;this.createdAt=createdAt;}
    public String getId(){return id;} public void setId(String value){id=value;} public ComplaintType getType(){return type;} public void setType(ComplaintType value){type=value;} public String getPassengerId(){return passengerId;} public void setPassengerId(String value){passengerId=value;} public String getPassenger(){return passenger;} public void setPassenger(String value){passenger=value;} public String getBookingId(){return bookingId;} public void setBookingId(String value){bookingId=value;} public int getRating(){return rating;} public void setRating(int value){rating=value;} public String getSubject(){return subject;} public void setSubject(String value){subject=value;} public String getDescription(){return description;} public void setDescription(String value){description=value;} public String getStatus(){return status;} public void setStatus(String value){status=value;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime value){createdAt=value;}

    public String id(){return id;} public ComplaintType type(){return type;} public String passengerId(){return passengerId;} public String passenger(){return passenger;} public String bookingId(){return bookingId;} public int rating(){return rating;} public String subject(){return subject;} public String description(){return description;} public String status(){return status;} public LocalDateTime createdAt(){return createdAt;}
}
