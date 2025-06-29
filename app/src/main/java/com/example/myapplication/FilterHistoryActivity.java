package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.FilteredContentAdapter;
import com.example.myapplication.db.FilteredContentDao;
import com.example.myapplication.model.FilteredContent;
import java.util.List;

public class FilterHistoryActivity extends AppCompatActivity {
    private FilteredContentDao contentDao;
    private FilteredContentAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_history);

        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("过滤历史");

        // 初始化RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化数据库
        contentDao = new FilteredContentDao(this);
        contentDao.open();

        // 加载数据
        loadFilteredContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次活动可见时刷新数据
        loadFilteredContent();
    }

    private void loadFilteredContent() {
        List<FilteredContent> contentList = contentDao.getAllFilteredContent();
        Log.d("FilterHistory", "从数据库加载到 " + contentList.size() + " 条记录");
        if (adapter == null) {
            adapter = new FilteredContentAdapter(contentList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(contentList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentDao.close();
    }
} 