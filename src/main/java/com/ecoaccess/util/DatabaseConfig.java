package com.ecoaccess.util;
/** Local PostgreSQL settings. Change these values for your installation. */
public final class DatabaseConfig { private DatabaseConfig(){} public static final String URL=System.getenv().getOrDefault("ECOACCESS_DB_URL","jdbc:postgresql://localhost:5432/ecoaccess"); public static final String USER=System.getenv().getOrDefault("ECOACCESS_DB_USER","postgres"); public static final String PASSWORD=System.getenv().getOrDefault("ECOACCESS_DB_PASSWORD","postgres"); }
