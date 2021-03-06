/* Copyright 2017 Esri
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

package com.esri.android.ecologicalmarineunitexplorer.data;


import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;

import java.util.List;

/**
 * This interface defines a number of callbacks used
 * throughout the application when calling asynchronous methods.
 */

public interface ServiceApi {
  interface SummaryCallback {
    void onWaterColumnsLoaded(WaterColumn column  );
  }
  interface StatCallback{
    void onStatsLoaded(boolean successFlag);
  }

  interface ColumnProfileCallback{
    void onProfileLoaded(WaterProfile profile);
  }

  interface GeocodingCallback{
    void onGeocodeResult(List<GeocodeResult> results
    );
  }
  interface EMUByDepthCallback{
    void onPolygonsRetrieved( FeatureLayer layer);
  }
}
