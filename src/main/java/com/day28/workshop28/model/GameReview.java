package com.day28.workshop28.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class GameReview {
    private Integer gameId;
    private String name;
    private Integer year;
    private Integer rank;
    private Float averageRating;
    private Integer usersRated;
    private String url;
    private String thumbnail;
    private List<String> reviews;
    private Timestamp timestamp;

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
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public Integer getRank() {
        return rank;
    }
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    public Float getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }
    public Integer getUsersRated() {
        return usersRated;
    }
    public void setUsersRated(Integer usersRated) {
        this.usersRated = usersRated;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public List<String> getReviews() {
        return reviews;
    }
    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public String toString() {
        return "Review [gameId=" + gameId + ", name=" + name + ", year=" + year + ", rank=" + rank + ", averageRating="
                + averageRating + ", usersRated=" + usersRated + ", url=" + url + ", thumbnail=" + thumbnail
                + ", reviews=" + reviews + ", timestamp=" + timestamp + "]";
    }

    public static GameReview createFromDoc(Document doc) {
        GameReview gr = new GameReview();
        gr.setGameId(doc.getInteger("game_id"));
        gr.setName(doc.getString("name"));
        gr.setYear(doc.getInteger("year"));
        gr.setRank(doc.getInteger("rank"));
        gr.setAverageRating(doc.getDouble("avg_rating").floatValue());
        gr.setUsersRated(doc.getInteger("users_rated"));
        gr.setUrl(doc.getString("url"));
        gr.setThumbnail(doc.getString("thumbnail"));
        gr.setTimestamp(Timestamp.from(Instant.now()));
        gr.setReviews(doc.getList("reviews", String.class));

        System.out.println("\nGameReview >>> created POJO: " + gr);
        return gr;
    }

    public JsonObject toJson() {
        
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        List<String> reviews = getReviews();
        for (String s : reviews) {
            arrayBuilder.add(s);
        }
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("game_id", getGameId())
            .add("name", getName())
            .add("year", getYear())
            .add("rank", getRank())
            .add("avg_rating", getAverageRating())
            .add("users_rated", getUsersRated())
            .add("url", getUrl())
            .add("thumbnail", getThumbnail())
            .add("reviews", arrayBuilder)
            .add("timestamp", getTimestamp().toString()); 
        
        return builder.build();
    }


}
