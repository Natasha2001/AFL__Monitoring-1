package com.theagriculture.app.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theagriculture.app.R;
import com.theagriculture.app.adapter.PendingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;


public class pending_fragment extends Fragment {

    //variables
    private ArrayList<String> mDdaName;
    private ArrayList<String> mAdaName;
    private ArrayList<String> mAddress;
    private ArrayList<String> mpkado;
    private ArrayList<String> mpkdda;
    private ArrayList<String> mId;
    private String token;
    private String villagename;
    private String blockname;
    private String district;
    private String aid;
    boolean doubleBackToExitPressedOnce = false;
    //private List<String> itemArrayList;
    //tags
    private static final String TAG = "pending_fragment";
    //private String pendingUrl = "http://18.224.202.135/api/locations/pending";
    private String pendingUrl = "http://18.224.202.135/api/locationsDatewise/pending";
    final ArrayList<Section> sections = new ArrayList<>();
    private String nextPendingUrl = "null";
    private LinearLayoutManager layoutManager;

    //private AdminLocationAdapter recyclerViewAdater;
    //public PendingAdapter recyclerViewAdater;
    private ItemAdapter item_adapter;
    private SectionAdapter recyclerViewAdater;
    // private ProgressBar progressBar;
    private boolean isNextBusy = false;
    private boolean isSendingNotifications = false;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressBar spinner;
    private boolean isRefresh;


    int count_entry = 0;
    //ProgressDialog pDialog;

    // Required empty public constructor
    public pending_fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        isRefresh = false;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.pending_fragment, container, false);

        swipeRefreshLayout = view.findViewById(R.id.refreshpull4);
        recyclerView = view.findViewById(R.id.recyclerViewpending);
//        progressBar = view.findViewById(R.id.locations_loading);
        spinner = view.findViewById(R.id.pending_progress);
//        spinner.setVisibility(View.VISIBLE);


        //for complete scroll for recycler view (from bottom to up(top))
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                //swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        isRefresh = false;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                spinner.setVisibility(View.GONE);
                Log.d(TAG, "onRefresh called from pending_fragment in locations");
                getFragmentManager().beginTransaction().detach(pending_fragment.this).attach(pending_fragment.this).commit();
            }
        });

        recyclerViewAdater = new SectionAdapter(getActivity(), sections);
        recyclerView.setAdapter(recyclerViewAdater);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        final SharedPreferences preferences = getActivity().getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
        token = preferences.getString("token", "");
        Log.d(TAG, "onCreateView: " + token);
//        Log.d(TAG, "onCreateView: inflated fragment_ongoing");

        // spinner.setVisibility(View.VISIBLE);
//        getData(pendingUrl);
        recyclerViewAdater.notifyDataSetChanged();

        return view;
    }

    private void getData(final String url) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    nextPendingUrl = jsonObject.getString("next");
                    Log.d(TAG, "onResponse: nextPendingUrl " + nextPendingUrl);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDdaName = new ArrayList<>();
                        mAdaName = new ArrayList<>();
                        mAddress = new ArrayList<>();
                        mpkado = new ArrayList<>();
                        mpkdda = new ArrayList<>();
                        mId = new ArrayList<>();
                        JSONObject cd = jsonArray.getJSONObject(i);
                        String mdate = cd.getString("date");
                        JSONArray jsonArray_locations = cd.getJSONArray("locations");
                        for (int j = 0; j < jsonArray_locations.length(); j++) {
                            JSONObject c = jsonArray_locations.getJSONObject(j);
                            try {
                                aid = c.getString("id");
                                mId.add(aid);
                            } catch (JSONException e) {
                                mId.add("null");
                            }
                            try {
                                JSONObject adoobj = c.getJSONObject("ado");
                                JSONObject authado = adoobj.getJSONObject("auth_user");
                                mpkado.add(authado.getString("pk"));
                            } catch (JSONException e) {
                                mpkado.add("null");
                            }
                            try {
                                JSONObject ddaobj = c.getJSONObject("dda");
                                JSONObject authddo = ddaobj.getJSONObject("auth_user");
                                mpkdda.add(authddo.getString("pk"));
                            } catch (JSONException e) {
                                mpkdda.add("null");
                            }
                            try {
                                JSONObject ddaobj = c.getJSONObject("dda");
                                JSONObject authddo = ddaobj.getJSONObject("auth_user");
                                mpkdda.add(authddo.getString("pk"));
                            } catch (JSONException e) {
                                mpkdda.add("null");
                            }
                            villagename = c.getString("village_name");
                            blockname = c.getString("block_name");
                            district = c.getString("district");
                            try {
                                JSONObject mDdaObject = c.getJSONObject("dda");
                                String ddaName = mDdaObject.getString("name");
                                mDdaName.add(ddaName);
                            } catch (JSONException e) {
                                mDdaName.add("Not Assigned");
                            }
                            try {
                                JSONObject mAdoObject = c.getJSONObject("ado");
                                String adoName = mAdoObject.getString("name");
                                mAdaName.add(adoName);
                            } catch (JSONException e) {
                                mAdaName.add("Not Assigned");
                            }
                            mAddress.add(villagename.toUpperCase() + ", " +
                                    blockname.toUpperCase() + ", " + district.toUpperCase());
                        }
                        sections.add(new Section(mdate, mDdaName, mAdaName, mAddress, mId, mpkado, mpkdda, true, false, false));
                    }
                    spinner.setVisibility(View.GONE);
                } catch (Exception e) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "An exception occurred", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the reuest has either time out or there is no connection
                    //Toast.makeText(getActivity(), "This error is case1", Toast.LENGTH_LONG).show();
                    final BottomSheetDialog mBottomDialogNotificationAction = new BottomSheetDialog(getActivity());
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.no_internet, null);
                    mBottomDialogNotificationAction.setContentView(sheetView);
                    mBottomDialogNotificationAction.setCancelable(false);
                    mBottomDialogNotificationAction.show();
                    // Remove default white color background

                    FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    bottomSheet.setBackground(null);


                    TextView close = sheetView.findViewById(R.id.close);
                    Button retry = sheetView.findViewById(R.id.retry);
                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomDialogNotificationAction.dismiss();
                            //spinner.setVisibility(View.VISIBLE);
                            getData(url);
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!doubleBackToExitPressedOnce) {
                                doubleBackToExitPressedOnce = true;
                                Toast toast = Toast.makeText(getActivity(), "Tap on Close App again to exit app", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        doubleBackToExitPressedOnce = false;
                                    }
                                }, 3600);
                            } else {
                                mBottomDialogNotificationAction.dismiss();
                                Intent a = new Intent(Intent.ACTION_MAIN);//will exit app
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);
                            }
                        }

                    });

                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    Toast.makeText(getActivity(), "This error is case2", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Toast.makeText(getActivity(), "This error is server error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Toast.makeText(getActivity(), "This error is case4", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Toast.makeText(getActivity(), "This error is case5", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Token " + token);
                return map;
            }
        };
        requestQueue.add(jsonObjectRequest1);
        jsonObjectRequest1.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int totalCount, pastItemCount, visibleItemCount;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: out DX " + dx + " DY " + dy);
                if (dy > 0) {
                    //added this
                    totalCount = layoutManager.getItemCount();
                    pastItemCount = layoutManager.findFirstVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();
                    //Toast.makeText(getActivity(),"totalCount is "+ totalCount + "pastItemCount is " + pastItemCount + " visibleItemCount" + visibleItemCount,Toast.LENGTH_LONG).show();
                    //if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    if (((pastItemCount + visibleItemCount) >= totalCount) && (pastItemCount >= 0) && (totalCount >= PAGE_SIZE)) {
                        count_entry++;
                        //Toast.makeText(getActivity(),"cleared first if with count "+ count_entry,Toast.LENGTH_LONG).show();
                        //if (!isNextBusy) {
                        //Toast.makeText(getActivity(),"cleared next if",Toast.LENGTH_LONG).show();
                        //pendingUrl.equals(nextPendingUrl);
                        //getData(nextPendingUrl);
                        // progressBar.setVisibility(View.VISIBLE);
                        getNextData(nextPendingUrl);
                        //Toast.makeText(getActivity(),"We reached end of page",Toast.LENGTH_LONG).show();
                        //loadNextLocations();
                        //}
                    }
                }
            }
        });

    }

    public void getNextData(final String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //spinner.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    nextPendingUrl = jsonObject.getString("next");
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDdaName = new ArrayList<>();
                        mAdaName = new ArrayList<>();
                        mAddress = new ArrayList<>();
                        mpkado = new ArrayList<>();
                        mpkdda = new ArrayList<>();
                        mId = new ArrayList<>();
                        JSONObject cd = jsonArray.getJSONObject(i);
                        String mdate = cd.getString("date");
                        JSONArray jsonArray_locations = cd.getJSONArray("locations");
                        for (int j = 0; j < jsonArray_locations.length(); j++) {
                            JSONObject c = jsonArray_locations.getJSONObject(j);
                            try {
                                aid = c.getString("id");
                                mId.add(aid);
                            } catch (JSONException e) {
                                mId.add("null");
                            }
                            try {
                                JSONObject adoobj = c.getJSONObject("ado");
                                JSONObject authado = adoobj.getJSONObject("auth_user");
                                mpkado.add(authado.getString("pk"));
                            } catch (JSONException e) {
                                mpkado.add("null");
                            }
                            try {
                                JSONObject ddaobj = c.getJSONObject("dda");
                                JSONObject authddo = ddaobj.getJSONObject("auth_user");
                                mpkdda.add(authddo.getString("pk"));
                            } catch (JSONException e) {
                                mpkdda.add("null");
                            }
                            villagename = c.getString("village_name");
                            blockname = c.getString("block_name");
                            district = c.getString("district");
                            try {
                                JSONObject mDdaObject = c.getJSONObject("dda");
                                String ddaName = mDdaObject.getString("name");
                                mDdaName.add(ddaName);
                            } catch (JSONException e) {
                                mDdaName.add("Not Assigned");
                            }
                            try {
                                JSONObject mAdoObject = c.getJSONObject("ado");
                                String adoName = mAdoObject.getString("name");
                                mAdaName.add(adoName);
                            } catch (JSONException e) {
                                mAdaName.add("Not Assigned");
                            }
                            mAddress.add(villagename.toUpperCase() + ", " +
                                    blockname.toUpperCase() + ", " + district.toUpperCase());
                        }
                        sections.add(new Section(mdate, mDdaName, mAdaName, mAddress, mId, mpkado, mpkdda, true, false, false));
                    }
                    spinner.setVisibility(View.GONE);
                    // progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    //  progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "An exception occurred", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the reuest has either time out or there is no connection
                    //Toast.makeText(getActivity(), "This error is case1", Toast.LENGTH_LONG).show();
                    final BottomSheetDialog mBottomDialogNotificationAction = new BottomSheetDialog(getActivity());
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.no_internet, null);
                    mBottomDialogNotificationAction.setContentView(sheetView);
                    mBottomDialogNotificationAction.setCancelable(false);
                    mBottomDialogNotificationAction.show();

                    // Remove default white color background
                    FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    bottomSheet.setBackground(null);
                    TextView close = sheetView.findViewById(R.id.close);
                    Button retry = sheetView.findViewById(R.id.retry);

                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomDialogNotificationAction.dismiss();
                            //   spinner.setVisibility(View.VISIBLE);
                            getNextData(url);
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!doubleBackToExitPressedOnce) {
                                doubleBackToExitPressedOnce = true;
                                Toast toast = Toast.makeText(getActivity(), "Tap on Close App again to exit app", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        doubleBackToExitPressedOnce = false;
                                    }
                                }, 3600);
                            } else {
                                mBottomDialogNotificationAction.dismiss();
                                Intent a = new Intent(Intent.ACTION_MAIN);//will exit app
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);
                            }
                        }

                    });
                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    Toast.makeText(getActivity(), "This error is case2", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Toast.makeText(getActivity(), "This error is server error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Toast.makeText(getActivity(), "This error is case4", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Toast.makeText(getActivity(), "This error is case5", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                }
                // progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Token " + token);
                return map;
            }
        };
        requestQueue.add(jsonObjectRequest1);
        jsonObjectRequest1.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int totalCount, pastItemCount, visibleItemCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: out DX " + dx + " DY " + dy);
                if (dy > 0) {
                    //added this
                    totalCount = layoutManager.getItemCount();
                    pastItemCount = layoutManager.findFirstVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();
                    if (((pastItemCount + visibleItemCount) >= totalCount) && (pastItemCount >= 0) && (totalCount >= PAGE_SIZE)) {
                        count_entry++;
                        if (!nextPendingUrl.equals("null")) {
                            getNextData(nextPendingUrl);
                        }
                    }
                }
            }
        });
    }

   /* private void loadNextLocations() {
        Toast.makeText(getActivity(), "reached load Next function", Toast.LENGTH_LONG).show();
        if (!nextPendingUrl.equals("null")) {
            getNextPendingLocations();
        }
    }*/

    private void getNextPendingLocations() {
        Log.d(TAG, "getNextPendingLocations: inside");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //   progressBar.setVisibility(View.VISIBLE);
        isNextBusy = true;
        final JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, nextPendingUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getActivity(), "entered response function got response " + response.toString(), Toast.LENGTH_LONG).show();
                try {
                    Toast.makeText(getActivity(), "entered try", Toast.LENGTH_LONG).show();
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    nextPendingUrl = jsonObject.getString("next");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        // Toast.makeText(getActivity(), "entered i", Toast.LENGTH_LONG).show();

                        //itemArrayList = new ArrayList<>();
                        mDdaName = new ArrayList<>();
                        mAdaName = new ArrayList<>();
                        mAddress = new ArrayList<>();
                        mpkado = new ArrayList<>();
                        mpkdda = new ArrayList<>();
                        mId = new ArrayList<>();

                        JSONObject cd = jsonArray.getJSONObject(i);
                        String date = cd.getString("date");
                        JSONArray jsonArray_locations = cd.getJSONArray("locations");
                        for (int j = 0; j < jsonArray_locations.length(); j++) {
                            //  Toast.makeText(getActivity(), "entered j", Toast.LENGTH_LONG).show();
                            JSONObject c = jsonArray_locations.getJSONObject(j);
                            try {
                                String aid = c.getString("id");
                                mId.add(aid);
                            } catch (JSONException e) {
                                mId.add("null");
                            }
                            try {
                                JSONObject adoobj = c.getJSONObject("ado");
                                JSONObject authado = adoobj.getJSONObject("auth_user");
                                mpkado.add(authado.getString("pk"));

                            } catch (JSONException e) {
                                mpkado.add("null");
                            }
                            try {
                                JSONObject ddaobj = c.getJSONObject("dda");
                                JSONObject authddo = ddaobj.getJSONObject("auth_user");
                                mpkdda.add(authddo.getString("pk"));
                            } catch (JSONException e) {
                                mpkdda.add("null");
                            }
                            villagename = c.getString("village_name");
                            blockname = c.getString("block_name");
                            district = c.getString("district");
                            try {
                                JSONObject mDdaObject = c.getJSONObject("dda");
                                String ddaName = mDdaObject.getString("name");
                                mDdaName.add(ddaName);
                            } catch (JSONException e) {
                                mDdaName.add("Not Assigned");
                            }
                            try {
                                JSONObject mAdoObject = c.getJSONObject("ado");
                                String adoName = mAdoObject.getString("name");
                                mAdaName.add(adoName);
                            } catch (JSONException e) {
                                mAdaName.add("Not Assigned");
                            }
                            mAddress.add(villagename.toUpperCase() + ", " + blockname.toUpperCase() + ", " + district.toUpperCase());
                            Toast.makeText(getActivity(), "Added mAddress", Toast.LENGTH_LONG).show();
                        }
                        sections.add(new Section("date", mDdaName, mAdaName, mAddress, mId, mpkado, mpkdda, true, false, false));
                        Toast.makeText(getActivity(), "Added a section", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An exception occurred", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onResponse: " + e.getLocalizedMessage());
                    e.printStackTrace();
                    isNextBusy = false;
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the reuest has either time out or there is no connection
                    //Toast.makeText(getActivity(), "This error is case1", Toast.LENGTH_LONG).show();
                    final BottomSheetDialog mBottomDialogNotificationAction = new BottomSheetDialog(getActivity());
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.no_internet, null);
                    mBottomDialogNotificationAction.setContentView(sheetView);
                    mBottomDialogNotificationAction.setCancelable(false);
                    mBottomDialogNotificationAction.show();

                    // Remove default white color background

                    FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    bottomSheet.setBackground(null);


                    TextView close = sheetView.findViewById(R.id.close);
                    Button retry = sheetView.findViewById(R.id.retry);

                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomDialogNotificationAction.dismiss();
                            //spinner.setVisibility(View.VISIBLE);
                            getNextPendingLocations();
                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!doubleBackToExitPressedOnce) {
                                doubleBackToExitPressedOnce = true;
                                Toast toast = Toast.makeText(getActivity(), "Tap on Close App again to exit app", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        doubleBackToExitPressedOnce = false;
                                    }
                                }, 3600);
                            } else {
                                mBottomDialogNotificationAction.dismiss();
                                Intent a = new Intent(Intent.ACTION_MAIN);//will exit app
                                a.addCategory(Intent.CATEGORY_HOME);
                                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(a);
                            }
                        }

                    });


                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    Toast.makeText(getActivity(), "This error is case2", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Toast.makeText(getActivity(), "This error is case3", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Toast.makeText(getActivity(), "This error is case4", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Toast.makeText(getActivity(), "This error is case5", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "An unknown error occurred.", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "onErrorResponse: " + error);
                isNextBusy = false;
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Token " + token);
                return map;
            }
        };
        requestQueue.add(jsonObjectRequest2);
        requestFinished(requestQueue);//this function is defined below
        jsonObjectRequest2.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Toast.makeText(getActivity(), "Volley error " + error, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void requestFinished(RequestQueue queue) {

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {

            @Override
            public void onRequestFinished(Request<Object> request) {
                //    progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void sendNotifications() {
        isSendingNotifications = true;
        String url = "http://18.224.202.135/api/trigger/sms/pending";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: sendNotifications " + response);
                        Toast.makeText(getActivity(), "Notifications Successfully Sent!",
                                Toast.LENGTH_SHORT).show();
                        isSendingNotifications = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError)
                            Toast.makeText(getActivity(), "Check Your Internt Connection Please!",
                                    Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Something went wrong, please try again!",
                                    Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onErrorResponse: sendNotifications " + error);
                        isSendingNotifications = false;
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Token " + token);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart: ");
        spinner = view.findViewById(R.id.pending_progress);
        spinner.setVisibility(View.VISIBLE);
        getData(pendingUrl);
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        //spinner.setVisibility(View.GONE);
//        if (isRefresh) {
//            getFragmentManager().beginTransaction().detach(pending_fragment.this)
//                    .attach(pending_fragment.this).commit();
//            Log.d(TAG, "onResume: REFRESH");
//            isRefresh = false;
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
//        isRefresh = true;
    }
}