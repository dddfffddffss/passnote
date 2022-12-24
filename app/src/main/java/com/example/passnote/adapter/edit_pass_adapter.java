package com.example.passnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class edit_pass_adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    HashMap<Integer, String> tag_list;
    ArrayList<Integer> key_array;
    HashSet<String> contents_tag;

    public edit_pass_adapter(Context c,HashMap<Integer, String> tag_list, HashSet<String> contents_tag){
        this.c = c;
        this.tag_list = tag_list;
        key_array = new ArrayList<>(tag_list.keySet());
        this.contents_tag = contents_tag;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.edit_holder_tag_layout, parent, false) ;
        return new edit_pass_adapter.holder(c, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int key = key_array.get(position);
        ((edit_pass_adapter.holder) holder).set_content(tag_list.get(key), position);
    }

    @Override
    public int getItemCount() {
        return tag_list.size();
    }

    public HashSet<String> getContents_tag(){
        return contents_tag;
    }

    class holder extends RecyclerView.ViewHolder {
        Context context;
        TextView t;
        click c;
        public holder(Context context, @NonNull View itemView) {
            super(itemView);

            this.context = context;
            c = new click(context);
            itemView.setOnClickListener(c);
            t = itemView.findViewById(R.id.edit_tag_text);
        }

        public void set_content(String s, int position){
            t.setText(s);
            if(contents_tag.contains(s)){
                t.setBackground(context.getResources().getDrawable(R.drawable.sellected_tag_cell));
            } else {
                t.setBackground(context.getResources().getDrawable(R.drawable.tag_cell));
            }
            c.set_content(t, s, position);
        }

        class click implements View.OnClickListener {
            Context c;
            TextView click_t;
            String tag;
            int position;

            public click(Context c){
                this.c = c;
            }
            @Override
            public void onClick(View v) {
                if(contents_tag.contains(tag)){
                    contents_tag.remove(tag);
                    click_t.setBackground(c.getResources().getDrawable(R.drawable.sellected_tag_cell));
                } else {
                    contents_tag.add(tag);
                    click_t.setBackground(c.getResources().getDrawable(R.drawable.tag_cell));
                }

                notifyItemChanged(position);
            }

            public void set_content(TextView click_t, String tag, int position){
                this.click_t = click_t;
                this.tag = tag;
                this.position = position;
            }
        }
    }
}
