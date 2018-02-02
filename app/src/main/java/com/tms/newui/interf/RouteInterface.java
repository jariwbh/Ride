package com.tms.newui.interf;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by viren on 7/12/17.
 */

public interface RouteInterface {
  void  onRouteLoad(List<List<HashMap<String, String>>> result, JSONObject distance, JSONObject duration);
  void  onRouteLoadFailed();
}
