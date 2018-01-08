package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;

/**
 * Created by yangpeixing on 17/1/17.
 */
public class ListViewAdapter extends BaseAdapter {
    Context context;
    public ListViewAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if(view==null) {
            holder = new ViewHolder();
            view=LayoutInflater.from(context).inflate(R.layout.item,null);
            holder.tv= (TextView) view.findViewById(R.id.tv);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        holder.tv.setText("第" + i + "个文本");
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,holder.tv.getText(),0).show();
            }
        });
        return view;
    }
}

class ViewHolder{
    TextView tv;
}
