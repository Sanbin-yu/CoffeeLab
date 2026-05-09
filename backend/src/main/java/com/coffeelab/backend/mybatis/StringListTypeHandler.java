package com.coffeelab.backend.mybatis;

import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.List;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(List.class)
public class StringListTypeHandler extends JsonTypeHandler<List<String>> {
    public StringListTypeHandler() {
        super(TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
    }
}
