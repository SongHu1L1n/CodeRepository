package edu.boun.edgecloudsim.applications.File_IO;

import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

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
    public void info_write() throws IOException {
        File file = new File(info);
        if(!file.exists()){
            file.createNewFile();
        }
        System.out.println("fw前文件大小: " + file.length());
        // 文件为空 向其中写入文件
        while (file.length() != 0){
            System.out.println("info文件不为空，等待清空后再写入...");
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


    // 联调测试
    @Test
    public void socket_with_python_test(){
        for(int j = 0; j < 10; j++){
            try {
                Socket socket = new Socket("192.168.66.1",7777);
                // 向INFO传输基础信息
                //******************************************************************************************************
                File file = new File(info);
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                int k = 0;
                while (file.length() != 0){
                    k++;
                };
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(String.valueOf(taskType) + "\n");
                    fw.write(String.valueOf(speed) + "\n");
                    fw.write(String.valueOf(wlan_up_and_down_load_delay) + "\n");
                    fw.write(String.valueOf(wan_up_and_down_load_delay) + "\n");
                    fw.write(String.valueOf(gsm_up_and_down_load_delay) + "\n");
                    fw.write(String.valueOf(expectedProcessingDelayOnEdge) + "\n");
                    fw.write(String.valueOf(expectedProcessingDelayOnCloud));
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //******************************************************************************************************


                //获取输出流，向服务器端发送信息
                OutputStream os = socket.getOutputStream();//字节输出流
                PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
                pw.write("我是Java客户端");
                pw.flush();
//                System.out.println(1);
                socket.shutdownOutput();//关闭输出流
//                System.out.println(2);

                InputStream is = socket.getInputStream();
                byte[] b = new byte[1024];
                is.read(b);
                String s = new String(b);
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < s.length(); i++){
                    if(s.charAt(i) == '_' || s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'){
                        sb.append(s.charAt(i));
                    }
                }
                String res = sb.toString();
                System.out.println(j + "-: " + res);


//            BufferedReader in = new BufferedReader(new InputStreamReader(is));
//            System.out.println(3);
//            String info = null;
//                System.out.println(4);
                is.close();
//            in.close();
                socket.close();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test(){
       for(int i = 0; i < 10000; i++){
           socket_with_python_test();
       }
    }

}
