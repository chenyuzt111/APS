from src import config
import csv
import datetime
import math
# -*- coding: gbk -*-


# 机器
class Machine:
    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.operation = []
    def get_max_time(self): # 得到运行机器的最大时间
        max_time = 0
        for job in self.operation:
            if max_time < job[1]:
                max_time = job[1]
        return max_time


# 人数组合机器
class PeopleMachine(Machine):
    def __init__(self, id, name, people_num):
        Machine.__init__(self, id, name)
        self.people_num = people_num



# 人数
class People(Machine):
    def __init__(self, id, name):
        Machine.__init__(self, id, name)



# 人数机器集合
class MachinesSet:
    max_people_in_machine = config.max_people_in_machine  # 单个机器的最大人数
    set_name = '人数组合'
    def __init__(self, people_num):
        self.machine_list = []
        index = 0
        # 生成人数为1的机器
        for i in range(people_num):# 人数为1 的机器数量
            people = 1
            name = 'ID'+str(i+1)
            self.machine_list.append(PeopleMachine(i, name, people))
            index += 1

        # # 生成其他人数的机器
        # for people in range(2,self.max_people_in_machine+1):
        #     # 获得当前人数中，以i人一组的组合个数
        #     j = int(people_num / people)
        #     for z in range(j):
        #         # 增加j个以i为一组的组合人数
        #         name = 'ID' + str(index + 1) + '-' + str(people)
        #         self.machine_list.append(PeopleMachine(index,name,people))
        #         index += 1

    # 获取人数机器的范围集合
    def get_index_set(self, min, max):
        begin = 0
        end = 0
        for i in self.machine_list:
            if begin == 0:
                if i.people_num >= min:
                    begin = i.id
                    break
        for i in self.machine_list:
            if end == 0:
                if max == self.max_people_in_machine:
                    end = len(self.machine_list)
                elif i.people_num > max:
                    end = i.id
        return [begin, end]

    # 获取所有机器的利用效率
    def get_utilization_efficiency(self, working_calendar):
        total_people_time = 0  # 所有机器的总时间
        time_all = working_calendar.work_hours_within_specified_time() # 规定时间内的总工时
        for machine in self.machine_list:
            if machine.operation != []:
                for job in machine.operation:
                    total_people_time += (job[1] - job[0])*machine.people_num
        return total_people_time/(time_all/working_calendar.unit_time)

    # 获取所有机器的总工时
    def get_total_work(self, working_calendar):
        total_people_time = 0  # 所有机器的总时间
        time_all = working_calendar.work_hours_within_specified_time()  # 规定时间内的总工时
        for machine in self.machine_list:
            if machine.operation != []:
                for job in machine.operation:
                    total_people_time += (job[1] - job[0])*machine.people_num
        return total_people_time


# 测试机器
class TestMachineSet:
    set_name = '测试工序机器'
    def __init__(self,name_list):
        self.machine_list = []
        for i in range(len(name_list)):
            self.machine_list.append(Machine(i, name_list[i]))


# 所有机器
class AllMachineSet:
    set_name = '所有工序机器'
    def __init__(self, address):
        self.machine_list = []
        with open(address, newline='', encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            for row in reader:
                self.machine_list.append(Machine(row[0], row[4]))

