package com.example.bogystravels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyRecyclerViewAdapter.ItemClickListener{

    private static final String TAG = "MainActivity";
    private static final String ARG_NAME = "username";

    MyRecyclerViewAdapter adapter;

    public static void startActivity(Context context, String username) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(ARG_NAME, username);
        context.startActivity(intent);
    }

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textViewWelcome);
        if (getIntent().hasExtra(ARG_NAME)) {
            textView.setText(String.format("Welcome - %s", getIntent().getStringExtra(ARG_NAME)));
        }
        findViewById(R.id.buttonLogout).setOnClickListener(this);
        findViewById(R.id.buttonRead).setOnClickListener(this);
        findViewById(R.id.buttonAdd).setOnClickListener(this);

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();

        getAllData();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        getAllData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogout:
                signOut();
                break;
            case R.id.buttonAdd:
                launchAddSingleActivity();
                break;
            case R.id.buttonRead:
                getAllData();
                break;
        }
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Signed out of google");
                        launchLoginActivity();
                    }
                });

    }

    /*private void revokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google revoke access
        googleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Revoked Access");
                    }
                });
    }*/

    private void launchLoginActivity() {
        //startActivity(LoginActivity.class, "asd"));
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void launchAddSingleActivity() {
        //startActivity(LoginActivity.class, "asd"));
        startActivity(new Intent(this, AddSingleActivity.class));
    }

    private void launchEditSingleActivity(String value) {
        Intent i = new Intent(this, EditSingleActivity.class);
        i.putExtra("key",value);
        startActivity(i);
    }

    private void getAllData(){
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String firebaseID = firebaseAuth.getUid();

        db.collection("users").whereEqualTo("userId", firebaseID).orderBy("date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Map> arrayMap = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map temp = document.getData();
                        temp.put("id", document.getId());
                        arrayMap.add(temp);
                    }
                    populateRecycler(arrayMap);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void populateRecycler(ArrayList<Map> arrayMap){

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, arrayMap);
        adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener) this);
        recyclerView.setAdapter(adapter);

        Log.w(TAG, "Qk refresh batee!");
    }

    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        launchEditSingleActivity(adapter.getItem(position));
    }

    public String timestampToString(Timestamp timestamp){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        return DateFor.format(timestamp.toDate());
    }

}