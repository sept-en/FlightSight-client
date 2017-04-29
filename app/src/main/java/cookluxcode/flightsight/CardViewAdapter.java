package cookluxcode.flightsight;

import android.icu.text.AlphabeticIndex;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    //private List<int> routes;

    public CardViewAdapter (/*List<int> routes*/) {
        //this.routes = routes;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_route_card, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //int route = routes.get(i);
        //viewHolder.routeFrom.setText(route);
    }

    @Override
    public int getItemCount() {
        //return routes.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView routeFrom;
        private TextView routeTo;
        private TextView startTime;
        private TextView finishTime;

        public ViewHolder (View itemView)
        {
            super(itemView);
            routeFrom = (TextView) itemView.findViewById(R.id.card_textfield_from);
        }
    }
}
