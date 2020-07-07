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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //inicjacja zmiennych
        final EditText login = findViewById(R.id.loginEv);
        final EditText mail = findViewById(R.id.mailEv);
        final EditText password = findViewById(R.id.passwordEv);
        Button registeredBtn = findViewById(R.id.registeredBtn);

        registeredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject registerdata = new JSONObject();
                String user_login = login.getText().toString();
                String user_mail = mail.getText().toString();
                String user_password = password.getText().toString();
                if (user_login != "" && user_mail != "" && user_password != "") {
                    try {
                        // dane wkładam do JSONa
                        registerdata.put("login", user_login);
                        registerdata.put("mail", user_mail);
                        registerdata.put("password", user_password);
                        Log.e("TAG", registerdata.toString());
                        // link na serwer "registerdata" to nazwa klasy na serwerze
                        new RegisterActivity.SendJSONtoServer().execute("http://192.168.0.66:8080/dzienniczek-serwer/registerdata", registerdata.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // tą klase całą kopiować
    private class SendJSONtoServer extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);

        protected void onPreExecute() {
            dialog.setMessage("Rejestracja..");
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
            if (result.contains("succeeded")) {// this is expecting a response code to be sent from your server upon receiving the POST data
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Rejestracja nie powiodła się",Toast.LENGTH_SHORT).show();
            }
        }
    }
}