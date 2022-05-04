package com.terator.service.osmImporter;

import com.terator.model.City;

public interface OsmImporter {

    City importData(String osmData);

}
