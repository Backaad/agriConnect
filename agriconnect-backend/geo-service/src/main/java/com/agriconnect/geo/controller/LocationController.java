package com.agriconnect.geo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geo/locations")
public class LocationController {

    @GetMapping
    public String getLocationStatus() {
        return "Geo Service is running";
    }

    // Future endpoints for spatial queries will be added here
}
