package com.steven.hicks.beans.album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Album
{
    private String m_name = "";
    private String m_artist = "";
    private long   m_id;
    private String m_mbid = "";
    private String m_url = "";
    private LocalDate m_releasedate;
    private Image[] image;
    private int    m_listeners;
    private long   m_playCount;
    private Tracks tracks;
    private Bio wiki;

    public Album()
    {}

    @Override
    public String toString()
    {
        return m_name + " " + m_artist + " ";
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public String getArtist()
    {
        return m_artist;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public void setArtist(String artist)
    {
        m_artist = artist;
    }

    public long getId()
    {
        return m_id;
    }

    public void setId(long id)
    {
        m_id = id;
    }

    public String getMbid()
    {
        return m_mbid;
    }

    public void setMbid(String mbid)
    {
        m_mbid = mbid;
    }

    public String getUrl()
    {
        return m_url;
    }

    public void setUrl(String url)
    {
        m_url = url;
    }

    public LocalDate getReleasedate()
    {
        return m_releasedate;
    }

    public void setReleasedate(LocalDate releasedate)
    {
        m_releasedate = releasedate;
    }

    public Image[] getImage()
    {
        return image;
    }

    public void setImage(Image[] image)
    {
        this.image = image;
    }

    public Tracks getTracks()
    {
        return tracks;
    }

    public void setTracks(Tracks tracks)
    {
        this.tracks = tracks;
    }

    public int getListeners()
    {
        return m_listeners;
    }

    public void setListeners(int listeners)
    {
        m_listeners = listeners;
    }

    public long getPlayCount()
    {
        return m_playCount;
    }

    public void setPlayCount(long playCount)
    {
        m_playCount = playCount;
    }

    public Bio getWiki()
    {
        return wiki;
    }

    public void setWiki(Bio wiki)
    {
        this.wiki = wiki;
    }

}
