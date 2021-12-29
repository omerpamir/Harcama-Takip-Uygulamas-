package com.fm.expensecalculator.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fm.expensecalculator.R;
import com.fm.expensecalculator.adapters.ExpenseListAdapter;
import com.fm.expensecalculator.db.ExpenseDB;
import com.fm.expensecalculator.db.models.ExpenseModel;
import com.fm.expensecalculator.db.models.SheetModel;
import com.fm.expensecalculator.utils.AppConstants;
import com.fm.expensecalculator.view.custom.CustomPieChart;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.fm.expensecalculator.utils.AppConstants.INTENT_MODE;
import static com.fm.expensecalculator.utils.AppConstants.INTENT_MODE_ADD;
import static com.fm.expensecalculator.utils.AppConstants.INTENT_MODE_EDIT;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_EXPENSE;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_MONTH;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_MONTH_ID;
import static com.fm.expensecalculator.utils.AppConstants.SELECTED_YEAR;

public class MonthDetailActivity extends AppCompatActivity {

    private ListView listView_months;
    private ExpenseListAdapter adapter;
    private TextView tv_income;
    private TextView tv_surplus;
    private TextView tv_month;
    private TextView tv_empty_info;
    private TextView tv_total_regular;
    private TextView tv_total_non_regular;
    private double income = 0;
    private String month_id;
    private String sel_month;
    private String sel_year;
    private RelativeLayout layout_totals;
    private CustomPieChart pieChart;
    private ArrayList<ExpenseModel> expenseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView_months = (ListView) findViewById(R.id.listview_months);
        tv_income = (TextView) findViewById(R.id.tv_income);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_surplus = (TextView) findViewById(R.id.tv_surplus);
        tv_empty_info = (TextView) findViewById(R.id.tv_empty_info);
        tv_total_regular = (TextView) findViewById(R.id.tv_total_regular);
        tv_total_non_regular = (TextView) findViewById(R.id.tv_total_non_regular);
        layout_totals = (RelativeLayout) findViewById(R.id.layout_totals);
        pieChart = (CustomPieChart) findViewById(R.id.pie_chart);

        month_id = getIntent().getStringExtra(SELECTED_MONTH_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddExpenseActivity.class)
                        .putExtra(SELECTED_MONTH_ID, month_id)
                        .putExtra(SELECTED_MONTH, sel_month)
                        .putExtra(SELECTED_YEAR, sel_year)
                        .putExtra(INTENT_MODE, INTENT_MODE_ADD));
            }
        });

        fetchIncomeFromDB();

        tv_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditIncomeDialog();
            }
        });
        listView_months.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog(position, parent.getAdapter().getItemId(position));
            }
        });

        fetchExpenseFromDB();

    }

    private void fetchIncomeFromDB() {
        SheetModel sheetData = new ExpenseDB(getApplicationContext()).getIncomeData(month_id);
        if (sheetData != null) {
            income = sheetData.getIncome();
            sel_month = sheetData.getMonth();
            sel_year = sheetData.getYear();
            tv_month.setText(sheetData.getMonth() + " " + sheetData.getYear());
            tv_income.setText("Income: " + AppConstants.CURRENCY + income);
            setTitle(sheetData.getMonth() + " " + sheetData.getYear());
        }
    }

    private void fetchExpenseFromDB() {
        expenseData = new ExpenseDB(getApplicationContext()).getExpenses(month_id);
        if (expenseData != null) {
            tv_empty_info.setVisibility(View.GONE);
            layout_totals.setVisibility(View.VISIBLE);
            tv_surplus.setVisibility(View.VISIBLE);
            listView_months.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new ExpenseListAdapter(this, expenseData);
                listView_months.setAdapter(adapter);
            } else {
                adapter.updateExpenseList(expenseData);
            }
            double total_amount = 0;
            double total_regular = 0;
            double total_non_regular = 0;
            for (int i = 0; i < expenseData.size(); i++) {
                if (!expenseData.get(i).getAmount().equalsIgnoreCase("")) {
                    total_amount = total_amount + Double.valueOf(expenseData.get(i).getAmount());
                    if (expenseData.get(i).isRegular())
                        total_regular = total_regular + Double.valueOf(expenseData.get(i).getAmount());
                    else
                        total_non_regular = total_non_regular + Double.valueOf(expenseData.get(i).getAmount());
                }
            }
            double surplus = income - total_amount;
            DecimalFormat roundFormat = new DecimalFormat("#.##");
            if (surplus > 0) {
                tv_surplus.setText("Surplus: " + AppConstants.CURRENCY + roundFormat.format(surplus));
                tv_surplus.setTextColor(getResources().getColor(R.color.colorSurplus));
            } else {
                tv_surplus.setText("Deficit: " + AppConstants.CURRENCY + roundFormat.format(surplus));
                tv_surplus.setTextColor(getResources().getColor(R.color.colorDeficit));
            }
            tv_total_regular.setText("Regular: " + AppConstants.CURRENCY + roundFormat.format(total_regular));
            tv_total_non_regular.setText("Non-Regular: " + AppConstants.CURRENCY + roundFormat.format(total_non_regular));
            showExpenseStatistics(Float.parseFloat(total_regular + ""), Float.parseFloat(total_non_regular + ""));
        } else {
            tv_surplus.setVisibility(View.GONE);
            tv_empty_info.setVisibility(View.VISIBLE);
            layout_totals.setVisibility(View.GONE);
            listView_months.setVisibility(View.GONE);
        }
    }

    private void showExpenseStatistics(float regular, float non_regular) {
        ArrayList<Float> pieValues = new ArrayList<>();
        pieValues.add(regular);
        pieValues.add(non_regular);
        pieChart.drawChart(this, pieValues);
    }

    private void showEditIncomeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_income, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Edit Income");

        final EditText et_income = (EditText) dialogView.findViewById(R.id.et_income);
        et_income.setText(income + "");
        Button btn_update = (Button) dialogView.findViewById(R.id.btn_update);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_income.getText().toString().length() > 0 && !et_income.getText().toString().equalsIgnoreCase("") && !et_income.getText().toString().equalsIgnoreCase(".")) {
                    new ExpenseDB(getApplicationContext()).editIncome(Double.valueOf(et_income.getText().toString()), month_id);
                    Toast.makeText(MonthDetailActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    fetchIncomeFromDB();
                    fetchExpenseFromDB();
                } else
                    Toast.makeText(MonthDetailActivity.this, "Please enter the income", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDeleteDialog(final int position, final long itemId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_delete, null);
        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("Edit Income");

        Button btn_delete = (Button) dialogView.findViewById(R.id.btn_delete);
        Button btn_edit = (Button) dialogView.findViewById(R.id.btn_edit);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddExpenseActivity.class)
                        .putExtra(SELECTED_MONTH_ID, month_id)
                        .putExtra(SELECTED_MONTH, sel_month)
                        .putExtra(SELECTED_YEAR, sel_year)
                        .putExtra(SELECTED_EXPENSE, expenseData.get(position))
                        .putExtra(INTENT_MODE, INTENT_MODE_EDIT));
                alertDialog.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExpenseDB(getApplicationContext()).deleteExpense(itemId);
                Toast.makeText(MonthDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

//                fetchIncomeFromDB();
                fetchExpenseFromDB();
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchIncomeFromDB();
        fetchExpenseFromDB();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}