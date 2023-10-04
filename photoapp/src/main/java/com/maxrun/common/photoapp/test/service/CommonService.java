package com.maxrun.common.photoapp.test.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class CommonService {

	@Autowired
    CommonMapper commonMapper;

    public List<Map<String, Object>> getPointScoreList(Map<String, Object> map) {
    	return commonMapper.getPointScoreList(map);
    }
    
    public List<Map<String, Object>> getPointDetailList(Map<String, Object> map) {
    	return commonMapper.getPointDetailsList(map);
    }
    
    public List<Map<String, Object>> getIncentiveRangkingList(Map<String, Object> map){
    	return commonMapper.getIncentiveRangkingList(map);
    }
    
    public List<Map<String, Object>> getWorkList(Map<String, Object> param){
    	return commonMapper.getWorkList(param);
    }
    
    public List<Map<String, Object>> getMamaFeeList(@RequestParam Map<String, Object> param) {
    	return commonMapper.getMamaFeeList(param);
    }
    
    public Map<String, Object> getRealIncentive(@RequestParam Map<String,Object> param){
    	return commonMapper.getRealIncentive(param);
    }
}
