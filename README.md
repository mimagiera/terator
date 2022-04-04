# trajectory-generator

Used library: https://github.com/osmlab/atlas

Each node contains tags. See: https://wiki.openstreetmap.org/wiki/Map_features

Sample tags:
```
{natural=saddle, name=Collada de Meners, mountain_pass=yes}
{parking=underground, amenity=parking, fee=yes, name=Aparcament Vinyes i Prat de la Creu, layer=-1}
{motorcycle=yes, barrier=lift_gate, bicycle=no, motorcar=yes, foot=no}
{highway=crossing, crossing=marked}
```

Swagger available at: http://localhost:8080/swagger-ui/


Convert osm to osm.pbf:
https://wiki.openstreetmap.org/wiki/Osmconvert

` .\osmconvert64-0.8.8p.exe map.osm -o="map.osm.pbf"`