package com.ecoaccess.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ecoaccess.dao.AdminDao;
import com.ecoaccess.dao.Database;
import com.ecoaccess.dao.PassengerDao;
import com.ecoaccess.dao.StaffDao;
import com.ecoaccess.exception.AppExceptions.*;
import com.ecoaccess.model.Admin;
import com.ecoaccess.model.Passenger;
import com.ecoaccess.model.Staff;
import com.ecoaccess.model.Enums.Role;
import com.ecoaccess.util.*;

/** Authentication and account orchestration over entity-specific DAOs. */
public class AccountService {
    private final PassengerDao passengerDao; private final StaffDao staffDao; private final AdminDao adminDao;
    public AccountService(){this(new PassengerDao(),new StaffDao(),new AdminDao());}
    public AccountService(PassengerDao passengerDao, StaffDao staffDao, AdminDao adminDao){this.passengerDao=passengerDao;this.staffDao=staffDao;this.adminDao=adminDao;}

    public Passenger register(String name,String mobile,String email,String password,String confirm,String otp){
        name=Validation.name(name); mobile=Validation.mobile(mobile); email=Validation.email(email); Validation.password(password);
        if(!password.equals(confirm))throw new ValidationException("Passwords do not match."); if(!"123456".equals(otp))throw new ValidationException("Use demo OTP 123456.");
        if(passengerDao.findByMobile(mobile).isPresent())throw new BusinessRuleException("Mobile already registered.");
        Passenger passenger=new Passenger(Ids.next("P"),name,mobile,email,Passwords.hash(password),0); Connection connection=null;
        try{connection=Database.connection();passengerDao.insert(connection,passenger);return passenger;}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(connection);}
    }

    public Object authenticate(Role role,String identity,String password){
        Validation.required(password,"Password is required.");
        return switch(role){
            case PASSENGER->{Passenger passenger=passengerDao.findByMobile(Validation.mobile(identity)).orElseThrow(()->new AuthenticationException("No account found with this mobile."));if(!Passwords.matches(password,passenger.getPasswordHash()))throw new AuthenticationException("Incorrect password.");yield passenger;}
            case STAFF->{Staff staff=staffDao.findByEmployeeId(Validation.required(identity,"Employee ID is required.")).orElseThrow(()->new AuthenticationException("No account found with this Employee ID."));if(!Passwords.matches(password,staff.getPasswordHash()))throw new AuthenticationException("Incorrect password.");yield staff;}
            case ADMIN->{Admin admin=adminDao.findByEmail(Validation.email(identity)).orElseThrow(()->new AuthenticationException("No account found with this email."));if(!Passwords.matches(password,admin.getPasswordHash()))throw new AuthenticationException("Incorrect password.");yield admin;}
        };
    }

    public void updatePassenger(Passenger passenger){Connection connection=null;try{connection=Database.connection();passengerDao.update(connection,passenger);}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(connection);}}
    public PassengerDao passengers(){return passengerDao;} public StaffDao staff(){return staffDao;} public AdminDao admins(){return adminDao;}
}
