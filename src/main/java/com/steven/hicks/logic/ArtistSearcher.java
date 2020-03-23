package com.steven.hicks.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steven.hicks.beans.ArtistAlbums;
import com.steven.hicks.beans.artist.Artist;
import com.steven.hicks.beans.artist.Image;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ArtistSearcher
{
    private static ObjectMapper m_objectMapper = new ObjectMapper();

    private String lastFmKey;
    private String fanArtKey;

    public ArtistSearcher(String lastFmKey, String fanArtKey) {
        this.lastFmKey = lastFmKey;
        this.fanArtKey = fanArtKey;
    }

    @FunctionalInterface
    private interface NodeConsumer {
        void consumeNode(JsonNode jsonNode) throws IOException;
    }

    private void wrapAPICall(StringBuilder urlString, NodeConsumer nodeConsumer) {
        try {
            URL url = new URL(urlString.toString());
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
            nodeConsumer.consumeNode(node);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     *
     * Returns a list of <Artist>Artist</Artist> given an <ArtistQueryBuilder>ArtistQueryBuilder</ArtistQueryBuilder>
     *
     * @param query - <ArtistQueryBuilder>ArtistQueryBuilder</ArtistQueryBuilder> taking a name, mbid, limit, and page number
     * @return List<<Artist>Artist</Artist>>
     */
    public List<Artist> searchForArtists(ArtistQueryBuilder query)
    {
        StringBuilder apiEndpoint = new StringBuilder("https://ws.audioscrobbler.com/2.0/?method=artist.search&artist=");

        apiEndpoint.append(query.getArtist());
        apiEndpoint.append("&limit=" + query.getLimit());
        apiEndpoint.append("&page=" + query.getPage());

        apiEndpoint.append("&api_key=" + lastFmKey +"&format=json");

        List<Artist> artistList = new ArrayList<>();
        wrapAPICall(apiEndpoint, jsonNode -> {
            JsonNode inner = jsonNode.get("results").get("artistmatches").get("artist");
            Artist[] artists = m_objectMapper.treeToValue(inner, Artist[].class);
            artistList.addAll(Arrays.asList(artists));
        });

//        artistList.forEach(x -> addImagesFromFanArt(x, false));
        return artistList;
    }

    /**
     * @NonNull(query.mbid)
     *
     * Takes an <ArtistQueryBuilder>ArtistQueryBuilder</ArtistQueryBuilder> with an MBID and returns a list of
     * <ArtistAlbums>ArtistAlbums</ArtistAlbums>, which are dumb representations of the fuller <Album>Album</Album> object.
     * Can then use <AlbumSearcher>AlbumSearcher</AlbumSearcher> to get the <Album>Album</Album> from the <ArtistAlbum>ArtistAlbum</ArtistAlbum>
     *
     * @param query - an <ArtistQueryBuilder>ArtistQueryBuilder</ArtistQueryBuilder> that <b>MUST</b> have the MBID field.
     * @return - List<<ArtistAlbums>ArtistAlbums</ArtistAlbums>>
     */
    public List<ArtistAlbums> getAlbums(ArtistQueryBuilder query)
    {
        StringBuilder apiEndpoint = new StringBuilder("https://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&mbid=");

        apiEndpoint.append(query.getMbid());
        apiEndpoint.append("&limit=" + query.getLimit());
        apiEndpoint.append("&page=" + query.getPage());
        apiEndpoint.append("&api_key=" + lastFmKey +"&format=json");

        List<ArtistAlbums> albumList = new ArrayList<>();
        wrapAPICall(apiEndpoint, jsonNode -> {
            m_objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            JsonNode inner = jsonNode.get("topalbums").get("album");
            List<ArtistAlbums> artistAlbums = m_objectMapper.readValue(inner.toString(), new TypeReference<List<ArtistAlbums>>() {});
            artistAlbums.removeIf(x -> x.getImage().length == 0);
            artistAlbums.removeIf(x -> x == null);
            albumList.addAll(artistAlbums);
        });

        return albumList;
    }

    /**
     * Given an MBID, returns an 'full' artist, which includes tags, and bio, in addition to everything the searchForArtists()
     * results gives you.
     *
     * @param artist
     * @return <Artist>Artist</Artist>
     */
    public Artist getFullArtist(Artist artist)
    {
        StringBuilder apiEndpoint = new StringBuilder("https://ws.audioscrobbler.com/2.0/?method=artist.getInfo&mbid=");
        apiEndpoint.append(artist.getMbid());
        apiEndpoint.append("&api_key=" + lastFmKey +"&format=json");

        Artist[] fullArtist = new Artist[1];

        wrapAPICall(apiEndpoint, jsonNode -> {
            m_objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            JsonNode inner = jsonNode.get("artist");
            Artist aFullArtist = m_objectMapper.treeToValue(inner, Artist.class);
            fullArtist[0] = aFullArtist;
        });

        Artist aFullArtist = fullArtist[0];

        addImagesFromFanArt(aFullArtist, false);
        aFullArtist.setListeners(artist.getListeners());
        return aFullArtist;
    }

    private void addImagesFromFanArt(Artist artist, boolean isSecondTry)
    {
        StringBuilder apiEndpoint2 = new StringBuilder("https://webservice.fanart.tv/v3/music/"+artist.getMbid()+"&?api_key=" + fanArtKey + "&format=json");
        boolean problemWithMbid = false;
        try
        {
            URL url = new URL(apiEndpoint2.toString());
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));)
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            Map<String, Object> node = m_objectMapper.readValue(data.toString(), new TypeReference<Map<String, Object>>(){});

            List<Image> images = new ArrayList<>();

            JsonNode tree = m_objectMapper.readTree(data.toString());
            JsonNode thumbnails = tree.get("artistthumb");
            JsonNode backgrounds = tree.get("artistbackground");
            JsonNode hdLogos = tree.get("hdmusiclogo");
            JsonNode logos = tree.get("musiclogo");
            JsonNode albumCovers = tree.get("albums");
            if (thumbnails != null) //first try thumbnails
            {
                int counter = 0;
                while (thumbnails.get(counter) != null)
                {
                    String id = thumbnails.get(counter).get("id").asText();
                    String urll = thumbnails.get(counter).get("url").asText();
                    String likes = thumbnails.get(counter).get("likes").asText();

                    Image image = new Image();
                    image.setText(urll);
                    images.add(image);
                    counter++;
                }
            }
            else if (backgrounds != null) //then try backgrounds
            {
                int counter = 0;
                while (backgrounds.get(counter) != null)
                {
                    String id = backgrounds.get(counter).get("id").asText();
                    String urll = backgrounds.get(counter).get("url").asText();
                    String likes = backgrounds.get(counter).get("likes").asText();

                    Image image = new Image();
                    image.setText(urll);
                    images.add(image);
                    counter++;
                }
            }
            else if (hdLogos != null) //then try HD logos
            {
                int counter = 0;
                while (hdLogos.get(counter) != null)
                {
                    String id = hdLogos.get(counter).get("id").asText();
                    String urll = hdLogos.get(counter).get("url").asText();
                    String likes = hdLogos.get(counter).get("likes").asText();

                    Image image = new Image();
                    image.setText(urll);
                    images.add(image);
                    counter++;
                }
            }
            else if(logos != null) // then try logos
            {
                int counter = 0;
                while (logos.get(counter) != null)
                {
                    String id = logos.get(counter).get("id").asText();
                    String urll = logos.get(counter).get("url").asText();
                    String likes = logos.get(counter).get("likes").asText();

                    Image image = new Image();
                    image.setText(urll);
                    images.add(image);
                    counter++;
                }
            }
            else if (albumCovers != null) // last resort use album covers
            {
                Iterator<JsonNode> it = albumCovers.iterator();
                while (it.hasNext())
                {
                    JsonNode node2 = it.next();
                    JsonNode albumCover = node2.get("albumcover");
                    if (albumCover != null)
                    {
                        String urll = albumCover.findValue("url").asText();

                        Image image = new Image();
                        image.setText(urll);
                        images.add(image);
                    }
                }
            }

            Image[] imageArray = new Image[images.size()];
            imageArray = images.toArray(imageArray);
            artist.setImage(imageArray);
        }
        catch (IOException e)
        {
            problemWithMbid = true;
        }

        //If we couldn't find the artist by first mbid, look up mbid from MusicBrainz and try it again
        if (problemWithMbid && !isSecondTry)
        {
            String newMbid = "";
            String newName = "";
            StringBuilder mbEndpoint = new StringBuilder("http://musicbrainz.org/ws/2/artist/?query=" + artist.getName().replace(" ", "%20") + "&fmt=json");
            try
            {
                URL url = new URL(mbEndpoint.toString());
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setRequestProperty("User-Agent", "StevesReviews/1.0 stevesreviews.net shicks255@yahoo.com");

                StringBuilder data = new StringBuilder();
                String input;
                try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));)
                {
                    while ((input = in.readLine()) != null)
                        data.append(input);
                }

                JsonNode node = m_objectMapper.readTree(data.toString());
                JsonNode artists = node.get("artists").get(0);

                newMbid = artists.get("id").asText();
                newName = artists.get("name").asText();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

            if (newName.equalsIgnoreCase(artist.getName()))
            {
                artist.setMbid(newMbid);
                addImagesFromFanArt(artist, true);
            }
        }
    }
}
