package edu.boun.edgecloudsim.applications.sample_DRL;

//import edu.boun.edgecloudsim.applications.sample_app5.GameTheoryHelper;
//import edu.boun.edgecloudsim.applications.sample_app5.MultiArmedBanditHelper;

//import edu.boun.edgecloudsim.applications.sample_app5.OrchestratorStatisticLogger;
//import edu.boun.edgecloudsim.applications.sample_app5.OrchestratorTrainerLogger;
//import edu.boun.edgecloudsim.applications.sample_app5.VehicularNetworkModel;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.SimEvent;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class VehicularEdgeOrchestrator extends EdgeOrchestrator {

    private static final int BASE = 100000; //start from base in order not to conflict cloudsim tag!
    private static final int UPDATE_PREDICTION_WINDOW = BASE+1;

    public static final int CLOUD_DATACENTER_VIA_GSM = 1;
    public static final int CLOUD_DATACENTER_VIA_RSU = 2;
    public static final int EDGE_DATACENTER = 3;

    private int cloudVmCounter;
    private int edgeVmCounter;
    private int numOfMobileDevice;

    private OrchestratorStatisticLogger statisticLogger;
    private OrchestratorTrainerLogger trainerLogger;

//    private MultiArmedBanditHelper MAB;
//    private GameTheoryHelper GTH;

    public VehicularEdgeOrchestrator(int _numOfMobileDevices, String _policy, String _simScenario) {
        super(_policy, _simScenario);
        this.numOfMobileDevice = _numOfMobileDevices;
    }

    @Override
    public void initialize() {
        cloudVmCounter = 0;
        edgeVmCounter = 0;

        statisticLogger = new OrchestratorStatisticLogger();
        trainerLogger = new OrchestratorTrainerLogger();



        double lookupTable[][] = SimSettings.getInstance().getTaskLookUpTable();
        // assume the first app has the lowest and the last app has the highest task length value
        // 假设第一个应用程序的任务长度值最低，最后一个应用程序的任务长度值最高
        double minTaskLength = lookupTable[0][7];
        double maxTaskLength = lookupTable[lookupTable.length-1][7];
    }

    @Override
    public int getDeviceToOffload(Task task){
        int result = 0;

        double avgEdgeUtilization = SimManager.getInstance().getEdgeServerManager().getAvgUtilization();
        double avgCloudUtilization = SimManager.getInstance().getCloudServerManager().getAvgUtilization();

        VehicularNetworkModel networkModel = (VehicularNetworkModel)SimManager.getInstance().getNetworkModel();
        double wanUploadDelay = networkModel.estimateUploadDelay(SimSettings.NETWORK_DELAY_TYPES.WAN_DELAY, task);
        double wanDownloadDelay = networkModel.estimateDownloadDelay(SimSettings.NETWORK_DELAY_TYPES.WAN_DELAY, task);

        double gsmUploadDelay = networkModel.estimateUploadDelay(SimSettings.NETWORK_DELAY_TYPES.GSM_DELAY, task);
        double gsmDownloadDelay = networkModel.estimateDownloadDelay(SimSettings.NETWORK_DELAY_TYPES.GSM_DELAY, task);

        double wlanUploadDelay = networkModel.estimateUploadDelay(SimSettings.NETWORK_DELAY_TYPES.WLAN_DELAY, task);
        double wlanDownloadDelay =networkModel.estimateDownloadDelay(SimSettings.NETWORK_DELAY_TYPES.WLAN_DELAY, task);

        int options[] = {
                EDGE_DATACENTER,
                CLOUD_DATACENTER_VIA_RSU,
                CLOUD_DATACENTER_VIA_GSM
        };

        if(policy.equals("RANDOM")){
            double probabilities[] = {0.33, 0.33, 0.34};

            double randomNumber = SimUtils.getRandomDoubleNumber(0, 1);
            double lastPercentage = 0;
            boolean resultFound = false;
            for(int i=0; i<probabilities.length; i++) {
                if(randomNumber <= probabilities[i] + lastPercentage) {
                    result = options[i];
                    resultFound = true;
                    break;
                }
                lastPercentage += probabilities[i];
            }
//            System.out.println("任务类型" + task.getTaskType());
            if(!resultFound) {
                SimLogger.printLine("Unexpected probability calculation for random orchestrator! Terminating simulation...");
                System.exit(1);
            }

        }else if (policy.equals("DRL")){

            double SPEED_ON_THE_ROAD[] = {20, 40, 60};
            // 任务延迟要求
            double max_delay_require = SimSettings.getInstance().getTaskLookUpTable()[task.getTaskType()][13];

            //  task.getAssociatedHostId()
            //任务提交位置 task.getSubmittedLocation()
            // System.out.println("任务的X坐标：" + task.getSubmittedLocation().getXPos());
            //  SimManager.getInstance().getEdgeServerManager().getVmList(task.getSubmittedLocation().getPlaceTypeIndex());

            /*
            * 需要传输的数据:
            *   1.任务类型
            *   2.速度
            *   3.三种延迟
            *   4.预计处理时间
            * */

            /*1*/ int taskType = task.getTaskType();
            /*2*/ double speed = SPEED_ON_THE_ROAD[task.getSubmittedLocation().getPlaceTypeIndex()];
            /*3*/
            double wlan_up_and_down_load_delay = wlanUploadDelay + wlanDownloadDelay;
            double wan_up_and_down_load_delay = wanUploadDelay + wanDownloadDelay;
            double gsm_up_and_down_load_delay = gsmUploadDelay + gsmDownloadDelay;
            /*4*/
            double expectedProcessingDelayOnEdge = (double)task.getCloudletLength() / (double)SimManager.getInstance().getEdgeServerManager().getVmList(0).get(0).getMips();
            double expectedProcessingDelayOnCloud = (double) task.getCloudletLength() / (double)SimSettings.getInstance().getMipsForCloudVM(); //云上的预期处理时间
//            System.out.println("task.getCloudletLength(): " + task.getCloudletLength() + ", mips: " +
//                    SimSettings.getInstance().getMipsForCloudVM() + ", expectedProcessingDelayOnCloud: "
//                    + expectedProcessingDelayOnCloud + ", 手动测试结果: " + task.getCloudletLength() / SimSettings.getInstance().getMipsForCloudVM());
//            System.out.println();

//            SimSettings.getInstance().getTaskLookUpTable();
//            SimManager.getInstance().getEdgeServerManager().getVmList(0).

            double[] expectedDelays = {
                    wlanUploadDelay + wlanDownloadDelay + expectedProcessingDelayOnEdge,
                    wanUploadDelay + wanDownloadDelay + expectedProcessingDelayOnCloud,
                    gsmUploadDelay + gsmDownloadDelay + expectedProcessingDelayOnCloud
            };

            // 先建立通信连接, 再传输文件信息，传输完成，从socket接收结
            try {
                Socket socket = new Socket("192.168.66.1",7779);
                // 向INFO传输基础信息
                //******************************************************************************************************
                String info = "E:\\CodeRepository\\JavaCode\\EdgeCloudSim-master\\scripts\\sample_DRL\\config\\info.txt";
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
                socket.shutdownOutput();//关闭输出流

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
                if(res.equals("EDGE_DATACENTER")){
                    result = EDGE_DATACENTER;
                }else if(res.equals("CLOUD_DATACENTER_VIA_RSU")){
                    result = CLOUD_DATACENTER_VIA_RSU;
                }else if(res.equals("CLOUD_DATACENTER_VIA_GSM")){
                    result = CLOUD_DATACENTER_VIA_GSM;
                }
                is.close();
                socket.close();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SimLogger.printLine("Unknow edge orchestrator policy! Terminating simulation...");
            System.exit(1);
        }
        return result;

    }

    @Override
    public Vm getVmToOffload(Task task, int deviceId) {
        Vm selectedVM = null;

        if (deviceId == CLOUD_DATACENTER_VIA_GSM || deviceId == CLOUD_DATACENTER_VIA_RSU) {
            int numOfCloudHosts = SimSettings.getInstance().getNumOfCloudHost();
            int hostIndex = (cloudVmCounter / numOfCloudHosts) % numOfCloudHosts;
            int vmIndex = cloudVmCounter % SimSettings.getInstance().getNumOfCloudVMsPerHost();

            selectedVM = SimManager.getInstance().getCloudServerManager().getVmList(hostIndex).get(vmIndex);

            cloudVmCounter++;
            cloudVmCounter = cloudVmCounter % SimSettings.getInstance().getNumOfCloudVMs();
        }
        else if (deviceId == EDGE_DATACENTER) {
            int numOfEdgeVMs = SimSettings.getInstance().getNumOfEdgeVMs();
            int numOfEdgeHosts = SimSettings.getInstance().getNumOfEdgeHosts();
            int vmPerHost = numOfEdgeVMs / numOfEdgeHosts;

            int hostIndex = (edgeVmCounter / vmPerHost) % numOfEdgeHosts;
            int vmIndex = edgeVmCounter % vmPerHost;

            selectedVM = SimManager.getInstance().getEdgeServerManager().getVmList(hostIndex).get(vmIndex);

            edgeVmCounter++;
            edgeVmCounter = edgeVmCounter % numOfEdgeVMs;
        }
        else {
            SimLogger.printLine("Unknow device id! Terminating simulation...");
            System.exit(1);
        }
        return selectedVM;
    }

    @Override
    public void startEntity() {
        if(policy.equals("PREDICTIVE")) {
            schedule(getId(), SimSettings.CLIENT_ACTIVITY_START_TIME +
                            edu.boun.edgecloudsim.applications.sample_app5.OrchestratorStatisticLogger.PREDICTION_WINDOW_UPDATE_INTERVAL,
                    UPDATE_PREDICTION_WINDOW);
        }
    }

    @Override
    public void shutdownEntity() {
    }


    @Override
    public void processEvent(SimEvent ev) {
        if (ev == null) {
            SimLogger.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null! Terminating simulation...");
            System.exit(1);
            return;
        }

        switch (ev.getTag()) {
            case UPDATE_PREDICTION_WINDOW:
            {
                statisticLogger.switchNewStatWindow();
                schedule(getId(), edu.boun.edgecloudsim.applications.sample_app5.OrchestratorStatisticLogger.PREDICTION_WINDOW_UPDATE_INTERVAL,
                        UPDATE_PREDICTION_WINDOW);
                break;
            }
            default:
                SimLogger.printLine(getName() + ": unknown event type");
                break;
        }
    }

    public void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            SimLogger.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null! Terminating simulation...");
            System.exit(1);
            return;
        }
    }

    public void taskCompleted(Task task, double serviceTime) {
        if(policy.equals("AI_TRAINER"))
            trainerLogger.addSuccessStat(task, serviceTime);

        if(policy.equals("PREDICTIVE"))
            statisticLogger.addSuccessStat(task, serviceTime);

//        if(policy.equals("MAB"))
//            MAB.updateUCB(task, serviceTime);
    }

    public void taskFailed(Task task) {
        if(policy.equals("AI_TRAINER"))
            trainerLogger.addFailStat(task);

        if(policy.equals("PREDICTIVE"))
            statisticLogger.addFailStat(task);

//        if(policy.equals("MAB"))
//            MAB.updateUCB(task, 0);
    }

    public void openTrainerOutputFile() {
        trainerLogger.openTrainerOutputFile();
    }

    public void closeTrainerOutputFile() {
        trainerLogger.closeTrainerOutputFile();
    }

}
