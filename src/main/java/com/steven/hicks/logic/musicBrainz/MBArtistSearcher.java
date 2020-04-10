package com.steven.hicks.logic.musicBrainz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.steven.hicks.beans.artist.Image;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MBArtistSearcher {

    private static ObjectMapper m_objectMapper = new ObjectMapper();
    private static String MUSIC_BRAINZ_ENDPOINT = "https://musicbrainz.org/ws/2/";
    private static String FAN_ART_ENDPOINT = "https://webservice.fanart.tv/v3/music/";

    public JsonNode searchForArtist(String artistName) {

        StringBuilder endpoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + "artist?query=artist:");
        endpoint.append(artistName);
        endpoint.append("&fmt=json");

        try
        {
            URL url = new URL(endpoint.toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            JsonNode node = m_objectMapper.readTree(data.toString());
            JsonNode artists = node.get("artists");

            System.out.println(artists);

            return artists;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public JsonNode searchForArtistWithImages(String artistName, String fanArtKey) {
        JsonNode artists = searchForArtist(artistName);

        for (Iterator<JsonNode> iter = artists.iterator(); iter.hasNext(); )
        {
            JsonNode it = iter.next();

            String mbid = it.get("id").asText();
            JsonNode images = getArtistImages(mbid, fanArtKey);

            ((ObjectNode)it).putArray("images").add(images);
        }

        System.out.println(artists);
        return artists;
    }


    public JsonNode getArtist(String mbid) {
        StringBuilder endpoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + "artist/");
        endpoint.append(mbid);
        endpoint.append("?fmt=json");

        try
        {
            URL url = new URL(endpoint.toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            JsonNode artist = m_objectMapper.readTree(data.toString());
            System.out.println(artist);

            return artist;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public JsonNode getArtistWithImages(String mbid, String fanArtKey) {
        JsonNode artist = getArtist(mbid);
        JsonNode images = getArtistImages(mbid, fanArtKey);
        ((ObjectNode)artist).putArray("images").add(images);

        System.out.println(artist);
        return artist;
    }

    private JsonNode getArtistImages(String mbid, String fanArtKey) {
        StringBuilder endpoint = new StringBuilder(FAN_ART_ENDPOINT + mbid);
        endpoint.append("?api_key=" + fanArtKey);
        endpoint.append("&format=json");

        try
        {
            URL url = new URL(endpoint.toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));)
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

            String nodes = Stream.of(imageArray)
                    .map(i -> {
                        try
                        {
                            return m_objectMapper.writeValueAsString(i);
                        } catch (JsonProcessingException e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .collect(Collectors.joining(",", "[", "]"));

            JsonNode imageNodes = m_objectMapper.readTree(nodes);
            return imageNodes;
        } catch (Exception e)
        {

        }

        return null;
    }
}
