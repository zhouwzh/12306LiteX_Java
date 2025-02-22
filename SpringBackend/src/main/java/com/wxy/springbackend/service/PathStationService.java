package com.wxy.springbackend.service;

import com.wxy.springbackend.model.PathStation;
import com.wxy.springbackend.repository.PathStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathStationService {
    private final PathStationRepository pathStationRepository;

    public PathStationService(PathStationRepository pathStationRepository) {
        this.pathStationRepository = pathStationRepository;
    }

    public List<PathStation> getAllPathStation(){
        return pathStationRepository.getAllPathStation();
    }
}
