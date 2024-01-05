package no.hiof.haakonp.app;

import static no.hiof.haakonp.app.formFunctions.appendFormToConnection;

import android.annotation.SuppressLint;
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
import java.util.Arrays;
import java.util.HashMap;

public class EmneActivity extends AppCompatActivity {
    private TextView subjectNumberView;
    private TextView subjectNameView;
    private TextView userNameView;
    private ListView commentList;

    private final ArrayList<String> commentArray = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emne);
        subjectNumberView = findViewById(R.id.reply_id);
        subjectNameView = findViewById(R.id.reply_text);
        userNameView = findViewById(R.id.username);
        commentList = findViewById(R.id.lvComments);
        Button createCommentButton = findViewById(R.id.btnCreateComment);

        Button logoutButton = findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(buttonFunctions.appendLogoutFunc(EmneActivity.this));
        

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fetchCommentTask fetchComment = new fetchCommentTask();
            int subjectId = extras.getInt("subject_id");
            fetchComment.subject_id = subjectId;
            fetchComment.execute();
            String subjectName = extras.getString("subject_name");
            String userName = extras.getString("user_name");

            subjectNumberView.setText(String.valueOf(subjectId));
            subjectNameView.setText(subjectName);
            userNameView.setText(userName);
            createCommentButton.setOnClickListener(buttonFunctions.appendCreateCommentFunc(EmneActivity.this, subjectId, userName));

        }

    }
    private class fetchCommentTask extends AsyncTask<Void, Void, String> {
        protected int subject_id;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                HashMap<String,String> commentVars = new HashMap<>();
                commentVars.put("room", String.valueOf(subject_id));
                HttpURLConnection httpURLConnection = appendFormToConnection((urlVariable.steg2 + "getRoom.php"), getBaseContext(), commentVars);
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
                    int depth = jsonObject.getInt("depth") * 3;
                    char[] whiteSpace = new char[depth];
                    Arrays.fill(whiteSpace, ' ');
                    StringBuilder comment = new StringBuilder();

                    comment.append(whiteSpace);
                    comment.append(jsonObject.getString("name"));
                    comment.append(": ");
                    comment.append(jsonObject.getString("text"));
                    commentArray.add(comment.toString());
                }
                ArrayAdapter<String> commentAdapter = new ArrayAdapter<>(EmneActivity.this, android.R.layout.simple_list_item_1, commentArray);
                commentList.setAdapter(commentAdapter);
                commentList.setOnItemClickListener((adapterView, view, pos, id) -> {
                    Intent emneIntent = new Intent(EmneActivity.this, ReplyActivity.class);
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(pos);
                        emneIntent.putExtra("comment_text", jsonObject.getString("text"));
                        emneIntent.putExtra("comment_id", jsonObject.getString("id"));
                        emneIntent.putExtra("room_id", jsonObject.getInt("subject"));
                        emneIntent.putExtra("user_name", userNameView.getText());

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

