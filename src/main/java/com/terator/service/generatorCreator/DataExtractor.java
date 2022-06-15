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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class DataExtractor {

    private static final String BUILDING = "building";
    /**
     * more at https://wiki.openstreetmap.org/wiki/Pl:Key:building
     */
    private static final Set<String> LIVING_BUILDING_TYPES = Set.of("apartments", "house");
    private static final Set<String> OFFICE_BUILDING_TYPES = Set.of("office");

    public static List<Building> extractLivingPlaces(List<AtlasEntity> entities) {
        return extractBuildings(entities, entity -> {
            var buildingValue = entity.getOsmTags().get(BUILDING);
            return buildingValue != null && LIVING_BUILDING_TYPES.contains(buildingValue);
        });
    }

    public static List<Building> extractOfficePlaces(List<AtlasEntity> entities) {
        return extractBuildings(entities, entity -> {
            var buildingValue = entity.getOsmTags().get(BUILDING);
            return buildingValue != null && OFFICE_BUILDING_TYPES.contains(buildingValue);
        });
    }

    public static List<PointOnMapOnRoad> extractCityEdgePoints(Atlas atlas) {
        // map_kawalek
//        var rectangle = Rectangle.forCorners(
//                new Location(Latitude.degrees(50.0554800), Longitude.degrees(19.9029400)),
//                new Location(Latitude.degrees(50.0567500), Longitude.degrees(19.9056300))
//        );

        //map_czarnowiejska
        var rectangle = Rectangle.forCorners(
                new Location(Latitude.degrees(50.0620000), Longitude.degrees(19.9123000)),
                new Location(Latitude.degrees(50.0700000), Longitude.degrees(19.9269000))
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

    private static List<Building> extractBuildings(List<AtlasEntity> entities,
                                                   Predicate<AtlasEntity> additionalPredicate
    ) {
        return entities.stream()
                .filter(entity -> entity.getOsmTags().containsKey(BUILDING))
                .filter(additionalPredicate)
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


}
