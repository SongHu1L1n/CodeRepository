info = r"E:\CodeRepository\JavaCode\EdgeCloudSim-master\scripts\sample_DRL\config\info.txt"
result = r"E:\CodeRepository\JavaCode\EdgeCloudSim-master\scripts\sample_DRL\config\result.txt"


def info_read():
    info_list = []
    import os
    # 文件不为空，读取文件
    while os.path.getsize(info) == 0:
        print("文件为空， 等待写入后再读取...")
    size = os.path.getsize(info)
    print("第一次取大小: ", size)
    with open(info, 'r+') as file:
        print("第二次取大小: ", os.path.getsize(info))
        for line in file:
            info_list.append(line.rstrip('\n'))
        file.truncate(0)
        print("第三次取大小: ", os.path.getsize(info))
    print(info_list)

    task_type = eval(info_list[0])
    speed = eval(info_list[1])
    wlan_up_and_down_load_delay = eval(info_list[2])
    wan_up_and_down_load_delay = eval(info_list[3])
    gsm_up_and_down_load_delay = eval(info_list[4])

    expected_processing_delay_on_dge = eval(info_list[5])
    expected_processing_delay_on_cloud = eval(info_list[6])


if __name__ == '__main__':
    info_read()
    from stable_baselines3 import PPO
