package cookluxcode.flightsight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cookluxcode.flightsight.db.RouteModel;

/**
 * Created by dan on 30.04.17.
 */

public class MyRoutesActivity extends Activity {
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CardViewAdapter(
                this,
                SQLite.select().from(RouteModel.class).queryList()));
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        Intent intent = new Intent(this, CreateRouteActivity.class);
        startActivity(intent);
    }
}
