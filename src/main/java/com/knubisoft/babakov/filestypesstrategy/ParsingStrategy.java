package com.knubisoft.babakov.filestypesstrategy;

import com.knubisoft.babakov.dto.DataReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;

import java.io.File;
import java.util.List;

public interface ParsingStrategy<T extends DataReadWriteSource> {

    Table parseToTable(T content);

    void writeToFileOrDB(File file, List<? extends BaseEntity> list);

}
