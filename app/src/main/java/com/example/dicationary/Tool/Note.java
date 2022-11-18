package com.example.dicationary.Tool;


public class Note {
    private int id;
    private String word;
    private String date;

    public Note(String word, String date) {
        this.word = word;
        this.date = date;
    }

    public Note(int id, String word, String date) {
        this.id = id;
        this.word = word;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
