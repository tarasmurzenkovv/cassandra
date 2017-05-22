package com.dataart.tmurzenkov.cassandra.service.impl.json;

import com.datastax.driver.core.LocalDate;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Date;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;


/**
 * Data stax {@link LocalDate} custom deserializer.
 *
 * @author tmurzenkov
 */
public class DateDeserializer extends StdDeserializer<Date> {
    /**
     * Constructor.
     */
    protected DateDeserializer() {
        super(Date.class);
    }

    /**
     * Actual logic.
     *
     * @param jsonParser
     * @param deserializationContext
     * @return
     * @throws IOException
     */
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String valueAsString = jsonParser.getValueAsString();
        return new Date(parse(valueAsString, ofPattern("dd-MM-yyyy")).toEpochDay());
    }
}
