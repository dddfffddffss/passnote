package com.example.passnote;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.adapter.edit_tag_adapter;
import com.example.passnote.util.pass_data;

import java.util.HashMap;

public class edit_tag extends AppCompatActivity {
    TextView current;
    EditText tag_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tag);

        // 1. 전체 tag_list
        pass_data pass_data = new pass_data(this);
        HashMap<Integer, String> tag_list = pass_data.get_tag_list();

        // 2. tag name
        current = findViewById(R.id.edit_current);
        tag_name = findViewById(R.id.tag_name);

        // 3.리사이클러뷰
        RecyclerView r = findViewById(R.id.edit_tag);
        r.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        edit_tag_adapter e = new edit_tag_adapter(tag_list, current, tag_name);
        r.setAdapter(e);

        // 4 . Buttons
        Button save = findViewById(R.id.edit_tag_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Current = current.getText().toString();
                String name = tag_name.getText().toString();

                if (!Current.equals("Edit Tag!")){
                    pass_data.tag_update(Current, name);
                    Toast.makeText(getApplicationContext(),name+"로 바꼈습니다.",Toast.LENGTH_SHORT).show();
                } else if (name.equals("")) {
                    Toast.makeText(getApplicationContext(),"공백은 이름이 될 수 없습니다.",Toast.LENGTH_SHORT).show();
                } else {
                    if(pass_data.get_tag_list().containsValue(name)){
                        Toast.makeText(getApplicationContext(),name+"은 이미 있습니다.",Toast.LENGTH_SHORT).show();
                    } else {
                        pass_data.tag_add(name);
                        Toast.makeText(getApplicationContext(),name+"이 추가되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                }

                current.setText("Edit Tag!");
                tag_name.setText("");
                e.update_all_list(pass_data.get_tag_list());
                e.notifyDataSetChanged();
            }
        });

        Button cancle = findViewById(R.id.edit_tag_cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button delete = findViewById(R.id.edit_tag_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cu = current.getText().toString();
                if(pass_data.get_tag_list().containsValue(cu)){
                    pass_data.tag_delete(cu);
                    Toast.makeText(getApplicationContext(),cu+"가 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    current.setText("Edit Tag!");

                    e.update_all_list(pass_data.get_tag_list());
                    e.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(),cu+"는 없는 테그입니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_tag_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == R.id.menu_tag_add) {
            current.setText("Edit Tag!");
            tag_name.setText("");
        }
        return false;
    }
}