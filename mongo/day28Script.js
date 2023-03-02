use bgg;

//db.game.find();
//db.comment.find();

// a1) Projection for DragonMaster game
//db.game.aggregate([
//    {
//        $match: {gid:2}
//    }
//    ,
//    {
//        $project: {
//            gid:1, 
//            name:1, 
//            year:1, 
//            ranking:1, 
//            users_rated:1, 
//            url:1, 
//            image:1,
//            timestamp: "$$NOW"}
//    }
//
//]);

// a2) average rating of comments by gid
//db.comment.aggregate([
//    {$match: {gid:2}}
//    ,
//    {
//        $group: {
//            _id: "$gid", // group by gid
//            users_rated: {$sum: 1}, // +1 for every game
//            average_rating: {$avg: "$rating"},
//            reviews: {$push: {$concat: ["/review/", {$toString: "$c_id"}]}},
//        }
//    }
//    ,
//    {
//        $lookup: {
//            from: "game",
//            foreignField: "gid",
//            localField: "gid",
//            as: "game"
//        }
//    }
//    
//]);

// a) query for reviews details by game_id
db.comment.aggregate([
    {
        $match: {gid:2}   
    }
    ,
    {
        // build a list of documents w gid: 2
        $project: {
            c_id: 1,
            gid: 1,
            rating: 1,
            path: {$concat: ["/review/", "$c_id"]}
        }
    }
    ,
    {
        // group paths together as array of paths
        // calculate avg rating and comment count
        $group: {
            _id: "$gid",
            users_rated: {$sum: 1},
            avg_rating: {$avg: "$rating"},
            paths: {$push: "$path"}
        }
    }
    ,
    {
        // add game document with matching gid
        $lookup: {
            from: "game",
            foreignField: "gid", // (db.game)
            localField: "_id", // (db.comment)
            as: "game"
        }
    }
    ,
    {
        // unpack array of games (single game due to match)
        $unwind: "$game"
    }
    ,
    {
        // format results for output (return single document)
        $project: {
            _id: 0,
            game_id: "$game.gid",
            name: "$game.name",
            year: "$game.year",
            rank: "$game.ranking",
            average: "$avg_rating",
            users_rated: "$users_rated",
            url: "$game.url",
            thumbnail: "$game.image",
            reviews: "$paths",
            timestamp: "$$NOW"
        }
    }
]);

// b Query for lowest/highest rated review for each game
db.comment.aggregate([
    {
        // rating: -1 gets highest rating (descending)
        // rating: 1 gets lowest rating (ascending)
        $sort: {"gid": 1, "rating": -1}
    }
    ,
    {
        // group by game id and select first element (highest/lowest)
        $group: {
            _id: "$gid",
            review_id: {$first: "$c_id"},
            user: {$first: "$user"},
            rating: {$first: "$rating"},
            comment: {$first: "$c_text"}
        }
    }
    ,
    {
        // join game document for each comment from game collection
        $lookup: {
            from: "game",
            foreignField: "gid",
            localField: "_id",
            as: "game"
        }
    }
    ,
    {
        // unpack game for easier access
        $unwind: "$game"
    }
    ,
    {
        // format data for presentation
        $project: {
            _id: "$_id",
            name: "$game.name",
            rating: "$rating",
            user: "$user",
            comment: "$comment",
            review_id: "$review_id"
        }
    }
]);

// Query to validate above code
db.comment.aggregate([
    {
        $match: {gid: 1072}
    }
]);
