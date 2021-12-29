package com.fm.expensecalculator.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fm.expensecalculator.R;
import com.fm.expensecalculator.db.ExpenseDB;
import com.fm.expensecalculator.db.models.SheetModel;
import com.fm.expensecalculator.utils.AppConstants;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MonthlyListAdapter extends BaseAdapter {

    private ArrayList<SheetModel> sheetData;
    private Context context;
    private LayoutInflater layoutInflater;

    public MonthlyListAdapter(Context context, ArrayList<SheetModel> sheetData) {
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
            convertView = layoutInflater.inflate(R.layout.row_list_item_sheet, parent, false);

            viewContainer.tv_income = (TextView) convertView.findViewById(R.id.tv_income);
            viewContainer.tv_month = (TextView) convertView.findViewById(R.id.tv_month);
            viewContainer.tv_year = (TextView) convertView.findViewById(R.id.tv_year);
            viewContainer.tv_surplus = (TextView) convertView.findViewById(R.id.tv_surplus);

            convertView.setTag(viewContainer);
        } else {
            viewContainer = (ViewContainer) convertView.getTag();
        }

        viewContainer.tv_income.setText("Income: " + AppConstants.CURRENCY + sheetData.get(position).getIncome());
        viewContainer.tv_month.setText(sheetData.get(position).getMonth());
        viewContainer.tv_year.setText(sheetData.get(position).getYear());
        //calculate total surplus
        double total_expense = new ExpenseDB(context).getTotalExpenseMonthly(sheetData.get(position).getId());
        double income = sheetData.get(position).getIncome();
        double surplus = income - total_expense;
        DecimalFormat roundFormat = new DecimalFormat("#.##");
        if (surplus > 0) {
            viewContainer.tv_surplus.setText("Surplus: " + AppConstants.CURRENCY + roundFormat.format(surplus));
            viewContainer.tv_surplus.setTextColor(context.getResources().getColor(R.color.colorSurplus));
        } else {
            viewContainer.tv_surplus.setText("Deficit: " + AppConstants.CURRENCY + roundFormat.format(surplus));
            viewContainer.tv_surplus.setTextColor(context.getResources().getColor(R.color.colorDeficit));
        }

        return convertView;
    }

    private class ViewContainer {
        public TextView tv_surplus;
        private TextView tv_month;
        private TextView tv_year;
        private TextView tv_income;
    }
}