package com.ecoaccess.model;

import java.time.LocalDate; import java.time.LocalTime;

public class Ticket {
    private String pnr; private String train; private LocalDate date; private LocalTime time; private String station; private String platform; private String origin; private String destination; private String coach; private String className;
    public Ticket() { }
    public Ticket(String pnr,String train,LocalDate date,LocalTime time,String station,String platform,String origin,String destination,String coach,String className){this.pnr=pnr;this.train=train;this.date=date;this.time=time;this.station=station;this.platform=platform;this.origin=origin;this.destination=destination;this.coach=coach;this.className=className;}
    public String getPnr(){return pnr;} public void setPnr(String value){pnr=value;} public String getTrain(){return train;} public void setTrain(String value){train=value;} public LocalDate getDate(){return date;} public void setDate(LocalDate value){date=value;} public LocalTime getTime(){return time;} public void setTime(LocalTime value){time=value;} public String getStation(){return station;} public void setStation(String value){station=value;} public String getPlatform(){return platform;} public void setPlatform(String value){platform=value;} public String getOrigin(){return origin;} public void setOrigin(String value){origin=value;} public String getDestination(){return destination;} public void setDestination(String value){destination=value;} public String getCoach(){return coach;} public void setCoach(String value){coach=value;} public String getClassName(){return className;} public void setClassName(String value){className=value;}

    public String pnr(){return pnr;} public String train(){return train;} public LocalDate date(){return date;} public LocalTime time(){return time;} public String station(){return station;} public String platform(){return platform;} public String origin(){return origin;} public String destination(){return destination;} public String coach(){return coach;} public String className(){return className;}
}
