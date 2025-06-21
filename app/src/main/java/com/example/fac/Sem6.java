// Sem1.java
package com.example.fac;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fac.databinding.ActivitySem6Binding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Sem6 extends AppCompatActivity {

    private ActivitySem6Binding binding;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private ArrayList<Document> documentList;
    private String semesterName; // Store semester name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySem6Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.recyclerViewSem6);
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
        Log.d("SEM6_DEBUG", "Fetching documents for semester: " + semester);
        documentList.clear();

        String[] subjectSubcollectionNames = {"PYTHON","Artificial Intelligence","Cryptography And Network","Security","Android Programming","iOS Programming","Embedded Systems","Advanced Algorithms","Machine Learning","Soft Computing","Information Theory And Coding","Advanced Operating Systems","Internet Of Things","Graph Theory","Advanced Computer Network","Notice","University Result","Mid Sem Result","Exam Timetable"}; // Ensure this is correct

        final int[] subcollectionQueriesCompleted = {0};
        final int totalSubcollections = subjectSubcollectionNames.length;

        for (String subjectName : subjectSubcollectionNames) {
            Log.d("SEM6_DEBUG", "Fetching from subject: " + subjectName);
            db.collection("documents")
                    .document(semester)
                    .collection(subjectName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SEM6_DEBUG", "Firestore query for subject " + subjectName + " successful. Documents found: " + task.getResult().size()); // Log document count
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name"); // Now 'name' is original file name
                                String url = document.getString("url");
                                Log.d("SEM6_DEBUG", "Document Data - Subject: " + subjectName + ", Name: " + name + ", URL: " + url); // Log each document's data
                                if (name != null && url != null) {
                                    // Pass semesterName here when creating Document object
                                    Document doc = new Document(name, url, subjectName, semesterName);
                                    documentList.add(doc);
                                } else {
                                    Log.w("SE6_DEBUG", "Document with null name or url found in " + subjectName);
                                }
                            }
                        } else {
                            Log.w("SEM1_DEBUG", "Error getting documents from " + subjectName, task.getException());
                            Toast.makeText(Sem6.this, "Error fetching documents from " + subjectName, Toast.LENGTH_SHORT).show();
                        }

                        subcollectionQueriesCompleted[0]++;
                        if (subcollectionQueriesCompleted[0] == totalSubcollections) {
                            Log.d("SEM6_DEBUG", "All subcollection queries completed. Total documents in list: " + documentList.size()); // Log final list size
                            adapter.notifyDataSetChanged();
                            if (documentList.isEmpty()) {
                                binding.emptyView.setVisibility(View.VISIBLE);
                                Log.d("SEM6_DEBUG", "Empty view set to VISIBLE");
                            } else {
                                binding.emptyView.setVisibility(View.GONE);
                                Log.d("SEM6_DEBUG", "Empty view set to GONE");
                            }
                        }
                    });
        }
    }
}