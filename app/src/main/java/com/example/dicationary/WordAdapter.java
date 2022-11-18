package com.example.dicationary;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.dicationary.Tool.Note;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends BaseAdapter implements Filterable {
    private List<Note> mData;
    private final Context mContext;
    private final boolean use;

    private ArrayList<Note> mOriginalValues = null;
    private MyFilter mFilter;
    private final Object mLock = new Object();


    public WordAdapter(List<Note> mData, Context mContext,boolean use) {
        this.mData = mData;
        this.mContext = mContext;
        this.use=use;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v;
        if (use) {
            v= LayoutInflater.from(mContext).inflate(R.layout.celllayout,null);
        }else {
            v= LayoutInflater.from(mContext).inflate(R.layout.cardlayout,null);
        }

        TextView txt_time=v.findViewById(R.id.txt_time);
        TextView txt_edit_content=v.findViewById(R.id.txt_edit_content);

        Note note= (Note) getItem(position);

        txt_edit_content.setText(note.getWord());
        txt_time.setText(note.getDate());

        return v;
    }

    //实现Filterable接口，重写getFilter方法
    @Override
    public Filter getFilter() {
        if(mFilter==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    class MyFilter extends Filter{
        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();

            // 原始数据备份为空时，上锁，同步复制原始数据
            if(mOriginalValues==null){
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mData);
                }
            }
            
            // 当首字母为空时
            if(prefix==null || prefix.length()==0){

                List<Note> list1;
                // 同步复制一个原始备份数据
                synchronized (mLock) {
                     list1= new ArrayList<>(mOriginalValues);
                }

                // 此时返回的results就是原始的数据，不进行过滤
                results.values = list1;
                results.count = list1.size();
            }else{

                String prefixString = prefix.toString().toLowerCase();

                List<Note> values;
                // 同步复制一个原始备份数据
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }
                int count = values.size();

                List<Note> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    // 从List<Note>中拿到TaskModel对象
                    final Note value = values.get(i);

                    // Note对象的任务名称属性作为过滤的参数
                    final String valueText =value.getWord(); //value.toString().toLowerCase();

                    // 关键字是否和item的过滤参数匹配
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        // 处理首字符是空格
                        final String[] words = valueText.split(" ");

                        for (String a : words) {
                            // 一旦找到匹配的就break，跳出for循环
                            if (a.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                // 此时的results就是过滤后的List<TaskModel>数组
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // 此时，Adapter数据源就是过滤后的Results
            mData = (List<Note>) results.values;
            // 这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
            if(results.count>0){
                notifyDataSetChanged();
                Log.d(TAG, "publishResults1: ");
            }else{
                // 当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
                notifyDataSetInvalidated();
                Log.d(TAG, "publishResults2: ");
            }
        }
    }


}
