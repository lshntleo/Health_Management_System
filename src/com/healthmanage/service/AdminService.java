package com.healthmanage.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import com.healthmanage.model.Attendance;
import com.healthmanage.model.Coupon;
import com.healthmanage.model.Gym;
import com.healthmanage.model.Person;
import com.healthmanage.model.User;

import com.healthmanage.config.EnvConfig;
import com.healthmanage.dao.AdminDAO;
import com.healthmanage.model.Coupon;
import com.healthmanage.model.Gym;
import com.healthmanage.model.Person;
import com.healthmanage.view.AdminView;
import com.healthmanage.utils.FileIO;

import com.healthmanage.utils.SHA256;
import com.healthmanage.utils.Sort;
import com.healthmanage.utils.Time;


public class AdminService {
	private CouponService couponservice;
	private static AdminService instance;
	private List<Attendance> attendanceList = new ArrayList<>();

	private AdminView adminView;
	Time time = Time.getInstance();
	private AdminDAO adminDAO;

	
	private AdminService() {
		this.couponservice = CouponService.getInstance();
		this.adminDAO = new AdminDAO();

	}

	public static AdminService getInstance() {
		if (instance == null) {
			instance = new AdminService();
		}
		return instance;
	}
	

	// 회원 이름순 정렬 후 전체조회
	public Collection<User> memberList() {
	      List<User> users = Sort.sortUser(Gym.users.values());
	      return users;
}

	public void load() {
		adminDAO.loadAdmins(EnvConfig.get("ADMIN_FILE"));

	}
	
	public void save() {
		adminDAO.saveAdmins();
	}





	public String memberSearch(String memberNum) { // 회원 검색조회
		if (Gym.users.containsKey(memberNum)) {
			return Gym.users.get(memberNum).toString();
		} else {
			return null;
		}
	}

	public void pwChange(String memberNum, String pw){ //비밀번호 수정
		//로그인 상태에서 비밀번호 입력받아 맞는지 확인
		//기존 비밀번호가 맞으면 새로운 비밀번호 변경
		String hashedPw = SHA256.encrypt(pw);

		
		if(!Gym.users.get(memberNum).getPassword().equals(hashedPw)) {
			adminView.showMessage("비밀번호가 올바르지 않습니다.");
			return;
		}

		String newPw = adminView.getInput("새로운 비밀번호를 입력하세요.");
		String newHashedPw = SHA256.encrypt(newPw);
		Gym.users.get(memberNum).setPassword(newHashedPw);
		adminView.showMessage("비밀번호가 성공적으로 변경되었습니다.");


	}
	
	public void memberDelete(String memberNum) { // 삭제
		Gym.users.remove(memberNum);
	}

	public boolean adminLogin(String adminId, String pw) {
		String hashedPw = SHA256.encrypt(pw);

		if (!Gym.admins.containsKey(adminId)) {
			System.out.println("없는 아이디입니다.");
			return false;
		}

		else {
			if (!Gym.admins.get(adminId).getPassword().equals(hashedPw)) {
				System.out.println("비밀번호가 일치하지 않습니다.");
				return false;
			} else {
				System.out.println("로그인 성공");
				return true;
			}
		}

	}


	// 영어 소문자+숫자, 5~12자
	public boolean isValidId(String adminId) {
		return Pattern.matches("^[a-z0-9]{5,12}$", adminId);
	}

	// 8~16자, 대문자,숫자,소문자영문,특수문자 1개 이상 포함
	public boolean isValidPw(String adminPw) {
		return Pattern.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$", adminPw);
	}

	public Collection<Coupon> findAllCoupon() {
		return couponservice.findAllCoupons();
	}

	public void addCoupon(String number, int coinAmount) {
		couponservice.createCoupon(number, coinAmount);
	}

	public String deleteCoupon(String number) {
		Coupon coupon = couponservice.deleteCoupon(number);
		if (coupon == null) {
			return "삭제 실패 - 없는 쿠폰번호 입니다.";
		}
		return coupon.toString() + "삭제";
	}
	
	// 회원 운동시간 누적기준 정렬
	public Map<String, String> getRank() {
		// attendance list 받아오기
	      
        // 시간 계산하기
        Map<String, String> tmpList = new HashMap<>();
        	            
        for (int i = 0; i < attendanceList.size(); i++) {
           String tmpId = attendanceList.get(i).getUserId();
           String tmpTime = attendanceList.get(i).getWorkOutTime();
           
           if (!tmpList.containsKey(tmpId)) {
        	   tmpList.put(tmpId, tmpTime);
           }
           else {
              String existingTime = tmpList.get(tmpId);
              Duration duration1 = time.totalDuration(existingTime);
              Duration duration2 = time.totalDuration(tmpTime);
              
              tmpList.replace(tmpId, duration1.plus(duration2).toString());
           }
           
        }
        
        // attendance list 넘겨주기
        Map<String, String> sortedList = Sort.sortRank2(tmpList);
        
        return sortedList;
	}

}
