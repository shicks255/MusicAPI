import com.fasterxml.jackson.databind.JsonNode;
import com.steven.hicks.logic.musicBrainz.MBAlbumSearcher;
import org.junit.BeforeClass;
import org.junit.Test;

public class MBAlbumSearchTest {

    private static MBAlbumSearcher mbAlbumSearcher;
    private static String FAN_ART_KEY;
    private static String SLAYER_MBID = "bdacc37b-8633-4bf8-9dd5-4662ee651aec";

    @BeforeClass
    public static void setup() {
        FAN_ART_KEY = System.getProperty("fanArtKey");
        mbAlbumSearcher = new MBAlbumSearcher();
    }

    @Test
    public void shouldSearchForSlayer() {
        JsonNode slayerAlbums = mbAlbumSearcher.searchForAlbumsByArtist(SLAYER_MBID);

        System.out.println(slayerAlbums);

    }
}
