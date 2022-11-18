package com.example.dicationary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dicationary.databinding.ActivityEditBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private ActivityEditBinding binding;
    SQLiteDatabase db;

    ContentValues values;
    Bundle bundle;
    String word;
    int isNewWord,id;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        db=new DataBase(EditActivity.this).getWritableDatabase();

        values=new ContentValues();

        Intent intent=getIntent();
        bundle=intent.getExtras();

        //isNewWord=1表示修改数据，isNewWord=0表示添加新数据
        isNewWord=bundle.getInt("isnew");

        if (isNewWord==1){
            id=bundle.getInt("id");
            word=bundle.getString("word");
            binding.editContext.setText(word);
        }

        //添加和修改数据
        binding.imgSave.setOnClickListener(view -> {
            Intent intent1=new Intent();
            if (isNewWord==0){
                //添加新数据
                values.put("word",binding.editContext.getText().toString());
                values.put("date",dateToStr());

                db.insert("Notes",null,values);

                Toast.makeText(EditActivity.this,"便签添加成功,单击修改，长按删除",Toast.LENGTH_SHORT).show();

            }else if (isNewWord==1){
                //反之
                values.put("word",binding.editContext.getText().toString());
                values.put("date",dateToStr());

                db.update("Notes",values,"id=?",new String[]{id+""});

                Toast.makeText(EditActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK,intent1);

            finish();
        });

        //统计输入了多少字
        binding.imgCounter.setOnClickListener(view -> {
            int counter=binding.editContext.getText().length();
            binding.textLiterary.setText("累计输入："+counter+" 字");
        });

        //清空文字框
        binding.imgRubbish.setOnClickListener(view -> binding.editContext.setText(""));

    }

    //对返回键做处理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是中间的"Home"键，程序退出
        if (keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }else if (keyCode==KeyEvent.KEYCODE_BACK){//如果是返回键
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //格式化时间
    public String dateToStr(){
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        return simpleDateFormat.format(date);
    }
}