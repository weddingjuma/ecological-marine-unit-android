/* Copyright 2016 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For additional information, contact:
 * Environmental Systems Research Institute, Inc.
 * Attn: Contracts Dept
 * 380 New York Street
 * Redlands, California, USA 92373
 *
 * email: contracts@esri.com
 *
 */


package com.esri.android.ecologicalmarineunitexplorer;

import android.databinding.tool.util.L;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.esri.android.ecologicalmarineunitexplorer.R;
import com.esri.android.ecologicalmarineunitexplorer.chartSummary.SummaryChartFragment;
import com.esri.android.ecologicalmarineunitexplorer.chartSummary.SummaryChartPresenter;
import com.esri.android.ecologicalmarineunitexplorer.data.DataManager;
import com.esri.android.ecologicalmarineunitexplorer.data.EMU;
import com.esri.android.ecologicalmarineunitexplorer.data.WaterColumn;
import com.esri.android.ecologicalmarineunitexplorer.map.MapFragment;
import com.esri.android.ecologicalmarineunitexplorer.map.MapPresenter;
import com.esri.android.ecologicalmarineunitexplorer.summary.SummaryFragment;
import com.esri.android.ecologicalmarineunitexplorer.summary.SummaryPresenter;
import com.esri.android.ecologicalmarineunitexplorer.util.ActivityUtils;
import com.esri.android.ecologicalmarineunitexplorer.watercolumn.WaterColumnFragment;
import com.esri.android.ecologicalmarineunitexplorer.watercolumn.WaterColumnPresenter;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements WaterColumnFragment.OnWaterColumnSegmentClickedListener,
    SummaryFragment.OnRectangleTappedListener, SummaryFragment.OnDetailClickedListener {


  private SummaryPresenter mSummaryPresenter;
  private DataManager mDataManager;
  private WaterColumnPresenter mWaterColumnPresenter;

  public MainActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Get data access setup
    mDataManager = DataManager.getDataManagerInstance(getApplicationContext());

    // Set up fragments
    setUpMagFragment();
  }

  /**
   * Configure the map fragment
   */
  private void setUpMagFragment(){

    // Set up the toolbar for the map fragment
    setUpMapToolbar();

    final FragmentManager fm = getSupportFragmentManager();

    MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);

    if (mapFragment == null) {
      mapFragment = MapFragment.newInstance();
      MapPresenter mapPresenter = new MapPresenter(mapFragment, mDataManager);
      ActivityUtils.addFragmentToActivity(
          getSupportFragmentManager(), mapFragment, R.id.map_container, "map fragment");
    }
  }

  /**
   * Override the application label used for the toolbar title
   */
  private void setUpMapToolbar() {
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    ((AppCompatActivity) this).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
    actionBar.setTitle("Explore An Ocean Location");
    toolbar.setNavigationIcon(null);

  }
  /**
   * Set up toolbar for chart detail
   * @param EMUid - integer representing the name of the EMU
   */
  private void setUpChartSummaryToolbar(int EMUid){
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    ((AppCompatActivity) this).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
    actionBar.setTitle("Detail for EMU " + EMUid);
  }

  /**
   * Set the text for the summary toolbar and listen
   * for navigation requests
   */
  private void setUpSummaryToolbar() {
    final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
    ((AppCompatActivity) this).setSupportActionBar(toolbar);
    ((AppCompatActivity) this).getSupportActionBar().setTitle(R.string.ocean_summary_location_title);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        //Remove summary and water column
        removeSummaryAndWaterColumnViews();

        //Reconstitute the large map
        reconstituteLargeMap();

      }
    });

  }
  private void removeSummaryAndWaterColumnViews(){
    final FragmentManager fm = getSupportFragmentManager();
    SummaryFragment summaryFragment = (SummaryFragment) fm.findFragmentById(R.id.summary_container) ;
    if (summaryFragment != null ) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.remove(summaryFragment);
      transaction.commit();
    }
    WaterColumnFragment waterColumnFragment = (WaterColumnFragment) fm.findFragmentById(R.id.column_container);
    if (waterColumnFragment != null){
      FragmentTransaction waterTransaction = getSupportFragmentManager().beginTransaction();
      waterTransaction.remove(waterColumnFragment);
      waterTransaction.commit();
    }
  }

  private void removeMapView(){
    final FragmentManager fm = getSupportFragmentManager();
    MapFragment mapFragment =  (MapFragment) fm.findFragmentById(R.id.map_container);
    if (mapFragment != null){
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.remove(mapFragment);
      transaction.commit();
    }
  }

  private void reconstituteLargeMap(){
    final FragmentManager fm = getSupportFragmentManager();
    MapFragment mapFragment =  (MapFragment) fm.findFragmentById(R.id.map_container);
    if (mapFragment != null){
      mapFragment.resetMap();
    }

    FrameLayout mapLayout = (FrameLayout) findViewById(R.id.map_container);
    LinearLayout.LayoutParams  layoutParams  =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    mapLayout.setLayoutParams(layoutParams);
    mapLayout.requestLayout();

    // Set the toolbar title and remove navigation
    setUpMapToolbar();
  }

  /**
   * Add the WaterColumnFragment and SummaryFragment to
   * the view while shrinking the map view.
   * @param waterColumn
   */
  public void showSummary(WaterColumn waterColumn){

    final FragmentManager fm = getSupportFragmentManager();
    SummaryFragment summaryFragment = (SummaryFragment) fm.findFragmentById(R.id.summary_container) ;

    if (summaryFragment == null){
      summaryFragment = SummaryFragment.newInstance();
      mSummaryPresenter = new SummaryPresenter(summaryFragment);
    }
    // Set up the summary toolbar
    setUpSummaryToolbar();

    mSummaryPresenter.setWaterColumn(waterColumn);

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    // Adjust the map's layout
    FrameLayout mapLayout = (FrameLayout) findViewById(R.id.map_container);
    LinearLayout.LayoutParams  layoutParams  =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,7);
    layoutParams.setMargins(0, 0,36,0);
    mapLayout.setLayoutParams(layoutParams);
    mapLayout.requestLayout();

    FrameLayout summaryLayout = (FrameLayout) findViewById(R.id.summary_container);
    summaryLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,9));
    summaryLayout.requestLayout();

    // Replace whatever is in the summary_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
    transaction.replace(R.id.summary_container, summaryFragment);
    transaction.addToBackStack("summary fragment");

    // Commit the transaction
    transaction.commit();

    WaterColumnFragment waterColumnFragment = (WaterColumnFragment) fm.findFragmentById(R.id.column_container);
    if (waterColumnFragment == null){
      waterColumnFragment = WaterColumnFragment.newInstance();
      mWaterColumnPresenter = new WaterColumnPresenter(waterColumnFragment);
    }


    FragmentTransaction wcTransaction = getSupportFragmentManager().beginTransaction();
    wcTransaction.replace(R.id.column_container, waterColumnFragment);
    wcTransaction.commit();
    mWaterColumnPresenter.setWaterColumn(waterColumn);
  }

  public void showSummaryDetail(int emuName){
    // Remove summary and water column
    removeSummaryAndWaterColumnViews();

    // Remove map
    removeMapView();

    FrameLayout layout = (FrameLayout) findViewById(R.id.chartContainer);
    layout.setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    layout.requestLayout();

    // Add the chart view to the column container
    final FragmentManager fm = getSupportFragmentManager();
    SummaryChartPresenter presenter = null;
    SummaryChartFragment chartFragment = (SummaryChartFragment) fm.findFragmentById(R.id.chartContainer);
    if (chartFragment == null){
      chartFragment = SummaryChartFragment.newInstance();
      presenter = new SummaryChartPresenter(chartFragment, mDataManager);
    }
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.chartContainer, chartFragment);
    transaction.commit();

    chartFragment.setPresenter(presenter);
    presenter.getDetailForSummary(emuName);



  }
  /**
   * When a water column segment is tapped, show the
   * associated item in the SummaryFragment
   * @param position - index of the recycler view to display
   */
  @Override public void onSegmentClicked(int position) {
    final FragmentManager fm = getSupportFragmentManager();
    SummaryFragment summaryFragment = (SummaryFragment) fm.findFragmentById(R.id.summary_container) ;
    if (summaryFragment != null){
      summaryFragment.scrollToSummary(position);
    }
  }

  @Override public void onRectangleTap(int position) {
    final FragmentManager fm = getSupportFragmentManager();
    WaterColumnFragment waterColumnFragment = (WaterColumnFragment) fm.findFragmentById(R.id.column_container);
    if (waterColumnFragment != null ){
      waterColumnFragment.highlightSegment(position);
    }
  }

  @Override public void onButtonClick(int emuName) {
      showSummaryDetail(emuName);
  }
}