package com.wireless.ambeent.mozillaprototype.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.wireless.ambeent.mozillaprototype.R;
import com.wireless.ambeent.mozillaprototype.helpers.Constants;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //A library object that helps validating phone numbers and country code selection
    private CountryCodePicker mCcp;

    //Phone number text input
    private EditText editTextCarrierNumber;

    //Move to next activity after entering valid phone number
    private Button continueButton;

    //Save phone number
    private SharedPreferences sharedPreferences;

    private boolean isTheNumberValid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Init SharedPreferences
        sharedPreferences  = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        //This line is for testing purposes
    //    sharedPreferences.edit().putString(Constants.USER_PHONE_NUMBER, "").apply();

        //If the phone is already registered, skip this activity.
        if(isPhoneNumberAlreadySaved()) startMainActivity();


        //Init views
        continueButton = (Button) findViewById(R.id.continue_Button);
        mCcp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);
        mCcp.registerCarrierNumberEditText(editTextCarrierNumber); //Register the EditText to CountryCodePicker library


        //Do not let the user press continue unless the phone number is valid with the selected country code
        mCcp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {

                isTheNumberValid = isValidNumber;

            }
        });

        //Save the number to SharedPreferences and move to MainActivity
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Warn the user if the number is not valid
                if(!isTheNumberValid){
                    Toast.makeText(LoginActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Valid number, save it with plus sign
                sharedPreferences.edit().putString(Constants.USER_PHONE_NUMBER, mCcp.getFullNumberWithPlus()).apply();

                Log.i(TAG, "Entered Number: " + mCcp.getFullNumberWithPlus());

                startMainActivity();


            }
        });

    }

    //Format the number with plus sign and no spaces
    private String formatNumber(String number){

        String formattedNumber = "+" +number.replaceAll("[^0-9]","");
        Log.i(TAG, "formatNumber: " +formattedNumber);

        return formattedNumber;


    }

    //Checks shared preferences to see whether the phone number is already saved
    private boolean isPhoneNumberAlreadySaved(){

        String savedPhoneNumber = sharedPreferences.getString(Constants.USER_PHONE_NUMBER, "");

        if(savedPhoneNumber.equalsIgnoreCase("")) return false;

        return true;
    }

    //Start MainActivity
    private void startMainActivity(){
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }
}
