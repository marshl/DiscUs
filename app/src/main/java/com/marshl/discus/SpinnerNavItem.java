package com.marshl.discus;

public class SpinnerNavItem {
    private String title;
    private int year;

    public SpinnerNavItem(String title, int year) {
        this.title = title;
        this.year = year;
    }

    public String getTitle() {
        return this.title;
    }

    public int getYear() {
        return this.year;
    }
}
