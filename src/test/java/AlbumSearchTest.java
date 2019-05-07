import com.steven.hicks.beans.album.Album;
import com.steven.hicks.logic.AlbumQueryBuilder;
import com.steven.hicks.logic.AlbumSearcher;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlbumSearchTest
{
    @Test
    public void basicSearch()
    {
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("Integrity Blues").build();
        AlbumSearcher searcher = new AlbumSearcher();
        List<Album> albums = searcher.searchForAlbums(queryBuilder);
        assertTrue("Found dark side of the moon records", albums != null && albums.size()>0);
    }

    @Test
    public void searchForAlbumTest1()
    {
        String albumTitle = "disarm the descent";
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName(albumTitle).setLimit(1).setPage(1).build();
        AlbumSearcher searcher = new AlbumSearcher();
        List<Album> albums = searcher.searchForAlbums(queryBuilder);

        Album album = albums.get(0);

        Album full = searcher.getFullAlbum(album.getMbid(), album.getName(), album.getArtist());
        assertTrue("Wrong album artist found for " + albumTitle,full.getArtist().equalsIgnoreCase("Killswitch Engage"));
        LocalDate year = searcher.getAlbumDate(full.getMbid());
        assertEquals("Wrong year found for " + albumTitle, year, LocalDate.of(2013, 03, 27));

        System.out.println(full);
    }

    @Test
    public void searchForFullAlbum()
    {
        String albumTitle = "disarm the descent";
        AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("disarm the descent").setLimit(1).setPage(1).build();
        AlbumSearcher searcher = new AlbumSearcher();
        List<Album> albums = searcher.searchForAlbums(queryBuilder);

        Album album = albums.get(0);

        Album full = searcher.getFullAlbum(album.getMbid(), albumTitle, album.getArtist());
        assertTrue(albumTitle + " has images", full.getImage().length > 0);
        assertTrue(albumTitle + " has tracks", full.getTracks().getTrack().length > 0);
    }

}
