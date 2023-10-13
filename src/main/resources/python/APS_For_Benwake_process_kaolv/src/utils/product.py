import csv
import math
import datetime
# -*- coding: gbk -*-

# 产品类
class Product:

    # 初始化基础信息 每个计算功能都需要产品id和产品名称和产品
    def __init__(self,id,name,process_num,each_capacity,process_name):
        # 这里面是对象变量，每个对象中的相同变量都不一样
        self.id = id # 产品id
        self.name = name # 产品名称
        self.process_num = int(process_num) # 产品工序数量
        self.process_name = process_name # 工序名称
        self.each_capacity = [float(i) for i in each_capacity] # 产品工序产能列表
        self.route_information = [] # 产品的工艺路径信息，前缀



    # 增加产品类型和人数信息
    def add_people(self,process_people_limit,type):
        self.process_people_limit = [int(i) for i in process_people_limit]# 人数限制列表
        self.type = type # 产品类型



    # 增加库存信息
    def add_inventory(self,now_inventory,safe_inventory,min_size,increase_num):
        self.now_inventory = int(now_inventory) # 现有库存
        self.safe_inventory = int(safe_inventory) # 安全库存
        self.min_size = int(min_size) # 最小批量
        self.increase_num = int(increase_num) # 阶梯数量


    # 判断补货时间以及补货数量
    def determine_replenishment_time(self,num,process_name,working_calendar,job,writer_all = None,writer_all_origin=None,writer_all_semi = None):
        end_time = [0] # 补货的各个时间
        end_time_material = ['0']
        if 'Correspondence_list' in self.__dict__:
            for i in self.Correspondence_list: # 遍历每个对应关系
                if process_name == i.corresponding_process:
                    num_for_son =  math.ceil(num*i.correlation_num[1]/i.correlation_num[0])
                    job_inf = [job + 1,self.id,self.name.encode("gbk", 'ignore').decode("gbk", "ignore"),process_name]
                    end_time.append(i.son.determine_replenishment_time(job_inf,num_for_son,working_calendar,writer_all,writer_all_origin,writer_all_semi))
                    end_time_material.append(i.son.code)
        index = end_time.index(max(end_time))
        return max(end_time),end_time_material[index]

    # 更新成品下的各个原材料的数量
    def update_material_inventory(self, start_time, num, process_name, working_calendar):
        if 'Correspondence_list' in self.__dict__:
            for i in self.Correspondence_list:  # 遍历每个对应关系
                if process_name == i.corresponding_process:
                    num_for_son = math.ceil(num * i.correlation_num[1] / i.correlation_num[0])
                    i.son.subtract_inventory(num_for_son, start_time, working_calendar)

# 产品控制类
class ProductDict:

    # 从csv文件获取所有物料的基础信息
    def __init__(self, address, consider_TF, no_con_people=None):
        self.product_all = {}  # 元素为产品对象
        if consider_TF:
            self.consider_process(address)
        else:
            self.not_consider_process(address, no_con_people)
    # 考虑工序的
    def consider_process(self,address):
        with open(address, newline='', encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            head_id = 0
            name = ''
            process_name = []  # 工序名称
            each_capacity_this = []  # 工序产能
            process_people_limit_this = []  # 工序人数
            type_machine = []  # 机器
            for row in reader:
                if row[0] != head_id:  # 更新变量
                    if head_id != 0:  # 将上个信息生成新对象
                        self.product_all[head_id] = Product(head_id, name, len(process_name), each_capacity_this, process_name)
                        self.product_all[head_id].add_people(process_people_limit_this, type_machine)
                    head_id = row[0]
                    name = row[1]
                    process_name = []
                    type_machine = []
                    each_capacity_this = []
                    process_people_limit_this = []
                    process_name.append(row[2])
                    each_capacity_this.append(row[6])
                    process_people_limit_this.append(row[7])
                    process_people_limit_this.append(row[8])
                    type_machine.append([row[4],row[5]])
                else:
                    process_name.append(row[2])
                    each_capacity_this.append(row[6])
                    process_people_limit_this.append(row[7])
                    process_people_limit_this.append(row[8])
                    type_machine.append([row[4], row[5]])
            # 将最后一个也录入到系统
            self.product_all[head_id] = Product(head_id, name, len(process_name), each_capacity_this, process_name)
            self.product_all[head_id].add_people(process_people_limit_this, type_machine)

    # 不考虑工序的
    def not_consider_process(self,address,no_con_people = None):
        with open(address, newline='',encoding = 'UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            head = []
            for row in reader:
                head = row[2:]
                break # 获取表头标签
            for row in reader:
                id = row[0] # 产品id
                name = row[1] # 产品名称
                each_capacity = row[2:] # 产品工序产能
                stay_index = [i for i in range(len(each_capacity)) if each_capacity[i] != '']
                head_this = []
                each_capacity_this = []
                for i in stay_index:
                    each_capacity_this.append(each_capacity[i])
                    head_this.append(head[i])
                process_num = len(each_capacity_this) # 产品工序数量
                #字典形式
                self.product_all[id] = Product(id,name,process_num,each_capacity_this,head_this)
        if no_con_people is not None:
            self.get_people_type(no_con_people)

    # 从csv文件获取所有物料的人数和产品类型
    def get_people_type(self,address):
        with open(address, newline='',encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  #跳过表头标签
            for row in reader:
                id = row[0] #产品编码
                type = row[2] # 产品类型
                type = [[''],[type],['']]
                process_people_limit = row[3:] # 产品人数限制
                stay_index = [i for i in range(len(process_people_limit)) if process_people_limit[i] != '']
                process_people_limit_this = []
                for i in stay_index:
                    process_people_limit_this.append(process_people_limit[i])
                self.product_all[id].add_people(process_people_limit_this,type)

    # 从csv文件获取所有物料的库存信息
    def get_inventory(self, address,history_dict = {}):

        with open(address, newline='',encoding = 'UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  #跳过表头标签
            for row in reader:
                id = row[0]  # 产品编码
                if history_dict != {}:
                    if history_dict.get(id,'no') != 'no':
                        now_inventory = int(row[2]) + int(history_dict[id])  # 现有库存
                    else:
                        now_inventory = row[2]
                else:
                    now_inventory = row[2]  # 现有库存
                safe_inventory = row[3]  # 安全库存
                min_size = row[4]  # 最小批量
                increase_num = row[5]  # 阶梯数量
                if self.product_all.get(id) is not None:
                    self.product_all[id].add_inventory(now_inventory, safe_inventory, min_size, increase_num)

    # 从csv文件中获取物料的工艺顺序信息
    def get_route_information(self, address):
        with open(address, newline='', encoding='UTF-8') as csv_file:
            reader = csv.reader(csv_file)
            next(reader, None)  # 跳过表头标签
            for row in reader:
                job_id = row[0]  # 物料编码
                if row[2] != '':
                    prefix_process_id = int(row[2])  # 前置工序
                    suffix_process_id = int(row[3])  # 后缀工序
                    self.product_all[job_id].route_information.append([prefix_process_id, suffix_process_id])
                # if row[3] != '':
                #     last_process_id = int(row[3]) # 最后一个工序
                #     self.product_all[job_id].route_information.append([suffix_process_id, last_process_id])

    # 获取所有产品的产品类型列表
    def get_type_list(self):
        product_type_set = set()
        keys = self.product_all.keys()
        for i in keys:
            for z in self.product_all[i].type:
                for aa in z:
                    if aa != '':
                        product_type_set.add(aa)
        product_type = sorted(product_type_set)
        return product_type

    # 获得关联的对应信息
    def get_orrespondence_for_singer(self, totalRowMaterial):
        for i in self.product_all:
            if totalRowMaterial.all_material.get(i) is not None:
                self.product_all[i].Correspondence_list = totalRowMaterial.all_material[i].Correspondence_list
            else:
                print('没有找到'+i+"物料的对应关系")
        max = 0
        for i in self.product_all:
            if 'Correspondence_list' in self.product_all[i].__dict__:
                for j in self.product_all[i].Correspondence_list:
                    if 'cycle_time' in j.son.__dict__:
                        if max < j.son.cycle_time:
                            max = j.son.cycle_time
        #print('最大等待时间为：'+str(max))

    # 获取产品中的库存量
    def print_Raw_material_inventory(self,working_calendar):
        material_inventory = {}
        for product_code,product in self.product_all.items():
            if 'Correspondence_list' in product.__dict__:
                for correspondence in product.Correspondence_list:
                    material_inventory = correspondence.son.get_Raw_material_inventory(material_inventory,working_calendar)
        time_set = set()
        for code,values in material_inventory.items():
            for this_value in values:
                time_set.add(this_value[1])
        time_list = list(time_set)
        time_list.sort(key=lambda x: datetime.datetime.strptime(x, "%Y-%m-%d"))  # 变成时间列表并排序

        # 物料名称 日期
        f = open('data/output_data/csv/material_inventory.csv', 'w', newline='')
        writer_all = csv.writer(f)
        head = ['物料编码'] + time_list
        writer_all.writerow(head)
        for code, values in material_inventory.items():
            row = [code] + [0]*len(time_list)
            for value in values:
                for time_index in range(len(time_list)):
                    if datetime.datetime.strptime(value[1], "%Y-%m-%d") <= datetime.datetime.strptime(time_list[time_index], "%Y-%m-%d"):
                        row[time_index + 1] = value[0]
            writer_all.writerow(row)
        f.close()

    # 获取成品随时间变化的库存信息，包括销售订单以及生产计划
    def get_product_inventory(self, sale_order_plan, scheduling_list, working_calendar, print_csv=True):
        now_time = working_calendar.working_calendar_date[0]  # 获取当前时间
        # 初始化，得到刚开始的库存信息
        for code,product in self.product_all.items():
            product.now_inventory_time = [[product.now_inventory, now_time]]  # 得到初始库存信息
        # 将销售信息写入库存信息中
        for order in sale_order_plan.OrderPlan:
            # 销售订单对产品分类好了，时间从小到大排序
            product_id = order.product_id
            num = self.product_all[product_id].now_inventory_time[-1][0] - order.product_num
            date = str(order.delivery_time).split(' ')[0]
            if self.product_all[product_id].now_inventory_time[-1][1] != date:
                self.product_all[product_id].now_inventory_time.append([num,date])
            else:
                self.product_all[product_id].now_inventory_time[-1][0] = num
        # 将生产计划写入库存信息中，这次是加 并且要判断此任务完成了没有
        for schedule in scheduling_list:
            if schedule.actually_start_time != '未开始' or schedule.delivery_status != 'Uncertain':  # 判断条件说明任务完成了
                time_str = schedule.end_time  # 入库时间
                num = schedule.production_num  # 个数
                product_id = schedule.product_id  # id
                index_location = -1  # 插入位置
                # 开始寻找插入位置
                for time_invent in range(len(self.product_all[product_id].now_inventory_time) - 1, -1, -1):
                    last_time = datetime.datetime.strptime(self.product_all[product_id].now_inventory_time[time_invent][1], "%Y-%m-%d")
                    time = datetime.datetime.strptime(time_str, "%Y-%m-%d")
                    if last_time == time:  # 如果时间等于原有时间，不用插入
                        index_location = time_invent
                        break
                    elif  last_time > time:  # 跳过
                        continue
                    elif last_time < time:  # 插入对应的位置
                        last_num = self.product_all[product_id].now_inventory_time[time_invent][0]
                        index_location = time_invent + 1
                        self.product_all[product_id].now_inventory_time.insert(index_location,[last_num,time_str])
                        break
                # 根据插入位置修改库存
                for i in range(index_location, len(self.product_all[product_id].now_inventory_time)):
                    self.product_all[product_id].now_inventory_time[i][0] += num

        self.print_product_inventory()  # 打印

    def print_product_inventory(self):
        time_list = []
        time_set = set()
        for code, product in self.product_all.items():
            for this_value in product.now_inventory_time:
                time_set.add(this_value[1])
        time_list = list(time_set)
        time_list.sort(key=lambda x: datetime.datetime.strptime(x, "%Y-%m-%d"))  # 变成时间列表并排序
        # 物料名称 日期
        f = open('data/output_data/csv/whole_product_material_inventory.csv', 'w', newline='')
        writer_all = csv.writer(f)
        head = ['物料编码'] + time_list
        writer_all.writerow(head)
        for code, product in self.product_all.items():
            row = [code] + [0] * len(time_list)
            for value in product.now_inventory_time:
                for time_index in range(len(time_list)):
                    if datetime.datetime.strptime(value[1], "%Y-%m-%d") <= datetime.datetime.strptime(
                            time_list[time_index], "%Y-%m-%d"):
                        row[time_index + 1] = value[0]
            writer_all.writerow(row)
        f.close()

