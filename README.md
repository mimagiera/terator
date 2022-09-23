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

Convert osm to osm.pbf:
https://wiki.openstreetmap.org/wiki/Osmconvert

` .\osmconvert64-0.8.8p.exe map_kawalek.osm -o="map_kawalek.osm.pbf"`

export
`.\osmosis-0.48.3\bin\osmosis --read-pbf .\malopolskie-latest.osm.pbf --bounding-box top=50.0768 left=19.8861 
bottom=50.0522 right=19.9365 completeWays=yes --write-pbf krk_min.osm.pbf`

To run whole generator with optimization: run class `ApplicationWithOptimization`. To run application with comparing 
data to single days with parameters from [src/main/resources/var.tsv](src/main/resources/var.tsv) run class 
`ApplicationWithCheckingDays`.

To build single jar file run gradle task `shadowJar`. Its main class (`ApplicationWithOptimization` or 
`ApplicationWithCheckingDays`) can be specified in [build.gradle](build.gradle) file in:
```
jar {
    manifest {
        attributes "Main-Class": "com.terator.ApplicationWithCheckingDays"
    }
}
```

If using different map then `krk_min.osm.pbf` you need to specify its corners coordinates (i was unable to extract 
it from map file). It can be specified in: `com.terator.service.generatorCreator.DataExtractor.extractCityEdgePoints`