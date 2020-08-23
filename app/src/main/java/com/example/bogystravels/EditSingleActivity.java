package com.example.bogystravels;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EditSingleActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EditSingleActivity";
    private static String docKey;
    private static String APIKEY;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    TextView editTextTextPersonName;
    TextView editTextTextPostalAddress;
    TextView editTextTextCountryName;

    private TextView mDisplayDate;
    private TextView mDisplayDate2;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;

    private List<String> suggestions = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_single);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            docKey = extras.getString("key");
            APIKEY = extras.getString("apiKey");
        }

        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        editTextTextPostalAddress = findViewById(R.id.editTextTextPostalAddress);
        editTextTextCountryName = findViewById(R.id.editTextTextCountryName2);
        mDisplayDate = findViewById(R.id.editTextDate);
        mDisplayDate2 = findViewById(R.id.editTextDate3);

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
                        EditSingleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);

                dialog.getDatePicker().setMinDate(-62167348957886L);
                dialog.getDatePicker().setMaxDate(253402207200000L);
                dialog.getDatePicker().updateDate(year,month,day);

                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(Objects.requireNonNull(formatter.parse(mDisplayDate2.getText().toString())));
                    dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
                        EditSingleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);

                dialog.getDatePicker().setMinDate(-62167348957886L);
                dialog.getDatePicker().setMaxDate(253402207200000L);
                dialog.getDatePicker().updateDate(year,month,day);

                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(Objects.requireNonNull(formatter.parse(mDisplayDate.getText().toString())));
                    dialog.getDatePicker().setMinDate(c.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
        findViewById(R.id.buttonDelete).setOnClickListener(this);
        findViewById(R.id.buttonCancel).setOnClickListener(this);

        GetDocument(docKey);

        final AutoCompleteTextView editText = findViewById(R.id.editTextTextPostalAddress);
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
            public void afterTextChanged(final Editable s) {
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 1000);
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                AddSingleActivity addSingleActivity = new AddSingleActivity();
                List parts = addSingleActivity.splitRight(adapter.getItem(pos),", ", 2);
                editText.setText(parts.get(0).toString());
                if (parts.get(1)!=null){
                    editTextTextCountryName.setText(parts.get(1).toString());
                }
            }
        });
    }

    Runnable mFilterTask = new Runnable() {
        @Override
        public void run() {
            CharSequence s = editTextTextPostalAddress.getText();
            AddSingleActivity addSingleActivity = new AddSingleActivity();
            try {
                suggestions = addSingleActivity.apiCall(APIKEY, s.toString());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCancel:
                finish();
                break;
            case R.id.buttonOK:
                AddSingleActivity addSingleActivity = new AddSingleActivity();
                List<Object> results = addSingleActivity.safeAdd(this, editTextTextPersonName.getText().toString(), editTextTextPostalAddress.getText().toString(),editTextTextCountryName.getText().toString(), mDisplayDate.getText().toString(), mDisplayDate2.getText().toString());
                if (results!=null){
                    EditDocument(docKey, results.get(0).toString(), results.get(1).toString(), results.get(2).toString(), results.get(3), results.get(4));
                    finish();
                };
                break;

            case R.id.buttonDelete:
                DeleteDocument(docKey);
                finish();
                break;
        }
    }

    private void GetDocument(String item){
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(item).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (document.contains("name")){
                            editTextTextPersonName.setText(document.get("name").toString());
                        }
                        if (document.contains("location")){
                            editTextTextPostalAddress.setText(document.get("location").toString());
                        }
                        if (document.contains("country")){
                            editTextTextCountryName.setText(document.get("country").toString());
                        }
                        if (document.contains("dateA")){
                            MainActivity mainActivity = new MainActivity();
                            mDisplayDate.setText(mainActivity.timestampToString((Timestamp) document.get("dateA")));
                        }
                        if (document.contains("dateD")){
                            MainActivity mainActivity = new MainActivity();
                            mDisplayDate2.setText(mainActivity.timestampToString((Timestamp) document.get("dateD")));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Get failed with ", task.getException());
                }
            }
        });
    }

    private void DeleteDocument(String item){
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(item)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document!", e);
                    }
                });
    }

    private void EditDocument(String id, String name, String loc, String country, Object arrive, Object depart){
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference document = db.collection("users").document(id);

        updateDocField(document, "name", name);
        updateDocField(document, "location", loc);
        updateDocField(document, "country", country);
        updateDocField(document, "dateA", arrive);
        updateDocField(document, "dateD", depart);
    }

    private void updateDocField(DocumentReference document, String name, Object object){
        document.update(name, object)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

}