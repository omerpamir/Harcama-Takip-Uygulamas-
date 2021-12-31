package com.fm.egecuzdan.view;

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

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.adapters.AylıkListeAdapter;
import com.fm.egecuzdan.db.ExpenseDB;
import com.fm.egecuzdan.db.models.SheetModel;
import com.fm.egecuzdan.utils.AppConstants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_AY_ID;

public class AnaSayfa extends AppCompatActivity {

    private ListView listView_yıllık;
    private AylıkListeAdapter adapter;
    private TextView tv_null;
    private TextView tv_ort_artan;
    private CardView cv_genel_bilgi;
    private TextView tv_ort_gelir;
    private TextView tv_genel_kalan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ana_sayfa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView_yıllık = (ListView) findViewById(R.id.listview_yıllık);
        tv_null = (TextView) findViewById(R.id.tv_null);
        tv_ort_artan = (TextView) findViewById(R.id.tv_kalan_deger);
        tv_ort_gelir = (TextView) findViewById(R.id.tv_toplam_gelir);
        tv_genel_kalan = (TextView) findViewById(R.id.tv_genel_kalan);
        cv_genel_bilgi = (CardView) findViewById(R.id.cv_genel_bilgi);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewSheetDialog();
            }
        });

        fetchSheetsFromDB();

        listView_yıllık.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), AylıkDetay.class).putExtra(SEÇİLEN_AY_ID, "" + parent.getAdapter().getItemId(position)));
            }
        });
    }

    //get data from sheets table
    private void fetchSheetsFromDB() {
        ArrayList<SheetModel> sheetData = new ExpenseDB(this).getSheets();
        if (sheetData != null) {
            tv_null.setVisibility(View.GONE);
            cv_genel_bilgi.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new AylıkListeAdapter(this, sheetData);
                listView_yıllık.setAdapter(adapter);
            } else {
                adapter.updateSheetList(sheetData);
            }

            double total_expense = new ExpenseDB(this).getOverallTotalExpense();
            double total_income = 0;
            for (int i = 0; i < sheetData.size(); i++) {
                total_income = total_income + sheetData.get(i).getGelir();
            }
            double total_surplus = total_income - total_expense;
            DecimalFormat roundFormat = new DecimalFormat("#.##");
            tv_ort_artan.setText(AppConstants.PARA_BİRİMİ + roundFormat.format(total_surplus));
            if (total_surplus > 0) {
                tv_genel_kalan.setText("Toplam Artan");
                tv_genel_kalan.setTextColor(getResources().getColor(R.color.colorSurplus));
                tv_ort_artan.setTextColor(getResources().getColor(R.color.colorSurplus));
            } else {
                tv_genel_kalan.setText("Toplam Eksik");
                tv_genel_kalan.setTextColor(getResources().getColor(R.color.colorDeficit));
                tv_ort_artan.setTextColor(getResources().getColor(R.color.colorDeficit));
            }
            tv_ort_gelir.setText("Toplam Gelir: " + AppConstants.PARA_BİRİMİ + roundFormat.format(total_income));
        } else {
            tv_null.setVisibility(View.VISIBLE);
            cv_genel_bilgi.setVisibility(View.GONE);
        }
    }

    //show dialog box to add new sheet
    private void showAddNewSheetDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_new_sheet, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Yeni Sheet Ekle");

        final EditText et_income = (EditText) dialogView.findViewById(R.id.et_gelir);
        final Spinner spinner_month = (Spinner) dialogView.findViewById(R.id.spinner_aylar);
        final Spinner spinner_year = (Spinner) dialogView.findViewById(R.id.spinner_yıllar);
        Button btn_update = (Button) dialogView.findViewById(R.id.btn_ekle);

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
                        Toast.makeText(getApplicationContext(), "Yeni sheet eklendi", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        fetchSheetsFromDB();
                    } else {
                        Toast.makeText(getApplicationContext(), "Halihazırda böyle bir sheet mevcut", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(AnaSayfa.this, "Lütfen gelir miktarını giriniz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSheetsFromDB();
    }
}