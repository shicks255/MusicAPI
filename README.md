# MusicAPI

API for searching for Artists and Albums

Uses Last.FM, MusicBrainz and FanArt

You need to provide API keys for Last.fm and FanArt.tv

Usage:

ArtistSearching:
ArtistQueryBuilder builder = new ArtistQueryBuilder.Builder().withName("Pink Floyd").build();
ArtistSearcher searcher = new ArtistSearcher(LastFmAPIKey, FanArtAPIKey);
List<Artists> artists = searcher.searchForArtists();

To get 'Full' Artist (this includes tags, and biography and images)
Artist fullArtist = artistSearcher.getFullArtist(someArtistObject);

One way to get albums:
List<ArtistAlbums> albums = artistSearcher.getAlbums(builder);

AlbumSearching:
AlbumQueryBuilder queryBuilder = new AlbumQueryBuilder.Builder().albumName("Dark Side of the Moon").build();
AlbumSearcher searcher = new AlbumSearcher(LAstFmAPIKey);
List<Album> albums = searcher.searchForAlbums(queryBuilder)

To get 'Full' album (this includes tags and releaseDate)
Album fullAlbum = searcher.getFullAlbum(album.getMbid(), album.getName(), album.getArtist());

