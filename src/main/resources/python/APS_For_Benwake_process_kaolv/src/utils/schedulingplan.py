import math
import time
import csv
import datetime
from collections import Counter
import random
from src import config
import copy
# -*- coding: gbk -*-


# 排产计划类
class SchedulingPlan:

    def __init__(self, history_orders, product_orders):
        self.scheduling_list = []
        self.product_num = len(product_orders)
        if history_orders != None:
            self.history_num = len(history_orders.history_order_plan)
            self.scheduling_list = history_orders.history_order_plan + product_orders
        else:
            self.history_num = 0
            self.scheduling_list = product_orders

    # 获得每个订单的可用机器情况
    def get_machine_running_time(self, machines_set, Product_control, working_calendar):
        for i in self.scheduling_list:  # i 单个任务
            i.temp_processing_time = []  # 工序加工时间
            product = Product_control.product_all[i.product_id]
            i.get_machine_running_time(machines_set, product, working_calendar)

    # 对加工顺序初始化
    def initialize_queue(self):
        self.queue = []
        # 1. 订单任务的前两个工序
        self.queue = self.origin_order_sorted()
        # 2. 组装工序的安排
        self.early_insertion_for_last()
        # 3. 提前处理 "PR" 类型的订单
        # self.prioritize_pr_orders()
        # 4. 提前处理 "PO" 类型的订单
        self.prioritize_po_orders()
        # 5. 将历史遗留订单插入到队列的最开始，因为它比PO优先级要高
        self.queue = self.history_sorted() + self.queue

    # 将PO类型订单插入到最开始，实现能排的时候先排，和预计开始时间时间没关系
    def prioritize_po_orders(self):
        # 获取所有PO订单的索引
        po_order_indices = [i for i, order in enumerate(self.queue) if self.scheduling_list[order].priority[:2] == 'PO']

        if not po_order_indices:
            # 如果没有PO订单，无需重新排序
            return

        # 重新排序队列，将PO订单放到最前面
        new_queue = []
        for index in po_order_indices:
            new_queue.append(self.queue[index])

        # 将非PO订单添加到新队列中
        for order_index in range(len(self.queue)):
            if order_index not in po_order_indices:
                new_queue.append(self.queue[order_index])

        # 更新队列
        self.queue = new_queue

    # 将PR类型订单插入到PO后边，实现能排的时候先排，和预计开始时间时间没关系
    # def prioritize_pr_orders(self):
    #     # 获取所有PR订单的索引
    #     pr_order_indices = [i for i, order in enumerate(self.queue) if
    #                         self.scheduling_list[order].priority[:2] == 'PR']
    #
    #     if not pr_order_indices:
    #         # 如果没有PR订单，无需重新排序
    #         return
    #
    #     # 重新排序队列，将PR订单放到PO后
    #     new_queue_pr = []
    #     for index in pr_order_indices:
    #         new_queue_pr.append(self.queue[index])
    #
    #     # 将非PR订单添加到新队列中
    #     for order_index in range(len(self.queue)):
    #         if order_index not in pr_order_indices:
    #             new_queue_pr.append(self.queue[order_index])
    #
    #     # 更新队列
    #     self.queue = new_queue_pr

    # 对历史订单进行排序
    def history_sorted(self):
        random.seed(time.time())
        OS = []
        # 历史存留
        i = 0
        for job in range(0, self.history_num):  # 作业编号的第i出现表示该作业的第i次操作
            for op in range(len(self.scheduling_list[job].process_name)):
                OS.append(i)
            i = i + 1
        random.shuffle(OS)
        return OS

    # 对生产订单进行排序
    def origin_order_sorted(self):
        random.seed(time.time())
        inserted_orders_Priority = self.get_sort_priority()
        # 每个优先级的订单个数
        list_priority_num = Counter(inserted_orders_Priority)

        OS = []
        now_job = self.history_num
        # pr是优先级
        for pr in list_priority_num:
            # 相同优先级的个数
            num_temp = list_priority_num[pr]
            # 定义相同优先级的顺序列表
            priority_list = []
            for i in range(now_job, now_job + num_temp):
                priority_list.append(i)
            for i in range(2):
                random.shuffle(priority_list)
            for i in priority_list:
                for j in range(len(self.scheduling_list[i].process_num) - 1):
                    OS.append(i)
            now_job += num_temp
        # 工序三
        now_job = self.history_num
        # pr是优先级
        for pr in list_priority_num:
            # 相同优先级的个数
            num_temp = list_priority_num[pr]
            # 定义相同优先级的顺序列表
            priority_list = []
            for i in range(now_job, now_job + num_temp):
                priority_list.append(i)
            for i in range(2):
                random.shuffle(priority_list)
            for i in priority_list:
                OS.append(i)
            now_job += num_temp
        return OS

    # 提前工序三顺序
    def early_insertion_for_last(self):
        random.seed(time.time())
        last_process_list = self.queue[-self.product_num:]
        Maximum_allowed_insertion_position = self.get_end_insert_location()
        # 删除后几个元素
        for i in last_process_list:
            self.queue.pop()
        for i in last_process_list:
            # 获得开始下标
            start_location = self.queue.index(i)
            if self.scheduling_list[i].priority[:2] == 'PO':
                location_temp = start_location
            else:
                if Maximum_allowed_insertion_position[i - self.history_num] + 1 == self.product_num:
                    end_location = len(self.queue)
                # 获得随机索引值
                else:
                    end_location = self.queue.index(Maximum_allowed_insertion_position[i - self.history_num])
                try:
                    location_temp = random.randint(start_location, end_location)
                except:
                    end_location = len(self.queue)
                    location_temp = random.randint(start_location, end_location)
            # 插入
            self.queue.insert(location_temp, i)

    # 得到任务的优先级
    def get_sort_priority(self):
        inserted_orders_Priority = []
        for i in range(self.history_num, self.history_num + self.product_num):
            if inserted_orders_Priority == []:
                inserted_orders_Priority.append(1)
            else:
                if self.scheduling_list[i].start_time == self.scheduling_list[i - 1].start_time:
                    inserted_orders_Priority.append(inserted_orders_Priority[-1])
                else:
                    inserted_orders_Priority.append(inserted_orders_Priority[-1] + 1)
        return inserted_orders_Priority

    # 求生产订单中结束时间所对应的开始时间的任务号
    def get_end_insert_location(self):
        Maximum_allowed_insertion_position = []
        for i in range(self.history_num, self.history_num + self.product_num):
            for j in range(self.history_num, self.history_num + self.product_num):
                if datetime.datetime.strptime(self.scheduling_list[i].finish_time,
                                              "%Y-%m-%d") <= datetime.datetime.strptime(
                    self.scheduling_list[j].start_time, "%Y-%m-%d"):
                    Maximum_allowed_insertion_position.append(j)
                    break
                if j == self.history_num + self.product_num - 1:
                    Maximum_allowed_insertion_position.append(j)
        return Maximum_allowed_insertion_position

    # 计算库存所需要的开始时间
    def consider_the_material(self, job, process_id, production_num, Product_control, working_calendar, writer_all=None,
                              writer_all_origin=None,writer_all_semi = None):
        product_id = self.scheduling_list[job].product_id  # 物料编码
        now_process_name = None
        if process_id == 0:
            now_process_name = '组装'
        if process_id == len(self.scheduling_list[job].process_name) - 1:
            now_process_name = '包装'
        if now_process_name == None:
            return 0  # 如果工序不是组装第一个工序和包装第一个工序，则不考虑物料
        # 检测物料是否缺货，以及补货的最大时间
        final_time, son_code = Product_control.product_all[product_id].determine_replenishment_time(production_num,
                                                                                          now_process_name,
                                                                                          working_calendar, job,
                                                                                 writer_all, writer_all_origin,writer_all_semi)
        if process_id == 0:  # 得到有货时间
            day = math.floor(
                (final_time * working_calendar.unit_time) / working_calendar.hours_in_day)
            self.scheduling_list[job].material_time = working_calendar.get_date_after_days(day)
            self.scheduling_list[job].Key_materials = son_code
        return final_time

    # 确定开始时间之后，更新原材料以及对应的库存
    def update_material_inventory(self, job, process_id, start_time, production_num, Product_control, working_calendar):
        product_id = self.scheduling_list[job].product_id  # 物料编码
        now_process_name = None
        if process_id == 0:
            now_process_name = '组装'
        if process_id == len(self.scheduling_list[job].process_name) - 1:
            now_process_name = '包装'
        if now_process_name != None:
            # print(str(job)+'--'+product_id+'--------成品消耗--------'+now_process_name)
            # 更新对应的库存
            Product_control.product_all[product_id].update_material_inventory(start_time, production_num,
                                                                              now_process_name, working_calendar)

    # 开始排程
    def decode(self, Product_control, working_calendar, machines_set, test_machine_list, LAST=False):
        # 对物料进行初始化
        for i in range(len(machines_set.machine_list)):
            machines_set.machine_list[i].operation = []  # 最后的结果 人数机器

        for i in range(len(test_machine_list)):
            test_machine_list[i].operation = []  # 最后的结果 测试机器

        for i in range(len(self.scheduling_list)):
            self.scheduling_list[i].temp_processing_time = []  # 工序加工时间
            self.scheduling_list[i].start_task_cstr = 0  # 初始化 每个任务的当前已加工时间，每项操作的允许开始时间
            self.scheduling_list[i].start_task_cstr_parallel = 0  # 初始化 每个任务的并行已加工时间，每项操作的允许开始时间
            self.scheduling_list[i].index = 0  # 任务进行到了第几个工序
            if self.scheduling_list[i].priority != '历史遗留订单':  # 历史遗留不考虑
                self.scheduling_list[i].material_time = 0
            if 'completion_num' in self.scheduling_list[i].__dict__:
                del self.scheduling_list[i].completion_num

        # 得到半成品的制作或购买计划
        writer_all = None
        writer_all_origin = None
        writer_all_semi = None
        if LAST:
            # 半成品的制作计划 以及 每个任务原材料有货的时间
            f_semi = open('data/output_data/csv/semi_item_control_work_plan.csv', 'w', newline='')
            writer_all = csv.writer(f_semi)
            head = ['物料编码', '数量', '开始制作时间', '需入库时间']
            writer_all.writerow(head)
            f_origin = open('data/output_data/csv/origin_material_availability_time.csv', 'w', newline='')
            writer_all_origin = csv.writer(f_origin)
            head = ['任务号', '父物料编码', '父物料名称', '工序', '子物料编码', '子物料所需数量', '子物料有货时间']
            writer_all_origin.writerow(head)
            f_writer_all_semi = open('data/output_data/csv/semi_material_availability_time.csv', 'w', newline='')
            writer_all_semi = csv.writer(f_writer_all_semi)
            head = ['任务号', '父物料编码', '父物料名称', '工序','半成品编码','半成品所需数量', '子物料编码', '子物料所需数量', '子物料有货时间']
            writer_all_semi.writerow(head)

        # 遍历得到最好的解
        for job in self.queue:
            test_machine = None  # 测试机器
            index = self.scheduling_list[job].index  # 当前进行到第几个工序
            start_time_for_material = 0
            if config.consider_the_material:  # 考虑物料
                if self.scheduling_list[job].priority != '历史遗留订单':  # 历史遗留不考虑
                    production_num = self.scheduling_list[job].production_num
                    start_time_for_material = self.consider_the_material(job, index, production_num, Product_control,
                                                                         working_calendar, writer_all, writer_all_origin, writer_all_semi)
            # 遍历人数组合，在人数组合下选择对应的人

            people_combine = self.scheduling_list[job].people_number[index][-1]
            people_num = people_combine['people_num']
            process_time = people_combine['process_time']

            # 遍历一个人
            completion_num_all = 0
            start_time_all = 0
            end_time_all = 0

            for peo_num in range(people_num):
                for i in people_combine['machine_running']:  # 遍历所有机器
                    # 可以获取加工时间和人数
                    # 约束条件3： 当前任务要在先序工序结束后在开始
                    max_test_time = i['machine'].get_max_time()  # 当前机器的最大结束时间
                    max_task_time = self.get_max_running_time()  # 当前所有任务的最大结束时间

                    if config.produce_in_parallel:  # 并行逻辑 单人逻辑需要重新计算
                        start_cstr = self.parallel_production_compute_start(process_time, job)
                    else:
                        start_cstr = int(self.scheduling_list[job].start_task_cstr)

                    # 考虑YC、XD、PO的开始生产时间，不加对PO的限制就可能会使得PO优先级高于历史遗留订单
                    start_time_yc = 0
                    if (index == 0 and self.scheduling_list[job].priority[:2] == 'YC') or (
                        index == 0 and self.scheduling_list[job].priority[:2] == 'XD'):
                        start_time_yc = working_calendar.get_time_period(self.scheduling_list[job].start_time)
                    # 增加约束条件：YG不能提前太久，需要根据用户输入进行时间范围的确定，主要是修改开头
                    elif index == 0 and self.scheduling_list[job].priority[:2] == 'YG':
                        date_obj = datetime.datetime.strptime(self.scheduling_list[job].finish_time, "%Y-%m-%d")
                        result_date_obj = date_obj + datetime.timedelta(days=config.yg_delta)
                        start_time_first = datetime.datetime.strptime(config.start_time, "%Y-%m-%d")
                        if result_date_obj > start_time_first:
                            result_date_str = result_date_obj.strftime("%Y-%m-%d")
                            start_time_yc = working_calendar.get_time_period(result_date_str)

                    start_cstr = int(max(start_time_for_material, start_cstr, start_time_yc))  # 物料的时间
                    if start_cstr >= len(working_calendar.people_in_plan):
                        self.scheduling_list[job].start_task_cstr = len(working_calendar.people_in_plan)
                        self.scheduling_list[job].start_task_cstr_parallel = len(working_calendar.people_in_plan)
                    start_cstr_temp = int(self.scheduling_list[job].start_task_cstr)  # 开始时间
                    if start_cstr_temp >= len(working_calendar.people_in_plan):
                        continue
                    max_duration = int(
                            max(max_task_time, max_test_time, start_cstr_temp, start_cstr) + process_time)  # 最大结束时间
                    Time_people_list = working_calendar.people_in_plan[start_cstr:max_duration]  # 人数列表
                    machine_list_this_process = Product_control.product_all[self.scheduling_list[job].product_id].type[index]

                    test_machine = []
                    if machine_list_this_process != ['']:
                        for machine_id in machine_list_this_process:
                            if machine_id != '':
                                test_machine.append(self.get_test_machine(machine_id, test_machine_list, config.Consider_the_process))

                        test_machine_time = 0
                        for machine_test in test_machine:
                                if machine_test.get_max_time() > test_machine_time:
                                    test_machine_time = machine_test.get_max_time()
                        max_duration = int(
                                max(test_machine_time,max_task_time, max_test_time, start_cstr_temp, start_cstr) + process_time)  # 最大结束时间
                        Time_people_list = working_calendar.people_in_plan[start_cstr:max_duration]  # 人数列表
                        start_time = self.find_first_available_place(start_cstr, max_duration, i, process_time,machines_set.machine_list,
                                                                         Time_people_list, working_calendar, test_machine)
                    else:
                        start_time = self.find_first_available_place(start_cstr, max_duration, i, process_time,machines_set.machine_list,
                                                                         Time_people_list, working_calendar)  # 开始时间计算

                    i['start_time'] = start_time

                    i['end_time'] = start_time + process_time

                # 得到最早的结束时间以及相对应的机器,单个机器
                people_combine['information_in_machine'], people_combine['mut_start_time'], people_combine['mut_end_time'] = self.scheduling_list[job].get_earliest_end_machine(
                people_combine['machine_running'], 1)

                # 选择出最优人数的情况

                # 如果找不到合适的人数情况
                if self.scheduling_list[job].people_number[index][-1]['mut_end_time'] == float('inf'):
                    if peo_num == people_num - 1:
                        self.scheduling_list[job].start_task_cstr = len(working_calendar.people_in_plan)
                        self.scheduling_list[job].start_task_cstr_parallel = len(working_calendar.people_in_plan)
                    if 'completion_num' not in self.scheduling_list[job].__dict__:
                        self.scheduling_list[job].completion_num = []
                    if peo_num == people_num - 1:
                        self.scheduling_list[job].completion_num.append(completion_num_all)
                    continue

                # 临界点判断,是否超过排程的最大时间
                start_time = self.scheduling_list[job].people_number[index][-1]['mut_start_time']

                # 新增生产计划的完工状态
                if self.scheduling_list[job].index == 0:
                    self.scheduling_list[job].completion_num = []
                # 若开始时间大于最大时间,跳过
                if start_time >= len(working_calendar.people_in_plan):
                    if peo_num == people_num - 1:
                        self.scheduling_list[job].start_task_cstr = len(working_calendar.people_in_plan)
                        self.scheduling_list[job].start_task_cstr_parallel = len(working_calendar.people_in_plan)
                    start_time_all = len(working_calendar.people_in_plan)
                    end_time_all = len(working_calendar.people_in_plan)
                    continue
                # 结束时间大于最大时间，跳过 修改结束时间到最大时间

                if peo_num == 0:
                    start_time_all = start_time
                if peo_num == people_num - 1:
                    end_time_all = self.scheduling_list[job].people_number[index][-1]['mut_end_time']
                # name_task 为当前第几个任务物料的第几个工序
                product_process_num = Product_control.product_all[self.scheduling_list[job].product_id].process_num
                name_task = "{}-{}".format(job, product_process_num - len(self.scheduling_list[job].process_name) +
                                               self.scheduling_list[job].index+1)

                for machine in self.scheduling_list[job].people_number[index][-1]['information_in_machine']:
                        machine['machine'].operation.append([machine['start_time'], machine['end_time'], name_task])


                if test_machine != []:
                    for machine_test in test_machine:
                        machine_test.operation.append([start_time, process_time + start_time, name_task])

                # 主要记录完成个数
                completion_num_temp = 0
                for machine in self.scheduling_list[job].people_number[index][-1]['information_in_machine']:
                    if machine['end_time'] > len(working_calendar.people_in_plan):
                        machine['end_time'] = len(working_calendar.people_in_plan)
                        prcTime = len(working_calendar.people_in_plan) - machine['start_time']
                        completion_num_temp += int(
                            (prcTime * self.scheduling_list[job].process_num[self.scheduling_list[job].index]) /
                            self.scheduling_list[job].people_number[index][-1]['process_time'])

                    else:
                        prcTime = self.scheduling_list[job].people_number[index][-1]['process_time']
                        completion_num_temp += self.scheduling_list[job].process_num[self.scheduling_list[job].index]
                completion_num_all += completion_num_temp
                if peo_num == people_num - 1:
                    self.scheduling_list[job].completion_num.append(completion_num_temp)

            # 并行生产
            prcTime_parallel = self.scheduling_list[job].people_number[index][-1]['process_time']
            self.scheduling_list[job].index += 1  # 更新每个任务的工序数量

            self.scheduling_list[job].temp_processing_time.append(end_time_all - start_time_all)  # 更新加工时间

            # 修改每个任务的完工时间
            if self.scheduling_list[job].start_task_cstr < int(end_time_all):
                self.scheduling_list[job].start_task_cstr = int(end_time_all)
                # 修改每个任务的完工时间
                self.scheduling_list[job].start_task_cstr_parallel = int((end_time_all))

            if config.Consider_the_process:
                # 根据工艺路径修改机器所使用情况,任务号，工序号，所用机器
                for machine in self.scheduling_list[job].people_number[index][-1]['information_in_machine']:
                    self.update_remaining_operations_machine_usage(job, index, machine['machine'].id,
                                                               Product_control)

            # 修改原材料库存信息
            if config.consider_the_material:
                if self.scheduling_list[job].priority != '历史遗留订单' and end_time_all != 0:  # 历史遗留不考虑
                    production_num = self.scheduling_list[job].production_num
                    self.update_material_inventory(job, index, start_time_all, production_num, Product_control,
                                                   working_calendar)

        # 得到每个任务的结束时间，不一定完成
        self.get_end_time(working_calendar)
        # 根据结束时间以及完成情况确定是否在规定的时间完成，有三种Yes,No,Uncertain
        self.get_delivery_status()
        # 获取订单的完成率
        self.Completion_rate_PO, self.Completion_rate_all = self.get_Completion_rate()
        # 获取PR订单的完成率
        self.Completion_rate_PR, self.Completion_rate_all1 = self.get_Completion_rate_pr()
        # 工人利用率，总体时间
        self.worker_utilization = machines_set.get_utilization_efficiency(working_calendar)
        # 关闭半成品购买文件
        if LAST:
            f_semi.close()
            f_origin.close()
            f_writer_all_semi.close()

    # 寻找第一个可用位置
    def find_first_available_place(self, start_cstr, max_duration, machine_dict,process_time, machines_list, Time_people_list,
                                   working_calendar, test_machine=None):
        machine_used = [True] * int(max_duration)
        # 约束条件1：不能和当前人数机器的其他任务冲突
        machine_used = self.update_available_time(machine_used, machine_dict['machine'].operation)  # 人数机器的可用状态
        if test_machine is not None:
            for machine_test in test_machine:
                if machine_test != None:
                    # 约束条件2：测试不能和当前测试机器的其他任务冲突
                    machine_used = self.update_available_time(machine_used, machine_test.operation)  # 测试机器的可用状态

        # 约束条件4：人数限制约束，任意时刻不能超过最大人数
        machine_used = self.limit_maximum_people_num(machine_used, machine_dict['machine'], start_cstr,
                                                         max_duration, machines_list, Time_people_list,
                                                         working_calendar)  # 任务在考虑最大人数下的可用状态
        # 约束条件5：人数限制约束，按照人员的具体出勤时间

        # 查找第一个符合约束的可用位置
        # 从产品之前工序的结束时间开始，到机器的使用时长，开始见缝插针
        for k in range(start_cstr, max_duration):
            if self.is_free(machine_used, k, process_time):
                # k就是当前任务的开始时间
                return k

    # 更新剩余机器的使用情况
    def update_remaining_operations_machine_usage(self, job, process_index, machine_id, Product_control):
        # 获取对应物料
        product_id = self.scheduling_list[job].product_id
        process_index += Product_control.product_all[product_id].process_num - len(self.scheduling_list[job].process_name)
        next_process = 0
        # 获取对应关联规则
        for i in Product_control.product_all[product_id].route_information:
            if process_index + 1 == i[0]:
                next_process = i[1] - 1
                break
        if next_process != 0:  # 开始更改机器的种类
            # 首先将对应工序的可用机器进行替换为当前机器
            temp_machine_list = []  # 可用的机器列表
            try:
                for machine in self.scheduling_list[job].people_number[next_process][-1]['machine_running']:
                    if machine['machine'].id == machine_id:
                        temp_machine_list.append(machine)
                        break
            except:
                print('dfs')
            self.scheduling_list[job].people_number[next_process][-1]['machine_running'] = temp_machine_list  # 进行替换

        # if next_process != 0:  # 替换其他工序的机器
        #     for process in range(process_index + 1,
        #                          len(self.scheduling_list[job].people_number) - 2 ):  # 不删除最后两个工序测试和包装
        #         if process != next_process:  # 除对应关联工序以外的其他工序
        #             temp_machine_index = 0
        #             for machine_index in range(len(self.scheduling_list[job].people_number[process][-1]['machine_running'])):
        #                 if self.scheduling_list[job].people_number[process][-1]['machine_running'][machine_index][
        #                     'machine'].id == machine_id:
        #                     temp_machine_index = machine_index
        #                     del self.scheduling_list[job].people_number[process][-1]['machine_running'][temp_machine_index]
        #                     break

    # 并行生产的时间间隔提前
    # 并行生产计算开始时间
    def parallel_production_compute_start(self, process_time, job):
        if self.scheduling_list[job].temp_processing_time == []:  # 任务的首个工序
            start_cstr = 0  # 开始时间
        else:  # 非首个工序
            processing_time = process_time  # 当前工序的加工时间
            last_process_time = self.scheduling_list[job].temp_processing_time[-1]  # 上个工序的加工时间
            processing_time_for_bach = math.ceil(
                ((processing_time * config.bach_size) / self.scheduling_list[job].production_num))  # 产一批bach_size需要的时间
            last_process_time_for_bach = math.ceil(
                ((last_process_time * config.bach_size) / self.scheduling_list[job].production_num))
            if processing_time > last_process_time:
                start_cstr = self.scheduling_list[
                                 job].start_task_cstr_parallel - last_process_time + last_process_time_for_bach
            else:
                start_cstr = self.scheduling_list[
                                 job].start_task_cstr_parallel + processing_time_for_bach - processing_time
        return start_cstr

    def is_free(self, tab, start, duration):
        '''
        检查机器在start时刻开始执行此工艺，能不能塞下长度为duration的时段，
        :param tab: machines机器运行情况，从零开始到最长工艺的执行
        :param start: 工艺开始时间
        :param duration: 工艺的加工时间
        :return: 返回是否可以从start时刻开始，塞下工艺的加工时段
        '''
        for k in range(start, start + duration):
            try:
                if not tab[k]:
                    return False
            except:
                print('scheduling428出现错误')
        return True

    # 根据机器的使用情况修正当前机器的可用状态，不可用为False，可用为True
    def update_available_time(self, machine_used, operation):
        for job in operation:
            end = job[1]
            start = job[0]
            for k in range(start, end):
                if k < len(machine_used):
                    # 根据机器所执行的工序，得到机器从零到max_duration的运行情况
                    machine_used[k] = False
        return machine_used

    # 根据其他人数机器的运行情况，来限制最大人数
    def limit_maximum_people_num(self, machine_used, this_machine, start_cstr, max_duration, machines_list, Time_people_list, working_calendar):
        # 遍历machine_all,求出所用的机器运行情况
        machine_used_all = []
        for i in range(len(machines_list)):
            # if machines_list[i] != this_machine:
            machine_used_singer = [True] * max_duration
            machine_used_singer = self.update_available_time(machine_used_singer, machines_list[i].operation)
            machine_used_all.append(machine_used_singer)
        # 考虑人数限制
        # 如果排程单位中的人数列表不满足任务的完成,增加时间
        if len(Time_people_list) < max_duration - start_cstr:
            # 规定时间出勤人数列表
            Time_people_list = working_calendar.get_people_in_plan(max_duration)
            Time_people_list = Time_people_list[start_cstr:]
        # 修改人数限制
        for time in range(start_cstr, max_duration):
            # 当前时刻的人数
            this_people = 0
            for i in range(len(machine_used_all)):
                if machine_used_all[i][time] == False:
                    this_people += machines_list[i].people_num
            try:
                if int(this_machine.people_num + this_people) > int(Time_people_list[time - start_cstr]):
                    machine_used[time] = False
            except:
                print('limit_maximum_people_num出现错误')
        return machine_used

    # 得到机器id列表组合----暂时无用
    def get_ms(self):
        ms = []
        for job in self.scheduling_list:
            job_machines = []
            for process in job.machine_running:
                process_machines = []
                for machine in process:
                    process_machines.append(machine['machine'].id)
                job_machines.append(process_machines)
            ms.append(job_machines)
        return ms

    # 得到当前所有任务的最大运行时间
    def get_max_running_time(self):
        max = 0
        for i in self.scheduling_list:
            if max < i.start_task_cstr:
                max = i.start_task_cstr
        return max

    # 获得PO订单的完成率
    def get_Completion_rate(self):
        yes_num = 0  # yes的个数
        no_num = 0  # 总个数
        yes_num_po = 0  # yes的个数
        no_num_po = 0  # 总个数
        for i in self.scheduling_list:
            if i.delivery_status == 'Yes':
                yes_num += 1
                if i.priority == 'PO(客户付款)':
                    yes_num_po += 1
            if i.delivery_status == 'No':
                no_num += 1
                if i.priority == 'PO(客户付款)':
                    no_num_po += 1
        po_all = yes_num_po + no_num_po
        if yes_num_po + no_num_po == 0:
            po_all = 1
        try:
            return float(yes_num_po / (po_all)), float(yes_num / (yes_num + no_num))
        except:
            return 1

    # 获得PR订单的完成率
    def get_Completion_rate_pr(self):
        yes_num = 0  # yes的个数
        no_num = 0  # 总个数
        yes_num_po = 0  # yes的个数
        no_num_po = 0  # 总个数
        for i in self.scheduling_list:
            if i.delivery_status == 'Yes':
                yes_num += 1
                if i.priority[:2] == 'PR':
                    yes_num_po += 1
            if i.delivery_status == 'No':
                no_num += 1
                if i.priority[:2] == 'PR':
                    no_num_po += 1
        po_all = yes_num_po + no_num_po
        if yes_num_po + no_num_po == 0:
            po_all = 1
        try:
            return float(yes_num_po / (po_all)), float(yes_num / (yes_num + no_num))
        except:
            return 1

    # 计算完成状态
    def get_delivery_status(self):
        for i in self.scheduling_list:
            i.completion_status, i.remaining_num = i.get_remaining_quantity()  # 是否完成，剩余数量
            if datetime.datetime.strptime(i.end_time, "%Y-%m-%d") > datetime.datetime.strptime(i.finish_time,
                                                                                               "%Y-%m-%d"):
                i.delivery_status = 'No'
            else:
                if i.completion_status:
                    i.delivery_status = 'Yes'
                else:
                    i.delivery_status = 'Uncertain'

    # 计算任务的结束时间
    def get_end_time(self, working_calendar):
        for i in self.scheduling_list:
            day = int(i.start_task_cstr / (working_calendar.hours_in_day / working_calendar.unit_time))
            i.end_time = working_calendar.get_date_after_days(day)

    # 得到对应的机器
    def get_test_machine(self, machine_id, test_machine_list, Consider_the_process):
        test_machine = None
        if Consider_the_process:
            test_machine = test_machine_list[int(machine_id)-1]
        else:
            for i in test_machine_list:
                if i.name == machine_id:
                    test_machine = i  # 寻找对应的机器
                    break
        return test_machine

    # 寻找最好的状态
    def search_the_best(self, Product_control, working_calendar, machines_set, test_machine_list, totalRowMaterial):
        self.number_cycles = config.number_cycles  # 循环次数
        best_score = 0  # 最好的分数
        best_queue = []  # 最好情况下的队列
        for i in range(self.number_cycles):
            if i > 0:
                self.get_machine_running_time(machines_set, Product_control,
                                  working_calendar)  # 对于每个订单都有对于的物料 ，每个物料都有对于的人数以及加工时间
            totalRowMaterial_test = copy.deepcopy(totalRowMaterial)
            # 获取成品的关联关系
            if config.consider_the_material:
                Product_control.get_orrespondence_for_singer(totalRowMaterial_test)
            self.initialize_queue()  # 生成队列

            self.decode(Product_control, working_calendar, machines_set, test_machine_list)  # 得到结果
            if 0.3 * self.Completion_rate_all + 0.5 * self.Completion_rate_PO + 0.2 * self.worker_utilization > best_score:
                best_score = 0.3 * self.Completion_rate_all + 0.5 * self.Completion_rate_PO + 0.2 * self.worker_utilization
                best_queue = self.queue
            print('循环次数：' + str(i) + '次')
        return best_queue

    # 将数据转化为scv图表
    def get_completion_to_csv(self, Product_control, working_calendar):
        # 任务id,来源id,物料编码,物料名称，总数量，完成数量，实际完成时间，需入库时间，是否完成，优先级，所包含订单
        f = open('data/output_data/csv/all_plan_completion_state.csv', 'w', newline='')
        writer_all = csv.writer(f)
        head = ['任务号', '任务来源ID', '物料编码', '物料名称', '总数量', '完成数量', '实际开始时间', '实际完成时间', '需入库时间',
                '是否按时完成', '延期天数', '优先级', '未完成原因', '所包含订单']
        writer_all.writerow(head)
        for job_id in range(len(self.scheduling_list)):
            product_name = Product_control.product_all[self.scheduling_list[job_id].product_id].name.encode("gbk",
                                                                                                            'ignore').decode("gbk", "ignore")
            self.scheduling_list[job_id].reason_no = '无'
            if self.scheduling_list[job_id].delivery_status == 'No':
                if self.scheduling_list[job_id].priority == '历史遗留订单':

                    self.scheduling_list[job_id].reason_no = '优先级不足'
                elif self.scheduling_list[job_id].actually_start_time == '未开始':
                    if datetime.datetime.strptime(self.scheduling_list[job_id].material_time,
                                                  "%Y-%m-%d") <= datetime.datetime.strptime(
                        self.scheduling_list[job_id].end_time, "%Y-%m-%d"):  # 有货时间小于最大排程时间
                        if self.scheduling_list[job_id].Key_materials == '0':
                            self.scheduling_list[job_id].reason_no = '优先级不足'
                        else:
                            self.scheduling_list[job_id].reason_no = '优先级不足,关键物料'+self.scheduling_list[job_id].Key_materials+'有货时间:' + str(self.scheduling_list[job_id].material_time)
                    else:
                        self.scheduling_list[job_id].reason_no = '原材料不足,关键物料:' + self.scheduling_list[job_id].Key_materials+'有货时间:' + str(self.scheduling_list[job_id].material_time)
                else:
                    if datetime.datetime.strptime(self.scheduling_list[job_id].material_time,
                                                  "%Y-%m-%d") < self.scheduling_list[job_id].actually_start_time:   # 有货时间小于最大排程时间
                        if self.scheduling_list[job_id].Key_materials == '0':
                            self.scheduling_list[job_id].reason_no = '优先级不足'
                        else:
                            self.scheduling_list[job_id].reason_no = '优先级不足,关键物料' + self.scheduling_list[
                                job_id].Key_materials + '有货时间:' + str(self.scheduling_list[job_id].material_time)
                    else:
                        self.scheduling_list[job_id].reason_no = '原材料不足,关键物料:' + self.scheduling_list[job_id].Key_materials +'有货时间:' + str(self.scheduling_list[job_id].material_time)  # 优化方向 显示哪里不够

            row = [job_id + 1,
                   self.scheduling_list[job_id].id,
                   self.scheduling_list[job_id].product_id,
                   product_name, self.scheduling_list[job_id].production_num,
                   str(self.scheduling_list[job_id].completion_num),
                   self.scheduling_list[job_id].actually_start_time,
                   self.scheduling_list[job_id].end_time,
                   self.scheduling_list[job_id].finish_time,
                   self.scheduling_list[job_id].delivery_status,
                   working_calendar.days_between_two_date(self.scheduling_list[job_id].end_time,
                                                          self.scheduling_list[job_id].finish_time),
                   self.scheduling_list[job_id].priority,
                   self.scheduling_list[job_id].reason_no,
                   self.scheduling_list[job_id].notes]
            writer_all.writerow(row)
        f.close()
