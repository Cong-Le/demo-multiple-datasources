package com.example.demo_multiple_datasources.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class UuidGenerator {
    private static final TimeBasedGenerator v1Generator = Generators.timeBasedGenerator();
    
    public static String generateModelId() {
        return v1Generator.generate().toString();
    }
}
