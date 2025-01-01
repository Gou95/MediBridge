package com.indosoft.mediBridge.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.databinding.ActivityAddressBinding;

public class AddressActivity extends AppCompatActivity {

    ActivityAddressBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_address);

        String state = AppSession.getInstance(this).getString(Constants.STATE_NAME);
        String city = AppSession.getInstance(this).getString(Constants.CITY_NAME);
        String name = AppSession.getInstance(this).getString(Constants.RELAILER_NAME);

        Log.d("aaa", "onCreate: " + state +""+ city);
      //  Toast.makeText(this, state + city, Toast.LENGTH_SHORT).show();

        binding.txtState.setText(name);
        binding.txtCity.setText(city);

    }
}