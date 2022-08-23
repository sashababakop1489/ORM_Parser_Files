package com.knubisoft.babakov.orm;

import com.knubisoft.babakov.dto.DataReadWriteSource;
import com.knubisoft.babakov.entity.BaseEntity;
import java.util.List;

public interface ORMInterface {

    <T> List<T> readAll(DataReadWriteSource<?> source, Class<T> cls);

    void writeAll(DataReadWriteSource<?> source, List<? extends BaseEntity> result);
}
