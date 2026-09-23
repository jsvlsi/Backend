package com.ecoaccess.exception;

public final class AppExceptions {
    private AppExceptions() { }
    public static class ApplicationException extends RuntimeException { public ApplicationException(String message){super(message);} public ApplicationException(String message, Throwable cause){super(message,cause);} }
    public static class ValidationException extends ApplicationException { public ValidationException(String m){super(m);} }
    public static class AuthenticationException extends ApplicationException { public AuthenticationException(String m){super(m);} }
    public static class AuthorizationException extends ApplicationException { public AuthorizationException(String m){super(m);} }
    public static class NotFoundException extends ApplicationException { public NotFoundException(String m){super(m);} }
    public static class BusinessRuleException extends ApplicationException { public BusinessRuleException(String m){super(m);} }
    public static class DatabaseException extends ApplicationException { public DatabaseException(String m, Throwable c){super(m,c);} }
}
