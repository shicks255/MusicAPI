package com.steven.hicks.logic.musicBrainz;

import com.fasterxml.jackson.databind.JsonNode;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MBAlbumSearcher {
    private static String MUSIC_BRAINZ_ENDPOINT = "https://musicbrainz.org/ws/2/";
    private static String ALBUM_ART_ENDPOINT = "https://musicbrainz.org/ws/2/";

    public void searchForAlbum(String albumTitle) {
        StringBuilder endpoint = new StringBuilder(MUSIC_BRAINZ_ENDPOINT + "release-group?query=artist:");
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
}
