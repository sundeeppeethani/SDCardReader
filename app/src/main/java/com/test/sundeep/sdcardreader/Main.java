package com.test.sundeep.sdcardreader;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main extends AppCompatActivity {

    private HashMap<String,Integer> extensions;
    ArrayList<FileInfo> allFiles = new ArrayList<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extensions = new HashMap<>();
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Main.this);
                pd.setMessage("Scanning Files...");
                pd.setCancelable(false);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();
                startScan();
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.textView).setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
            }
        });
    }

    void startScan() {
        /*String media_path = Environment.getExternalStorageDirectory().getPath();
        Log.d("test",media_path);
        String[] splitPath= media_path .split("/");*/
        final String root = "/" + "sdcard2/";
        Log.d("test", root);
        File sd_card = new File(root);
        scan(sd_card);
    }

    void scan(final File dir) {
        ArrayList<File> files = null;
        try {
            files = new ScanDir().execute(dir).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (files.size() > 0) {
            for (File file : files) {
                scan(file);
            }
            pd.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    putStats();
                }
            },1500);
        }
    }

    private void putStats() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.textView).setVisibility(View.GONE);
        findViewById(R.id.button2).setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        sb.append("Total No.of Files : "+allFiles.size()+"\n");
        sb.append("Names and Sizes of top 10 files :\n");
        Collections.sort(allFiles,sizeComparator);
        for(int i=0;i<10;i++){
            FileInfo f = allFiles.get(i);
            sb.append(f.getName()+"   "+f.getSize()+"\n");
        }
        long size = 0;
        for(FileInfo f:allFiles){
            size = size+f.getSize();
        }
        sb.append("\nAverage File Size :"+(size/allFiles.size())+"\n");
        Collections.sort(allFiles,countComparator);
        sb.append("Top 5 Frequent Files :\n");
        for(int i=0;i<5;i++){
            FileInfo f = allFiles.get(i);
            sb.append(f.getExtension()+"    "+extensions.get(f.getExtension())+"/"+allFiles.size()+"\n");
        }
        ((TextView)findViewById(R.id.textView2)).setText(sb.toString());
    }

    public static Comparator<FileInfo> countComparator
            = new Comparator<FileInfo>() {

        public int compare(FileInfo f1, FileInfo f2) {

            return (f1.countOfExt>f2.countOfExt)?1:(f1.countOfExt<f2.countOfExt)?-1:0;
        }
    };

    public static Comparator<FileInfo> sizeComparator
            = new Comparator<FileInfo>() {

        public int compare(FileInfo f1, FileInfo f2) {

            return (f1.size>f2.size)?1:(f1.size<f2.size)?-1:0;
        }
    };

    String suffixOf(String name){
        if(name == null || name.equals("")){
            return "";
        }
        String suffix = "";
        int index = name.lastIndexOf(".");
        if (index != -1 ) {
            suffix = name.substring(index + 1);
        }
        return suffix;
    }

    private class ScanDir extends AsyncTask<File,Void,ArrayList<File>>{

        @Override
        protected ArrayList<File> doInBackground(File... params) {
            File[] files = params[0].listFiles();
            ArrayList<File> dirs = new ArrayList<>();
            if(files.length>0) {
                for (File file : files) {
                    if (file.isDirectory())
                        dirs.add(file);
                    else {
                        FileInfo f = new FileInfo();
                        String ext = suffixOf(file.getName());
                        f.setExtension(ext);
                        f.setName(file.getName());
                        f.setSize(Integer.parseInt(String.valueOf(file.length()/1024)));
                        if(!extensions.containsKey(ext)){
                            extensions.put(ext,1);
                            f.setCountOfExt(1);
                        } else {
                            f.setCountOfExt(extensions.get(ext)+1);
                            extensions.remove(ext);
                            extensions.put(ext,f.getCountOfExt());
                        }
                        allFiles.add(f);
                    }
                }
            }
            return dirs;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
