package com.ecoaccess.util;
import java.time.*;import java.util.regex.*;import com.ecoaccess.exception.AppExceptions.ValidationException;
public final class Validation { private Validation(){}
 public static String required(String v,String message){if(v==null||v.trim().isEmpty())throw new ValidationException(message);return v.trim();}
 public static String name(String v){v=required(v,"Full name is required.");if(!v.matches("[A-Za-z]+( [A-Za-z]+)*")||v.length()>20)throw new ValidationException("Name must contain letters/spaces only and be at most 20 characters.");return v;}
 public static String mobile(String v){v=required(v,"Mobile number is required.").replaceAll("[^0-9]","");if(v.length()==12&&v.startsWith("91"))v=v.substring(2);if(!v.matches("[6-9][0-9]{9}"))throw new ValidationException("Enter a valid 10-digit Indian mobile number.");return v;}
 public static String email(String v){v=required(v,"Email is required.");if(!v.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))throw new ValidationException("Enter a valid email address.");return v;}
 public static String password(String v){v=required(v,"Password is required.");if(!v.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$"))throw new ValidationException("Password needs 8+ chars, upper, lower and number.");return v;}
 public static String pnr(String v){v=required(v,"PNR is required.");if(!v.matches("\\d{10}"))throw new ValidationException("Enter a valid 10-digit PNR.");return v;}
 public static void bookingDate(LocalDate d){if(d==null||d.isBefore(LocalDate.now()))throw new ValidationException("Service date cannot be before today.");}
 public static int range(int n,int min,int max,String msg){if(n<min||n>max)throw new ValidationException(msg);return n;}
 public static void card(String name,String no,String expiry,String cvv){name=required(name,"Card holder name is required.");if(!no.matches("\\d{16}"))throw new ValidationException("Enter a valid 16-digit card number.");if(!expiry.matches("(0[1-9]|1[0-2])/\\d{2}"))throw new ValidationException("Enter expiry in MM/YY format.");if(!cvv.matches("\\d{3,4}"))throw new ValidationException("Enter a valid CVV.");}
 public static String alphaText(String v,String label,int max){v=required(v,label+" is required.");if(v.length()>max||!v.matches("[A-Za-z ]+"))throw new ValidationException(label+" must contain letters and spaces only.");return v;}
}
