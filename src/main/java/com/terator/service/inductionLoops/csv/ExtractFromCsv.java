package com.terator.service.inductionLoops.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
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
        URL resource = ExtractFromCsv.class.getClassLoader().getResource(fileName);
        try {
            File file = Paths.get(resource.toURI()).toFile();
            return new CsvToBeanBuilder<TableType>(new FileReader(file))
                    .withType(springGenericType)
                    .build()
                    .parse();
        } catch (FileNotFoundException | URISyntaxException e) {
            LOGGER.error("Cannot read from csv", e);
            return List.of();
        }
    }
}
