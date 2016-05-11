import logging
import json
from socket import error as socket_error
import socket
import threading
import time
from random import randint

BUFFER_SIZE = 1024
ADDR = 'localhost'

fl = open('../config.json')
config = json.load(fl)
PORT = config['text_analysis']


def get_logger(level=logging.DEBUG, logger_name='stdout', logfile=None):
    formatter = logging.Formatter('[%(threadName)10s]: %(message)s')
    logger = logging.getLogger(logger_name)
    logger.setLevel(level)
    if logfile:
        logger.addHandler(logging.FileHandler(logfile))
    ch = logging.StreamHandler()
    ch.setFormatter(formatter)
    logger.addHandler(ch)
    return logger

std_logger = get_logger(level=logging.DEBUG, logger_name='stdout')


class Messager(object):
    
    def __init__(self, addr, port):
        """
        more recent one message will be saved
        """
        self.addr = addr
        self.port = port
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((self.addr, self.port))
        s.listen(10)
        self.s = s
        self.finished = False
        self.recent_msgs_from_remote = []
        self.msg_lock = threading.Lock()

    def send(self, remote_addr, remote_port, data):
        std_logger.debug('{} ------------------------------> {}'.format(self.port, remote_port))
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
#             std_logger.debug('Establishing connection at {}:{}'.format(remote_addr, remote_port))
            s.connect((remote_addr, remote_port))
        except socket_error:
            std_logger.debug('Fail sent {} to process {}:{}, system time is {}'.\
                             format(data, remote_addr, remote_port, time.time()))
            s.close()
            return
#         std_logger.debug('waiting for sending')
#         std_logger.debug('message=%s', data)
        s.send(data)
        std_logger.debug('Sent {} to process {}:{}, system time is {}'.\
                             format(data, remote_addr, remote_port, time.time()))
        s.close()
        
    @staticmethod
    def recv_fn(addr, conn, recent_msgs_from_remote, msg_lock):
        std_logger.debug('transfer messager is handling recv')
        msg = ''
        while 1:
            std_logger.debug('Connection address:%s', addr)
            data = conn.recv(BUFFER_SIZE)
            if not data: break # once the s is closed by sender, close.
            msg += data
        std_logger.debug("received msg:%s", msg)
        msg_lock.acquire()
        recent_msgs_from_remote.append(msg)
        msg_lock.release()
        conn.close()

    def recv(self, handler_fn):
        """
        standby and waiting for connection. 
        once a connection is received, create a handler in a new thread to handle the connection. 
        """
        std_logger.debug('transfer messager is running')
        threads = []
        while not self.finished:
#             std_logger.debug('waiting for a connection at {}:{}'.format(self.id, self.port))
            conn, addr = self.s.accept()
            # create a new thread to handle this conn
            t = threading.Thread(target=handler_fn, args=(addr, conn, self.recent_msgs_from_remote, self.msg_lock))
            threads.append(t)
            t.start()
        std_logger.debug('***TERMINATE***')
        for t in threads:
            t.join()
    
    def run(self, handler_fn):
        t = threading.Thread(target=self.recv, args=(handler_fn,))
        t.start()
    

if __name__ == '__main__':
    import sys
    m = Messager(ADDR, int(sys.argv[1]))
    remote_port = int(sys.argv[2])
    m.run(Messager.recv_fn)
    while 1:
        cmd = raw_input()
        cmds = cmd.split(' ')
        act = cmds[0]
        if act == 's': #send
            t = threading.Thread(target=m.send, args=(ADDR, remote_port, cmds[1],))
            t.start()
            t.join()
        elif act == 'show':
            std_logger.info(m.recent_msgs_from_remote)
        else:
            std_logger.error('invalid cmd')

