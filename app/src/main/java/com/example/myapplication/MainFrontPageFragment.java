package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


import com.example.myapplication.R;
import com.example.myapplication.SearchWebActivity;

public class MainFrontPageFragment extends Fragment {

    private boolean[] isSwitchOnWrapper = new boolean[1];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_front_page, container, false);

        Button sendButton = view.findViewById(R.id.sendButton);
        EditText userInputEditText;
        userInputEditText = view.findViewById(R.id.user_input_edit_text);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = userInputEditText.getText().toString();

                // 创建 Intent 并启动服务
                Intent intent = new Intent(getActivity(), ChatCompletionService.class);
                intent.putExtra("user_input", userInput);
                getActivity().startService(intent);
            }
        });

        isSwitchOnWrapper[0] = false;

        ImageButton navIcon = view.findViewById(R.id.nav_icon);
        navIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });

        ImageButton openBiliButton = view.findViewById(R.id.openBiliButton);
        openBiliButton.setOnClickListener(new View.OnClickListener() {
            //            @Override
//            public void onClick(View v) {
//                String url = "https://www.bilibili.com/";
//                Intent intent = new Intent(getActivity(), SearchWebActivity.class);
//                intent.putExtra("url", url);
//                startActivity(intent);
//            }
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BiliActivity.class);
                intent.putExtra("switch_state", isSwitchOnWrapper[0]);
                startActivity(intent);
            }
        });


        ImageButton openWebViewButton = view.findViewById(R.id.openBaiduButton);
        openWebViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BaiduActivity.class);
                intent.putExtra("switch_state", isSwitchOnWrapper[0]);
                startActivity(intent);
            }
        });

        EditText urlEditText = view.findViewById(R.id.urlEditText);
        Button goButton = view.findViewById(R.id.goButton);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlEditText.getText().toString();
                Intent intent = new Intent(getActivity(), SearchWebActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        SwitchCompat mySwitch = view.findViewById(R.id.mySwitch);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwitchOnWrapper[0] = isChecked;
                if (isChecked) {
                    Toast.makeText(getActivity(), "鹰眼保护已开启", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "鹰眼保护已关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}