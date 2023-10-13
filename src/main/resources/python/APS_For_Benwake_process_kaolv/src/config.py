# 排程设置

hours_in_day = 8.5  # 小时 每人每天工作时间，单位h
day_in_week = 6  # 天 每周工作的天数
unit_time = 0.05  # 小时 排程的单位时间间隔，单位h
# 0代表从这一天的9点第0个单位，8.5/0.05 大约200个单位，每个单位代表0.05小时，如果是200，可能到晚上7 8点了
# 一天时间安排
start_time_day = '9:00'  # 一天的开始时间
noon_rest_time = '12:00/13:00'  # 中午休息时间
afternoon_rest_time = '17:30/18:00'  # 晚上休息时间
unit_time_day = 30  # 分钟 半个小时为间隔跳转(min)
rest_hours = 1.5  # 小时 每天的休息时长(h)
transfer_rest_time = 5  # min 任务转移需要时间

# 人数组装设置
max_people_in_machine = 1  # 个 单个机器的最大人数
# 寻优设置
number_cycles = 1  # 循环次数

# 排程开始时间
start_time = '2023-10-08'  # 排程的开始时间

scheduled_days_num = 240  # 天 排程的工作日

# 销售订单转化生产订单
scheduling_workload = 240  # 天 规定对应时间段内的生产计划

# 考虑工序
Consider_the_process = False

# 是否并行生产
produce_in_parallel = True

# 是否考虑物料
consider_the_material = True

# 考虑历史遗留
consider_history = True

# 设置并行生产的批量，10个10个做还是100个做还是1个做完就可以并行生产
bach_size = 10  # 个数

# po优先级提前几天生产
in_advance_po = 7

# 采购订单是否增加天数,目前作为5
buy_delay_days = 5

# 是否拆分PO订单
split_po_orders = True

# YG最早提前天数
yg_delta = -180
