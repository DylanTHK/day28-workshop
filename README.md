


Concatenation for aggregation
```
ProjectionOperation projectFields = Aggregation.project("gid", "rating")
    .andExpression("concat('/review/',c_id)").as("path")
    .andExclude("_id");
```