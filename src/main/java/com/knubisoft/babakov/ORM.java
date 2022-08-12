package com.knubisoft.babakov;

import com.knubisoft.babakov.filestipesstrategy.CSVParsingStrategy;
import com.knubisoft.babakov.filestipesstrategy.JSONParsingStrategy;
import com.knubisoft.babakov.filestipesstrategy.ParsingStrategy;
import com.knubisoft.babakov.filestipesstrategy.XMLParsingStrategy;
import com.knubisoft.babakov.table.Table;
import com.knubisoft.babakov.type.ContentType;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class ORM {
    @SneakyThrows
    public <T> List<T> transform(File file, Class<T> cls) {
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        ContentType contentType = guessContentTypeByContent(content);
        ParsingStrategy parsingStrategy = createStrategyByContentType(contentType);

        Table table = parsingStrategy.parseToTable(content);
        return convertTableToListOfClasses(table, cls);
    }

    private <T> List<T> convertTableToListOfClasses(Table table, Class<T> cls) {
        List<T> result = new ArrayList<>();
        for (int index = 0; index < table.size(); index++) {
            Map<String, String> row = table.getTableRowByIndex(index);
            T instance = reflectTableRowToClass(row, cls);
            result.add(instance);
        }
        return result;
    }

    @SneakyThrows
    private <T> T reflectTableRowToClass(Map<String, String> row, Class<T> cls) {
        T instance = cls.getDeclaredConstructor().newInstance();
        for (Field each: cls.getDeclaredFields()) {
            each.setAccessible(true);
            String value = row.get(each.getName());
            if (value != null) {
                each.set(instance, transformValueToFieldType(each, value));
            }
        }
        return instance;
    }

    private static Object transformValueToFieldType(Field field, String value) {
        Map<Class<?>, Function<String, Object>> typeToFunction = new LinkedHashMap<>();
        typeToFunction.put(String.class, s -> s);
        typeToFunction.put(int.class, Integer::parseInt);
        typeToFunction.put(Float.class, Float::parseFloat);
        typeToFunction.put(LocalDate.class, LocalDate::parse);
        typeToFunction.put(LocalDateTime.class, LocalDate::parse);
        typeToFunction.put(Long.class, Long::parseLong);
        typeToFunction.put(BigInteger.class, BigInteger::new);

        return typeToFunction.getOrDefault(field.getType(), type -> {
            throw new UnsupportedOperationException("Type is not supported by parser " + type);
        }).apply(value);
    }

    private ParsingStrategy createStrategyByContentType(ContentType contentType) {
        switch (contentType) {
            case JSON:
                return new JSONParsingStrategy();
            case XML:
                return new XMLParsingStrategy();
            case CSV:
                return new CSVParsingStrategy();
            default:
                throw new UnsupportedOperationException("Unknown strategy " + contentType);
        }
    }

    private ContentType guessContentTypeByContent(String content) {
        char firstChar = content.charAt(0);
        switch (firstChar) {
            case '{':
            case '[':
                return ContentType.JSON;
            case '<':
                return ContentType.XML;
            default:
                return ContentType.CSV;
        }
    }
}
