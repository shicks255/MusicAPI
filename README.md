## MusicAPI

API for searching for Artists and Albums

Uses Last.FM, MusicBrainz and FanArt

You need to provide API keys for Last.fm and FanArt.tv

Usage:

**ArtistSearching**:<br/>
ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().withName("Pink Floyd").build();<br/>
ArtistSearcher searcher = new ArtistSearcher(LastFmAPIKey, FanArtAPIKey);<br/>
List<Artists> artists = searcher.searchForArtists();<br/>

To get 'Full' Artist (this includes tags, and biography and images)<br/>
Artist fullArtist = artistSearcher.getFullArtist(someArtistObject);<br/>

One way to get albums:<br/>
List<ArtistAlbums> albums = artistSearcher.getAlbums(builder);<br/>

**AlbumSearching**:<br/>
AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("Dark Side of the Moon").build();<br/>
AlbumSearcher searcher = new AlbumSearcher(LAstFmAPIKey);<br/>
List<Album> albums = searcher.searchForAlbums(queryBuilder)<br/>

To get 'Full' album (this includes tags and releaseDate)<br/>
Album fullAlbum = searcher.getFullAlbum(album.getMbid(), album.getName(), album.getArtist());<br/>

