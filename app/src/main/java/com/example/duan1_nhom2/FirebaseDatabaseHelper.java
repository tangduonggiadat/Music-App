package com.example.duan1_nhom2;

import androidx.annotation.NonNull;

import com.example.duan1_nhom2.Model.Nhac;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    FirebaseDatabase mData;
    DatabaseReference myRef;
    List<Nhac> dsn = new ArrayList<>();
    public interface DataStatus{
        void DataIsLoaded(List<Nhac> dsn, List<String> keys);
    }
    public FirebaseDatabaseHelper(){
        mData = FirebaseDatabase.getInstance();
        myRef = mData.getReference().child("Nhac");
    }
    public void readNhac(final DataStatus dataStatus){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dsn.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot i: snapshot.getChildren()){
                    keys.add(i.getKey());
                    dsn.add(i.getValue(Nhac.class));
                }
                dataStatus.DataIsLoaded(dsn, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
