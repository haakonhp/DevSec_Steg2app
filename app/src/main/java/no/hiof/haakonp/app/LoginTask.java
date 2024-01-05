package no.hiof.haakonp.app;

import static no.hiof.haakonp.app.formFunctions.createForm;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class LoginTask extends AsyncTask<String, Void, String> {
    public static LoginTask createLoginTask(LoginTaskResponse delegate, Context mainContext) {
        return new LoginTask(delegate, mainContext);
    }

    public interface LoginTaskResponse {
        void processLoginFinish(String token);
    }
    private String token;
    private final LoginTaskResponse delegate;
    private final Context mainContext;
    private LoginTask(LoginTaskResponse delegate, Context mainContext) {
        this.delegate = delegate;
        this.mainContext = mainContext;
    }

    @Override
    protected String doInBackground(String... params) {
        String email = params[0];
        String password = params[1];

        try {
            HttpURLConnection connection = certificateHTTPSModule.setupHTTPSConnection((urlVariable.steg2 + "loginGetToken.php"), mainContext, urlVariable.trustedDomain);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            HashMap<String, String> loginInfo = new HashMap<>();
            loginInfo.put("email", email);
            loginInfo.put("password", password);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(createForm(loginInfo));
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            }
            return null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String token) {
//        super.onPostExecute(s);
        delegate.processLoginFinish(token);
    }


}
