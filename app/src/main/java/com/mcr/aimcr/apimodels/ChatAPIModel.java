package com.mcr.aimcr.apimodels;

import com.mcr.aimcr.apiinterface.ChatAPI;

public class ChatAPIModel {
    Text data;
    String text;
    int status;

    public ChatAPIModel(String txt){
        text = txt;
    }

    public String getText() {
        return text;
    }

    public Text getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public class Text {
        String auto,jp;

        public String getAuto() {
            return auto;
        }

        public String getJp() {
            return jp;
        }
    }
}
