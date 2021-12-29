package com.fm.expensecalculator.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fm.expensecalculator.R;
import com.fm.expensecalculator.db.ExpenseDB;
import com.fm.expensecalculator.db.models.ExpenseModel;
import com.fm.expensecalculator.utils.AppConstants;
import com.fm.expensecalculator.view.AddExpenseActivity;
import com.fm.expensecalculator.view.MonthDetailActivity;

import java.util.ArrayList;

import static com.fm.expensecalculator.utils.AppConstants.INTENT_MODE;
import static com.fm.expensecalculator.utils.AppConstants.INTENT_MODE_EDIT;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_MONTH;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_MONTH_ID;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_YEAR;

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
            convertView = layoutInflater.inflate(R.layout.row_list_item_expense, parent, false);

            viewContainer.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
            viewContainer.tv_description = (TextView) convertView.findViewById(R.id.tv_description);
            viewContainer.tv_regular = (TextView) convertView.findViewById(R.id.tv_regular);
            viewContainer.tv_date = (TextView) convertView.findViewById(R.id.tv_date);

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