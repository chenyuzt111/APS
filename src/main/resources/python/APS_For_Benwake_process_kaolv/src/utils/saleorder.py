import pandas as pd
import datetime
# -*- coding:utf-8 -*-


# 销售订单类
class SaleOrder:
    def __init__(self, id, product_id, product_num, delivery_time, type):
        self.id = id  # 订单id
        self.product_id = product_id  # 产品编码
        self.product_num = int(product_num)  # 产品数量
        self.delivery_time = delivery_time  # 交付时间
        self.type = type  # 订单类型
        self.note = id  # 临时加的，存储订单
        # 延期操作
        # YC目前+7
        if self.type[:2] == 'YC':
            self.delivery_time = self.delivery_time + datetime.timedelta(days=14)
        # 需要对XD的期望发货日期与today进行对比，期望发货日期-today>30则期望发货日期不进行数据处理，<=30，则期望发货日期+30
        elif self.type[:2] == 'XD' and ((int(self.delivery_time.timestamp()) - int(datetime.datetime.now().timestamp())) // (3600 * 24)) <= 30:
            self.delivery_time = self.delivery_time + datetime.timedelta(days=30)

    # 订单类型换成数值优先级
    def priority_to_num(self):
        if self.type[:2] == "XD":
            return 1
        elif self.type[:2] == "YC":
            return 2
        elif self.type[:2] == "YG":
            return 3
        elif self.type[:2] == "PR":
            return 4
        elif self.type[:2] == "PO":
            return 5
        else:
            print("错误的优先级")
            return -1


# 销售计划类
class SaleOrderPlan:

    def __init__(self, address):
        self.OrderPlan = []  # 元素为销售订单对象
        data = pd.read_csv(address,encoding = 'utf-8')
        data['time'] = pd.to_datetime(data['time'])  # 更改数据类型
        data = data.drop(data[data['ID'] == '0'].index)  # 删除 0 元素
        data.sort_values(by=["code", "time"], axis=0, ascending=[True, True], inplace=True)
        data = data.reset_index(drop=True) # 排序后重置索引
        for i in range(len(data)):
            order_temp = data.loc[i]
            self.OrderPlan.append(SaleOrder(order_temp[0],
                                            order_temp[1],
                                            order_temp[3],
                                            order_temp[4],
                                            order_temp[5],
                                            ))

    # 叠加相同产品相同时间下的订单数量
    def Overlay_of_same_product_and_date(self):
        # 在此之前要搞定每个物料中，每个时间的数量求和
        previous_time = '1999-1-1'
        previous_id = ''
        # 需要保留的订单
        stay_num_list = []
        # 全部订单
        all_list = []
        for i in range(len(self.OrderPlan)):
            if previous_id == '':
                previous_id = self.OrderPlan[i].product_id
            all_list.append(i)
            if previous_time == str(self.OrderPlan[i].delivery_time) and previous_id == self.OrderPlan[i].product_id:
                if self.OrderPlan[i].type[:2] == 'PO':
                    stay_num_list.append(i - 1)
                    continue
                # 更新数量
                self.OrderPlan[i].product_num += self.OrderPlan[i-1].product_num

                # 优先级转化
                if self.OrderPlan[i-1].priority_to_num() > self.OrderPlan[i].priority_to_num():
                    self.OrderPlan[i].type = self.OrderPlan[i-1].type

                # 更新note
                self.OrderPlan[i].note = ','.join([self.OrderPlan[i-1].note, self.OrderPlan[i].note])
            else:
                previous_time = str(self.OrderPlan[i].delivery_time)
                previous_id = self.OrderPlan[i].product_id
                if i != 0:
                    stay_num_list.append(i - 1)
            if i == len(self.OrderPlan) - 1:
                stay_num_list.append(i)
        # 对不需要的进行删除
        drop_list = list(set(all_list) - set(stay_num_list))
        drop_list.sort(reverse=True)
        for i in drop_list:
            del self.OrderPlan[i]

