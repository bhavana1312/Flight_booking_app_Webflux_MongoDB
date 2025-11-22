package com.flightapp.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document
public class Airline{
 @Id
 public String id;
 public String name;
 public String code;
 public String logoUrl;
 public Airline(){}
 public Airline(String name,String code,String logoUrl){this.name=name;this.code=code;this.logoUrl=logoUrl;}
}
