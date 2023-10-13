import math
import csv
import datetime
from src.utils.machine import MachinesSet
from src.utils.workingcalendar import WorkingCalendarInDay


# -*- coding: gbk -*-


# 细化的排程计划 根据排程结果
class ConcretePlan:
    # 键是日期，值是machine_set
    days_people_combine_plan = {}  # 每天的人数组合工作情况
    weeks_people_combine_plan = {}  # 每周的人数组合工作情况
    product_start_time_for_operation1 = {}  # 每个任务的最先开始时间
    product_start_time_for_wrap = {}  # 每个任务的包装工序的开始时间

    # 后续再加 以单个人为主体，以任务为主体.
    def get_days_plan(self, machines_set, working_calendar, scheduling_plan, Product_control):

        for i in range(working_calendar.during_days):  # 生成每天针对人数组合机器的工作需求
            self.days_people_combine_plan[working_calendar.date_month_day[i]] = MachinesSet(
                working_calendar.max_people_num)
            for j in self.days_people_combine_plan[working_calendar.date_month_day[i]].machine_list:
                j.operation = []
        for j in range(len(machines_set.machine_list)):
            for job in machines_set.machine_list[j].operation:

                # 节点集合
                point_set = set()
                point_set.add(job[0])  # 起始时间
                point_set.add(job[1])  # 完工时间
                string_temp = job[2]  # 任务号-工序号
                job_index = int(string_temp.split('-')[0])  # 任务号
                process_index = int(string_temp.split('-')[1])  # 工序号
                process_num = Product_control.product_all[
                    scheduling_plan.scheduling_list[job_index].product_id].process_num  # 物料的工序数量
                job_process_num = len(scheduling_plan.scheduling_list[job_index].completion_num)  # 任务中的工序数量
                temp_list = []
                for i in range(process_num - job_process_num + 1, process_num + 1):
                    temp_list.append(i)
                try:
                    index_final = temp_list.index(process_index)
                except:
                    print('工序出错')
                complete_num = scheduling_plan.scheduling_list[job_index].completion_num[index_final]  # 工序完成个数
                # 开始的天数
                o_num = math.floor(job[0] / (working_calendar.hours_in_day / working_calendar.unit_time))
                # 结束的天数
                oi_num = math.floor(job[1] / (working_calendar.hours_in_day / working_calendar.unit_time))
                for i in range(oi_num - o_num):
                    point_set.add((o_num * (working_calendar.hours_in_day / working_calendar.unit_time)) + (i + 1) * (
                                working_calendar.hours_in_day / working_calendar.unit_time))
                point_set_list = sorted(point_set)
                length = point_set_list[-1] - point_set_list[0]  # 工序总长度
                temp_num = 0  # 暂时的数量
                for i in range(len(point_set_list) - 1):
                    if i + o_num >= len(working_calendar.date_month_day):
                        break
                    date = working_calendar.date_month_day[i + o_num]
                    if i == len(point_set_list) - 2:
                        today_num = complete_num - temp_num  # 最后一天等于总完成个数减去之前的个数
                    else:
                        today_num = int(complete_num * float(point_set_list[i + 1] - point_set_list[i]) / length)
                        temp_num += today_num
                    if self.days_people_combine_plan.get(date) is not None:
                        self.days_people_combine_plan[date].machine_list[j].operation.append(
                        [point_set_list[i], point_set_list[i + 1], string_temp, today_num])



        # 后续再加 以单个人为主体，以任务为主体

    def get_weeks_plan(self, machines_set, working_calendar):
        list_week = working_calendar.get_deadline_weekly()  # 每周的时间节点
        working_calendar.list_week = list_week
        for i in range(len(list_week)):  # 生成每天针对人数组合机器的工作需求
            self.weeks_people_combine_plan['the_' + str(i + 1) + '_week'] = MachinesSet(
                working_calendar.max_people_num)
            for j in self.weeks_people_combine_plan['the_' + str(i + 1) + '_week'].machine_list:
                j.operation = []
        for j in range(len(machines_set.machine_list)):
            for job in machines_set.machine_list[j].operation:
                # 节点集合
                point_set = set()
                point_set.add(job[0])  # 起始时间
                point_set.add(job[1])  # 完工时间
                string_temp = job[2]  # 任务号
                # 开始的周次
                o_week = 0
                for i in range(len(list_week) - 1):
                    if job[0] < list_week[i]:
                        o_week = i
                        break
                    if i == len(list_week) - 2 and o_week == 0:
                        o_week = len(list_week) - 1
                # 结束的周次
                oi_week = len(list_week) - 1
                for i in range(len(list_week) - 2, -1, -1):
                    if job[1] > list_week[i]:
                        oi_week = i + 1
                        break
                    if i == 0 and oi_week == len(list_week) - 1:
                        oi_week = 0
                for i in range(o_week, oi_week):
                    point_set.add(list_week[i])
                point_set_list = sorted(point_set)
                for i in range(len(point_set_list) - 1):
                    date = 'the_' + str(i + o_week + 1) + '_week'
                    self.weeks_people_combine_plan[date].machine_list[j].operation.append(
                        [point_set_list[i], point_set_list[i + 1], string_temp])

    # 将每天的工作情况化存入csv中
    def everyday_num_to_csv(self, scheduling_plan, working_calendar, Product_control):
        working_calendar_in_day = WorkingCalendarInDay()
        index_day = 0
        ff = open('data/output_data/csv/all_plan_num_in_process.csv', 'w', newline='')
        writer_all = csv.writer(ff)
        head = ['任务号', '任务来源ID', '物料编码', '物料名称', '对应工序', '所做数量', '日期', '开始时间', '结束时间']
        writer_all.writerow(head)
        for date in self.days_people_combine_plan:
            f = open('data/output_data/csv/' + date + '_plan.csv', 'w', newline='')
            writer = csv.writer(f)
            head = ['任务号', '任务来源ID', '物料编码', '物料名称', '对应工序', '所做数量', '开始时间', '结束时间']
            writer.writerow(head)
            for machine in self.days_people_combine_plan[date].machine_list:
                for job in machine.operation:
                    job_id = int(job[2].split('-')[0])  # 任务号
                    source_id = scheduling_plan.scheduling_list[job_id].id  # 任务来源
                    product_id = scheduling_plan.scheduling_list[job_id].product_id  # 物料编码
                    product_name = Product_control.product_all[product_id].name  # 物料名称
                    process_id = int(job[2].split('-')[1])
                    process_num = Product_control.product_all[product_id].process_num  # 物料的工序数量
                    job_process_num = len(scheduling_plan.scheduling_list[job_id].completion_num)  # 任务中的工序数量
                    temp_list = []
                    for i in range(process_num - job_process_num + 1, process_num + 1):
                        temp_list.append(i)
                    index_final = temp_list.index(process_id)
                    process_name = scheduling_plan.scheduling_list[job_id].process_name[index_final]  # 对应工序
                    job_num = job[3]  # 所作数量
                    during_time = working_calendar_in_day.unit_time / 60 / working_calendar.unit_time
                    start_str = working_calendar.one_day_time_paln[int((job[0] - index_day * (
                                working_calendar.hours_in_day / working_calendar.unit_time)) / during_time)].split('/')[
                        -1]
                    hour = start_str.split(':')[0]
                    minutes = int(start_str.split(':')[1]) + ((job[0] - index_day * (
                                working_calendar.hours_in_day / working_calendar.unit_time)) % during_time) * working_calendar.unit_time * 60
                    start_str = hour + ':' + str(int(minutes))  # 开始时间
                    try:
                        end_str = working_calendar.one_day_time_paln[int((job[1] - index_day * (
                                    working_calendar.hours_in_day / working_calendar.unit_time)) / during_time)].split(
                            '/')[-1]
                    except:
                        print('出现错误')
                    hour = end_str.split(':')[0]
                    minutes = int(end_str.split(':')[1]) + ((job[1] - index_day * (
                                working_calendar.hours_in_day / working_calendar.unit_time)) % during_time) * working_calendar.unit_time * 60
                    end_str = hour + ':' + str(int(minutes))  # 结束时间时间
                    product_name = product_name.encode("gbk", 'ignore').decode("gbk", "ignore")
                    writer.writerow(
                        [job_id + 1, source_id, product_id, product_name, process_name, job_num, start_str, end_str])
                    writer_all.writerow(
                        [job_id + 1, source_id, product_id, product_name, process_name, job_num, date, start_str,
                         end_str])

            f.close()
            index_day += 1
        ff.close()

    # 为物料服务 计算对应工序的结束时间
    def get_start_time_for_all_process(self, machines_set, working_calendar, scheduling_plan, Product_control):
        ff = open('data/output_data/csv/item_control_work_plan.csv', 'w', newline='')
        writer_all = csv.writer(ff)
        head = ['任务号', '物料编码', '对应工序', '所做数量', '开始时间']
        writer_all.writerow(head)
        for machine in machines_set.machine_list:
            for job in machine.operation:
                job_id = int(job[2].split('-')[0])  # 获取任务号
                process_id = int(job[2].split('-')[1])  # 获取工序号
                product_id = scheduling_plan.scheduling_list[job_id].product_id  # 对应物料
                process_num = Product_control.product_all[product_id].process_num  # 对应物料工序数量
                if process_id == 1 or process_id == process_num:
                    # 获得开始时间和对应物料数量
                    start_day = int((job[0]) / (working_calendar.hours_in_day / working_calendar.unit_time))
                    start_time = datetime.datetime.strptime(working_calendar.get_date_after_days(start_day), "%Y-%m-%d")
                    process_name = Product_control.product_all[product_id].process_name[process_id - 1]  # 工序名称
                    corresponding_num = scheduling_plan.scheduling_list[job_id].production_num  # 对应数量
                    if process_id == 1:  # 增加开始时间
                        scheduling_plan.scheduling_list[job_id].actually_start_time = start_time
                    if process_id == process_num:
                        if scheduling_plan.scheduling_list[job_id].index == 1:
                            scheduling_plan.scheduling_list[job_id].actually_start_time = start_time
                    if scheduling_plan.scheduling_list[job_id].priority != '历史遗留订单':
                        writer_all.writerow([str(job_id + 1), product_id, process_name, corresponding_num, start_time])
                elif scheduling_plan.scheduling_list[job_id].priority == '历史遗留订单':  # 为历史遗留任务增加开始时间
                    if process_id + scheduling_plan.scheduling_list[job_id].index == 4:
                        start_day = int((job[0]) / (working_calendar.hours_in_day / working_calendar.unit_time))
                        start_time = datetime.datetime.strptime(working_calendar.get_date_after_days(start_day),
                                                                "%Y-%m-%d")
                        scheduling_plan.scheduling_list[job_id].actually_start_time = start_time

        ff.close()
