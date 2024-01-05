package no.hiof.haakonp.app;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class formFunctions {
    @NonNull
    public static HttpURLConnection appendFormToConnection(String inputURL, Context ctx, HashMap<String,String> params) throws IOException {
        HashMap<String, String> parametersAppendedToken = new HashMap<>();
        if(params != null) {
            parametersAppendedToken.putAll(params);
        }

        HttpURLConnection httpURLConnection = certificateHTTPSModule.setupHTTPSConnection(inputURL, ctx, urlVariable.trustedDomain);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStream outputStream = httpURLConnection.getOutputStream();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("UUID", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        parametersAppendedToken.put("auth_token", token);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(createForm(parametersAppendedToken));
        writer.flush();
        writer.close();
        outputStream.close();
        return httpURLConnection;
    }

    public static String createForm(Map<String, String> params) {
        // Skaper disse manuelt, skulle gjerne benyttet en faktisk pakke til dette
        // Men hver integrete løsning (spesielt okhttp) insisterer på
        // at deres builders skal kun lage requestBodies rettet til deres consumers
        // Og disse passer ikke inn i koden til Håkon. Derfor blir det brukt denne
        // relativt dumme løsningen, fungere gjør den i det minste.
        StringBuilder builder = new StringBuilder();
        params.forEach((k,v) -> appendAttribute(builder,k,v));
        return builder.toString().replace("+", "%2b");
    }

    public static void appendAttribute(StringBuilder builder, String attribute, String value) {
        builder.append(attribute);
        builder.append("=");
        builder.append(value);
        builder.append("&");
    }
}
