package com.day28.workshop28.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.day28.workshop28.model.Game;
import com.day28.workshop28.model.GameReview;
import com.day28.workshop28.repository.GameRepo;
import com.day28.workshop28.repository.ReviewRepo;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Service
public class ReviewService {
    
    @Autowired 
    private ReviewRepo reviewRepo;

    @Autowired
    private GameRepo gameRepo;
    
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
        List<Document> listDoc;
        if (rating.equalsIgnoreCase("highest") || rating.equalsIgnoreCase("lowest")) {
            // send -1 to get highest rating
            listDoc = gameRepo.getGamesByRating(rating);
        } else {
            return null;
        }
        // Doc -> POJO
        List<Game> listGame = new LinkedList<>();
        for (Document d : listDoc) {
            listGame.add(Game.docToGame(d));
        }
        System.out.println("\nReviewSvc >>> Game List: " + listGame);

        // POJO -> Json
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Game g : listGame) {
            arrayBuilder.add(g.toJson());
        }

        // add json objects to array builder
        // build main jsonobject (rating, games, timestamp)
        JsonObject mainObject = Json.createObjectBuilder()
            .add("rating", rating)
            .add("games", arrayBuilder)
            .add("timestamp", Timestamp.from(Instant.now()).toString())
            .build();

        // returning value for controller
        return mainObject.toString();
    }

}
