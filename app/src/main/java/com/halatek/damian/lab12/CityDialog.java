package com.halatek.damian.lab12;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.halatek.damian.lab12.Model.City;

import java.util.ArrayList;
import java.util.List;

public class CityDialog extends Dialog implements AdapterView.OnItemSelectedListener{

    private AppCompatActivity mContext;

    private List<City> mCities = new ArrayList<>();
    private City mSelectedCity;

    private AppCompatSpinner mCitySpinner;
    private Button mAccept;

    public CityDialog(@NonNull AppCompatActivity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_settings);

        mCities.add(new City("Current location", -1));
        mCities.add(new City("Hurzuf", 707860));
        mCities.add(new City("Verkhneye Shchekotikhino", 475279));
        mCities.add(new City("Sankt Nikolai im Sausal", 7872417));
        mCities.add(new City("Santa Cruz de la Salceda", 3109930));
        mCities.add(new City("Geisenbrunn", 2921716));
        mCities.add(new City("Arrondissement d'Oloron-Sainte-Marie", 2989568));

        mCitySpinner = findViewById(R.id.cities);
        ArrayAdapter<City> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mCities.toArray(new City[mCities.size()]));
        mCitySpinner.setAdapter(adapter);
        mCitySpinner.setOnItemSelectedListener(this);

        mAccept = findViewById(R.id.select_city_btn);
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).setSelectedCity(mSelectedCity);
                dismiss();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCity = mCities.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
