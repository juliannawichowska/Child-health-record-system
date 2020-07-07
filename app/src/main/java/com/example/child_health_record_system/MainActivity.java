package com.example.child_health_record_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static String my_url = "192.168.0.66:8080";
    Button add_child_btn;
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
    }
}