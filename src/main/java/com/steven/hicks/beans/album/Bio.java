package com.steven.hicks.beans.album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bio
{
    private String published = "";
    private String summary = "";
    private String content = "";

    public String getPublished()
    {
        return published;
    }

    public void setPublished(String published)
    {
        this.published = published;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}