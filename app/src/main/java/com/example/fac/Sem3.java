// Sem1.java
package com.example.fac;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fac.databinding.ActivitySem3Binding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Sem3 extends AppCompatActivity {

    private ActivitySem3Binding binding;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private ArrayList<Document> documentList;
    private String semesterName; // Store semester name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySem3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.recyclerViewSem3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        documentList = new ArrayList<Document>();
        adapter = new DocumentAdapter(this, documentList);
        recyclerView.setAdapter(adapter);

        semesterName = getIntent().getStringExtra("semester"); // Get semester name here
        if (semesterName != null) {
            fetchDocuments(semesterName); // Pass semester name to fetchDocuments
        }
    }

    private void fetchDocuments(String semester) {
        Log.d("SEM1_DEBUG", "Fetching documents for semester: " + semester);
        documentList.clear();

        String[] subjectSubcollectionNames = {"asdfghjk","Discrete Mathematics","Data Structures And Algorithms","Digital Electronics","IT Workshop","Database Management Systems","Notice","University Result","Mid Sem Result","Exam Timetable"}; // Ensure this is correct

        final int[] subcollectionQueriesCompleted = {0};
        final int totalSubcollections = subjectSubcollectionNames.length;

        for (String subjectName : subjectSubcollectionNames) {
            Log.d("SEM3_DEBUG", "Fetching from subject: " + subjectName);
            db.collection("documents")
                    .document(semester)
                    .collection(subjectName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SEM3_DEBUG", "Firestore query for subject " + subjectName + " successful. Documents found: " + task.getResult().size()); // Log document count
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name"); // Now 'name' is original file name
                                String url = document.getString("url");
                                Log.d("SEM3_DEBUG", "Document Data - Subject: " + subjectName + ", Name: " + name + ", URL: " + url); // Log each document's data
                                if (name != null && url != null) {
                                    // Pass semesterName here when creating Document object
                                    Document doc = new Document(name, url, subjectName, semesterName);
                                    documentList.add(doc);
                                } else {
                                    Log.w("SEM3_DEBUG", "Document with null name or url found in " + subjectName);
                                }
                            }
                        } else {
                            Log.w("SEM3_DEBUG", "Error getting documents from " + subjectName, task.getException());
                            Toast.makeText(Sem3.this, "Error fetching documents from " + subjectName, Toast.LENGTH_SHORT).show();
                        }

                        subcollectionQueriesCompleted[0]++;
                        if (subcollectionQueriesCompleted[0] == totalSubcollections) {
                            Log.d("SEM3_DEBUG", "All subcollection queries completed. Total documents in list: " + documentList.size()); // Log final list size
                            adapter.notifyDataSetChanged();
                            if (documentList.isEmpty()) {
                                binding.emptyView.setVisibility(View.VISIBLE);
                                Log.d("SEM3_DEBUG", "Empty view set to VISIBLE");
                            } else {
                                binding.emptyView.setVisibility(View.GONE);
                                Log.d("SEM3_DEBUG", "Empty view set to GONE");
                            }
                        }
                    });
        }
    }
}