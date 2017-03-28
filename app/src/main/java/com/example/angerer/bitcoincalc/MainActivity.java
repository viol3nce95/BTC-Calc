package com.example.angerer.bitcoincalc;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Currency selected in Spinner
    public static String selected;
    // Checking if the value from EditText was set by program, not by user
    public static boolean manually_set = false;
    // If connection WAS lost, boolean will be true
    public static boolean LOST_CONNECTION = false;

    // ProgressDialog if no connection was found
    private ProgressDialog process_inet;

    // URL for JSON with Bitcoin data
    private static String url = "http://api.coindesk.com/v1/bpi/currentprice.json";

    // Prices for different currencies
    double[] pricearray = new double[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise dropdown
        createSpinner();

        // current currency
        selected = "EUR (Euro)";
        // values of the currencies 0 = EURO, 1 = USD, 2 = GBP
        pricearray[0] = 0.0;
        pricearray[1] = 0.0;
        pricearray[2] = 0.0;

        // initialise Progressdialog for lost internet connection
        process_inet = new ProgressDialog(MainActivity.this);
        process_inet.setCancelable(false);
        process_inet.setMessage("Waiting for Network connection..");

        // continuously check internet connection
        runnable.run();

        // Check if user entered something to EditText (Bitcoin count)
        EditText txt = (EditText) findViewById(R.id.editText);
        txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!manually_set) {
                    calculate();
                }
                else{
                    manually_set = false;
                }
            }
        });
    }

    // External Thread for checking Internet connectivity
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {

            // Internet available and not lost by last check, update current Bitcoin price
            if(isNetworkAvailable() && !LOST_CONNECTION){
                if(process_inet.isShowing())
                    process_inet.hide();

                // Get new Price from API
                new GetCourses().execute();
            }

            // Internet was not lost before, but lost now -> Show Progressdialog and set connection as lost
            if(!LOST_CONNECTION && !isNetworkAvailable()){
                process_inet.show();
                LOST_CONNECTION = true;
            }

            // Internet available again -> Switch connection lost off
            if(isNetworkAvailable())
                LOST_CONNECTION = false;

            // Rerun every 1000ms
            handler.postDelayed(this, 1000);
        }
    };

    // Check if Network connectivity is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Calculate Bitcoin price with entered value
    private void calculate(){
        EditText txt = (EditText) findViewById(R.id.editText);
        TextView course = (TextView) findViewById(R.id.kursLabel);
        TextView gesamt = (TextView) findViewById(R.id.wert_btc);
        double wert = 0.0;

        if(selected.equals("EUR (Euro)")){

            // Show current Course of BTC in EUR
            String current_course = String.format("%.2f", pricearray[0]);
            course.setText("BTC-Kurs: "+current_course+"€");

            // If a value was entered, calculate price of amount entered
            if(txt.getText().length() > 0){
                wert = Double.parseDouble(txt.getText().toString());

                // Bitcoins maximum decimals is 8
                wert = RoundTo8Decimals(wert);

                // Round digits for different values (Maximum BTC = 21000000)
                if(wert >= 10000000.0 && wert < 21000000.0 && txt.getText().length() >= 17){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 1000000.0 && wert < 10000000.0 && txt.getText().length() >= 16){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 100000.0 && wert < 1000000.0 && txt.getText().length() >= 15){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 10000.0 && wert < 100000.0 && txt.getText().length() >= 14){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 1000.0 && wert < 10000.0 && txt.getText().length() >= 13){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 100.0 && wert < 1000.0 && txt.getText().length() >= 12){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 10.0 && wert < 100.0 && txt.getText().length() >= 11){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 1.0 && wert < 10.0 && txt.getText().length() >= 10){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert > 0.0 && txt.getText().length() >= 10 && wert < 1.0){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert <= 0.0 && txt.getText().length() >= 10){
                    manually_set = true;
                    txt.setText("0.00000000");
                    txt.setSelection(txt.getText().length());
                }

                if(wert > 20999999.0 && txt.getText().length() >= 8){
                    manually_set = true;
                    txt.setText("21000000");
                    wert = 21000000.00000000;
                    txt.setSelection(txt.getText().length());
                    Toast.makeText(getApplicationContext(), "There are only 21000000 Bitcoins, how the f*** did you get all?", Toast.LENGTH_LONG).show();
                }

                // Calculate
                String lastprice = String.format("%.2f",(wert*pricearray[0]));
                gesamt.setText(""+lastprice+"€");
            }
            else{
                gesamt.setText("0€");
            }
        }
        else if(selected.equals("USD (Dollar)"))
        {
            String current_course = String.format("%.2f", pricearray[1]);
            course.setText("BTC-Kurs: "+current_course+"$");
            if(txt.getText().length() > 0){
                wert = Double.parseDouble(txt.getText().toString());

                wert = RoundTo8Decimals(wert);

                if(wert >= 10000000.0 && wert < 21000000.0 && txt.getText().length() >= 17){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 1000000.0 && wert < 10000000.0 && txt.getText().length() >= 16){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 100000.0 && wert < 1000000.0 && txt.getText().length() >= 15){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 10000.0 && wert < 100000.0 && txt.getText().length() >= 14){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 1000.0 && wert < 10000.0 && txt.getText().length() >= 13){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 100.0 && wert < 1000.0 && txt.getText().length() >= 12){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 10.0 && wert < 100.0 && txt.getText().length() >= 11){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert >= 1.0 && wert < 10.0 && txt.getText().length() >= 10){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert > 0.0 && txt.getText().length() >= 10 && wert < 1.0){
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if(wert <= 0.0 && txt.getText().length() >= 10){
                    manually_set = true;
                    txt.setText("0.00000000");
                    txt.setSelection(txt.getText().length());
                }

                if(wert > 20999999.0 && txt.getText().length() >= 8){
                    manually_set = true;
                    txt.setText("21000000");
                    wert = 21000000.00000000;
                    txt.setSelection(txt.getText().length());
                    Toast.makeText(getApplicationContext(), "There are only 21000000 Bitcoins, how the f*** did you get all?", Toast.LENGTH_LONG).show();
                }

                String lastprice = String.format("%.2f",(wert*pricearray[1]));
                gesamt.setText(""+lastprice+"$");
            }
            else{
                gesamt.setText("0$");
            }
        }
        else if(selected.equals("GBP (Pfund)")) {
            String current_course = String.format("%.2f", pricearray[2]);
            course.setText("BTC-Kurs: " + current_course + "£");
            if (txt.getText().length() > 0) {
                wert = Double.parseDouble(txt.getText().toString());

                wert = RoundTo8Decimals(wert);

                if (wert >= 10000000.0 && wert < 21000000.0 && txt.getText().length() >= 17) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 1000000.0 && wert < 10000000.0 && txt.getText().length() >= 16) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 100000.0 && wert < 1000000.0 && txt.getText().length() >= 15) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 10000.0 && wert < 100000.0 && txt.getText().length() >= 14) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 1000.0 && wert < 10000.0 && txt.getText().length() >= 13) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 100.0 && wert < 1000.0 && txt.getText().length() >= 12) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 10.0 && wert < 100.0 && txt.getText().length() >= 11) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert >= 1.0 && wert < 10.0 && txt.getText().length() >= 10) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert > 0.0 && txt.getText().length() >= 10 && wert < 1.0) {
                    manually_set = true;
                    txt.setText(String.format("%.8f", wert));
                    txt.setSelection(txt.getText().length());
                }

                if (wert <= 0.0 && txt.getText().length() >= 10) {
                    manually_set = true;
                    txt.setText("0.00000000");
                    txt.setSelection(txt.getText().length());
                }

                if (wert > 20999999.0 && txt.getText().length() >= 8) {
                    manually_set = true;
                    txt.setText("21000000");
                    wert = 21000000.00000000;
                    txt.setSelection(txt.getText().length());
                    Toast.makeText(getApplicationContext(), "There are only 21000000 Bitcoins, how the f*** did you get all?", Toast.LENGTH_LONG).show();
                }

                String lastprice = String.format("%.2f", (wert * pricearray[2]));
                gesamt.setText("" + lastprice + "£");
            } else {
                gesamt.setText("0£");
            }
        }

        Log.i("INFO", selected);
    }

    private double RoundTo8Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("#########.########");
        return Double.valueOf(df2.format(val));
    }

    // Spinner for currency selection
    private void createSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    // If other item was selected, save which one
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Spinner spinner = (Spinner) findViewById(R.id.dropdown);
        selected = spinner.getSelectedItem().toString();
        calculate();
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Async Task for http request and getting the courses
    private class GetCourses extends AsyncTask<Void, Void, Void> {

        // Pre execute
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Background task
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequest handler = new HttpRequest();

            // Making a request to url and getting response
            String jsonString = handler.callURL(url);

            if (jsonString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);

                    // Getting JSON Array node
                    JSONObject prices = jsonObj.getJSONObject("bpi");

                    String euro_price = "";
                    String usd_price = "";
                    String gbp_price = "";


                    // Currency node is JSON Object
                    JSONObject euro = prices.getJSONObject("EUR");
                    euro_price = euro.getString("rate").replace(",", "");

                    JSONObject usd = prices.getJSONObject("USD");
                    usd_price = usd.getString("rate").replace(",", "");

                    JSONObject gbp = prices.getJSONObject("GBP");
                    gbp_price = gbp.getString("rate").replace(",", "");

                    pricearray[0] = Double.parseDouble(euro_price);
                    pricearray[1] = Double.parseDouble(usd_price);
                    pricearray[2] = Double.parseDouble(gbp_price);

                }
                catch (final JSONException e) {
                    Log.e("ERROR", e.getMessage());
                }
            } else {
                Log.e("ERROR", "Couldn't find JSON.");
            }

            return null;
        }

        // After data was received, calculate prices
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            calculate();
        }

    }
}
