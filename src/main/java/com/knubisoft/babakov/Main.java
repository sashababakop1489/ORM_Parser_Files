package com.knubisoft.babakov;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        URL url = Main.class.getClassLoader().getResource("sample.xml");
        List<Person2> result = new ORM().transform(new File(url.toURI()), Person2.class);
    }

}