package com.day28.workshop28.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.day28.workshop28.model.GameReview;
import com.day28.workshop28.repository.ReviewRepo;

@Service
public class ReviewService {
    
    @Autowired 
    private ReviewRepo reviewRepo;
    
    public String getReviewsByGameId(Integer gameId) {
        // extract document from Mongo
        Document gameDoc = reviewRepo.getReviewsById(gameId);
        if (null == gameDoc) {
            return null;
        }
        // parse document to POJO
        GameReview gameReview = GameReview.createFromDoc(gameDoc);
        // send parse POJO to Json builder
        String stringReview = gameReview.toJson().toString();
        // return JsonString
        return stringReview;
    }

    public String getReviewsByRating(String rating) {
        System.out.println("\n ReviewSvc >>> rating detected: " + rating);
        
        if (rating.equalsIgnoreCase("highest")) {
            // send -1 to get highest rating
        } else if (rating.equalsIgnoreCase("lowest")) {
            // send 1 to review to get lowest rating
        } else {
            return null;
        }

        return null;
    }

}
