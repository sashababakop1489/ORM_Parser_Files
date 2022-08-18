package com.knubisoft.babakov.dto;

import com.knubisoft.babakov.dto.DataReadWriteSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Getter
public final class FileReadWriteSource implements DataReadWriteSource<String> {

    private final File source;

    @Override
    @SneakyThrows
    public String getContent() {
        return FileUtils.readFileToString(source, StandardCharsets.UTF_8);
    }

    public File getFile(){
        return source;
    }
}
