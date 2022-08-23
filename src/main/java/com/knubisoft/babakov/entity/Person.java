package com.knubisoft.babakov.entity;

import com.knubisoft.babakov.annotation.TableAnnotation;
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
@TableAnnotation(value = "person")
public class Person extends BaseEntity {
    private String name;
    private BigInteger age;
    private BigInteger salary;
    private String position;
    private LocalDate dateOfBirth;
    private Float xxx;
}
