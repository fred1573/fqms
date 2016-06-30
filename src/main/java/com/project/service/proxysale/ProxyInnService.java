package com.project.service.proxysale;

import com.project.bean.proxysale.PriceDetailQuery;
import com.project.core.orm.Page;
import com.project.entity.account.User;
import com.project.entity.area.Area;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.proxysale.Channel;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyInnDelLog;
import com.project.entity.proxysale.ProxysaleChannel;
import com.project.web.proxysale.ProxyInnFormAdd;

import java.util.List;
import java.util.Map;

/**
 * 供销平台-客栈服务
 * Created by Administrator on 2015/6/9.
 */
public interface ProxyInnService {

    /**
     * 根据客栈名查询客栈代销记录
     *
     * @param page         page
     * @param innName      inn name
     * @param pricePattern price pattern
     * @param status       status
     * @return page
     */
    Page<ProxyInn> find(Page<ProxyInn> page, Integer areaId, String innName, Integer pricePattern, Integer status);

    /**
     * 添加审核数据
     */
    void add(ProxyInnFormAdd proxyInnAdd);

    /**
     * 根据区域查询客栈
     *
     * @param channelArea
     * @return list
     */
    List<Integer> findByArea(Area channelArea);

    /**
     * 卖价上架,上架规则：
     * 1.有合同
     * 2.有房型、房间价格
     * 3.未删除，未卖价上架
     * 4.已选卖价模式
     * * @param id
     */
    void salePriceOnShelf(Integer id);

    /**
     * 卖价下架，下架规则：
     * 1.未删除
     * 2.未卖价下架
     *
     * @param id
     */
    void salePriceOffShelf(Integer id);

    /**
     * 底价上架,上架规则：
     * 1.有合同
     * 2.有房型、房间价格
     * 3.未删除，未底价上架
     * 4.已选底价模式
     *
     * @param id
     */
    void basePriceOnShelf(Integer id);

    /**
     * 底架下架，下架规则：
     * 1.未删除
     * 2.未底价下架
     *
     * @param id
     */
    void basePriceOffShelf(Integer id);

    ProxyInn get(Integer id);

    /**
     * 客栈老板修改客栈信息，不记录修改人
     *
     * @param proxyInn
     */
    void modify(ProxyInn proxyInn);

    /**
     * 运营修改客栈信息，记录修改人
     *
     * @param proxyInn
     */
    void modifyBackend(ProxyInn proxyInn, List<ProxysaleChannel> newProxysaleChannel);

    /**
     * 根据客栈ID查找代销客栈，已删除记录不在查询范围内
     *
     * @param innId
     * @return proxyInn
     */
    ProxyInn findByInnId(Integer innId);

    List<Integer> findAll();

    List<ProxyInn> findByStatus(Integer status);


    /**
     * 上架， 上架过了就不上了，卖价底价符合条件的都要上
     *
     * @param proxyInn
     */
    void onshelf(ProxyInn proxyInn);

    void updateOTALink(Integer proxyInnId, String otaLink);

    /**
     * 根据区域名称查询区域
     *
     * @param areaName
     * @return
     */
    Area getAreaByAreaName(String areaName);

    /**
     * 根据客栈ID获取渠道关联信息
     */
    List<Channel> getRelationChannels(Integer id);

    Map<Integer, String> getOTALinks(String innsJson);

    /**
     * 获取 客栈的汇总信息
     *
     * @param areaId 区域ID
     * @return 汇总对象
     */
    Integer[] getProxyInnSummary(Integer areaId);

    /**
     * 获取今天汇总的信息
     *
     * @param areaId 区域
     * @param inn    客栈
     * @return
     * @throws Exception
     */
    Object[] getUpAndDownSummary(Integer areaId, Integer inn, Boolean isToday);

    Object getSignManager(Integer innId);

    /**
     * 根据代销客栈ID、抽佣比例查询该客栈和渠道的关联关系
     *
     * @param proxyInnId   代销客栈ID
     * @param innId        客栈ID
     * @param pricePattern 普通代销模式的总抽佣比例
     * @return
     */
    Map<String, List<Map<String, Object>>> getProxyInnChannel(Integer proxyInnId, Float pricePattern, Integer innId);

    List<Map<String, Object>> getChannelByArea(Integer areaId, Short pricePattern);

    User getCurrentUser();

    /**
     * 下架代销客栈
     *
     * @param proxyInnId   代销客栈ID
     * @param pricePattern 价格策略
     * @param reason       下架原因
     */
    void offShelfProxyInn(Integer proxyInnId, Short pricePattern, String reason);

    /**
     * 设置渠道、上架精品代销、上架普通代销
     *
     * @param jsonData 参数的json字符串
     */
    void modifyProxyInnChannel(String jsonData);

    /**
     * 区域批量上架客栈指定价格策略的
     *
     * @param jsonData 参数的json字符串
     * @param isOnline true为上线，false位下线
     */
    void batchOnShelfByArea(String jsonData, boolean isOnline);

    /**
     * 根据代销客栈ID查询总抽佣比例
     *
     * @param proxyInnId   代销客栈ID
     * @param pricePattern 价格策略
     * @return 总抽佣比例
     */
    Float getProxyInnPricePatternByProxyInn(Integer proxyInnId, Short pricePattern, Integer innId);

    /**
     * 合同审核通过时，保存客栈与渠道关联关系，保存普通代销的总抽佣比例
     *
     * @param proxyInn          代销客栈对象
     * @param pricePattern      普通代销的总抽佣比例
     * @param saleChannelIdList 普通代销的客栈渠道关联关系
     */
    void initProxySaleInnChannel(ProxyInn proxyInn, Float pricePattern, List<Integer> saleChannelIdList);

    /**
     * 删除代销客栈，调oms&crm接口清理数据
     *
     * @param id 代销客栈ID
     */
    void delete(Integer id, String reason);

    /**
     * 保存代销客栈移除记录
     *
     * @param proxyInn 代销客栈
     * @param reason   移除原因
     */
    void saveDelLog(ProxyInn proxyInn, String reason);

    /**
     * 查询移除列表
     *
     * @param page
     * @return
     */
    Page<ProxyInnDelLog> findDelList(Page<ProxyInnDelLog> page);

    /**
     * 修改客栈的分佣比例
     *
     * @param id         客栈ID
     * @param percentage 新的比例
     */
    void updateInnerPercentage(Integer id, Float percentage) throws Exception;

    /**
     * 根据分页对象、客栈名称、开始日期、结束日期和操作类型分页查询代销操作记录
     *
     * @param page        分页对相同
     * @param innName     客栈名称（支持模糊查询）
     * @param startDate   搜索的开始时间
     * @param endDate     搜索的结束时间
     * @param operateType 操作类型
     * @return 符合搜索条件的查询结果
     */
    Page<FinanceOperationLog> findProxySaleOperationLogList(Page<FinanceOperationLog> page, String innName, String startDate, String endDate, String operateType);

    /**
     * 查询已加入代销平台且上架普通代销，并且已上线了某一分销商的所有客栈
     */
    Page findPriceUpdateInnList(Page page, String innName);

    /**
     * 从OMS获取调价后的价格及原价
     */
    String findPriceDetailByChannel(PriceDetailQuery detailQuery);

    /**
     * 执行加减价，调OMS接口
     */
    void updatePrice(Integer accountId, String otaList, String roomList, String innName);


    /**
     * 获取客栈开通的渠道(不区分底价卖家)
     *
     * @param innId
     * @return
     */

    List<Map<String, Object>> getChannelByInnId(Integer innId);

    /**
     * 封装渠道ID，渠道名称
     *
     * @param innId
     * @return
     */
    List<Map<String, Object>> packChannel(Integer innId);
}
