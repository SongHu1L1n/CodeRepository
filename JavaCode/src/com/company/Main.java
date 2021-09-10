package com.company;

import java.util.*;

public class Main {
    public static void main(String[] args) {
	// write your code here
       int num = 10;
//        System.out.println(num / 3);
//        System.out.println(25 % 11);
        List<Integer> list = new ArrayList();
        list.add(1);
        System.out.println(1 == list.get(0));


    }
    public boolean robot(String command, int[][] obstacles, int x, int y) {
        int[] right_now = new int[]{0, 0}, terminal = new int[]{x, y};
        if(x == 0 && y == 0){
            return true;
        }
        // if(obstacles == null || obstacles.length == 0){
        //     for(int i = 0; i < command.length(); i++){
        //         if(command.charAt(i) == 'U'){
        //             right_now[1]++;
        //         }else{
        //             right_now[0]++;
        //         }
        //     }
        //     return right_now[0] == terminal[0] && right_now[1] == terminal[1];
        // }
        Map<Integer, List<Integer>> map = new HashMap<>();
        for(int[] obstacle: obstacles){
            List list =  map.getOrDefault(obstacle[0], new ArrayList<>());
            list.add(obstacle[1]);
            map.put(obstacle[0], list);
        }
        int i = 0;
        while(true){
            if(command.charAt(i) == 'U'){
                right_now[1]++;
            }else{
                right_now[0]++;
            }

            if(map.containsKey(right_now[0])){
                List list = map.get(right_now[0]);
                for(int j = 0; j < list.size(); j++){
                    if(right_now[1] == (int)list.get(j)){
                        return false;
                    }
                }
            }
            if(right_now[0] > terminal[0] || right_now[1] > terminal[1]){
                return false;
            }
            if(right_now[0] == terminal[0] && right_now[1] == terminal[1]){
                return true;
            }
            i = (i + 1) % command.length();
        }

    }
}
