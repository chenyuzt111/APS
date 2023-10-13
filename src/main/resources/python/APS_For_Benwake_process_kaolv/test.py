from src import config
from src.utils.concreteplan import ConcretePlan
from src.utils.drawgantt import DrawGantt
from src.utils.product import ProductDict
from src.utils.material import TotalRowMaterial
from src.utils.workingcalendar import WorkingCalendar
from src.utils.machine import MachinesSet, AllMachineSet, TestMachineSet
from src.utils.productorder import ProductionOrderPlan
from src.utils.saleorder import SaleOrderPlan
from src.utils.schedulingplan import SchedulingPlan
from src.utils.historyorder import HistoryOrderPlan
from src.utils.data_preprocessing import Data_preprocess
import sys
import time
import warnings

warnings.filterwarnings("ignore")

if __name__ == '__main__':
    start_time_pro = time.time()
    '''
    优先进行数据预处理和数据检查
    '''
    # 新增的物料表，用于处理采购订单和即时库存
    wuliao_address = 'data/origin_data/APS-主数据-物料清单列表.xlsx'
    origin_sale_address = 'data/origin_data/origin_sale.csv'
    origin_history_address = 'data/origin_data/origin_history.csv'
    machine_address = 'data/origin_data/true_machine.csv'
    product_capacity_process_address = 'data/origin_data/product_capacity_process.csv'
    origin_route_information_address = 'data/origin_data/origin_route_information.csv'
    # 先处理物料清单列表和采购订单，即时库存的表
    data_pre = Data_preprocess()
    data_pre.preprocess1(wuliao_address)
    # 当该方法需要return时，再用下边这句，目前是把不合规的都打印出来了，应该不用单独看字典
    # Corr_dict1, Corr_dict2 = data_pre.preprocess1(wuliao_address)
    # print(Corr_dict)  # 如果出现问题那么久打印出来看看，并且停掉程序
    # 开始处理需求和历史数据
    data_pre.preprocess2(origin_sale_address, origin_history_address)
    # 现在开始检查
    product_code, semi_code, material_code = data_pre.check_sale_plan()
    # 分别检查13/DZ 12 10
    data_pre.check_13andDZ(product_code)
    data_pre.check_12(semi_code)
    data_pre.check_MOQ(semi_code, material_code)
    data_pre.check_working_calendar()
    # 考虑不考虑工序检查的数据不一样
    if not config.Consider_the_process:
        data_pre.check_people_type(product_code)
        data_pre.check_product_capacity(product_code)
        # key_dict = data_pre.get_dict3()
        # for key in key_dict.keys():
        #     print(key, ':', key_dict[key])
    elif config.Consider_the_process:
        data_pre.check_product_capacity_process()
        data_pre.check_route_information()
        data_pre.check_route_information_mid(product_code)
        data_pre.check_true_machine()
        data_pre.preprocess3(product_capacity_process_address, machine_address)
        data_pre.preprocess4(origin_route_information_address)
        # key_dict = data_pre.get_dict4()
        # for key in key_dict.keys():
        #     print(key, ':', key_dict[key])

    preprocess_time = time.time()
    run_time = round(preprocess_time - start_time_pro)
    # 计算时分秒
    hour = run_time // 3600
    minute = (run_time - 3600 * hour) // 60
    second = run_time - 3600 * hour - 60 * minute

    #print(f'数据预处理及完整性检查耗时：{hour}小时{minute}分钟{second}秒')
    data_pre.determine()
    # symbol = input("请输入回车继续运行！输入1表示停止")
    # if symbol == '1':
    #     sys.exit()
    '''
    地址导入
    '''
    # 若代码出问题 可能是csv的编码格式有问题
    # 若从云文档下载的文件，encoding为utf-8
    # 若execl中保存的文件，encoding为GB2312，记事本可以查看编码类型
    try:
        people_type_address = None
        # 获取数据
        if config.Consider_the_process:  # 考虑工序
            product_address = 'data/origin_data/process_all_information.csv'
            machine_address = 'data/origin_data/true_machine.csv'
        else:
            product_address = 'data/origin_data/product_capacity.csv'  # 输入物料产能数据-不考虑工序
            people_type_address = 'data/origin_data/people_type.csv'  # 物料人数数据
        inventory_address = 'data/origin_data/product_inventory.csv'  # 输入-物料库存信息
        date_address = 'data/origin_data/Working_Calendar.csv'  # 输入-工作日历
        sale_address = 'data/origin_data/sale_plan.csv'  # 开始加工时间计算
        history_address = 'data/origin_data/history_information.csv'  # 未完工数据整理
        route_information_address = 'data/origin_data/route_information.csv'

        '''
        获取全局时间对象
        '''
        # 展示对应关系
        # 时间测试
        start_time = config.start_time
        scheduled_days_num = config.scheduled_days_num
        working_calendar = WorkingCalendar(start_time, scheduled_days_num, date_address)  # 得到工作日历情况

        '''
        获得资源，产品、物料、人员、机器
        '''
        # 产品测试
        Product_control = ProductDict(product_address, config.Consider_the_process, people_type_address)  # 获取产能信息

        Product_control.get_inventory(inventory_address)  # 获取库存信息

        Product_control.get_route_information(route_information_address)  # 获取工艺路线信息

        # 物料信息
        if config.consider_the_material:
            totalRowMaterial = TotalRowMaterial()
            totalRowMaterial.get_Correspondence_all_finished_product('data/origin_data/工序BOM.xlsx')
            totalRowMaterial.get_Correspondence_all_Semi_finished_products('data/origin_data/物料清单.xlsx')
            totalRowMaterial.add_consider_the_time(working_calendar)  # 最大考虑时间
            totalRowMaterial.add_inventory_time(working_calendar)  # 增加库存随时间变化的列表

        # 人数机器集合
        machines_set = MachinesSet(working_calendar.max_people_num)  # 得到人数机器组合情况

        # 测试机器组合
        if config.Consider_the_process:
            test_machine_list = AllMachineSet(machine_address)
        else:
            product_type = Product_control.get_type_list()
            test_machine_list = TestMachineSet(product_type)

        '''
        获得任务 包括 销售订单 历史遗留 生产计划 排程计划
        '''
        # 销售订单获取
        sale_order_plan = SaleOrderPlan(sale_address)
        sale_order_plan.Overlay_of_same_product_and_date()
        # # 历史订单
        if config.consider_history:
            history_order_plan = HistoryOrderPlan(history_address)
        else:
            history_order_plan = None
        # 生产计划的生成
        Production_Order_Plan = ProductionOrderPlan(sale_order_plan.OrderPlan, product_address, inventory_address,
                                                    working_calendar, history_order_plan)
        # 求生产计划的开始时间
        Production_Order_Plan.get_start_date(product_address, people_type_address, working_calendar)
        # 得到规定时间内的订单
        scheduling_workload = config.scheduling_workload  # 规定对应时间段内的生产计划
        date = working_calendar.get_date_after_days(scheduling_workload, start_time)
        Production_Order_Plan.get_specified_order(date)  # 根据时间存入订单类型

        # 打印

        Production_Order_Plan.print_csv(Product_control, history_order_plan)  # 将销售订单引起的生产订单进行打印

        # 排产计划
        scheduling_plan = SchedulingPlan(history_order_plan,
                                         Production_Order_Plan.production_order_Plan_part)  # 将历史和销售订单进行组合得到排程计划

        '''
        开始排程
        '''
        scheduling_plan.get_machine_running_time(machines_set, Product_control,
                                                 working_calendar)  # 对于每个订单都有对于的物料 ，每个物料都有对于的人数以及加工时间
        if config.number_cycles != 1:
            scheduling_plan.queue = scheduling_plan.search_the_best(Product_control, working_calendar, machines_set,
                                                                    test_machine_list.machine_list,
                                                                    totalRowMaterial)  # 寻找最好的解
        else:
            scheduling_plan.initialize_queue()

        if config.consider_the_material:
            Product_control.get_orrespondence_for_singer(totalRowMaterial)
        #print('------------最终结果------------')
        if config.number_cycles != 1:
            scheduling_plan.get_machine_running_time(machines_set, Product_control,
                                                     working_calendar)  # 对于每个订单都有对于的物料 ，每个物料都有对于的人数以及加工时间
        scheduling_plan.decode(Product_control, working_calendar, machines_set, test_machine_list.machine_list,
                               LAST=True)  # 根据最好的解进行排程
        # PO完成率，整个项目完成率，所有天数的效率
        # print("PO完成率：", round(scheduling_plan.Completion_rate_PO, 4))
        # print("PR完成率：", round(scheduling_plan.Completion_rate_PR, 4))
        # print("总体完成率：", round(scheduling_plan.Completion_rate_all, 4))
        # print("总体平均效率：", round(scheduling_plan.worker_utilization, 4))

        '''
        对结果进行展示，包括 csv 图
        '''

        concrete_plan = ConcretePlan()  # 生成一个详细计划对象
        concrete_plan.get_days_plan(machines_set, working_calendar, scheduling_plan, Product_control)  # 得到每天的机器的工作计划，准备画图
        concrete_plan.get_weeks_plan(machines_set, working_calendar)  # 得到每周的机器的工作计划，准备画图
        concrete_plan.everyday_num_to_csv(scheduling_plan, working_calendar, Product_control)  # 将每天的结果存入csv
        concrete_plan.get_start_time_for_all_process(machines_set, working_calendar, scheduling_plan,
                                                     Product_control)  # 得到对应物料工序的开始时间
        # 上述还没有封装好，类似于函数

        #  将最后的计划导入csv中

        # 任务id,来源id,物料编码,物料名称，总数量，完成数量，实际完成时间，需入库时间，是否完成，优先级，所包含订单
        scheduling_plan.get_completion_to_csv(Product_control, working_calendar)

        # 得到库存原材料库存信息
        if config.consider_the_material:
            Product_control.print_Raw_material_inventory(working_calendar)

        # 得到成品的库存信息，包含销售订单以及生产计划
        Product_control.get_product_inventory(sale_order_plan, scheduling_plan.scheduling_list, working_calendar)
        print(521)
        sys.stdout.flush()
    except:
        print(520)
        sys.stdout.flush()
    end_time_pro = time.time()

    # 画图
    # draw_gantt = DrawGantt(scheduling_plan)  # 根据排程计划个数生成画图对象
    # draw_gantt.draw_chart(machines_set, scheduling_plan, working_calendar, Product_control.product_all)  # 绘制总人数
    # draw_gantt.draw_chart(test_machine_list, scheduling_plan, working_calendar, Product_control.product_all,
    #                       type='test')  # 绘制测试机器
    #
    # # 绘制每天
    # # for day in concrete_plan.days_people_combine_plan:
    # #     draw_gantt.draw_chart_for_day(day,concrete_plan.days_people_combine_plan[day],scheduling_plan,working_calendar,Product_control.product_all)
    # # 绘制每周 需要每周都要一个日期列表
    #
    # for week in concrete_plan.weeks_people_combine_plan:
    #     draw_gantt.draw_chart_for_week(week, concrete_plan.weeks_people_combine_plan[week], scheduling_plan,
    #                                    working_calendar, Product_control.product_all)

    end_time1 = time.time()

    run_time = round(end_time_pro - start_time_pro)
    # 计算时分秒
    hour = run_time // 3600
    minute = (run_time - 3600 * hour) // 60
    second = run_time - 3600 * hour - 60 * minute
    # 输出
    #print(f'不出图需要的时间：{hour}小时{minute}分钟{second}秒')
    run_time = round(end_time1 - start_time_pro)
    # 计算时分秒
    hour = run_time // 3600
    minute = (run_time - 3600 * hour) // 60
    second = run_time - 3600 * hour - 60 * minute
    #print(f'出图需要的时间：{hour}小时{minute}分钟{second}秒')