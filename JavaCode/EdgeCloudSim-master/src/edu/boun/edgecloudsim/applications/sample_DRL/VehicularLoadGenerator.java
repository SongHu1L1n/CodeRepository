package edu.boun.edgecloudsim.applications.sample_DRL;

import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.task_generator.LoadGeneratorModel;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;
import edu.boun.edgecloudsim.utils.TaskProperty;
import org.apache.commons.math3.distribution.ExponentialDistribution;

import java.util.ArrayList;

public class VehicularLoadGenerator extends LoadGeneratorModel {
    // 设备任务类型
    int taskTypeOfDevices[];

    public VehicularLoadGenerator(int _numberOfMobileDevices, double _simulationTime, String _simScenario) {
        super(_numberOfMobileDevices, _simulationTime, _simScenario);
    }

    @Override
    public void initializeModel() {
        taskList = new ArrayList<TaskProperty>();

        // 每个设备初始化一种任务类型
        taskTypeOfDevices = new int[numberOfMobileDevices];
        for (int i = 0; i < numberOfMobileDevices; i++) {
            int randomTaskType = -1; // 随机任务类型
            double taskTypeSelector = SimUtils.getRandomDoubleNumber(0, 100); // 随机数
            double taskTypePercentage = 0; // 任务类型占比
            double[][] taskKookUpTable = SimSettings.getInstance().getTaskLookUpTable(); // 任务列表
            // 寻找合适任务
            for (int j = 0; j < taskKookUpTable.length; j++) {
                taskTypePercentage += taskKookUpTable[j][0];

                if (taskTypeSelector <= taskTypePercentage){
                    randomTaskType = j;
                    break;
                }
            }

            if (randomTaskType == -1){
                SimLogger.printLine("Impossible is occurred! no random task type!");
                continue;
            }

            taskTypeOfDevices[i] = randomTaskType; // 设置任务类型

            //配置参数
            double poissonMean = taskKookUpTable[randomTaskType][2];
            double activePeriod = taskKookUpTable[randomTaskType][3];
            double idlePeriod = taskKookUpTable[randomTaskType][4];
            double activePeriodStartTime = SimUtils.getRandomDoubleNumber( // 活动周期开始时间
                    SimSettings.CLIENT_ACTIVITY_START_TIME,
                    SimSettings.CLIENT_ACTIVITY_START_TIME * 2); // 活动周期在模拟开始后不久开始
            double virtualTime = activePeriodStartTime;

            ExponentialDistribution rng = new ExponentialDistribution(poissonMean); // 指数分布
            
            while (virtualTime < simulationTime){
                double interval = rng.sample() * 80;
                if(interval <= 0){
                    SimLogger.printLine("Impossible is occurred! interval is " + interval + " for device " + i + " time " + virtualTime);
                    continue;
                }
                virtualTime += interval;
                if (virtualTime > activePeriodStartTime + activePeriod){
                    activePeriodStartTime = activePeriodStartTime + activePeriod + idlePeriod;
                    virtualTime = activePeriodStartTime;
                    continue;
                }

                long inputFileSize = (long) taskKookUpTable[randomTaskType][5];
                long inputFileSizeBias = inputFileSize / 10;

                long outputFileSize = (long) taskKookUpTable[randomTaskType][6];
                long outputFileSizeBias = outputFileSize / 10;

                long length = (long) taskKookUpTable[randomTaskType][7];
                long lengthBias = length / 10;

                int pesNumber = (int) taskKookUpTable[randomTaskType][8];

                inputFileSize = SimUtils.getRandomLongNumber(inputFileSize - inputFileSizeBias, inputFileSize + inputFileSizeBias);
                outputFileSize = SimUtils.getRandomLongNumber(outputFileSize - outputFileSizeBias, outputFileSize + outputFileSizeBias);
                length = SimUtils.getRandomLongNumber(length - lengthBias, length + lengthBias);

                taskList.add(new TaskProperty(virtualTime, i, randomTaskType, pesNumber, length, inputFileSize, outputFileSize));
            }

        }
    }

    @Override
    public int getTaskTypeOfDevice(int deviceId) {
        return taskTypeOfDevices[deviceId];
    }
}
