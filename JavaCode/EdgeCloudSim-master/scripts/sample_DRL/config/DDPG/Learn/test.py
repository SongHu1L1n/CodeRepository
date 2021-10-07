import gym
from gym import spaces
import numpy as np

import stable_baselines3


class Task2DEnv(gym.Env):
    metadata = {
        'render.modes': ['human', 'rgb_array'],
        'video.frames_per_second': 2
    }

    def __init__(self, task_type, speed, predictedServiceTimeForEdge, predictedServiceTimeForCloudViaRSU, predictedServiceTimeForCloudViaGSM):
        self.taskType = task_type
        self.speed = speed

        # Edge
        '''
            先放三种延迟，再放预计处理时间
        '''

        self.service_time_for_edge = predictedServiceTimeForEdge
        self.service_time_for_cloud_via_rsu = predictedServiceTimeForCloudViaRSU
        self.service_time_for_cloud_via_gsm = predictedServiceTimeForCloudViaGSM


        self.action_space = spaces.Discrete(3)
        self.observation_space = spaces.Discrete(2)
        self.state = 0

    # 给出下一时刻的状态、当前动作的回报
    def step(self, action):

        '''
            action :0, 1, 2 分别表示 EDGE_DATACENTER， CLOUD_DATACENTER_VIA_RSU， CLOUD_DATACENTER_VIA_GSM
        '''

        # 已知信息
        required_max_delay = [1.0, 0.5, 1.5]  # 三个任务的最大延迟要求
        bandwidth = [10, 50, 20]  # 带宽       edge rsu  gsm 根据action选择
        # costs = [0.5, 1, 1]
        storage = [125000, 2500000, 2500000]  # VM存储
        input_file_size = [20, 40, 20]
        output_file_size = [20, 20, 80]
        f = [2, 10, 0]  # CPU频率
        sensitivity = [0.5, 0.8, 0.25]
        reward = 0
        # 能量消耗
        if action == 0:
            delay = self.service_time_for_edge
        elif action == 1:
            delay = self.service_time_for_cloud_via_rsu
        else:
            delay = self.service_time_for_cloud_via_gsm
        # speed快 选择云
        '''
        '''
        if delay < required_max_delay[self.taskType]:
            QoE = 1
        elif delay > 2 * required_max_delay[self.taskType]:
            QoE = 0
        else:
            QoE = (1 - (delay - required_max_delay[self.taskType]) / required_max_delay[self.taskType]) * (1 - sensitivity[self.taskType])
        c = sensitivity[self.taskType] * self.speed / 100

        W = (required_max_delay[self.taskType] - delay) / required_max_delay[self.taskType]
        # self.state = np.array([np.array(Q), np.array(self.speed)])
        self.state = W
        return self.speed, QoE, True, {}  # 是否结束当前episode, 及调试信息

    def reset(self):
        return self.state

    def render(self, mode='human'):
        pass

    def close(self):
        return None


if __name__ == '__main__':
    #
    from stable_baselines import deepq
    # from stable_baselines import PPO2

    # 接收基础参数，进行训练
    info = r"E:\CodeRepository\JavaCode\EdgeCloudSim-master\scripts\sample_DRL\config\info.txt"

    import os
    # 通信模块
    import socket
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.bind(("192.168.66.1", 8897))
        sock.listen(5)
        print("等待连接......")
    except:
        print("init socket error!")

    while True:
        conn, addr = sock.accept()
        print("got client......")
        print()

        # *******************************************************
        # 文件不为空，读取文件
        info_list = []
        k = 0
        while os.path.getsize(info) == 0:
            k += 1
        size = os.path.getsize(info)
        # print("第一次取大小: ", size)
        with open(info, 'r+') as file:
            # print("第二次取大小: ", os.path.getsize(info))
            for line in file:
                info_list.append(line.rstrip('\n'))
            file.truncate(0)
            # print("第三次取大小: ", os.path.getsize(info))
        print(info_list)
        # 更新参数
        task_type = eval(info_list[0])
        speed = eval(info_list[1])
        predictedServiceTimeForEdge = eval(info_list[2])
        predictedServiceTimeForCloudViaRSU = eval(info_list[3])
        predictedServiceTimeForCloudViaGSM = eval(info_list[4])

        # *******************************************************

        conn.settimeout(30)
        szBuf = conn.recv(1024)
        print("recv:" + str(szBuf, 'utf8'))

        '''
            ***********************************************
        '''

        #
        # # # 模型训练
        env = Task2DEnv(task_type, speed, predictedServiceTimeForEdge, predictedServiceTimeForCloudViaRSU, predictedServiceTimeForCloudViaGSM)
        model = deepq.DQN(policy='MlpPolicy', env=env)
        model.learn(total_timesteps=10000)
        obs = env.reset()
        # action = -1
        for _ in range(10):
            _action, state = model.predict(observation=obs)
            action = _action
            obs, reward, done, information = env.step(_action)
            env.render()
        print("action : ", action)
        # 得到训练结果， 传输回去
        # sensitivity = [0.5, 0.8, 0.25]
        # c = speed / 100 * sensitivity[task_type]
        # required_max_delay = [0.5, 1, 1.5]
        # edge_process_time = wlan_up_and_down_load_delay + expected_processing_delay_on_edge
        # cloud_rsu_process_time = wan_up_and_down_load_delay + expected_processing_delay_on_cloud
        # cloud_gsm_process_time = gsm_up_and_down_load_delay + expected_processing_delay_on_cloud
        # if edge_process_time < required_max_delay[task_type] and c <= 0.16:
        #     action = 0
        # elif c > 0.16 and c < 0.3:
        #     action = 1
        # else:
        #     action = 2

        if action == 0:
            result = 'EDGE_DATACENTER'
        elif action == 1:
            result = 'CLOUD_DATACENTER_VIA_RSU'
        elif action == 2:
            result = 'CLOUD_DATACENTER_VIA_GSM'
        # conn.send(bytes(20))
        conn.send(result.encode())

    conn.close()
    print("end of servive")



