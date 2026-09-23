package com.ecoaccess.model;

import java.time.*;

public class Redemption {
    private String id; private String passengerId; private String passenger; private String rewardName; private int points; private String couponCode; private int couponValue; private String status; private LocalDate date; private LocalDateTime createdAt; private LocalDateTime expiresAt;
    public Redemption() { }
    public Redemption(String id,String passengerId,String passenger,String rewardName,int points,String couponCode,int couponValue,String status,LocalDate date,LocalDateTime createdAt,LocalDateTime expiresAt){this.id=id;this.passengerId=passengerId;this.passenger=passenger;this.rewardName=rewardName;this.points=points;this.couponCode=couponCode;this.couponValue=couponValue;this.status=status;this.date=date;this.createdAt=createdAt;this.expiresAt=expiresAt;}
    public String getId(){return id;} public void setId(String value){id=value;} public String getPassengerId(){return passengerId;} public void setPassengerId(String value){passengerId=value;} public String getPassenger(){return passenger;} public void setPassenger(String value){passenger=value;} public String getRewardName(){return rewardName;} public void setRewardName(String value){rewardName=value;} public int getPoints(){return points;} public void setPoints(int value){points=value;} public String getCouponCode(){return couponCode;} public void setCouponCode(String value){couponCode=value;} public int getCouponValue(){return couponValue;} public void setCouponValue(int value){couponValue=value;} public String getStatus(){return status;} public void setStatus(String value){status=value;} public LocalDate getDate(){return date;} public void setDate(LocalDate value){date=value;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime value){createdAt=value;} public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime value){expiresAt=value;}
}
