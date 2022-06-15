package com.terator.service.generatorCreator.building;

import com.terator.model.City;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.generatorTable.FromBuildingTypeGenerator;
import com.terator.service.generatorCreator.DataExtractor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public enum BuildingType {
    HOUSE(new FromHouseStrategy(), city -> DataExtractor.extractLivingPlaces(city.entities())),
    OFFICE(new FromOfficeStrategy(), city -> DataExtractor.extractOfficePlaces(city.entities())),
    CITY_EDGE_POINT(new FromCityEdgePointStrategy(), city -> DataExtractor.extractCityEdgePoints(city.atlas()));

    private final FromBuildingTypeStrategy fromBuildingTypeStrategy;

    @Getter
    private final Function<City, List<? extends LocationWithMetaSpecificParameter>> entitiesProvider;

    public FromBuildingTypeGenerator getFromBuildingTypeGenerator() {
        return fromBuildingTypeStrategy.createGenerator();
    }
}
