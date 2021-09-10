import os


def info_write():
    # 写
    # 获取文件大小
    file_name = "/home/forest/git/EdgeCloudSim-master/connect/result.txt"
    size = os.path.getsize(file_name)
    print(size)
    with open(file_name, 'w') as file:
        file.write('EDGE_DATACENTER_by_pycharm')
    size = os.path.getsize(file_name)
    print(size)


def info_read():
    file_name = "/home/forest/git/EdgeCloudSim-master/connect/info.txt"
    while os.path.getsize(file_name) == 0:
        pass
    info = []
    with open(file_name, 'r+') as file:

        for line in file:
            info.append(line.rstrip('\n'))
        print(len(info))
        print(info)
        file.truncate(0)
    # for item in info:
    #     print(item.endswith('\n'))


if __name__ == '__main__':
    # info_write()
    # while True:
    info_read()