#!/usr/bin/python3
# -*- coding: utf-8 -*-

"""
@module  : star.py
@author  : Rinne
@contact : minami.rinne.me@gmail.com
@time    : 2020/01/21 午後 10:22
"""
import json
import logging
import random
import time

import requests
from bs4 import BeautifulSoup


class BiliSpider:
    def __init__(self):
        self.logger = logging.getLogger('log')
        self.logger.setLevel(logging.DEBUG)
        fh = logging.FileHandler("logger.log", encoding='utf-8')
        fh.setLevel(logging.DEBUG)
        self.logger.addHandler(fh)
        ch = logging.StreamHandler()
        ch.setLevel(logging.DEBUG)

        self.video_url = "https://www.bilibili.com/video/av"
        self.video_base_info_api_url = "https://api.bilibili.com/archive_stat/stat?aid="
        self.video_list_info_api_url = "https://api.bilibili.com/x/player/pagelist?aid="
        self.video_tag_info_api_url = "https://api.bilibili.com/x/tag/archive/tags?aid="
        self.video_replies_api_url = "http://api.bilibili.com/x/v2/reply?jsonp=jsonp&;pn=1&type=1&oid="
        self.video_bullet_screen_api_url = "http://comment.bilibili.com/"

    def bangumi(self):
        pass

    def video(self, av_number, video_bs):
        video_base_info_api_url = self.video_base_info_api_url + str(av_number) + "&jsonp=jsonp"
        video_tag_info_api_url = self.video_tag_info_api_url + str(av_number)
        video_url = self.video_url + str(av_number)

        video_type = []
        video_info = {}

        get_video_info = json.loads(requests.get(video_base_info_api_url).text)
        get_tag_info = json.loads(requests.get(video_tag_info_api_url).text)

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

        tag_list = []
        for x in get_tag_info.get("data"):
            tag_list.append(x.get("tag_name"))

        bullet_screen_info = self.video_bullet_screen_info(av_number)

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

        print("[" + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()) + "] " + video_url + " 获取成功 " + str(
            video_info))
        self.logger.info("[" + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()) + "] " + video_url + " 获取成功")
        return video_info

    def star(self):
        av_flag = 50
        av_number = 7000080

        while av_number < 99999999:
            if av_flag == av_number:
                av_flag += random.randint(50, 70)
                time.sleep(random.randint(5, 10))

            video_url = self.video_url + str(av_number)
            get_requests = requests.get(video_url)
            video_bs = BeautifulSoup(get_requests.text, "html.parser")

            # 检查视频是否存在
            if "视频去哪了呢" in str(video_bs.text):
                av_number += 1
                print("[" + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()) + "] " + video_url + " 此稿件已删除")
                self.logger.info(
                    "[" + time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()) + "] " + video_url + " 此稿件已删除")
                continue

            if len(video_bs.find_all("div", attrs={"class": "r-con"})) > 0:
                self.video(av_number, video_bs)
            else:
                print("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                self.bangumi()

            av_number += 1

    def video_replies_info(self, av_number):
        video_replies_api_url = self.video_replies_api_url + str(av_number)
        get_video_replies = json.loads(requests.get(video_replies_api_url).text)

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
        get_video_list_info = json.loads(requests.get(video_list_info_api_url).text)

        flag = 1
        bullet_screen_list = []
        for x in get_video_list_info["data"]:
            bullet_screen_dict = {}
            video = {}
            # 提取视频的cid
            video_cid = x.get("cid")

            # 利用api请求视频的弹幕
            video_bullet_screen_requests = requests.get(self.video_bullet_screen_api_url + str(video_cid) + ".xml")
            video_bullet_screen_requests.encoding = "utf-8"
            video_bullet_screen_bs = BeautifulSoup(video_bullet_screen_requests.text, "xml")

            # 提取弹幕信息
            for i in video_bullet_screen_bs.find_all("d"):
                # 提取时间戳
                video_times = str(i).split(",")[4]
                # 提取弹幕内容
                info = i.get_text()
                # sender = str(x).split(",")[6]
                # bullet_screen_dict[int(time)] = sender + ":" + info
                bullet_screen_dict[int(video_times)] = info
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
