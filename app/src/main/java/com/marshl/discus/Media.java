package com.marshl.discus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Media {
    private String reference;
    private String title;
    private String name;
    private String episodeTitle;
    private String description;

    private String year;
    private String type;
    private String director;
    private MediaCategory category = MediaCategory.Unknown;

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEpisodeTitle() {
        return this.episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;

        Pattern pattern = Pattern.compile("([0-9?]+)(.*), +<a href='.+?'>(.+?)</a>");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            this.year = matcher.group(1);
            this.setType(matcher.group(2).trim());
            this.director = matcher.group(3).trim();
        }
    }

    public String getYear() {
        return this.year;
    }

    public String getType() {
        return this.type;
    }

    private void setType(String type) {

        this.type = type;

        // This has to be done in a particular order
        // so that "TV Documentary Shorts" are categorised as documentaries, for example
        if (this.type.toLowerCase().contains("game")) {
            this.category = MediaCategory.Game;
        } else if (this.type.toLowerCase().contains("documentary")) {
            this.category = MediaCategory.Documentary;
        } else if (this.type.toLowerCase().contains("movie")) {
            this.category = MediaCategory.Film;
        } else if (this.type.toLowerCase().contains("tv")) {
            this.category = MediaCategory.Television;
        } else if (this.type.toLowerCase().contains("video")) {
            this.category = MediaCategory.Video;
        } else if (this.type.toLowerCase().contains("soundtrack")) {
            this.category = MediaCategory.Music;
        } else {
            this.category = MediaCategory.Film;
        }
    }

    public String getDirector() {
        return this.director;
    }

    public MediaCategory getCategory() {
        return this.category;
    }

    public enum MediaCategory {
        Game,
        Television,
        Film,
        Music,
        Video,
        Documentary,
        Unknown
    }
}
