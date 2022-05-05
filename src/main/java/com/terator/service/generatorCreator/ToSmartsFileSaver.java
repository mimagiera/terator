package com.terator.service.generatorCreator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.openstreetmap.atlas.geography.Location;
import org.openstreetmap.atlas.geography.atlas.Atlas;
import org.openstreetmap.atlas.geography.atlas.items.AtlasEntity;
import org.openstreetmap.atlas.geography.atlas.items.Route;
import org.openstreetmap.atlas.geography.atlas.routing.AStarRouter;
import org.openstreetmap.atlas.utilities.scalars.Distance;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ToSmartsFileSaver {
    public static void saveToXml(Atlas atlas, List<ImmutablePair<Location, Location>> result) throws IOException {
        var routes = result.stream().limit(2)
                .map(pair -> {
                    var houseLocation = pair.getLeft();
                    var churchLocation = pair.getRight();
                    final Route route =
                            AStarRouter.dijkstra(atlas, Distance.MAXIMUM).route(houseLocation, churchLocation);
                    if (route != null) {
                        var isFoot =
                                route.nodes().stream().anyMatch(
                                        n -> n.outEdges().stream().anyMatch(e -> e.containsKey(List.of("highway"))));
                        if (!isFoot)
                            return route.nodes().stream().map(AtlasEntity::getOsmIdentifier)
                                    .collect(Collectors.toList());
                        else return new LinkedList<Long>();
                    } else {
                        return new LinkedList<Long>();
                    }
                }).toList();

        var allVehicles = routes.stream().map(ToSmartsFileSaver::generateSingleVehicleXMLBasedOnNodes).toList();

        var finalSmartsXml = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8"?>
                <data>
                """);

        allVehicles.forEach(finalSmartsXml::append);
        finalSmartsXml.append("</data>\n");
        var totalRes = finalSmartsXml.toString();

        FileWriter fileWriter = new FileWriter(String.format("routes_%d.txt", Instant.now().toEpochMilli()));
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(totalRes);
        printWriter.close();
    }

    static private String generateSingleVehicleXMLBasedOnNodes(List<Long> nodesIds) {
        var result = new StringBuilder(String.format("""
                <vehicle id="%s" type="CAR" start_time="0.0" driverProfile="NORMAL">
                """, UUID.randomUUID()));

        nodesIds.forEach(nodeId -> result.append(String.format("<node id =\"%d\"/>\n", nodeId)));
        result.append("</vehicle>\n");
        return result.toString();
    }

}
