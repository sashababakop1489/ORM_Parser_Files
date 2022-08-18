package com.knubisoft.babakov.orm;

import com.knubisoft.babakov.dto.ConnectionReadWriteSource;
import com.knubisoft.babakov.dto.DataReadWriteSource;
import com.knubisoft.babakov.dto.FileReadWriteSource;
import com.knubisoft.babakov.entity.BaseEntity;
import com.knubisoft.babakov.filestypesstrategy.*;
import com.knubisoft.babakov.dto.Table;

import lombok.SneakyThrows;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class ORM implements ORMInterface {



    @Override
    public <T> List<T> readAll(DataReadWriteSource<?> inputSource, Class<T> cls) {
        Table table = convertToTable(inputSource);
        return convertTableToListOfClasses(table, cls);
    }

    private Table convertToTable(DataReadWriteSource<?> inputSource) {
        if (inputSource instanceof ConnectionReadWriteSource){
            ConnectionReadWriteSource dataInputSource = (ConnectionReadWriteSource) inputSource;
            return new DatabaseParsingStrategy().parseToTable(dataInputSource);
        } else if (inputSource instanceof FileReadWriteSource){
            FileReadWriteSource fileSource = (FileReadWriteSource) inputSource;
            return getStringParsingStrategy(fileSource).parseToTable(fileSource);
        } else {
            throw new UnsupportedOperationException("Unknown DataInputSource " + inputSource);
        }
    }

    private ParsingStrategy<FileReadWriteSource> getStringParsingStrategy(FileReadWriteSource inputSource){
        String content = inputSource.getContent();
        char firstChar = content.charAt(0);
        switch (firstChar){
            case '{':
            case '[':
                return new JSONParsingStrategy();
            case '<':
                return new XMLParsingStrategy();
            default:
                return new CSVParsingStrategy();
        }
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
    @Override
    public void writeAll(DataReadWriteSource<?> inputSource, List<? extends BaseEntity> list) {
        if (inputSource instanceof ConnectionReadWriteSource dataInputSource){
          //  new DatabaseParsingStrategy().writeToFileOrDB(dataInputSource, list);
        } else if (inputSource instanceof FileReadWriteSource fileSource){
            getStringParsingStrategy(fileSource).writeToFileOrDB(fileSource.getFile(), list);
        } else {
            throw new UnsupportedOperationException("Unknown DataInputSource " + inputSource);
        }
    }
}

