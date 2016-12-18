package com.marshl.discus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Media {
    private String id;
    private String title;
    private String name;
    private String episodeTitle;
    private String description;

    private String year;
    private String type;
    private String director;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
            this.type = matcher.group(2).trim();
            this.year = matcher.group(1);
            this.director = matcher.group(3).trim();
        }
    }

    public String getYear() {
        return this.year;
    }

    public String getType() {
        return this.type;
    }

    public String getDirector() {
        return this.director;
    }


}
