package com.ecoaccess.util; import java.util.*;
public final class Ids { private Ids(){} public static String next(String prefix){return prefix+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();} public static String coupon(int value){return "EA"+value+"-"+UUID.randomUUID().toString().replace("-","").substring(0,4).toUpperCase();} }
