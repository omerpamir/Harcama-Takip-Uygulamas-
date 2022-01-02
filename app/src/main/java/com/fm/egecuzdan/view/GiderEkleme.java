package com.fm.egecuzdan.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fm.egecuzdan.R;
import com.fm.egecuzdan.db.GiderDB;
import com.fm.egecuzdan.db.models.GiderModel;

import java.util.Calendar;

import static com.fm.egecuzdan.utils.AppConstants.INTENT_MODE;
import static com.fm.egecuzdan.utils.AppConstants.INTENT_MODE_ADD;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_GİDER;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_AY;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_AY_ID;
import static com.fm.egecuzdan.utils.AppConstants.SEÇİLEN_YIL;

public class GiderEkleme extends AppCompatActivity {

    private EditText et_remarks, et_amount;
    private Button btn_save;
    private DatePicker datePicker;
    private RadioButton rb_regular, rb_non_regular;
    private String sel_month_id;
    private String sel_month;
    private String sel_year;
    private Button btn_update;
    private GiderModel giderModel;
    private String TAG = "add_exp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        et_remarks = (EditText) findViewById(R.id.et_açıklama);
        et_amount = (EditText) findViewById(R.id.et_miktar);
        btn_save = (Button) findViewById(R.id.btn_kaydet);
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        rb_regular = (RadioButton) findViewById(R.id.rb_düzenli);
        rb_non_regular = (RadioButton) findViewById(R.id.rb_düznsiz);
        btn_update = (Button) findViewById(R.id.btn_güncelle);

        sel_month_id = getIntent().getStringExtra(SEÇİLEN_AY_ID);
        sel_month = getIntent().getStringExtra(SEÇİLEN_AY);
        sel_year = getIntent().getStringExtra(SEÇİLEN_YIL);

        if (getIntent().getIntExtra(INTENT_MODE, INTENT_MODE_ADD) == INTENT_MODE_ADD) {
            setTitle("Gider Ekle");
            btn_update.setVisibility(View.GONE);
            btn_save.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            int today_day = c.get(Calendar.DAY_OF_MONTH);
            c.set(Integer.parseInt(sel_year), getMonthNumber(sel_month), c.getActualMinimum(Calendar.DAY_OF_MONTH));
            datePicker.setMinDate(c.getTimeInMillis());
            c.set(Integer.parseInt(sel_year), getMonthNumber(sel_month), c.getActualMaximum(Calendar.DAY_OF_MONTH));
            datePicker.setMaxDate(c.getTimeInMillis());
            datePicker.updateDate(Integer.parseInt(sel_year), getMonthNumber(sel_month), today_day);
            Log.d(TAG, "onCreate: " + today_day);
        } else {
            setTitle("Gider Düzenle");
            btn_update.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.GONE);
            giderModel = (GiderModel) getIntent().getSerializableExtra(SEÇİLEN_GİDER);

            et_amount.setText(giderModel.getMiktar() + "");
            et_remarks.setText(giderModel.getAçıklama() + "");
            if (giderModel.düzenliÖdemedir()) {
                rb_regular.setChecked(true);
            } else
                rb_non_regular.setChecked(true);

            Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(sel_year), getMonthNumber(sel_month), c.getActualMinimum(Calendar.DAY_OF_MONTH));
            datePicker.setMinDate(c.getTimeInMillis());
            c.set(Integer.parseInt(sel_year), getMonthNumber(sel_month), c.getActualMaximum(Calendar.DAY_OF_MONTH));
            datePicker.setMaxDate(c.getTimeInMillis());
            datePicker.updateDate(Integer.parseInt(sel_year), getMonthNumber(sel_month), Integer.parseInt(giderModel.getTarih()));

        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new GiderDB(getApplicationContext()).giderEkle(et_amount.getText().toString(), et_remarks.getText().toString(), String.valueOf(datePicker.getDayOfMonth()), rb_regular.isChecked(), sel_month_id);
                    Toast.makeText(GiderEkleme.this, "Eklendi", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: save " + rb_regular.isChecked());
                    finish();
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new GiderDB(getApplicationContext()).updateGider(et_amount.getText().toString(), et_remarks.getText().toString(), String.valueOf(datePicker.getDayOfMonth()), rb_regular.isChecked(), giderModel.getId());
                    Toast.makeText(GiderEkleme.this, "Eklendi", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: upd " + rb_regular.isChecked());
                    finish();
                }
            }
        });
    }

    //returns month number
    private int getMonthNumber(String month) {
        String[] months = getResources().getStringArray(R.array.aylar);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(month)) {
                return i;
            }
        }
        return 0;
    }

    private boolean validate() {
        if (et_amount.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(this, "Değer giriniz", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_amount.getText().toString().length() < 0) {
            Toast.makeText(this, "Değer giriniz", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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