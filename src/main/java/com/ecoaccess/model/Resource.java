package com.ecoaccess.model;

import com.ecoaccess.model.Enums.ServiceType;

public class Resource {
    private String id; private String station; private ServiceType type; private int quantity;
    public Resource() { }
    public Resource(String id,String station,ServiceType type,int quantity){this.id=id;this.station=station;this.type=type;this.quantity=quantity;}
    public String getId(){return id;} public void setId(String value){id=value;} public String getStation(){return station;} public void setStation(String value){station=value;} public ServiceType getType(){return type;} public void setType(ServiceType value){type=value;} public int getQuantity(){return quantity;} public void setQuantity(int value){quantity=value;}
}
