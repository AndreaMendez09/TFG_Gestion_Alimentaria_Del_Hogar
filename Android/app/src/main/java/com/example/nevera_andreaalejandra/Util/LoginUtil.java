package com.example.nevera_andreaalejandra.Util;

import android.content.SharedPreferences;

public class LoginUtil {
    //Devuelve el email guardado
    public static String getUserMailPrefs(SharedPreferences preferences) {
        return preferences.getString("email", "");
    }

    //Devuelve la contraseña guardada
    public static String getUserPassPrefs(SharedPreferences preferences) {
        return preferences.getString("pass", "");
    }

    //Borra los valores guardados
    public static void removeSharedPreferences(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("email");
        editor.remove("pass");
        editor.apply();
    }
}
