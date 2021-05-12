package com.example.duan1_nhom2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1_nhom2.Model.Nhac;

import java.util.List;

public class RecyclerView_Config {
    Context context;
    NhacAdapter adapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<Nhac> dsn, List<String> keys){
        this.context = context;
        this.adapter = new NhacAdapter(dsn, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }
    class NhacItemView extends RecyclerView.ViewHolder {
        String key;
        TextView txtTenNhac, txtTenNgheSi, txtTheLoai, txtThoiLuong, txtURL;
        public NhacItemView(ViewGroup parent) {
            super(LayoutInflater.from(context).inflate(R.layout.adapter, parent, false));
            txtTenNgheSi = itemView.findViewById(R.id.txtTenNgheSi);
            txtTenNhac = itemView.findViewById(R.id.txtTenNhac);
            txtTheLoai = itemView.findViewById(R.id.txtTheLoai);
            txtThoiLuong = itemView.findViewById(R.id.txtThoiLuong);
            txtURL = itemView.findViewById(R.id.txtURL);
        }
        public void bind(Nhac nhac, String key){
            txtTenNhac.setText(nhac.getTenNhac());
            txtTenNgheSi.setText(nhac.getTenNgheSi());
            txtTheLoai.setText(nhac.getTheLoai());
            txtThoiLuong.setText(nhac.getThoiLuong());
            txtURL.setText(nhac.getURL());
            this.key = key;
        }
    }
    class NhacAdapter extends RecyclerView.Adapter<NhacItemView>{
        List<Nhac> dsn;
        List<String> keys;

        public NhacAdapter(List<Nhac> dsn, List<String> keys) {
            this.dsn = dsn;
            this.keys = keys;
        }

        @NonNull
        @Override
        public NhacItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NhacItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NhacItemView holder, int position) {
            holder.bind(dsn.get(position), keys.get(position));
        }

        @Override
        public int getItemCount() {
            return dsn.size();
        }
    }
}
