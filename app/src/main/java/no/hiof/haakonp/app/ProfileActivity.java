package no.hiof.haakonp.app;

import static no.hiof.haakonp.app.formFunctions.appendFormToConnection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class ProfileActivity extends AppCompatActivity {

    private TextView greetingText;
    private ListView subjectList;
    private final ArrayList<String> subjectArrayList = new ArrayList<>();
    private ListView roleList;
    private String userName;
    private final ArrayList<String> roleArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        greetingText = findViewById(R.id.greetingText);
        new FetchUserInfoTask().execute();

        subjectList = findViewById(R.id.subjectList);
        new FetchSubjectsTask().execute();

        roleList = findViewById(R.id.roleList);
        new FetchRolesTask().execute();

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(buttonFunctions.appendLogoutFunc(ProfileActivity.this));

    }
    private class FetchUserInfoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpURLConnection httpURLConnection = appendFormToConnection((urlVariable.steg2 + "getUserInfo.php"), getBaseContext(), null);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "Error: " + e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String firstName = jsonObject.getString("name");
                userName = firstName;
                greetingText.setText("Hello " + firstName + "!");
            } catch (JSONException e) {
                e.printStackTrace();
        }
        }
    }

    private class FetchRolesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpURLConnection httpURLConnection = appendFormToConnection((urlVariable.steg2 + "getRoles.php"), getBaseContext(), null);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String role = jsonObject.getString("role_name");
                    roleArrayList.add(role);
                }
                Collections.sort(roleArrayList);
                ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1, roleArrayList);
                roleList.setAdapter(roleAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class FetchSubjectsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpURLConnection httpURLConnection = appendFormToConnection((urlVariable.steg2 + "getSubjects.php"), getBaseContext(), null);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String subject = jsonObject.getString("subject_name");
                    subjectArrayList.add(subject + "\n");
                }
                // Collections.sort(subjectArrayList);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1, subjectArrayList);
                subjectList.setAdapter(arrayAdapter);
                subjectList.setOnItemClickListener((adapterView, view, pos, id) -> {
                    Intent emneIntent = new Intent(ProfileActivity.this, EmneActivity.class);
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(pos);
                        emneIntent.putExtra("subject_id", jsonObject.getInt("subject_id"));
                        emneIntent.putExtra("subject_name", jsonObject.getString("subject_name"));
                        emneIntent.putExtra("user_name", userName);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    startActivity(emneIntent);
                });
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
}