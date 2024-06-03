package com.mcr.aimcr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mcr.aimcr.adapter.MessageAdapter;
import com.mcr.aimcr.api.ClientAPI;
import com.mcr.aimcr.apiinterface.ChatAPI;
import com.mcr.aimcr.apiinterface.VoiceAPI;
import com.mcr.aimcr.apimodels.ChatAPIModel;
import com.mcr.aimcr.apimodels.VoiceAPIModel;
import com.mcr.aimcr.main.App;
import com.mcr.aimcr.models.MessageModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePage extends AppCompatActivity {
    Retrofit VoiceAPIs,ChatAPIs ;
    ClientAPI clientAPI;
    ByteBuffer Bb ;
    App app;
    View parentView;
    AudioFormat audioFormat;
    AudioManager audioManager;
    AudioTrack audioTrack;
    TextView defaultTV,voiceTextField;
    byte[] voiceData ;
    CircleImageView sendText;
    TextInputEditText textInput;
    RecyclerView Rv;
    MessageAdapter MsgAdapter;
    RecyclerView.Adapter Rv_Adapter;
    RecyclerView.LayoutManager Rv_Manager;
    String message,time;
    int sampleRate,channel,audioFrmt,bufferSize;
    public boolean isPlaying;
    public ArrayList<byte[]> voiceDatas;
    public ArrayList<String> voiceTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        isPlaying = false;

        clientAPI = new ClientAPI();
        app = new App(HomePage.this);

        parentView = findViewById(R.id.HomeParentView);
        sendText = findViewById(R.id.HomeBtn);
        textInput = findViewById(R.id.HomeInputText);
        MsgAdapter = new MessageAdapter();
        voiceTextField = findViewById(R.id.HomeVoiceText);

        Rv = findViewById(R.id.HomeRecycleView);
        Rv_Adapter = MsgAdapter;
        Rv_Manager = new LinearLayoutManager(this);
        Rv.setAdapter(Rv_Adapter);
        Rv.setLayoutManager(Rv_Manager);

        //Set-up Audio
        sampleRate = 24000;
        channel = AudioFormat.CHANNEL_OUT_MONO;
        audioFrmt = AudioFormat.ENCODING_PCM_16BIT;
        bufferSize = AudioTrack.getMinBufferSize(sampleRate,channel,audioFrmt);



        message = "";

        addEvent();

    }

    private void addEvent(){
        if(sendText!=null){
            sendText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(validateInput(textInput.getText().toString())){
                        if(isPlaying == false){
                            message = textInput.getText().toString();
                            sendText.setEnabled(false);
                            sendChat(message);
                        }else{
                            app.showToast("Harap tunggu , AI-Chan masih bicara tau !!!");
                        }
                    }else{
                        app.showToast("Input tidak boleh kosong ^-^ !");
                    }
                }
            });
        }
    }

    boolean validateInput(String val){
        for(int i=0;i<val.length();i++){
            if(val.charAt(i) != ' '){
                return true;
            }
        }
        return false;
    }

    private void sendChat(String msg){
        time = new SimpleDateFormat("hh:mm").format(Calendar.getInstance().getTime());
        ChatAPIs = clientAPI.getClientAPI();
        MsgAdapter.UpdateData(new MessageModel(msg,time,"User"));
        Rv.scrollToPosition(MsgAdapter.getItemCount()-1);
        textInput.setText("");
        ChatAPI  chatAPI = ChatAPIs.create(ChatAPI.class);
        Call<ChatAPIModel> resData = chatAPI.getResponse(new ChatAPIModel(msg));

        resData.enqueue(new Callback<ChatAPIModel>() {
            @Override
            public void onResponse(Call<ChatAPIModel> call, Response<ChatAPIModel> response) {
                if(response.isSuccessful()){
                    ChatAPIModel data = response.body();
                    ChatAPIModel.Text textData = data.getData();
                    voiceDatas = new ArrayList<>();
                    voiceTexts = new ArrayList<>();
                    String rawText = textData.getJp();
                    int lastindex = 0;
                    for(int i = 0 ; i < rawText.length() ;i++){
                        if((rawText.charAt(i)=='、' || rawText.charAt(i)=='。' || rawText.charAt(i)=='\n')&& (i-lastindex) > 15){
                            String text = rawText.substring(lastindex,i);
                            voiceTexts.add(text);
                            lastindex = i+1;
                            //app.showToast("Last index : " + lastindex +" \n" + text);
                        }
                    }
                    if(lastindex<rawText.length()){
                        String text = rawText.substring(lastindex,rawText.length()-1);
                        voiceTexts.add(text);
                        //app.showToast("Last index : " + lastindex +" \n" + text);
                    }
                    MsgAdapter.UpdateData(new MessageModel(textData.getAuto(),time,"AI"));
                    Rv.scrollToPosition(MsgAdapter.getItemCount() -1);
                    getResponse(0,voiceTexts.size());
                }else{
                    app.showToast("Tidak sukses");
                }
                sendText.setEnabled(true);
            }

            @Override
            public void onFailure(Call<ChatAPIModel> call, Throwable t) {
                app.showToast("Error : \n" + t.toString());
                sendText.setEnabled(true);
            }
        });
    }

    private void getResponse(int index, int size){
        if(index >= size){
            return;
        }
        VoiceAPIs = clientAPI.getClientAPI();
        VoiceAPI VApi = VoiceAPIs.create(VoiceAPI.class);
        Call<VoiceAPIModel> resData = VApi.getVoiceData(new VoiceAPIModel(voiceTexts.get(index)));

        resData.enqueue(new Callback<VoiceAPIModel>() {
            @Override
            public void onResponse(Call<VoiceAPIModel> call, Response<VoiceAPIModel> response) {
                if(response.isSuccessful()){
                    VoiceAPIModel data = response.body();
                    VoiceAPIModel.resBody datas = data.getData();
                    voiceData = datas.getData();
                    voiceDatas.add(voiceData);
                    isPlaying = true;
                    //app.showToast("Playing :" + voiceTexts.get(index));
                    getResponse(index+1,size);
                    if(index < 1){
                        playVoice(voiceData);
                    }
                }else{

                }
                sendText.setEnabled(true);
            }

            @Override
            public void onFailure(Call<VoiceAPIModel> call, Throwable t) {
                sendText.setEnabled(true);
                app.showSnackBar("Error : Tidak dapat terhubung ke server !\n" + t.toString(),parentView);
            }
        });
    }
    public int voicePos;
    private void playVoice(byte[] voiceData){
        voicePos = 0;
        //Gunakan Executor Service sebagai fungsi async supaya aplikasi tidak dipause atau dijeda
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(voicePos != voiceDatas.size()){
                    isPlaying = true;
                    voicePos  += 1;
                    audioTrack = new AudioTrack(
                            AudioManager.STREAM_MUSIC,
                            sampleRate,
                            channel,
                            audioFrmt,
                            bufferSize,
                            AudioTrack.MODE_STREAM
                    );audioTrack.play();
                    audioTrack.write(voiceDatas.get(voicePos-1), 0, voiceDatas.get(voicePos-1).length);
                    audioTrack.flush();
                    audioTrack.stop();
                    isPlaying = false;
                }
            }
        });

//        ExecutorService executors = Executors.newSingleThreadExecutor();
//        executors.submit(()->{
//            while (!Thread.currentThread().isInterrupted() || Thread.currentThread().isInterrupted()) {
//                if(isPlaying){
//                    voiceTextField.setText(voiceTexts.get(voicePos-1));
//                }else{
//                    voiceTextField.setText("");
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}