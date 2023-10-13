import pandas as pd


# 物料类
class RowMaterial:
    def __init__(self, code):
        self.code = code
        self.inventory = 0
        self.purchase_orders_list = []

    def getAnOrder(self, purchase_orders):
        self.purchase_orders_list.append(purchase_orders)

    def addInventory(self, num):
        self.inventory += num
    def addName(self, name):
        self.name = name

# 物料列表类
class TotalRowMaterial:
    # 导入所有物料的库存信息以及采购订单信息
    def __init__(self):
        item_inventory_total = self.read_item_inventory("即时库存汇总数据查询.xlsx")
        purchase_orders_total = self.read_purchase_orders("采购订单.xlsx")
        total_item = self.get_index_set(item_inventory_total, purchase_orders_total)
        self.all_material = {}
        for item in total_item:
            self.all_material[item] = RowMaterial(item)
        for row in item_inventory_total.itertuples():
            self.all_material[row[0]].addName(row[2])
        for row in item_inventory_total.itertuples():
            self.all_material[row[0]].addInventory(row[1])
        for row in purchase_orders_total.itertuples():
            self.all_material[row[0]].getAnOrder([row[1], row[2]])

    def read_item_inventory(self,file_name):
        return pd.read_excel(file_name).set_index("物料编码")[["可用量(主单位)","物料名称"]].drop(index=['合计'])

    def read_purchase_orders(self,file_name):
        return pd.read_excel(file_name).set_index("物料编码")[["剩余收料数量", "交货日期"]].dropna(how='any')

    def get_index_set(self,df1, df2):
        return list(set(list(df1.index.unique())).union(set(list(df2.index.unique()))))


if __name__ == '__main__':
    totalRowMaterial = TotalRowMaterial()
    a = 1

