package com.healthmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.healthmanage.model.Weight;
import com.healthmanage.utils.Time;
import com.healthmanage.view.View;

public class WeightService {
    private Map<String, List<Weight>> weightList = new HashMap<>(); //각 사용자의 몸무게 기록을 담을 map
    private static View view;
    private static WeightService instance;
    private Time time;
    private LogService logger;

    public WeightService() {
        view = new View();
        this.time = Time.getInstance();
        this.logger = LogService.getInstance();
    }
    public static WeightService getInstance() {
        if (instance == null) {
            instance = new WeightService();
        }
        return instance;
    }
    //Weight 추가 메서드
    public void addWeight(String userId, String weight) {

        //몸무게, 날짜(입력한 당시 날짜 자동기록) 입력받고 Weight 객체 생성
        String date = time.currentDay();
        Weight userWeight = new Weight(userId, date, weight);

        //user 의 Weight 객체를 리스트에 담기
        weightList.putIfAbsent(userId, new ArrayList<>()); // 기존에 없으면 새로운 리스트 생성-맵에 추가
        weightList.get(userId).add(userWeight);
        
        logger.addLog(userId +"님의 몸무게가 등록되었습니다. : " + weight+" kg");
        view.showMessage("몸무게가 정상 기록되었습니다!");
    }

    //Weight 전체 조회 메서드 - 일단은 그냥 전체 기록 조회...
    public void ListWeight(String userId) {

        //로그인한 user의 id(키 값)을 이용해 map에서 list 꺼내오기
        List<Weight> userWeightList = weightList.get(userId);
        if(userWeightList == null || userWeightList.isEmpty()){
            view.showMessage("아직 몸무게 기록이 없습니다!");
            view.showMessage("몸무게를 입력해주세요.");
        } else {
            for(Weight userWeight : userWeightList){
                view.showMessage(userWeight.toString());
            }
        }
    }

    //Weight 월별 조회 메서드
    public void ListWeightByMonth(String userId, String yearMonth) {
        List<Weight> userWeightList = weightList.get(userId);
        List<Weight> monthWeightList = new ArrayList<Weight>();

        if(userWeightList == null || userWeightList.isEmpty()){
            view.showMessage("아직 몸무게 기록이 없습니다!");
            view.showMessage("몸무게를 입력해주세요.");
        } else {
            view.showMessage("[" + yearMonth + "]");
            for(Weight userWeight : userWeightList){
                if(time.getYearMonthByInput(userWeight.date).equals(yearMonth)){
                    monthWeightList.add(userWeight);
                }
            }

        }

        if(monthWeightList.isEmpty()){
            view.showMessage("검색된 몸무게 기록이 없습니다.");
        }else{
            for(Weight weight : monthWeightList){
                view.showMessage(weight.toString());
            }
        }
    }

    //Weight 일별 조회 메서드
    public void ListWeightByDay(String userId, String date) {
        List<Weight> userWeightList = weightList.get(userId);
        List<Weight> dayWeightList = new ArrayList<Weight>();

        if(userWeightList == null || userWeightList.isEmpty()){
            view.showMessage("아직 몸무게 기록이 없습니다!");
            view.showMessage("몸무게를 입력해주세요.");
        } else {
            view.showMessage("[" + date + "]");
            for(Weight userWeight : userWeightList){
                if(userWeight.date.equals(date)){
                    dayWeightList.add(userWeight);
                }
            }
        }
        if(dayWeightList.isEmpty()){
            view.showMessage("검색된 몸무게 기록이 없습니다.");
        }else{
            for(Weight weight : dayWeightList){
                view.showMessage(weight.toString());
            }
        }
    }
}
