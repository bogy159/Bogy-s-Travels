package com.example.bogystravels;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AddSingleActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AddSingleActivity";
    private static String APIKEY;

    TextView editTextTextPersonName;
    TextView editTextTextPostalAddress;

    private TextView mDisplayDate;
    private TextView mDisplayDate2;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;

    private List<String> suggestions = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_single);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            APIKEY = extras.getString("apiKey");
        }
        System.out.println("Klucha mi e: " + APIKEY);

        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        editTextTextPostalAddress = findViewById(R.id.editTextTextPostalAddress);
        mDisplayDate = findViewById(R.id.editTextDate);
        mDisplayDate2 = findViewById(R.id.editTextDate2);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(Objects.requireNonNull(formatter.parse(mDisplayDate.getText().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddSingleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDisplayDate2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(Objects.requireNonNull(formatter.parse(mDisplayDate2.getText().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddSingleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                mDisplayDate2.setText(date);
            }
        };

        findViewById(R.id.buttonOK).setOnClickListener(this);
        findViewById(R.id.buttonCancel).setOnClickListener(this);


        AutoCompleteTextView editText = findViewById(R.id.editTextTextPostalAddress);
        adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, suggestions);
        adapter.setNotifyOnChange(true);
        editText.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {

            private Handler mHandler = new Handler();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 1000);
            }



        });
    }

    Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            CharSequence s = editTextTextPostalAddress.getText();
            try {
                suggestions = apiCall(APIKEY, s.toString());
                System.out.println("Quarry for string: " + s);

                if (!suggestions.isEmpty()){
                    adapter.clear();
                    adapter.addAll(suggestions);
                    adapter.notifyDataSetChanged();
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            //Force the adapter to filter itself, necessary to show new data.
            //Filter based on the current text because api call is asynchronous.
            adapter.getFilter().filter(s);
        }
    };

    public List<String> apiCall(final String apiKey, final String prefix) throws IOException, InterruptedException {
        final List<String> citiesList = new ArrayList<String>();
        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://wft-geo-db.p.rapidapi.com/v1/geo/cities?limit=5&namePrefix="+ prefix +"&sort=-population&types=CITY")
                            .get()
                            .addHeader("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", apiKey)
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        JSONArray arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {
                            citiesList.add(arr.getJSONObject(i).get("name").toString() + ", " + arr.getJSONObject(i).get("country").toString());
                        }

                    } catch (Throwable t) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + jsonData + "\"");
                        Log.e(TAG, "Error: \"" + t + "\"");
                    }
                }
                catch (Exception e) {
                    Log.e(TAG,"An error has occured: " + e);
                }
            }
        });

        newThread.start();
        newThread.join();
        return citiesList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCancel:
                finish();
                break;
            case R.id.buttonOK:

                List<Object> results = safeAdd(this, editTextTextPersonName.getText().toString(), editTextTextPostalAddress.getText().toString(), mDisplayDate.getText().toString(), mDisplayDate2.getText().toString());
                if (results!=null){
                    addData(results.get(0).toString(), results.get(1).toString(), results.get(2), results.get(3));
                    finish();
                };
                break;
        }
    }

    public void addData(String name, String loc, Object dateA, Object dataD){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("location", loc);
        user.put("name", name);
        user.put("dateA", dateA);
        user.put("dateD", dataD);
        user.put("userId", firebaseAuth.getUid());

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public List<Object> safeAdd(Context context, String name, String loc, String arrive, String depart){
        Date dateA = null;
        Date dateD = null;
        try {
            if (loc.equals("")){
                Toast.makeText(context, "City must be selected before saving!", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!arrive.equals("")){
                dateA = formatter.parse(arrive);
            }
            else{
                Toast.makeText(context, "Arrival date has to be set, before saving!", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!depart.equals("")){
                dateD = formatter.parse(depart);
            }
            else{
                Toast.makeText(context, "Departure date has to be set, before saving!", Toast.LENGTH_SHORT).show();
                return null;
            }
            assert dateD != null;
            assert dateA != null;
            if (dateD.compareTo(dateA)<0){
                Toast.makeText(context, "Date of arrival must not be after date of departure!", Toast.LENGTH_SHORT).show();
                return null;
            }

            List<Object> results = new ArrayList<Object>();

            /*
            List<String> result  = new ArrayList<>();
            List<Date> result2  = new ArrayList<>();
            result.add(name);
            result.add(loc);
            result2.add(dateA);
            result2.add(dateD);
            */
            results.add(name);
            results.add(loc);
            results.add(dateA);
            results.add(dateD);

            //return new Pair<>(result, result2);
            return results;

        } catch (ParseException e) {
            Log.e(TAG, "Catch error: " + e);
            e.printStackTrace();
            return null;
        }

    }

}