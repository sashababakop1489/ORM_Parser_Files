package com.knubisoft.babakov.filestypesstrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.knubisoft.babakov.dto.FileReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;


import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JSONParsingStrategy implements ParsingStrategy<FileReadWriteSource> {
    @SneakyThrows
    @Override
    public Table parseToTable(FileReadWriteSource content) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(content.getContent());
        Map<Integer, Map<String, String>> result = buildTable(tree);
        return new Table(result);
    }

    private Map<Integer, Map<String, String>> buildTable(JsonNode tree) {
        Map<Integer, Map<String, String>> map = new LinkedHashMap<>();
        int index = 0;
        for (JsonNode each : tree) {
            Map<String, String> item = buildRow(each);
            map.put(index, item);
            index++;
        }
        return map;
    }

    private Map<String, String> buildRow(JsonNode each) {
        Map<String, String> item = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> itr = each.fields();
        while (itr.hasNext()) {
            Map.Entry<String, JsonNode> next = itr.next();
            item.put(next.getKey(), next.getValue().textValue());
        }
        return item;
    }
    @SneakyThrows
    @Override
    public void writeToFileOrDB(File file, List<? extends BaseEntity> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        String oldStr = FileUtils.readFileToString(new File("src/main/resources/sample.json"), StandardCharsets.UTF_8);
        oldStr = oldStr.substring(0, oldStr.lastIndexOf(']')) + ",";
        objectMapper.registerModule(new JSR310Module());
        String str = objectMapper.writeValueAsString(list);
        str = str.substring(1);


        PrintWriter writer = new PrintWriter("src/main/resources/sample.json");
        writer.print("");
        writer.close();
        try(PrintWriter out = new PrintWriter(new FileWriter("src/main/resources/sample.json", true)))
        {
            out.write(oldStr);
            out.write(str);
        }
    }
}
