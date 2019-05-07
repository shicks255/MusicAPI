package com.steven.hicks.beans.album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tracks
{
    private Track[] track;

    public Track[] getTrack()
    {
        return track;
    }

    public void setTrack(Track[] track)
    {
        this.track = track;
    }
}
