import asyncio
import time

async def tcp_echo_client(message="hello world"):
    reader, writer = await asyncio.open_connection('65.21.131.239', 9002)
    #reader, writer = await asyncio.open_connection('localhost', 9002)

    s1 = b'A\x03Sea\x00\x11Q\x14A"\x00\x00\x00\x00\x00\x124\xfb'
    s2 = b'A\x03Sea\x00\x11Q\x14A"\x00\x00\x00\x00\x00\x124\xf3'
    s3 = b'A\x03Sea\x00\x11Q\x14A"\x00\x00\x00\x00\x00\x124\xa5'


    s = s3
    writer.write(s) #Login package
    data = await reader.read(100)
    print(data)
    if data == b'resp_crc=' + s[-1].to_bytes(1, byteorder='big'):
      ss = [2, 100, 255, 255, 33, 24, 72, 71, 60, 250, 20, 230, 2, 21, 177, 141, 3, 33, 176, 1,
            137, 214, 56, 36, 32, 177, 44, 6, 105, 145, 0, 0, 122, 122]
      ss = bytes(ss)
      writer.write(ss)


    try:
      print('Received: %r' % data.decode())
    except:
      pass


    print('Close the socket')
    writer.close()


"""
message = 'Hello World!'
loop = asyncio.get_event_loop()
loop.run_until_complete(tcp_echo_client(message))
loop.close()

"""


async def main():
    start = time.time()
    for i in range(0, 1):
        tasks = map(asyncio.create_task, [tcp_echo_client() for i in range(0, 1)])
        await asyncio.wait(tasks, return_when=asyncio.ALL_COMPLETED)
    print(time.time() - start)

if __name__ == '__main__':
    asyncio.run(main())
