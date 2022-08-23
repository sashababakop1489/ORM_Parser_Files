package com.knubisoft.babakov.filestypesstrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knubisoft.babakov.dto.FileReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;
import lombok.SneakyThrows;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XMLParsingStrategy implements ParsingStrategy<FileReadWriteSource> {
    @SneakyThrows
    @Override
    public Table parseToTable(FileReadWriteSource content) {
        // TODO
        XmlMapper xmpMapper = new XmlMapper();
        JsonNode tree = xmpMapper.readTree(content.getContent());
        Map<Integer, Map<String, String>> result = buildTable(tree);
        return new Table(result);
    }

    private Map<Integer, Map<String, String>> buildTable(JsonNode tree) {
        Map<Integer, Map<String, String>> map = new LinkedHashMap<>();
        int index = 0;
        for (JsonNode each : tree) {
            for (JsonNode f : each) {
                Map<String, String> item = buildRow(f);
                map.put(index, item);
                index++;
            }
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
    public void writeToFile(FileReadWriteSource file, List<? extends BaseEntity> list) {

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        String xml = xmlMapper.writeValueAsString(list);
        xml = xml.replaceAll("ArrayList", "root");
        Files.write(Path.of("src/main/resources/" + file.getSource().getName()), xml.getBytes());
    }
}
