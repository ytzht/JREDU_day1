package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dao.ClassProj;
import com.jredu_day1.R;
import com.util.ImageLoaderUtil;
import com.util.UrlUtil;

import java.util.List;

/**
 * Created by ThinkPad on 2015/11/18.p
 */
public class ClassProjAdapter extends BaseAdapter {

    private Context context;
    private List<ClassProj> myData;

    public ClassProjAdapter(Context context, List<ClassProj> myData) {
        this.context = context;
        this.myData = myData;
    }
    @Override
    public int getCount() {
        return myData.size();
    }

    @Override
    public Object getItem(int position) {
        return myData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHold vh ;


        if(convertView == null) {
            vh = new ViewHold();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_classproj, null);
            vh.class_image = (ImageView)convertView.findViewById(R.id.image_classproj);
            vh.class_title = (TextView)convertView.findViewById(R.id.text_classproj);
            vh.item_click = (ImageView)convertView.findViewById(R.id.item_click);
            convertView.setTag(vh);
        }else{
            vh = (ViewHold)convertView.getTag();
        }
        ClassProj classProj = myData.get(position);
        ImageLoaderUtil.display(UrlUtil.BASE_URL + classProj.getPhotoUri(), vh.class_image);
        vh.item_click.setImageResource(R.drawable.person_job_line);
        vh.class_title.setText(classProj.getRemark() + " (" + classProj.getVedioCt() + ")");
//        vh.class_image.setImageResource(R.drawable.banner_02);


        return convertView;
    }

    public class ViewHold {
        public ImageView class_image;
        public TextView class_title;
        public ImageView item_click;
    }
}
