#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
@module  : user.py
@author  : Rinne
@contact : minami.rinne.me@gmail.com
@time    : 2020/05/19 午前 01:26
"""

import binascii
import json
import random
import time

import requests
from kafka import KafkaProducer


class User:
    def __init__(self):
        self.user_info_api_url = "https://api.bilibili.com/x/web-interface/card?mid="
        self.producer = KafkaProducer(bootstrap_servers='127.0.0.1:9092')

    def user(self):
        uid_number = 1
        while uid_number < 10000000:
            user_url = self.user_info_api_url + str(uid_number)
            try:
                get_user_info = json.loads(requests.get(user_url, timeout=(10, 27)).text)
            except Exception as e:
                print(e)
                time.sleep(random.randint(60, 70))
                get_user_info = json.loads(requests.get(user_url, timeout=(10, 27)).text)

            crc32 = binascii.crc32(str(uid_number).encode("utf-8"))
            get_user_info["crc32"] = crc32
            user_json = json.dumps(get_user_info, ensure_ascii=False)
            print(user_json)
            uid_number += 1
            self.producer.send('User_Info', bytes(user_json, "UTF-8"))


if __name__ == "__main__":
    User().user()
