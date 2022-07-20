package com.terator.service.inductionLoops.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public abstract class ExtractFromCsv<TableType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractFromCsv.class);
    private final String fileName;
    private final Class<TableType> springGenericType;

    protected ExtractFromCsv(String fileName) {
        this.fileName = fileName;
        this.springGenericType = (Class<TableType>) GenericTypeResolver.resolveTypeArgument(getClass(), ExtractFromCsv.class);
    }

    public List<TableType> findAll() {
        var resource = ExtractFromCsv.class.getClassLoader()
                .getResourceAsStream(fileName);
        try (Reader targetReader = new InputStreamReader(resource)) {
            return new CsvToBeanBuilder<TableType>(targetReader)
                    .withType(springGenericType)
                    .build()
                    .parse();
        } catch (IOException e) {
            LOGGER.error("Cannot read from csv", e);
            return List.of();
        }
    }
}
