package com.knubisoft.babakov;

import com.knubisoft.babakov.dto.ConnectionReadWriteSource;
import com.knubisoft.babakov.dto.DataReadWriteSource;
import com.knubisoft.babakov.dto.FileReadWriteSource;
import com.knubisoft.babakov.entity.Person;
import com.knubisoft.babakov.orm.ORM;
import com.knubisoft.babakov.orm.ORMInterface;
import lombok.SneakyThrows;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class Main {

    private static final ORMInterface ORM = new ORM();

    public static void main(String[] args) throws Exception {
        withConnection(connection -> {
            process(connection);
            return null;
        });
    }
        @SneakyThrows
        private static void process(Connection connection) {
        URL url = Main.class.getClassLoader().getResource("sample.csv");

        List<Person> result;
         result = ORM.readAll(new FileReadWriteSource(new File(url.toURI())), Person.class);
         result.add(new Person("WRITE", BigInteger.ZERO, BigInteger.ZERO, "WRITE", LocalDate.now(), 0F));
         ORM.writeAll(new FileReadWriteSource(new File(url.toURI())), result);

        DataReadWriteSource<ResultSet> rw = new ConnectionReadWriteSource(connection, "person");
        result = ORM.readAll(rw, Person.class);
        result.add(new Person("WRITE", BigInteger.ZERO, BigInteger.ZERO, "WRITE", LocalDate.now(), 0F));
        ORM.writeAll(rw, result);
    }

    @SneakyThrows
    public static void withConnection(Function<Connection, Void> function) {
        try (Connection с = DriverManager.getConnection("jdbc:sqlite:sample.db")) {
            try (Statement stmt = с.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS person " +
                        "(id INTEGER not NULL, " +
                        " name VARCHAR(255), " +
                        " position VARCHAR(255), " +
                        " age INTEGER, " +
                        " PRIMARY KEY ( id ))");

                stmt.executeUpdate("DELETE FROM person");
                for (int index = 0; index < 10; index++) {
                    stmt.executeUpdate("INSERT INTO person (name, position, age) VALUES ('1', '1', 1)");
                }
            }
            function.apply(с);
        }
    }
}