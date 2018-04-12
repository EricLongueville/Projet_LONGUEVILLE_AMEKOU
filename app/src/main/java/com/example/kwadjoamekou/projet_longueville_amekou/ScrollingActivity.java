package com.example.kwadjoamekou.projet_longueville_amekou;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScrollingActivity extends AppCompatActivity{

    TextView vueReponse;
    ProgressBar progressBar;
    public static String resultat;
    static String resultatText;
    static String titre;
    public static String dept;
    public static String nomDept;

    //vue
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vueReponse = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        new AsyncRequest().execute();
    }

    //menu action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                boite();
                return true;
            case R.id.action_map:
                maps();
                return true;
            case R.id.action_wiki:
                wiki();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //boite de dialogue
    private void boite(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScrollingActivity.this);
        alertDialog.setTitle("Sauvegarde");
        alertDialog.setMessage("Attention les données seront sauvegardées dans le répertoire /data/user/0/com.example.kwadjoamekou.projet_longueville_amekou/files accessible uniquement sur les téléphones rootés.\n"+"Continuer?");

        alertDialog.setPositiveButton(getString(R.string.action_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                try {
                    saving();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.action_infirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        alertDialog.show();
    }

    //ouverture d'une application maps
    public void maps(){
        Intent i;

        String url = "http://maps.google.com/maps?daddr="+nomDept;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
        startActivity(intent);
        Toast toast2 = Toast.makeText(ScrollingActivity.this, "ouverture de l'application de navigation", Toast.LENGTH_SHORT);
        toast2.show();
    }

    public void wiki(){
        Intent i;

        String url = "https://fr.wikipedia.org/wiki/"+nomDept;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
        startActivity(intent);
        Toast toast2 = Toast.makeText(ScrollingActivity.this, "page wikipedia du département", Toast.LENGTH_SHORT);
        toast2.show();
    }

    //sauvegarde de la liste dans un fichier
    public void saving() throws IOException {
        FileOutputStream output = null;

        try {
            output = openFileOutput(titre, MODE_PRIVATE);
            output.write(resultatText.getBytes());
            //System.out.println(getFilesDir());

            if (output != null)
                output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //requete à l'api et traitement du résultat Json
    class AsyncRequest extends AsyncTask<Void, Void, String> {

        //private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
            dept = MainActivity.champText.getText().toString();
            resultat = "";

            try {
                URL url = new URL("https://geo.api.gouv.fr/departements/" + dept + "/communes?fields=departement&format=json&geometry=centre");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                        resultat = stringBuilder.toString();
                    return resultat;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {

            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            resultatText = "";
            if(resultat.length() < 1){
                resultatText = getString(R.string.erreur_recherche);
                vueReponse.setText(resultatText);
            } else {
                try {
                    JSONObject obj = new JSONObject("{ \"results\" : " + resultat + "}");//resultat
                    JSONArray tableau = obj.optJSONArray("results");
                    for (int i = 0; i < tableau.length(); i++) {
                        JSONObject ligne = tableau.getJSONObject(i);

                        String nom = ligne.optString("nom");

                       /* if(i%2 == 1){

                        }*/
                        resultatText += nom + "\n";
                    }
                    vueReponse.setText(resultatText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            nomDept = "";
            try {
                JSONObject obj2 = new JSONObject("{ \"results\" : " + resultat + "}");//resultat
                JSONArray tableau = obj2.optJSONArray("results");
                for(int i = 0 ; i < 1 ; i++) {
                    JSONObject ligne = tableau.getJSONObject(i);
                    JSONObject ligne2 = ligne.getJSONObject("departement");

                    String departement = ligne2.optString("nom");
                    nomDept += departement;
                }
                titre = getString(R.string.titre_liste)+" " + dept + " (" + nomDept + ")";
                TextView scrollingTitre = findViewById(R.id.textView4);
                scrollingTitre.setText(titre);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
