package com.esri.android.ecologicalmarineunitexplorer.waterprofile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.esri.android.ecologicalmarineunitexplorer.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CombinedData;

import java.util.ArrayList;
import java.util.List;

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

public class ChartFragment extends Fragment {

  private TextView mTxtChartTitle;
  private TextView mTxtXAxisTitle;
  private CombinedChart mChart;
  private CombinedData mData;


  public static ChartFragment newInstance(int tabNumber, CombinedData combinedData) {
    ChartFragment fragment = new ChartFragment();;
    fragment.setChartData(combinedData);
    return fragment;
  }
  @Override
  @Nullable
  public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup container,
      final Bundle savedInstance){
    View view = layoutInflater.inflate(R.layout.water_profile, container, false);
    mTxtChartTitle = (TextView) view.findViewById(R.id.txtChartTitle);
    mTxtXAxisTitle = (TextView) view.findViewById(R.id.txtXAxisTitle);
    mChart = (CombinedChart) view.findViewById(R.id.propertyChart);
    mChart.setDrawOrder(new CombinedChart.DrawOrder[]{ CombinedChart.DrawOrder.LINE,
        CombinedChart.DrawOrder.SCATTER
    });

    if (mData !=  null){
      showChart();
    }
    return view;
  }

  /**
   * Set the data for the chart
   * @param data - CombinedData item used by the chart
   */
  public void setChartData(CombinedData data) {
    mData = data;
  }

  /**
   * Display the chart view
   */
  private void showChart(){
    String [] labels = mData.getDataSetLabels();
    String property = mData.getDataSetLabels()[labels.length -1];
    mTxtChartTitle.setText( property + " vs. Ocean Depth");
    mChart.setData(mData);
    mChart.getAxisLeft().setInverted(true);
    mChart.getXAxis().setEnabled(true);
    mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    mChart.getXAxis().setAxisMinValue(mData.getXMin()-1);
    mChart.getXAxis().setAxisMaxValue(mData.getXMax() + 1);
    mChart.getAxisRight().setEnabled(false);
    mChart.getAxisLeft().setDrawGridLines(true);
    mChart.setDrawGridBackground(true);
    mChart.setDescription("");
    mChart.getLegend().setEnabled(false);
    mChart.setData(mData);
    mChart.invalidate();
    if (property.equalsIgnoreCase("TEMPERATURE")){
      property = property + " \u2103";
    }else if (property.equalsIgnoreCase("SALINITY")){
      property = property + " ppm";
    }else{
      property = property + " \u00b5" + "m/L";
    }
    mTxtXAxisTitle.setText(property);
  }
}
