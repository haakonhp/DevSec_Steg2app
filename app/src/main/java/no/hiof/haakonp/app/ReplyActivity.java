package no.hiof.haakonp.app;

import static no.hiof.haakonp.app.formFunctions.appendFormToConnection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class ReplyActivity extends AppCompatActivity {
    TextView commentSent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        TextView replyText = findViewById(R.id.reply_text);
        TextView userName = findViewById(R.id.username);
        Button replyButton = findViewById(R.id.btnSubmit);
        commentSent = findViewById(R.id.comment_sent);
        TextView commentField = findViewById(R.id.comment_field);
        Button logoutButton = findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(buttonFunctions.appendLogoutFunc(ReplyActivity.this));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String answerTo = extras.getString("comment_text") != null ? extras.getString("comment_text") : "Topp kommentar";
            replyText.setText("Svarer på: " + "\n" + answerTo);
            userName.setText(extras.getString("user_name"));
        }
      replyButton.setOnClickListener(view -> {
            sendCommentTask sendComment = new sendCommentTask(
                    commentField.getText().toString().trim(),
                    extras.getInt("room_id"),
                    extras.getString("comment_id")
            );
            sendComment.execute();
      });
    }
    private class sendCommentTask extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params = new HashMap<>();

        private sendCommentTask(String reply_text, int room_id, String reply_id) {
            params.put("text", reply_text);
            params.put("room_id", String.valueOf(room_id));
            if(reply_id != null) {
                params.put("reply_id", reply_id);
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpURLConnection httpURLConnection = appendFormToConnection((urlVariable.steg2 + "sendComment.php"), getBaseContext(), params);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "Error: " + e.getMessage();
            }
            return "Succeeded";
        }


        @Override
        protected void onPostExecute(String response) {
            commentSent.setText("Comment sent!");
            finish();
        }
    }
}
