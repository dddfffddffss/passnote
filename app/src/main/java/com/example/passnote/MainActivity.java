package com.example.passnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passnote.adapter.contents_adapter;
import com.example.passnote.adapter.main_tag_adapter;
import com.example.passnote.util.pass_data;
import com.example.passnote.util.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView contents;
    com.example.passnote.adapter.contents_adapter contents_adapter;

    RecyclerView tag;
    main_tag_adapter mta;

    EditText search;

    pass_data pass_data;

    String stop;
    int pass_stop, tag_stop;

    private ActivityResultLauncher<String> get_csv_launcher;
    private ActivityResultLauncher<String> restore_launcher;
    private ActivityResultLauncher<Intent> backup_launcher;
    util u;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            main_activity
                1. ????????? ??? ???????????????????????? ???????????? ?????????.
                2. object??? 3???: search???, tag???, content???
                    search: ???????????? ?????? ???????????? ????????? ????????? ??????.
                    tag: ????????? tag??? ????????? ?????? ????????? ?????????.
                    content: ??????????????? ??????. ????????? edit_pass??? ????????????. -> to_edit_pass
                3. ???????????? ??????, ??????????????? ??????.
            edit_pass
                1. 2?????? ???????????? ??? ??????. pass??? ????????? ????????? ??????
        */

        // ???????????? ?????? Lock_activuty??? ???????????????.
        Intent lock = new Intent(getApplicationContext(), LockActivity.class);
        startActivity(lock.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        u = new util();
        u.verifyStoragePermissions(this);

        // 1. pass_data??? ??????????????? ?????? ?????????. -> ?????? ????????? ?????? pass_data??? ????????????.
        pass_data = new pass_data(this);

        // 2. ????????? ???????????????
        contents = findViewById(R.id.content);
        contents.setLayoutManager(new LinearLayoutManager(this));

        // 2-1. contents_adapter??? edit_pass??? ???????????? ????????? ????????? ??????. -> Intent??? ?????????.
        Intent intent = new Intent(getApplicationContext(), edit_pass.class);
        contents_adapter = new contents_adapter(getApplicationContext(), intent, pass_data);
        contents.setAdapter(contents_adapter);

        // 3. ?????? ??????????????? dsfdfsfsdsfsd
        tag = findViewById(R.id.tag);
        tag.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // 3-1.main_tag_adapter??? contents_adapter??? ???????????? ????????? ????????? ????????? ??????. -> contents_adapter??? ?????????.
        mta = new main_tag_adapter(this, contents_adapter, pass_data);
        tag.setAdapter(mta);

        // 4. name search: contents_adapter??? ???????????? ????????? ????????? ??????.
        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mta.update_all_list();
                mta.notifyDataSetChanged();
                tag.setAdapter(mta);

                contents_adapter.set_search_name(s.toString());
                contents.setAdapter(contents_adapter);
            }
        });

        // 4. reset: name search??? ?????? ??????
        Button reset = findViewById(R.id.button);
        reset.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            search.setText("");
        });

        mta.set_search_reset_button(search, reset);

        // ????????? ????????? ?????? = restore, get_csv
        get_csv_launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    String[] ss = null;
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        String s;
                        int count=0;
                        HashSet<String> nameset = pass_data.get_name_list();
                        while((s=br.readLine())!=null){
                            ss = s.split(",");
                            if(!nameset.contains(ss[0])){
                                pass_data.content_add(ss[0],ss[1],ss[2]);
                                count++;
                            }
                        }

                        Toast.makeText(getApplicationContext(),count+"??? ?????????",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("csv","csv",e);
                        if(ss==null){
                            Toast.makeText(getApplicationContext(),"???????????? ??????!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),ss[0]+"?????? ???????????? ??????!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        restore_launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    FileChannel src = null,dst = null;
                    try {
                        File currentDB = getApplicationContext().getDatabasePath("passnote");
                        InputStream inputStream = getContentResolver().openInputStream(uri);

                        src = new FileOutputStream(currentDB).getChannel();
                        dst = ((FileInputStream)inputStream).getChannel();
                        src.transferFrom(dst, 0, dst.size());

                        Toast.makeText(getApplicationContext(), "?????? ??????!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "?????? ??????!", Toast.LENGTH_SHORT).show();
                        Log.e("csv","csv",e);
                    } finally {
                        try {
                            if(src != null)src.close();
                            if(dst != null)dst.close();
                        } catch (IOException e) {
                            Log.e("csv","close",e);
                        }
                    }
                });

        backup_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    FileChannel dst = null,src = null;
                    Uri uri = null;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try{
                            Intent intent1 = result.getData();
                            if(intent1!=null)uri = intent1.getData();
                            OutputStream outputStream = getContentResolver().openOutputStream(uri);

                            dst = ((FileOutputStream)outputStream).getChannel();
                            src = new FileInputStream(getDatabasePath("passnote")).getChannel();

                            dst.transferFrom(src, 0, src.size());

                            src.close();
                            dst.close();
                            Toast.makeText(getApplicationContext(), "?????? ??????!", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "?????? ??????!", Toast.LENGTH_SHORT).show();
                            Log.e("backup","",e);
                        }finally {
                            try {
                                if(src!=null)src.close();
                                if(dst!=null)dst.close();
                            } catch (IOException e) {
                                Log.e("backup","",e);
                            }
                        }
                    }
                });
    }

    protected void onPause() {
        super.onPause();

        pass_stop = contents_adapter.get_stop_position();
        tag_stop = mta.getStop_postion();

        stop = search.getText().toString();
    }

    @Override
    protected void onResume() {
        contents_adapter.update_all_list();
        contents_adapter.notifyDataSetChanged();
        contents.setAdapter(contents_adapter);
        contents.scrollToPosition(pass_stop);

        mta.update_all_list();
        mta.notifyDataSetChanged();
        tag.setAdapter(mta);
        tag.scrollToPosition(tag_stop);

        search.setText(stop);

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(getApplicationContext(), edit_pass.class);
            intent.putExtra("content_id", -100);
            startActivity(intent);
        } else if(item.getItemId() == R.id.menu_tag_add) {
            Intent intent = new Intent(getApplicationContext(), edit_tag.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.menu_memo) {
            Intent intent = new Intent(getApplicationContext(), memo.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.menu_tag_status) {
            //???????????? ???

        // ?????? ??????
        } else if(item.getItemId() == R.id.backup) {
            String getTime1 = new SimpleDateFormat("yyMMdd_hh_mm_ss", Locale.KOREA).format(System.currentTimeMillis());

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/*");
            intent.putExtra(Intent.EXTRA_TITLE, getTime1+".db");
            backup_launcher.launch(intent);

        // restore ??????
        } else if(item.getItemId() == R.id.restore) {
            restore_launcher.launch("*/*");

            // get_csv ??????
        } else if(item.getItemId() == R.id.get_csv) {
            get_csv_launcher.launch("text/*");

        } else if(item.getItemId() == R.id.edit_apppass) {
            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
            EditText e = new EditText(this);
            e.setText(pass_data.get_password());
            myAlertBuilder.setTitle("???????????? ?????????");
            myAlertBuilder.setView(e);
            myAlertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pass_data.set_password(e.getText().toString());
                }
            });
            myAlertBuilder.setNegativeButton("??????", null);
            myAlertBuilder.show();
        }
        return false;
    }
}