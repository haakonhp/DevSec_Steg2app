package no.hiof.haakonp.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

public class buttonFunctions {
    public static View.OnClickListener appendLogoutFunc(Context context) {
            // Fjerner token
        return view -> {
            SharedPreferences preferences = context.getSharedPreferences("UUID", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        };
    }
    public static View.OnClickListener appendCreateCommentFunc(Context context, int room_id, String username) {
        return view -> {
                Intent emneIntent = new Intent(context, ReplyActivity.class);
        emneIntent.putExtra("room_id", room_id);
        emneIntent.putExtra("user_name", username);
        context.startActivity(emneIntent);
        };
    }
}
