package com.example.kwadjoamekou.projet_longueville_amekou;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static EditText champText;
    ProgressBar progressBar;

    //redirection vers la seconde activité
    private View.OnClickListener myhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.action_redirect, Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        champText = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar);


        Button b1;
        b1 = findViewById(R.id.button);
        b1.setBackgroundColor(Color.GREEN);
        b1.setOnClickListener(myhandler);
    }

}

