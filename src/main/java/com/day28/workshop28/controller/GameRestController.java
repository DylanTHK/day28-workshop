package com.day28.workshop28.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.day28.workshop28.service.ReviewService;

@RestController
@RequestMapping
public class GameRestController {
    
    @Autowired
    private ReviewService reviewSvc;
    
    private static final String MESSAGE_BAD_REQUEST = """
            {
                "error type": "BAD REQUEST"
                "error message": "Invalid Input",
            }
        """;

    @GetMapping(path="/game/{gameId}/reviews")
    public ResponseEntity<String> getReviews(@PathVariable Integer gameId) {
        String result = reviewSvc.getReviewsByGameId(gameId);
        // build response entity
        if (null != result) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MESSAGE_BAD_REQUEST);
        }
    }

    @GetMapping(path="/games/{rating}")
    public ResponseEntity<String> getGameByRating(@PathVariable String rating) {
        if (!(rating.equalsIgnoreCase("highest") || rating.equalsIgnoreCase("lowest")))
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(MESSAGE_BAD_REQUEST); 

        String result = reviewSvc.getReviewsByRating(rating);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
}
