package com.example.socword;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.mingrisoft.greendao.entity.greendao.CET4Entity;
import com.mingrisoft.greendao.entity.greendao.CET4EntityDao;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SynthesizerListener {

    private TextView timeText,dateText,wordText,engishText;
    private ImageView playVioce;
    private String mMonth,mDay,mWay,mHours,mMinute;
    private SpeechSynthesizer speechSynthesizer;
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private RadioGroup radioGroup;
    private RadioButton radioOne, radioTwo,radioThree;
    private SharedPreferences sharePreferences;
    SharedPreferences.Editor editor = null;
    int j = 0;
    List<Integer> list;
    List<CET4Entity> datas;
    int k;
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    private SQLiteDatabase db;
    private DaoMaster mMdaoMaster,dbMaster;
    private DaoSession mDaoSession,dbSession;
    private CET4EntityDao questionDao,dbDao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        sharePreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharePreferences.edit();
        list = new ArrayList<Integer>();
        Random r = new Random();
        int i;
        while (list.size()<10){
            i = r.nextInt(20);
            if (!list.contains(i)){
                list.add(i);
            }
        }

        km =(KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");
        AssetsDatabaseManager.initManager(this);
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("word.db");


        mMdaoMaster = new DaoMaster(db1);
        mDaoSession = mMdaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();


        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"wrong.db",null);


        db = helper.getWritableDatabase();
        dbMaster = new DaoMaster(db);
        dbSession = dbMaster.newSession();
        dbDao = dbSession.getCET4EntityDao();



        timeText = (TextView) findViewById(R.id.time_text);
        dateText = (TextView)findViewById(R.id.date_text);
        wordText = (TextView)findViewById(R.id.word_text);
        engishText = (TextView)findViewById(R.id.english_text);
        playVioce = (ImageView)findViewById(R.id.play_vioce);
        playVioce.setOnClickListener(this);

        radioGroup = (RadioGroup)findViewById(R.id.choose_group);
        radioOne = (RadioButton)findViewById(R.id.choose_btn_one);
        radioTwo = (RadioButton)findViewById(R.id.choose_btn_two);
        radioThree = (RadioButton)findViewById(R.id.choose_btn_three);
        radioGroup.setOnCheckedChangeListener(this);

        getDBData();
        setParam();



    }





    protected void onStart(){
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        mMonth = String.valueOf(calendar.get(Calendar.MONTH));
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        if (calendar.get(Calendar.HOUR)<10){
            mHours = "0" + calendar.get(Calendar.HOUR);
        }else {
            mHours = String.valueOf(calendar.get(Calendar.HOUR));
        }

        if (calendar.get(Calendar.MINUTE)<10){
            mMinute = "0" + calendar.get(Calendar.MINUTE);
        }else {
            mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        }


        if ("1".equals(mWay)){
            mWay =  "天";
        }else if ("2".equals(mWay)){
            mWay = "一";
        }else if ("3".equals(mWay)){
            mWay = "二";
        }else if ("4".equals(mWay)){
            mWay = "三";
        }else if ("5".equals(mWay)){
            mWay = "四";
        }else if ("6".equals(mWay)){
            mWay = "五";
        }else if ("7".equals(mWay)){
            mWay = "六";
        }
        timeText.setText(mHours+":"+mMinute);
        dateText.setText(mMonth+"月"+mDay+"日"+"   "+"星期"+mWay);

        BaseApplication.addDestroyActivity(this,"mainActivity");

    }

    private void saveWrongData(){
        String word = datas.get(k).getWord();
        String english = datas.get(k).getEnglish();
        String china = datas.get(k).getChina();
        String sign = datas.get(k).getSign();
        CET4Entity data = new CET4Entity(Long.valueOf(dbDao.count()),word,english,china,sign);
        dbDao.insertOrReplace(data);
    }

    private void btnGetText(String msg,RadioButton btn){
        if (msg.equals(datas.get(k).getChina())){
            wordText.setTextColor(Color.GREEN);
            engishText.setTextColor(Color.GREEN);
            btn.setTextColor(Color.GREEN);
        }else {
            wordText.setTextColor(Color.RED);
            engishText.setTextColor(Color.RED);
            btn.setTextColor(Color.RED);

            saveWrongData();

            int wrong = sharePreferences.getInt("wrong",0);
            editor.putInt("wrong",wrong+1);
            editor.putString("wrongId",","+datas.get(j).getId());
            editor.commit();
        }
    }








    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_vioce:
                String text = wordText.getText().toString();
                speechSynthesizer.startSpeaking(text,this);
                break;
        }

    }





    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        radioGroup.setClickable(false);
        switch (checkedId){
            case R.id.choose_btn_one:
                String msg = radioOne.getText().toString().substring(2);
                btnGetText(msg,radioOne);
                break;
            case R.id.choose_btn_two:
                String msg1 = radioTwo.getText().toString().substring(2);
                btnGetText(msg1,radioTwo);
                break;
            case R.id.choose_btn_three:
                String msg2 = radioThree.getText().toString().substring(2);
                btnGetText(msg2,radioThree);
                break;
        }
    }


    private void setTextColor(){
        radioOne.setChecked(false);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);

        radioOne.setTextColor(Color.parseColor("#ffffff"));
        radioTwo.setTextColor(Color.parseColor("#ffffff"));
        radioThree.setTextColor(Color.parseColor("#ffffff"));
        wordText.setTextColor(Color.parseColor("#ffffff"));
        engishText.setTextColor(Color.parseColor("#ffffff"));

    }










    private SpeechListener listener = new SpeechListener() {
        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }
    };

    public void setParam(){
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"=5f8fe9ea");
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED,"50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME,"50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH,"50");
    }





    private void unlocked(){
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent1);
        kl.disableKeyguard();
        finish();
    }

    private void setChina(List<CET4Entity>datas,int j) {
        Random r = new Random();
        List<Integer> listInt = new ArrayList<>();
        int i;
        while (listInt.size() < 4) {
            i = r.nextInt(20);
            if (!listInt.contains(i)){
                listInt.add(i);
            }
        }
        if (listInt.get(0)<7){
            radioOne.setText("A:"+datas.get(k).getChina());
            if (k - 1 >=0){
                radioTwo.setText("B:"+datas.get(k - 1).getChina());
            }else {
                radioTwo.setText("B:"+datas.get(k + 2).getChina());
            }
            if (k + 1 < 20){
                radioThree.setText("C:"+datas.get(k + 1).getChina());
            }else {
                radioThree.setText("C:"+datas.get(k - 1).getChina());
            }
        }else  if (listInt.get(0)<14){
            radioTwo.setText("B:"+datas.get(k).getChina());
            if (k - 1 >=0){
                radioOne.setText("A:"+datas.get(k - 1).getChina());
            }else {
                radioOne.setText("A:"+datas.get(k + 2).getChina());
            }
            if (k + 1 < 20){
                radioThree.setText("C:"+datas.get(k + 1).getChina());
            }else {
                radioThree.setText("C:"+datas.get(k - 1).getChina());
            }
        }else {
            radioThree.setText("C:"+datas.get(k).getChina());
            if (k - 1 >=0){
                radioTwo.setText("B:"+datas.get(k - 1).getChina());
            }else {
                radioTwo.setText("B:"+datas.get(k + 2).getChina());
            }
            if (k + 1 < 20){
                radioOne.setText("A:"+datas.get(k + 1).getChina());
            }else {
                radioOne.setText("A:"+datas.get(k - 1).getChina());
            }
        }
    }


    private void getDBData(){
        datas = questionDao.queryBuilder().list();
        k = list.get(j);
        wordText.setText(datas.get(k).getWord());
        engishText.setText(datas.get(k).getEnglish());
        setChina(datas,k);
    }


    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            x2 = event.getX();
            y2 = event.getY();
            if (y1 - y2 >200){
                int num = sharePreferences.getInt("alreadyMastered",0)+1;
                editor.putInt("alreadyMastered",num);
                editor.commit();
                Toast.makeText(this, "已掌握", Toast.LENGTH_SHORT).show();
                getNextData();
            }else if (y2 - y1 >200){
                Toast.makeText(this, "待加功能。。。", Toast.LENGTH_SHORT).show();
            }else if (x1 - x2 >200){
                getNextData();
            }else if (x2- x1 >200){
                unlocked();
            }
        }

        return super.onTouchEvent(event);
    }



    private void getNextData(){
        j++;
        int i = sharePreferences.getInt("allNum",2);
        if (i>j){
            getDBData();
            setTextColor();
            int num = sharePreferences.getInt("alreadyStudy",0)+1;
            editor.putInt("alreadyStudy",num);
            editor.commit();
        }else {
            unlocked();
        }
    }













    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}