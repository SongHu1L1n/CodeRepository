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

    @Test
    public void test(){
        int[] nums = new int[]{1,2,1,3,5,6,4};
        StringBuilder sb = new StringBuilder();
        char num = '9';
        int a = Integer.parseInt(String.valueOf(num));
        sb.deleteCharAt(sb.length() - 1);
        Set<String> set = new HashSet<>();
        set.contains()
        findPeakElement(nums);
    }
}
