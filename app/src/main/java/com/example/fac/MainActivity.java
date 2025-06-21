// MainActivity.java
package com.example.fac;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fac.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int SAF_FILE_PICKER_REQUEST_CODE = 102;
    private String selectedSemester;
    private String selectedSubjectName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSemesterSelectionDialog();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_aboutus)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    navController.navigate(R.id.nav_home);
                } else if (id == R.id.nav_gallery) {
                    navController.navigate(R.id.nav_gallery);
                } else if (id == R.id.nav_aboutus) {
                    navController.navigate(R.id.nav_aboutus);
                } else if (id == R.id.nav_slideshow) {
                    showLogoutDialog();
                }
                drawer.closeDrawers();
                return true;
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showSemesterSelectionDialog() {
        String[] semesters = {"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Semester");
        builder.setItems(semesters, (dialog, which) -> {
            String selectedSemester = "Sem" + (which + 1);
            showSubjectNameDialog(selectedSemester);
        });
        builder.show();
    }

    private void showSubjectNameDialog(String semester) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Subject Name");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String subjectName = input.getText().toString().trim();
            if (!subjectName.isEmpty()) {
                openFilePicker(semester, subjectName);
                startSemesterActivity(semester, subjectName);
            } else {
                Toast.makeText(MainActivity.this, "Subject name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void startSemesterActivity(String semester, String subjectName) {
        Intent intent;
        switch (semester) {
            case "Sem1":
                intent = new Intent(MainActivity.this, Sem1.class);
                break;
            case "Sem2":
                intent = new Intent(MainActivity.this, Sem2.class);
                break;
            case "Sem3":
                intent = new Intent(MainActivity.this, Sem3.class);
                break;
            case "Sem4":
                intent = new Intent(MainActivity.this, Sem4.class);
                break;
            case "Sem5":
                intent = new Intent(MainActivity.this, Sem5.class);
                break;
            case "Sem6":
                intent = new Intent(MainActivity.this, Sem6.class);
                break;
            case "Sem7":
                intent = new Intent(MainActivity.this, Sem7.class);
                break;
            case "Sem8":
                intent = new Intent(MainActivity.this, Sem8.class);
                break;
            default:
                return;
        }
        intent.putExtra("semester", semester);
        intent.putExtra("subjectName", subjectName); // Pass the subject name
        startActivity(intent);
    }

    private void openFilePicker(String semester, String subjectName) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, SAF_FILE_PICKER_REQUEST_CODE);
        this.selectedSemester = semester;
        this.selectedSubjectName = subjectName;
        Log.d("FilePicker", "SAF file picker intent launched");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAF_FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                uploadFileToFirebase(fileUri, selectedSemester, selectedSubjectName);
                Log.d("FileUri", "File URI: " + fileUri.toString());
            }
        }
    }

    private void uploadFileToFirebase(Uri fileUri, String semester, String subjectName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        String originalFileName = getFileNameFromUri(fileUri); // Get original file name
        if (originalFileName == null || originalFileName.isEmpty()) {
            originalFileName = UUID.randomUUID().toString(); // Fallback to UUID if name extraction fails
            Log.w("Upload", "Could not extract original file name, using UUID instead.");
        }

        StorageReference fileRef = storageRef.child("documents/" + semester + "/" + subjectName + "/" + originalFileName);

        String finalOriginalFileName = originalFileName;
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> { saveDocumentMetadata(semester, subjectName, downloadUri.toString(), finalOriginalFileName);// Save original file name
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Storage Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("StorageUpload", "Storage upload error: " + e.getMessage());
                });
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
            }
        } catch (Exception e) {
            Log.e("FileUtil", "Error getting file name from URI: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }


    private void saveDocumentMetadata(String semester, String subjectName, String downloadUrl, String fileName) {
        Map<String, Object> documentData = new HashMap<>();
        documentData.put("url", downloadUrl);
        documentData.put("name", fileName); // Save original file name

        db.collection("documents")
                .document(semester)
                .collection(subjectName)
                .add(documentData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Firestore Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreUpload", "Firestore upload error: " + e.getMessage());
                });
    }
}