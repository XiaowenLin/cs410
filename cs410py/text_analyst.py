from textblob import TextBlob, Word
from utils import *


class Analyst(Messager):
    def __init__(self, addr, port=PORT):
        self.messager = Messager(addr, port)
        self.messager.run(Analyst.recv_fn)

    def correct(self, msg):
        b = TextBlob(msg)
        corr = b.correct()
        if msg != corr:
            return corr

    def synset(self, word):
        w = Word(word)
        ret = set()
        for syn in w.synsets:
            ret.add(syn.name().split('.')[0])
        return list(ret)
        
    def sentiment(self, text):
        testimonial = TextBlob("Textblob is amazingly simple to use. What great fun!")
        return testimonial.sentiment.polarity

    def handle_query(self, query):
        toks = ''.join(c for c in query if c.isalnum() or c.isspace()).split()
        

    @staticmethod
    def recv_fn(addr, conn, recent_msgs_from_remote, msg_lock, analyst):
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

        to_do = json.loads(msg)
        cmd = to_do.get('cmd')
        if cmd == 'correct':
            ret = analyst.correct(to_do.get('query'))
            if ret:
                analyst.send(json.dumps({'correct':'false', 'query':ret}))
            else:
                analyst.send(json.dumps({'correct':'true'}))
        elif cmd == 'synset':
            pass
        elif cmd == 'sentiment':
            pass
        elif cmd == 'search':
            query = cmds.get('query')
            analyst.handle_query(query)


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
            conn.settimeout(3 * MAX_DELAY)
            # create a new thread to handle this conn
            t = threading.Thread(target=handler_fn, args=(addr, conn, self.recent_msgs_from_remote, self.msg_lock, self,))
            threads.append(t)
            t.start()
        std_logger.debug('***TERMINATE***')
        for t in threads:
            t.join()

if __name__ == '__main__':
    analyst = Analyst('localhost', 9000)
    
