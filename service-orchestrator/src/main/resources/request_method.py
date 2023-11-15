import requests
import time
import random
import os
import platform

address = input("요청 보낼 주소 : ").replace(" ", "")
if not address.startswith("http://"):
    address = "http://" + address
elif address.startswith("https://"):
    print('https not supported. replace https with http')
    address = address.replace("https", "http")
    
headers = {
    'Content-Type': 'application/json'
}
os_type = platform.system().lower()
count = 0
request_counts = {}
try:
    while True:
        second = random.choice([0.01, 0.001, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.5, 0.5, 0.5, 0.5, 0.5, 5, 10])
        if os_type == 'linux':
            os.system('clear')
        elif os_type == 'window':
            os.system('cls')
        count += 1
        
        response = requests.get(
            address,
            headers=headers,
            stream=True
        )
        real_url = response.raw._connection.sock.getpeername()[0]

        if request_counts.get(str(real_url)):
            request_counts[str(real_url)] = int(request_counts[real_url]) + 1
        else:
            request_counts[real_url] = 1
        print(str(count) + "번째 요청")
        print("request url : {}".format(response.url))
        print("request real url : {}".format(real_url))
        
        for k, v in request_counts.items():
            print(" - {} request counts : {}".format(k, v))

        if second > 1:
            for i in reversed(range(second)):
                print('\rnext request will be sent in {} seconds.'.format(str(i +1)) if i < second else "next request will be sent in {} seconds.".format(str(i +1)), end = "")
                time.sleep(1)
        else:
            time.sleep(second)
        
except KeyboardInterrupt as e:
    print('\n종료.', e)
except Exception as e:
    print(e)


