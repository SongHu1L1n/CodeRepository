import gym
from gym import spaces
import numpy as np

import stable_baselines3


class Task2DEnv(gym.Env):
    metadata = {
        'render.modes': ['human', 'rgb_array'],
        'video.frames_per_second': 2
    }

    def __init__(self, task_type, speed, wlan_up_and_down_load_delay, wan_up_and_down_load_delay,
                 gsm_up_and_down_load_delay, expected_processing_delay_on_edge, expected_processing_delay_on_cloud):
        self.taskType = task_type
        self.speed = speed

        # Edge
        '''
            先放三种延迟，再放预计处理时间
        '''

        self.wlan_up_and_down_load_delay = wlan_up_and_down_load_delay
        self.expectedProcessingDelayOnEdge = expected_processing_delay_on_edge

        # Cloud_via_RSU
        self.wan_up_and_down_load_delay = wan_up_and_down_load_delay
        self.expectedProcessingDelayOnCloud = expected_processing_delay_on_cloud

        # Cloud_via_GSM
        self.gsm_up_and_down_load_delay = gsm_up_and_down_load_delay

        self.action_space = spaces.Discrete(3)
        self.observation_space = spaces.Discrete(2)
        self.state = 0

    # 给出下一时刻的状态、当前动作的回报
    def step(self, action):

        '''
            action :0, 1, 2 分别表示 EDGE_DATACENTER， CLOUD_DATACENTER_VIA_RSU， CLOUD_DATACENTER_VIA_GSM
        '''

        # 已知信息
        required_max_delay = [0.5, 1.0, 1.5]  # 三个任务的最大延迟要求
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
            # 能量消耗
            E = f[0] * self.expectedProcessingDelayOnEdge + bandwidth[0] * self.wlan_up_and_down_load_delay
            # 费用消耗 占比
            # M = self.expectedProcessingDelayOnEdge * self.edgeUtilization * storage[0]
            F = storage[0] / (storage[0] - (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024))  # 计算资源使用率
            delay = self.expectedProcessingDelayOnEdge + self.wlan_up_and_down_load_delay
        elif action == 1:
            E = f[1] * self.expectedProcessingDelayOnCloud + bandwidth[1] * self.wan_up_and_down_load_delay
            F = storage[1] / (storage[1] - (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024))
            delay = self.expectedProcessingDelayOnCloud + self.wan_up_and_down_load_delay
        else:
            E = f[2] * self.expectedProcessingDelayOnEdge + bandwidth[0] * self.gsm_up_and_down_load_delay
            F = storage[2] / (storage[2] - (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024))
            delay = self.expectedProcessingDelayOnCloud + self.gsm_up_and_down_load_delay
        # speed快 选择云

        W = (required_max_delay[self.taskType] - delay) / required_max_delay[self.taskType]
        Q = F * W
        # self.state = np.array([np.array(Q), np.array(self.speed)])
        self.state = Q
        return W, Q, True, {}  # 是否结束当前episode, 及调试信息

    def reset(self):
        return self.state

    def render(self, mode='human'):
        pass

    def close(self):
        return None


if __name__ == '__main__':
    #
    # from stable_baselines import deepq
    # from stable_baselines import PPO2

    # 接收基础参数，进行训练
    info = r"E:\CodeRepository\JavaCode\EdgeCloudSim-master\scripts\sample_DRL\config\info.txt"

    import os
    # 通信模块
    import socket
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.bind(("192.168.66.1", 7789))
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
        wlan_up_and_down_load_delay = eval(info_list[2])
        wan_up_and_down_load_delay = eval(info_list[3])
        gsm_up_and_down_load_delay = eval(info_list[4])
        expected_processing_delay_on_dge = eval(info_list[5])
        expected_processing_delay_on_cloud = eval(info_list[6])
        # *******************************************************

        conn.settimeout(30)
        # szBuf = conn.recv(1024)
        # print("recv:" + str(szBuf, 'utf8'))

        #
        # # # 模型训练
        # env = Task2DEnv(task_type, speed, wlan_up_and_down_load_delay, wan_up_and_down_load_delay,
        # gsm_up_and_down_load_delay, expected_processing_delay_on_dge, expected_processing_delay_on_cloud)
        # model = deepq.DQN(policy='MlpPolicy', env=env)
        # model.learn(total_timesteps=10000)
        # obs = env.reset()
        # # action = -1
        # for _ in range(10):
        #     _action, state = model.predict(observation=obs)
        #     action = _action
        #     obs, reward, done, information = env.step(_action)
        #     env.render()
        # print("action : ", action)
        # 得到训练结果， 传输回去
        import random
        action = random.randint(0, 3)
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



