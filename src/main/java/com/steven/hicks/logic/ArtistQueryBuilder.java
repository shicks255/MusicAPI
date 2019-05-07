package com.steven.hicks.logic;


/**
 *
 * Used for building queries to search for Artists
 *
 * Uses the builder pattern.  ArtistQueryBuilder has private constructor, cannot be
 * instantiated except through its static class Builder.
 *
 * ArtistQueryBuilder aqb = new ArtistQueryBuilder.Builder().methods.build();
 *
 */
public class ArtistQueryBuilder
{
    private String m_artist = "";
    private String m_mbid = "";
    private int    m_limit = 30;
    private int    m_page = 1;

    private ArtistQueryBuilder()
    {}

    public static class Builder
    {
        private String name = "";
        private String mbid = "";
        private int    limit = 30;
        private int    page = 1;

        public Builder artistName(String name)
        {
            this.name = name.replace(" ", "%20");
            return this;
        }

        public Builder mbid(String mbid)
        {
            this.mbid = mbid;
            return this;
        }

        public Builder setLimit(int limit)
        {
            this.limit = limit;
            return this;
        }

        public Builder setPage(int page)
        {
            this.page = page;
            return this;
        }

        public ArtistQueryBuilder build()
        {
            ArtistQueryBuilder builder = new ArtistQueryBuilder();
            builder.m_artist = this.name;
            builder.m_mbid = this.mbid;
            builder.m_limit = this.limit;
            builder.m_page = this.page;

            return builder;
        }
    }

    public String getArtist()
    {
        return m_artist;
    }

    public void setArtist(String artist)
    {
        m_artist = artist;
    }

    public String getMbid()
    {
        return m_mbid;
    }

    public void setMbid(String mbid)
    {
        m_mbid = mbid;
    }

    public int getLimit()
    {
        return m_limit;
    }

    public void setLimit(int limit)
    {
        m_limit = limit;
    }

    public int getPage()
    {
        return m_page;
    }

    public void setPage(int page)
    {
        m_page = page;
    }
}
