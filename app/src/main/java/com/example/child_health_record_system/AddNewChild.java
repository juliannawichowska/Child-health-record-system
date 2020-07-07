package com.example.child_health_record_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class AddNewChild extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText childName = findViewById(R.id.child_name);
        final EditText childSurname = findViewById(R.id.child_surname);
        final EditText childPesel = findViewById(R.id.child_pesel);
        final EditText childWeight = findViewById(R.id.child_weight);
        final EditText childHeight = findViewById(R.id.child_height);
        final EditText childDate = findViewById(R.id.child_date);

        Button childCommit = findViewById(R.id.child_commit);


        childCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject child_data = new JSONObject();
                String child_name = childName.getText().toString();
                String child_surname = childSurname.getText().toString();
                String PESEL = childPesel.getText().toString();
                String weight = childWeight.getText().toString();
                String height = childHeight.getText().toString();
                String birth_date = childDate.getText().toString();
                String login = LoginActivity.user_login;
                String password = LoginActivity.user_pass;
                if (login != "" && password != "") {
                    try {
                        // dane wkładam do JSONa
                        child_data.put("child_name", child_name);
                        child_data.put("child_surname", child_surname);
                        child_data.put("PESEL", PESEL);
                        child_data.put("weight", weight);
                        child_data.put("height", height);
                        child_data.put("birth_date", birth_date);
                        child_data.put("login", login);
                        child_data.put("password", password);
                        Log.e("TAG", child_data.toString());
                        // link na serwer "servletdata" to nazwa klasy na serwerze
                        new AddNewChild.RegisterOperation().execute("http://192.168.0.66:8080/telematyka-serwer/addchild", child_data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
    private class RegisterOperation extends AsyncTask<String, Void, String> {

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

        }
    }
}
