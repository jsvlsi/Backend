package com.ecoaccess.model;

public class Admin {
    private String id; private String name; private String email; private String passwordHash;
    public Admin() { }
    public Admin(String id,String name,String email,String passwordHash){this.id=id;this.name=name;this.email=email;this.passwordHash=passwordHash;}
    public String getId(){return id;} public void setId(String value){id=value;} public String getName(){return name;} public void setName(String value){name=value;} public String getEmail(){return email;} public void setEmail(String value){email=value;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String value){passwordHash=value;}
}
