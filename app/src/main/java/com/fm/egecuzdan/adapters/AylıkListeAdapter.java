package com.fm.egecuzdan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.db.GiderDB;
import com.fm.egecuzdan.db.models.SheetModel;
import com.fm.egecuzdan.utils.AppConstants;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AylıkListeAdapter extends BaseAdapter {

    private ArrayList<SheetModel> sheetData;
    private Context context;
    private LayoutInflater layoutInflater;

    public AylıkListeAdapter(Context context, ArrayList<SheetModel> sheetData) {
        this.context = context;
        this.sheetData = sheetData;
    }

    public void updateSheetList(ArrayList<SheetModel> sheetData) {
        this.sheetData = sheetData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return sheetData.size();
    }

    @Override
    public Object getItem(int position) {
        return sheetData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return sheetData.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;

        if (convertView == null) {
            viewContainer = new ViewContainer();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_list_item_tablo, parent, false);

            viewContainer.tv_gelir = (TextView) convertView.findViewById(R.id.tv_gelir);
            viewContainer.tv_ay = (TextView) convertView.findViewById(R.id.tv_ay);
            viewContainer.tv_yıl = (TextView) convertView.findViewById(R.id.tv_yıl);
            viewContainer.tv_artan = (TextView) convertView.findViewById(R.id.tv_artan);

            convertView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) convertView.getTag();
        }

        viewContainer.tv_gelir.setText("Gelir: " + AppConstants.PARA_BİRİMİ + sheetData.get(position).getGelir());
        viewContainer.tv_ay.setText(sheetData.get(position).getAy());
        viewContainer.tv_yıl.setText(sheetData.get(position).getYıl());
        // toplam artan hesaplanıyor
        double toplam_gider = new GiderDB(context).getAylıkGider(sheetData.get(position).getId());
        double gelir = sheetData.get(position).getGelir();
        double artan = gelir - toplam_gider;
        DecimalFormat roundFormat = new DecimalFormat("#.##");
        if (artan > 0) {
            viewContainer.tv_artan.setText("Artan: " + AppConstants.PARA_BİRİMİ + roundFormat.format(artan));
            viewContainer.tv_artan.setTextColor(context.getResources().getColor(R.color.colorSurplus));
        } else {
            viewContainer.tv_artan.setText("Eksik: " + AppConstants.PARA_BİRİMİ + roundFormat.format(artan));
            viewContainer.tv_artan.setTextColor(context.getResources().getColor(R.color.colorDeficit));
        }

        return convertView;
    }

    private static class ViewContainer {
        public TextView tv_artan;
        private TextView tv_ay;
        private TextView tv_yıl;
        private TextView tv_gelir;
    }
}