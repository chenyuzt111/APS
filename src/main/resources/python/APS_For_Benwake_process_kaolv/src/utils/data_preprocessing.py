import pandas as pd
import sys
from src import config

# 定义一个全局变量，用于判断数据完整性检查情况，如果为True，那么久没问题，False则需要停掉程序
GLOBAL_VARIABLE = True


# 数据预处理
class Data_preprocess:
    def __init__(self):
        # dict1和dict2用于预处理标准间替代件
        self.dict1 = {}
        self.dict2 = {}
        # 设计一个字典，保存各个表缺失的信息(不考虑工序)
        self.dict3 = {'物料清单列表': [['错误信息']],
                      'BOM': [["父项物料编码", "子项物料编码", "用量:分子", "用量:分母", "生产工序"]],
                      '物料清单': [["父项物料编码", "子项物料编码", "用量:分子", "用量:分母"]],
                      '物料库存信息': [['物料编码', '物料名称', '现有库存', '安全库存', '最小批量', '阶梯数量']],
                      '半成品库存信息': [['物料编码', '物料名称', '现有库存', '安全库存', '最小批量', '阶梯数量']],
                      'MOQ': [['物料编码', '物料名称', '周期', 'MOQ', 'MPQ']],
                      '物料人数数据': [['物料编码', '物料名称', '产品类型', '组装1人数MAX', '组装1人数MIN',
                                  '测试人数MAX', '测试人数MIN', '包装人数MAX', '包装人数MIN']],
                      '工作日历': [['date', 'people_num']],
                      '物料产能数据': [['code', 'product_name', '组装', '测试', '包装']]}
        # 设计一个字典，保存各个表缺失的信息(考虑工序)
        self.dict4 = {'物料清单列表': [['错误信息']],  # 未更新
                      'BOM': [["父项物料编码", "子项物料编码", "用量:分子", "用量:分母", "生产工序"]],  # 未更新
                      '物料清单': [["父项物料编码", "子项物料编码", "用量:分子", "用量:分母"]],  # 未更新
                      '物料库存信息': [['物料编码', '物料名称', '现有库存', '安全库存', '最小批量', '阶梯数量']],  # 未更新
                      '半成品库存信息': [['物料编码', '物料名称', '现有库存', '安全库存', '最小批量', '阶梯数量']],  # 未更新
                      'MOQ': [['物料编码', '物料名称', '周期', 'MOQ', 'MPQ']],  # 未更新
                      '物料人数数据': [['物料编码', '物料名称', '产品类型', '组装1人数MAX', '组装1人数MIN',  # 未更新
                                  '测试人数MAX', '测试人数MIN', '包装人数MAX', '包装人数MIN']],  # 未更新
                      '工作日历': [['date', 'people_num']],  # 未更新
                      '物料工序和产能表-考虑工序': [['所有工序', '产品族', '工序名称', '标准工时', '人数MAX', '人数MIN']],  # 更新
                      '工序搭配': [['所用工序', '产品族', '前置工序', '后置工序', '切换时间(s)']],  # 更新
                      '工序搭配中间表': [['物料编码', '物料名称', '前置工序', '后缀工序']],  # 更新
                      '机器表': [['产品族', '工序', '机器名称', '使用车间']]}  # 更新

    # 此方法用于将物料清单的标准件替代件进行整合，整合到采购订单和即时库存中
    def preprocess1(self, wuliao_address):
        # 读取数据
        caigou_address = "data/origin_data/采购订单.xlsx"
        jishi_address = "data/origin_data/即时库存汇总数据查询.xlsx"
        material_total = pd.read_excel(wuliao_address)[['父项物料编码', '项次', '子项物料编码', '子项类型']]

        # 使用groupby将数据分组
        material_total_grouped = material_total.groupby(['父项物料编码', '项次'])
        # 筛选出符合条件的组合
        result_to_dict1 = material_total_grouped.filter(lambda x: ('标准件' in x['子项类型'].values) and ('替代件' in x['子项类型'].values))

        # 初始化dict1
        for i in result_to_dict1['父项物料编码']:
            self.dict1[i] = []

        # 将result_to_dict1整合成dict1，先筛选出和父项编码一样的，然后再根据项次分组，进行打包，得到结果
        for key in self.dict1.keys():
            item_list = result_to_dict1[result_to_dict1['父项物料编码'] == key]
            item_list = item_list.groupby('项次')[['子项物料编码', '子项类型']].apply(
                lambda x: dict(zip(x['子项物料编码'], x['子项类型']))).reset_index()[0].tolist()
            self.dict1[key] = item_list

        # 将dict2处理为标准件为键，替代件列表为值的字典
        for key, value_list in self.dict1.items():
            for value_dict in value_list:
                if len(value_dict) != 1:
                    dict2_key = ''
                    # 确定键
                    for k, v in value_dict.items():
                        if v == '标准件':
                            dict2_key = k
                    self.dict2[dict2_key] = []
                    # 确定值
                    for k, v in value_dict.items():
                        if v == '替代件':
                            self.dict2[dict2_key].append(k)

        # 对dict2需要进行检查
        self.check_reverse_pairs(self.dict2)
        self.check_duplicates_pairs(self.dict2)

        # 程序如果没问题，则开始对采购订单和即时库存进行处理
        purchase_orders_total = pd.read_excel(caigou_address)
        item_inventory_total = pd.read_excel(jishi_address)
        '''
        此处进行缺失值填补非常重要，既是先验检查又能在处理时降低复杂度
        '''
        purchase_orders_total['剩余收料数量'] = purchase_orders_total['剩余收料数量'].fillna(0)
        item_inventory_total['可用量(主单位)'] = item_inventory_total['可用量(主单位)'].fillna(0)
        # 开始数据的求和
        for key, value_list in self.dict2.items():
            if len(value_list) == 1:
                # 采购订单
                purchase_orders_total.loc[purchase_orders_total['物料编码'] == key, '剩余收料数量'] += \
                    purchase_orders_total[purchase_orders_total['物料编码'] == value_list[0]]['剩余收料数量'].sum()
                # 即时库存
                item_inventory_total.loc[item_inventory_total['物料编码'] == key, '可用量(主单位)'] += \
                    item_inventory_total[item_inventory_total['物料编码'] == value_list[0]]['可用量(主单位)'].sum()
            else:
                for value in value_list:
                    # 采购订单
                    purchase_orders_total.loc[purchase_orders_total['物料编码'] == key, '剩余收料数量'] += \
                        purchase_orders_total[purchase_orders_total['物料编码'] == value]['剩余收料数量'].sum()
                    # 即时库存
                    item_inventory_total.loc[item_inventory_total['物料编码'] == key, '可用量(主单位)'] += \
                        item_inventory_total[item_inventory_total['物料编码'] == value]['可用量(主单位)'].sum()

        # 将处理后的采购订单和即时库存保存
        purchase_orders_total.to_excel("data/origin_data/采购订单.xlsx", index=False)
        item_inventory_total.to_excel("data/origin_data/即时库存汇总数据查询.xlsx", index=False)
        # 返回对应关系，如果出错在这里看，可以看到哪里找错了，方便发现问题
        # return (self.dict1, self.dict2)
        # print('preprocess1: ', GLOBAL_VARIABLE)

    # 数据完整性检查
    # 检查dict2是否存在相反的键值对
    def check_reverse_pairs(self, dictionary):
        global GLOBAL_VARIABLE
        # 设置集合存储键值对
        checked_pairs = set()
        for key, value in dictionary.items():
            # 存在相同或者相反就打印并且返回
            if (key, value[0]) in checked_pairs or (value[0], key) in checked_pairs:
                self.dict3['物料清单列表'].append([f"存在标准件替代件相反的两对: '{key}':'{value[0]}' 和 '{value[0]}':'{key}'"])
                self.dict4['物料清单列表'].append([f"存在标准件替代件相反的两对: '{key}':'{value[0]}' 和 '{value[0]}':'{key}'"])
                # print(f"存在标准件替代件相反的两对: '{key}':'{value[0]}' 和 '{value[0]}':'{key}'")
            else:
                checked_pairs.add((key, value[0]))
        if len(self.dict3['物料清单列表']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_reverse_pairs: ', GLOBAL_VARIABLE)

    # 检查dict2是否存在值相同的键值对
    def check_duplicates_pairs(self, dictionary):
        global GLOBAL_VARIABLE
        # 创建一个空字典，用于存储具有相同值的键值对
        duplicates = {}
        # 遍历原始字典的键值对
        for key, value in dictionary.items():
            # 如果值已经在duplicates字典中，将该键值对添加到列表中
            if value[0] in duplicates:
                duplicates[value[0]].append(key)
            else:
                # 如果值不在duplicates字典中，创建一个新的键值对列表
                duplicates[value[0]] = [key]
        # 打印具有相同值的键值对
        for value, keys in duplicates.items():
            if len(keys) > 1:
                self.dict3['物料清单列表'].append([f"替代件{value}具有相同的标准件：{', '.join(keys)}"])
                self.dict4['物料清单列表'].append([f"替代件{value}具有相同的标准件：{', '.join(keys)}"])
                # print(f"替代件{value}具有相同的标准件：{', '.join(keys)}")
        if len(self.dict3['物料清单列表']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_duplicates_pairs: ', GLOBAL_VARIABLE)

    # 需求计划处理和检查
    # 将原始数据处理成sale_plan.csv和history_information.csv
    def preprocess2(self, origin_sale_address, origin_history_address):
        origin_sale = pd.read_csv(origin_sale_address, encoding='utf-8')
        origin_history = pd.read_csv(origin_history_address, encoding='utf-8')
        # 取出所需要的列
        sale_plan = origin_sale[['ID号', '物料编码', '物料名称', '数量', '期望发货时间', '订单类型']]
        history_information = origin_history[['订单编号', '物料编码', '工序', '总数量', '完成数量']]
        # 新表的列名
        sale_plan_columns = ['ID', 'code', 'name', 'num', 'time', 'order_type']
        history_information_columns = ['code',	'product_id', 'process_name', 'num', 'process_num']
        sale_plan.columns = sale_plan_columns
        history_information.columns = history_information_columns
        # 相应的处理，此处出列num为正数
        sale_plan['num'] = sale_plan['num'].fillna(0).astype(int)
        sale_plan['time'] = sale_plan['time'].str.replace('/', '-')
        history_information['num'] = history_information['num'].fillna(0).astype(int)
        history_information['process_num'] = history_information['process_num'].fillna(0).astype(int)
        # 存为csv
        sale_plan.to_csv('data/origin_data/sale_plan.csv', index=False, encoding='utf-8')
        history_information.to_csv('data/origin_data/history_information.csv', index=False, encoding='utf-8')

    # 处理考虑工序部分的表格(基于物料基础数据表和物料工序和产能表，机器表，合并)
    def preprocess3(self, product_capacity_process_address, true_machine_address):
        product_capacity_process = pd.read_csv(product_capacity_process_address, encoding='utf-8')[
            ['产品族', '工序名称', '包装方式', '标准工时', '人数MAX', '人数MIN']]
        base_material = pd.read_csv('data/origin_data/物料基础数据表.csv', encoding='utf-8')[
            ['产品族', '物料编码', '物料名称', '包装方式']]
        true_machine = pd.read_csv(true_machine_address, encoding='utf-8')[['产品族', '工序', '机器名称']]
        # 将两个表按照产品族进行内连接
        merge_capacity_process = product_capacity_process.merge(base_material, on='产品族', how='inner')
        # 将多余出来的单品和整箱所在行删掉，形成中间第一结果表
        total_code = list(set(base_material['物料编码']))
        total_code = list(filter(lambda i: (i[:2] != '10') & (i[:2] != '12'), total_code))
        mid_result = pd.DataFrame()
        for i in total_code:
            item = merge_capacity_process[merge_capacity_process['物料编码'] == i]
            # 此处理论上已经做过数据完整性检查，不用做异常处理，此时因为表格不完整，为保证程序正常运行，加入异常捕获
            try:
                if '整箱' in item['包装方式_y'].values[0]:
                    item = item[item['包装方式_x'] != '单品']
                elif '单品' in item['包装方式_y'].values[0]:
                    item = item[item['包装方式_x'] != '整箱']
                # 这样放进来就可以保证他们是按照工序顺序存储的
                mid_result = pd.concat([mid_result, item], axis=0)
            except:
                print(f"没有找到物料{i}对应的产品族")
        mid_result = mid_result.reset_index(drop=True)
        # 将中间结果和机器表进行整合，需要将机器名称添加进去，因为可能有多个名称，所以需要新加列
        for i in range(len(mid_result)):
            item = true_machine[(true_machine['产品族'] == mid_result['产品族'][i]) &
                                (true_machine['工序'] == mid_result['工序名称'][i])]
            item_len = len(item)
            for j in range(1, item_len + 1):
                column_name = f'机器ID{j}'
                if column_name not in mid_result.columns:
                    mid_result[column_name] = None
                mid_result[f'机器ID{j}'][i] = list(item['机器名称'])[j - 1]
        # 整理为需要的列顺序
        cols_to_move = ['标准工时', '人数MAX', '人数MIN']
        new_order = [col for col in mid_result.columns if col not in cols_to_move] + cols_to_move
        mid_result = mid_result[new_order]
        # 将另一个包装方式删掉，并进行重命名
        mid_result = mid_result.drop(columns='包装方式_y')
        mid_result = mid_result.rename(columns={'包装方式_x': '包装方式'})
        mid_result.to_csv('data/origin_data/process_all_information.csv', encoding='utf-8', index=False)
        # mid_result.to_excel('data/origin_data/process_all_information.xlsx', encoding='utf-8', index=False)

    # 处理考虑工序部分的表格(工序搭配中间表)
    def preprocess4(self, origin_route_information_address):
        route_information = pd.read_csv(origin_route_information_address, encoding='utf-8')[
            ['产品族', '所用工序', '前置工序', '后置工序', '切换时间(s)']]
        base_material = pd.read_csv('data/origin_data/物料基础数据表.csv', encoding='utf-8')[
            ['产品族', '物料编码', '物料名称', '包装方式']]
        # 将两个表按照产品族进行内连接
        merge_route_information = route_information.merge(base_material, on='产品族', how='inner')
        # 将多余出来的单品和整箱所在行删掉，形成中间第一结果表
        total_code = list(set(base_material['物料编码']))
        total_code = list(filter(lambda i: (i[:2] != '10') & (i[:2] != '12'), total_code))
        mid_result = pd.DataFrame()
        for i in total_code:
            item = merge_route_information[merge_route_information['物料编码'] == i]
            try:
                mid_result = pd.concat([mid_result, item], axis=0)
            except:
                print(f"没有找到物料{i}对应的产品族")
        # 重置索引，调整列名
        mid_result = mid_result.reset_index(drop=True)
        mid_result = mid_result[['所用工序', '产品族', '物料编码', '物料名称', '前置工序', '后置工序', '切换时间(s)']]
        mid_result.to_csv('data/origin_data/mid_route_information.csv', encoding='utf-8', index=False)
        # mid_result.to_excel('data/origin_data/mid_route_information.xlsx', encoding='utf-8', index=False)

    # 需求-成品-半成品-物料信息完整性检查
    # 获取13 DZ 12 10列表用于后边检查，同时检查BOM和物料清单对应关系
    def check_sale_plan(self):
        global GLOBAL_VARIABLE
        sale_plan = pd.read_csv('data/origin_data/sale_plan.csv', encoding='utf-8')
        history_information = pd.read_csv('data/origin_data/history_information.csv', encoding='utf-8')
        # 合并现在订单和历史订单的产品id
        now_code = list(sale_plan['code'])
        history_code = list(history_information['product_id'])
        now_code.extend(history_code)
        # 获取到需求计划里所有的物料编码，会有重复，所以用set去重
        product_code = list(set(now_code))
        # 在BOM里先检查13 DZ有哪些是不在BOM里的
        bom = pd.read_excel("data/origin_data/工序BOM.xlsx")[["父项物料编码", "子项物料编码", "用量:分子", "用量:分母", "生产工序"]]
        # 筛选出BOM中和需求计划对应上的数据信息
        bom = bom[bom['父项物料编码'].isin(product_code)]
        code_notin = [i for i in product_code if i not in bom['父项物料编码'].tolist()]
        # 如果存在一些产品是BOM里没有的，那么就变为False
        if not bool(code_notin):
            self.dict3['BOM'].extend(code_notin)
            self.dict4['BOM'].extend(code_notin)
        # 产品检查完需要检查半成品和基础物料
        semi_code = []
        material_code = []
        # 新加子项DZ
        material_dz_code = []
        # 如果物料都有，那么就该检查13/DZ对应的12/10和其他信息是否存在，如果有缺失也是不行的，需要把父料保存到dict3里
        if bom.isnull().values.any():
            for index, row in bom.iterrows():
                # 检查该行是否有缺失值
                if row.isnull().any():
                    # 如果行中有缺失值，将父项物料编码所在的行添加到dict3里
                    self.dict3['BOM'].append([row[0], row[1], row[2], row[3], row[4]])
                    self.dict4['BOM'].append([row[0], row[1], row[2], row[3], row[4]])
                # 如果缺失值不在这一行，那么需要将产品对应的12/10添加到表中
                else:
                    if row[1][:2] == '12':
                        semi_code.append(row[1])
                    elif row[1][:2] == '10':
                        material_code.append(row[1])
                    elif row[1][:2] == 'DZ':
                        material_dz_code.append(row[1])
        # 如果没缺失值，那就将12/10添加到列表中
        else:
            for index, row in bom.iterrows():
                if row[1][:2] == '12':
                    semi_code.append(row[1])
                elif row[1][:2] == '10':
                    material_code.append(row[1])
                elif row[1][:2] == 'DZ':
                    material_dz_code.append(row[1])
        # 去重
        semi_code = list(set(semi_code))
        material_code = list(set(material_code))
        material_dz_code = list(set(material_dz_code))
        # 13/DZ的对应关系和是否存在已经检查完成，下一步将BOM中的子项取出，分为12/10，并且检查物料清单中12对应10的关系是否全面
        # 读取物料清单，此时10就不需要考虑了，只需要考虑12
        material_list = pd.read_excel("data/origin_data/物料清单.xlsx")[["父项物料编码", "子项物料编码", "用量:分子", "用量:分母"]]
        code_12_notin = [i for i in semi_code if i not in material_list['父项物料编码'].tolist()]
        code_semi_dz_notin = [i for i in material_dz_code if i not in material_list['父项物料编码'].tolist()]
        # 如果存在一些物料是物料清单里没有的，那么就变为False
        if not bool(code_12_notin):
            self.dict3['物料清单'].extend(code_12_notin)
            self.dict4['物料清单'].extend(code_12_notin)
        if not bool(code_semi_dz_notin):
            self.dict3['物料清单'].extend(code_semi_dz_notin)
            self.dict4['物料清单'].extend(code_semi_dz_notin)
        # 在这里增加一个递归程序，目的是，递归的找到所有的12和10，因为12可能会对应12，此时应该不断迭代到最后一步
        # 先将物料清单中父料编码在semi_code中存在的信息进行保留
        # semi_material_list = material_list[material_list['父项物料编码'].isin(semi_code)]
        '''
        for i in semi_code:
            def
            这里筛选出父料存在的，找到所有对应的子料，作为中间表
            再遍历中间表
                if i对应的子料为10，那就添加到表中，但是这里有个问题，子料有很多，所以是否需要先进行分组
                elif i对应的子料为12.那么就再进行一遍这个程序 
        因为时间问题，此处先简化为遍历所有的物料清单，先将存在于semi_code里边的数据子料分别添加进对应的列表里
        然后将物料清单和semi_code对应上，然后对semi_material_list进行检查对应信息
        '''
        for index, row in material_list.iterrows():
            # 必须这行的父料在semi_code里才行
            if row[0] in semi_code:
                # 子料也在并且是10就继续
                if row[1] in material_code and row[1][:2] == '10':
                    continue
                # 如果子料不在material_code里并且是10就添加进列表里
                elif row[1] not in material_code and row[1][:2] == '10':
                    material_code.append(row[1])
                    # print(row[1])
                # 如果子料是12开头并且在semi_code里那就行
                elif row[1] in semi_code and row[1][:2] == '12':
                    continue
                # 如果子料为12开头并且不在semi_code里就把他加进去
                elif row[1] not in semi_code and row[1][:2] == '12':
                    semi_code.append(row[1])
                    # print(row[1])
            elif row[0] not in semi_code:
                continue
        semi_material_list = material_list[material_list['父项物料编码'].isin(semi_code)]
        # 如果物料都有，那么就该检查12对应的10和其他信息是否存在，如果有缺失也是不行的，需要把物料存到物料清单里
        if semi_material_list.isnull().values.any():
            for index, row in bom.iterrows():
                # 检查该行是否有缺失值
                if row.isnull().any():
                    # 如果行中有缺失值，将父项物料编码添加到dict3里
                    self.dict3['物料清单'].append([row[0], row[1], row[2], row[3]])
                    self.dict4['物料清单'].append([row[0], row[1], row[2], row[3]])

        # 把物料清单里边的数据全部拿出来，分配到12/10上边（这两行现在被上边替代，如果哪天需要检查所有的物料，那么这两行要更新）
        # semi_code = semi_code.extend(material_list['父项物料编码'])
        # material_code = material_code.extend(material_list['子项物料编码'])
        # 保证没问题就要进行去重操作
        semi_code = list(set(semi_code))
        material_code = list(set(material_code))
        '''
        至此，拿到所有的13/DZ：product_code
        所有的12：semi_code
        所有的10：material_code
        下边开始做其他表的检查
        '''
        if len(self.dict3['BOM']) > 1:
            GLOBAL_VARIABLE = False
        if len(self.dict3['物料清单']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_sale_plan:', GLOBAL_VARIABLE)
        return product_code, semi_code, material_code

    def check_13andDZ(self, product_code):
        global GLOBAL_VARIABLE
        # 读取物料库存
        product_inventory = pd.read_csv('data/origin_data/product_inventory.csv', encoding='utf-8')
        for i in range(len(product_code)):
            # 记录在物料13列表里但是不在物料库存里的数据
            if product_code[i] not in product_inventory['物料编码'].values:
                self.dict3['物料库存信息'].append(product_code[i])
                self.dict4['物料库存信息'].append(product_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断这两个是否为空
                key_info = product_inventory[product_inventory['物料编码'] == product_code[i]][
                    ['最小批量', '阶梯数量']].isnull().values.any()
                if key_info:
                    self.dict3['物料库存信息'].append(
                        product_inventory[product_inventory['物料编码'] == product_code[i]].values.tolist()[0])
                    self.dict4['物料库存信息'].append(
                        product_inventory[product_inventory['物料编码'] == product_code[i]].values.tolist()[0])
        if len(self.dict3['物料库存信息']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_13andDZ:', GLOBAL_VARIABLE)

    def check_12(self, semi_code):
        global GLOBAL_VARIABLE
        # 读取物料库存
        product_inventory = pd.read_csv('data/origin_data/半成品库存信息.csv', encoding='utf-8')
        for i in range(len(semi_code)):
            # 记录在物料12列表里但是不在物料库存里的数据
            if semi_code[i] not in product_inventory['物料编码'].values:
                self.dict3['半成品库存信息'].append(semi_code[i])
                self.dict4['半成品库存信息'].append(semi_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断这两个是否为空
                key_info = product_inventory[product_inventory['物料编码'] == semi_code[i]][
                    ['最小批量', '阶梯数量']].isnull().values.any()
                if key_info:
                    self.dict3['半成品库存信息'].append(
                        product_inventory[product_inventory['物料编码'] == semi_code[i]].values.tolist()[0])
                    self.dict4['半成品库存信息'].append(
                        product_inventory[product_inventory['物料编码'] == semi_code[i]].values.tolist()[0])
        if len(self.dict3['半成品库存信息']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_12:', GLOBAL_VARIABLE)

    def check_MOQ(self, semi_code, material_code):
        global GLOBAL_VARIABLE
        # 读取MOQ信息
        MOQ = pd.read_csv('data/origin_data/MOQ及采购周期.csv', encoding='utf-8')
        for i in range(len(semi_code)):
            # 记录在MOQ列表里但是不在物料库存里的数据
            if semi_code[i] not in MOQ['物料编码'].values:
                self.dict3['MOQ'].append(semi_code[i])
                self.dict4['MOQ'].append(material_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断是否为空
                key_info = MOQ[MOQ['物料编码'] == semi_code[i]].isnull().values.any()
                if key_info:
                    self.dict3['MOQ'].append(MOQ[MOQ['物料编码'] == semi_code[i]].values.tolist()[0])
                    self.dict4['MOQ'].append(MOQ[MOQ['物料编码'] == semi_code[i]].values.tolist()[0])
        for i in range(len(material_code)):
            # 记录在物料13列表里但是不在物料库存里的数据
            if material_code[i] not in MOQ['物料编码'].values:
                self.dict3['MOQ'].append(material_code[i])
                self.dict4['MOQ'].append(material_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断是否为空
                key_info = MOQ[MOQ['物料编码'] == material_code[i]].isnull().values.any()
                if key_info:
                    self.dict3['MOQ'].append(MOQ[MOQ['物料编码'] == material_code[i]].values.tolist()[0])
                    self.dict4['MOQ'].append(MOQ[MOQ['物料编码'] == material_code[i]].values.tolist()[0])
        if len(self.dict3['MOQ']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_MOQ:', GLOBAL_VARIABLE)

    def check_people_type(self, product_code):
        global GLOBAL_VARIABLE
        people_type = pd.read_csv('data/origin_data/people_type.csv', encoding='utf-8')
        for i in range(len(product_code)):
            # 记录在物料13列表里但是不在物料库存里的数据
            if product_code[i] not in people_type['物料编码'].values:
                self.dict3['物料人数数据'].append(product_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断是否为空
                key_info = people_type[people_type['物料编码'] == product_code[i]].isnull().values.any()
                if key_info:
                    self.dict3['物料人数数据'].append(people_type[people_type['物料编码'] == product_code[i]].values.tolist()[0])
        if len(self.dict3['物料人数数据']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_people_type:', GLOBAL_VARIABLE)

    def check_working_calendar(self):
        global GLOBAL_VARIABLE
        working_calendar = pd.read_csv('data/origin_data/Working_Calendar.csv')
        working_calendar['date'] = pd.to_datetime(working_calendar['date'])
        working_calendar.set_index('date', inplace=True)

        missing_dates = pd.date_range(start=working_calendar.index.min(), end=working_calendar.index.max()).difference(working_calendar.index)
        if len(missing_dates) != 0:
            for i in missing_dates:
                self.dict3['工作日历'].append(i.strftime('%Y-%m-%d'))
                self.dict4['工作日历'].append(i.strftime('%Y-%m-%d'))
        if len(self.dict3['工作日历']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_working_calendar:', GLOBAL_VARIABLE)

    def check_product_capacity(self, product_code):
        global GLOBAL_VARIABLE
        product_capacity = pd.read_csv('data/origin_data/product_capacity.csv', encoding='utf-8')
        for i in range(len(product_code)):
            # 记录在物料13列表里但是不在物料库存里的数据
            if product_code[i] not in product_capacity['code'].values:
                self.dict3['物料产能数据'].append(product_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断这两个是否为空
                key_info = product_capacity[product_capacity['code'] == product_code[i]].isnull().values.any()
                if key_info:
                    self.dict3['物料产能数据'].append(
                        product_capacity[product_capacity['code'] == product_code[i]].values.tolist()[0])
        if len(self.dict3['物料产能数据']) > 1:
            GLOBAL_VARIABLE = False
        # print('check_product_capacity:', GLOBAL_VARIABLE)

    # 考虑工序的完整性检查
    # 物料工序和产能表
    def check_product_capacity_process(self):
        global GLOBAL_VARIABLE
        product_capacity_process = pd.read_csv('data/origin_data/product_capacity_process.csv', encoding='utf-8')[
            ['所用工序', '产品族', '工序名称', '标准工时', '人数MAX', '人数MIN']]
        for index, row in product_capacity_process.iterrows():
            if row.isnull().any():
                self.dict4['物料工序和产能表-考虑工序'].append(row.tolist())
        if len(self.dict4['物料工序和产能表-考虑工序']) > 1:
            GLOBAL_VARIABLE = False

    # 工序搭配表
    def check_route_information(self):
        global GLOBAL_VARIABLE
        product_capacity_process = pd.read_csv('data/origin_data/route_information_origin.csv', encoding='utf-8')[
            ['所用工序', '产品族', '前置工序', '后置工序', '切换时间(s)']]
        for index, row in product_capacity_process.iterrows():
            if row.isnull().any():
                self.dict4['工序搭配'].append(row.tolist())
        if len(self.dict4['工序搭配']) > 1:
            GLOBAL_VARIABLE = False

    # 工序搭配中间表
    def check_route_information_mid(self, product_code):
        global GLOBAL_VARIABLE
        product_capacity_process = pd.read_csv('data/origin_data/route_information.csv', encoding='utf-8')[
            ['物料编码', '物料名称', '前置工序', '后缀工序']]
        for i in range(len(product_code)):
            # 记录不存在于数据的物料
            if product_code[i] not in product_capacity_process['物料编码'].values:
                self.dict4['工序搭配中间表'].append(product_code[i])
            # 如果存在，则要判断缺失值
            else:
                # 判断是否为空
                key_info = product_capacity_process[
                    product_capacity_process['物料编码'] == product_code[i]].isnull().values.any()
                if key_info:
                    self.dict4['工序搭配中间表'].append(
                        product_capacity_process[product_capacity_process['物料编码'] == product_code[i]].values.tolist()[0])
        if len(self.dict4['工序搭配中间表']) > 1:
            GLOBAL_VARIABLE = False

    # 机器表
    def check_true_machine(self):
        global GLOBAL_VARIABLE
        true_machine = pd.read_csv('data/origin_data/true_machine.csv', encoding='utf-8')[
            ['产品族', '工序', '机器名称', '使用车间']]
        for index, row in true_machine.iterrows():
            if row.isnull().any():
                self.dict4['机器表'].append(row.tolist())
        if len(self.dict4['机器表']) > 1:
            GLOBAL_VARIABLE = False

    # 最终设置一个判断全局变量是否为False的函数，如果是就停掉程序
    def determine(self):
        global GLOBAL_VARIABLE
        if not GLOBAL_VARIABLE:
            # print("输入数据存在错误，请查看不完整数据统计.xlsx进行修改！")
            print(0)
            sys.stdout.flush()
            # self.print_excel()
            # sys.exit()
        else:
            # print("输入数据没问题，程序继续运行！")
            print(1)
            sys.stdout.flush()

    # 将检查出的信息存到excel里
    def print_excel(self):
        # 创建一个Excel写入器
        writer = pd.ExcelWriter('data/output_data/不完整数据统计.xlsx', engine='xlsxwriter')
        # 考虑工序存dict4
        if config.Consider_the_process:
            # 将字典中的数据写入Excel工作表
            for key, value_list in self.dict4.items():
                # 检查数据类型并将字符串转换为列表
                value_list = [item if isinstance(item, list) else [item] for item in value_list]
                # 创建一个DataFrame，将列表中的元素写入行
                df = pd.DataFrame(value_list)
                # 将DataFrame写入Excel工作表
                df.to_excel(writer, sheet_name=key, index=False, header=False)
        # 不考虑工序存dict3
        elif not config.Consider_the_process:
            # 将字典中的数据写入Excel工作表
            for key, value_list in self.dict3.items():
                # 检查数据类型并将字符串转换为列表
                value_list = [item if isinstance(item, list) else [item] for item in value_list]
                # 创建一个DataFrame，将列表中的元素写入行
                df = pd.DataFrame(value_list)
                # 将DataFrame写入Excel工作表
                df.to_excel(writer, sheet_name=key, index=False, header=False)

        # 保存Excel文件
        writer.save()

    # 返回dict3的函数
    def get_dict3(self):
        return self.dict3

    # 返回dict4的函数
    def get_dict4(self):
        return self.dict4


