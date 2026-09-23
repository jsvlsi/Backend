package com.ecoaccess.model;

import java.time.*; import com.ecoaccess.model.Enums.*;

public class FareQuote {
    private int base; private int tax; private int gross; private int discount; private int payable;
    public FareQuote() { } public FareQuote(int base,int tax,int gross,int discount,int payable){this.base=base;this.tax=tax;this.gross=gross;this.discount=discount;this.payable=payable;}
    public int getBase(){return base;} public void setBase(int value){base=value;} public int getTax(){return tax;} public void setTax(int value){tax=value;} public int getGross(){return gross;} public void setGross(int value){gross=value;} public int getDiscount(){return discount;} public void setDiscount(int value){discount=value;} public int getPayable(){return payable;} public void setPayable(int value){payable=value;}
}
