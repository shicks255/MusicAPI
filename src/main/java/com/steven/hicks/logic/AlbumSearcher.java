package com.steven.hicks.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steven.hicks.MissingConfigKeyException;
import com.steven.hicks.NoConfigException;
import com.steven.hicks.beans.album.Album;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class AlbumSearcher
{
    private static ObjectMapper m_objectMapper = new ObjectMapper();
    private ResourceBundle config;

    public AlbumSearcher()
    {
        try {
            config = ResourceBundle.getBundle("config");
        } catch (Exception e) {
            throw new NoConfigException("Invalid or missing config.properties file");
        }
        try {
            config.getString("lastFM_api_key");
        } catch (Exception E) {
            throw new MissingConfigKeyException("Missing config property lastFM_api_key");
        }
        try {
            config.getString("fanArt_api_key");
        } catch (Exception E) {
            throw new MissingConfigKeyException("Missing config property fanArt_api_key");
        }
    }

    /**
     *
     * Returns a list of <Album>Album</Album> given an <AlbumQueryBuilder>AlbumQueryBuilder</AlbumQueryBuilder>
     *
     * @param query - an AlbumQueryBuilder with the albumName, limit per page, and page number fields.
     * @return List<<Album>Album</Album>>
     */
    public List<Album> searchForAlbums(AlbumQueryBuilder query)
    {
        StringBuilder apiEndpoint = new StringBuilder("https://ws.audioscrobbler.com/2.0/?method=album.search&album=");

        apiEndpoint.append(query.getAlbum());
        apiEndpoint.append("&limit=" + query.getLimit());
        apiEndpoint.append("&page=" + query.getPage());

        apiEndpoint.append("&api_key=" + config.getString("lastFM_api_key") +"&format=json");

        List<Album> albumList = Collections.emptyList();
        try
        {
            URL url = new URL(apiEndpoint.toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            JsonNode node = m_objectMapper.readTree(data.toString());
            JsonNode inner = node.get("results").get("albummatches").get("album");

            Album[] artists = m_objectMapper.treeToValue(inner, Album[].class);
            albumList = Arrays.asList(artists);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return albumList;
    }

    /**
     *
     * Used to get details from an album, such as Tracks[] and Images[]
     * Can be used after getting all the albums from an <ArtistSearcher>ArtistSearcher</ArtistSearcher>
     * If mbid is present, query sing that, else query using artistName and title
     *
     * @param mbid - String MusicBrainz id.
     * @param title - String title
     * @param artistName - String artistName
     * @return <Album>Album</Album>
     */
    public Album getFullAlbum(String mbid, String title, String artistName)
    {
        StringBuilder apiEndpoint = new StringBuilder("https://ws.audioscrobbler.com/2.0/?method=album.getInfo&artist=" + artistName.replace(" ", "%20").replace("&", "%26"));

        if (mbid.length() > 0)
                                  apiEndpoint.append("&mbid=" + mbid);
        else
            apiEndpoint.append("&album=" + title.replace(" ", "%20"));
        apiEndpoint.append("&api_key=" + config.getString("lastFM_api_key") +"&format=json");

        Album fullAlbum = null;
        try
        {
            URL url = new URL(apiEndpoint.toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            JsonNode node = m_objectMapper.readTree(data.toString());
            if (node.get("album") != null)
            {
                JsonNode inner = node.get("album");
                Album aa = m_objectMapper.treeToValue(inner, Album.class);
                fullAlbum = aa;
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return fullAlbum;
    }

    /**
     * Since lastfm album info doesnt include release date, this will fetch it from MusicBrainz
     * @param mbid string of the album
     * @return <LocalDate>release date</LocalDate>
     */
    public LocalDate getAlbumDate(String mbid)
    {
        StringBuilder apiEndpoint = new StringBuilder("http://musicbrainz.org/ws/2/release/" + mbid);
        apiEndpoint.append("?fmt=json&inc=release-groups");
        try
        {
            URL url = new URL(apiEndpoint.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "http://www.stevenmhicks.com");

            StringBuilder data = new StringBuilder();
            String input;
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            JsonNode node = m_objectMapper.readTree(data.toString());
            JsonNode releaseGroup = node.findValue("release-group");
            if (releaseGroup != null && releaseGroup.size()>0)
            {
                String release = releaseGroup.get("first-release-date").asText();
                String[] dateItems = release.split("-");
                if (dateItems != null && dateItems.length == 3)
                {
                    LocalDate releaseDate = LocalDate.of(Integer.parseInt(dateItems[0]),
                            Integer.parseInt(dateItems[1]),
                            Integer.parseInt(dateItems[2]));
                    return releaseDate;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return LocalDate.of(1900, 01, 01);
    }
}
