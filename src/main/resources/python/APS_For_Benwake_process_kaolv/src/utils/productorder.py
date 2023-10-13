import math
import csv
import datetime
from src.utils.product import ProductDict
from src import config
# -*- coding: gbk -*-



# 生产订单
class ProductionOrder:
    def __init__(self, id, product_id, production_num, finish_time):
        self.id = id  # 引起补货的销售订单di
        self.product_id = product_id  # 产品id
        self.production_num = production_num  # 生产数量
        self.finish_time = finish_time  # 入库时间
        self.start_time = ''  # 生产开始时间
        self.priority = ''  # 生产订单优先级
        self.notes = ''  # 生产订单所影响的销售订单id集合
        self.include_orders = None  # 包含的订单对象
        self.actually_start_time = '未开始'  # 实际开始时间

    # 计算 订单的优先级
    def calculate_priority(self):
        priority_list = []
        for i in self.include_orders:
            priority_list.append(i.priority_to_num())
        num_priority = max(priority_list)
        self.priority =  self.priority_num_back(num_priority)

    # 将订单优先级数值换成文字
    def priority_num_back(self,_str):
        if _str == 1:
            return "XD(意向询单)"
        elif _str == 2:
            return "YC(销售预测)"
        elif _str == 3:
            return "YG(供应链预估)"
        elif _str == 4:
            return "PR(客户提出付款意向)"
        elif _str == 5:
            return "PO(客户付款)"

    # 计算所包含订单的id
    def combining_notes(self):
        notes_str_list = []
        for i in self.include_orders:
            notes_str_list.append(i.note)
        self.notes = ','.join(notes_str_list)

    # 得到对应机器的可用状态,可用的机器有哪些
    def get_machine_running_time(self,people_machines,product,working_calendar):
        self.machine_running = []
        self.process_num = []
        self.process_name = []
        self.people_number = []
        for i in range(product.process_num):
            self.process_name.append(product.process_name[i])
            self.process_num.append(self.production_num)
            process_list = []
            [max,min] = product.process_people_limit[(i*2):(i*2+2)]
            for j in range(len(people_machines.machine_list)):
                process_list.append({'machine': people_machines.machine_list[j]})
            self.machine_running.append(process_list)
            people_num_list = []
            for people_num in range(min, max + 1):
                process_time = math.ceil(self.production_num*product.each_capacity[i]/working_calendar.unit_time/3600/people_num)
                process_list = []
                for j in range(len(people_machines.machine_list)):
                    process_list.append({'machine': people_machines.machine_list[j],'end_time':float('inf'),'start_time':float('inf')})
                people_num_list.append({'people_num': people_num, 'process_time': process_time,'machine_running':process_list})
            self.people_number.append(people_num_list)

    # 得到对应工序的最早结束时间以及对应的机器
    def get_earliest_end_machine(self, machine_running, people_num):
        machine = []
        machine_running.sort(key=lambda x: x['end_time'])  # 根据机器情况的最晚完成时间排序
        for i in range(people_num):
                machine.append(machine_running[i])
        start_time = machine[0]['start_time']
        end_time = machine[-1]['end_time']
        return machine, start_time, end_time

    # 计算是否完成，以及剩余的数量
    def get_remaining_quantity(self):
        remaining_num = []
        if 'completion_num' in self.__dict__:
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


# 生产计划类 没有加入对po的提前时间
class ProductionOrderPlan:
    # 根据销售订单计划，生成生产计划,包括数量 优先级 包含的订单类型
    def __init__(self, OrderPlan, product_capacity_address, product_inventory_address, working_calendar, history_object=None):
        self.production_order_Plan = []  # 生产计划列表
        self.production_order_Plan_part = []
        # 新增产品对象
        product_Dict = ProductDict(product_capacity_address, config.Consider_the_process)

        history_input_time = {}
        if history_object != None:
            history_dict = history_object.get_production_num_dict()
            product_Dict.get_inventory(product_inventory_address, history_dict)
        else:
            product_Dict.get_inventory(product_inventory_address)
        product_code = product_Dict.product_all.keys()  # 物料code集合
        product_replenish_or_not = {}
        for i in product_code:
            product_replenish_or_not[i] = {}  # key是物料id
        # 生产订单对应的订单对象列表
        include_orders_dict = {}
        # 遍历所有的销售订单计划
        for order in OrderPlan:
            if product_Dict.product_all.get(order.product_id) == None:
                continue
            # 此物料的现有库存
            now_inventory = product_Dict.product_all[order.product_id].now_inventory
            # 此物料的安全库存
            safe_inventory = product_Dict.product_all[order.product_id].safe_inventory
            # 判断其他订单是否需要生产
            if order.type[:2] == 'PO':
                if now_inventory - order.product_num >= 0:
                    # 更新库存
                    product_Dict.product_all[order.product_id].now_inventory = now_inventory - order.product_num
                    if product_replenish_or_not[order.product_id] != {}:
                        for i in product_replenish_or_not[order.product_id].keys():
                            product_replenish_or_not[order.product_id][i].append(order)  # 存储订单对象
                else:
                    # 求生产订单所影响的
                    if product_replenish_or_not[order.product_id] == {}:
                        product_replenish_or_not[order.product_id][order.id] = []
                        product_replenish_or_not[order.product_id][order.id].append(order)  # 存储订单对象
                    else:
                        temp_dict = product_replenish_or_not.pop(order.product_id)
                        for i in temp_dict.keys():
                            include_orders_dict[i] = temp_dict[i]
                        product_replenish_or_not[order.product_id] = {}
                        product_replenish_or_not[order.product_id][order.id] = []
                        product_replenish_or_not[order.product_id][order.id].append(order)  # 重新更新

                    min_size = product_Dict.product_all[order.product_id].min_size  # 最小批量
                    increase_num = product_Dict.product_all[order.product_id].increase_num  # 阶梯数量
                    add_num = min_size
                    if increase_num == 0:
                        increase_num = 1
                    if safe_inventory - (now_inventory - order.product_num + add_num) > 0:
                        add_num += math.ceil((safe_inventory - (
                                now_inventory - order.product_num + add_num))/increase_num)*increase_num
                    # 更新库存
                    product_Dict.product_all[order.product_id].now_inventory = now_inventory - order.product_num + add_num
                    if product_Dict.product_all.get(order.product_id) is not None:
                        self.production_order_Plan.append(ProductionOrder(order.id, order.product_id, add_num,
                                                                          str(order.delivery_time).split(' ')[0]))
            else:
                if now_inventory - order.product_num >= safe_inventory:
                    # 更新库存
                    product_Dict.product_all[order.product_id].now_inventory = now_inventory - order.product_num
                    if product_replenish_or_not[order.product_id] != {}:
                        for i in product_replenish_or_not[order.product_id].keys():
                            product_replenish_or_not[order.product_id][i].append(order)  # 存储订单对象
                else:
                    # 求生产订单所影响的
                    if product_replenish_or_not[order.product_id] == {}:
                        product_replenish_or_not[order.product_id][order.id] = []
                        product_replenish_or_not[order.product_id][order.id].append(order)  # 存储订单对象
                    else:
                        temp_dict = product_replenish_or_not.pop(order.product_id)
                        for i in temp_dict.keys():
                            include_orders_dict[i] = temp_dict[i]
                        product_replenish_or_not[order.product_id] = {}
                        product_replenish_or_not[order.product_id][order.id] = []
                        product_replenish_or_not[order.product_id][order.id].append(order)  # 重新更新

                    min_size = product_Dict.product_all[order.product_id].min_size  # 最小批量
                    increase_num = product_Dict.product_all[order.product_id].increase_num  # 阶梯数量
                    add_num = min_size
                    if increase_num == 0:
                        increase_num = 1
                    if safe_inventory - (now_inventory - order.product_num + add_num) > 0:
                        add_num += math.ceil((safe_inventory - (
                                    now_inventory - order.product_num + add_num)) / increase_num) * increase_num
                    # 更新库存
                    product_Dict.product_all[
                        order.product_id].now_inventory = now_inventory - order.product_num + add_num
                    if product_Dict.product_all.get(order.product_id) is not None:
                        self.production_order_Plan.append(ProductionOrder(order.id, order.product_id, add_num,
                                                                          str(order.delivery_time).split(' ')[0]))

            if history_object != None:
                # 判断历史遗留的开始时间
                if history_dict.get(order.product_id) != None:
                    if now_inventory - order.product_num - history_dict.get(order.product_id) < 0:
                        history_input_time[order.product_id] = order.delivery_time
                        del history_dict[order.product_id]

        # 循环结束之后存在剩余订单残留
        for i in product_replenish_or_not.keys():
            if product_replenish_or_not[i] != {}:
                for j in product_replenish_or_not[i].keys():
                    include_orders_dict[j] = product_replenish_or_not[i][j]

        # 根据订单id赋值
        for i in self.production_order_Plan:
            temp = include_orders_dict.get(i.id,None)
            if temp != None:
                i.include_orders = include_orders_dict[i.id]
                i.calculate_priority()  # 获取订单优先级
                i.combining_notes()  # 获取订单所包含的订单id
        if history_object != None:
            # 计算历史遗留的结束时间
            history_object.get_finish_time_all(history_input_time, working_calendar)

        # 让用户判断是否筛选PO出来
        if config.split_po_orders:
            # 将po筛选出来
            new_production_list = []
            new_production_index_list = []
            for i in range(len(self.production_order_Plan)):
                if self.production_order_Plan[i].priority[:2] == 'PO' and len(self.production_order_Plan[i].include_orders) != 1:  # 如果是po，并且包含其他订单，提出来
                    include_orders_po = []
                    include_orders_other = []
                    index_po_order = []
                    for order_op in range(len(self.production_order_Plan[i].include_orders)):
                        if self.production_order_Plan[i].include_orders[order_op].type[:2] == 'PO':
                            include_orders_po.append(self.production_order_Plan[i].include_orders[order_op])
                            index_po_order.append(order_op)
                        else:
                            include_orders_other.append(self.production_order_Plan[i].include_orders[order_op])
                    for change_index, delete_index in enumerate(index_po_order):  # 删除下标
                        delete_index -= change_index
                        del self.production_order_Plan[i].include_orders[delete_index]
                    # 先生成新po订单
                    num = 0
                    for j in include_orders_other:
                        num += j.product_num
                    time = datetime.datetime.strftime(include_orders_po[0].delivery_time, "%Y-%m-%d")
                    new_ProductionOrder = ProductionOrder(include_orders_po[0].id, include_orders_po[0].product_id, self.production_order_Plan[i].production_num - num,time)
                    new_ProductionOrder.include_orders = include_orders_po  # 销售订单集合
                    new_ProductionOrder.priority = include_orders_po[0].type  # 优先级
                    new_ProductionOrder.combining_notes()  # notes
                    new_production_list.append(new_ProductionOrder)
                    new_production_index_list.append(i)

                    # 将旧的信息改掉
                    self.production_order_Plan[i].finish_time = datetime.datetime.strftime(self.production_order_Plan[i].include_orders[0].delivery_time,"%Y-%m-%d")
                    # 修改id
                    self.production_order_Plan[i].id = self.production_order_Plan[i].include_orders[0].id
                    # 修改数量
                    self.production_order_Plan[i].production_num = num
                    # 修改优先级
                    self.production_order_Plan[i].priority = self.production_order_Plan[i].include_orders[0].type
                    # 修改notes
                    self.production_order_Plan[i].combining_notes()  # notes
            for i in range(len(new_production_list)):
                self.production_order_Plan.insert(new_production_index_list[i], new_production_list[i])

    # 计算开始时间
    def get_start_date(self,product_capacity_address,people_type_address,working_calendar):
        # 新增产品对象
        product_Dict = ProductDict(product_capacity_address,config.Consider_the_process,people_type_address)
        # 计算加工时间，工作日天数
        for production_order in self.production_order_Plan:
            # 获取完成时间
            finish_time = production_order.finish_time
            # 获取对应产品的产能信息，以及 人数信息
            each_capacity = product_Dict.product_all[production_order.product_id].each_capacity
            # 获取人数信息
            process_people_limit = product_Dict.product_all[production_order.product_id].process_people_limit
            # 生产一件所需时间
            one_process_time = 0
            for i in range(len(each_capacity)):
                try:
                    one_process_time += float(each_capacity[i])/int(process_people_limit[i*2 + 1])
                except:
                    print('get_start_date出现错误')
            # 加工天数
            process_day = - math.ceil(one_process_time * production_order.production_num / 3600 / (working_calendar.hours_in_day - 1))
            # 得到对应的开始时间，此处PO进行了减7的操作
            if production_order.priority[:2] == 'PO' or production_order.priority[:2] == 'PR':
                production_order.start_time = working_calendar.get_date_after_days(process_day - config.in_advance_po, finish_time)
            else:
                production_order.start_time = working_calendar.get_date_after_days(process_day,finish_time)
        self.sorted_by_start_time()

    # 对生产计划按照开始时间排序
    def sorted_by_start_time(self):
        self.production_order_Plan.sort(key= lambda production_order: datetime.datetime.strptime(production_order.start_time, "%Y-%m-%d") )

    # 打印生产计划到csv
    def print_csv(self, Product_control, history_order_plan = None):
        fp = open('data/output_data/csv/Order_processing_time_sequence.csv', 'w', newline='')
        headline = ['生产ID', '任务来源ID', '物料编码', '物料名称', '需补货数量', '预计开始时间', '需入库时间', '优先级', '所包含销售订单']
        if history_order_plan is not None:
            now_id = 1 + history_order_plan.history_num
        else:
            now_id = 1
        w_fp = csv.writer(fp)
        w_fp.writerow(headline)
        for i in self.production_order_Plan:
            finish_time = str(i.finish_time).split(' ')[0]
            row = [str(now_id), i.id, i.product_id, Product_control.product_all[i.product_id].name.encode("gbk", 'ignore').decode("gbk", "ignore"),
                   i.production_num, i.start_time, finish_time, i.priority, i.notes]
            now_id += 1
            try:
                w_fp.writerow(row)
            except:
                print('生产数据文件出现读写错误')
        fp.close()

    # 选取规定日期之前的订单计划
    def get_specified_order(self,date):

        temp_specified_time = datetime.datetime.strptime(date, "%Y-%m-%d")
        for i in self.production_order_Plan:
            temp_start_time = datetime.datetime.strptime(i.start_time, "%Y-%m-%d")
            if temp_start_time <= temp_specified_time:
                self.production_order_Plan_part.append(i)

    # 得到订单计划的个数
    def get_specified_order_num(self,judge):
        if judge: # True 显示部分
            return len(self.production_order_Plan_part)
        else: # False 显示全部
            return len(self.production_order_Plan)

