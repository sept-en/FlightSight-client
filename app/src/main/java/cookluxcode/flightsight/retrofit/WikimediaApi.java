package cookluxcode.flightsight.retrofit;

//ImageUrls:
//        https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&titles=File:Panorama%20of%20Kyiv%20from%20Saint%20Sophia%20Monastery.jpg

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikimediaApi {
    @GET("/w/api.php")
    Observable<String> getImageUrls(
            @Query("format") String format, // json
            @Query("action") String action, // query,
            @Query("prop") String prop, // imageinfo
            @Query("iiprop") String iiprop, // url
            @Query("titles") String filename); // File:%your_file%
}
