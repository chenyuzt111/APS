package com.benewake.system.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface ApsFileService {
    ResponseEntity<InputStreamResource> ApsIntegrityCheckeFile();
}
