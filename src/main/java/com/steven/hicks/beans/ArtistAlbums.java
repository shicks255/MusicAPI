package com.steven.hicks.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steven.hicks.beans.artist.Image;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistAlbums
{
    private String m_name = "";
    private String m_mbid = "";
    private Integer m_playcount = 0;

    private Image[] image;

    public String getName()
    {
        return m_name;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public String getMbid()
    {
        return m_mbid;
    }

    public void setMbid(String mbid)
    {
        m_mbid = mbid;
    }

    public Image[] getImage()
    {
        return image;
    }

    public void setImage(Image[] image)
    {
        this.image = image;
    }

    public Integer getPlaycount()
    {
        return m_playcount;
    }

    public void setPlaycount(Integer playcount)
    {
        m_playcount = playcount;
    }
}
