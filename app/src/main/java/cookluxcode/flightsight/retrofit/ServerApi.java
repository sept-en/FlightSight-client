package cookluxcode.flightsight.retrofit;

import cookluxcode.flightsight.model.Places;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServerApi {
    @GET("/v1/route_places/by_latlng/{id}/{start_lat}/{start_lng}/{finish_lat}/{finish_lng}")
    Observable<Places> getPlaces(
            @Path("id") int requestId,
            @Path("start_lat") double startLat,
            @Path("start_lng") double startLng,
            @Path("finish_lat") double finishLat,
            @Path("finish_lng") double finishLng);

}
