package edu.boun.edgecloudsim.applications.File_IO;
import org.junit.Test;

import java.io.*;

public class file_io_test {
    String info = "E:\\CodeRepository\\JavaCode\\EdgeCloudSim-master\\scripts\\sample_DRL\\config\\info.txt";
    String result = "E:\\CodeRepository\\JavaCode\\EdgeCloudSim-master\\scripts\\sample_DRL\\config\\result.txt";

    int taskType = 1;
    double speed = 20.0;

    double wlan_up_and_down_load_delay = 0.1;
    double wan_up_and_down_load_delay = 0.1;
    double gsm_up_and_down_load_delay = 0.1;

    double expectedProcessingDelayOnEdge = 0.3;
    double expectedProcessingDelayOnCloud = 0.3;

    @Test
    public void writeMethodOne() throws IOException {
        File file = new File(info);
        if(!file.exists()){
            file.createNewFile();
        }
        System.out.println("fw前文件大小: " + file.length());
        // 文件为空 向其中写入文件
        while (file.length() != 0){
            System.out.println("文件不为空，等待清空后再写入...");
        };
        FileWriter fw = new FileWriter(file);
//        fw.write("");
//        System.out.println("fw后文件大小: " + file.length());  fw后， 文件大小为0
//        System.out.println("写入前文件大小: " + file.length());
        System.out.println("文件写入开始");

        fw.write(String.valueOf(taskType) + "\n");
        fw.write(String.valueOf(speed) + "\n");
        fw.write(String.valueOf(wlan_up_and_down_load_delay) + "\n");
        fw.write(String.valueOf(wan_up_and_down_load_delay) + "\n");
        fw.write(String.valueOf(gsm_up_and_down_load_delay) + "\n");
        fw.write(String.valueOf(expectedProcessingDelayOnEdge) + "\n");
        fw.write(String.valueOf(expectedProcessingDelayOnCloud) + "\n");
        fw.flush();
        fw.close();
        System.out.println("文件写入完成！");
        System.out.println("写入后文件大小: " + file.length());
    }

    @Test
    public void writeMethodTwo() throws IOException {
        File file = new File(info);
        if(!file.exists()){
            file.createNewFile();
        }
        // 写内容
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        bw.write("");
        bw.flush();
        bw.close();

        // 读内容
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null){
            System.out.println(line);
        }
        br.close();
    }
}
