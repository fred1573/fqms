package com.project.service.finance;

import com.project.bean.excel.ExcelSheetBean;
import com.project.bean.finance.AjaxChannelReconciliation;
import com.project.core.orm.Page;
import com.project.entity.finance.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 对账业务逻辑处理类
 * Created by 番茄桑 on 2015/8/31.
 */
@Component
@Transactional
public interface FinanceOrderService {

    String AFTER = "2";
    String CURRENT = "1";

    /**
     * 创建订单对象/更新订单对象
     *
     * @param financeParentOrder
     */
    void createFinanceOrder(FinanceParentOrder financeParentOrder);

    /**
     * 更新订单
     *
     * @param financeParentOrder
     */
    void updateFinanceOrder(FinanceParentOrder financeParentOrder);

    /**
     * 修复订单，只更改订单的账期，其他属性不该
     *
     * @param financeParentOrder
     */
    void repairFinanceOrder(FinanceParentOrder financeParentOrder);

    /**
     * 用于导出EXCEL，指定结算月份全部渠道的结算详情
     *
     * @param settlementTime
     * @return
     */
    List<FinanceChannelSettlement> getFinanceParentOrderByChannel(String settlementTime);

    /**
     * 根据查询条件，分页过滤查询渠道结算
     *
     * @param page           分页对象
     * @param settlementTime 结算月份
     * @param channelName    渠道名称（支持模糊查询）
     * @param auditStatus    核单状态
     * @param isArrival      是否收到款项
     * @return 渠道结算对象集合
     */
    Page<FinanceChannelSettlement> getFinanceParentOrderByChannel(Page<FinanceChannelSettlement> page, String settlementTime, String channelName, String auditStatus, Boolean isArrival, boolean isPage);

    /**
     * 根据结算时间和是否收到款项，统计渠道结算
     *
     * @param settlementTime
     * @param isArrival
     * @return
     */
    Map<String, Object> getFinanceChannelSettlementCount(String settlementTime, Boolean isArrival);

    /**
     * 根据查询条件，分页过滤查询渠道结算订单详情对象集合
     *
     * @param page           分页对象
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param channelOrderNo 渠道订单编号
     * @param auditStatus    核单状态
     * @param priceStrategy  订单模式
     * @return
     */
    Page<FinanceParentOrder> findChannelIncomeOrderList(Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Boolean isArrival, Short priceStrategy, boolean isPage);

    /**
     * 统计查询渠道订单总数和总金额
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算时间
     * @param channelOrderNo 渠道订单号
     * @param auditStatus    核单状态
     * @param priceStrategy  价格模式
     * @return
     */
    Map<String, Object> getChannelOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Short priceStrategy);

    /**
     * 根据查询条件分页查询客栈结算对象
     *
     * @param page             分页对象
     * @param innName          客栈名称（支持模糊查询）
     * @param settlementTime   结算月份
     * @param confirmStatus    客栈确认状态
     * @param settlementStatus 结算状态
     * @param isMatch          账实是否相符
     * @return
     */
    Page<FinanceInnSettlement> findFinanceInnSettlementList(Page<FinanceInnSettlement> page, String innName, String settlementTime, String confirmStatus, String settlementStatus, Boolean isTagged, boolean isPage, Boolean isMatch, String status);

    /**
     * 根据结算时间和是否结算，统计客栈结算
     *
     * @param settlementTime
     * @param settlementStatus 结算状态
     * @return
     */
    Map<String, Object> getFinanceInnSettlementCount(String settlementTime, String settlementStatus, String status);

    /**
     * 统计客栈订单结算
     *
     * @param innId          客栈ID
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param channelOrderNo 订单号（模糊查询）
     * @param auditStatus    核单状态
     * @param priceStrategy  价格策略
     * @param isArrival      是否收到款项
     * @return
     */
    Map<String, Object> getInnOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String auditStatus, Short priceStrategy, Boolean isArrival);

    /**
     * 修改isArrival为渠道商款项已经收到
     * 并将该渠道该结算月份的所有订单的款项收到状态改为true
     *
     * @param id             渠道结算记录的ID
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     */
    void updateFinanceChannelSettlementStatus(Integer id, Integer channelId, String settlementTime);

    /**
     * 根据ID修改指定客栈指定结算月份的结算状态，isSettlement
     *
     * @param id               客栈结算对象
     * @param settlementStatus 修改确认状态
     */
    void updateInnSettlementStatus(Integer id, String settlementStatus,String settlementTime,Integer innId);

    /**
     * 根据ID和是否标注，修改客栈的标注状态
     *
     * @param id       主键ID
     * @param isTagged 是否标注
     */
    void updateFinanceInnSettlementTag(Integer id, Boolean isTagged);

    /**
     * 为PMS提供的接口
     * 根据客栈ID和结算月份，修改客栈确认状态为已确认
     *
     * @param innId
     * @param settlementTime
     */
    void updateFinanceInnSettlementStatus(Integer innId, String settlementTime);

    /**
     * 根据结算月份构建渠道结算对象
     *
     * @param settlementTime
     * @return
     */
    void createFinanceChannelSettlementList(String settlementTime);

    /**
     * 根据结算月份构建客栈结算对象
     *
     * @param settlementTime
     */
    void createFinanceInnSettlementList(String settlementTime);

    /**
     * 导出渠道的订单Excel
     *
     * @param response             http响应对象
     * @param exportSettlementTime 结算周期
     * @param exportChannelId      渠道ID（批量导出时值为空）
     */
    void exportChannelOrder(HttpServletResponse response, String exportSettlementTime, Short exportChannelId);

    /**
     * 异步导出进账单总表和账单明细
     *
     * @param request
     * @param settlementTime  账期
     * @param exportChannelId 分销商ID
     * @throws Exception
     */
    void asynchronousExportChannelOrder(HttpServletRequest request, String settlementTime, Short exportChannelId) throws Exception;

    /**
     * 根据结算时间和客栈ID构建订单Excel
     *
     * @param response       响应对象
     * @param settlementTime 结算月份
     * @param innId          客栈ID
     * @return
     */
    void exportInnOrder(HttpServletResponse response, Integer innId, String settlementTime) throws Exception;

    /**
     * 按照账期批量导出客栈结算单到webapp的download目录下
     *
     * @param request
     * @param settlementTime
     * @param status(结算客栈状态)
     * @throws Exception
     */
    void batchExportInnOrder(HttpServletRequest request, String settlementTime, String status) throws Exception;

    /**
     * 根据结算时间和渠道ID构建订单Excel
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @return
     */
    List<ExcelSheetBean> buildChannelOrderData(Short channelId, String settlementTime);

    /**
     * 根据客栈ID查询该客栈的结算历史
     *
     * @param innId    客栈ID
     * @param pageSize 每页显示条数
     * @param pageNo   当前页码
     * @return 客栈的结算历史
     */
    Page<FinanceInnSettlement> getFinanceInnSettlementList(Integer innId, Integer pageSize, Integer pageNo);

    /**
     * 根据客栈ID和结算月份查询订单详情
     *
     * @param innId          客栈ID
     * @param settlementTime 结算月份
     * @param pageSize       每页显示条数
     * @param pageNo         当前页码
     * @param priceStrategy  价格策略
     * @param isPage         是否分页
     * @return
     */
    Page<FinanceParentOrder> findInnOrderList(Integer innId, String settlementTime, Integer pageSize, Integer pageNo, Short priceStrategy, Boolean isPage);

    /**
     * 接收对账Excel的文件流，为指定渠道的指定月份的订单进行核对
     *
     * @param file           文件流对象
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @return
     */
    AjaxChannelReconciliation channelReconciliation(MultipartFile file, Integer channelId, String settlementTime);

    /**
     * 根据客栈ID和appkey验证参数是否合法
     *
     * @param innId
     * @param appKey
     * @return
     */
    boolean checkInnId(Integer innId, String appKey);

    /**
     * 发送客栈结算账单
     *
     * @param settlementTime 结算月份
     */
    void sendInnBill(String settlementTime);

    /**
     * 获取openID
     */
    void synchronizationInnInfo(FinanceInnSettlementInfo financeInnSettlementInfo);

    /**
     * 获取全部结算客栈信息
     */
    List<FinanceInnSettlementInfo> getAllInnSettlementInfo();

    /**
     * 批量结算
     *
     * @param settlementTime
     */
    void batchSettlement(String settlementTime);

    /**
     * 根据查询条件分页查询操作记录
     *
     * @param page           分页对象
     * @param keyWord        搜索关键字（匹配客栈名称和渠道名称）
     * @param settlementTime 结算月份
     * @param startDate      操作开始时间
     * @param endDate        操作结束时间
     * @param operateType    操作类型
     * @return
     */
    Page<FinanceOperationLog> findFinanceOperationLogList(Page<FinanceOperationLog> page, String keyWord, String settlementTime, String startDate, String endDate, String operateType);

    /**
     * 根据渠道结算对象ID更新渠道结算的实收金额以及备注
     *
     * @param id           渠道结算对象ID
     * @param incomeAmount 实收金额
     * @param remarks      备注
     */
    void updateChannelSettlementIncome(Integer id, BigDecimal incomeAmount, String remarks);

    /**
     * 根据结算月份统计渠道的实收金额
     *
     * @param settlementTime
     * @return
     */
    Map<String, Object> findChannelSettlementIncomeAmount(String settlementTime);

    /**
     * 按账期抓取-封装更新订单
     *
     * @param financeParentOrder
     */
    void createFinanceOrderWithPeriod(FinanceParentOrder financeParentOrder);

    /**
     * 获取文件列表
     *
     * @param request
     * @return
     */
    File[] getFileList(HttpServletRequest request);

    /**
     * 根据文件名称删除文件
     *
     * @param request
     * @param fileName
     */
    void removeFile(HttpServletRequest request, String fileName);

    /**
     * 按客栈ID、渠道ID、账期生成账单
     *
     * @param settlementTime
     */
    void createFinanceInnChannelSettlementList(String settlementTime);

    /**
     * 按账期获取所有渠道
     *
     * @param settlementTime
     * @return
     */
    List<FinanceChannelSettlement> getFinanceChannelSettlementsWithSettlement(String settlementTime);

    /**
     * 查询特殊结算下的正常订单
     *
     * @param page
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    Page<FinanceParentOrder> findSpecialInnNormalOrder(Page<FinanceParentOrder> page, Integer innId, Integer channelId, String settlementTime, String channelOrderNo);

    /**
     * 统计特殊结算下的正常订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    List<Map<String, Object>> selectSpecialInnNormalOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo);

    /**
     * 查询特殊结算下的订单
     *
     * @param page
     * @param innId
     * @param channelId
     * @param settlementTime
     * @return
     */

    Page<FinanceSpecialOrder> findFinanceSpecialOrder(Page<FinanceSpecialOrder> page, Integer innId, Integer channelId, String settlementTime, String orderNo, String status, String contactsStatus);

    /**
     * 统计特殊结算下的赔付订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    List<Map<String, Object>> selectSpecialInnRecoveryOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo);

    /**
     * 统计特殊结算下的退款订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    List<Map<String, Object>> selectSpecialInnRefundOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo, String contactsStatus);

    /**
     * 统计特殊结算下的补款订单
     *
     * @param innId
     * @param channelId
     * @param settlementTime
     * @param channelOrderNo
     * @return
     */
    List<Map<String, Object>> selectSpecialInnReplenishmentOrderCount(Integer innId, Integer channelId, String settlementTime, String channelOrderNo);

    /**
     * 统计未结算客栈数据
     *
     * @param settlementTime
     * @param status
     * @return
     */
    List<Map<String, Object>> selectUnbalanceFinanceInnSettlementCount(String settlementTime, String status);

    /**
     * 按id和账期查询客栈信息
     *
     * @param id
     * @param settlementTime
     * @return
     */
    FinanceInnSettlement findFinanceInnSettlement(Integer id, String settlementTime);

    /**
     * 根据挂账类型查询挂账客栈
     *
     * @param settlementTime
     * @param page
     * @param arrearsStatus
     * @return
     */
    Page<FinanceInnSettlement> findArrearFinanceInnSettlement(String settlementTime, Page<FinanceInnSettlement> page, String arrearsStatus, String innName);

    /**
     * 根据挂账类型统计挂账客栈
     *
     * @param settlementTime
     * @param arrearsStatus
     * @return
     */
    Map<String, Object> selectArrearFinanceInnSettlement(String settlementTime, String arrearsStatus);

    /**
     * 结算错误消息推送
     *
     * @param innId
     * @param settlementTime
     */
    void sendErrorMessage(Integer innId, String settlementTime);

    /**
     * 清理正常订单可能存在的特殊订单
     *
     * @param settlementTime
     */
    void cleanSpecialOrder(String settlementTime);

    /**
     * 查询累计挂账客栈
     *
     * @param settlementTime
     * @param page
     * @param arrearsStatus
     * @param innName
     * @return
     */
    Page<FinanceInnSettlement> findTotalArrearFinanceInnSettlement(String settlementTime, Page<FinanceInnSettlement> page, String arrearsStatus, String innName);
    /**
     * 统计累计挂账客栈
     *
     * @param settlementTime
     * @param arrearsStatus
     * @return
     */
    Map<String, Object> selectTotalArrearFinanceInnSettlement(String settlementTime, String arrearsStatus);
}
