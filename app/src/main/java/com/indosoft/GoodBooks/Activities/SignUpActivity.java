package com.indosoft.GoodBooks.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.indosoft.GoodBooks.databinding.ActivitySignUpBinding;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    String text = "I accept and agree to the Terms & Conditions and privacy policy";
    SpannableString spannableString = new SpannableString(text);
    List<String> state = Arrays.asList("select state","Madhya Pradesh", "Delhi", "J&K", "Uttar Pradesh", "Bihar");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtLogin.setPaintFlags(binding.txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        initclicks();



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, state);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerState.setAdapter(arrayAdapter);

        int termsStart = text.indexOf("Terms & Conditions");
        int termsEnd = termsStart + "Terms & Conditions".length();

        int privacyStart = text.indexOf("privacy policy");
        int privacyEnd = privacyStart + "privacy policy".length();


        spannableString.setSpan(new StyleSpan(Typeface.BOLD), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Apply bold style to "Privacy Policy"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set the SpannableString to the CheckBox
        binding.checkbox.setText(spannableString);

    }
    private void initclicks() {

        binding.txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
        binding.btnSignin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
            startActivity(intent);
        });
        binding.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You can show the popup here if needed (optional)
                showTermsConditionsPopup();
            }
        });
        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                }else {

                }
            }
        });
    }

    private void showTermsConditionsPopup() {
        // Create the dialog for Terms & Conditions
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Terms & Conditions")
                .setMessage("Here are the Terms & Conditions of the app...")
                .setCancelable(false) // Prevents closing without agreeing
                .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Optionally check the checkbox when user agrees
                        binding.checkbox.setChecked(true);
                    }
                })
                .setNegativeButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }
}