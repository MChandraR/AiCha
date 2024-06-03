package com.mcr.aimcr.main;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class App {
    private final Context context;

    public App(Context ctx){
        context = ctx;
    }

    public void showSnackBar(String message,View parentView){
        Snackbar.make(parentView,message,Snackbar.LENGTH_SHORT).show();
    }

    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
