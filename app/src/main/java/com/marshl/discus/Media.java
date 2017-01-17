package com.marshl.discus;

import java.util.Date;

public class Media {

    private String title;
    private String year;
    private String contentRating;
    private Date releaseDate;
    private int durationMinutes;
    private String genres;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String languages;
    private String country;
    private String awards;
    private String posterUrl;
    private Integer metascore;
    private Float imdbRating;
    private Integer imdbVotes;
    private String imdbId;
    private String type;

    public Media() {}

    public Media(Media other){
        this.title = other.title;
        this.year = other.year;
        this.contentRating = other.contentRating;
        this.releaseDate = other.releaseDate;
        this.durationMinutes = other.durationMinutes;
        this.genres = other.genres;
        this.director = other.director;
        this.writer = other.writer;
        this.actors = other.actors;
        this.plot = other.plot;
        this.languages = other.languages;
        this.country = other.country;
        this.awards = other.awards;
        this.posterUrl = other.posterUrl;
        this.metascore = other.metascore;
        this.imdbRating = other.imdbRating;
        this.imdbVotes = other.imdbVotes;
        this.imdbId = other.imdbId;
        this.type = other.type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(Integer imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getContentRating() {
        return this.contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getGenres() {
        return this.genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getDirector() {
        return this.director;
    }

    public void setDirector(String directory) {
        this.director = directory;
    }

    public String getWriter() {
        return this.writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {
        return this.actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return this.plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguages() {
        return this.languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return this.awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Integer getMetascore() {
        return this.metascore;
    }

    public void setMetascore(int metascore) {
        this.metascore = metascore;
    }

    public Float getImdbRating() {
        return this.imdbRating;
    }

    public void setImdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbId() {
        return this.imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
