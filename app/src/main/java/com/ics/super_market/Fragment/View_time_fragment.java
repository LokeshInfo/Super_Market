package com.ics.super_market.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ics.super_market.Adapter.Home_adapter;
import com.ics.super_market.Adapter.View_time_adapter;
import com.ics.super_market.Config.BaseURL;
import com.ics.super_market.Model.Category_model;
import com.ics.super_market.AppController;
import com.ics.super_market.MainActivity;
import com.ics.super_market.R;
import com.ics.super_market.ConnectivityReceiver;
import com.ics.super_market.CustomVolleyJsonRequest;
import com.ics.super_market.RecyclerTouchListener;
import com.ics.super_market.Session_management;


public class View_time_fragment extends Fragment {

    private static String TAG = View_time_fragment.class.getSimpleName();

    private RecyclerView rv_time;
    private TextView tv_content;

    private List<String> time_list = new ArrayList<>();
    private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;

    private String getdate;

    private Session_management sessionManagement;

    public View_time_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.delivery_time));

        sessionManagement = new Session_management(getActivity());

        rv_time = (RecyclerView) view.findViewById(R.id.rv_times);
        tv_content = view.findViewById(R.id.tv_content);
        rv_time.setLayoutManager(new LinearLayoutManager(getActivity()));

        getdate = getArguments().getString("date");

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetTimeRequest(getdate);
        } else {
            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        rv_time.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_time, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String gettime = time_list.get(position);

                sessionManagement.cleardatetime();

                sessionManagement.creatdatetime(getdate,gettime);

                ((MainActivity) getActivity()).onBackPressed();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeGetTimeRequest(String date) {

        // Tag used to cancel the request
        String tag_json_obj = "json_time_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("date",date);

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Processing");
        dialog.setCancelable(true);
        dialog.show();

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_TIME_SLOT, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        for(int i=0;i<response.getJSONArray("times").length();i++) {
                            time_list.add(""+response.getJSONArray("times").get(i));
                        }
                     if(response.getJSONArray("times").length()==0){
                         tv_content.setVisibility(View.VISIBLE);
                     }else{
                         tv_content.setVisibility(View.GONE);
                     }

                        View_time_adapter adapter = new View_time_adapter(time_list);
                        rv_time.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
