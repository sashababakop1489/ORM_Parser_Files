package com.knubisoft.babakov.filestipesstrategy;

import com.knubisoft.babakov.table.Table;

public interface ParsingStrategy {

    Table parseToTable(String content);

}
