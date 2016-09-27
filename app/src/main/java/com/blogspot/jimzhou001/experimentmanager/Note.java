package com.blogspot.jimzhou001.experimentmanager;

public class Note {

    private String title;
    private String date;
    private String time;
    private long millis;

    public Note(String title, String date, String time, long millis) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.millis = millis;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getMillis() {
        return millis;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    @Override
    public boolean equals(Object obj) {
        Note note = (Note)obj;
        if (note.getTitle().equals(getTitle())&&note.getDate().equals(getDate())&&note.getTime().equals(getTime())) {
            return true;
        }
        return false;
    }

}
