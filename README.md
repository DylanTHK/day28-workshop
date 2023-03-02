Day28 Workshop


MongoAggregation Query (Java)
1. Operations
- MatchOperation matchGameId = Aggregation.match(<Criteria>)
- ProjectionOperation projectFields = Aggregation.project(<project_fields...>)
- GroupOperation groupByGameId = Aggregation.group(<field_to_group_by>)
- LookupOperation lookupGame = Aggregation.lookup(<collection_name>, <local_field>, <foreign_field>, <as_field_name>)
- UnwindOperation unwindGame = Aggregation.unwind(<array_field_name>);
2. Pipeline
- Aggregation pipeline = Aggregation.newAggregation(<add_all_aggregations...>)
3. MongoTemplate
- AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, <main_collection>, Document.class);
4. Extract Documents
### Single Document Query
- Document firstDoc = results.getUniqueMappedResult(); 
### Multiple Document Query
- List<Document> resultList = results.getMappedResults();

## Important MongoDB Aggregation
## Operation: Match
Mongo
```
$match: {gid:2}
```
Java
```
MatchOperation matchGameId = Aggregation.match(Criteria.where("gid").is(gameId));
```
## Operation: Projection
Mongo
```
$project: {
    c_id: 1,
    gid: 1,
    rating: 1,
    path: {$concat: ["/review/", "$c_id"]}
}
```
Java
```
ProjectionOperation projectFields = Aggregation.project("gid", "rating")
    .andExpression("concat('/review/',c_id)").as("path")
    .andExclude("_id");
```

## Operation: Group
Mongo
```
<!-- group operators count and array(push) -->
$group: {
        _id: "$gid",
        users_rated: {$sum: 1},
        avg_rating: {$avg: "$rating"}, // TODO
        paths: {$push: "$path"}
    }
```
Java
```
GroupOperation groupByGameId = Aggregation.group("gid")
    .count().as("users_rated")
    .avg("rating").as("avg_rating")
    .push("path").as("paths");
```
## Operation: Lookup (Join)
Mongo
```
$lookup: {
        from: "game",
        foreignField: "gid", // (db.game)
        localField: "_id", // (db.comment)
        as: "game"
    }
```
Java
```
LookupOperation lookupGame = Aggregation.lookup(COLLECTION_GAME, "_id", "gid", "game");
```
## Operation: Unwind (unpack arrays)
Mongo
```
$unwind: "$game"
```
Java
```
UnwindOperation unwindGame = Aggregation.unwind("game");
```
## Operation: Projection
Mongo
```
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
```
Java
```
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
```
## Operation: sort
Mongo
```
<!-- rating: -1 sorts ascending (highest on top) -->
<!-- rating: 1 sorts ascending (lowest on top) -->
$sort: {"gid": 1, "rating": -1}
```
Java
```
SortOperation sortByRating = Aggregation
    .sort(Sort.by(Direction.ASC, "gid"))
    .and(Sort.by(Direction.DESC, "rating"));
```
## Operation: group and sort
Mongo
```
$group: {
        _id: "$gid",
        review_id: {$first: "$c_id"},
        user: {$first: "$user"},
        rating: {$first: "$rating"},
        comment: {$first: "$c_text"}
    }
```
Java
```
GroupOperation groupByGameId = Aggregation.group("gid")
    .first("c_id").as("review_id")
    .first("user").as("user")
    .first("rating").as("rating")
    .first("c_text").as("comment");
```

## Concatenation for aggregation
- andExpression("concat('/review/',c_id)")
```
ProjectionOperation projectFields = Aggregation.project("gid", "rating")
    .andExpression("concat('/review/',c_id)").as("path")
    .andExclude("_id");
```

## FORMAT FOR MONGOIMPORT (JsonArray - .json)
```
mongoimport --uri "<connection_url>/?directConnection=true&appName=mongosh+1.6.0&authSource=admin" --db <db_name> --collection <collection_name> --file <file_location_url> --jsonArray
```

## FORMAT FOR MONGOIMPORT (.csv)
```
mongoimport --uri "<connection_url>/?directConnection=true&appName=mongosh+1.6.0&authSource=admin" --db <db_name> --collection <collection_name> --file <file_location_url> --type csv --headerline --ignoreBlanks
```
## Alternative method
```
mongoimport --authenticationDatabase=admin <ur> -d <db_name> -c <collection> --type=csv --headerline --file googleplaystore.csv
```