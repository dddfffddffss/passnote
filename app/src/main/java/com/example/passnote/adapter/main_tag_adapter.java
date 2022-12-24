package com.example.passnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.R;
import com.example.passnote.util.pass_data;

import java.util.ArrayList;

public class main_tag_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context c;
    pass_data pass_data;
    contents_adapter content;
    ArrayList<String> key_array;
    Button reset;
    EditText search;

    int stop_postion;

    public main_tag_adapter(Context c, contents_adapter content, pass_data pass_data){
        this.c = c;
        this.content = content;
        this.pass_data = pass_data;

        key_array = new ArrayList<>(pass_data.get_tag_list().values());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.main_holder_tag_layout, parent, false) ;
        return new holder(view, content);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        String key = key_array.get(position);
        ((holder) holder).set_content(key, this, position);
        if(content.get_tag_switch(key)){
            ((holder) holder).main_tag.setBackground(c.getResources().getDrawable(R.drawable.sellected_tag_cell));
        } else {
            ((holder) holder).main_tag.setBackground(c.getResources().getDrawable(R.drawable.tag_cell));
        }
    }

    @Override
    public int getItemCount() {
        return key_array.size();
    }

    public void update_all_list(){
        key_array = new ArrayList<>(pass_data.get_tag_list().values());
    }

    public void set_search_reset_button(EditText search, Button reset){
        this.search = search;
        this.reset = reset;
    }

    public int getStop_postion(){
        return stop_postion;
    }

    public class holder extends RecyclerView.ViewHolder {
        contents_adapter content;

        public ImageView main_tag;
        TextView tag;
        tag_click lintener;

        public holder(@NonNull View itemView, contents_adapter content) {
            super(itemView);

            this.content = content;

            lintener = new tag_click();
            itemView.setOnClickListener(lintener);

            main_tag = itemView.findViewById(R.id.main_tag);
            tag = itemView.findViewById(R.id.main_tag_tv);
        }

        public void set_content(String tag_content, main_tag_adapter parent, int position){
            lintener.set_tag_id(tag_content);
            lintener.setparent(parent,position);
            tag.setText(tag_content);
        }

        class tag_click implements View.OnClickListener {
            main_tag_adapter parent;
            int position;
            String tag_content;

            public void set_tag_id(String tag_content) {
                this.tag_content = tag_content;
            }

            public void setparent(main_tag_adapter parent,int position) {
                this.position = position;
                this.parent = parent;
            }

            @Override
            public void onClick(View v) {
                stop_postion = position;
                if(search.getText().length()>0){
                    reset.performClick();
                }
                content.set_tag_switch(tag_content);
                ImageView i = v.findViewById(R.id.main_tag);
                if(content.get_tag_switch(tag_content)){
                    i.setBackground(c.getResources().getDrawable(R.drawable.sellected_tag_cell));
                }else{
                    i.setBackground(c.getResources().getDrawable(R.drawable.tag_cell));
                }
                parent.notifyItemChanged(position);
            }
        }
    }
}
