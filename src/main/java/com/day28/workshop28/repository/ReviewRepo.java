package com.day28.workshop28.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import static com.day28.workshop28.Constants.*;

@Repository
public class ReviewRepo {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Document getReviewsById(Integer gameId) {
        // Operations
        // $match: {gid:2}
        MatchOperation matchGameId = Aggregation.match(Criteria.where("gid").is(gameId));
        // $project: {
        //         c_id: 1,
        //         gid: 1,
        //         rating: 1,
        //         path: {$concat: ["/review/", "$c_id"]}
        //     }
        ProjectionOperation projectFields = Aggregation.project("gid", "rating")
            .andExpression("concat('/review/',c_id)").as("path")
            .andExclude("_id");
        // $group: {
        //         _id: "$gid",
        //         users_rated: {$sum: 1},
        //         avg_rating: {$avg: "$rating"}, // TODO
        //         paths: {$push: "$path"}
        //     }
        GroupOperation groupByGameId = Aggregation.group("gid")
            .count().as("users_rated")
            .avg("rating").as("avg_rating")
            .push("path").as("paths");
        // $lookup: {
        //         from: "game",
        //         foreignField: "gid", // (db.game)
        //         localField: "_id", // (db.comment)
        //         as: "game"
        //     }
        LookupOperation lookupGame = Aggregation.lookup(COLLECTION_GAME, "_id", "gid", "game");
        // $unwind: "$game"
        UnwindOperation unwindGame = Aggregation.unwind("game");
        // $project: {
        //         _id: 0,
        //         game_id: "$game.gid",
        //         name: "$game.name",
        //         year: "$game.year",
        //         rank: "$game.ranking",
        //         average: "$avg_rating",
        //         users_rated: "$users_rated",
        //         url: "$game.url",
        //         thumbnail: "$game.image",
        //         reviews: "$paths",
        //         timestamp: "$$NOW"
        //     }
        ProjectionOperation projectPresent = Aggregation.project("avg_rating", "users_rated")
            .andExpression("_id").as("game_id")
            .andExpression("game.name").as("name")
            .andExpression("game.year").as("year")
            .andExpression("game.ranking").as("rank")
            .andExpression("game.url").as("url")
            .andExpression("game.image").as("thumbnail")
            .andExpression("paths").as("reviews")
            .andExpression("{$toString: '$$NOW'}").as("timestamp")
            .andExclude("_id");

        // create pipeline (to hold all the aggregation operations)
        Aggregation pipeline = Aggregation.newAggregation(matchGameId, projectFields, groupByGameId, lookupGame, unwindGame
        , projectPresent);

        // apply pipeline to mongoTemplate
        AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, COLLECTION_COMMENT, Document.class);
        
        // getUniqueMappedResults OR doc.get(0) works
        List<Document> doc = results.getMappedResults();
        Document firstDoc = results.getUniqueMappedResult();
        // System.out.println("\nReviewRepo >>> Extracted Document: " + doc);
        System.out.println("\nReviewRepo >>> Extracted Document(First): " + firstDoc);

        return firstDoc;
    }

}
