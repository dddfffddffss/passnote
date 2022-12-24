package com.example.passnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.R;

import java.util.ArrayList;
import java.util.HashMap;

public class edit_tag_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    HashMap<Integer, String> tag_list;
    ArrayList<Integer> key_array;

    TextView current;
    EditText name;

    public edit_tag_adapter(HashMap<Integer, String> tag_list, TextView current, EditText name){
        this.tag_list = tag_list;
        key_array = new ArrayList<>();
        key_array.addAll(tag_list.keySet());

        this.current = current;
        this.name = name;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.edit_holder_tag_layout, parent, false) ;
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int key = key_array.get(position);
        ((holder) holder).setcontent(tag_list.get(key));
    }

    public void update_all_list(HashMap<Integer, String> tag_list){
        this.tag_list = tag_list;
        key_array = new ArrayList<>();
        key_array.addAll(tag_list.keySet());
    }

    @Override
    public int getItemCount() {
        return key_array.size();
    }

    class holder extends RecyclerView.ViewHolder{
        TextView tag_list_holder;
        edit_tag_click click;

        public holder(@NonNull View itemView) {
            super(itemView);

            tag_list_holder = itemView.findViewById(R.id.edit_tag_text);
            click = new edit_tag_click();
            itemView.setOnClickListener(click);
        }

        public void setcontent(String tag_name){
            click.setname(tag_name);
            tag_list_holder.setText(tag_name);
        }

        class edit_tag_click implements View.OnClickListener {
            String to_set_name;

            public void setname(String name){
                this.to_set_name = name;
            }

            @Override
            public void onClick(View v) {
                current.setText(to_set_name);
                name.setText(to_set_name);
            }
        }
    }
}
