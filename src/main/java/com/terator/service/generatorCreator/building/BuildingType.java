package com.terator.service.generatorCreator.building;

import com.terator.model.generatorTable.FromBuildingTypeGenerator;

public enum BuildingType {
    HOUSE(new FromHouseStrategy()),
    OFFICE(new FromOfficeStrategy());

    private final FromBuildingTypeStrategy fromBuildingTypeStrategy;

    BuildingType(FromBuildingTypeStrategy fromBuildingTypeStrategy) {
        this.fromBuildingTypeStrategy = fromBuildingTypeStrategy;
    }

    public FromBuildingTypeGenerator getFromBuildingTypeGenerator() {
        return fromBuildingTypeStrategy.createGenerator();
    }
}
