package com.terator.generator;

import com.terator.model.Location;
import com.terator.model.SingleTrajectory;
import com.terator.parser.ToAtlasParser;
import org.openstreetmap.atlas.geography.atlas.items.Node;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Iterators.limit;

@Service
public class TrajectoriesGenerator {

    private final ToAtlasParser toAtlasParser;

    public TrajectoriesGenerator(ToAtlasParser toAtlasParser) {
        this.toAtlasParser = toAtlasParser;
    }

    List<SingleTrajectory> createTrajectoriesForFile(String fileName) {
        var atlas = toAtlasParser.parse(fileName);
        var nodes = atlas.nodes().iterator();
        return trajectoriesFromNodes(nodes);
    }

    private List<SingleTrajectory> trajectoriesFromNodes(Iterator<Node> nodes) {
        Instant instant = Instant.now();
        var result = new LinkedList<SingleTrajectory>();
        limit(nodes, 10).forEachRemaining(node -> {
            var l = node.getLocation();
            System.out.printf("Osm args: %s%n", node.getOsmTags());
            Location startLocation = new Location(l.getLongitude().toString(), l.getLatitude().toString());
            result.add(new SingleTrajectory(startLocation, startLocation, instant));
        });

        return result;
    }
}
