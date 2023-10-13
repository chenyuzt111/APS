import pandas as pd
import csv
import datetime
import math
from src import config
# -*- coding: gbk -*-


# 物料类
class RowMaterial:

    def __init__(self, code):
        self.code = code
        self.inventory = 0  # 现有库存
        self.now_time = [[]]  # 当前库存时间
        self.purchase_orders_list = []  # 入库时间以及数量
        self.Correspondence_list = []  # 对应子物料关系
        self.replenishment_quantity = 0  # 需要补货的数量
        self.consider_the_time = 0  # 考虑时间内需要考虑物料

    # 得到物料情况
    def get_Raw_material_inventory(self, material_inventory, working_calendar):
        if self.Correspondence_list != []:  # 如果是半成品，则考虑剩下的
            for correspondence in self.Correspondence_list:
                material_inventory = correspondence.son.get_Raw_material_inventory(material_inventory, working_calendar)
        if material_inventory.get(self.code) == None:
            # 修改时间字符串
            self.get_inventory_time(working_calendar)
            material_inventory[self.code] = self.now_time
        return material_inventory

    # 改变物料库存的时间
    def get_inventory_time(self, working_calendar):
        # 修改时间字符串
        for time_inf in range(len(self.now_time)):
            time = self.now_time[time_inf][1]  # 时间戳
            time = math.floor(time * working_calendar.unit_time / working_calendar.hours_in_day)  # 天数
            date = working_calendar.get_date_after_days(time)  # 获取日期
            self.now_time[time_inf][1] = date  # 修改日期

    # 得到最小的库存
    def get_min_inventory(self):
        min_num = 0
        for i in range(len(self.now_time) - 1):
            if min_num == 0 or min_num > self.now_time[i][0]:
                min_num = self.now_time[i][0]
        return min_num

    # 增加不同时间下的列表
    def add_inventory_time(self,working_calendar):
        self.now_time = [[self.inventory,0]]
        for order_index in range(len(self.purchase_orders_list)):
            order_num = self.purchase_orders_list[order_index][0]  # 数量
            order_time = self.purchase_orders_list[order_index][1].split(' ')[0]  # 时间年月日
            if 'cycle_time' in self.__dict__:
                if datetime.datetime.strptime(
                                                order_time, "%Y/%m/%d") == datetime.datetime.strptime(config.start_time, "%Y-%m-%d"):
                    self.now_time[0] = [self.inventory + order_num, 0]
                if datetime.datetime.strptime(working_calendar.get_date_after_days(config.scheduled_days_num),
                                              "%Y-%m-%d") >= datetime.datetime.strptime(
                                                order_time, "%Y/%m/%d") > datetime.datetime.strptime(config.start_time, "%Y-%m-%d"):
                    time = working_calendar.get_time_period(order_time)
                    if time == self.now_time[-1][1]:
                        self.now_time[-1][0] += order_num
                    else:
                        self.now_time.append([order_num,time])
        a = [float('inf'), self.consider_the_time]
        self.now_time.append([float('inf'),self.consider_the_time])
        self.now_time.sort(key=lambda ele: ele[1], reverse=False) # 根据时间进行排序
        index = self.now_time.index(a)
        length = len(self.now_time)
        for i in range(length - 1 - index):
            self.now_time.pop() # 删除需求之外的计划
        now = self.now_time[0][0]
        for now_inventory in range(1,len(self.now_time)):
            now += self.now_time[now_inventory][0]
            self.now_time[now_inventory][0] = now

    # 计算最晚考虑时间
    def calculate_latest_consider_time(self,working_calendar):
        if 'cycle_time' in self.__dict__:
            self.consider_the_time = self.cycle_time * working_calendar.hours_in_day / working_calendar.unit_time
            if self.Correspondence_list != []:
                time_list = []
                for son_dict in self.Correspondence_list:
                    time = son_dict.son.calculate_latest_consider_time(working_calendar)
                    time_list.append(time)
                time = max(time_list)
                self.consider_the_time += time
        return self.consider_the_time

    # 判断是否补货
    def determine_replenishment_time(self,job_inf,num,working_calendar,writer_all = None,writer_all_origin=None,writer_all_semi = None,job_inf_semi=[]):
        if self.Correspondence_list == []: #原材料
            num_positive = 0
            for now_inventory_temp in range(len(self.now_time)):
                if self.now_time[now_inventory_temp][0] - num >= 0:
                    num_positive +=1
                else:
                    num_positive = 0

            if writer_all_origin is not None: # 将有货时间写入文件中
                try:
                    day = math.floor(
                        (self.now_time[len(self.now_time) - num_positive][1] * working_calendar.unit_time) / working_calendar.hours_in_day)
                except:
                    print('出现错误')
                day = working_calendar.get_date_after_days(day)
                row = job_inf + [self.code,num,day]
                writer_all_origin.writerow(row)
            if writer_all_semi is not None and job_inf_semi != []: # 将有货时间写入文件中
                day = math.floor(
                    (self.now_time[len(self.now_time) - num_positive][1] * working_calendar.unit_time) / working_calendar.hours_in_day)
                day = working_calendar.get_date_after_days(day)
                row = job_inf_semi + [self.code,num,day]
                writer_all_semi.writerow(row)
            return self.now_time[len(self.now_time) - num_positive][1]
        else:  # 半成品
            # 半成品必须要制作的开始时间，以及对应的数量。
            # 要根据制作的时间来减去原材料的库存，跟库存留下记号。
            # 如何判断制作的时间，首先看半成品是否缺货，在对应节点-1中找时间，如果他不缺货，则返回当前不缺货的时间，
            # 如果他缺货，找不到一个对应有货的时间，则需要补货，补货的数量需要考虑安全库存的量，在反推原材料都有货的时间，在此基础上加上制作时间，就是原材料有货的时间，
            # 在此要减去有货的对应时间的库存，并且在制作时间上，加上有货的时间节点
            num_positive = 0
            for now_inventory_temp in range(len(self.now_time)):
                if self.now_time[now_inventory_temp][0] - num >= 0:
                    num_positive += 1
                else:
                    num_positive = 0

            if num_positive != 1:  # 制作周期中不缺货
                if writer_all_origin is not None:  # 将有货时间写入文件中
                    day = math.floor(
                        (self.now_time[len(self.now_time) - num_positive][
                             1] * working_calendar.unit_time) / working_calendar.hours_in_day)
                    day = working_calendar.get_date_after_days(day)
                    row = job_inf + [self.code, num, day]
                    writer_all_origin.writerow(row)
                return self.now_time[len(self.now_time) - num_positive][1]
            else:  # 遍历时间节点之后都缺货
                # 不知道他什么时候有货，尽可能早的制作，因此 制作时间就是今天！制作数量是个问题？
                min_min = self.get_min_inventory() # 这是他的最小库存,从头开始都检测了一遍
                make_num = self.judge_safe_inventory(min_min, num)  # 需要补货的数量
                job_inf_semi = job_inf + [self.code, make_num]
                Time_in_stock = self.consider_child_material(job_inf, make_num, working_calendar, job_inf_semi, writer_all_semi)  # 计算原材料有货的时间
                make_time = 0  # 制作的时间
                if 'cycle_time' in self.__dict__:
                    make_time = self.cycle_time * working_calendar.hours_in_day / working_calendar.unit_time  # 制作时间
                self.add_inventory(make_num,Time_in_stock + make_time)  # 更新库存
                if Time_in_stock + make_time == self.now_time[len(self.now_time) - 1][1]:
                    return Time_in_stock + make_time  # 原材料有货的时间加上制作时间
                day_begin = math.floor((Time_in_stock * working_calendar.unit_time) / working_calendar.hours_in_day)
                try:
                    day_begin = working_calendar.get_date_after_days(day_begin)
                    day_end = math.floor(((Time_in_stock + make_time) * working_calendar.unit_time) / working_calendar.hours_in_day)
                    day_end = working_calendar.get_date_after_days(day_end)
                    # print('因任务'+str(job_inf[0])+',半成品'+self.code+'从'+day_begin+'开始生产，到'+day_end+'入库，生产数量:'+str(make_num))
                    if writer_all is not None:
                        writer_all.writerow([self.code, str(make_num), day_begin, day_end])
                except:
                    print('半成品生产以及入库时间超过工序的工作日历的最大时间')
                self.all_material_sub_inventory(make_num,Time_in_stock,working_calendar)
                if writer_all_origin is not None:  # 将有货时间写入文件中
                    writer_all_origin.writerow(job_inf + [self.code, num, day_end])
                return Time_in_stock + make_time  # 原材料有货的时间加上制作时间
                # 上述情况需要增加两方面的库存，首先就是原材料的库存，以及制作原材料所消耗的库存

    # 半成品生产所消耗的所有原材料
    def all_material_sub_inventory(self, consume_num, time, working_calendar):
        #print('-------半成品消耗--------')
        if self.Correspondence_list != []:
            for i in self.Correspondence_list:  # 遍历每个对应关系
                    num_for_son = math.ceil(consume_num * i.correlation_num[1] / i.correlation_num[0])
                    i.son.subtract_inventory(num_for_son, time, working_calendar)

    # 消耗原材料 减去原材料
    def subtract_inventory(self, consume_num, time, working_calendar):
        insert_index = 0
        for j in range(len(self.now_time)):
            if time == self.now_time[j][1]:
                insert_index = j
                # if self.now_time[insert_index][0] - consume_num< 0:
                #     print('物料库存小于零')
                self.now_time[insert_index][0] -= consume_num
                for i in range(insert_index + 1, len(self.now_time)):
                    # if self.now_time[i][0] - consume_num < 0:
                    #     print('物料库存小于零')
                    self.now_time[i][0] -= consume_num
                break
            if time < self.now_time[j][1]:
                insert_index = j
                num = self.now_time[insert_index - 1][0] - consume_num
                self.now_time.insert(insert_index, [num, time])
                # if num < 0:
                #     print('物料库存小于零')
                for i in range(insert_index + 1, len(self.now_time)):
                    # if self.now_time[i][0] - consume_num< 0:
                    #     print('物料库存小于零')
                    self.now_time[i][0] -= consume_num

                break
        day = math.floor((time*working_calendar.unit_time)/working_calendar.hours_in_day)
        # try:
        #     date = working_calendar.get_date_after_days(day)
        #     if self.now_time[insert_index][0] < 0:
        #         print('物料库存小于零')
        #     print('物料' + self.code +',消耗数量为'+ str(consume_num)+',时间：'+date+',剩余库存：'+str(self.now_time[insert_index][0]))
        # except:
        #     print('物料' + self.code +',消耗数量为'+ str(consume_num)+',时间超过工作日历最大时间'+',剩余库存：'+str(self.now_time[insert_index][0]))

    # 增加半成品的库存
    def add_inventory(self,add_num,time):
        for j in range(len(self.now_time)):
            if time == self.now_time[j][1]:
                insert_index = j
                self.now_time[insert_index][0] += add_num
                for i in range(insert_index + 1, len(self.now_time)):
                    self.now_time[i][0] += add_num
                break
            if time < self.now_time[j][1]:
                insert_index = j
                num = add_num + self.now_time[insert_index - 1][0]
                self.now_time.insert(insert_index, [num, time])
                for i in range(insert_index + 1, len(self.now_time)):
                    self.now_time[i][0] += add_num
                break

    # 判断原材料
    def consider_child_material(self,job_inf,replenishment,working_calendar,job_inf_semi=[],writer_all_semi=None):
        if self.Correspondence_list != []:
            end_time = []  # 补货的各个时间
            for i in self.Correspondence_list:  # 遍历每个对应关系
                    num_for_son = math.ceil(replenishment * i.correlation_num[1] / i.correlation_num[0])
                    end_time.append(i.son.determine_replenishment_time(job_inf,num_for_son, working_calendar,writer_all_semi = writer_all_semi,job_inf_semi=job_inf_semi))
            return max(end_time)
        return 0

    # 判断安全库存
    def judge_safe_inventory(self,now_inventory,num = 0):
        if 'safe_inventory' in self.__dict__:
            if now_inventory - num < self.safe_inventory:
                add_num = self.min_inventory  # 增加的数量
                if now_inventory + add_num - num < self.safe_inventory:
                    ladder_num = self.safe_inventory - (now_inventory + add_num - num)
                    if self.ladders_inventory == 0:
                        self.ladders_inventory = 1
                    ladder = math.ceil(ladder_num/self.ladders_inventory)
                    add_num += ladder*self.ladders_inventory
                return add_num
        return 0

    # 增加采购
    def getAnOrder(self, purchase_orders):
        self.purchase_orders_list.append(purchase_orders)

    # 增加库存
    def addInventory(self, num):
        self.inventory += num

    # 增加名称
    def addName(self, name):
        self.name = name
    # 从文件中加入MOQ和采购周期，（后期要和数量绑定，暂时不考虑）

    def addCycleTime(self, time_day):
        self.cycle_time = time_day

    # 增加额外库存信息
    def additional_inventory(self, safe, min, ladder):
        self.safe_inventory = safe
        self.min_inventory = min
        self.ladders_inventory = ladder


# 物料列表类
class TotalRowMaterial:
    # 导入所有物料的库存信息以及采购订单信息
    def __init__(self):
        item_inventory_total = self.read_item_inventory("data/origin_data/即时库存汇总数据查询.xlsx")
        purchase_orders_total = self.read_purchase_orders("data/origin_data/采购订单.xlsx")
        bom = self.read_bom("data/origin_data/工序BOM.xlsx")
        total_item = self.get_index_set(item_inventory_total, purchase_orders_total, bom)
        all_item = self.read_all_items("data/origin_data/物料数据表.csv")
        set3 = set(total_item).union(set(all_item))
        self.all_material = {}
        for item in set3:
            self.all_material[item] = RowMaterial(item)  # 增加物料

        for row in bom.itertuples():
            self.all_material[row[0]].addName(row[1])  # 增加物料名称

        for row in item_inventory_total.itertuples():
            self.all_material[row[0]].addInventory(row[1])  # 增加现有库存

        self.cycle_time_total('data/origin_data/MOQ及采购周期.csv')  # 增加MOQ和采购时间的周期信息
        self.additional_inventory('data/origin_data/半成品库存信息.csv')  # 增加半成品的库存信息

        for material in self.all_material:
            self.all_material[material].inventory = self.all_material[material].inventory

        for row in purchase_orders_total.itertuples():
            self.all_material[row[0]].getAnOrder([row[1], row[2]])  # 增加采购订单

        for code in self.all_material:  # 调整物料的购买时间从前往后排序
            if self.all_material[code].purchase_orders_list != []:
                self.all_material[code].purchase_orders_list.sort(key=lambda x: datetime.datetime.strptime(x[1].split(' ')[0], "%Y/%m/%d"))

    def read_all_items(self, address):
        a = pd.read_csv(address)
        all_list1 = list(a['物料编码'])
        return all_list1

    def add_inventory_time(self, working_calendar):
        for i in self.all_material:
            self.all_material[i].add_inventory_time(working_calendar)

    def add_consider_the_time(self, working_calendar):  # 增加考虑时间
        for i in self.all_material:
            self.all_material[i].calculate_latest_consider_time(working_calendar)

    def additional_inventory(self, file_name):
        with open(file_name, newline='', encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            for row in reader:
                product_id = row[0]  # 物料编码
                if self.all_material.get(product_id) is not None:
                    safe_inventory = row[3]  # 安全库存
                    min_inventory = row[4]  # 最小批量
                    ladders_inventory = row[5]  # 阶梯数量
                    if safe_inventory == '':
                        safe_inventory = 1
                    if min_inventory == '':
                        min_inventory = 1
                    if ladders_inventory == '':
                        ladders_inventory = 1
                    self.all_material[product_id].additional_inventory(int(safe_inventory), int(min_inventory), int(ladders_inventory))

    def cycle_time_total(self, file_name):
        with open(file_name, newline='', encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            for row in reader:
                product_id = row[0]  # 物料编码
                cycle_time = row[2]  # 采购周期或者制作周期
                if cycle_time == '':
                    cycle_time = 1
                if self.all_material.get(product_id) is not None:
                    self.all_material[product_id].addCycleTime(int(cycle_time))

    def read_item_inventory(self, file_name):
        try:
            return pd.read_excel(file_name).set_index("物料编码")[["可用量(主单位)", "物料名称"]].drop(index=['合计'])
        except:
            return pd.read_excel(file_name).set_index("物料编码")[["可用量(主单位)", "物料名称"]]

    # 对采购订单的交货日期进行加天数的操作，此处不对原列进行修改是一个数据源被改变的问题，最好用新的列代替，省去很多麻烦
    def read_purchase_orders(self, file_name):
        data = pd.read_excel(file_name).set_index("物料编码")[["剩余收料数量", "交货日期"]].dropna(how='any')
        # 创建一个新的列，用于存储处理后的日期
        data['处理后交货日期'] = None
        for i in range(len(data)):
            # 用csv阶段，这个判断可以留着，以后从数据库调用数据是没问题的，那这条语句可以删除
            if int(data['交货日期'][i][:4]) > 3000:
                data['处理后交货日期'][i] = data['交货日期'][i]
                continue

            # 将str格式的时期改为datetime格式，方便加天数
            date_obj = datetime.datetime.strptime(data['交货日期'][i], '%Y/%m/%d %H:%M:%S')
            date_obj = date_obj + datetime.timedelta(days=config.buy_delay_days)

            # 加完天数后改回str格式的时间，因为后边多处在操作字符串格式的时间，避免修改太多的东西
            data['处理后交货日期'][i] = date_obj.strftime('%Y/%m/%d %H:%M:%S')
        # 删除原始的交货日期列，如果不需要保留
        data.drop(columns=['交货日期'], inplace=True)
        # 重命名新列为原始的交货日期列
        data.rename(columns={'处理后交货日期': '交货日期'}, inplace=True)
        return data

    # # 原来方法
    # def read_purchase_orders(self,file_name):
    #     return pd.read_excel(file_name).set_index("物料编码")[["剩余收料数量", "交货日期"]].dropna(how='any')

    # 从BOM里读取物料编码
    def read_bom(self, file_name):
        bom = pd.read_excel(file_name)[['父项物料编码', '物料名称', '子项物料编码', '子项物料名称']]
        new_bom = pd.DataFrame({'物料编码': bom['父项物料编码'].tolist() + bom['子项物料编码'].tolist(),
                                '物料名称': bom['物料名称'].tolist() + bom['子项物料名称'].tolist()})
        new_bom.set_index('物料编码', inplace=True)
        return new_bom

    def read_item_Correspondence(self, file_name):
        return pd.read_excel(file_name).set_index("物料编码")[["可用量(主单位)", "物料名称"]].drop(index=['合计'])

    # def get_index_set(self, df1, df2):
    #     return list(set(list(df1.index.unique())).union(set(list(df2.index.unique()))))

    def get_index_set(self, df1, df2, df3):
        return list(set(list(df1.index.unique())).union(set(list(df2.index.unique()))).union(set(list(df3.index.unique()))))
    # 获取对应关系
    def get_Correspondence_all_finished_product(self, address):
        item_Correspondence_total = pd.read_excel(address)[["父项物料编码", "子项物料编码", "用量:分子", "用量:分母", "生产工序"]]
        for row in item_Correspondence_total.itertuples():
            if self.all_material.get(row[1]) is not None:
                if self.all_material.get(row[2]) is not None:
                    child = self.all_material[row[2]]
                    self.all_material[row[1]].Correspondence_list.append(Correspondence(child,[int(row[4]),int(row[3])],row[5]))

    # 获取对应关系
    def get_Correspondence_all_Semi_finished_products(self, address):
        item_Correspondence_total = pd.read_excel(address)[["父项物料编码", "子项物料编码", "用量:分子", "用量:分母"]]
        for row in item_Correspondence_total.itertuples():
            if self.all_material.get(row[1]) is not None:
                if self.all_material.get(row[2]) is not None:
                    child = self.all_material[row[2]]
                    self.all_material[row[1]].Correspondence_list.append(Correspondence(child, [int(row[4]), int(row[3])]))


# 对应关系类
class Correspondence:
    # 存入对应关系
    def __init__(self, Child_items, correlation_num,process='0'):
        self.son = Child_items
        self.correlation_num = correlation_num
        if process != '0':
            self.corresponding_process = process
