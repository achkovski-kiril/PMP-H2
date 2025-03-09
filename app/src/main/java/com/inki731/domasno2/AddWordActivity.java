package com.inki731.domasno2;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AddWordActivity extends AppCompatActivity {

    private EditText editMacedonian, editEnglish;
    private Button saveButton;
    private File wordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        editMacedonian = findViewById(R.id.editMacedonian);
        editEnglish = findViewById(R.id.editEnglish);
        saveButton = findViewById(R.id.saveButton);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (storageDir != null) {
            wordFile = new File(storageDir, "dictionary.txt");
        }

        saveButton.setOnClickListener(v -> addWord());
    }

    private void addWord() {
        String macedonian = editMacedonian.getText().toString().trim();
        String english = editEnglish.getText().toString().trim();

        if (macedonian.isEmpty() || english.isEmpty()) {
            Toast.makeText(this, "Both fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String newEntry = macedonian + ", " + english;

        try (FileWriter writer = new FileWriter(wordFile, true)) {
            writer.append(newEntry).append("\n");
            Toast.makeText(this, "Word added!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving word", Toast.LENGTH_SHORT).show();
        }

        editMacedonian.setText("");
        editEnglish.setText("");

        finish();
    }
}
