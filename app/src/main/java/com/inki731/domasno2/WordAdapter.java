package com.inki731.domasno2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;

    public WordAdapter(Context context, ArrayList<String> words) {
        super(context, 0, words);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.word_item, parent, false);
        }

        String word = getItem(position);
        if (word != null) {
            TextView wordTextView = convertView.findViewById(R.id.wordTextView);
            wordTextView.setText(word);
        }

        return convertView;
    }
}