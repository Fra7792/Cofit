package com.cofitconsulting.cofit.user.registro_finanziario;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.model.ModelTassa;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GraficoTasseFragment extends Fragment {

    private PieChart pieChart;
    private Spinner spinner, spinnerAnno;
    private FirebaseAuth fAuth;
    private String userID;
    private List<ModelTassa> modelList = new ArrayList<>();
    private FirebaseFirestore fStore;
    private ProgressDialog pd;
    private ArrayList<Float> importo2 = new ArrayList<>();
    private ArrayList<String> tassa2 = new ArrayList<>();
    private List<PieEntry> pieEntryList2 = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_grafico_tasse, container, false);

        pieChart = v.findViewById(R.id.pieChart);
        spinner = v.findViewById(R.id.spinner);
        spinnerAnno = v.findViewById(R.id.spinnerAnno);
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getInstance().getCurrentUser().getUid();
        pd = new ProgressDialog(getContext());
        ArrayAdapter<CharSequence> styleadapter = ArrayAdapter.createFromResource(
                getContext(), R.array.grafico, R.layout.spinner_layout);
        styleadapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(styleadapter);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 2017; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_layout, years);
        spinnerAnno.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                importo2.clear();
                tassa2.clear();
                pieEntryList2.clear();
                pieChart.invalidate();
                pieChart.clear();
                String scelta = spinner.getSelectedItem().toString();
                createPie(scelta, importo2, tassa2, pieEntryList2);

                spinnerAnno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        importo2.clear();
                        tassa2.clear();
                        pieEntryList2.clear();
                        pieChart.invalidate();
                        pieChart.clear();
                        String scelta = spinner.getSelectedItem().toString();
                        createPie(scelta, importo2, tassa2, pieEntryList2);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return v;
    }

    public class PercentFormatter extends ValueFormatter {

        public DecimalFormat mFormat;

        public PercentFormatter() {
            mFormat = new DecimalFormat();
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value) + " %";
        }

        @Override
        public String getPieLabel(float value, PieEntry pieEntry) {
            if (pieChart != null && pieChart.isUsePercentValuesEnabled()) {
                // Converted to percent
                return getFormattedValue(value);
            } else {
                // raw value, skip percent sign
                return mFormat.format(value);
            }
        }

    }

    private void showLegend(PieChart chart) {
        Legend l = chart.getLegend();
        l.setDrawInside(false);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setWordWrapEnabled(true);
        l.setXEntrySpace(8f);
        l.setYEntrySpace(10f);
        l.setYOffset(3f);
    }

    private void setPieChart(PieChart pieChart, PieData pieData){
        pieChart.setData(pieData);
        pieChart.setCenterText("Le tue tasse");
        pieChart.setCenterTextColor(0xffEDE7F6);
        pieChart.setCenterTextSize(20f);
        pieChart.setHoleColor(0xff673AB7);
        pieChart.setTouchEnabled(true);
        pieChart.animateXY(1500, 1500);
        pieChart.setEntryLabelColor(0xff000000);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }

    private void setPieDataSet(PieDataSet pieDataSet){
                        pieDataSet.setSliceSpace(5f);
                        pieDataSet.setColors(0xfff06292, 0xffBA68C8, 0xff9575CD, 0xff7986CB, 0xff64B5F6, 0xff4DB6AC, 0xff81C784, 0xffFFD54F, 0xffFF8A65);
                        pieDataSet.setSelectionShift(15f);
                        pieDataSet.setValueTextColor(0xff000000);
                        pieDataSet.setValueTextSize(15f);
                        pieDataSet.setValueFormatter(new PercentFormatter());
    }

    private void createPie(final String scelta, final ArrayList<Float> importo, final ArrayList<String> tassa, final List<PieEntry> pieEntryList){

        final Float[] impIVA = {(float) 0.00};
        final Float[] impDIP = {(float) 0.00};
        final Float[] impIMU = {(float) 0.00};
        final Float[] impCCIAA = {(float) 0.00};
        final Float[] impINAIL = {(float) 0.00};
        final Float[] impINPS = {(float) 0.00};
        final Float[] impRITENUTE = {(float) 0.00};
        final Float[] impRIFIUTI = {(float) 0.00};
        final Float[] impTASSE = {(float) 0.00};

        fStore.collection(userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                pd.dismiss();

                for (DocumentSnapshot doc : task.getResult()) {
                    ModelTassa modelTassa = new ModelTassa(doc.getString("Tassa"),
                            doc.getDouble("Importo"),
                            doc.getString("Scadenza"),
                            doc.getBoolean("Pagato"),
                            doc.getBoolean("Permesso pagamento"));
                    modelList.add(modelTassa);

                    String years = spinnerAnno.getSelectedItem().toString();

                    if (modelTassa.getPagato() && scelta.equals("Pagate") && modelTassa.getTassa().contains(years)) {
                        tassa.add(modelTassa.getTassa());
                        Float imp = Float.parseFloat(modelTassa.getImporto().toString());
                        importo.add(imp);
                    } else if (!modelTassa.getPagato() && scelta.equals("Da pagare") && modelTassa.getTassa().contains(years)) {
                        tassa.add(modelTassa.getTassa());
                        Float imp = Float.parseFloat(modelTassa.getImporto().toString());
                        importo.add(imp);
                    }
                }
                for (int i = 0; i < tassa.size(); i++) {

                    if (tassa.get(i).contains("IVA")) {
                        impIVA[0] = impIVA[0] + importo.get(i);
                    } else if (tassa.get(i).contains("DIPENDENTI")) {
                        impDIP[0] = impDIP[0] + importo.get(i);
                    } else if (tassa.get(i).contains("IMU")) {
                        impIMU[0] = impIMU[0] + importo.get(i);
                    } else if (tassa.get(i).contains("INPS")) {
                        impINPS[0] = impINPS[0] + importo.get(i);
                    } else if (tassa.get(i).contains("CCIAA")) {
                        impCCIAA[0] = impCCIAA[0] + importo.get(i);
                    } else if (tassa.get(i).contains("INAIL")) {
                        impINAIL[0] = impINAIL[0] + importo.get(i);
                    } else if (tassa.get(i).contains("RITENUTE")) {
                        impRITENUTE[0] = impRITENUTE[0] + importo.get(i);
                    } else if (tassa.get(i).contains("RIFIUTI")) {
                        impRIFIUTI[0] = impRIFIUTI[0] + importo.get(i);
                    } else if (tassa.get(i).contains("TASSE")) {
                        impTASSE[0] = impTASSE[0] + importo.get(i);
                    }

                }
                if (impIMU[0] > 0) pieEntryList.add(new PieEntry(impIMU[0], "IMU-TASI"));
                if (impDIP[0] > 0) pieEntryList.add(new PieEntry(impDIP[0], "DIPENDENTI"));
                if (impIVA[0] > 0) pieEntryList.add(new PieEntry(impIVA[0], "IVA"));
                if (impRIFIUTI[0] > 0) pieEntryList.add(new PieEntry(impRIFIUTI[0], "RIFIUTI"));
                if (impCCIAA[0] > 0) pieEntryList.add(new PieEntry(impCCIAA[0], "CCIAA"));
                if (impINAIL[0] > 0) pieEntryList.add(new PieEntry(impINAIL[0], "INAIL"));
                if (impRITENUTE[0] > 0)
                    pieEntryList.add(new PieEntry(impRITENUTE[0], "RITENUTE"));
                if (impINPS[0] > 0) pieEntryList.add(new PieEntry(impINPS[0], "INPS"));
                if (impTASSE[0] > 0) pieEntryList.add(new PieEntry(impTASSE[0], "TASSE"));
                PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
                setPieDataSet(pieDataSet);
                PieData pieData = new PieData(pieDataSet);
                setPieChart(pieChart, pieData);
                showLegend(pieChart);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        });

    }
}













