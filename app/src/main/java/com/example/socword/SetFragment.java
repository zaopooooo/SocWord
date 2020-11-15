package com.example.socword;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.fragment.app.Fragment;


public class SetFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private SwitchButton switchButton;
    private Spinner spinnerDifficulty;
    private Spinner spinnerAllNum;
    private Spinner spinnerNewNum;
    private Spinner spinnerReviewNum;
    private ArrayAdapter<String> adapterDifficulty,
            adapterAllNum,
            adapterNewNum,
            adapterReviewNum;

    String [] difficulty = new String[]{"小学","初中","高中","四级","六级"};
    String [] allNum = new String[]{"2道","4道","6道","8道"};
    String [] newNum = new String[]{"10","30","50","100"};
    String [] reviewNum = new String[]{"10","30","50","700"};

    SharedPreferences.Editor editor = null;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.set_fragment_layout,null);
        init(view);
        return view;
    }

    public void onStart(){
        super.onStart();
        if (sharedPreferences.getBoolean("btnTf",false)){
            switchButton.openSwitch();
        }else {
            switchButton.closeSwitch();
        }
    }





    public void init(View view){
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        switchButton = (SwitchButton) view.findViewById(R.id.switch_btn);
        switchButton.setOnClickListener(this);

        spinnerDifficulty = (Spinner) view.findViewById(R.id.spinner_difficulty);
        spinnerAllNum = (Spinner) view.findViewById(R.id.spinner_all_number);
        spinnerNewNum = (Spinner) view.findViewById(R.id.spinner_new_number);
        spinnerReviewNum = (Spinner) view.findViewById(R.id.spinner_review_number);

        adapterDifficulty = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,difficulty);
        spinnerDifficulty.setAdapter(adapterDifficulty);
        setSpinnerItemSelectedByValue(spinnerDifficulty,sharedPreferences.getString("difficulty","四级"));
        this.spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("difficulty",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        adapterAllNum = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,allNum);
        spinnerAllNum.setAdapter(adapterAllNum);
        setSpinnerItemSelectedByValue(spinnerAllNum,sharedPreferences.getInt("allNum",2)+"道");
        this.spinnerAllNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                int i = Integer.parseInt(msg.substring(0,1));
                editor.putInt("allNum",i);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        adapterNewNum = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,newNum);
        spinnerNewNum.setAdapter(adapterNewNum);
        setSpinnerItemSelectedByValue(spinnerNewNum,sharedPreferences.getString("newNum","10"));
        this.spinnerNewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("newNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        adapterReviewNum = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_selectable_list_item,reviewNum);
        spinnerReviewNum.setAdapter(adapterReviewNum);
        setSpinnerItemSelectedByValue(spinnerReviewNum,sharedPreferences.getString("reviewNum","10"));
        this.spinnerReviewNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String msg = parent.getItemAtPosition(position).toString();
                editor.putString("reviewNum",msg);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







    }

    private void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();
        int k = apsAdapter.getCount();
        for (int i = 0;i<k;i++){
            if (value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i,true);
            }
        }
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_btn:
                if (switchButton.isSwitchOpen()){
                    switchButton.closeSwitch();
                    editor.putBoolean("btnTf",false);
                }else {
                    switchButton.openSwitch();
                    editor.putBoolean("btnTf",true);
                }
                editor.commit();
                break;
        }
    }
}
