package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dao.Vedio;
import com.jredu_day1.R;
import com.util.ImageLoaderUtil;
import com.util.UrlUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2015/11/17.pm
 */
public class VedioAdapter extends BaseAdapter{
    Date sdf_date;
    private Context context;
    private List<Vedio> myData;

    public VedioAdapter(Context context, List<Vedio> myData) {
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
        ViewHoldN vhN ;
        if(convertView == null) {
            vhN = new ViewHoldN();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_vedio_face,null);
            vhN.vedio_image = (ImageView)convertView.findViewById(R.id.news_image);
            vhN.vedio_title = (TextView)convertView.findViewById(R.id.news_title);
            vhN.vedio_pubdate = (TextView)convertView.findViewById(R.id.news_pubdate);
            vhN.vedio_author = (TextView)convertView.findViewById(R.id.news_author);
            vhN.vedio_instruct = (TextView)convertView.findViewById(R.id.news_instruct);
            convertView.setTag(vhN);
        }else{
            vhN = (ViewHoldN)convertView.getTag();
        }
        Vedio vedio = myData.get(position);
        ImageLoaderUtil.display(UrlUtil.BASE_URL + vedio.getVPickUri(), vhN.vedio_image);
        vhN.vedio_title.setText(vedio.getVedioName());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            sdf_date = sdf.parse(vedio.getPubDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        vhN.vedio_pubdate.setText(vedio.getPubDate());
        vhN.vedio_author.setText(vedio.getAuthor());
        vhN.vedio_instruct.setText(vedio.getInstruction());
        return convertView;
    }
    public class ViewHoldN{
        public ImageView vedio_image;
        public TextView vedio_title;
        public TextView  vedio_pubdate;
        public TextView  vedio_author;
        public TextView  vedio_instruct;

    }
}
