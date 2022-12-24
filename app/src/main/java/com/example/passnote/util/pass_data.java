package com.example.passnote.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.passnote.memo;

import java.util.HashMap;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

public class pass_data {
    SQLiteDatabase sdb;
    ContentValues cv;

    int pass_pointer, tag_pointer;
    String password;

    public pass_data(Context c){
        sdb = c.openOrCreateDatabase("passnote",MODE_PRIVATE,null);
        cv = new ContentValues();
        /*
        sdb.execSQL("drop table pass_table");
        sdb.execSQL("drop table tag_table");
         */

        sdb.execSQL("create table IF NOT EXISTS pass_table (id int, name text, login_id text, login_pass text)");
        sdb.execSQL("create table IF NOT EXISTS tag_table (id int, tag text, contents_id int)");

        // pass_pointer 없으면, name에 0 저장.
        Cursor cs = sdb.query("pass_table",new String[]{"id","name"},"id = -1",null,null,null,null);
        if(!cs.moveToNext()){
            // id=-1 -> name = pass_pointer, login_id = pass
            // id=-2 -> login_id = old_memo, login_pass = memo
            sdb.execSQL(" insert into pass_table(id, name, login_id) values (-1, '0', 'pass')");
            sdb.execSQL(" insert into pass_table(id, name, login_id) values (-2, '0')");
        } else {
            pass_pointer = Integer.parseInt(cs.getString(1));
            try{
                password = (cs.getString(2));
            }catch (Exception e){
                password = "pass";
            }
        }

        // tag_pointer 없으면, name에 0 저장.
        cs = sdb.query("tag_table",new String[]{"id","tag"},"id = -1",null,null,null,null);
        if(!cs.moveToNext()){
            sdb.execSQL(" insert into tag_table (id, tag) values (-1, '0')");
        } else {
            tag_pointer = Integer.parseInt(cs.getString(1));
        }
        cs.close();
    }

    public HashMap<Integer, String[]> get_all_list(){
        HashMap<Integer, String[]> result = new HashMap<>();

        Cursor cs = sdb.query("pass_table",new String[]{"*"},"id > -1",null,null,null,null);
        while (cs.moveToNext()) {
            String[] ss = new String[]{
                    cs.getString(1),
                    cs.getString(2),
                    cs.getString(3) };
            result.put(cs.getInt(0), ss);
        }
        cs.close();

        return result;
    }

    public HashMap<Integer, String> get_tag_list(){
        HashMap<Integer, String> result = new HashMap<>();

        Cursor cs = sdb.query("tag_table",new String[]{"*"},"id > -1 and contents_id = -1",null,null,null,null);
        while (cs.moveToNext()) {
            result.put(cs.getInt(0), cs.getString(1));
        }
        cs.close();

        return result;
    }

    public void set_password(String password){
        cv.clear();
        cv.put("login_id",password);
        sdb.update("pass_table",cv,"id = -1",null);
    }

    public String get_password(){
        return password;
    }

    public HashSet<String> get_name_list(){
        HashSet<String> result = new HashSet<>();

        Cursor cs = sdb.query("pass_table",new String[]{"name"},"id > -1",null,null,null,null);
        while (cs.moveToNext()) {
            result.add(cs.getString(0));
        }
        cs.close();

        return result;
    }

    public HashMap<Integer, HashSet<String>> get_contents_tag_list(){
        HashMap<Integer, HashSet<String>> result = new HashMap<>();

        Cursor cs = sdb.query("tag_table", new String[]{"contents_id", "tag"},"id > -1 and contents_id>-1",null,null,null,null);
        while (cs.moveToNext()) {
            int contents_id = cs.getInt(0);
            if(!result.containsKey(contents_id))result.put(contents_id, new HashSet<>());
            result.get(contents_id).add(cs.getString(1));
        }
        cs.close();

        return result;
    }

    public HashSet<String> get_contents_tag_list(int contents_id){
        HashSet<String> result = new HashSet<>();

        Cursor cs = sdb.query("tag_table", new String[]{"tag"},"id > -1 and contents_id = "+contents_id,null,null,null,null);
        while (cs.moveToNext())result.add(cs.getString(0));
        cs.close();

        return result;
    }

    public int content_add(String name, String login_id, String login_pass){
        cv.clear();
        cv.put("id",pass_pointer);
        cv.put("name",name);
        cv.put("login_id",login_id);
        cv.put("login_pass",login_pass);
        sdb.insert("pass_table","id",cv);
        pass_pointer++;

        cv.clear();
        cv.put("name",String.valueOf(pass_pointer));
        sdb.update("pass_table",cv,"id = -1",null);

        return pass_pointer-1;
    }

    public void content_update(int id, String name, String login_id, String login_pass){
        cv.clear();
        cv.put("name",name);
        cv.put("login_id",login_id);
        cv.put("login_pass",login_pass);
        sdb.update("pass_table",cv,"id = "+id,null);
    }

    public void tag_add(String tag){
        cv.clear();
        cv.put("id",tag_pointer);
        cv.put("tag",tag);
        cv.put("contents_id",-1);
        sdb.insert("tag_table",null,cv);
        tag_pointer++;

        cv.clear();
        cv.put("tag",String.valueOf(tag_pointer));
        sdb.update("tag_table",cv,"id = -1",null);
    }

    public void tag_add(HashSet<String> tag, int contents_id){
        try{
            sdb.beginTransaction();
            Cursor cs = sdb.query("tag_table",new String[]{"tag","id"},"contents_id = "+contents_id,null,null,null,null);
            while(cs.moveToNext()){
                if(!tag.contains(cs.getString(0))){
                    sdb.delete("tag_table","id = "+cs.getInt(1),null);
                } else {
                    tag.remove(cs.getString(0));
                }
            }
            cs.close();

            cv.clear();
            for(String tag_name:tag){
                cv.put("id",tag_pointer);
                cv.put("tag",tag_name);
                cv.put("contents_id",contents_id);
                sdb.insert("tag_table",null,cv);

                tag_pointer++;
                cv.clear();
            }

            cv.put("tag",String.valueOf(tag_pointer));
            sdb.update("tag_table",cv,"id = -1",null);

            sdb.setTransactionSuccessful();
        }catch (Exception e){
            Log.e("transaction_err","tag_add",e);
        }finally {
            sdb.endTransaction();
        }
    }

    public void tag_update(String current, String name){
        cv.clear();
        cv.put("tag",name);
        sdb.update("tag_table",cv,"tag = '"+current+"'",null);
    }

    public void content_delete(int id){
        sdb.delete("pass_table","id = "+id,null);
    }

    public void tag_delete(String name){
        sdb.delete("tag_table","tag = '"+name+"'",null);
    }

    public void set_old_memo(String memo){
        cv.clear();
        cv.put("login_pass",memo);
        sdb.update("pass_table",cv,"id = -2",null);
    }

    public String get_old_memo(){
        Cursor cs = sdb.query("pass_table",new String[]{"login_pass"},"id = -2",null,null,null,null);

        String memo;
        if(cs.moveToNext()){
            memo = cs.getString(0);
        }else{
            memo = null;
        }
        cs.close();

        return memo!=null?memo:"";
    }

    public void set_memo(String memo){
        cv.clear();
        cv.put("login_id",memo);
        sdb.update("pass_table",cv,"id = -2",null);
    }

    public String get_memo(){
        Cursor cs = sdb.query("pass_table",new String[]{"login_id"},"id = -2",null,null,null,null);

        String memo;
        if(cs.moveToNext()){
            memo = cs.getString(0);
        }else{
            memo = null;
        }
        cs.close();

        return memo!=null?memo:"";
    }
}
