package com.day28.workshop28.model;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Game {
    public Integer gameId;
    public String name;
    public Integer rating;
    public String user;
    public String comment;
    public String reviewId;

    public Integer getGameId() {
        return gameId;
    }
    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getReviewId() {
        return reviewId;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    @Override
    public String toString() {
        return "Game [gameId=" + gameId + ", name=" + name + ", rating=" + rating + ", user=" + user + ", comment="
                + comment + ", reviewId=" + reviewId + "]";
    }

    public static Game docToGame(Document doc) {
        Game g = new Game();
        g.setGameId(doc.getInteger("_id"));
        g.setName(doc.getString("name"));
        g.setRating(doc.getInteger("rating"));
        g.setUser(doc.getString("user"));
        g.setComment(doc.getString("comment"));
        g.setReviewId(doc.getString("review_id"));
        return g;
    } 

    public JsonObject toJson() {
        JsonObject gameJson = Json.createObjectBuilder()
            .add("_id", getGameId())
            .add("name", getName())
            .add("rating", getRating())
            .add("user", getUser())
            .add("comment", getComment())
            .add("review_id", getReviewId())
            .build();
        return gameJson;
    }
    
}
