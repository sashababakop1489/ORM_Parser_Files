package com.knubisoft.babakov.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Table
//@Entity
public class Person2 extends BaseEntity {
    private String name;
    private BigInteger age;
}
