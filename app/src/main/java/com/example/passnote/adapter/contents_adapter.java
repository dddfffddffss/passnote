package com.example.passnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.R;
import com.example.passnote.util.pass_data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class contents_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    Intent intent;

    com.example.passnote.util.pass_data pass_data;
    HashMap<Integer, String[]> all_list;
    HashMap<Integer, HashSet<String>> contents_tag;
    HashSet<String> tag_switch;
    ArrayList<Integer> key_array;

    int stop_position;

    public contents_adapter(Context c, Intent intent, pass_data pass_data){
        this.pass_data = pass_data;

        all_list = pass_data.get_all_list();
        key_array = new ArrayList<>();
        key_array.addAll(all_list.keySet());

        contents_tag = pass_data.get_contents_tag_list();

        tag_switch  = new HashSet<>();

        this.c = c;
        this.intent = intent;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.main_holder_content_layout, parent, false) ;
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int key = key_array.get(position);
        ((holder) holder).set_content(all_list.get(key));
        ((holder) holder).set_content_click(key,position);
    }

    @Override
    public int getItemCount() {
        return key_array.size();
    }

    public void set_tag_switch(String tag_content){
        if(tag_switch.contains(tag_content)){
            tag_switch.remove(tag_content);
        } else{
            tag_switch.add(tag_content);
        }

        key_array = new ArrayList<>();
        if(tag_switch.isEmpty()){
            key_array.addAll(all_list.keySet());
        } else {
            for(int i:all_list.keySet()) {
                if(contents_tag.containsKey(i) && contents_tag.get(i).containsAll(tag_switch)) {
                    key_array.add(i);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void reset_tag_switch() {
        tag_switch = new HashSet<>();
        key_array = new ArrayList<>();
        key_array.addAll(all_list.keySet());

        notifyDataSetChanged();
    }

    public boolean get_tag_switch(String tag_content){
        return tag_switch.contains(tag_content);
    }

    public void set_search_name(String search_name){
        reset_tag_switch();

        key_array = new ArrayList<>();
        for(int i:all_list.keySet()){
            if(all_list.get(i)[0].contains(search_name))key_array.add(i);
        }

        notifyDataSetChanged();
    }

    public void update_all_list(){
        all_list = pass_data.get_all_list();
        key_array = new ArrayList<>();
        key_array.addAll(all_list.keySet());

        tag_switch  = new HashSet<>();

        contents_tag = pass_data.get_contents_tag_list();
    }

    public int get_stop_position(){
        return stop_position;
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView name, login_id, login_pass;
        content_click content_click;
        public int id;

        public holder(@NonNull View itemView) {
            super(itemView);

            this.content_click =  new content_click(c, intent);
            itemView.setOnClickListener(content_click);

            name = itemView.findViewById(R.id.name);
            login_id = itemView.findViewById(R.id.login_id);
            login_pass = itemView.findViewById(R.id.login_pass);
        }

        public void set_content_click(int id, int position){
            content_click.setid(id, position);
        }

        public void set_content(String[] content){
            name.setText(content[0]);
            login_id.setText(content[1]);
            login_pass.setText(content[2]);
        }

        class content_click implements View.OnClickListener  {
            Context c;
            Intent intent;
            int id,position;

            public content_click(Context c, Intent intent){
                this.c = c;
                this.intent = intent;
            }

            @Override
            public void onClick(View v){
                stop_position = position;

                intent.putExtra("content_id", id);
                c.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
            }

            public void setid(int id, int position){
                this.id = id;
                this.position = position;
            }
        }
    }
}