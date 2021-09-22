from socketserver import BaseRequestHandler, ThreadingTCPServer
BUF_SIZE = 1024


class Handler(BaseRequestHandler):
    def handle(self):
        while True:
            data = self.request.recv(BUF_SIZE)
            if len(data) > 0:
                print('receive = ', data)
                response = '{}'.format(data)
                self.request.sendall(response)
                print("send = ", response)
            else:
                print('close')
                break


def another():
    import socket
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM);
        sock.bind(("192.168.66.1", 7777))
        sock.listen(5)
    except:
        print("init socket error!")

    while True:
        conn, addr = sock.accept()
        print("get client")
        conn.settimeout(30)
        szBuf = conn.recv(1024)
        print("recv:" + str(szBuf, 'utf8'))

        if "0" == szBuf:
            conn.send(b"exit")
        else:
            result = 'EDGE_DATACENTER'
            # conn.send(bytes(20))
            a = 0
            for i in range(10000):
                a += i
            conn.send(result.encode())

    conn.close()
    print("end of servive")


if __name__ == '__main__':
    import random
    for _ in range(10):
        print(random.randint(0, 3))