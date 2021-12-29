package com.fm.egecuzdan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.db.models.ExpenseModel;
import com.fm.egecuzdan.utils.AppConstants;

import java.util.ArrayList;

public class ExpenseListAdapter extends BaseAdapter {

    private ArrayList<ExpenseModel> expenseModels;
    private Context context;
    private LayoutInflater layoutInflater;

    public ExpenseListAdapter(Context context, ArrayList<ExpenseModel> expenseModels) {
        this.context = context;
        this.expenseModels = expenseModels;
    }

    public void updateExpenseList(ArrayList<ExpenseModel> expenseModels) {
        this.expenseModels = expenseModels;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return expenseModels.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return expenseModels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer viewContainer;

        if (convertView == null) {
            viewContainer = new ViewContainer();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_list_item_gider, parent, false);

            viewContainer.tv_amount = (TextView) convertView.findViewById(R.id.tv_miktar);
            viewContainer.tv_description = (TextView) convertView.findViewById(R.id.tv_açıklama);
            viewContainer.tv_regular = (TextView) convertView.findViewById(R.id.tv_düzenli);
            viewContainer.tv_date = (TextView) convertView.findViewById(R.id.tv_tarih);

            convertView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) convertView.getTag();
        }

        viewContainer.tv_regular.setText(isRegular(expenseModels.get(position).isRegular()));
        viewContainer.tv_amount.setText(AppConstants.CURRENCY + expenseModels.get(position).getAmount());
        viewContainer.tv_description.setText(expenseModels.get(position).getRemarks());
        viewContainer.tv_date.setText(expenseModels.get(position).getDate());

        return convertView;
    }

    private String isRegular(boolean b) {
        if (b)
            return "Regular";
        else
            return "Non-Regular";
    }

    private class ViewContainer {
        private TextView tv_amount;
        private TextView tv_description;
        private TextView tv_regular;
        private TextView tv_date;
    }
}