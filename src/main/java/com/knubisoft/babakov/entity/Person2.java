package com.knubisoft.babakov.entity;

import com.knubisoft.babakov.annotation.TableAnnotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableAnnotation(value = "person")
public class Person2 extends BaseEntity {
    private String name;
    private BigInteger age;
}
