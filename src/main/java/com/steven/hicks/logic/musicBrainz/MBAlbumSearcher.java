package com.steven.hicks.logic.musicBrainz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class MBAlbumSearcher {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String MUSIC_BRAINZ_ENDPOINT = "https://musicbrainz.org/ws/2/";
    private static String ALBUM_ART_ENDPOINT = "http://coverartarchive.org/release/";

    private JsonNode process(StringBuilder endPoint, NodeProcessor nodeProcessor) {

        try {
            URL url = new URL(endPoint.toString());
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

            JsonNode node = objectMapper.readTree(data.toString());
            return nodeProcessor.processNode(node);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public JsonNode getAlbum(String albumMbid) {
        StringBuilder endPoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + "release-group/");
        endPoint.append(albumMbid);
        endPoint.append("?fmt=json");
        endPoint.append("&inc=releases+artist-credits");

        return process(endPoint, (node) -> {
            JsonNode releases = node.get("releases");

            //iterate through each release
            for (Iterator<JsonNode> it = releases.iterator(); it.hasNext(); )
            {
                JsonNode release = it.next();
                JsonNode tracks = null;

                //iterate through each release until we find images
                String id = release.get("id").asText();
                JsonNode images = getAlbumImage(id);
                if (tracks == null)
                    ((ObjectNode) node).putArray("tracks").add(getTracks(id));
                if (images != null)
                {
                    ((ObjectNode) node).putArray("images").add(images);
                    break;
                }
            }

            return node;
        });
    }

    private JsonNode getTracks(String releaseId) {
        StringBuilder endPoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + "release/");
        endPoint.append(releaseId);
        endPoint.append("?fmt=json");
        endPoint.append("&inc=recordings");

        return process(endPoint, (node) -> {
            JsonNode tracks = node.findValue("tracks");
            return tracks;
        });
    }

    public JsonNode searchForAlbumsByArtist(String artistMbid) {
        StringBuilder endPoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + "release-group?query=arid:");
        endPoint.append(artistMbid);
        endPoint.append("%20AND%20primarytype:album%20AND%20status:official&fmt=json");

        return process(endPoint, (node) -> {
            JsonNode albums = node.get("release-groups");

            //iterate through each release group
            for (Iterator<JsonNode> it = albums.iterator(); it.hasNext(); )
            {
                JsonNode releaseGroup = it.next();
                String releaseDate = getReleaseDate(releaseGroup.get("id").textValue());
                ((ObjectNode) releaseGroup).put("releaseDate", releaseDate);

                //iterate through releases until we get album info from albumArtArchive
                JsonNode releases = releaseGroup.get("releases");

                //iterate through each release until we find images
                for (Iterator<JsonNode> itt = releases.iterator(); itt.hasNext(); )
                {
                    JsonNode release = itt.next();
                    String id = release.get("id").asText();
                    JsonNode images = getAlbumImage(id);
                    if (images != null)
                    {
                        ((ObjectNode) releaseGroup).putArray("images").add(images);
                        break;
                    }
                }
            }

            return albums;
        });
    }

    private String getReleaseDate(String releaseGroupId) {
        String endPoint = MUSIC_BRAINZ_ENDPOINT + "release-group/" + releaseGroupId;
        return process(new StringBuilder(endPoint), (node) -> node).get("first-release-date").asText();
    }

    private JsonNode getAlbumImage(String releaseId) {
        String endPoint = ALBUM_ART_ENDPOINT + releaseId;

        try
        {
            URL url = new URL(endPoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "application/json");
            connection.setRequestMethod("GET");

            StringBuilder data = new StringBuilder();
            String input;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));)
            {
                while ((input = in.readLine()) != null)
                    data.append(input);
            }

            if (data.length() == 0)
                return null;

            JsonNode node = objectMapper.readTree(data.toString());
            JsonNode images = node.get("images");
            return images;

        } catch (Exception e)
        {
            System.out.println(e);
        }

        return null;
    }

}