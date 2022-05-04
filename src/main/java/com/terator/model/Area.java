package com.terator.model;

public record Area(
        Location topLeft,
        Location topRight,
        Location bottomLeft,
        Location bottomRight
) {
}
