package cookluxcode.flightsight.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dan on 30.04.17.
 */

/*
    EntityId:
    https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&sites=enwiki&titles=Kiev&prop=extracts&languages=en

    ImageName:
    https://www.wikidata.org/w/api.php?action=wbgetclaims&entity=Q1899&property=P18

    ImageUrls:
    https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&titles=File:Panorama%20of%20Kyiv%20from%20Saint%20Sophia%20Monastery.jpg
 */

public interface WikidataApi {
    @GET("/w/api.php")
    Observable<String> getEntities(
            @Query("action") String action, // wbgetentities
            @Query("format") String format, // json
            @Query("sites") String sites, // enwiki
            @Query("prop") String prop, // extracts,
            @Query("languages") String languages, // en
            @Query("titles") String title);

    @GET("/w/api.php")
    Observable<String> getImageNames(
            @Query("format") String format, // json
            @Query("action") String action, // wbgetclaims
            @Query("property") String property, // P18
            @Query("entity") String entityId);
}
