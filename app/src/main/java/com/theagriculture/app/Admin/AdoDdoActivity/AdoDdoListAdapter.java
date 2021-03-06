package com.theagriculture.app.Admin.AdoDdoActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.theagriculture.app.Admin.CompleteDetailsFragment;
import com.theagriculture.app.Ado.ReviewReport;
import com.theagriculture.app.Ado.ado_map_activity;
import com.theagriculture.app.R;
import com.theagriculture.app.adapter.PendingAdapter;

import java.util.ArrayList;

public class AdoDdoListAdapter extends RecyclerView.Adapter<AdoDdoListAdapter.AdoListHolder> {

    private static final String TAG = "AdoListAdapter";
    private ArrayList<String> mtextview1;
    private ArrayList<String> mtextview2;
    private ArrayList<String> longitude;
    private ArrayList<String> latitude;
   // public Boolean mshowshimmer = true;
    private ArrayList<String> mAdoName;
    private ArrayList<String> idList;
    private ArrayList<String> mAdoCompleteIds;
    private boolean isDDoAdo;
    private boolean isDDo;
    private boolean isAdoComplete = false;
    private int isDdoTabNo;
    //private int shimmer_count = 5;
    Context mcontext;

    public AdoDdoListAdapter(Context mcontext, ArrayList<String> mtextview1, ArrayList<String> mtextview2, ArrayList<String> idList) {
        this.mcontext = mcontext;
        this.mtextview1 = mtextview1;
        this.mtextview2 = mtextview2;
        isDDoAdo = false;
        isDDo = false;
        this.idList = idList;
    }

    public AdoDdoListAdapter(Context mcontext, ArrayList<String> mtextview1, ArrayList<String> mtextview2, boolean isAdoComplete, ArrayList<String> mAdoCompleteIds) {
        this.mtextview1 = mtextview1;
        this.mtextview2 = mtextview2;
        this.mAdoCompleteIds = mAdoCompleteIds;
        this.isAdoComplete = isAdoComplete;
        isDDoAdo = true;
        this.mcontext = mcontext;
    }

    public AdoDdoListAdapter(Context mcontext, ArrayList<String> mtextview1, ArrayList<String> mtextview2, ArrayList<String> mAdoName, boolean isDDo) {
        this.mtextview1 = mtextview1;
        this.mtextview2 = mtextview2;
        this.mAdoName = mAdoName;
        this.isDDo = isDDo;
        isDDoAdo = true;
        this.mcontext = mcontext;
        isDdoTabNo = 1;
    }

    public AdoDdoListAdapter(Context mcontext, ArrayList<String> mtextview1, ArrayList<String> mtextview2, ArrayList<String> mAdoName, ArrayList<String> mIds, boolean isDDo, int isDdoTabNo) {
        this.mtextview1 = mtextview1;
        this.mtextview2 = mtextview2;
        this.mAdoName = mAdoName;
        this.isDDo = isDDo;
        isDDoAdo = true;
        this.mcontext = mcontext;
        idList = mIds;
        this.isDdoTabNo = isDdoTabNo;
    }

    /*public AdoDdoListAdapter(FragmentActivity activity, nothing_toshow_fragment no_data) {
        this.mcontext = activity;
        change_fragemnt(no_data);
    }*/

    private void change_fragemnt(nothing_toshow_fragment no_data) {
        AppCompatActivity activity = (AppCompatActivity) no_data.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, no_data).addToBackStack(null).commit();
    }

    @NonNull
    @Override
    public AdoDdoListAdapter.AdoListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.ado_location_listview_admin, parent, false);

        final AdoListHolder adoListHolder = new AdoListHolder(view);
        /*
        adoListHolder.Adolistlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (!mshowshimmer) {
                    //FOR ADO -> PENDING
                    if (!isDDoAdo) {
                        Intent intent = new Intent(mcontext, ado_map_activity.class);
                        int position = adoListHolder.getAdapterPosition();
                        Log.d(TAG, "onClick: ");
                        intent.putExtra("longitude", longitude.get(position));
                        intent.putExtra("latitude", latitude.get(position));
                        intent.putExtra("id", idList.get(position));
                        intent.putExtra("title", mtextview1.get(position));
                        intent.putExtra("village_name", mtextview1.get(position));
                        mcontext.startActivity(intent);
                    }

                    //FOR ADMIN -> DDA -> ONGOING AND COMPLETED
                    if (isDDo) {
                        switch (isDdoTabNo) {
                            case 1:
                                break;
                            case 2:
                            case 3:
                                Intent intent = new Intent(mcontext, ReviewReport.class);
                                int position = adoListHolder.getAdapterPosition();
                                String id = idList.get(position);
                                intent.putExtra("id", id);
                                intent.putExtra("isDdo", true);
                                mcontext.startActivity(intent);
                                break;
                        }
                    }
                    //FOR ADMIN -> ADO -> COMPLETED
                    if (!isDDo && isDdoTabNo == 3) {
                        Intent intent = new Intent(mcontext, ReviewReport.class);
                        int position = adoListHolder.getAdapterPosition();
                        String id = idList.get(position);
                        intent.putExtra("id", id);
                        intent.putExtra("isDdo", false);
                        mcontext.startActivity(intent);
                    }
                    //FOR ADO -> COMPLETED
                    if (isAdoComplete) {
                        Intent intent = new Intent(mcontext, ReviewReport.class);
                        int position = adoListHolder.getAdapterPosition();
                        String id = mAdoCompleteIds.get(position);
                        intent.putExtra("id", id);
                        intent.putExtra("isDdo", false);
                        mcontext.startActivity(intent);
                    }
                }
           // }
        });
        return adoListHolder;

         */
        return adoListHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdoDdoListAdapter.AdoListHolder holder, int position) {
        /*
        if (mshowshimmer) {
            holder.shimmerFrameLayout.startShimmer();
        } else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);

         */
            holder.tv1.setBackground(null);
            holder.tv2.setBackground(null);
            holder.tv1.setText(mtextview1.get(position));
            holder.tv2.setText(mtextview2.get(position));
            holder.tv3.setText(String.valueOf(mtextview1.get(position).charAt(0)));
            Log.d(TAG, "onBindViewHolder: ");
            if (isDDo) {
                holder.mAdoName.setText(mAdoName.get(position));
                holder.mAdoName.setVisibility(View.VISIBLE);
                holder.mAdoName.setBackground(null);
            }
            Log.d(TAG, "onBindViewHolder: error in this");

        //}

    }

    @Override
    public int getItemCount() {
        //return mshowshimmer ? shimmer_count : mtextview1.size();
        return  mtextview1.size();
    }

    public class AdoListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv1;
        TextView tv2;
        TextView tv3;
        RelativeLayout Adolistlayout;
        //ShimmerFrameLayout shimmerFrameLayout;
        TextView mAdoName;

        public AdoListHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            Adolistlayout = itemView.findViewById(R.id.ado_loaction_parent);
            tv1 = itemView.findViewById(R.id.lname);
            tv2 = itemView.findViewById(R.id.laddress);
            tv3 = itemView.findViewById(R.id.img);
            //shimmerFrameLayout = itemView.findViewById(R.id.ado_location_shimmer);
            mAdoName = itemView.findViewById(R.id.ado_name);
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(mcontext,"You clicked",Toast.LENGTH_LONG).show();

            if (!isDDoAdo) {
                Intent intent = new Intent(mcontext, ado_map_activity.class);
                int position = this.getAdapterPosition();
                Log.d(TAG, "onClick: ");
                intent.putExtra("longitude", longitude.get(position));
                intent.putExtra("latitude", latitude.get(position));
                intent.putExtra("id", idList.get(position));
                intent.putExtra("title", mtextview1.get(position));
                intent.putExtra("village_name", mtextview1.get(position));
                mcontext.startActivity(intent);
            }

            //FOR ADMIN -> DDA -> ONGOING AND COMPLETED
            if (isDDo) {
                switch (isDdoTabNo) {
                    case 1:
                        break;
                    case 2:
                    case 3:
                        /*
                        Intent intent = new Intent(mcontext, ReviewReport.class);
                        int position = this.getAdapterPosition();
                        String id = idList.get(position);
                        intent.putExtra("id", id);
                        intent.putExtra("isDdo", true);
                        mcontext.startActivity(intent);

                         */
                        Bundle bundle = new Bundle();
                        bundle.putString("id", idList.get(this.getAdapterPosition()));
                        bundle.putString("review_address_top",mtextview1.get(this.getAdapterPosition())+", "+mtextview2.get(this.getAdapterPosition()));
                        CompleteDetailsFragment abc = new CompleteDetailsFragment();
                        abc.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,abc).addToBackStack(null).commit();
                        break;
                }
            }
            //FOR ADMIN -> ADO -> COMPLETED
            if (!isDDo && isDdoTabNo == 3) {
                Bundle bundle = new Bundle();
                bundle.putString("id", idList.get(this.getAdapterPosition()));
                bundle.putString("review_address_top",mtextview1.get(this.getAdapterPosition())+", "+mtextview2.get(this.getAdapterPosition()));
                CompleteDetailsFragment abc = new CompleteDetailsFragment();
                abc.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,abc).addToBackStack(null).commit();
                /*
                Intent intent = new Intent(mcontext, ReviewReport.class);
                int position =this.getAdapterPosition();
                String id = idList.get(position);
                intent.putExtra("id", id);
                intent.putExtra("isDdo", false);
                mcontext.startActivity(intent);

                 */
            }
            //FOR ADO -> COMPLETED
            if (isAdoComplete) {
                /*
                Intent intent = new Intent(mcontext, ReviewReport.class);
                int position = this.getAdapterPosition();
                String id = mAdoCompleteIds.get(position);
                intent.putExtra("id", id);
                intent.putExtra("isDdo", false);
                mcontext.startActivity(intent);

                 */
                Bundle bundle = new Bundle();
                bundle.putString("id", idList.get(this.getAdapterPosition()));
                bundle.putString("review_address_top",mtextview1.get(this.getAdapterPosition())+", "+mtextview2.get(this.getAdapterPosition()));
                CompleteDetailsFragment abc = new CompleteDetailsFragment();
                abc.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,abc).addToBackStack(null).commit();
            }


        }
    }

    public void sendPostion(ArrayList<String> longitude, ArrayList<String> latitdue) {
        this.longitude = longitude;
        this.latitude = latitdue;
    }
}
