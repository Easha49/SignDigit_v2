package com.example.youtubeactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.youtubeactivity.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding binding;
    TextView name,email,birthday,phone,gender,UserName,Division,District ;
    SwipeRefreshLayout swipeRefreshLayout ;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    TextView Upload ;
    CircleImageView img;

    ActivityResultLauncher<String> launcher;
    FirebaseDatabase database;
    FirebaseStorage storage;
    CardView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        /*requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getSupportActionBar ().hide ();
        //getSupportActionBar ().setTitle ("                       Your Profile");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(Profile.this, Login.class));
        }
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        name=findViewById(R.id.name);
        email= findViewById(R.id.email);
        img = findViewById(R.id.image);
        birthday = findViewById(R.id.birthday);
        phone = findViewById(R.id.phone);
        logout = findViewById(R.id.logout);
        gender = findViewById(R.id.gender) ;
        Upload = findViewById(R.id.upload) ;
        UserName = findViewById(R.id.userName) ;
        swipeRefreshLayout = findViewById(R.id.reload) ;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        Division = findViewById(R.id.division) ;
        District = findViewById(R.id.district) ;

        StorageReference dc = storage.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        // Refresh the Whole page .-------------------------------------------------------------------------------------------------------------
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load Image from storage in ImageView
                dc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide
                                .with(Profile.this)
                                .load(uri) // the uri you got from Firebase
                                .circleCrop()
                                .override(600,600)
                                .into(img); //Your imageView variable
                        Toast.makeText(Profile.this, "Success", Toast.LENGTH_SHORT).show();
                        String userid = firebaseAuth.getCurrentUser().getUid() ;
                        DocumentReference documentReference = firebaseFirestore.collection("users").document(userid) ;

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // --------------------------------------------------------------------------------------------------------------------------------------

        // set user wallpaper to the CircleImageView ----------------------------------------------------------------------------------------------
        dc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(Profile.this)
                        .load(uri) // the uri you got from Firebase
                        .circleCrop()
                        .override(600,600)
                        .into(img); //Your imageView variable
                Toast.makeText(Profile.this, "Success", Toast.LENGTH_SHORT).show();
                String userid = firebaseAuth.getCurrentUser().getUid() ;
                DocumentReference documentReference = firebaseFirestore.collection("users").document(userid) ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // --------------------------------------------------------------------------------------------------------------------------------------


        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {

                    @Override
                    public void onActivityResult(Uri result) {
                        binding.image.setImageURI(result);

                        StorageReference reference = storage.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());

                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //  Toast.makeText(ProfileActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri result) {

                                                        database.getReference().child("users").child("Image").child(firebaseAuth.getCurrentUser().getUid())
                                                                .setValue(result.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(Profile.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Profile.this, "not uploaded", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Profile.this, "failed uploaded", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");
            }
        });

        // Loading Other information from firebase firestore .


        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("username"));
                email.setText("E-Mail: " + value.getString("email"));
                phone.setText("Contact No: " + value.getString("phone"));
                birthday.setText("Date of Birth: " + value.getString("dateOfBirth"));
                gender.setText("Physical Condition: " + value.getString("gender"));
                UserName.setText("Full Name: " + value.getString("fname"));
                Division.setText("Division: " + value.getString("division"));
                District.setText("District: " + value.getString("district"));
            }
        });

        // for User Logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("isLoggedin");
                editor.commit();
                //Toast.makeText(UserProfile_Page.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Profile.this, Login.class));
                finish();
            }
        });

    }
}