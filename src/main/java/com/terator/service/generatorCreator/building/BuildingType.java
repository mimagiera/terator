package com.terator.service.generatorCreator.building;

import com.terator.model.City;
import com.terator.model.generatorTable.FromBuildingTypeGenerator;
import com.terator.service.generatorCreator.DataExtractor;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;

import java.util.List;
import java.util.function.Function;

public enum BuildingType {
    HOUSE(new FromHouseStrategy(), city -> DataExtractor.extractLivingPlaces(city.entities())),
    OFFICE(new FromOfficeStrategy(), city -> DataExtractor.extractOfficePlaces(city.entities()));

    private final FromBuildingTypeStrategy fromBuildingTypeStrategy;
    private final Function<City, List<AtlasEntity>> entitiesProvider;

    BuildingType(FromBuildingTypeStrategy fromBuildingTypeStrategy,
                 Function<City, List<AtlasEntity>> entitiesProvider
    ) {
        this.fromBuildingTypeStrategy = fromBuildingTypeStrategy;
        this.entitiesProvider = entitiesProvider;
    }

    public FromBuildingTypeGenerator getFromBuildingTypeGenerator() {
        return fromBuildingTypeStrategy.createGenerator();
    }

    public Function<City, List<AtlasEntity>> getEntitiesProvider() {
        return entitiesProvider;
    }
}
