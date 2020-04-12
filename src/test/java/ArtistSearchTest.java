import com.steven.hicks.beans.ArtistAlbums;
import com.steven.hicks.beans.artist.Artist;
import com.steven.hicks.logic.ArtistQueryBuilder;
import com.steven.hicks.logic.ArtistSearcher;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ArtistSearchTest
{
    private static String lastFmKey = "";
    private static String fanArtKey = "";
    private static ArtistSearcher artistSearcher;

    @BeforeClass
    public static void setup() {
        lastFmKey = System.getProperty("lastFmKey");
        fanArtKey = System.getProperty("fanArtKey");
        artistSearcher = new ArtistSearcher(lastFmKey, fanArtKey);
    }

    @Test
    public void queryShouldGetList() {
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName("Pink Floyd").build();
        List<Artist> artists = artistSearcher.searchForArtists(builder);
        assert(artists.size() > 0);
    }

    @Test
    public void queryShouldContainArtist() {
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName("Pink Floyd").build();
        List<Artist> artists = artistSearcher.searchForArtists(builder);
        assertTrue("Cant query Pink Floyd", artists != null && artists.size() > 0 && artists.get(0).getName().equalsIgnoreCase("pink floyd"));
    }

    @Test
    public void queryShouldReturnEmptyList() {
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName("asfdceazcxzzxczc").build();
        List<Artist> artists = artistSearcher.searchForArtists(builder);
        assertTrue(artists.size() == 0);
    }

    @Test
    public void shouldGetFullArtist() {
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName("Pink Floyd").build();
        List<Artist> artists = artistSearcher.searchForArtists(builder);
        Artist pinkFloyd = artists.get(0);
        Artist fullPinkFloyd = artistSearcher.getFullArtist(pinkFloyd);

        assertTrue(fullPinkFloyd.getTags().getTag().length > 0);
        assertTrue(fullPinkFloyd.getImage().length > 0);
    }

    @Test
    public void shouldHaveAlbum() {
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName("Pink Floyd").setLimit(1).build();
        List<Artist> artists = artistSearcher.searchForArtists(builder);

        ArtistQueryBuilder albumBuilder = new ArtistQueryBuilder.Builder().mbid(artists.get(0).getMbid()).build();
        List<ArtistAlbums> albums = artistSearcher.getAlbums(albumBuilder);
        assertTrue(albums.size() > 0);
    }
}