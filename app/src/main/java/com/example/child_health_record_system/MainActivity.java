package com.example.child_health_record_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String my_url = "192.168.0.66:8080";
    Button add_child_btn, child_list_btn;
    TextView welcome_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcome_text = findViewById(R.id.welcome_text);
        welcome_text.setText("Aplikacja rodzica");

        add_child_btn = findViewById(R.id.button2);
        add_child_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tutaj powinna otwierac sie klasa AddNewChild
                Intent intent = new Intent(MainActivity.this, AddNewChild.class);
                startActivity(intent);
            }
        });
        child_list_btn = findViewById(R.id.child_list_btn);
        child_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject child_data = new JSONObject();
                try {
                    child_data.put("login", LoginActivity.user_login);
                    child_data.put("password", LoginActivity.user_pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ChildList().execute("http://192.168.0.66:8080/dzienniczek-serwer/childlist", child_data.toString());
            }
        });
    }

    private class ChildList extends AsyncTask<String, Void, String> {

        private String jsonResponse;

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
                receivedata(httpURLConnection);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        private void receivedata(HttpURLConnection connection) throws Exception {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String returnString = "";
            StringBuilder allData = new StringBuilder("");

            while ((returnString = in.readLine()) != null) {
                allData.append(returnString);
            }
            in.close();

            Log.e("TAG", allData.toString());
        }

        //odlsyanie danych z serwera
        protected void onPostExecute(String result) {
            Log.e("TAG", result);
            result = result.substring(result.indexOf("/")+19);
            result = result.replace("=", ":");
            JSONArray jArray = null;
            try {
                jArray = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            int numberOfRows = jArray.length()-1;
            int numberOfButtonsPerRow = 1;
            int buttonIdNumber = 0;


            final LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.buttonlayout);

            for (int i = 0; i < numberOfRows; i++) {
                LinearLayout newLine = new LinearLayout(MainActivity.this);
                newLine.setLayoutParams(params);
                newLine.setOrientation(LinearLayout.VERTICAL);
                String imie = null;
                String nazwisko = null;
                try {
                    JSONObject jObject = null;
                    ArrayList<String> rap = new ArrayList<String>();
                    jObject = jArray.getJSONObject(i);
                    imie = (String) jObject.get("imie");
                    rap.add("Imię " + imie);
                    nazwisko = (String) jObject.get("nazwisko");
                    rap.add("Nazwisko " + nazwisko);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < numberOfButtonsPerRow; j++) {
                    Button button = new Button(MainActivity.this);
                    // You can set button parameters here:
                    button.setId(buttonIdNumber);
                    button.setLayoutParams(params);
                    button.setText(imie + " " + nazwisko);
                    final int finalButtonIdNumber = buttonIdNumber;
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

//                            Intent is = new Intent(getApplicationContext(), MainActivity.class);
//                            is.putExtra("buttonVariable", finalButtonIdNumber);
//                            startActivity(is);
                        }
                    });

                    newLine.addView(button);
                    buttonIdNumber++;
                }
                verticalLayout.addView(newLine);
            }
        }
    }
}