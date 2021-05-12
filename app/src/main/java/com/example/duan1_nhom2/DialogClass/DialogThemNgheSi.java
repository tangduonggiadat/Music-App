package com.example.duan1_nhom2.DialogClass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.duan1_nhom2.AdditionalFunctions.AdditionalFunctions;
import com.example.duan1_nhom2.Model.NgheSi;
import com.example.duan1_nhom2.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DialogThemNgheSi extends DialogFragment {
    EditText txtTenNgheSi, txtNgaySinh, txtThongTinThem, txtThemNgheSi;
    ImageView ivArtistIcon;
    TextView txtXacNhan;
    Button btnChonAnh;
    ProgressBar pbUploadNgheSi;
    UploadTask uploadTask;
    Uri uri = null;
    String fileName = null;
    DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference("NgheSi");
    StorageReference myStorageRef = FirebaseStorage.getInstance().getReference("ImageNgheSi");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_them_nghesi, container, false);
        findView(view);
        txtXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadArtist();
            }
        });
        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFile();
            }
        });
        return view;
    }

    private void findView(View view) {
        txtTenNgheSi = view.findViewById(R.id.txtTenNgheSi);
        txtNgaySinh = view.findViewById(R.id.txtNgaySinh);
        txtThongTinThem = view.findViewById(R.id.txtThongTinThem);
        ivArtistIcon = view.findViewById(R.id.ivArtistIcon);
        txtXacNhan = view.findViewById(R.id.txtXacNhan);
        btnChonAnh = view.findViewById(R.id.btnChonAnh);
        txtThemNgheSi = view.findViewById(R.id.txtThemNgheSi);
        pbUploadNgheSi = view.findViewById(R.id.pbUploadNgheSi);
    }

    private void chooseImageFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void uploadArtist() {
        final String tenNgheSi = txtTenNgheSi.getText().toString();
        final String ngaySinh = txtNgaySinh.getText().toString();
        final String thongTinThem = txtThongTinThem.getText().toString();
        if (!AdditionalFunctions.isStringEmpty(getContext(), tenNgheSi, ngaySinh, thongTinThem)) {
            return;
        }
        if (uri == null) {
            uploadArtistWithoutImage(tenNgheSi, ngaySinh, thongTinThem);
        } else {
            uploadArtistWithImage(tenNgheSi, ngaySinh, thongTinThem);
        }

    }

    private void uploadArtistWithImage(final String tenNgheSi, final String ngaySinh, final String thongTinThem) {
        final StorageReference fileRef;
        if (fileName.trim().equals("")) {
            fileRef = myStorageRef.child(System.currentTimeMillis() + ".mp3");
        } else {
            fileRef = myStorageRef.child(fileName);
        }
        uploadTask = fileRef.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String urlAnh = task.getResult().toString();
                    String maNgheSi = myDatabaseRef.push().getKey();
                    NgheSi ngheSi = new NgheSi(maNgheSi, tenNgheSi, 0, 0, thongTinThem, urlAnh);
                    myDatabaseRef.child(maNgheSi).setValue(ngheSi);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progress = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pbUploadNgheSi.setProgress(progress);
                if (progress == 100){
                    AdditionalFunctions.changeTextInMilisecond(txtThemNgheSi,"Thành Công!","Thêm Nghệ Sĩ");
                }
            }
        });
    }

    private void uploadArtistWithoutImage(final String tenNgheSi, final String ngaySinh, final String thongTinThem) {
        String maNgheSi = myDatabaseRef.push().getKey();
        NgheSi ngheSi = new NgheSi(maNgheSi, tenNgheSi, 0, 0, thongTinThem, "NoImage");
        myDatabaseRef.child(maNgheSi).setValue(ngheSi).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    txtThemNgheSi.setText("Thành Công!");
                    AdditionalFunctions.changeTextInMilisecond(txtThemNgheSi,"Thành Công!","Thêm Nghệ Sĩ");
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            this.uri = uri;
            fileName = AdditionalFunctions.getFileName(getContext(), uri);
            Picasso.with(getContext()).load(uri).into(ivArtistIcon);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams)layoutParams);
    }
}
