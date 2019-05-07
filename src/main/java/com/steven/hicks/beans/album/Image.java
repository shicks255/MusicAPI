package com.steven.hicks.beans.album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Image
{
    String text = "";
    String size = "";

    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    @JsonProperty("#text")
    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}