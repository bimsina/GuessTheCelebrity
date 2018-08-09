package com.example.bimsina.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView celebrityImage;
    String result;
    Pattern p;
    Matcher matcher;
    ArrayList<String> celebNames = new ArrayList<String>();
    ArrayList<String> celebUrl = new ArrayList<String>();
    int choosenCeleb;
    int locationOfCorrectCeleb;
    int locationOfinCorrectCeleb;
    String[] answers;
    DownloadImage imageTask;
    Bitmap imageCeleb;
    Button button0,button1,button2,button3;
    Random random;
    TextView answer;



    public class DownloadImage extends AsyncTask<String ,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadUrl extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            result = null;
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader urlReader = new InputStreamReader(inputStream);
                int data = urlReader.read();
                while(data != -1){
                    char current = (char)data;
                    result += current;
                    data = urlReader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }
    public void click(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectCeleb))){
            answer.setText("Correct");
        }
        else{
            answer.setText("Wrong it was " + celebNames.get(choosenCeleb));
        }
        nextQuestion();

    }

    public void nextQuestion()
    {
        random = new Random();
        choosenCeleb = random.nextInt(celebNames.size());
        locationOfCorrectCeleb = random.nextInt(4);
        imageTask = new DownloadImage();

        try {

            imageCeleb = imageTask.execute(celebUrl.get(choosenCeleb)).get();
            celebrityImage.setImageBitmap(imageCeleb);

            for(int i=0;i<4;i++)
            {
                locationOfinCorrectCeleb = random.nextInt(celebNames.size());
                if(i == locationOfCorrectCeleb){
                    answers[i] = celebNames.get(choosenCeleb);
                }
                else {
                    if (locationOfinCorrectCeleb == locationOfCorrectCeleb) {
                        locationOfinCorrectCeleb = random.nextInt(celebNames.size());
                        answers[i] = celebNames.get(locationOfinCorrectCeleb);
                    }
                    answers[i] = celebNames.get(locationOfinCorrectCeleb);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        answer = findViewById(R.id.answer);
        celebrityImage = findViewById(R.id.celebrityImage);

        choosenCeleb = 0;
        answers = new String[4];


        DownloadUrl task = new DownloadUrl();
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] split = result.split("<div class=\"sidebarContainer\">");

//            result = task.execute("https://www.babepedia.com/pornstartop100").get();
//            String[] split = result.split("<div class=\"thumbtext\">#100:");

            p = Pattern.compile("<img src=\"(.*?)\"");
            matcher = p.matcher(split[0]);
            while(matcher.find()){
                celebUrl.add(matcher.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            matcher = p.matcher(split[0]);
            while(matcher.find()){
                celebNames.add(matcher.group(1));
            }

//            p = Pattern.compile("src=\"(.*?)\"");
//            matcher = p.matcher(split[0]);
//            while(matcher.find()){
//                Log.i("images",matcher.group(1));
//                celebUrl.add("https://www.babepedia.com" + matcher.group(1));
//
//            }
//
//            p = Pattern.compile("alt=\"(.*?)\"");
//            matcher = p.matcher(split[0]);
//            while(matcher.find()){
//                celebNames.add(matcher.group(1));
//            }


            nextQuestion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
