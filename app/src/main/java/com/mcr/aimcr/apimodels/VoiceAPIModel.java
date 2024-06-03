package com.mcr.aimcr.apimodels;

public class VoiceAPIModel {
    private String message;
    resBody data ;

    public VoiceAPIModel(String msg){
        message = msg;
    }

    public resBody getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class resBody{
        private byte[] data;

        public byte[] getData() {
            return data;
        }
    }
}
