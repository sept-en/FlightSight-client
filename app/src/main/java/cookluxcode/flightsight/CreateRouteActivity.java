package cookluxcode.flightsight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cookluxcode.flightsight.db.PlaceModel;
import cookluxcode.flightsight.db.RouteModel;
import cookluxcode.flightsight.model.Place;
import cookluxcode.flightsight.retrofit.ServerApi;
import cookluxcode.flightsight.retrofit.WikidataApi;
import cookluxcode.flightsight.retrofit.WikimediaApi;
import cookluxcode.flightsight.retrofit.WikipediaApi;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CreateRouteActivity extends Activity {
    private static final String TAG = CreateRouteActivity.class.getSimpleName();
    private static final int REQUEST_CODE_FROM_LOCATION = 1;
    private static final int REQUEST_CODE_TO_LOCATION = 2;

    @BindView(R.id.from_text) EditText fromText;
    @BindView(R.id.to_text) EditText toText;
    @BindView(R.id.create_button) Button createButton;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.places_count) TextView placesCount;
    @BindView(R.id.progress_layout) LinearLayout progressLayout;

    LatLng from;
    LatLng to;
    Retrofit retrofit;
    ServerApi serverApi;
    WikidataApi wikidataApi;
    WikimediaApi wikimediaApi;
    WikipediaApi wikipediaApi;
    int requestId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);
        ButterKnife.bind(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_URL)
//                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
//        serverApi = retrofit.create(ServerApi.class);
        wikidataApi = new Retrofit.Builder()
                .baseUrl("https://www.wikidata.org")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(WikidataApi.class);

        wikimediaApi = new Retrofit.Builder()
                .baseUrl("https://commons.wikimedia.org")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(WikimediaApi.class);

        wikipediaApi = new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(WikipediaApi.class);

        requestId = new Random().nextInt();
    }

    @OnClick(R.id.pick_from_button)
    public void onPickFromClick() {
        Log.d(TAG, "onPickFromClick()");
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FROM_LOCATION);
    }

    @OnClick(R.id.pick_to_button)
    public void onPickToClick() {
        Log.d(TAG, "onPickToClick()");
        Intent intent = new Intent(this, PickLocationActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TO_LOCATION);
    }

    @OnClick(R.id.create_button)
    public void onCreateClick() {
        Log.d(TAG, "onCreateClick()");
        if (from == null || to == null) {
            Toast.makeText(this, R.string.please_pick, Toast.LENGTH_SHORT).show();
            return;
        }

        final RouteModel route = new RouteModel(from, to);
        final long id = FlowManager.getModelAdapter(RouteModel.class).insert(route);
        final AtomicInteger count = new AtomicInteger(0);

        final List<PlaceModel> placeModels = Collections.synchronizedList(new ArrayList<PlaceModel>());

        createButton.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);

        Utils.getLocalities(this, from, to, 300000)
                .flatMap(new Function<Pair<Place, String>, ObservableSource<Place>>() {
                    @Override
                    public ObservableSource<Place> apply(@NonNull Pair<Place, String> placeStringPair) throws Exception {
                        final Place place = placeStringPair.first;
                        String engName = placeStringPair.second;
                        return getDescription(engName).zipWith(
                                getImageUrl(engName),
                                new BiFunction<String, String, Place>() {
                                    @Override
                                    public Place apply(@NonNull String desc, @NonNull String imgUrl) throws Exception {
                                        place.description = desc;
                                        if (!imgUrl.isEmpty()) place.imageUrls.add(imgUrl);
                                        return place;
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Place>() {
                            @Override
                            public void accept(@NonNull Place place) throws Exception {
                                placesCount.setText(getString(R.string.places_loaded, count.incrementAndGet()));
                                placeModels.add(new PlaceModel(place, route));
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Log.e(TAG, Log.getStackTraceString(throwable));
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                FlowManager.getModelAdapter(PlaceModel.class).insertAll(placeModels);
                                createButton.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.INVISIBLE);

                                Intent intent = new Intent(CreateRouteActivity.this, FlightRouteActivity.class);
                                intent.putExtra(FlightRouteActivity.ROUTE_ID_EXTRA, id);
                                startActivity(intent);
                            }
                        },
                        new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
//                                if (disposable != null)
//                                    disposable.dispose();
                            }
                        });

//        FlowManager.getModelAdapter(RouteModel.class).load(route);
//
//        List<PlaceModel> placeModels = new ArrayList<>();
//        for (Place place : places)
//            placeModels.add(new PlaceModel(place, route));
//        FlowManager.getModelAdapter(PlaceModel.class).insertAll(placeModels);
//
//        Intent intent = new Intent(CreateRouteActivity.this, FlightRouteActivity.class);
//        intent.putExtra(FlightRouteActivity.ROUTE_ID_EXTRA, id);
//        startActivity(intent);
//        serverApi.getPlaces(requestId, from.latitude, from.longitude, to.latitude, to.longitude)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Consumer<Places>() {
//                           @Override
//                           public void accept(Places places) throws Exception {
//                               Log.d(TAG, "Places request succesful");
//                               createButton.setVisibility(View.VISIBLE);
//                               progressBar.setVisibility(View.INVISIBLE);
//
//                               RouteModel route = new RouteModel(from, to);
//
//                               long id = FlowManager.getModelAdapter(RouteModel.class).insert(route);
//                               FlowManager.getModelAdapter(RouteModel.class).load(route);
//
//                               List<PlaceModel> placeModels = new ArrayList<>();
//                               for (Place place : places.objects)
//                                   placeModels.add(new PlaceModel(place, route));
//                               FlowManager.getModelAdapter(PlaceModel.class).insertAll(placeModels);
//
//                               Intent intent = new Intent(CreateRouteActivity.this, FlightRouteActivity.class);
//                               intent.putExtra(FlightRouteActivity.ROUTE_ID_EXTRA, id);
//                               startActivity(intent);
//                           }
//                       },
//                        new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                Log.e(TAG, "Getting places error");
//                                Log.e(TAG, Log.getStackTraceString(throwable));
//                            }
//                        });
        createButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FROM_LOCATION:
                if (resultCode == RESULT_OK) {
                    from = data.getParcelableExtra(PickLocationActivity.LAT_LNG_EXTRA);
                    String name = Utils.getLocalityName(this, from);
                    fromText.setText(name != null ? name : from.toString());
                }
                break;
            case REQUEST_CODE_TO_LOCATION:
                if (resultCode == RESULT_OK) {
                    to = data.getParcelableExtra(PickLocationActivity.LAT_LNG_EXTRA);
                    String name = Utils.getLocalityName(this, to);
                    toText.setText(name != null ? name : to.toString());
                }
                break;

        }
    }

    public Observable<String> getDescription(String cityName) {
        return wikipediaApi.getDescriptionObject("json", "query", "extracts", "", "", cityName)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        JSONObject pages = new JSONObject(s).getJSONObject("query").getJSONObject("pages");
                        return pages.getJSONObject(pages.keys().next()).getString("extract");
                    }
                });
    }

    public Observable<String> getImageUrl(String cityName) {
        return wikidataApi.getEntities("wbgetentities", "json", "enwiki", "extracts", "en", cityName)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        String entityId = new JSONObject(s).getJSONObject("entities").keys().next();
                        return wikidataApi.getImageNames("json", "wbgetclaims", "P18", entityId);
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull String s) throws Exception {
                        try {
                            String imageName =
                                    new JSONObject(s)
                                            .getJSONObject("claims")
                                            .getJSONArray("P18")
                                            .getJSONObject(0)
                                            .getJSONObject("mainsnak")
                                            .getJSONObject("datavalue")
                                            .getString("value");

                            return wikimediaApi.getImageUrls("json", "query", "imageinfo", "url", String.format("File:%s", imageName));
                        } catch (JSONException ex) {
                            return Observable.just("");
                        }
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        try {
                            JSONObject pages = new JSONObject(s).getJSONObject("query").getJSONObject("pages");
                            return
                                    pages.getJSONObject(pages.keys().next())
                                            .getJSONArray("imageinfo")
                                            .getJSONObject(0)
                                            .getString("url");
                        } catch (JSONException ex) {
                            return "";
                        }
                    }
                });
    }
}
