package com.day28.workshop28.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.stereotype.Repository;

import static com.day28.workshop28.Constants.*;

import java.util.List;

@Repository
public class GameRepo {
    
    @Autowired
    MongoTemplate mongoTemplate;

    // b Query for lowest/highest rated review for each game
    // db.comment.aggregate([
    //     {
    //         // rating: -1 gets highest rating (DESC)
    //         // rating: 1 gets lowest rating (ASC)
    //         $sort: {"gid": 1, "rating": -1}
    //     }
    //     ,
    //     {
    //         // group by game id and select first element (highest/lowest)
    //         $group: {
    //             _id: "$gid",
    //             review_id: {$first: "$c_id"},
    //             user: {$first: "$user"},
    //             rating: {$first: "$rating"},
    //             comment: {$first: "$c_text"}
    //         }
    //     }
    //     ,
    //     {
    //         // join game document for each comment from game collection
    //         $lookup: {
    //             from: "game",
    //             foreignField: "gid",
    //             localField: "_id",
    //             as: "game"
    //         }
    //     }
    //     ,
    //     {
    //         // unpack game for easier access
    //         $unwind: "$game"
    //     }
    //     ,
    //     {
    //         // format data for presentation
    //         $project: {
    //             _id: "$_id",
    //             name: "$game.name",
    //             rating: "$rating",
    //             user: "$user",
    //             comment: "$comment",
    //             review_id: "$review_id"
    //         }
    //     }
    // ]);
    public List<Document> getGamesByRating(String rating) {
        Boolean isHighest = rating.equalsIgnoreCase("highest") ? true : false;
        // 1. Operations
        // $sort: {"gid": 1, "rating": -1}
        SortOperation sortByRating;
        if (isHighest) {
            sortByRating = Aggregation
                .sort(Sort.by(Direction.ASC, "gid"))
                .and(Sort.by(Direction.DESC, "rating"));
        } else {
            sortByRating = Aggregation
                .sort(Sort.by(Direction.ASC, "gid"))
                .and(Sort.by(Direction.ASC, "rating"));
        }
        
        // $group: {
        //        _id: "$gid",
        //        review_id: {$first: "$c_id"},
        //        user: {$first: "$user"},
        //        rating: {$first: "$rating"},
        //        comment: {$first: "$c_text"}
        //    }
        GroupOperation groupByGameId = Aggregation.group("gid")
            .first("c_id").as("review_id")
            .first("user").as("user")
            .first("rating").as("rating")
            .first("c_text").as("comment");
        
        // $lookup: {from: "game", foreignField: "gid", localField: "_id", as: "game"}
        LookupOperation lookupGame = Aggregation.lookup(COLLECTION_GAME, "_id", 
            "gid", "game");
        // $unwind: "$game"
        UnwindOperation unwindGame = Aggregation.unwind("game");

        // $project: {
        //     _id: "$_id",
        //     name: "$game.name",
        //     rating: "$rating",
        //     user: "$user",
        //     comment: "$comment",
        //     review_id: "$review_id"
        // }
        ProjectionOperation projectGame = Aggregation.project("rating", "user", "comment", "review_id")
        .and("game.name").as("name");
        
        // TODO REMOVE THIS
        LimitOperation limitBy = Aggregation.limit(10); 
        // 2. Pipeline
        Aggregation pipeline = Aggregation.newAggregation(sortByRating, groupByGameId, 
            lookupGame, unwindGame, projectGame,
            limitBy);

        // 3. MongoTemplate Aggregate
        AggregationResults<Document> results = mongoTemplate.aggregate(
            pipeline, COLLECTION_COMMENT, Document.class);
        
        List<Document> resultList = results.getMappedResults();
        System.out.println("\nGameRepo >>> getGamesByRating(Mapped): " + resultList);

        return resultList;
    }
}
