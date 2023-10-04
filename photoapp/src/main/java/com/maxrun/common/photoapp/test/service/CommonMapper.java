package com.maxrun.common.photoapp.test.service;
		
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {

    public List<Map<String, Object>> getPointScoreList(Map<String, Object> param);
    public List<Map<String, Object>> getPointDetailsList(Map<String, Object> param);
    public List<Map<String, Object>> getIncentiveRangkingList(Map<String, Object> param);
    public List<Map<String, Object>> getWorkList(Map<String, Object> param);
    public List<Map<String, Object>> getMamaFeeList(Map<String, Object> param);
    public Map<String, Object> getRealIncentive(Map<String, Object> param);   
}
