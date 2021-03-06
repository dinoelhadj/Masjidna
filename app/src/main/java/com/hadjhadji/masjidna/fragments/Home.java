package com.hadjhadji.masjidna.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hadjhadji.masjidna.R;

import java.util.Locale;

public class Home extends Fragment {
    TextView totalAmount_tv, lastMonthAmount_tv, thisMonthAmount_tv, lastJumAmount_tv;
    ImageView languageIV;
    String language;
    String euro = "€";


    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance() {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //SharedPref
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.masjidna_shared_pref),Context.MODE_PRIVATE);

        //Read from SharedPref
        language = sharedPref.getString("salatLang","en");
        updateResources(getContext(),language);

        totalAmount_tv = view.findViewById(R.id.total_amount_tv);
        lastMonthAmount_tv = view.findViewById(R.id.lastmonth_amount_tv);
        thisMonthAmount_tv = view.findViewById(R.id.thismonth_amount_tv);
        lastJumAmount_tv = view.findViewById(R.id.lastJ_amount_tv);
        languageIV = view.findViewById(R.id.languageIV);

        if (language.equals("ar")){
            languageIV.setImageResource(R.drawable.spain);
        } else {
            languageIV.setImageResource(R.drawable.arab);
        }
        languageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language = sharedPref.getString("salatLang","en");

                if (language.equals("sp")){
                    languageIV.setImageResource(R.drawable.arab);
                    updateResources(getContext(),"ar");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("salatLang", "ar");
                    editor.apply();

                } else {
                    languageIV.setImageResource(R.drawable.spain);
                    updateResources(getContext(),"sp");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("salatLang", "sp");
                    editor.apply();
                }
                refreshActivity();

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://masjidna-8e74b-default-rtdb.europe-west1.firebasedatabase.app");


        DatabaseReference sadaka_ref = database.getReference("Jummuas").child("general");

        sadaka_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalAmount_tv.setText(euro + snapshot.child("total").getValue());
                lastMonthAmount_tv.setText(euro + snapshot.child("last_month").getValue());
                thisMonthAmount_tv.setText(euro + snapshot.child("this_month").getValue());
                lastJumAmount_tv.setText(euro + snapshot.child("last_jum").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    private void refreshActivity(){
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }
}