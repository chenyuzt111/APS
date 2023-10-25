package com.benewake.system.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ApsFileService {
    ResponseEntity<Resource> ApsIntegrityCheckeFile();
}
