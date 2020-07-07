package com.example.child_health_record_system;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView logintext = findViewById(R.id.textView);
        logintext.setText("Wpisz swój login bądź adres e-mail");
        TextView passtext = findViewById(R.id.textView2);
        passtext.setText("Wpisz swoje hasło");

        final EditText loginedit = findViewById(R.id.editText3);
        final EditText passedit = findViewById(R.id.editText4);

        Button loginbutt = findViewById(R.id.button);
        Button registerButton = findViewById(R.id.registerButton);


        loginbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject logindata = new JSONObject();
                String user_login = loginedit.getText().toString();
                String user_pass = passedit.getText().toString();
                if (user_login != "" && user_pass != "") {
                    try {
                        // dane wkładam do JSONa
                        logindata.put("login", user_login);
                        logindata.put("password", user_pass);
                        Log.e("TAG", logindata.toString());
                        // link na serwer "servletdata" to nazwa klasy na serwerze
                        new SendJSONtoServer().execute("http://" + MainActivity.my_url +"/dzienniczek-serwer/servletdata", logindata.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    // tą klase całą kopiować
    private class SendJSONtoServer extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Logowanie..");
            dialog.show();
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        // funkcja która odbiera jakies dane z serwera w string result i wtedy mozna cos z tym robic
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            Log.e("TAG", result);
            if (result.contains("accepted")) {// this is expecting a response code to be sent from your server upon receiving the POST data
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Nieprawidłowa nazwa użytkownika lub hasło",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
