#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
@module  : star.py
@author  : Rinne
@contact : minami.rinne.me@gmail.com
@time    : 2020/01/21 午後 10:22
"""
import binascii
import json
import random
import time

import requests
from bs4 import BeautifulSoup
from kafka import KafkaProducer


class BiliSpider:
    def __init__(self):
        self.producer = KafkaProducer(bootstrap_servers='127.0.0.1:9092')
        self.video_url = "https://www.bilibili.com/video/av"
        self.video_base_info_api_url = "https://api.bilibili.com/archive_stat/stat?aid="
        self.video_list_info_api_url = "https://api.bilibili.com/x/player/pagelist?aid="
        self.video_tag_info_api_url = "https://api.bilibili.com/x/tag/archive/tags?aid="
        self.video_replies_api_url = "http://api.bilibili.com/x/v2/reply?jsonp=jsonp&;pn=1&type=1&oid="
        self.video_bullet_screen_api_url = "http://comment.bilibili.com/"

    def star(self):
        av_number = 1

        while av_number <= 10000000:
            video_url = self.video_url + str(av_number)

            try:
                get_requests = requests.get(video_url, timeout=(10, 27))
                video_bs = BeautifulSoup(get_requests.text, "html.parser")
            except Exception as e:
                print(e)
                time.sleep(random.randint(60, 70))
                get_requests = requests.get(video_url, timeout=(10, 27))
                video_bs = BeautifulSoup(get_requests.text, "html.parser")

            # 检查视频是否存在
            if "视频去哪了呢" in str(video_bs.text):
                av_number += 1
                print("[" + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()) + "] " + video_url + " 此稿件已删除")
                continue

            if len(video_bs.find_all("div", attrs={"class": "r-con"})) > 0:
                self.video(av_number, video_bs)
            else:
                self.bangumi(av_number)

            av_number += 1

    def bangumi(self, av_number):
        bangumi_base_info_api_url = self.video_base_info_api_url + str(av_number) + "&jsonp=jsonp"
        bangumi_info = {}

        try:
            get_video_info = json.loads(requests.get(bangumi_base_info_api_url, timeout=(10, 27)).text)
        except Exception as e:
            print(e)
            time.sleep(random.randint(60, 70))
            get_video_info = json.loads(requests.get(bangumi_base_info_api_url, timeout=(10, 27)).text)

        bullet_screen_info = self.video_bullet_screen_info(av_number)

        try:
            bangumi_info = {"av": "av" + str(av_number),
                            "view": get_video_info.get("data").get("view"),
                            "replies_number": get_video_info.get("data").get("reply"),
                            "favorite": get_video_info.get("data").get("favorite"),
                            "coin": get_video_info.get("data").get("coin"),
                            "share": get_video_info.get("data").get("share"), "count": bullet_screen_info.get("flag"),
                            "bullet_screen": bullet_screen_info.get("bullet"),
                            "replies": self.video_replies_info(av_number)}
        except Exception as e:
            print(e)

        bangumi_json = json.dumps(bangumi_info, ensure_ascii=False)
        self.producer.send('Word_Count', bytes(bangumi_json, "UTF-8"))
        print(bangumi_json)

    def video(self, av_number, video_bs):
        video_base_info_api_url = self.video_base_info_api_url + str(av_number) + "&jsonp=jsonp"
        video_tag_info_api_url = self.video_tag_info_api_url + str(av_number)
        video_url = self.video_url + str(av_number)

        video_type = []
        video_info = {}
        video_title = ""
        video_author = ""
        video_time = ""

        try:
            get_video_info = json.loads(requests.get(video_base_info_api_url, timeout=(10, 27)).text)
            get_tag_info = json.loads(requests.get(video_tag_info_api_url, timeout=(10, 27)).text)
        except Exception as e:
            print(e)
            time.sleep(random.randint(60, 70))
            get_video_info = json.loads(requests.get(video_base_info_api_url, timeout=(10, 27)).text)
            get_tag_info = json.loads(requests.get(video_tag_info_api_url, timeout=(10, 27)).text)

        try:
            video_count_info = video_bs.find("div", attrs={"class": "r-con"})
            # 提取视频作者名字
            video_author = video_count_info.find("a", attrs={"report-id": "name"}).get_text()

            video_head_info = video_bs.find("div", attrs={"id": "viewbox_report",
                                                          "class": "video-info report-wrap-module report-scroll-module"})

            # 提取视频标题
            video_title = video_head_info.contents[0].get_text()

            # 提取视频类型
            for x in video_head_info.contents[1].find_all("a"):
                video_type.append(x.get_text())

            # 提取视频上传时间
            if len(video_head_info.contents[1].find_all("span")) < 2:
                video_time = video_head_info.contents[1].find_all("span")[0].get_text()
            else:
                video_time = video_head_info.contents[1].find_all("span")[1].get_text()
        except Exception as e:
            print(e)

        tag_list = []
        for x in get_tag_info.get("data"):
            tag_list.append(x.get("tag_name"))

        bullet_screen_info = self.video_bullet_screen_info(av_number)

        try:
            video_info["av"] = "av" + str(av_number)
            video_info["title"] = video_title
            video_info["author"] = video_author
            video_info["view"] = get_video_info.get("data").get("view")
            video_info["replies_number"] = get_video_info.get("data").get("reply")
            video_info["favorite"] = get_video_info.get("data").get("favorite")
            video_info["coin"] = get_video_info.get("data").get("coin")
            video_info["share"] = get_video_info.get("data").get("share")
            video_info["type"] = video_type
            video_info["tag"] = tag_list
            video_info["time"] = video_time
            video_info["url"] = video_url
            video_info["count"] = bullet_screen_info.get("flag")
            video_info["bullet_screen"] = bullet_screen_info.get("bullet")
            video_info["replies"] = self.video_replies_info(av_number)
            info_json = json.dumps(video_info, ensure_ascii=False)
            print(info_json)
            self.producer.send('Word_Count', bytes(info_json, "UTF-8"))
        except Exception as e:
            print(e)

    def video_replies_info(self, av_number):
        video_replies_api_url = self.video_replies_api_url + str(av_number)
        try:
            get_video_replies = json.loads(requests.get(video_replies_api_url, timeout=(10, 27)).text)
        except Exception as e:
            print(e)
            time.sleep(random.randint(60, 70))
            get_video_replies = json.loads(requests.get(video_replies_api_url, timeout=(10, 27)).text)

        replies_list = []
        replies_info = {}
        for key in ["replies", "hots"]:
            if get_video_replies["code"] == 12002:
                break

            try:
                for x in get_video_replies.get("data").get(key):
                    replies_info["time"] = x.get("ctime")
                    replies_info["name"] = x.get("member").get("uname")
                    replies_info["sex"] = x.get("member").get("sex")
                    replies_info["like"] = x.get("like")
                    replies_info["level"] = int(x.get("member").get("level_info").get("current_level"))
                    replies_info["message"] = x.get("content").get("message")
                    replies_list.append(replies_info)
                    replies_info = {}
                    if x["replies"] is not None:
                        for i in x["replies"]:
                            replies_info["time"] = i.get("ctime")
                            replies_info["name"] = i.get("member").get("uname")
                            replies_info["sex"] = i.get("member").get("sex")
                            replies_info["like"] = i.get("like")
                            replies_info["level"] = int(i.get("member").get("level_info").get("current_level"))
                            replies_info["message"] = i.get("content").get("message")
                            replies_list.append(replies_info)
                            replies_info = {}
            except Exception as ex:
                print(ex)

        return replies_list

    def video_bullet_screen_info(self, av_number):
        info_dict = {}
        video_list_info_api_url = self.video_list_info_api_url + str(av_number)
        try:
            get_video_list_info = json.loads(requests.get(video_list_info_api_url, timeout=(10, 27)).text)
        except Exception as e:
            print(e)
            time.sleep(random.randint(60, 70))
            get_video_list_info = json.loads(requests.get(video_list_info_api_url, timeout=(10, 27)).text)

        flag = 1
        bullet_screen_list = []
        for x in get_video_list_info["data"]:
            bullet_screen_dict = {}
            video = {}
            video_cid = ""

            try:
                # 提取视频的cid
                video_cid = x.get("cid")
                # 利用api请求视频的弹幕
                video_bullet_screen_requests = requests.get(self.video_bullet_screen_api_url + str(video_cid) + ".xml", timeout=(10, 27))
                video_bullet_screen_requests.encoding = "utf-8"
                video_bullet_screen_bs = BeautifulSoup(video_bullet_screen_requests.text, "xml")
            except Exception as e:
                print(e)
                time.sleep(random.randint(60, 70))
                video_bullet_screen_requests = requests.get(self.video_bullet_screen_api_url + str(video_cid) + ".xml", timeout=(10, 27))
                video_bullet_screen_requests.encoding = "utf-8"
                video_bullet_screen_bs = BeautifulSoup(video_bullet_screen_requests.text, "xml")

            try:
                # 提取弹幕信息
                for i in video_bullet_screen_bs.find_all("d"):
                    message_info = {}
                    # 提取发送者ID
                    sender = str(i).split(",")[6]
                    # 提取时间戳
                    video_times = str(i).split(",")[4]
                    # 提取弹幕内容
                    info = i.get_text()
                    if sender not in bullet_screen_dict.keys():
                        message_info[int(video_times)] = info
                        bullet_screen_dict[sender] = message_info
                    else:
                        bullet_screen_dict[sender][int(video_times)] = info
            except Exception as e:
                print(e)
            video["p"] = x.get("page")
            video["cid"] = video_cid
            video["part"] = x.get("part")
            video["bullet"] = bullet_screen_dict
            bullet_screen_list.append(video)
            flag += 1

        info_dict["flag"] = flag
        info_dict["bullet"] = bullet_screen_list
        return info_dict


if __name__ == "__main__":
    BiliSpider().star()
