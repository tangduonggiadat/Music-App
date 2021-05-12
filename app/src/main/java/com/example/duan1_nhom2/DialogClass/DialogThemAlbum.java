package com.example.duan1_nhom2.DialogClass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.duan1_nhom2.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class DialogThemAlbum extends DialogFragment {
    EditText txtTenAlbum, txtTenNgheSi;
    TextView txtThemAlbum, txtXacNhan;
    ImageView ivAlbumIcon;
    Button btnChonAnh;
    ProgressBar pbUploadAlbum;
    UploadTask uploadTask;
    DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("Albums");
    StorageReference myStorageRef = FirebaseStorage.getInstance().getReference("Albums");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_them_album, container, false);
        return view;
    }
    private void findView(View view){
        txtTenAlbum = view.findViewById(R.id.txtTenAlbum);
        txtTenNgheSi = view.findViewById(R.id.txtTenNgheSi);
        txtThemAlbum = view.findViewById(R.id.txtThemAlbum);
        txtXacNhan = view.findViewById(R.id.txtXacNhan);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        pbUploadAlbum = view.findViewById(R.id.pbUploadAlbum);
    }
    private void chooseImageFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    private void uploadAlbum(){
        final String tenAlbum = txtTenAlbum.getText().toString();
        final String tenNgheSi = txtTenNgheSi.getText().toString();
        final StorageReference fileRef;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
        }
    }
}
