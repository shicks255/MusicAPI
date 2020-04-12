import com.fasterxml.jackson.databind.JsonNode;
import com.steven.hicks.logic.musicBrainz.MBArtistSearcher;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;

public class MBArtistSearchTest {

    private static MBArtistSearcher m_mbArtistSearcher;
    private static String fanArtKey = "";
    private static String SLAYER_MBID = "bdacc37b-8633-4bf8-9dd5-4662ee651aec";


    @BeforeClass
    public static void setup() {
        fanArtKey = System.getProperty("fanArtKey");
        m_mbArtistSearcher = new MBArtistSearcher();
    }

    @Test
    public void shouldSearchAndGetResults() {
        JsonNode artists = m_mbArtistSearcher.searchForArtist("slayer");

        for (Iterator<JsonNode> artist = artists.iterator(); artist.hasNext(); ) {
            String id = artist.next().get("id").asText();
            if (id.equals(SLAYER_MBID)) {
                assertTrue("Slayer was not found",true);
                return;
            }
        }

        assertTrue("Slayer was not found", false);
    }

    @Test
    public void shouldSearchAndGetResultsWithImages() {
        JsonNode slayer = m_mbArtistSearcher.searchForArtistWithImages("slayer", fanArtKey);

        for (Iterator<JsonNode> artistResult = slayer.iterator(); artistResult.hasNext(); ) {
            JsonNode artist = artistResult.next();
            assertTrue(artist.has("images"));
        }
    }

    @Test
    public void shouldGetSlayer() {
        JsonNode slayer = m_mbArtistSearcher.getArtist(SLAYER_MBID);

        assertTrue("Slayer did not have name property", slayer.has("name"));
        assertTrue("Slayer name property did not = Slayer", slayer.get("name").textValue().equals("Slayer"));
    }

    @Test
    public void shouldGetSlayerWithImages() {
        JsonNode slayer = m_mbArtistSearcher.getArtistWithImages(SLAYER_MBID, fanArtKey);

        assertTrue("Slayer has image element", slayer.has("images"));
        JsonNode images = slayer.get("images");
        assertTrue("Slayer did not have images", images.size() > 0);
    }

}
