package fecoder.models;

import java.util.Date;

public class Year {

    private int id;
    private String year;

    public Year() {}

    public Year(int id, String year) {
        this.id = id;
        this.year = year;
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getYear() { return this.year; }
    public void setYear(String year) { this.year = year; }
}
