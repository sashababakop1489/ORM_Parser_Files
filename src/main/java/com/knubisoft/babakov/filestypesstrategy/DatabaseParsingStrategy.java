package com.knubisoft.babakov.filestypesstrategy;

import com.knubisoft.babakov.dto.ConnectionReadWriteSource;
import com.knubisoft.babakov.dto.FileReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;
import lombok.SneakyThrows;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseParsingStrategy implements ParsingStrategy<ConnectionReadWriteSource>{
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
    @Override
    public void writeToFileOrDB(File file, List<? extends BaseEntity> list) {

    }
}
