package com.steven.hicks.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steven.hicks.beans.album.Album;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class AlbumSearcher
{
    private static ObjectMapper m_objectMapper = new ObjectMapper();
    private static String LAST_FM_ALBUM_SEARCH_ENDPOINT = "https://ws.audioscrobbler.com/2.0/?method=album.search&album=";
    private static String LAST_FM_ALBUM_INFO_ENDPOINT = "https://ws.audioscrobbler.com/2.0/?method=album.getInfo&artist=";
    private static String MUSIC_BRAINZ_ENDPOINT = "https://musicbrainz.org/ws/2/release/";

    private String lastFmKey;

    public AlbumSearcher(String lastFmKey)
    {
        this.lastFmKey = lastFmKey;
    }

    @FunctionalInterface
    private interface APINodeConsumer {
        void consumeNode(JsonNode apiNode) throws IOException;
    }

    private void wrapAPICall(String urlString, APINodeConsumer consumer) {
        try {

            URL url = new URL(urlString);
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
            consumer.consumeNode(node);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Returns a list of <Album>Album</Album> given an <AlbumQueryBuilder>AlbumQueryBuilder</AlbumQueryBuilder>
     * @param query - an AlbumQueryBuilder with the albumName, limit per page, and page number fields.
     * @return List<<Album>Album</Album>>
     */
    public List<Album> searchForAlbums(AlbumQueryBuilder query) {
        StringBuilder apiEndpoint = new StringBuilder(LAST_FM_ALBUM_SEARCH_ENDPOINT);

        apiEndpoint.append(query.getAlbum());
        apiEndpoint.append("&limit=" + query.getLimit());
        apiEndpoint.append("&page=" + query.getPage());
        apiEndpoint.append("&api_key=" + lastFmKey +"&format=json");

        List<Album> albums = new ArrayList<>();
        wrapAPICall(apiEndpoint.toString(), node -> {
            JsonNode inner = node.get("results").get("albummatches").get("album");

            Album[] artists = m_objectMapper.treeToValue(inner, Album[].class);
            albums.addAll(Arrays.asList(artists));
        });

        return albums;
    }

    /**
     * Used to get details from an album, such as Tracks[] and Images[]
     * Can be used after getting all the albums from an <ArtistSearcher>ArtistSearcher</ArtistSearcher>
     * If mbid is present, query using that, else query using artistName and title
     *
     * @param mbid - String MusicBrainz id.
     * @param title - String title
     * @param artistName - String artistName
     * @return <Album>Album</Album>
     */
    public Album getFullAlbum(String mbid, String title, String artistName) {
        StringBuilder apiEndpoint = new StringBuilder(LAST_FM_ALBUM_INFO_ENDPOINT + artistName.replace(" ", "%20").replace("&", "%26"));

        if (mbid.length() > 0)
            apiEndpoint.append("&mbid=" + mbid);
        else
            apiEndpoint.append("&album=" + title.replace(" ", "%20"));
        apiEndpoint.append("&api_key=" + lastFmKey +"&format=json");

        Album[] fullAlbum = new Album[1];
        wrapAPICall(apiEndpoint.toString(), node -> {
            if (node.get("album") != null)
            {
                JsonNode inner = node.get("album");
                fullAlbum[0] = m_objectMapper.treeToValue(inner, Album.class);
            }
        });

        Album fullAlbumSingular = fullAlbum[0];
        fullAlbumSingular.setReleasedate(getAlbumDate(fullAlbumSingular.getMbid()));

        return fullAlbumSingular;
    }

    /**
     * Since lastfm album info doesnt include release date, this will fetch it from MusicBrainz
     * @param mbid string of the album
     * @return <LocalDate>release date</LocalDate>
     */
    public LocalDate getAlbumDate(String mbid)
    {
        StringBuilder apiEndpoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + mbid);
        apiEndpoint.append("?fmt=json&inc=release-groups");

        LocalDate[] releaseDate = new LocalDate[1];
        wrapAPICall(apiEndpoint.toString(), node -> {
            JsonNode releaseGroup = node.findValue("release-group");
            if (releaseGroup != null && releaseGroup.size()>0)
            {
                String releaseDateString = releaseGroup.get("first-release-date").asText();
                String[] dateItems = releaseDateString.split("-");
                if (dateItems != null && dateItems.length == 3)
                {
                    LocalDate release = LocalDate.of(Integer.parseInt(dateItems[0]),
                            Integer.parseInt(dateItems[1]),
                            Integer.parseInt(dateItems[2]));

                    releaseDate[0] = release;
                }
            }
        });

        return releaseDate[0];
    }
}
