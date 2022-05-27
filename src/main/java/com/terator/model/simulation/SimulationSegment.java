package com.terator.model.simulation;

import org.openstreetmap.atlas.geography.atlas.items.Node;

public record SimulationSegment(
        Node startNode,
        Node endNode
) {
}
