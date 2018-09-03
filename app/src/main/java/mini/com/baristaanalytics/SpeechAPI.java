package mini.com.baristaanalytics;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Utilities.MyAdapter;
import Utilities.message_Item;

import static android.content.ContentValues.TAG;

public class SpeechAPI extends AppCompatActivity{
    Context ctx;
    private TextView message;
    private ImageButton btnSpeak;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<message_Item> message_items = new ArrayList<>();

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_api);
        ctx = this;
        message = (TextView) findViewById(R.id.message);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        //message_items = new ArrayList<>();
        // hide the action bar
        //getActionBar().hide();
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });




    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            //initRecyclerView();
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // result.get(0) contains the result from the user
                    message_Item message_item = new message_Item(result.get(0));
                    message_items.add(message_item);
                    Log.d(TAG, "The array size is: " + message_items.size());
                    initRecyclerView();
                    // Say anything related to 'like' or 'coffee' to trigger the maps
                    if(result.get(0).contains("like") && result.get(0).contains("coffee")){
                        Intent x = new Intent(this, MapsActivity.class);
                        String toSpeak = "Proceeding to user registration";
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
                        startActivity(x);
                        finish();
                    }
                    else if((result.get(0).contains("registration") ||
                            result.get(0).contains("register")) &&
                            (result.get(0).contains("user") || result.get(0).contains("customer"))){
                        Intent x = new Intent(this, RegisterCustomerActivity.class);
                        String toSpeak = "Let's get you signed in so you can order coffee";
                        textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                        startActivity(x);
                    }else if((result.get(0).contains("registration") ||
                            result.get(0).contains("register")) &&
                            (result.get(0).contains("admin") || result.get(0).contains("administrator"))){
                        Intent x = new Intent(this, RegisterAdminActivity.class);
                        String toSpeak = "Proceeding to administrator registration";
                        textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                        startActivity(x);
                    }
                    //message.setText(message_items.size());
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(message_items,this);
        recyclerView.setAdapter(adapter);
    }
}
