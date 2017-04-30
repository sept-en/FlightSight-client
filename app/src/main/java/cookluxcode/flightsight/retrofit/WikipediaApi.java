package cookluxcode.flightsight.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dan on 30.04.17.
 */
//Description:
//        https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=Stack%20Overflow

public interface WikipediaApi {
    @GET("/w/api.php")
    Observable<String> getDescriptionObject(
            @Query("format") String format, // json
            @Query("action") String action, // query
            @Query("prop") String prop, // extracts
            @Query("exintro") String exintro, // ""
            @Query("explaintext") String explainText, // ""
            @Query("titles") String title);
}
