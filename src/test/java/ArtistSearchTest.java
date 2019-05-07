import com.steven.hicks.beans.ArtistAlbums;
import com.steven.hicks.beans.album.Album;
import com.steven.hicks.beans.artist.Artist;
import com.steven.hicks.logic.AlbumSearcher;
import com.steven.hicks.logic.ArtistQueryBuilder;
import com.steven.hicks.logic.ArtistSearcher;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class ArtistSearchTest
{
    @Test
    public void basicTest()
    {
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName("Pink Floyd").build();
        ArtistSearcher searcher = new ArtistSearcher();
        List<Artist> artists = searcher.searchForArtists(builder);
        assertTrue("Cant query Pink Floyd", artists!=null && artists.size() > 0 && artists.get(0).getName().equalsIgnoreCase("pink floyd"));
    }

    @Test
    public void artistSearchTest1()
    {
        String artistName = "Pink Floyd";
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName(artistName).setLimit(1).build();
        ArtistSearcher searcher = new ArtistSearcher();
        List<Artist> artists = searcher.searchForArtists(builder);

        Artist fullArtist = searcher.getFullArtist(artists.get(0).getMbid());
        AlbumSearcher albumSearcher = new AlbumSearcher();

        List<ArtistAlbums> albums = searcher.getAlbums(new ArtistQueryBuilder.Builder().mbid(fullArtist.getMbid()).build());
        albums.sort(Comparator.comparing(ArtistAlbums::getPlaycount).reversed());
        List<Album> fullAlbums = albums.stream().map(x -> albumSearcher.getFullAlbum(x.getMbid(), x.getName(),artistName)).collect(Collectors.toList());

        assertTrue(artistName + " has albums", albums.size() > 0);
        assertTrue(artistName + " has albums that were able to get full", fullAlbums.size() > 0);
    }

    @Test
    public void artistSearchTest2()
    {
        String artistName = "The World Is A Beautiful Place";
        ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().artistName(artistName).setLimit(1).build();
        ArtistSearcher searcher = new ArtistSearcher();
        List<Artist> artists = searcher.searchForArtists(builder);

        Artist fullArtist = searcher.getFullArtist(artists.get(0).getMbid());
        AlbumSearcher albumSearcher = new AlbumSearcher();

        List<ArtistAlbums> albums = searcher.getAlbums(new ArtistQueryBuilder.Builder().mbid(fullArtist.getMbid()).build());
        albums.sort(Comparator.comparing(ArtistAlbums::getPlaycount).reversed());
        List<Album> fullAlbums = albums.stream().map(x -> albumSearcher.getFullAlbum(x.getMbid(), x.getName(),fullArtist.getName())).collect(Collectors.toList());

        assertTrue(artistName + " has albums", albums.size() > 0);
        assertTrue(artistName + " has albums that were able to get full", fullAlbums.size() > 0);
    }
}
