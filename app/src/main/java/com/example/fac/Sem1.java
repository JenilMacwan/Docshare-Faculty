// Sem1.java (Modified fetchDocuments method with corrected logging)
package com.example.fac;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fac.databinding.ActivitySem1Binding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Sem1 extends AppCompatActivity {

    private ActivitySem1Binding binding;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private ArrayList<Document> documentList;
    private String semesterName; // Store semester name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySem1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.recyclerViewSem1);
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

        List<Document> adapterDocumentList = adapter.getdocumentList();
        adapterDocumentList.clear();

        String[] subjectSubcollectionNames = {"pic", "OOP-C", "Maths-1","Engineering Physics","Fundamental Of Programming","Elements Of Civil Engineering","Engineering Graphics","Environmental Studies","Notice","University Result","Mid Sem Result","Exam Timetable"}; // Ensure this is correct

        final int[] subcollectionQueriesCompleted = {0};
        final int totalSubcollections = subjectSubcollectionNames.length;

        for (String subjectName : subjectSubcollectionNames) {
            Log.d("SEM1_DEBUG", "Fetching from subject: " + subjectName);
            db.collection("documents")
                    .document(semester)
                    .collection(subjectName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SEM1_DEBUG", "Firestore query for subject " + subjectName + " successful. Documents found: " + task.getResult().size()); // Log document count
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String url = document.getString("url");
                                Log.d("SEM1_DEBUG", "Firestore Document Data - Subject: " + subjectName + ", Name: " + name + ", URL: " + url);

                                if (name != null && url != null) {
                                    // Pass semester and subjectName to the Document constructor
                                    Document doc = new Document(name, url, subjectName, semester);
                                    documentList.add(doc);
                                    Log.d("SEM1_DEBUG", "Document object added: " + doc.getName() + ", Semester: " + doc.getSemester() + ", Subject: " + doc.getSubjectName());
                                } else {
                                    Log.w("SEM1_DEBUG", "Document with null name or url found in " + subjectName);
                                }
                            }
                            subcollectionQueriesCompleted[0]++;
                            if (subcollectionQueriesCompleted[0] == totalSubcollections) {
                                Log.d("SEM1_DEBUG", "All subcollection queries COMPLETED. Calling notifyDataSetChanged NOW."); // Log final list size
                                adapter.notifyDataSetChanged();
                                if (documentList.isEmpty()) {
                                    binding.emptyView.setVisibility(View.VISIBLE);
                                    Log.d("SEM1_DEBUG", "Empty view set to VISIBLE");
                                } else {
                                    binding.emptyView.setVisibility(View.GONE);
                                    Log.d("SEM1_DEBUG", "Empty view set to GONE");
                                }
                            }
                        }
                    });
        }
    }}


