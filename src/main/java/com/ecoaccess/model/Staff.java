package com.ecoaccess.model;

import com.ecoaccess.model.Enums.StaffStatus;

public class Staff {
    private String id; private String employeeId; private String name; private String passwordHash; private String jobRole; private StaffStatus status;
    public Staff() { }
    public Staff(String id,String employeeId,String name,String passwordHash,String jobRole,StaffStatus status){this.id=id;this.employeeId=employeeId;this.name=name;this.passwordHash=passwordHash;this.jobRole=jobRole;this.status=status;}
    public String getId(){return id;} public void setId(String value){id=value;} public String getEmployeeId(){return employeeId;} public void setEmployeeId(String value){employeeId=value;} public String getName(){return name;} public void setName(String value){name=value;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String value){passwordHash=value;} public String getJobRole(){return jobRole;} public void setJobRole(String value){jobRole=value;} public StaffStatus getStatus(){return status;} public void setStatus(StaffStatus value){status=value;}
}
