info = r"E:\CodeRepository\JavaCode\EdgeCloudSim-master\scripts\sample_DRL\config\info.txt"
result = r"E:\CodeRepository\JavaCode\EdgeCloudSim-master\scripts\sample_DRL\config\result.txt"


def info_read():
    info_list = []
    import os
    # 文件不为空，读取文件
    i = 0
    while os.path.getsize(info) == 0:
        i += 1
    size = os.path.getsize(info)
    # print("第一次取大小: ", size)
    with open(info, 'r+') as file:
        # print("第二次取大小: ", os.path.getsize(info))
        for line in file:
            info_list.append(line.rstrip('\n'))
        file.truncate(0)
        # print("第三次取大小: ", os.path.getsize(info))
    print(info_list)

    task_type = eval(info_list[0])
    speed = eval(info_list[1])
    wlan_up_and_down_load_delay = eval(info_list[2])
    wan_up_and_down_load_delay = eval(info_list[3])
    gsm_up_and_down_load_delay = eval(info_list[4])

    expected_processing_delay_on_dge = eval(info_list[5])
    expected_processing_delay_on_cloud = eval(info_list[6])


def result_write():
    res = 'EDGE_DATACENTER'
    import os
    i = 0
    while os.path.getsize(result) != 0 and os.path.getsize(result) != 30:
        i += 1
    with open(result, 'w+') as file:
        if os.path.getsize(result) == 30:
            for line in file:
                print(line)
            file.truncate(0)
        file.write(res)

def handle():
    while True:
        data = request.recv(1024).decode('UTF-8', 'ignore').strip()
        if not data: break
        print(data)
        self.feedback_data = ("回复\"" + self.data + "\":\n\t你好，我是Server端").encode("utf8")
        print("发送成功")
        self.request.sendall(self.feedback_data)

if __name__ == '__main__':
        # info_read()
        while 1:
            result_write()
    # from stable_baselines3 import PPO
    # result_write()
