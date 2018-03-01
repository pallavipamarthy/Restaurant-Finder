package com.example.sforestaurants.data;

public class Review {
    private String mAuthorName;
    private String mRating;
    private String mText;

    public Review(String author, String rating, String text) {
        mAuthorName = author;
        mRating = rating;
        mText = text;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(mAuthorName + " says:" +"\n");
        sb.append(mText +"\n");
        sb.append("Rating: " +mRating);
        sb.append("\n");
        return sb.toString();
    }
}
