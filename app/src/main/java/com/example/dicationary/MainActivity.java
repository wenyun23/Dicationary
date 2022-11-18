package com.example.dicationary;

import static com.example.dicationary.R.drawable.snow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dicationary.Tool.ImgTool;
import com.example.dicationary.Tool.Note;
import com.example.dicationary.databinding.ActivityMainBinding;

import org.json.JSONException;

import java.util.ArrayList;

//便签主页
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final ArrayList<Note> list=new ArrayList<>();
    private SQLiteDatabase db;

    private Bundle bundle;
    private Intent intent;

    private String txtMessage="每日一记，都是来自对小云便签的信任";
    private boolean flag=false;
    private long exitTime=0;

    @SuppressLint("StaticFieldLeak")
    public WordAdapter adpter1,adpter2;
    private static final String is_Nomor="tu1";
    private static final String is_Card="tu2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //绑定viewBinding,单向数据绑定
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //实例化一下参数
        initView();

        //将数据库的数据添加到list中
        initDiary();

        //默认加载第一个视图
        binding.listview.setAdapter(adpter1);

        //设置listview无边框
        binding.listview.setDivider(null);

        //初始需要设置一下背景图片
        binding.imgBack.setBackgroundResource(snow);

        //下拉刷新背景图片
        binding.swiperefresh.setOnRefreshListener(this::LoadImg);

        //点击获取一言获取
        binding.imgclick.setOnClickListener(view -> {
            if (flag){
                binding.txtOneSpeak.setText(getYiYan());
                flag=false;
            }
            else {
                binding.txtOneSpeak.setText(txtMessage);
                flag=true;
            }
        });

        //单击添加便签
        binding.floatBut.setOnClickListener(view -> {
            bundle.putInt("isnew",0);
            intent.putExtras(bundle);
            //startActivityForResult这里用这个，而不用startActivity，是为了更好的处理back.
            startActivityForResult(intent,274);
        });

        //单击更新便签
        binding.listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Note note=list.get(i);

            int id=note.getId();
            String word =note.getWord();

            bundle.putInt("isnew",1);
            bundle.putInt("id",id);
            bundle.putString("word",word);
            intent.putExtras(bundle);
            startActivityForResult(intent,275);
        });

        //长按删除便签
        binding.listview.setOnItemLongClickListener((adapterView, view, i, l) -> {

            AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("亲，确定要删除这则标签");
            dialog.setCancelable(false);

            dialog.setPositiveButton("确定",(dialogInterface, i1) -> {
                //长按listview时，同时根据i(也就是点击的是列表的哪一行)来找到这一列在数据库中的id
                Note note=list.get(i);
                int id=note.getId();
                //删除i列表
                list.remove(i);

                //同步数据库里面也要删除
                db.delete("Notes","id=?",new String[]{(id)+""});
                adpter1.notifyDataSetChanged();
                adpter2.notifyDataSetChanged();

                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
            });
            dialog.setNegativeButton("取消",(dialogInterface, i1) -> Toast.makeText(this,"取消成功",Toast.LENGTH_SHORT).show());

            dialog.create().show();
            return true;
        });

    }

    //对标题栏搜索框功能的实现
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        SearchView searchView= (SearchView) menu.findItem(R.id.app_serach).getActionView();

        //设置输入框的长度
        searchView.setMaxWidth(700);

        //设置是否显示搜索框展开时的提交按钮
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("请输入关键字");

        //设置监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("Recycle")
            @Override
            public boolean onQueryTextChange(String s) {
                //获取搜索框里面的内容
                String key=s.trim();

                //利用Filter过滤数据
                ListAdapter listAdapter=binding.listview.getAdapter();
                if (listAdapter instanceof Filterable){
                    Filter filter=((Filterable) listAdapter).getFilter();
                    if (key.length() == 0){
                        filter.filter(null);
                    }else {
                        filter.filter(key);
                    }

                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
        });
        return true;
    }

    //对菜单的个功能的实现
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_change:{//切换
                //SharedPreferences(数据存储),可以保存少量数据,这样可以使切换视图后还是上一次的数据呈现
                //Context.MODE_PRIVETE:指定该SharedPreferences数据只能被本应用程序读写
                SharedPreferences shp= getSharedPreferences(is_Nomor, Context.MODE_PRIVATE);
                boolean ViewType=shp.getBoolean(is_Card,false);

                //SharedPreferences没有写入的能力,但SharedPreferences.Editor可以向SharedPreferences写数据
                SharedPreferences.Editor editor=shp.edit();

                if ((ViewType)){
                    binding.listview.setAdapter(adpter1);
                    editor.putBoolean(is_Card,false);
                } else {
                    binding.listview.setAdapter(adpter2);
                    editor.putBoolean(is_Card,true);
                }
                editor.apply();
            }break;

            case R.id.id_order:{//换图
                LoadImg();
            }break;

            case R.id.id_clear:{//清空
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("亲，你确定要删除，它将不能恢复");
                builder.setCancelable(false);

                builder.setPositiveButton("确定", (dialogInterface, i) -> {
                    list.clear();
                    db.delete("Notes","",new String[]{});
                    adpter1.notifyDataSetChanged();
                    adpter2.notifyDataSetChanged();
                });

                builder.setNegativeButton("取消", (dialogInterface, i) -> Toast.makeText(MainActivity.this,"取消成功",Toast.LENGTH_SHORT).show());
                builder.create().show();
            }break;
        }
        return true;
    }

    //处理回调，刷新视图
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==274){
            if (resultCode==RESULT_OK){
                list.clear();
                initDiary();
                adpter1.notifyDataSetChanged();
                adpter2.notifyDataSetChanged();
            }
        }else if (requestCode==275){
            if (resultCode==RESULT_OK){
                list.clear();
                initDiary();
                adpter1.notifyDataSetChanged();
                adpter2.notifyDataSetChanged();
            }
        }
    }

    //利用volley加载"一言"
    private String getYiYan(){
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest request=new JsonObjectRequest(
                "http://v1.hitokoto.cn",
                null,
                response -> {
                    try {
                        txtMessage=response.getString("hitokoto");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("TAG", "doInBackground:asas "+error.getMessage())
        );
        queue.add(request);

        return txtMessage;
    }

    //利用Glide加载背景图片
    private void LoadImg(){
        String url= new ImgTool().ImgArray();

        //利用Glide加载图片
        Glide.with(this)
                .load(url)
                .placeholder(snow)//没加载出图片前的占位符
                //加载成功
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        if (binding.swiperefresh.isRefreshing()){//如果在刷新，就让刷新停止
                            binding.swiperefresh.setRefreshing(false);
                        }
                        return false;
                    }
                    //加载失败
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        if (binding.swiperefresh.isRefreshing()){//如上
                            binding.swiperefresh.setRefreshing(false);
                        }
                        return false;
                    }

                }).into(binding.imgBack);//将其放进加载出图片前预留的位置
    }

    //初始化数据库和适配器
    private void initView() {
        db=new DataBase(MainActivity.this).getWritableDatabase();
        bundle=new Bundle();
        intent=new Intent(MainActivity.this,EditActivity.class);

        adpter1=new WordAdapter(list, MainActivity.this,true);
        adpter2=new WordAdapter(list, MainActivity.this,false);
    }

    //利用 游标cursor 取数据库里面拿数据，并放到Notes这个类和list集合中
    private void initDiary(){
        Cursor cursor=db.query("Notes",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range")
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String word=cursor.getString(cursor.getColumnIndex("word"));
                @SuppressLint("Range")
                String date = cursor.getString(cursor.getColumnIndex("date"));

                Note note=new Note(id,word,date);

                list.add(note);

            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    //点击两次退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}