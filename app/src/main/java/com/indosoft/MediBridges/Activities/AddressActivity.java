package com.indosoft.MediBridges.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.databinding.ActivityAddressBinding;

public class AddressActivity extends AppCompatActivity {

    ActivityAddressBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_address);

        String state = AppSession.getInstance(this).getValue(Constants.STATE_NAME);
        String city = AppSession.getInstance(this).getValue(Constants.CITY_NAME);
        String name = AppSession.getInstance(this).getValue(Constants.RELAILER_NAME);

        Log.d("aaa", "onCreate: " + state +""+ city);
        Toast.makeText(this, "state"+state, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "city"+city, Toast.LENGTH_SHORT).show();

        binding.txtState.setText(state);
        binding.txtCity.setText(city);

    }
}