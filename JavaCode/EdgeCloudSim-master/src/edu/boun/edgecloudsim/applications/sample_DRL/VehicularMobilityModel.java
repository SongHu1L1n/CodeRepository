package edu.boun.edgecloudsim.applications.sample_DRL;

import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.mobility.MobilityModel;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VehicularMobilityModel extends MobilityModel {
    // 定义道路速度
    private final double SPEED_ON_THE_ROAD[] = {20, 40, 60};

    private int lengthOfSegment; // 分段长度
    private double totalTimeForLoop; // 循环总时间
    private int[] locationTypes; // 道路类型

    // 准备以下数组以减少 getLocation（）函数的计算量
    // NOTE: 如果客户机数量较多，则在RAM中保留以下值可能会很昂贵。在这种情况下，牺牲计算资源！
    private int[] initialLocationIndexArray; // 初始化位置下标数组
    private int[] initialPositionArray; // 初始位置数组(/m)  in meters unit
    private double[] timeToDriveLocationArray;// 行驶到位置的时间(/s) in seconds unit
    private double[] timeToReachNextLocationArray; // 到达下一个位置的时间(/s) in seconds unit

    //构造
    public VehicularMobilityModel(int _numberOfMobileDevices, double _simulationTime){
        super(_numberOfMobileDevices, _simulationTime);
    }

    @Override
    public void initialize() {
        Document doc = SimSettings.getInstance().getEdgeDevicesDocument();
        NodeList datacenterList = doc.getElementsByTagName("datacenter");
        Element location = (Element)((Element)datacenterList.item(0)).getElementsByTagName("location").item(0);
        int x_pos = Integer.parseInt(location.getElementsByTagName("x_pos").item(0).getTextContent());
        lengthOfSegment = x_pos * 2; //假设所有线段的长度相同
        int totalLengthOfRoad = lengthOfSegment * datacenterList.getLength(); // 道路总长度

        // 准备 locationTypes 数组以存储位置的吸引力级别
        locationTypes = new int[datacenterList.getLength()];
        timeToDriveLocationArray = new double[datacenterList.getLength()];
        for (int i = 0; i < datacenterList.getLength(); i++){
            Node datacenterNode = datacenterList.item(i); // 数据中心节点
            Element datacenterElement = (Element) datacenterNode; // 强制类型转换
            // 获取位置
            Element locationElement = (Element)datacenterElement.getElementsByTagName("location").item(0);
            // 设置吸引力级别
            locationTypes[i] = Integer.parseInt(locationElement.getElementsByTagName("attractiveness").item(0).getTextContent());

            // s = L / V(km/h) =  lengthOfSegment / SPEED_ON_THE_ROAD[i] * (1000 / 3600);
            // 本segment驾驶时间
            timeToDriveLocationArray[i] = ((double) 3.6 * (double) lengthOfSegment / (SPEED_ON_THE_ROAD[locationTypes[i]]));

            // 找出在道路上循环所需的时间
            totalTimeForLoop += timeToDriveLocationArray[i];
        }

        // 为每个设备指定一个随机x位置作为初始位置
        initialPositionArray = new int[numberOfMobileDevices];
        initialLocationIndexArray = new int[numberOfMobileDevices];
        timeToReachNextLocationArray = new double[numberOfMobileDevices];
        for (int i = 0; i < numberOfMobileDevices; i++) {
            // 随机初始化位置
            initialPositionArray[i] = SimUtils.getRandomNumber(0, totalLengthOfRoad - 1);
            // 初始位置下标(哪个segment)
            initialLocationIndexArray[i] = initialPositionArray[i] / lengthOfSegment;
            // 到达下一segment时间
            timeToReachNextLocationArray[i] = ((double) 3.6 *
                    (double) (lengthOfSegment - (initialPositionArray[i] % lengthOfSegment))) / (SPEED_ON_THE_ROAD[locationTypes[initialLocationIndexArray[i]]]);

        }

    }

    @Override
    public Location getLocation(int deviceId, double time) {
        int ofset = 0;
        double remainingTime = 0;

        int locationIndex = initialLocationIndexArray[deviceId];
        double timeToReachNextLocation = timeToReachNextLocationArray[deviceId];

        if(time < timeToReachNextLocation){
            ofset = initialPositionArray[deviceId];
            remainingTime = time;
        }
        else{
            remainingTime = (time - timeToReachNextLocation) % totalTimeForLoop;
            locationIndex = (locationIndex+1) % locationTypes.length;

            while(remainingTime > timeToDriveLocationArray[locationIndex]) {
                remainingTime -= timeToDriveLocationArray[locationIndex];
                locationIndex =  (locationIndex+1) % locationTypes.length;
            }

            ofset = locationIndex * lengthOfSegment;
        }

        int x_pos = (int) (ofset + ( (SPEED_ON_THE_ROAD[locationTypes[locationIndex]] * remainingTime) / (double)3.6));

        return new Location(locationTypes[locationIndex], locationIndex, x_pos, 0);
    }
}
