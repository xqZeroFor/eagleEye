package com.example.myapplication;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

    private LinearLayout keywordContainer; // 关键词容器
    private TextView counterText; // 计数器
    private boolean isEditMode = true;
    private List<String> wordList = new ArrayList<>();
    private int maxKeywords = 30;
    private ImageView addButton;
    private SeekBar seekBar;
    private TextView levelText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_adjust_page, container, false);

        // 初始化视图
        // 绑定视图
        keywordContainer = view.findViewById(R.id.keywordContainer);
        counterText = view.findViewById(R.id.counterText);
        addButton = view.findViewById(R.id.addButton);
        seekBar = view.findViewById(R.id.seekBar);
        levelText = view.findViewById(R.id.levelText);

        // 设置监听器
        addButton.setOnClickListener(v -> showAddWordDialog());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 计算等级（0-100 映射到 0-10）
                int level = Math.min(progress / 10, 10); // 取整
                levelText.setText("Lv." + level);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // 初始化数据
        initSampleData();
        updateCounter();

        return view;
    }

    private void initSampleData() {
        wordList.clear();
        wordList.addAll(KeywordManager.getInstance().getKeywords());
        refreshKeywordViews();
    }

    private void showAddWordDialog() {
        if (wordList.size() >= maxKeywords) {
            Toast.makeText(getContext(), "已达到最大关键词数量", Toast.LENGTH_SHORT).show();
            return;
        }
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
        addKeywordView(word);
        updateCounter();
        // 更新KeywordManager
        KeywordManager.getInstance().setKeywords(wordList);
    }

    private void refreshKeywordViews() {
        keywordContainer.removeAllViews();
        for (String word : wordList) {
            addKeywordView(word);
        }
    }

    private void addKeywordView(String word) {
        View keywordView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_keyword, keywordContainer, false);

        TextView textView = keywordView.findViewById(R.id.tv_keyword);
        ImageView deleteBtn = keywordView.findViewById(R.id.btn_delete);

        textView.setText(word);
        deleteBtn.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        deleteBtn.setOnClickListener(v -> {
            wordList.remove(word);
            keywordContainer.removeView(keywordView);
            updateCounter();
            // 更新KeywordManager
            KeywordManager.getInstance().setKeywords(wordList);
            Toast.makeText(getActivity(), "已删除: " + word, Toast.LENGTH_SHORT).show();
        });

        keywordContainer.addView(keywordView);
    }

    private void updateCounter() {
        counterText.setText(wordList.size() + "/" + maxKeywords);
    }


}