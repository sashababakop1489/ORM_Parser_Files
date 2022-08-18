package com.knubisoft.babakov.filestypesstrategy;

import com.knubisoft.babakov.dto.FileReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;

public class CSVParsingStrategy implements ParsingStrategy<FileReadWriteSource> {

    public static final String DELIMITER = ",";
    public static final String COMMENT = "--";

    @Override
    public Table parseToTable(FileReadWriteSource content) {
        List<String> lines = Arrays.asList(content.getContent().split(System.lineSeparator()));
        Map<Integer, String> mapping = buildMapping(lines.get(0));
        Map<Integer, Map<String, String>> result = buildTable(lines.subList(1, lines.size()), mapping);
        return new Table(result);
    }

    private Map<Integer, Map<String, String>> buildTable(List<String> lines, Map<Integer, String> mapping) {
        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            result.put(index, buildRow(mapping, line));
        }
        return result;
    }

    private Map<String, String> buildRow(Map<Integer, String> mapping, String line) {
        Map<String, String> nameToValueMap = new LinkedHashMap<>();
        String[] rowItems = splitLine(line);
        for (int rowIndex = 0; rowIndex < rowItems.length; rowIndex++) {
            String value = rowItems[rowIndex];
            nameToValueMap.put(mapping.get(rowIndex), value);
        }
        return nameToValueMap;
    }

    private Map<Integer, String> buildMapping(String firstLine) {
        Map<Integer, String> map = new LinkedHashMap<>();
        String[] array = splitLine(firstLine);
        for (int index = 0; index < array.length; index++) {
            String value = array[index];
            if (value.contains(COMMENT)) {
                value = value.split(COMMENT)[0];
            }
            map.put(index, value.trim());
        }
        return map;
    }

    private static String[] splitLine(String line) {
        return line.split(DELIMITER);
    }

    @SneakyThrows
    public  void writeToFileOrDB(File file, List<? extends BaseEntity> list) {
        String str = list.get(list.size() - 1).toString().replace('}', ' ');
        String[] strArr = str.split(",");
        String ans = "";
        for (String s : strArr)
            ans = ans + s.split("=")[1] + ",";
        ans = ans.substring(0, ans.lastIndexOf(",")).trim();

        FileWriter writer = new FileWriter("src/main/resources/" + file.getName(), true);
        writer.write("\n" + ans);
        writer.close();
    }
}
