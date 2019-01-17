package com.example.luolab.guanmeasure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.hardware.camera2.*;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;
import java.util.logging.FileHandler;

public class PPGView extends Fragment{

    private View ppgView;

    private Button start_btn;
    private Button setUiInfo_btn;
    private Button menu_btn;

    private AlertDialog.Builder UsrInfoDialog_Builder;
    private AlertDialog UsrInfoDialog;

    private ArrayList<String> usrInfo_Array;

    private LayoutInflater LInflater;

    private String Get_Uri = "http://140.116.164.6/getDataFromDB.php";
    private String Insert_Uri = "http://140.116.164.6/insertDataToDB.php";
    private String Get_Query_Command = "SELECT * FROM PPG";
    private String Get_Query_Command_GSR = "SELECT * FROM gsr";
    private String Insert_Query_Command = "INSERT INTO PPG (name,age,birthday,height,weight,doctor)VALUES";
    private String Insert_Query_Command_GSR = "INSERT INTO GSR (name,age,birthday,height,weight,doctor)VALUES";
    private String Update_Command = "UPDATE PPG SET ";
    private String Update_Command_GSR = "UPDATE GSR SET ";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){


        ppgView = inflater.inflate(R.layout.ppg, container, false);

        setUiInfo_btn = ppgView.findViewById(R.id.SetUsrInfo_btn);
        setUiInfo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDB();
                UsrInfoDialog.show();
            }
        });

        usrInfo_Array = new ArrayList<String>();


        return ppgView;
    }
    private String GetDB(String Query_Command,String uri)
    {
        String result = null;
        try {
            result = DBConnector.executeQuery(Query_Command,uri);
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
//            JSONArray jsonArray = new JSONArray(result);
//            for(int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonData = jsonArray.getJSONObject(i);
//
//                usrInfo_Array.add(jsonData.getString("name"));
//            }
        } catch(Exception e) {
        }
        return result;
    }
    private void updateDB()
    {
        usrInfo_Array.clear();

        String result = GetDB(Get_Query_Command,Get_Uri);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                usrInfo_Array.add(jsonData.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(LInflater.getContext()).setMessage("此應用程式需要有網路，偵測您無開啟網路" + '\n' + "請確定開始此應用程式時，網路是有連線的狀態" + '\n' + "如未開啟網路並連線，請開啟連線後，關閉此程式再重新開啟此應用程式")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
        }
    }
}