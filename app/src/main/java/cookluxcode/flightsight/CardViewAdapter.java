package cookluxcode.flightsight;

import android.content.Context;
import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cookluxcode.flightsight.db.RouteModel;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    List<RouteModel> routes;
    Context context;

    public CardViewAdapter (Context context, List<RouteModel> routes) {
        this.context = context;
        this.routes = routes;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_route_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final RouteModel route = routes.get(i);
        LatLng from = new LatLng(route.getStartLat(), route.getStartLng());
        LatLng to = new LatLng(route.getFinishLat(), route.getFinishLng());
        viewHolder.routeFrom.setText(Utils.getLocalityName(context, from));
        viewHolder.routeTo.setText(Utils.getLocalityName(context, to));
        viewHolder.distance.setText(String.format("%.2f km", Utils.distance(from, to) / 1000));
        viewHolder.startTime.setText(route.getStartTime() != null ? route.getStartTime() : "");
        viewHolder.finishTime.setText(route.getEndTime() != null ? route.getEndTime() : "");


        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FlightRouteActivity.class);
                intent.putExtra(FlightRouteActivity.ROUTE_ID_EXTRA, route.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        @BindView(R.id.card_textfield_from) TextView routeFrom;
        @BindView(R.id.card_textfield_to) TextView routeTo;
        @BindView(R.id.card_textfield_start_time) TextView startTime;
        @BindView(R.id.card_textfield_finish_time) TextView finishTime;
        @BindView(R.id.card_textfield_distance) TextView distance;

        public ViewHolder (View itemView)
        {
            super(itemView);
            root = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
