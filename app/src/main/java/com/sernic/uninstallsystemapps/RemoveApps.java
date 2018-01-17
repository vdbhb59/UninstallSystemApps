package com.sernic.uninstallsystemapps;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

import java.util.ArrayList;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by nicola on 25/12/17.
 */

public class RemoveApps extends AsyncTask <Void, Integer, Void> {
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private CircleProgressView mCircleProgressView;
    private ArrayList<App> mApps;
    private ArrayList<App> temp;
    private FloatingActionButton fab;
    private int totalAppSelected;

    public RemoveApps(MainActivity mActivity) {
        this.mActivity = mActivity;
        mApps = mActivity.getApplicationList();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        temp = new ArrayList<>();
        //totalAppSelected = ((MyAdapter)mRecyclerView.getAdapter()).getCheckOneApp();
        fab = (FloatingActionButton) mActivity.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)mActivity.findViewById(R.id.my_recycler_view);
        // Imposto i parametri dell'animazione iniziale
        mCircleProgressView = (CircleProgressView)mActivity.findViewById(R.id.circleView);
        mCircleProgressView.setSeekModeEnabled(false);
        mCircleProgressView.setSpinningBarLength(80);
        mCircleProgressView.setSpinSpeed(2);
        mCircleProgressView.setShowTextWhileSpinning(true);
        // Imposto l'animazione percent
        mCircleProgressView.setTextMode(TextMode.PERCENT);
        mCircleProgressView.setUnitVisible(true);
        mCircleProgressView.setValue(0);
        // Nascondo quello che devo e rendo visbile l'animazione

        mRecyclerView.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        mCircleProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        float count = 0;
        for(App app : mApps) {
            if(app.isSelected()) {
                if (app.isSystemApp()) {
                    RootManager.getInstance().uninstallSystemApp(app.getPath());
                } else {
                    RootManager.getInstance().uninstallPackage(app.getPackageName());
                }
                count++;
                publishProgress((int) (count / mApps.size() * 100));
            } else {
                temp.add(app);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //Incremento l'animazione
        mCircleProgressView.setValueAnimated(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Controllo che l'animazione abbbia finito prima di nasconderla
        mCircleProgressView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        if(_animationState == AnimationState.IDLE) {
                            mCircleProgressView.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // Update recyclerView
        ((MyAdapter)mRecyclerView.getAdapter()).updateList(temp);
        // Notify activity
        mActivity.setApplicationList(temp);
        View view = mActivity.findViewById(R.id.coordinatorLayout);
        Snackbar.make(view, "Le app selezionate sono state rimosse!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        // Creo l'allert per riavviare il telefono
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Riavviare ora per completare la disinstallazione?")
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with reboot
                        RootManager.getInstance().restartDevice();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }
}