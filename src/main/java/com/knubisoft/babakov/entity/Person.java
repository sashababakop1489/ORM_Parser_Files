package com.knubisoft.babakov.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Table
//@Entity
public class Person extends BaseEntity {
    private String name;
    private BigInteger age;
    private BigInteger salary;
    private String position;
    private LocalDate dateOfBirth;
    private Float xxx;

    @Override
    public String toString() {
        return "Person{" +
                "name=" + name +
                ", age=" + age +
                ", salary=" + salary +
                ", position=" + position +
                ", dateOfBirth=" + dateOfBirth +
                ", xxx=" + xxx +
                '}';
    }
}
