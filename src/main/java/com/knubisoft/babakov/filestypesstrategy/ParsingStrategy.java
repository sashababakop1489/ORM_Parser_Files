package com.knubisoft.babakov.filestypesstrategy;

import com.knubisoft.babakov.dto.DataReadWriteSource;
import com.knubisoft.babakov.dto.Table;
import com.knubisoft.babakov.entity.BaseEntity;

import java.util.List;

public interface ParsingStrategy<T extends DataReadWriteSource> {

    Table parseToTable(T content);

    default void writeToFile(T file, List<? extends BaseEntity> list) { }

    default void writeToDB(T file, List<? extends BaseEntity> list) { }
}
