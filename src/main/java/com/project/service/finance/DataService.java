package com.project.service.finance;

import com.project.bean.finance.BarAndLineData;
import com.project.bean.finance.DataForm;
import com.project.bean.finance.PieData;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * 订单数据统计分析的业务逻辑接口
 * Created by sam on 2015/12/24.
 */
@Component
@Transactional
public interface DataService {
    /**
     * @param dataForm 过滤条件
     * @return
     */
    Map<String, Object> getStatisticsSaleData(DataForm dataForm);

    /**
     * @param dataForm 过滤条件
     * @return
     */
    BarAndLineData getStatisticsBarData(DataForm dataForm);

    /**
     * @param dataForm 过滤条件
     * @return
     */
    Map<String, Object> getStatisticsTableData(DataForm dataForm);

    /**
     * @param dataForm
     * @return
     */
    PieData getPieData(DataForm dataForm);

    /**
     *
     * @param dataForm
     * @return
     */
    PieData getPieByTime(DataForm dataForm);

    /**
     * 查询全部订单总数量
     * @param dataForm
     * @return
     */
    int getAllOrderAmount(DataForm dataForm);

    /**
     * 修复订单中的扩展数据
     */
    void repairData();
}
