package com.example.bledemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<AllShopMappingResponse> {

    private final Context context;
    private final List<AllShopMappingResponse> devices;


    public ListAdapter(Context context, List<AllShopMappingResponse> devices) {
        super(context, -1,  devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list, parent, false);
        TextView tVName = rowView.findViewById(R.id.tVName);
        TextView tVRSSI = rowView.findViewById(R.id.tVRSSI);
        tVName.setText(devices.get(position).getShopName());
        tVRSSI.setText("GID: " + devices.get(position).getGatewayId() + " SID: " + devices.get(position).getId());

        return rowView;
    }

}
