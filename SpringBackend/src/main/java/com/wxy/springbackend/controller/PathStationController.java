package com.wxy.springbackend.controller;

import com.wxy.springbackend.model.PathStation;
import com.wxy.springbackend.service.PathStationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PathStationController {
    private final PathStationService pathStationService;
    public PathStationController(PathStationService pathStationService) {
        this.pathStationService = pathStationService;
    }

    @GetMapping("/path_station")
    public ResponseEntity<List<PathStation>> getAllPathStation() {
        List<PathStation> pathStations = pathStationService.getAllPathStation();
        return new ResponseEntity<>(pathStations, HttpStatus.OK);
    }
}
