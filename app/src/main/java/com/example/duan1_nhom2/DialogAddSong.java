package com.example.duan1_nhom2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.duan1_nhom2.Model.Nhac;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class DialogAddSong extends DialogFragment {
    Uri fileUri;
    TextView txtConfirm;
    EditText txtAddSongName, txtAddSongPerformer, txtAddSongCategory;
    StorageReference myStorageRef;
    DatabaseReference myDatabaseRef;
    UploadTask task;
    String songDuration = "0:00";
    String fileName = "";
    public DialogAddSong(Uri fileUri, String songDuration, String fileName){
        this.fileUri = fileUri;
        this.songDuration = songDuration;
        this.fileName = fileName;
    }
    public interface ProgressBarListener{
        void updateProgressBar(int progress);
    }
    ProgressBarListener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_music, container, false);
        findView(view);
        myDatabaseRef = FirebaseDatabase.getInstance().getReference("Nhac");
        myStorageRef = FirebaseStorage.getInstance().getReference("Nhac");
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task != null && task.isInProgress()){
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                }
                getDialog().dismiss();
            }
        });
        return view;
    }
    private void findView(View view){
        txtAddSongCategory = view.findViewById(R.id.txtAddSongCategory);
        txtAddSongName = view.findViewById(R.id.txtAddSongName);
        txtAddSongPerformer = view.findViewById(R.id.txtAddSongPerformer);
        txtConfirm = view.findViewById(R.id.txtConfirm);
    }
    private void uploadFile(){
        if (fileUri != null){
            final StorageReference fileRef;
            if (fileName.trim().equals("")){
                fileRef = myStorageRef.child(System.currentTimeMillis() + ".mp3");
            }else{
                fileRef = myStorageRef.child(fileName);
            }
            task = fileRef.putFile(fileUri);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
//                        Uri downloadUrl = task.getResult();
//                        String tenNhac = txtAddSongName.getText().toString();
//                        String tenNgheSi = txtAddSongPerformer.getText().toString();
//                        String theLoai = txtAddSongCategory.getText().toString();
//                        Nhac nhac = new Nhac(tenNhac, tenNgheSi,theLoai, songDuration, downloadUrl.toString());
//                        String maNhac = myDatabaseRef.push().getKey();
//                        myDatabaseRef.child(maNhac).setValue(nhac);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    listener.updateProgressBar((int)progress);
                }
            });
        }
    }

    public void setListener(DialogAddSong.ProgressBarListener listener){
        this.listener = listener;
    }
}
