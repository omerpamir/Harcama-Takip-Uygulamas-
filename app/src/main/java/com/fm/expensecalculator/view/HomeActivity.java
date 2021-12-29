package com.fm.expensecalculator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fm.expensecalculator.R;
import com.fm.expensecalculator.adapters.MonthlyListAdapter;
import com.fm.expensecalculator.db.ExpenseDB;
import com.fm.expensecalculator.db.models.SheetModel;
import com.fm.expensecalculator.utils.AppConstants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.fm.expensecalculator.utils.AppConstants.SELECTED_MONTH_ID;

public class HomeActivity extends AppCompatActivity {

    private ListView listView_annual;
    private MonthlyListAdapter adapter;
    private TextView tv_empty_info;
    private TextView tv_overall_surplus;
    private CardView cv_overall_info;
    private TextView tv_overall_income;
    private TextView tv_label_surplus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView_annual = (ListView) findViewById(R.id.listview_annual);
        tv_empty_info = (TextView) findViewById(R.id.tv_empty_info);
        tv_overall_surplus = (TextView) findViewById(R.id.tv_overall_surplus);
        tv_overall_income = (TextView) findViewById(R.id.tv_overall_income);
        tv_label_surplus = (TextView) findViewById(R.id.tv_label_surplus);
        cv_overall_info = (CardView) findViewById(R.id.cv_overall_info);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewSheetDialog();
            }
        });

        fetchSheetsFromDB();

        listView_annual.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), MonthDetailActivity.class).putExtra(SELECTED_MONTH_ID, "" + parent.getAdapter().getItemId(position)));
            }
        });
    }

    //get data from sheets table
    private void fetchSheetsFromDB() {
        ArrayList<SheetModel> sheetData = new ExpenseDB(this).getSheets();
        if (sheetData != null) {
            tv_empty_info.setVisibility(View.GONE);
            cv_overall_info.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new MonthlyListAdapter(this, sheetData);
                listView_annual.setAdapter(adapter);
            } else {
                adapter.updateSheetList(sheetData);
            }

            double total_expense = new ExpenseDB(this).getOverallTotalExpense();
            double total_income = 0;
            for (int i = 0; i < sheetData.size(); i++) {
                total_income = total_income + sheetData.get(i).getIncome();
            }
            double total_surplus = total_income - total_expense;
            DecimalFormat roundFormat = new DecimalFormat("#.##");
            tv_overall_surplus.setText(AppConstants.CURRENCY + roundFormat.format(total_surplus));
            if (total_surplus > 0) {
                tv_label_surplus.setText("Overall Surplus");
                tv_label_surplus.setTextColor(getResources().getColor(R.color.colorSurplus));
                tv_overall_surplus.setTextColor(getResources().getColor(R.color.colorSurplus));
            } else {
                tv_label_surplus.setText("Overall Deficit");
                tv_label_surplus.setTextColor(getResources().getColor(R.color.colorDeficit));
                tv_overall_surplus.setTextColor(getResources().getColor(R.color.colorDeficit));
            }
            tv_overall_income.setText("Total Income: " + AppConstants.CURRENCY + roundFormat.format(total_income));
        } else {
            tv_empty_info.setVisibility(View.VISIBLE);
            cv_overall_info.setVisibility(View.GONE);
        }
    }

    //show dialog box to add new sheet
    private void showAddNewSheetDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_new_sheet, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Add New Sheet");

        final EditText et_income = (EditText) dialogView.findViewById(R.id.et_income);
        final Spinner spinner_month = (Spinner) dialogView.findViewById(R.id.spinner_months);
        final Spinner spinner_year = (Spinner) dialogView.findViewById(R.id.spinner_years);
        Button btn_update = (Button) dialogView.findViewById(R.id.btn_add);

        ArrayList<String> yearsArray = new ArrayList<String>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        for (int i = 2000; i <= (currentYear + 5); i++) {
            yearsArray.add(Integer.toString(i));
        }
        ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, yearsArray);
        spinner_year.setAdapter(sp_adapter);
        spinner_year.setSelection(sp_adapter.getPosition(String.valueOf(currentYear)));
        spinner_month.setSelection(currentMonth);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_income.getText().toString().length() > 0) {
                    boolean status = new ExpenseDB(getApplicationContext()).addNewSheet(String.valueOf(spinner_month.getSelectedItemPosition()), spinner_year.getSelectedItem().toString(), Double.parseDouble(et_income.getText().toString()));
                    if (status) {
                        Toast.makeText(getApplicationContext(), "Added new sheet", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        fetchSheetsFromDB();
                    } else {
                        Toast.makeText(getApplicationContext(), "This sheet already exists", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(HomeActivity.this, "Please enter the income", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSheetsFromDB();
    }
}