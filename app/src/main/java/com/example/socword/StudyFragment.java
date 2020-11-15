package com.example.socword;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.mingrisoft.greendao.entity.greendao.DaoMaster;
import com.mingrisoft.greendao.entity.greendao.DaoSession;
import com.mingrisoft.greendao.entity.greendao.WisdomEntity;
import com.mingrisoft.greendao.entity.greendao.WisdomEntityDao;

import java.util.List;
import java.util.Random;

public class StudyFragment extends Fragment {

    private TextView difficultyTv,
            wisdomEnglish,
            wisdomChina,
            alreadyStudyText,
            alreaMasteredText,
            wrongText;

    private SharedPreferences sharedPreferences;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private WisdomEntityDao questionDao;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.study_fragment_layout,null);
        sharedPreferences = getActivity().getSharedPreferences("share", Context.MODE_PRIVATE);
        difficultyTv = (TextView) view.findViewById(R.id.difficulty_text);
        wisdomEnglish = (TextView) view.findViewById(R.id.wisdom_english);
        wisdomChina = (TextView) view.findViewById(R.id.wisdom_china);
        alreadyStudyText = (TextView) view.findViewById(R.id.already_study);
        alreaMasteredText = (TextView) view.findViewById(R.id.already_mastered);
        wrongText = (TextView) view.findViewById(R.id.wrong_text);

        AssetsDatabaseManager.initManager(getActivity());

        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = mg.getDatabase("wisdom.db");
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getWisdomEntityDao();
        return view;



    }



    public void onStart(){
        super.onStart();
        difficultyTv.setText(sharedPreferences.getString("difficulty","四级")+"英语");
        List<WisdomEntity> datas = questionDao.queryBuilder().list();
        Random random = new Random();
        int i = random.nextInt(10);

        wisdomEnglish.setText(datas.get(i).getEnglish());
        wisdomChina.setText(datas.get(i).getChina());
        setText();
    }


    public void  setText(){
        alreaMasteredText.setText(sharedPreferences.getInt("alreadyMastered",0)+"");
        alreadyStudyText.setText(sharedPreferences.getInt("alreadyStudy",0)+"");
        wrongText.setText(sharedPreferences.getInt("wrong",0)+"");
    }

}
