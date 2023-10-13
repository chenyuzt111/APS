import random
import numpy as np
import matplotlib.pyplot as plt
from src.utils.workingcalendar import WorkingCalendarInDay


# -*- coding: gbk -*-

# 画图类
class DrawGantt:
    def __init__(self, scheduling_plan):
        self.colors = self.get_color_list(scheduling_plan.history_num + scheduling_plan.product_num)

    # 接受一个解析后的数据字典，并创建一个甘特图。
    def draw_chart(self, machines_set, scheduling_plan, working_calendar, product_dict, type='0'):
        plt.rcParams['font.sans-serif'] = ['SimHei']  # 正常显示中文

        fig = plt.figure(figsize=(20, 10))
        ax = fig.add_subplot(111)

        no_empty_machine = []  # 非空机器名称

        job_set = set()  # 获得画图中的所有任务
        for machine in machines_set.machine_list:
            machine.operation.sort(key=lambda x: x[0])  # 按时间排序
        index = 0
        for machine in machines_set.machine_list:
            if machine.operation == []:
                continue  # 若机器没有使用,不展示

            no_empty_machine.append(machine.name)  # 纵坐标名称

            for op in machine.operation:  # 画柱状 barh(y,width)
                job = op[2].split('-')[0]  # 对应任务
                job_set.add(int(job))
                process = op[2].split('-')[1]
                string = '{}-{}'.format(int(job) + 1, int(process))
                rect = ax.barh((index * 0.5) + 0.5, op[1] - op[0], left=op[0],
                               height=0.3, align='center', edgecolor='#2d2a2e', color=self.colors[int(job)],
                               alpha=1)  # 柱状
                xloc = op[0] + 0.50 * int(rect[0].get_width())  # 文本x轴
                yloc = rect[0].get_y() + rect[0].get_height() / 2.0  # 文本y轴
                ax.text(xloc, yloc, string, horizontalalignment='center',
                        verticalalignment='center', color='black', weight='bold',
                        clip_on=True)  # 设置文本
            index += 1
        job_set = sorted(job_set)
        # 坐标显示问题
        ax.grid(color='gray', linestyle=':')  # 网格线
        # y轴标签
        ax.set_ylim(ymin=0.25, ymax=len(no_empty_machine) * 0.5 + 0.25)  # 设定y轴的范围
        pos = np.arange(0.5, len(no_empty_machine) * 0.5 + 0.5, 0.5)  # 生成递增位置
        plt.yticks(pos, no_empty_machine, fontsize=14)  # 设置纵坐标刻度
        # x轴标签
        max_xlim = (working_calendar.hours_in_day * working_calendar.during_days) / working_calendar.unit_time
        #max_xlim = (working_calendar.hours_in_day * 7) / working_calendar.unit_time
        interval = working_calendar.hours_in_day / working_calendar.unit_time
        ax.set_xlim(-8, max_xlim + 8)
        my_x_ticks = np.arange(0, max_xlim + interval, interval)
        plt.xticks(my_x_ticks, fontsize=12, labels=working_calendar.date_month_day)
        #plt.xticks(my_x_ticks, fontsize=12)
        plt.setp(ax.get_xticklabels(), rotation=12, fontsize=12)  # 旋转设置

        ax.invert_yaxis()  # 纵轴的标签以及对应的柱状图颠倒
        # 坐标轴标题

        plt.xlabel('日期', labelpad=-15.1,  # 调整x轴标签与x轴距离
                   x=1.02,  # 调整x轴标签的左右位置
                   fontsize=12)
        plt.ylabel('机器编号', labelpad=-30, y=1.02, rotation=0, fontsize=12)
        label = machines_set.set_name + '总排程调度图'
        fig.text(s=label, x=0.5, y=0.94, fontsize=20, ha='center', va='center')
        if type == '0':
            total_work = machines_set.get_total_work(working_calendar) * working_calendar.unit_time  # 总工时
            fig.text(s='总产能:{:.2f}H,效率:{:.2f},完成比例:{:.2f},PO完成比例:{:.2f}'.format(
                total_work, scheduling_plan.worker_utilization, scheduling_plan.Completion_rate_all,
                scheduling_plan.Completion_rate_PO
            ), x=0.969, y=0.93, fontsize=16, ha='right', va='center')
        # 画布设置
        plt.gcf().subplots_adjust(left=0.04, top=0.91, bottom=0.4, right=0.97)
        # 图例设置
        font = {'size': 11.4}
        for i in job_set:
            product_name = product_dict[scheduling_plan.scheduling_list[i].product_id].name
            legend_label = 'Job' + str(i + 1) + ':' + scheduling_plan.scheduling_list[i].id + '-' + product_name
            plt.scatter([], [], c=self.colors[i], s=100, label=legend_label)
        plt.legend(frameon=False,  # 边框
                   labelspacing=1,  # 图例条目之间的垂直间距
                   loc='upper center',  # 图例的位置
                   prop=font, ncol=5,  # 图例的纵数
                   handletextpad=-0.1,  # 图例句柄和文本之间的间距
                   columnspacing=0.1,  # 列之间的间距
                   bbox_to_anchor=(0.5, -0.08))  # 宽 高

        plt.savefig('data/output_data/img/' + label + '.png', dpi=200, bbox_inches='tight')
        plt.show()

    # 接受一个解析后的数据字典，并创建一个甘特图。
    def draw_chart_for_day(self, day, machines_set, scheduling_plan, working_calendar, product_dict):

        indx_day = working_calendar.date_month_day.index(day)  # 获取当前时间为第几天
        people_num = int(working_calendar.working_calendar_people[indx_day])  # 获取当前的人数信息
        total_time = int(people_num) * working_calendar.hours_in_day
        time_in_day = int(WorkingCalendarInDay().unit_time / 60 / working_calendar.unit_time)  # 获取时间间隔
        total_work = machines_set.get_total_work(working_calendar) * working_calendar.unit_time  # 总工时
        if total_work == 0:
            return None

        plt.rcParams['font.sans-serif'] = ['SimHei']  # 正常显示中文

        fig = plt.figure(figsize=(20, 10))
        ax = fig.add_subplot(111)

        no_empty_machine = []  # 非空机器名称

        job_set = set()  # 获得画图中的所有任务
        for machine in machines_set.machine_list:
            machine.operation.sort(key=lambda x: x[0])  # 按时间排序
        index = 0
        for machine in machines_set.machine_list:
            if machine.operation == []:
                continue  # 若机器没有使用,不展示

            no_empty_machine.append(machine.name)  # 纵坐标名称

            for op in machine.operation:  # 画柱状 barh(y,width)
                job = op[2].split('-')[0]  # 对应任务
                job_set.add(int(job))
                process = op[2].split('-')[1]
                string = '{}-{}'.format(int(job) + 1, int(process))
                rect = ax.barh((index * 0.5) + 0.5, op[1] - op[0], left=op[0],
                               height=0.3, align='center', edgecolor='#2d2a2e', color=self.colors[int(job)],
                               alpha=1)  # 柱状
                xloc = op[0] + 0.50 * int(rect[0].get_width())  # 文本x轴
                yloc = rect[0].get_y() + rect[0].get_height() / 2.0  # 文本y轴
                ax.text(xloc, yloc, string, horizontalalignment='center',
                        verticalalignment='center', color='black', weight='bold',
                        clip_on=True)  # 设置文本
            index += 1
        job_set = sorted(job_set)
        # 坐标显示问题
        ax.grid(color='gray', linestyle=':')  # 网格线
        # y轴标签
        ax.set_ylim(ymin=0.25, ymax=len(no_empty_machine) * 0.5 + 0.25)  # 设定y轴的范围
        pos = np.arange(0.5, len(no_empty_machine) * 0.5 + 0.5, 0.5)  # 生成递增位置
        plt.yticks(pos, no_empty_machine, fontproperties="Times New Roman", fontsize=14)  # 设置纵坐标刻度

        # x轴标签
        max_xlim = (working_calendar.hours_in_day) / working_calendar.unit_time
        interval = time_in_day
        ax.set_xlim(-1 + max_xlim * indx_day, max_xlim * (indx_day + 1) + 1)
        my_x_ticks = np.arange(max_xlim * indx_day, max_xlim * (indx_day + 1) + interval, interval)
        plt.xticks(my_x_ticks, fontsize=12, labels=working_calendar.one_day_time_paln)
        plt.setp(ax.get_xticklabels(), rotation=18, fontsize=12)  # 旋转设置

        ax.invert_yaxis()  # 纵轴的标签以及对应的柱状图颠倒
        # 坐标轴标题

        plt.xlabel('时间', labelpad=-15.1,  # 调整x轴标签与x轴距离
                   x=1.02,  # 调整x轴标签的左右位置
                   fontsize=12)
        plt.ylabel('机器编号', labelpad=-30, y=1.02, rotation=0, fontsize=12)
        label = day + '日' + machines_set.set_name + '排程调度图'
        fig.text(s=label, x=0.5, y=0.94, fontsize=20, ha='center', va='center')
        fig.text(s='人数:{:d}人,总产能:{:.2f}H,效率:{:.2f}'.format(people_num, total_work, total_work / total_time),
                 x=0.969, y=0.93, fontsize=16, ha='right', va='center')
        # 画布设置
        plt.gcf().subplots_adjust(left=0.04, top=0.91, bottom=0.4, right=0.97)

        # 图例设置
        font = {'size': 11.4}
        for i in job_set:
            product_name = product_dict[scheduling_plan.scheduling_list[i].product_id].name
            legend_label = 'Job' + str(i + 1) + ':' + scheduling_plan.scheduling_list[i].id + '-' + product_name
            plt.scatter([], [], c=self.colors[i], s=100, label=legend_label)
        plt.legend(frameon=False,  # 边框
                   labelspacing=1,  # 图例条目之间的垂直间距
                   loc='upper center',  # 图例的位置
                   prop=font, ncol=5,  # 图例的纵数
                   handletextpad=-0.1,  # 图例句柄和文本之间的间距
                   columnspacing=0.1,  # 列之间的间距
                   bbox_to_anchor=(0.5, -0.08))  # 宽 高

        plt.savefig('data/output_data/img/' + label + '.png', dpi=200, bbox_inches='tight')
        plt.show()

    # 接受一个解析后的数据字典，并创建一个甘特图。
    def draw_chart_for_week(self, week, machines_set, scheduling_plan, working_calendar, product_dict):
        total_work = machines_set.get_total_work(working_calendar) * working_calendar.unit_time  # 总工时
        if total_work == 0:
            return None
        week_index = int(week.split('_')[1])  # 确定第几周
        if week_index == 1:
            week_timestamp = [0, working_calendar.list_week[week_index - 1]]
        else:
            week_timestamp = [working_calendar.list_week[week_index - 2], working_calendar.list_week[week_index - 1]]
        total_time = 0
        # 根据 week_timestamp 来求总工时
        for i in range(int(week_timestamp[0]), int(week_timestamp[1])):
            total_time += working_calendar.people_in_plan[i]
        total_time = total_time * working_calendar.unit_time

        plt.rcParams['font.sans-serif'] = ['SimHei']  # 正常显示中文

        fig = plt.figure(figsize=(20, 10))
        ax = fig.add_subplot(111)

        no_empty_machine = []  # 非空机器名称

        job_set = set()  # 获得画图中的所有任务
        for machine in machines_set.machine_list:
            machine.operation.sort(key=lambda x: x[0])  # 按时间排序
        index = 0
        for machine in machines_set.machine_list:
            if machine.operation == []:
                continue  # 若机器没有使用,不展示

            no_empty_machine.append(machine.name)  # 纵坐标名称

            for op in machine.operation:  # 画柱状 barh(y,width)
                job = op[2].split('-')[0]  # 对应任务
                job_set.add(int(job))
                process = op[2].split('-')[1]
                string = '{}-{}'.format(int(job) + 1, int(process))
                rect = ax.barh((index * 0.5) + 0.5, op[1] - op[0], left=op[0],
                               height=0.3, align='center', edgecolor='#2d2a2e', color=self.colors[int(job)],
                               alpha=1)  # 柱状
                xloc = op[0] + 0.50 * int(rect[0].get_width())  # 文本x轴
                yloc = rect[0].get_y() + rect[0].get_height() / 2.0  # 文本y轴
                ax.text(xloc, yloc, string, horizontalalignment='center',
                        verticalalignment='center', color='black', weight='bold',
                        clip_on=True)  # 设置文本
            index += 1
        job_set = sorted(job_set)
        # 坐标显示问题
        ax.grid(color='gray', linestyle=':')  # 网格线
        # y轴标签
        ax.set_ylim(ymin=0.25, ymax=len(no_empty_machine) * 0.5 + 0.25)  # 设定y轴的范围
        pos = np.arange(0.5, len(no_empty_machine) * 0.5 + 0.5, 0.5)  # 生成递增位置
        plt.yticks(pos, no_empty_machine, fontproperties="Times New Roman", fontsize=14)  # 设置纵坐标刻度

        # x轴标签
        interval = (working_calendar.hours_in_day) / working_calendar.unit_time
        ax.set_xlim(-8 + week_timestamp[0], week_timestamp[1] + 8)
        my_x_ticks = np.arange(week_timestamp[0], week_timestamp[1] + interval, interval)
        # 时间标签
        date_label = []
        for i in my_x_ticks:
            date_label.append(working_calendar.get_date_after_days(int(i / interval)))
        plt.xticks(my_x_ticks, fontsize=12, labels=date_label)
        plt.setp(ax.get_xticklabels(), rotation=0, fontsize=12)  # 旋转设置
        ax.invert_yaxis()  # 纵轴的标签以及对应的柱状图颠倒
        # 坐标轴标题
        plt.xlabel('日期', labelpad=-15.1,  # 调整x轴标签与x轴距离
                   x=1.02,  # 调整x轴标签的左右位置
                   fontsize=12)
        plt.ylabel('机器编号', labelpad=-30, y=1.02, rotation=0, fontsize=12)
        label = week + machines_set.set_name + '排程调度图'
        fig.text(s=label, x=0.5, y=0.94, fontsize=20, ha='center', va='center')
        try:
            fig.text(s='总产能:{:.2f}H,效率:{:.2f}'.format(total_work, total_work / total_time), x=0.969, y=0.93,
                     fontsize=16, ha='right', va='center')
        except:
            print('画图出现问题')
        # 画布设置
        plt.gcf().subplots_adjust(left=0.04, top=0.91, bottom=0.4, right=0.97)

        # 图例设置
        font = {'size': 11.4}
        for i in job_set:
            product_name = product_dict[scheduling_plan.scheduling_list[i].product_id].name
            legend_label = 'Job' + str(i + 1) + ':' + scheduling_plan.scheduling_list[i].id + '-' + product_name
            plt.scatter([], [], c=self.colors[i], s=100, label=legend_label)
        plt.legend(frameon=False,  # 边框
                   labelspacing=1,  # 图例条目之间的垂直间距
                   loc='upper center',  # 图例的位置
                   prop=font, ncol=5,  # 图例的纵数
                   handletextpad=-0.1,  # 图例句柄和文本之间的间距
                   columnspacing=0.1,  # 列之间的间距
                   bbox_to_anchor=(0.5, -0.08))  # 宽 高

        plt.savefig('data/output_data/img/' + label + '.png', dpi=200, bbox_inches='tight')
        plt.show()

    # 根据任务量创建颜色列表
    def get_color_list(self, job_num):
        # 将hsv转化为rgb
        def GenerateHSVColor(h, s, v):
            h += 0.61803398
            if h > 1:
                h = h - 1
            else:
                h = h
            hi = int(h * 6)
            f = h * 6 - hi
            p = v * (1 - s)
            q = v * (1 - f * s)
            t = v * (1 - (1 - f) * s)
            if hi == 0:
                r = v
                g = t
                b = p
            elif hi == 1:
                r = q
                g = v
                b = p
            elif hi == 2:
                r = p
                g = v
                b = t
            elif hi == 3:
                r = p
                g = q
                b = v
            elif hi == 4:
                r = t
                g = p
                b = v
            elif hi == 5:
                r = v
                g = p
                b = q
            R = int(r * 256)
            G = int(g * 256)
            B = int(b * 256)
            return rgb2hex(R, G, B)

        # 将 RGB 颜色转换为十六进制颜色
        def rgb2hex(r, g, b):
            hex_color = "#{:02x}{:02x}{:02x}".format(r, g, b)
            return hex_color

        colors = []
        # 控制颜色变量
        a = 0.7 / (job_num + 1)
        # 定义hsv颜色通道
        for i in range(job_num):
            h = a * (i + 1) + 0.3
            s = random.randint(0, 5) * 0.05 + 0.4
            v = random.randint(0, 5) * 0.05 + 0.7
            colors.append(GenerateHSVColor(h, s, v))
        return colors
