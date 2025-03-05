package com.example.myapplication;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.flexbox.FlexboxLayout;
import java.util.ArrayList;
import java.util.List;

public class MainAdjustPageFragment extends Fragment {

    private FlexboxLayout flexboxLayout;
    private boolean isEditMode = false;
    private List<String> wordList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_adjust_page, container, false);

        flexboxLayout = view.findViewById(R.id.flexboxLayout);
        wordList = new ArrayList<>();

        view.findViewById(R.id.button_edit).setOnClickListener(v -> toggleEditMode());
        view.findViewById(R.id.button_add_word).setOnClickListener(v -> showAddWordDialog());

        return view;
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        for (int i = 0; i < flexboxLayout.getChildCount(); i++) {
            View wordView = flexboxLayout.getChildAt(i);
            ImageView deleteButton = wordView.findViewById(R.id.button_delete);
            deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        }
    }

    private void showAddWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Word");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String word = input.getText().toString().trim();
            if (!word.isEmpty()) {
                addWord(word);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addWord(String word) {
        wordList.add(word);
        View wordView = LayoutInflater.from(getContext()).inflate(R.layout.item_word, flexboxLayout, false);
        TextView textViewWord = wordView.findViewById(R.id.textViewWord);
        textViewWord.setText(word);

        ImageView deleteButton = wordView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(v -> {
            wordList.remove(word);
            flexboxLayout.removeView(wordView);
            Toast.makeText(getContext(), "Deleted: " + word, Toast.LENGTH_SHORT).show();
        });

        flexboxLayout.addView(wordView);
    }
}