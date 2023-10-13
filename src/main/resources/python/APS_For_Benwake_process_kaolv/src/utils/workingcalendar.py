import math
import csv
import datetime
from src import config

# -*- coding: gbk -*-

# 时间日历 # 基于当前的代码模型，后期在针对每个具体的人进行修改

class WorkingCalendar:
    # 放在这里的是类变量，用此类创建的所有对象中，都共享相同的变量。
    hours_in_day = config.hours_in_day  # 每人每天工作时间，单位h
    day_in_week = config.day_in_week  # 每周工作的天数
    unit_time = config.unit_time  # 排程的单位时间间隔，单位h

    # 初始化
    def __init__(self, start_date, during_days, working_calendar_address):
        self.start_date = start_date  # 开始日期
        self.during_days = during_days  # 排程天数
        self.working_calendar_date_all = []  # 日历中的所有日期
        self.working_calendar_people_all = []  # 日历中的所有人数信息
        self.date_month_day = []  # 日历中开始时间到截止时间的日期,主要是x轴的日期信息
        self.working_calendar_date = []  # 日历中开始时间到截止时间的日期
        self.working_calendar_people = []  # 日历中开始时间到截至时间的人数信息
        self.max_people_num = 0  # 排程天数中的最大人数
        self.one_day_time_paln = self.get_day_plan()
        # 获取信息
        with open(working_calendar_address, newline='',encoding = 'UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            for row in reader:
                if int(row[1]) != 0 :
                    self.working_calendar_date_all.append(row[0])
                    self.working_calendar_people_all.append(row[1])
        start_time_index = self.working_calendar_date_all.index(start_date)
        for i in range(start_time_index,start_time_index+during_days+1):
            date_temp = self.working_calendar_date_all[i].split('-')[1] + '-' + self.working_calendar_date_all[i].split('-')[2]
            self.date_month_day.append(date_temp) # 去除年份,保留月日
        for i in range(start_time_index, start_time_index + during_days):
            self.working_calendar_date.append(self.working_calendar_date_all[i])
            self.working_calendar_people.append(self.working_calendar_people_all[i])
        self.max_people_num = int(max(self.working_calendar_people))

        time_slot = int(self.during_days * self.hours_in_day / self.unit_time)
        self.people_in_plan = self.get_people_in_plan(time_slot)

    # 将时间字符串转变成 规定的时间戳
    def get_time_period(self,data):
        try:
            data = datetime.datetime.strptime(data.split(' ')[0], "%Y-%m-%d")
        except:
            data = datetime.datetime.strptime(data.split(' ')[0], "%Y/%m/%d")
        date_str = data.date().strftime("%Y-%m-%d") # 换成对应格式的字符串
        while True:
            if date_str in self.working_calendar_date_all:
                index = self.working_calendar_date_all.index(date_str)
                break
            else:
                date_str = datetime.datetime.strptime(date_str,
                                                      "%Y-%m-%d") + datetime.timedelta(days=-1)
                date_str = str(date_str).split(' ')[0]

        now_date_str = self.start_date
        now_index = self.working_calendar_date_all.index(now_date_str)
        day = index - now_index
        return day*self.hours_in_day/self.unit_time


    # 求开始时间到time_slot的出勤人数列表，单位时间为unit_time
    def get_people_in_plan(self,time_slot):
        people_in_plan = [] # 单位时间间隔的人数列表
        now_index = self.working_calendar_date_all.index(self.start_date)
        # 求具体的天数，向下取整
        day = math.floor((time_slot*self.unit_time)/self.hours_in_day)
        for i in range(day):
            for j in range(int(self.hours_in_day/self.unit_time)):
                people_in_plan.append(int(self.working_calendar_people_all[now_index+i]))
        # 剩余时间段
        time_temp = (time_slot)%int(self.hours_in_day/self.unit_time)
        for z in range(int(time_temp)):
            people_in_plan.append(int(self.working_calendar_people_all[day+now_index]))
        return people_in_plan



    # 获取每一天的计划(小时)
    def get_day_plan(self):
        one_day_time_paln = []  # 每天的工作小时计划
        time_in_day = WorkingCalendarInDay()
        temp_time_str = time_in_day.start_time
        end_time_str = time_in_day.computing_time(temp_time_str,int((self.hours_in_day + time_in_day.rest_hours)*60))
        while temp_time_str != end_time_str:
            time_str = temp_time_str
            if temp_time_str == time_in_day.noon_rest_time.split('/')[0]:
                time_str = time_in_day.noon_rest_time
                temp_time_str = time_in_day.noon_rest_time.split('/')[1]
            if temp_time_str == time_in_day.afternoon_rest_time.split('/')[0]:
                time_str = time_in_day.afternoon_rest_time
                temp_time_str = time_in_day.afternoon_rest_time.split('/')[1]
            one_day_time_paln.append(time_str)
            temp_time_str = time_in_day.computing_time(temp_time_str,time_in_day.unit_time)
        one_day_time_paln.append(end_time_str)
        return one_day_time_paln

    # 获取任意时间经过多少工作日的日期 工作日天数转变为日期
    def get_date_after_days(self, days, now_date = '0'):
        if now_date == '0':
            now_date_str = self.start_date
        else:
            now_date_str = str(now_date).split(' ')[0]

        while True:
            if now_date_str in self.working_calendar_date_all:
                now_index = self.working_calendar_date_all.index(now_date_str)
                break
            else:
                now_date_str = datetime.datetime.strptime(now_date_str, "%Y-%m-%d") + datetime.timedelta(days=-1)
                now_date_str = str(now_date_str).split(' ')[0]
        try:
            after_date_str = self.working_calendar_date_all[now_index + days]
        except:
            print('出错：日期超出工作日历')
        return after_date_str


    # 求计划中的总工时
    def work_hours_within_specified_time(self):
        time_all = 0
        for i in self.working_calendar_people:
            time_all += int(i)*self.hours_in_day
        return time_all


    # 求规定的计划中的每周的截至时间(时间戳)，组成一个列表
    def get_deadline_weekly(self):
        week_time_list = [] #存每周的间隔
        for date_index in range(len(self.working_calendar_date)):
            max_day_in_week = 0 # 一周之中最大的天数
            time_temp = datetime.datetime.strptime(self.working_calendar_date[date_index],"%Y-%m-%d")
            this_day = datetime.datetime.date(time_temp).weekday()
            if date_index == 0 and this_day == 0:
                continue
            if this_day > max_day_in_week:
                max_day_in_week = this_day
            else:
                week_time_list.append((date_index)*self.hours_in_day/self.unit_time)
        week_time_list.append(self.during_days*self.hours_in_day/self.unit_time)
        week_time_list_set = set(week_time_list)
        week_time_list = sorted(week_time_list_set)
        return week_time_list

    # 求两个日期字符串之间的天数
    def days_between_two_date(self,start_time,end_time):
        start_datetime = datetime.datetime.strptime(start_time, "%Y-%m-%d")
        end_datetime = datetime.datetime.strptime(end_time, "%Y-%m-%d")
        delta = end_datetime - start_datetime
        days = delta.days
        return days


# 对一天中的时间进行规划，具体安排工人的作息
class WorkingCalendarInDay:
    start_time = config.start_time_day # 一天的开始时间
    noon_rest_time =config.noon_rest_time # 中午休息时间
    afternoon_rest_time = config.afternoon_rest_time  # 晚上休息时间
    unit_time = config.unit_time_day # 半个小时为间隔跳转(min)
    rest_hours = config.rest_hours  # 每天的休息时长(h)

    #小时计算
    def computing_time(self,time_str,minutes):
        '''
        :param time_str: 时间的字符串：20:00 06:00
        :param minutes: 分钟数
        :return: 返回加入分钟数之后的时间字符串
        '''
        hour,minute = map(int,time_str.split(':'))
        minute += minutes
        other_hour =hour + int(minute/60)
        other_minute = int(minute%60)
        return '{:02d}:{:02d}'.format(other_hour,other_minute)


    #

