import gym
from gym import spaces


class MySim(gym.Env):
    def __init__(self):
        self.action_space = spaces.Discrete(5)
        self.observation_space = spaces.Discrete(2)

    def step(self, action):
        state = 1

        if action == 2:
            reward = 1
        else:
            reward = -1
        done = True
        info = {}
        return state, reward, done, info

    def reset(self):
        state = 0
        return state

    def render(self, mode='human'):
        pass

    def seed(self, seed=None):
        pass


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
        sock.bind(("192.168.66.1", 7777))
        sock.listen(5)
    except:
        print("init socket error!")

    while True:
        conn, addr = sock.accept()
        print("get client")

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
        szBuf = conn.recv(1024)
        print("recv:" + str(szBuf, 'utf8'))

        # # 模型训练
        # env = MySim()
        # model = deepq.DQN(policy='MlpPolicy', env=env)
        # model.learn(total_timesteps=1000)
        # obs = env.reset()
        # for _ in range(10):
        #     action, state = model.predict(observation=obs)
        #     print("action: ", action, ", state: ", state)
        #     obs, reward, done, info = env.step(action)
        #     env.render()

        # 得到训练结果， 传输回去
        if "0" == szBuf:
            conn.send(b"exit")
        else:
            result = 'EDGE_DATACENTER'
            # conn.send(bytes(20))
            conn.send(result.encode())

    conn.close()
    print("end of servive")



