package com.knubisoft.babakov.filestypesstrategy;

import com.knubisoft.babakov.annotation.TableAnnotation;
import com.knubisoft.babakov.dto.ConnectionReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseParsingStrategy implements ParsingStrategy<ConnectionReadWriteSource>{

    private List<String> metaInfo = null;

    @Override
    public Table parseToTable(ConnectionReadWriteSource content) {
        ResultSet rs = content.getContent();
        Map<Integer, Map<String, String>> result = buildTable(rs);
        return new Table(result);
    }

    @SneakyThrows
    private Map<Integer, Map<String, String>> buildTable(ResultSet rs) {
        ResultSetMetaData metaData = rs.getMetaData();

        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        int rowId = 0;
        while (rs.next()){
            Map<String, String> row = new LinkedHashMap<>();
            for (int index = 1; index < metaData.getColumnCount(); index++) {
                row.put(metaData.getColumnName(index), rs.getString(index));
            }
            result.put(rowId, row);
            rowId++;
        }
        return result;
    }
    @SneakyThrows
    @Override
    public void writeToDB(ConnectionReadWriteSource source, List<? extends BaseEntity> list) {
        metaInfo = buildMetaList(source);
        try (Statement statement = source.getSource().createStatement()) {
            statement.executeUpdate("DELETE FROM " + getTableName(list.get(0).getClass()));
        }
        for (Object o : list) {
            String preparedStringStatement = buildPreparedStatement(o);
            PreparedStatement preparedStatement = source.getSource().prepareStatement(preparedStringStatement);
            bindArguments(preparedStatement, o);
            preparedStatement.execute();
        }
    }

    @SneakyThrows
    private void bindArguments(PreparedStatement preparedStatement, Object o) {
        int index = 1;
        for (Field f : o.getClass().getDeclaredFields()) {
            if (metaInfo.contains(f.getName())) {
                f.setAccessible(true);
                preparedStatement.setObject(index, f.get(o));
                index++;
            }
        }
    }

    private List<String> buildMetaList(ConnectionReadWriteSource source) throws SQLException {
        ResultSetMetaData metaData = source.getContent().getMetaData();
        List<String> columnNames = new ArrayList<>();
        for (int index = 1; index <= metaData.getColumnCount(); index++) {
            columnNames.add(metaData.getColumnLabel(index));
        }
        return columnNames;
    }

    @SneakyThrows
    public String buildPreparedStatement(Object o) {
        Class<?> clazz = o.getClass();
        String tableName = getTableName(clazz);
        String fields = getFields(clazz);
        String values = getValues(clazz);
        return String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, fields, values);

    }

    private String getValues(Class<?> clazz) {
        List<String> fieldNames = streamOfMatchingFields(clazz)
                .map(field -> "?")
                .collect(Collectors.toList());
        return String.join(",", fieldNames);
    }

    private String getFields(Class<?> clazz) {
        List<String> fieldNames = streamOfMatchingFields(clazz)
                .collect(Collectors.toList());
        return String.join(",", fieldNames);
    }

    private Stream<String> streamOfMatchingFields(Class<?> clazz) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        return fields.stream()
                .map(Field::getName)
                .filter(metaInfo::contains);
    }

    private String getTableName(Class<?> clazz) {
        return clazz.getAnnotation(TableAnnotation.class).value();
    }
}
