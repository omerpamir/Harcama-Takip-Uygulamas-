package com.fm.egecuzdan.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.adapters.GiderListesiAdapter;
import com.fm.egecuzdan.db.GiderDB;
import com.fm.egecuzdan.db.models.GiderModel;
import com.fm.egecuzdan.db.models.SheetModel;
import com.fm.egecuzdan.utils.AppConstants;
import com.fm.egecuzdan.view.custom.CustomPieChart;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.fm.egecuzdan.utils.AppConstants.INTENT_MODE;
import static com.fm.egecuzdan.utils.AppConstants.INTENT_MODE_ADD;
import static com.fm.egecuzdan.utils.AppConstants.INTENT_MODE_EDIT;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_GİDER;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_AY;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_AY_ID;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_YIL;

public class AylıkDetay extends AppCompatActivity {

    private ListView listView_months;
    private GiderListesiAdapter adapter;
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
    private ArrayList<GiderModel> expenseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView_months = (ListView) findViewById(R.id.listview_aylar);
        tv_income = (TextView) findViewById(R.id.tv_gelir);
        tv_month = (TextView) findViewById(R.id.tv_ay);
        tv_surplus = (TextView) findViewById(R.id.tv_artan);
        tv_empty_info = (TextView) findViewById(R.id.tv_null);
        tv_total_regular = (TextView) findViewById(R.id.tv_toplam_düzenli);
        tv_total_non_regular = (TextView) findViewById(R.id.tv_toplam_düzensiz);
        layout_totals = (RelativeLayout) findViewById(R.id.layout_toplamlar);
        pieChart = (CustomPieChart) findViewById(R.id.pie_chart);

        month_id = getIntent().getStringExtra(SEÇİLEN_AY_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GiderEkleme.class)
                        .putExtra(SEÇİLEN_AY_ID, month_id)
                        .putExtra(SEÇİLEN_AY, sel_month)
                        .putExtra(SEÇİLEN_YIL, sel_year)
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
        SheetModel sheetData = new GiderDB(getApplicationContext()).getGelirData(month_id);
        if (sheetData != null) {
            income = sheetData.getGelir();
            sel_month = sheetData.getAy();
            sel_year = sheetData.getYıl();
            tv_month.setText(sheetData.getAy() + " " + sheetData.getYıl());
            tv_income.setText("Gelir: " + AppConstants.PARA_BİRİMİ + income);
            setTitle(sheetData.getAy() + " " + sheetData.getYıl());
        }
    }

    private void fetchExpenseFromDB() {
        expenseData = new GiderDB(getApplicationContext()).getGiderler(month_id);
        if (expenseData != null) {
            tv_empty_info.setVisibility(View.GONE);
            layout_totals.setVisibility(View.VISIBLE);
            tv_surplus.setVisibility(View.VISIBLE);
            listView_months.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new GiderListesiAdapter(this, expenseData);
                listView_months.setAdapter(adapter);
            } else {
                adapter.updateGiderListesi(expenseData);
            }
            double total_amount = 0;
            double total_regular = 0;
            double total_non_regular = 0;
            for (int i = 0; i < expenseData.size(); i++) {
                if (!expenseData.get(i).getMiktar().equalsIgnoreCase("")) {
                    total_amount = total_amount + Double.valueOf(expenseData.get(i).getMiktar());
                    if (expenseData.get(i).düzenliÖdemedir())
                        total_regular = total_regular + Double.valueOf(expenseData.get(i).getMiktar());
                    else
                        total_non_regular = total_non_regular + Double.valueOf(expenseData.get(i).getMiktar());
                }
            }
            double surplus = income - total_amount;
            DecimalFormat roundFormat = new DecimalFormat("#.##");
            if (surplus > 0) {
                tv_surplus.setText("Artan: " + AppConstants.PARA_BİRİMİ + roundFormat.format(surplus));
                tv_surplus.setTextColor(getResources().getColor(R.color.colorSurplus));
            } else {
                tv_surplus.setText("Eksik: " + AppConstants.PARA_BİRİMİ + roundFormat.format(surplus));
                tv_surplus.setTextColor(getResources().getColor(R.color.colorDeficit));
            }
            tv_total_regular.setText("Düzenli: " + AppConstants.PARA_BİRİMİ + roundFormat.format(total_regular));
            tv_total_non_regular.setText("Düzensiz: " + AppConstants.PARA_BİRİMİ + roundFormat.format(total_non_regular));
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
        dialogBuilder.setTitle("Gelir Düzenle");

        final EditText et_income = (EditText) dialogView.findViewById(R.id.et_gelir);
        et_income.setText(income + "");
        Button btn_update = (Button) dialogView.findViewById(R.id.btn_güncelle);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_income.getText().toString().length() > 0 && !et_income.getText().toString().equalsIgnoreCase("") && !et_income.getText().toString().equalsIgnoreCase(".")) {
                    new GiderDB(getApplicationContext()).editGelir(Double.valueOf(et_income.getText().toString()), month_id);
                    Toast.makeText(AylıkDetay.this, "Güncellendi", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    fetchIncomeFromDB();
                    fetchExpenseFromDB();
                } else
                    Toast.makeText(AylıkDetay.this, "Lütfen geliri giriniz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDeleteDialog(final int position, final long itemId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_delete, null);
        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle("Edit Income");

        Button btn_delete = (Button) dialogView.findViewById(R.id.btn_sil);
        Button btn_edit = (Button) dialogView.findViewById(R.id.btn_edit);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GiderEkleme.class)
                        .putExtra(SEÇİLEN_AY_ID, month_id)
                        .putExtra(SEÇİLEN_AY, sel_month)
                        .putExtra(SEÇİLEN_YIL, sel_year)
                        .putExtra(SEÇİLEN_GİDER, expenseData.get(position))
                        .putExtra(INTENT_MODE, INTENT_MODE_EDIT));
                alertDialog.dismiss();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GiderDB(getApplicationContext()).giderSil(itemId);
                Toast.makeText(AylıkDetay.this, "Silindi", Toast.LENGTH_SHORT).show();
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