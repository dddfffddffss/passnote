package com.example.passnote;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.adapter.edit_pass_adapter;
import com.example.passnote.util.pass_data;

import java.util.HashMap;
import java.util.HashSet;

public class edit_pass extends AppCompatActivity {

    boolean isnotpushed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_content);

        // 1. 전체 pass_data
        pass_data pass_data = new pass_data(this);
        HashMap<Integer, String[]> all_list = pass_data.get_all_list();

        // 2. 현재 페이지 데이터
        Intent intent = getIntent();
        int id = intent.getIntExtra("content_id",-100);

        HashSet<String> contents_tag = pass_data.get_contents_tag_list(id);
        HashMap<Integer, String> tag_list = pass_data.get_tag_list();

        String[] data;
        String name, login_id, login_pass;
        if(id>-1) {
            data = all_list.get(id);
            name = data[0];
            login_id = data[1];
            login_pass = data[2];
        } else {
            name = "";
            login_id = "";
            login_pass = "";
        }

        // 3. 각 오브젝트
        EditText et_name = findViewById(R.id.edit_name);
        EditText et_login_id = findViewById(R.id.edit_id);
        EditText et_login_pass = findViewById(R.id.edit_pass);

        et_name.setText(name);
        et_login_id.setText(login_id);
        et_login_pass.setText(login_pass);

        // 리사이클러 뷰
        RecyclerView edit_tag = findViewById(R.id.edit_tag_to_pass);
        edit_tag.setLayoutManager(new LinearLayoutManager(this));
        edit_pass_adapter eta = new edit_pass_adapter(getApplicationContext(), tag_list, contents_tag);
        edit_tag.setAdapter(eta);

        // delete버튼
        Button delete = findViewById(R.id.edit_delete);
        delete.setOnClickListener(v -> {
            // 하기전에 반드시 알람창 띄우기.
            pass_data.content_delete(id);
            Toast.makeText(getApplicationContext(), "삭제", Toast.LENGTH_SHORT).show();
            isnotpushed = false;
            onBackPressed();
        });

        // save버튼
        Button save = findViewById(R.id.edit_save);
        save.setOnClickListener(v -> {
            int final_id;

            String name1 = et_name.getText().toString();
            String login_id1 = et_login_id.getText().toString();
            String login_pass1 = et_login_pass.getText().toString();
            if (id==-100 && pass_data.get_name_list().contains(name1)){
                Toast.makeText(getApplicationContext(), name1 +"은 중복입니다.", Toast.LENGTH_SHORT).show();
            }else{
                if(id!=-100){
                    final_id = id;
                    pass_data.content_update(id, name1, login_id1, login_pass1);
                } else{
                    final_id = pass_data.content_add(name1, login_id1, login_pass1);
                }

                HashSet<String> content_tag = eta.getContents_tag();
                pass_data.tag_add(content_tag, final_id);

                Toast.makeText(getApplicationContext(), "저장", Toast.LENGTH_SHORT).show();
                isnotpushed = false;
                onBackPressed();

            }
        });

        // cancle버튼
        Button cancle = findViewById(R.id.edit_cancle);
        cancle.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
            isnotpushed = false;
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        if(isnotpushed){
            Toast.makeText(getApplicationContext(),"저장되지 않았습니다.",Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }
}
