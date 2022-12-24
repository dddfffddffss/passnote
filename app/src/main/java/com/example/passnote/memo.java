package com.example.passnote;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.passnote.util.pass_data;

public class memo extends AppCompatActivity {
    InputMethodManager imm;
    pass_data pass_data;
    EditText et;
    int count;
    String old_memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        pass_data = new pass_data(getApplication());
        count = 0;
        et = findViewById(R.id.edittext_memo);

        old_memo=pass_data.get_old_memo();

        et.setText(pass_data.get_memo());
        et.setEnabled(false);
        et.setTextColor(Color.BLACK);

        imm.hideSoftInputFromWindow(et.getWindowToken(),0);
    }

    MenuItem cancle,edit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memo, menu);

        edit = menu.findItem(R.id.memo_menu_edit);
        cancle = menu.findItem(R.id.memo_menu_cancle);
        cancle.setVisible(false);

        return true;
    }

    String s = "";

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if(item.getItemId() == R.id.memo_menu_edit) {
            s = et.getText().toString();
            count++;
            if(count%2==1){
                et.setEnabled(true);
                et.setSelection(et.getText().length());

                item.setTitle("저장");
                imm.showSoftInput(et,0);
                cancle.setVisible(true);
            }else{
                et.setEnabled(false);
                item.setTitle("수정");
                imm.hideSoftInputFromWindow(et.getWindowToken(),0);
                cancle.setVisible(false);
            }
        } else if(item.getItemId() == R.id.memo_menu_cancle) {
            et.setText(s);
            et.setEnabled(false);
            edit.setTitle("수정");
            imm.hideSoftInputFromWindow(et.getWindowToken(),0);
            cancle.setVisible(false);
        } else if(item.getItemId() == R.id.memo_menu_old) {
            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(memo.this);
            if(!old_memo.equals("")){
                myAlertBuilder.setMessage(old_memo);
            }else{
                myAlertBuilder.setMessage("Empty!");
            }
            myAlertBuilder.show();
        }
        return false;
    }

    @Override
    public void onStop() {
        pass_data.set_old_memo(et.getText().toString());
        pass_data.set_memo(et.getText().toString());

        super.onStop();
    }
}

