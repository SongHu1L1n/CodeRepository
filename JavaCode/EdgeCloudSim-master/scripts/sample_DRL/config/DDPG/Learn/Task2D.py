import gym
from gym import spaces
import numpy as np

import stable_baselines3


class Task2DEnv(gym.Env):
    metadata = {
        'render.modes': ['human', 'rgb_array'],
        'video.frames_per_second': 2
    }

    def __init__(self):
        self.taskType = 0
        self.speed = 0

        # Edge
        '''
            先放三种延迟，再放预计处理时间
        '''
        self.wlan_up_and_down_load_delay = 0
        self.expectedProcessingDelayOnEdge = 0

        # Cloud_via_RSU
        self.wan_up_and_down_load_delay = 0
        self.expectedProcessingDelayOnCloud = 0

        # Cloud_via_GSM
        self.gsm_up_and_down_load_delay = 0

        self.action_space = spaces.Discrete(3)
        self.observation_space = spaces.Discrete(2)
        self.state = None

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
        f = [0, 0, 0]  # CPU频率

        reward = 0
        # 能量消耗
        if action == 0:
            E = f[0] * self.expectedProcessingDelayOnEdge + bandwidth[0] * self.wlan_up_and_down_load_delay
            F = (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024) / storage[0]  # 只需要任务类型
            delay = self.expectedProcessingDelayOnEdge + self.wlan_up_and_down_load_delay
        elif action == 1:
            E = f[1] * self.expectedProcessingDelayOnCloud + bandwidth[1] * self.wan_up_and_down_load_delay
            F = (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024) / storage[1]
            delay = self.expectedProcessingDelayOnCloud + self.wan_up_and_down_load_delay
        else:
            E = f[2] * self.expectedProcessingDelayOnEdge + bandwidth[0] * self.gsm_up_and_down_load_delay
            F = (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024) / storage[0]
            delay = self.expectedProcessingDelayOnCloud + self.gsm_up_and_down_load_delay
        # speed快 选择云

        W = (required_max_delay[self.taskType] - delay) / required_max_delay[self.taskType]
        Q = 0.65 * E + (1 - 0.65) * F
        self.state = (self.speed, Q)
        return self.state, W, True, {}  # 是否结束当前episode, 及调试信息

    def reset(self):
        return self.state

    def render(self, mode='human'):
        pass

    def close(self):
        return None

