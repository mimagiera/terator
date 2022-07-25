package com.terator.service.generatorCreator;

import com.terator.model.Building;
import com.terator.model.LocationWithMetaSpecificParameter;
import com.terator.model.PointOnMapOnRoad;
import com.terator.service.trajectoryListCreator.LocationExtractor;
import com.terator.service.trajectoryListCreator.SurfaceAreaCalculator;
import org.apache.commons.math3.util.Pair;
import org.openstreetmap.atlas.geography.Latitude;
import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.Longitude;
import org.openstreetmap.atlas.geography.Rectangle;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.items.Edge;
import org.openstreetmap.atlas.utilities.scalars.Distance;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class DataExtractor {

    private static final String BUILDING = "building";
    /**
     * more at https://wiki.openstreetmap.org/wiki/Pl:Key:building
     */
    private static final Set<String> LIVING_BUILDING_TYPES =
            Set.of("apartments", "house", "residential", "detached", "dormitory", "semidetached_house", "hotel");
    private static final Set<String> OFFICE_BUILDING_TYPES = Set.of("office", "warehouse");
    private static final Set<String> SERVICES_BUILDING_TYPES =
            Set.of("retail", "commercial", "shop", "library", "supermarket", "sports_centre", "museum");
    private static final Set<String> SCHOOL_BUILDING_TYPES =
            Set.of("university", "school", "kindergarten", "college");

    public static List<Building> extractLivingBuildings(List<AtlasEntity> entities) {
        return extractBuildings(entities, LIVING_BUILDING_TYPES);
    }

    public static List<Building> extractOfficeBuildings(List<AtlasEntity> entities) {
        return extractBuildings(entities, OFFICE_BUILDING_TYPES);
    }

    public static List<Building> extractServicesBuildings(List<AtlasEntity> entities) {
        return extractBuildings(entities, SERVICES_BUILDING_TYPES);
    }

    public static List<Building> extractSchoolBuildings(List<AtlasEntity> entities) {
        return extractBuildings(entities, SCHOOL_BUILDING_TYPES);
    }

    private static List<Building> extractBuildings(List<AtlasEntity> entities,
                                                   Set<String> possibleBuildingTagValues
    ) {
        return entities.stream()
                .filter(entity -> entity.getOsmTags().containsKey(BUILDING))
                .filter(entity -> possibleBuildingTagValues.contains(entity.getOsmTags().get(BUILDING)))
                .map(atlasEntity -> LocationExtractor.locationOfAtlas(atlasEntity)
                        .map(location -> new Pair<>(location, atlasEntity)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(atlasEntityWithLocation -> new Building(
                        atlasEntityWithLocation.getKey(),
                        SurfaceAreaCalculator.calculateArea(atlasEntityWithLocation.getValue())
                ))
                .collect(Collectors.toList());
    }

    public static List<PointOnMapOnRoad> extractCityEdgePoints(Atlas atlas) {
        // map_kawalek
//        var rectangle = Rectangle.forCorners(
//                new Location(Latitude.degrees(50.0554800), Longitude.degrees(19.9029400)),
//                new Location(Latitude.degrees(50.0567500), Longitude.degrees(19.9056300))
//        );

        //krk_min.osm.pbf
        var rectangle = Rectangle.forCorners(
                new Location(Latitude.degrees(50.0522), Longitude.degrees(19.8861)),
                new Location(Latitude.degrees(50.0768), Longitude.degrees(19.9365))
        );

        final List<PointOnMapOnRoad> pointOnMapOnRoads = StreamSupport
                .stream(atlas.edges().spliterator(), false)
                .map(edge -> locationOnEdge(edge, rectangle).map(location -> new PointOnMapOnRoad(location, edge)))
                .filter(Optional::isPresent)
                .map(Optional::get)

                .toList();

        var a = pointOnMapOnRoads.stream().map(LocationWithMetaSpecificParameter::getLocation)
                .map(location -> location.getLatitude().toString() + "," + location.getLongitude().toString() +
                        ",#00FF00")
                .toList();
        var d = String.join("\n", a);

        return pointOnMapOnRoads;
    }

    private static Optional<Location> locationOnEdge(Edge edge, Rectangle bounds) {
        var insideRectanglePoints = new HashSet<Location>();
        var outsideRectanglePoints = new HashSet<Location>();
        for (Location location : edge.asPolyLine()) {
            if (bounds.fullyGeometricallyEncloses(location)) {
                insideRectanglePoints.add(location);
            } else {
                outsideRectanglePoints.add(location);
            }
        }

        if (insideRectanglePoints.isEmpty() || outsideRectanglePoints.isEmpty()) {
            return Optional.empty();
        } else {
            var insideClosest = insideRectanglePoints.stream().findFirst().get();
            var outsideClosest = outsideRectanglePoints.stream().findAny().get();
            var closestDistance = insideClosest.distanceTo(outsideClosest);
            for (Location insideLocation : insideRectanglePoints) {
                for (Location outsideLocation : outsideRectanglePoints) {
                    final Distance distance = insideLocation.distanceTo(outsideLocation);
                    if (distance.isLessThan(closestDistance)) {
                        outsideClosest = outsideLocation;
                        closestDistance = distance;
                    }
                }
            }
            var locationP = outsideClosest;
            var cx = locationP.getLatitude().toString() + "," + locationP.getLongitude().toString() +
                    ",#ff0000\n";
            var allPoints = edge.asPolyLine().stream()
                    .map(location -> location.getLatitude().toString() + "," + location.getLongitude().toString() +
                            ",#00FF00")
                    .toList();

            var d = String.join("\n", allPoints) + cx;

            return Optional.of(outsideClosest);
        }
    }

}
