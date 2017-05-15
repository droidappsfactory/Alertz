package com.droidappsfactory.alertz.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidappsfactory.alertz.R;
import com.droidappsfactory.alertz.adapter.ArticleListAdapterCustom;
import com.droidappsfactory.alertz.provider.AlertProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by tcsmans on 3/21/2017.
 */

public class AlertzList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    CoordinatorLayout coordinatorLayout;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    OnAlertAddListener listener;
    SetAlert.OnAlarmSetListner setListner;
    private final static int LOADER_ID = 0;

    ArticleListAdapterCustom articleListAdapterCustom;

    FirebaseAnalytics analytics;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_list,container,false);
        coordinatorLayout = (CoordinatorLayout)rootView.findViewById(R.id.coordinatorlt);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.alertz_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        floatingActionButton = (FloatingActionButton)rootView.findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAddAlert();
                listener.onAddClickListener();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    floatingActionButton.hide();
                else if (dy < 0)
                    floatingActionButton.show();
            }
        });

        return rootView;

    }

    private void recordAddAlert() {

        Bundle bundle =new Bundle();
        bundle.putString("FABBUTTON","Clicked");
        bundle.putString("ADDAlERT","Add Alert");

        analytics.logEvent("NEW_ALERT",bundle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
        articleListAdapterCustom = new ArticleListAdapterCustom(getActivity(),null,setListner);
        recyclerView.setAdapter(articleListAdapterCustom);

    }

    public void setListener(OnAlertAddListener listener, SetAlert.OnAlarmSetListner setListner) {
        this.listener = listener;
        this.setListner = setListner;
    }

    public static AlertzList newInstance(OnAlertAddListener listener, SetAlert.OnAlarmSetListner setListner) {
        
        Bundle args = new Bundle();
        
        AlertzList fragment = new AlertzList();
        fragment.setArguments(args);
        fragment.setListener(listener,setListner);
        return fragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), AlertProvider.CONTENT_ALERTZ_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(articleListAdapterCustom==null){
            articleListAdapterCustom = new ArticleListAdapterCustom(getActivity(),cursor,setListner);
            recyclerView.setAdapter(articleListAdapterCustom);
        }
        articleListAdapterCustom.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        articleListAdapterCustom.swapCursor(null);
    }

    public interface OnAlertAddListener{
        void onAddClickListener();
    }
}
