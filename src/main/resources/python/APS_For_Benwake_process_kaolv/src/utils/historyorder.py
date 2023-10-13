import math
import csv
from src import config
# -*- coding: gbk -*-


# 历史遗留订单情况
class HistoryOrder:

    def __init__(self, code, product_id, process_name, product_num, process_num):

        self.id = code  # 订单编号
        self.product_id = product_id  # 订单所生产的产品编码
        self.process_name = process_name  # 工序名称
        self.production_num = int(product_num)  # 订单所需产品数量
        self.process_num = [int(i) for i in process_num]  # 工序剩余数量
        self.finish_time = ''  # 结束时间
        self.priority = '历史遗留订单'  # 优先级
        self.notes = code  # 生产计划包括的订单
        self.actually_start_time = '0'  # 实际开始时间

    # 计算结束时间
    def get_finish_time(self, history_time, working_calendar):
        if history_time.get(self.product_id) != None:
            self.finish_time = str(history_time[self.product_id]).split(' ')[0]
        else:
            self.finish_time = working_calendar.get_date_after_days(config.scheduled_days_num)

    # 得到对应机器的可用状态,可用的机器有哪些
    def get_machine_running_time(self, people_machines, product, working_calendar):
        self.machine_running = []
        self.people_number = []
        num = len(self.process_num)
        # 工序
        for i in range(product.process_num - num, product.process_num):

            process_list = []
            for j in range(len(people_machines.machine_list)):
                process_list.append({'machine':people_machines.machine_list[j]})
            self.machine_running.append(process_list)
            try:
                [max, min] = product.process_people_limit[(i * 2):(i * 2 + 2)]
            except:
                print('df')
            people_num_list = []
            # 人数
            for people_num in range(min, max+1):
                process_time = math.ceil(
                    self.process_num[(i + num) % product.process_num] * product.each_capacity[i] / working_calendar.unit_time / 3600 /
                    people_num)
                process_list = []
                for j in range(len(people_machines.machine_list)):
                    process_list.append({'machine': people_machines.machine_list[j],'end_time':float('inf'),'start_time':float('inf')})
                people_num_list.append({'people_num':people_num,'process_time':process_time,'machine_running':process_list})
            self.people_number.append(people_num_list)

    # 得到对应工序的最早结束时间以及对应的机器
    def get_earliest_end_machine(self,machine_running,people_num):
        machine = []
        machine_running.sort(key=lambda x: x['end_time'])  # 根据机器情况的最晚完成时间排序
        for i in range(people_num):
            machine.append(machine_running[i])
        start_time = machine[0]['start_time']
        end_time = machine[-1]['end_time']
        return machine,start_time,end_time

    # 计算是否完成，以及剩余的数量
    def get_remaining_quantity(self):
        remaining_num = []
        for i in range(len(self.completion_num)):
            remaining_num.append(self.process_num[i]- self.completion_num[i])
        num = len(remaining_num)
        for j in range(len(self.process_num) - num):
            remaining_num.append(self.process_num[j+num])
        status = True
        for i in remaining_num:
            if i != 0:
                status = False
                break
        return status,remaining_num


# 历史遗留计划
class HistoryOrderPlan:

    def __init__(self,address):
        self.history_order_plan = []  # 历史订单信息
        temp_code = ''  # 当前订单编码
        temp_product_id = ''  # 当前产品id
        temp_product_num = 0  # 产品数量
        temp_process_num = []  # 工序数量
        temp_process_name = []  # 工序名称
        with open(address, newline='', encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            for row in reader:
                if temp_code == '':  # 首次
                    temp_code = row[0]
                    temp_process_num.append(int(row[3]) - int(row[4]))
                    temp_process_name.append(row[2])
                    temp_product_id = row[1]
                    temp_product_num = int(row[3])
                elif temp_code == row[0]:
                    temp_process_num.append(int(row[3]) - int(row[4]))
                    temp_process_name.append(row[2])
                elif temp_code != row[0]:
                    self.history_order_plan.append(HistoryOrder(
                        temp_code,
                        temp_product_id,
                        temp_process_name,
                        temp_product_num,
                        temp_process_num
                    ))  # 生成订单
                    temp_code = row[0]
                    temp_process_num = [int(row[3]) - int(row[4])]
                    temp_process_name = [row[2]]
                    temp_product_id = row[1]
                    temp_product_num = int(row[3])

            self.history_order_plan.append(HistoryOrder(
                temp_code,
                temp_product_id,
                temp_process_name,
                temp_product_num,
                temp_process_num
            ))  # 生成订单
        self.history_num = len(self.history_order_plan)  # 历史订单数量

    # 求在产的产品类型的数量
    def get_production_num_dict(self):
        production_num_dict = {}
        for i in self.history_order_plan:
            if production_num_dict.get(i.product_id, 0) == 0:
                production_num_dict[i.product_id] = i.production_num
            elif production_num_dict.get(i.product_id, 0) != 0:
                production_num_dict[i.product_id] += i.production_num
        return production_num_dict

    # 求历史遗留的完成时间
    def get_finish_time_all(self, history_time, working_calendar):
        for i in self.history_order_plan:
            i.get_finish_time(history_time, working_calendar)

    # 求历史遗留订单的数量
    def get_history_order_num(self):
        return len(self.history_order_plan)  # 订单数量

