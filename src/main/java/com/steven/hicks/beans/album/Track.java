package com.steven.hicks.beans.album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Track
{
    private Integer m_rank;
    private Attr attr;
    private String m_name = "";
    private int    m_duration;

    public Integer getRank()
    {
        return m_rank != null ? m_rank : 0;
    }

    public void setRank(Integer rank)
    {
        m_rank = rank;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public int getDuration()
    {
        return m_duration;
    }

    public void setDuration(int duration)
    {
        m_duration = duration;
    }

    @JsonProperty("@attr")
    public Attr getAttr()
    {
        return attr;
    }

    public void setAttr(Attr attr)
    {
        this.attr = attr;
    }
}