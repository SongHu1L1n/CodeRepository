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
        f = [0, 0, 0]  # CPU频率

        reward = 0
        # 能量消耗
        if action == 0:
            # 能量消耗
            E = f[0] * self.expectedProcessingDelayOnEdge + bandwidth[0] * self.wlan_up_and_down_load_delay
            # 费用消耗 占比
            # M = self.expectedProcessingDelayOnEdge * self.edgeUtilization * storage[0]
            F = (input_file_size[self.taskType] + output_file_size[self.taskType] * 1024) / storage[0]  # 计算资源使用率
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
        # self.state = np.array([np.array(Q), np.array(self.speed)])
        self.state = self.speed
        return self.state, W, True, {}  # 是否结束当前episode, 及调试信息

    def reset(self):
        return self.state

    def render(self, mode='human'):
        pass

    def close(self):
        return None


def connect_with_idea():
    import socket


if __name__ == '__main__':
    env = Task2DEnv(0, 40.0, 0.031355943741745745, 0.006117192530585962, 0.027207457053519783, 0.2791, 0.037213333333333334)
    from stable_baselines import deepq
    model = deepq.DQN(policy='MlpPolicy', env=env)
    model.learn(total_timesteps=10000)

    obs = env.reset()
    for _ in range(10):
        action, state = model.predict(observation=obs)
        print(action)
        obs, reward, done, info = env.step(action)
        env.render()




    # from stable_baselines3.common.env_checker import check_env
    # env = Task2DEnv(0, 0, 0, 0, 0, 0, 0)
    # check_env(env)

