package com.ecoaccess.service;

import java.sql.Connection;
import java.sql.SQLException;
import com.ecoaccess.dao.AccountRepository;
import com.ecoaccess.dao.Database;
import com.ecoaccess.exception.AppExceptions.*;
import com.ecoaccess.model.Admin;
import com.ecoaccess.model.Passenger;
import com.ecoaccess.model.Staff;
import com.ecoaccess.model.Enums.Role;
import com.ecoaccess.util.*;

/** Account/authentication operations using the standalone JavaBean models. */
public class AccountService {
    private final AccountRepository accountRepository;
    public AccountService(){this(new AccountRepository());}
    public AccountService(AccountRepository accountRepository){this.accountRepository=accountRepository;}

    public Passenger register(String name,String mobile,String email,String password,String confirm,String otp){
        name=Validation.name(name); mobile=Validation.mobile(mobile); email=Validation.email(email); Validation.password(password);
        if(!password.equals(confirm))throw new ValidationException("Passwords do not match."); if(!"123456".equals(otp))throw new ValidationException("Use demo OTP 123456.");
        if(accountRepository.findPassengerByMobile(mobile).isPresent())throw new BusinessRuleException("Mobile already registered.");
        Passenger passenger=new Passenger(Ids.next("P"),name,mobile,email,Passwords.hash(password),0); Connection connection=null;
        try{connection=Database.connection();accountRepository.insertPassenger(connection,passenger);return passenger;}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(connection);}
    }

    public Object authenticate(Role role,String identity,String password){
        Validation.required(password,"Password is required.");
        return switch(role){
            case PASSENGER->{Passenger passenger=accountRepository.findPassengerByMobile(Validation.mobile(identity)).orElseThrow(()->new AuthenticationException("No account found with this mobile."));if(!Passwords.matches(password,passenger.getPasswordHash()))throw new AuthenticationException("Incorrect password.");yield passenger;}
            case STAFF->{Staff staff=accountRepository.findStaffByEmployeeId(Validation.required(identity,"Employee ID is required.")).orElseThrow(()->new AuthenticationException("No account found with this Employee ID."));if(!Passwords.matches(password,staff.getPasswordHash()))throw new AuthenticationException("Incorrect password.");yield staff;}
            case ADMIN->{Admin admin=accountRepository.findAdminByEmail(Validation.email(identity)).orElseThrow(()->new AuthenticationException("No account found with this email."));if(!Passwords.matches(password,admin.getPasswordHash()))throw new AuthenticationException("Incorrect password.");yield admin;}
        };
    }

    public void updatePassenger(Passenger passenger){Connection connection=null;try{connection=Database.connection();accountRepository.updatePassenger(connection,passenger);}catch(SQLException exception){throw Database.failure(exception);}finally{Database.close(connection);}}
    public AccountRepository repository(){return accountRepository;}
}
