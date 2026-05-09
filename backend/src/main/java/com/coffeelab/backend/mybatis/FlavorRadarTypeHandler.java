package com.coffeelab.backend.mybatis;

import com.coffeelab.backend.model.FlavorRadar;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(FlavorRadar.class)
public class FlavorRadarTypeHandler extends JsonTypeHandler<FlavorRadar> {
    public FlavorRadarTypeHandler() {
        super(TypeFactory.defaultInstance().constructType(FlavorRadar.class));
    }
}
