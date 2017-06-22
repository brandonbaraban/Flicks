package com.codepath.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by bbaraban on 6/21/17.
 */

@Parcel // annotation indicates class is Parcelable
public class Movie {

    // values from API
    String title;
    String overview;
    String shortOverview;
    String longOverview;
    String posterPath; // only the path
    String backdropPath;
    Double voteAverage;

    public Movie() {}

    // initialize the JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        shortOverview = overview.length() > 147 ? overview.substring(0, 150) + "..." : overview;
        longOverview = overview.length() > 297 ? overview.substring(0, 300) + "..." : overview;
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getLongOverview() {
        return longOverview;
    }

    public String getShortOverview() {

        return shortOverview;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
