package com.example.home_study;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.home_study.Adapter.ContentAdapter;
import com.example.home_study.Model.Content;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {

    private Content content;
    private ImageView back;
    private RecyclerView contentRecycler;
    private ContentAdapter contentAdapter;
    private List<Content> contentList;
    private String bookTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        bookTitle = intent.getStringExtra("bookTitle").toString();
        contentRecycler = findViewById(R.id.contentRecycler);
        back = findViewById(R.id.backImage);
        back.setOnClickListener(v -> finish());
        contentRecycler.setLayoutManager(new LinearLayoutManager(this));

        contentRecycler = findViewById(R.id.contentRecycler);
        contentList = new ArrayList<>();
        loadStaticChapters(bookTitle);
        contentAdapter = new ContentAdapter(contentList, this::onContentSelected, this);

        contentRecycler.setAdapter(contentAdapter);

        Log.d("BookTitle", "Book: "+ bookTitle);

    }

    private void loadStaticChapters(String bookTitle)
    {
        contentList.clear();

        if (bookTitle.equals("English")) {
            contentList.add(new Content("Content", "English", R.drawable.english, null));
            contentList.add(new Content("Learning to learn", "UNIT 1", R.drawable.english, null));
            contentList.add(new Content("Places to visit", "UNIT 2", R.drawable.english, null));
            contentList.add(new Content("Hobbies and crafts", "UNIT 3", R.drawable.english, null));
            contentList.add(new Content("Revision 1 (Units 1–3)", "REVISION", R.drawable.english, null));

            contentList.add(new Content("Food for health", "UNIT 4", R.drawable.english, null));
            contentList.add(new Content("HIV and AIDS", "UNIT 5", R.drawable.english, null));
            contentList.add(new Content("Media, TV and Radio", "UNIT 6", R.drawable.english, null));
            contentList.add(new Content("Revision 2 (Units 4–6)", "REVISION", R.drawable.english, null));

            contentList.add(new Content("Cities of the future", "UNIT 7", R.drawable.english, null));
            contentList.add(new Content("Money and finance", "UNIT 8", R.drawable.english, null));
            contentList.add(new Content("People and traditional culture", "UNIT 9", R.drawable.english, null));
            contentList.add(new Content("Revision 3 (Units 7–9)", "REVISION", R.drawable.english, null));

            contentList.add(new Content("Newspapers and magazines", "UNIT 10", R.drawable.english, null));
            contentList.add(new Content("Endangered animals", "UNIT 11", R.drawable.english, null));
            contentList.add(new Content("Stigma and discrimination", "UNIT 12", R.drawable.english, null));
            contentList.add(new Content("Revision 4 (Units 10–12)", "REVISION", R.drawable.english, null));



        }else if (bookTitle.equalsIgnoreCase("Mathematics")) {
            contentList.add(new Content("Biology and technology", " ", R.drawable.physics, null));
            contentList.add(new Content("Motion and Forces", " ", R.drawable.physics, null));
            contentList.add(new Content("Energy and Work", "  ", R.drawable.physics, null));
            contentList.add(new Content("Work", "History", R.drawable.physics, null));

        }else if (bookTitle.equalsIgnoreCase("Physics")) {
            contentList.add(new Content("Introduction to Physics", "History", R.drawable.physics, null));
            contentList.add(new Content("Motion and Forces", "History", R.drawable.physics, null));
            contentList.add(new Content("Energy and Work", "History", R.drawable.physics, null));
            contentList.add(new Content("Work", "History", R.drawable.physics, null));

        }else if (bookTitle.equalsIgnoreCase("Biology")) {
            contentList.add(new Content("Content", "Biology", R.drawable.biology, null));
            contentList.add(new Content("Biology and technology", "UNIT 1", R.drawable.biology, null));
            contentList.add(new Content("Cell biology", "UNIT 2", R.drawable.biology, null));
            contentList.add(new Content("Human biology and health", "UNIT 3", R.drawable.biology, null));
            contentList.add(new Content("Micro-organisms and disease", "UNIT 4", R.drawable.biology, null));
            contentList.add(new Content("Classification", "UNIT 5", R.drawable.biology, null));
            contentList.add(new Content("Environment", "UNIT 6", R.drawable.biology, null));



        }else if (bookTitle.equalsIgnoreCase("Chemistry")) {
            contentList.add(new Content("Content", "Chemistry", R.drawable.chemistry, null));
            contentList.add(new Content("Structure of the Atom", "UNIT 1", R.drawable.chemistry, null));
            contentList.add(new Content("Periodic Classification of the Elements", "UNIT 2", R.drawable.chemistry, null));
            contentList.add(new Content("Chemical Bonding and Intermolecular Forces", "UNIT 3", R.drawable.chemistry, null));
            contentList.add(new Content("Chemical Reaction and Stoichiometery", "UNIT 4", R.drawable.chemistry, null));
            contentList.add(new Content("Physical States of Matter", "UNIT 5", R.drawable.chemistry, null));




        }else if (bookTitle.equalsIgnoreCase("Geography")) {
            contentList.add(new Content("Introduction to Physics", " ", R.drawable.physics, null));
            contentList.add(new Content("Motion and Forces", " ", R.drawable.physics, null));
            contentList.add(new Content("Energy and Work", " ", R.drawable.physics, null));
            contentList.add(new Content("Work", " ", R.drawable.physics, null));

        }else if (bookTitle.equalsIgnoreCase("History")) {
            contentList.add(new Content("Introduction to Physics", " ", R.drawable.physics, null));
            contentList.add(new Content("Motion and Forces", "", R.drawable.physics, null));
            contentList.add(new Content("Energy and Work", " ", R.drawable.physics, null));
            contentList.add(new Content("Work", " ", R.drawable.physics, null));
        }
        else {
//            contentList.add(new Content("Default Chapter 1", bookTitle, R.drawable.default_image, null));
//            contentList.add(new Content("Default Chapter 2", bookTitle, R.drawable.default_image, null));
        }

        fetchChapters(bookTitle);

    }
    private void fetchChapters(String chapter) {
        DatabaseReference contentRe = FirebaseDatabase.getInstance().getReference()
                .child("TextBooks")
                .child(chapter)
                .child("Content");

        contentRe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String unitTitle = dataSnapshot.getKey(); // Chapter title
                        String pdfUrl = dataSnapshot.child("pdfUrl").getValue(String.class); // PDF URL
                        Log.e("Unit", "Title: " + unitTitle + " PDF URL: " + pdfUrl);

                        // Match unitTitle with contentList
                        boolean found = false;
                        for (Content content : contentList) {
                            if (unitTitle != null && content.getContentName().equalsIgnoreCase(unitTitle)) {
                                content.setPdfUrl(pdfUrl); // Set PDF URL for the Content object
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            // If no match, create a new Content and add it to the list
                            contentList.add(new Content(unitTitle, chapter, R.drawable.math, pdfUrl));
                        }
                    }
                    contentAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                } else {
                    Log.e("Firebase", "No content found for chapter: " + chapter);
                    Toast.makeText(ContentActivity.this, "No chapters available for " + chapter, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ContentActivity.this, "Failed to load content: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onContentSelected(Content content) {
        String pdfUrl = content.getPdfUrl();
        if (pdfUrl != null){
            downloadAndOpenPdf(pdfUrl);
        }else {
            Toast.makeText(this, "PDF not available for this chapter", Toast.LENGTH_SHORT).show();
        }

    }

    private void downloadAndOpenPdf(String pdfUrl)
    {
        String fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
        File pdfFile = new File(getCacheDir(), fileName);

        if (pdfFile.exists())
        {
            openPdfViewActivity(pdfFile);
        }else {
            new Thread(()->{

                try {
                    URL url = new URL(pdfUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        InputStream inputStream = connection.getInputStream();

//                        File cacheDir = getCacheDir();
//                        File pdfFile = new File(cacheDir, "temp.pdf");
                        FileOutputStream outputStream =  new FileOutputStream(pdfFile);

                        byte[] buffer = new byte[1024];
                        int len;
                        while((len = inputStream.read(buffer)) != -1)
                        {
                            outputStream.write(buffer, 0, len);
                        }

                        outputStream.close();
                        inputStream.close();

                        runOnUiThread(()->{

                            openPdfViewActivity(pdfFile);
                        });
                    }else {
                        runOnUiThread(()->{
                            Toast.makeText(this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                        });
                    }
                }catch (Exception e){

                    Log.e("PDF download","error"+e.getMessage());
                    runOnUiThread(()->{
                        Toast.makeText(this, "Error downloading PDF", Toast.LENGTH_SHORT).show();
                    });

                }

            }).start();
        }

    }

    private void openPdfViewActivity(File pdfFile) {
        Intent intent  = new Intent(ContentActivity.this, PdfViewerActivity.class);
        intent.putExtra("pdfFilePath", pdfFile.getAbsolutePath());
        startActivity(intent);
    }

}