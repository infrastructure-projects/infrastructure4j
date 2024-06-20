package com.reopenai.infrastructure4j.core.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDateTime的序列化实例
 *
 * @author Allen Huang
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Instant instant = localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant();
        try {
            long timestamp = instant.toEpochMilli();
            jsonGenerator.writeNumber(timestamp);
        } catch (ArithmeticException e) {
            BigInteger seconds = BigInteger.valueOf(instant.getEpochSecond());
            BigInteger nanos = BigInteger.valueOf(instant.getNano());
            BigInteger combined = seconds.multiply(BigInteger.valueOf(1_000_000_000)).add(nanos);
            if (BigInteger.ZERO.compareTo(combined) >= 0) {
                jsonGenerator.writeNumber(0);
            } else {
                jsonGenerator.writeNumber(Long.MAX_VALUE);
            }
        }
    }

}