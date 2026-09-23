package com.ecoaccess.model;

/** Mutable JavaBean representation of a passenger. */
public class Passenger {
    private String id; private String name; private String mobile; private String email; private String passwordHash; private int points;
    public Passenger() { }
    public Passenger(String id, String name, String mobile, String email, String passwordHash, int points) { this.id=id; this.name=name; this.mobile=mobile; this.email=email; this.passwordHash=passwordHash; this.points=points; }
    public String getId(){return id;} public void setId(String value){id=value;}
    public String getName(){return name;} public void setName(String value){name=value;}
    public String getMobile(){return mobile;} public void setMobile(String value){mobile=value;}
    public String getEmail(){return email;} public void setEmail(String value){email=value;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String value){passwordHash=value;}
    public int getPoints(){return points;} public void setPoints(int value){points=value;}

    public String id(){return id;}
    public String name(){return name;}
    public String mobile(){return mobile;}
    public String email(){return email;}
    public String passwordHash(){return passwordHash;}
    public int points(){return points;}
}
