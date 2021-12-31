package com.fm.egecuzdan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.db.models.GiderModel;
import com.fm.egecuzdan.utils.AppConstants;

import java.util.ArrayList;

public class GiderListesiAdapter extends BaseAdapter {

    private ArrayList<GiderModel> giderModelleri;
    private Context context;
    private LayoutInflater layoutInflater;

    public GiderListesiAdapter(Context context, ArrayList<GiderModel> giderModelleri) {
        this.context = context;
        this.giderModelleri = giderModelleri;
    }

    public void updateGiderListesi(ArrayList<GiderModel> giderModelleri) {
        this.giderModelleri = giderModelleri;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return giderModelleri.size();
    }

    @Override
    public Object getItem(int position) {
        return giderModelleri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return giderModelleri.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;

        if (convertView == null) {
            viewContainer = new ViewContainer();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_list_item_gider, parent, false);

            viewContainer.tv_miktar = (TextView) convertView.findViewById(R.id.tv_miktar);
            viewContainer.tv_açıklama = (TextView) convertView.findViewById(R.id.tv_açıklama);
            viewContainer.tv_düzenliÖdeme = (TextView) convertView.findViewById(R.id.tv_düzenli);
            viewContainer.tv_tarih = (TextView) convertView.findViewById(R.id.tv_tarih);

            convertView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) convertView.getTag();
        }

        viewContainer.tv_düzenliÖdeme.setText(düzenliÖdemedir(giderModelleri.get(position).düzenliÖdemedir()));
        viewContainer.tv_miktar.setText(AppConstants.PARA_BİRİMİ + giderModelleri.get(position).getMiktar());
        viewContainer.tv_açıklama.setText(giderModelleri.get(position).getAçıklama());
        viewContainer.tv_tarih.setText(giderModelleri.get(position).getTarih());

        return convertView;
    }

    private String düzenliÖdemedir(boolean b) {
        if (b)
            return "Düzenli";
        else
            return "Düzensiz";
    }

    private class ViewContainer {
        private TextView tv_miktar;
        private TextView tv_açıklama;
        private TextView tv_düzenliÖdeme;
        private TextView tv_tarih;
    }
}