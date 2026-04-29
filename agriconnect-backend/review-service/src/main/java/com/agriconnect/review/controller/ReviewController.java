package com.agriconnect.review.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @GetMapping
    public String getReviews() {
        return "Reviews will be listed here.";
    }
}
