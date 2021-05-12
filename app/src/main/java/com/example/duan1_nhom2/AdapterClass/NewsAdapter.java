package com.example.duan1_nhom2.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duan1_nhom2.Model.Tintuc;
import com.example.duan1_nhom2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<Tintuc> {

    public NewsAdapter(Context context, int resource, List<Tintuc> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view =  inflater.inflate(R.layout.adapter_news, null);
        }
        Tintuc p = getItem(position);
        if (p != null) {
            TextView title = view.findViewById(R.id.tieude);
            TextView description=view.findViewById(R.id.mota);
            ImageView image=view.findViewById(R.id.image);


            title.setText(p.title);
            description.setText(p.description);
            Picasso.with(getContext()).load(p.image).into(image);
        }
        return view;
    }

}