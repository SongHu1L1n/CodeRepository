package com.company;

import org.junit.Test;

import java.util.*;

public class Main {
    int max, maxIdx;
    public int findPeakElement(int[] nums) {
        max = Integer.MIN_VALUE;
        maxIdx = -1;
        int l = 0, r = nums.length - 1;
        find(nums, l, r);
        return maxIdx;
    }

    void find(int[] nums, int l, int r){
        if(l < 0 || r > nums.length - 1 || l > r){
            return ;
        }
        int mid = l + (r - l) / 2;
        if(mid == l){
            if(nums[mid] > nums[mid + 1]){
                maxIdx = nums[mid] > max ? mid: maxIdx;
                max = Math.max(max, nums[mid]);
            }
        }else if(mid == r){
            if(nums[mid] > nums[mid - 1]){
                maxIdx = nums[mid] > max ? mid: maxIdx;
                max = Math.max(max, nums[mid]);
            }
        }else{
            if(nums[mid] > nums[mid - 1] && nums[mid] > nums[mid + 1]){ // 峰值
                maxIdx = nums[mid] > max ? mid: maxIdx;
                max = Math.max(max, nums[mid]);
                find(nums, l, mid - 2);
                find(nums, mid + 2, r);
            }else if(nums[mid - 1] <= nums[mid] && nums[mid] <= nums[mid + 1]){ // 递增
                find(nums, l, mid - 2);
                find(nums, mid, r);
            }else if(nums[mid - 1] >= nums[mid] && nums[mid] >= nums[mid] + 1){ // 递减
                find(nums, l, mid);
                find(nums, mid + 2, r);
            }
        }
    }
    public int lengthOfLongestSubstring(String s) {
        if(s.length() < 1){
            return 0;
        }
        int[] dp = new int[s.length()];
        Map<Character, Integer> map = new HashMap<>();
        dp[0] = 1;
        map.put(s.charAt(0), 0);
        int max = 1;
        for(int i = 1; i < s.length(); i++){
            if(!map.containsKey(s.charAt(i))){
                dp[i] = dp[i - 1] + 1;
                map.put(s.charAt(0), 1);
            }else{
                int last = map.get(s.charAt(i));
                if(last < i - 1 - dp[i - 1] + 1){
                    dp[i] = dp[i - 1] + 1;
                }else if(last == i - 1 - dp[i - 1] + 1){
                    dp[i] = dp[i - 1];
                }else{
                    dp[i] = i - last;
                }
            }
            max = Math.max(max, dp[i]);
            map.put(s.charAt(i), i);
        }
        return max;
    }

   @Test
   public void test(){
       int[] nums = {3, 1, 3, 1, 1};
       minSessions(nums, 8);



   }
    public int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        Arrays.sort(tasks);
        int cnt = 0;
        int l = 0, r = n - 1;
        while(tasks[r] == sessionTime){
            cnt++;
            r--;
        }
        while(l <= r){
            if(l == r){
                return cnt+1;
            }
            int curSum = tasks[l] + tasks[r];
            if(curSum == sessionTime){
                cnt++;
                l ++;
                r--;
            }else if(curSum > sessionTime){
                r--;
                cnt++;
            }else{
                int tmp = curSum;
                int left = l, right = r;
                while(left < right && tmp + tasks[--right] <= sessionTime){
                    tmp += tasks[right];
                }
                while(left < right && tmp + tasks[++left] <= sessionTime){
                    tmp += tasks[left];
                }
                cnt++;
                l = left;
                r = right;
            }

        }
        return cnt;
    }



    void swap(int[] nums, int i, int j){
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
}
