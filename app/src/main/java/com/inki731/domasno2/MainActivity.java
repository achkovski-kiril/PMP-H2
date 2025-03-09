package com.inki731.domasno2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WordAdapter.OnWordDeleteListener {

    private Button addButton;
    private ListView wordListView;
    private ArrayList<String> allWords;
    private ArrayList<String> filteredWords;
    private File wordFile;
    private EditText searchMacedonian;
    private EditText searchEnglish;
    private WordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addButton);
        wordListView = findViewById(R.id.wordListView);
        searchMacedonian = findViewById(R.id.searchMacedonian);
        searchEnglish = findViewById(R.id.searchEnglish);

        allWords = new ArrayList<>();
        filteredWords = new ArrayList<>();

        adapter = new WordAdapter(this, filteredWords, this);
        wordListView.setAdapter(adapter);

        loadWords();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
            startActivity(intent);
        });

        TextWatcher filterWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        searchMacedonian.addTextChangedListener(filterWatcher);
        searchEnglish.addTextChangedListener(filterWatcher);
    }

    private void applyFilters() {
        String macedonianFilter = searchMacedonian.getText().toString().trim().toLowerCase();
        String englishFilter = searchEnglish.getText().toString().trim().toLowerCase();

        filteredWords.clear();

        if (macedonianFilter.isEmpty() && englishFilter.isEmpty()) {
            filteredWords.addAll(allWords);
        } else {
            for (String entry : allWords) {
                String[] parts = entry.split(", ");
                if (parts.length == 2) {
                    String macedonianWord = parts[0].toLowerCase();
                    String englishWord = parts[1].toLowerCase();

                    boolean matchesMacedonian = macedonianFilter.isEmpty() ||
                            macedonianWord.contains(macedonianFilter);
                    boolean matchesEnglish = englishFilter.isEmpty() ||
                            englishWord.contains(englishFilter);

                    if (matchesMacedonian && matchesEnglish) {
                        filteredWords.add(entry);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWords();
    }

    private void loadWords() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (storageDir == null) {
            Toast.makeText(this, "Error accessing storage", Toast.LENGTH_SHORT).show();
            return;
        }

        wordFile = new File(storageDir, "dictionary.txt");
        if (!wordFile.exists()) {
            try {
                wordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to create dictionary file", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        allWords.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(wordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allWords.add(line.trim());
                }
            }
            filteredWords.clear();
            filteredWords.addAll(allWords);

            adapter.notifyDataSetChanged();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading words", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWordDelete(int position, String word) {
        // Ask for confirmation before deleting
        new AlertDialog.Builder(this)
                .setTitle("Delete Word")
                .setMessage("Are you sure you want to delete this word?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    filteredWords.remove(position);
                    allWords.remove(word);
                    adapter.notifyDataSetChanged();

                    saveWordsToFile();

                    Toast.makeText(MainActivity.this, "Word deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void saveWordsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false))) {
            for (String word : allWords) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving changes", Toast.LENGTH_SHORT).show();
        }
    }
}
