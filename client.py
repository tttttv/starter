import asyncio
import time

async def tcp_echo_client(message="hello world"):
    reader, writer = await asyncio.open_connection('65.21.177.114', 9002)
    #reader, writer = await asyncio.open_connection('localhost', 9002)
    print('Send: %r' % message)
    writer.write(message.encode())

    data = await reader.read(100)
    print('Received: %r' % data.decode())

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
