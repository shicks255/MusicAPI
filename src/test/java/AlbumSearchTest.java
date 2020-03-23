import com.steven.hicks.beans.album.Album;
import com.steven.hicks.logic.AlbumQueryBuilder;
import com.steven.hicks.logic.AlbumSearcher;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlbumSearchTest
{
    private static String lastFmKey = "";
    private static AlbumSearcher albumSearcher;

    @BeforeClass
    public static void setup() {
        lastFmKey = System.getProperty("lastFmKey");
        albumSearcher = new AlbumSearcher(lastFmKey);
    }

    @Test
    public void shouldSearchForAlbums()
    {
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("Dark Side of the Moon").build();
        AlbumSearcher searcher = new AlbumSearcher(lastFmKey);
        List<Album> albums = searcher.searchForAlbums(queryBuilder);
        assertTrue("Unable to find dark side of the moon", albums != null && albums.size()>0);
    }

    @Test
    public void shouldReturnZeroAlbums() {
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("safkljlelkjldklm").build();
        AlbumSearcher searcher = new AlbumSearcher(lastFmKey);
        List<Album> albums = searcher.searchForAlbums(queryBuilder);
        assertTrue("No album should have been found for search safkljlelkjldklm", albums != null && albums.size() == 0);
    }

    @Test
    public void shouldReturnFullAlbum()
    {
        String albumTitle = "disarm the descent";
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName(albumTitle).setLimit(10).setPage(1).build();
        AlbumSearcher searcher = new AlbumSearcher(lastFmKey);
        List<Album> albums = searcher.searchForAlbums(queryBuilder);
        Album album = albums.get(0);

        Album fullAlbum = searcher.getFullAlbum(album.getMbid(), album.getName(), album.getArtist());
        assertTrue("Wrong album artist found for " + albumTitle, fullAlbum.getArtist().equalsIgnoreCase("Killswitch Engage"));
        LocalDate year = searcher.getAlbumDate(fullAlbum.getMbid());
        assertEquals("Wrong year found for " + albumTitle, year, LocalDate.of(2013, 03, 27));
    }

    @Test
    public void searchForFullAlbum()
    {
        String albumTitle = "disarm the descent";
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("disarm the descent").setLimit(1).setPage(1).build();
        AlbumSearcher searcher = new AlbumSearcher(lastFmKey);
        List<Album> albums = searcher.searchForAlbums(queryBuilder);

        Album album = albums.get(0);

        Album full = searcher.getFullAlbum(album.getMbid(), albumTitle, album.getArtist());
        assertTrue(albumTitle + " has images", full.getImage().length > 0);
        assertTrue(albumTitle + " has tracks", full.getTracks().getTrack().length > 0);
    }

}
