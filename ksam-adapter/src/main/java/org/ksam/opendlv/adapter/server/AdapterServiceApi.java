package org.ksam.opendlv.adapter.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdapterServiceApi {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    @PostMapping("/{systemId}/adapt")
    public ResponseEntity<String> createMonDataEntry(@PathVariable("systemId") String systemId,
	    @RequestBody String monAdaptation) {
	if (Application.adapter.getReplayer().adaptMonitors(monAdaptation)) {
	    return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
