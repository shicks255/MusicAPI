package com.steven.hicks.beans.artist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist
{
    private String m_name = "";
    private String m_mbid = "";
    private String m_url = "";

    private Image[] image;
    private Tags tags;
    private Bio bio;

    private int    m_listeners;

    @Override
    public String toString()
    {
        return m_name + " " + m_listeners + " "  + m_url + " " + m_mbid;
    }

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

    public String getUrl()
    {
        return m_url;
    }

    public void setUrl(String url)
    {
        m_url = url;
    }

    public int getListeners()
    {
        return m_listeners;
    }

    public void setListeners(int listeners)
    {
        m_listeners = listeners;
    }

    public Image[] getImage()
    {
        return image;
    }

    public void setImage(Image[] image)
    {
        this.image = image;
    }

    public Bio getBio()
    {
        return bio;
    }

    public void setBio(Bio bio)
    {
        this.bio = bio;
    }

    public Tags getTags()
    {
        return tags;
    }

    public void setTags(Tags tags)
    {
        this.tags = tags;
    }


}
