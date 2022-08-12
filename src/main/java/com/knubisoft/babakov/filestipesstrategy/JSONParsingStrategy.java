package com.knubisoft.babakov.filestipesstrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knubisoft.babakov.table.Table;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JSONParsingStrategy implements ParsingStrategy {
    @SneakyThrows
    @Override
    public Table parseToTable(String content) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(content);
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
}
