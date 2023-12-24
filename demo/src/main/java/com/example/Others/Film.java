package com.example.Others;

public class Film {
    private String name;
    private String posterPath;

    public Film(String name, String posterPath) {
        this.name = name;
        this.posterPath = posterPath;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }
}

