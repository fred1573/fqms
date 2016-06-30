package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.proxysale.PriceDetailQuery;
import com.project.bean.proxysale.SyncChannel;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.area.AreaDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.*;
import com.project.entity.account.User;
import com.project.entity.area.Area;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.ota.OtaInfo;
import com.project.entity.proxysale.*;
import com.project.service.account.AccountService;
import com.project.service.ota.OtaInfoService;
import com.project.utils.CollectionsUtil;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.MD5;
import com.project.web.proxysale.ProxyInnFormAdd;
import com.project.web.proxysale.SignManagerResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.project.entity.proxysale.PriceStrategy.*;

@Service("proxyInnService")
@Transactional(rollbackFor = Exception.class)
public class ProxyInnServiceImpl implements ProxyInnService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInnServiceImpl.class);
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Autowired
    private ProxyInnDao proxyInnDao;
    @Autowired
    private ChannelInnRelation channelInnRelation;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SyncChannel syncChannel;
    @Autowired
    private ProxyInnBean proxyInnBean;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private ChannelInnDao channelInnDao;
    @Autowired
    private PricePatternDao pricePatternDao;
    @Autowired
    private PriceStrategyDao priceStrategyDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ProxyInnOnoffService proxyInnOnoffService;
    @Autowired
    private ProxysaleChannelService proxysaleChannelService;
    @Autowired
    private OtaInfoService otaInfoService;
    @Autowired
    private ProxysaleChannelDao proxysaleChannelDao;
    @Autowired
    private FinanceOperationLogDao financeOperationLogDao;
    @Autowired
    private OnOffRoomService onOffRoomService;
    @Autowired
    private ProxyAuditService proxyAuditService;
    @Autowired
    private ProxyAuditDao proxyAuditDao;
    @Autowired
    private HibernateUserDao hibernateUserDao;


    @Override
    public Page<ProxyInn> find(Page<ProxyInn> page, Integer areaId, String innName, Integer pricePattern, Integer status) {
        return proxyInnDao.find(page, areaId, innName, status);
    }

    @Override
    public void add(ProxyInnFormAdd proxyInnFormAdd) {
        ProxyInn proxyInn = proxyInnBean.parse(proxyInnFormAdd);
        Assert.assertNotNull(proxyInn);
        proxyInnDao.save(proxyInn);
    }

    @Override
    public List<Integer> findByArea(Area channelArea) {
        String hql = "select pi.id from ProxyInn pi where pi.area.id=? and pi.status!=0 and pi.valid=true";
        List<Integer> proxyInns = new ArrayList<>();
        if (channelArea.getLevel().equals(Area.LEVEL_PROVINCE)) {
            Set<Area> children = channelArea.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                for (Area child : children) {
                    proxyInns.addAll(proxyInnDao.createQuery(hql, child.getId()).list());
                }
            }
        } else {
            throw new RuntimeException("只能根据省份查询客栈");
        }
        return proxyInns;
    }


    @Override
    public void basePriceOffShelf(Integer id) {
        try {
            ProxyInn proxyInn = proxyInnDao.get(id);
            if (!proxyInn.isValid()) {
                LOGGER.info("不能对已删除的客栈作上架操作，proxyInnId=" + id);
                return;
            }
            if (!proxyInn.isBasePriceOnshelfed()) {
                LOGGER.info("不能对已下架的客栈作下架操作，proxyInnId=" + id);
                return;
            }
            if (proxyInn.isSalePriceOnshelfed()) {
                proxyInn.setStatus(ProxyInn.STATUS_BASE_OFFSHELF);
            } else {
                proxyInn.setStatus(ProxyInn.STATUS_OFFSHELF);
            }
            proxyInn.setBasePriceOnOffTime(new Date());
            proxyInn.setOnOffOperator(getCurrentUser());
            syncChannel.syncOnShelf(proxyInn, PricePattern.PATTERN_BASE_PRICE, false);
            //  proxyInn.setChannels(null);
            Set<ProxysaleChannel> pcs = proxyInn.getPcs();
            if (null != pcs && !pcs.isEmpty()) {
                for (ProxysaleChannel proxysaleChannel : pcs) {
                    if (proxysaleChannel.getStrategy() == 1) {
                        proxysaleChannel.setValid(false);
                        proxysaleChannelService.update(proxysaleChannel);
                    }
                }
            }
            proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_BASE_PRICE, getCurrentUser(), ProxyInnOnoff.OFF));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public User getCurrentUser() {
        String userName = SpringSecurityUtil.getCurrentUserName();
        return hibernateUserDao.findUserByUserCode(userName);
    }

    @Override
    public void basePriceOnShelf(Integer id) {
        try {
            ProxyInn proxyInn = proxyInnDao.get(id);
            // 执行关房操作
            if (!onOffRoomService.preOnShelf(proxyInn)) {
                throw new RuntimeException("关房失败");
            }
            if (!proxyInn.isValid()) {
                LOGGER.info("不能对已删除的客栈作上架操作，proxyInnId=" + id);
                return;
            }
            if (proxyInn.isBasePriceOnshelfed()) {
                LOGGER.info("不能对已底价上架的客栈作底价上架操作，proxyInnId=" + id);
                return;
            }
            if (!proxyInn.isBasePriceValid()) {
                throw new RuntimeException("底价模式无效,proxyInnId=" + id);
            }
            if (proxyInn.isSalePriceOnshelfed()) {
                proxyInn.setStatus(ProxyInn.STATUS_ONSHELF);
            } else {
                proxyInn.setStatus(ProxyInn.STATUS_SALE_OFFSHELF);
            }
            proxyInn.setBasePriceOnOffTime(new Date());
            proxyInn.setOnOffOperator(getCurrentUser());
            channelInnRelation.update(proxyInn);
            syncChannel.syncOnShelf(proxyInn, PricePattern.PATTERN_BASE_PRICE, true);
            proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_BASE_PRICE, getCurrentUser(), ProxyInnOnoff.ON));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void salePriceOffShelf(Integer id) {
        LOGGER.info("-----trace----salePriceOffShelf--------");
        try {
            ProxyInn proxyInn = proxyInnDao.get(id);
            //未删除
            if (!proxyInn.isValid()) {
                LOGGER.info("不能对已删除的客栈作上架操作，proxyInnId=" + id);
                return;
            }
            //未卖价下价
            if (!proxyInn.isSalePriceOnshelfed()) {
                LOGGER.info("不能对已下架的客栈作下架操作，proxyInnId=" + id);
                return;
            }
            if (proxyInn.isBasePriceOnshelfed()) {
                proxyInn.setStatus(ProxyInn.STATUS_SALE_OFFSHELF);
            } else {
                proxyInn.setStatus(ProxyInn.STATUS_OFFSHELF);
            }
            proxyInn.setSalePriceOnOffTime(new Date());
            proxyInn.setOnOffOperator(getCurrentUser());
            syncChannel.syncOnShelf(proxyInn, PricePattern.PATTERN_SALE_PRICE, false);
            //  proxyInn.setChannels(null);
            Set<ProxysaleChannel> pcs = proxyInn.getPcs();
            if (null != pcs && !pcs.isEmpty()) {
                for (ProxysaleChannel proxysaleChannel : pcs) {
                    if (proxysaleChannel.getStrategy() == 2) {
                        proxysaleChannel.setValid(false);
                        proxysaleChannelService.update(proxysaleChannel);
                    }
                }
            }
            proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_SALE_PRICE, getCurrentUser(), ProxyInnOnoff.OFF));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void salePriceOnShelf(Integer id) {
        try {
            LOGGER.info("--------trace-----卖价开始上架------");
            ProxyInn proxyInn = proxyInnDao.get(id);
            // 执行关房操作
            if (!onOffRoomService.preOnShelf(proxyInn)) {
                throw new RuntimeException("关房失败");
            }
            //未删除
            if (!proxyInn.isValid()) {
                LOGGER.info("不能对已删除的客栈作上架操作，proxyInnId=" + id);
                return;
            }
            //未卖价上价
            if (proxyInn.isSalePriceOnshelfed()) {
                LOGGER.info("不能对已卖价上架的客栈作卖价上架操作，proxyInnId=" + id);
                return;
            }
            //已选择卖价模式
            if (!proxyInn.isSalePriceValid()) {
                throw new RuntimeException("卖价模式无效,proxyInnId=" + id);
            }
            if (proxyInn.isBasePriceOnshelfed()) {
                proxyInn.setStatus(ProxyInn.STATUS_ONSHELF);
            } else {
                proxyInn.setStatus(ProxyInn.STATUS_BASE_OFFSHELF);
            }
            proxyInn.setSalePriceOnOffTime(new Date());
            proxyInn.setOnOffOperator(getCurrentUser());
//            channelInnRelation.update(proxyInn);

            syncChannel.syncOnShelf(proxyInn, PricePattern.PATTERN_SALE_PRICE, true);
            proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_SALE_PRICE, getCurrentUser(), ProxyInnOnoff.ON));
            LOGGER.info("--------trace-----卖价上架成功------");
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public ProxyInn get(Integer id) {
        return proxyInnDao.get(id);
    }

    @Override
    public void modify(ProxyInn proxyInn) {
        if (proxyInn.isValid()) {
            proxyInn.setEditTime(new Date());
            doModify(proxyInn);
        } else {
            LOGGER.info("被修改的客栈信息状态不正确,id=" + proxyInn.getId());
        }
    }

    /**
     * 如果设置的关联渠道为空，则是下架状态
     *
     * @param proxyInn
     * @param newProxysaleChannel
     */
    @Override
    public void modifyBackend(ProxyInn proxyInn, List<ProxysaleChannel> newProxysaleChannel) {
        LOGGER.info("-----trace------modifyBackend-------");
        if (proxyInn.isValid()) {
            proxyInn.setEditOperator(getCurrentUser());
            proxyInn.setEditTime(new Date());
            //获取当前客栈的所有渠道
            Set<ProxysaleChannel> pcs = proxyInn.getPcs();
            Iterator<ProxysaleChannel> channelIterator = pcs.iterator();
            while (channelIterator.hasNext()) { //  循环保存之前的所有渠道,把删了的
                ProxysaleChannel pc = channelIterator.next();
                if (!exists(newProxysaleChannel, pc)) { //  如果新的渠道不包括在中旧的渠道
                    Channel currentChannel = pc.getChannel(); //  获取之前的渠道。
                    syncChannel.syncOnShelf(proxyInn, currentChannel, pc.getStrategy(), false);
                    pc.setValid(false);
                    proxysaleChannelService.update(pc);
                    channelIterator.remove();
                    if (proxyInn.isBasePriceOnshelfed()) {
                        proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_BASE_PRICE, getCurrentUser(), ProxyInnOnoff.OFF));
                    }
                    if (proxyInn.isSalePriceOnshelfed()) {
                        proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_SALE_PRICE, getCurrentUser(), ProxyInnOnoff.OFF));
                    }
                }
            }

            if (null != newProxysaleChannel && !newProxysaleChannel.isEmpty()) {//  如果ID为null，说明是新的渠道，添加到客栈对象中
                for (ProxysaleChannel proxysaleChannel : newProxysaleChannel) {
                    if (null == proxysaleChannel.getId()) {
                        proxysaleChannel.setProxyInn(proxyInn);
                        proxyInn.getPcs().add(proxysaleChannel);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(newProxysaleChannel)) {//  把新的渠道上架
                syncOnshelfNewChannels(proxyInn, newProxysaleChannel);
            }
            doModify(proxyInn);
        } else {
            LOGGER.info("被修改的客栈信息状态不正确,id=" + proxyInn.getId());
        }
    }

    private void doModify(ProxyInn proxyInn) {
        proxyInnDao.update(proxyInn);
    }

    private void syncOnshelfNewChannels(ProxyInn proxyInn, Set<PricePattern> validPatterns, Set<Channel> newChannels) {
        for (Channel newChannel : newChannels) {
            for (PricePattern validPattern : validPatterns) {
                if (validPattern.getPattern().equals(PricePattern.PATTERN_BASE_PRICE)
                        && newChannel.getValidBasePriceStrategy() != null && proxyInn.isBasePriceOnshelfed()) {
                    syncChannel.syncOnShelf(proxyInn, newChannel, PricePattern.PATTERN_BASE_PRICE, true);
                    proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_BASE_PRICE, getCurrentUser(), ProxyInnOnoff.ON));
                } else if (validPattern.getPattern().equals(PricePattern.PATTERN_SALE_PRICE)
                        && newChannel.getValidSalePriceStrategy() != null && proxyInn.isSalePriceOnshelfed()) {
                    syncChannel.syncOnShelf(proxyInn, newChannel, PricePattern.PATTERN_SALE_PRICE, true);
                    proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_SALE_PRICE, getCurrentUser(), ProxyInnOnoff.ON));
                }
            }
        }
    }

    private void syncOnshelfNewChannels(ProxyInn proxyInn, List<ProxysaleChannel> newChannels) {
        for (ProxysaleChannel newChannel : newChannels) {
            if (newChannel.getId() == null && newChannel.getStrategy().equals(PricePattern.PATTERN_BASE_PRICE)
                    && newChannel.getChannel().getValidBasePriceStrategy() != null && proxyInn.isBasePriceOnshelfed()) {
                syncChannel.syncOnShelf(proxyInn, newChannel.getChannel(), PricePattern.PATTERN_BASE_PRICE, true);
                proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_BASE_PRICE, getCurrentUser(), ProxyInnOnoff.ON));
            } else if (newChannel.getId() == null && newChannel.getStrategy().equals(PricePattern.PATTERN_SALE_PRICE)
                    && newChannel.getChannel().getValidSalePriceStrategy() != null && proxyInn.isSalePriceOnshelfed()) {
                syncChannel.syncOnShelf(proxyInn, newChannel.getChannel(), PricePattern.PATTERN_SALE_PRICE, true);
                proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, PricePattern.PATTERN_SALE_PRICE, getCurrentUser(), ProxyInnOnoff.ON));
            }
        }
    }


    private boolean exists(List<ProxysaleChannel> newChannels, ProxysaleChannel ps) {
        if (CollectionsUtil.isNotEmpty(newChannels) && ps != null) {
            for (ProxysaleChannel newChannel : newChannels) {
                if (null == ps.getValid() || ps.getValid()) {
                    if (newChannel.getStrategy() == ps.getStrategy() &&
                            newChannel.getChannel().getId() == ps.getChannel().getId()
                            ) {
                        newChannel.setId(ps.getId());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean exists(Set<Channel> newChannels, Channel channel) {
        if (CollectionsUtil.isNotEmpty(newChannels) && channel != null) {
            for (Channel newChannel : newChannels) {
                if (channel.getId().equals(newChannel.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ProxyInn findByInnId(Integer innId) {
        return proxyInnDao.findByInnId(innId);
    }

    @Override
    public List<Integer> findAll() {
        return proxyInnDao.find("select t.id from ProxyInn t where t.valid=true and t.status!=0");
    }

    @Override
    public List<ProxyInn> findByStatus(Integer status) {
        return proxyInnDao.findBy("status", status, PropertyFilter.MatchType.EQ);
    }

    @Override
    public void onshelf(ProxyInn proxyInn) {
        if (!proxyInn.isSalePriceOnshelfed()) {
//            proxyInnBean.autoOnshelf(proxyInn, PricePattern.PATTERN_SALE_PRICE);


            //-----------------是否满足价格审核上架条件--------------
            //已上架${pattern}模式
            if (proxyInn.isSalePriceOnshelfed()) {
                return;
            }
            //逻辑调整：只要历史有价格通过的记录，就让自动上架
            if (!proxyAuditDao.hasCheckedPriceRecord(proxyInn.getInn(), PricePattern.PATTERN_SALE_PRICE)) {
                LOGGER.info(String.format("自动上架时发现无价格审核记录，不执行上架动作， innId=%d, pattern=%d", proxyInn.getInn()), PricePattern.PATTERN_SALE_PRICE);
                return;
            }
            //---------------------满足上架条件，上架！
            salePriceOnShelf(proxyInn.getId());
        }
    }

    @Override
    public void updateOTALink(Integer proxyInnId, String otaLink) {
        ProxyInn proxyInn = get(proxyInnId);
        proxyInn.setOtaLink(otaLink);
        modify(proxyInn);
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.CRM_URL), ApiURL.CRM_OTA_LINK, null);
        Map<String, Object> params = new HashMap<>();
        params.put("otaInfo", otaLink);
        params.put("pmsInnId", proxyInn.getInn());
        new HttpUtil().postForm(url, params);
    }

    @Override
    public Area getAreaByAreaName(String areaName) {
        return areaDao.selectAreaByAreaName(areaName);
    }

    @Override
    public List<Channel> getRelationChannels(Integer id) {
        return channelInnRelation.getChannelsWithoutPersistence(get(id));
    }

    @Override
    public Map<Integer, String> getOTALinks(String innsJson) {
        Map<Integer, String> map = new HashMap<>();
        JSONArray jsonArray;
        try {
            jsonArray = JSON.parseArray(innsJson);
        } catch (Exception e) {
            throw new RuntimeException(String.format("json转换时异常，入参：%s", innsJson), e);
        }
        if (jsonArray == null || jsonArray.size() <= 0) {
            throw new RuntimeException(String.format("入参转换成json数据后为null，入参：%s", innsJson));
        }
        for (Object json : jsonArray) {
            ProxyInn proxyInn = findByInnId(Integer.parseInt(json.toString()));
            String otaLink = proxyInn.getOtaLink();
            map.put(proxyInn.getInn(), otaLink == null ? "" : otaLink);
        }
        return map;
    }

    @Override
    public Integer[] getProxyInnSummary(Integer areaId) {
        Integer basePriceSummary = 0;
        Integer salePriceSummary = 0;
        List<Integer> statusList = this.proxyInnDao.getHostelSummary(areaId);
        for (Integer status : statusList) {
            if (Objects.equals(status, ProxyInn.STATUS_SALE_OFFSHELF) || Objects.equals(status, ProxyInn.STATUS_ONSHELF)) {
                basePriceSummary++;
            }
            if (Objects.equals(status, ProxyInn.STATUS_BASE_OFFSHELF) || Objects.equals(status, ProxyInn.STATUS_ONSHELF)) {
                salePriceSummary++;
            }
        }
        return new Integer[]{statusList.size(), basePriceSummary, salePriceSummary};
    }

    @Override
    public Object[] getUpAndDownSummary(Integer areaId, Integer inn, Boolean isToday) {
        return proxyInnDao.getUpAndDownSummary(areaId, inn, isToday);
    }

    @Override
    public Object getSignManager(Integer innId) {
        return JSON.parseObject(new HttpUtil().get(getUrl(innId)), SignManagerResult.class);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getProxyInnChannel(Integer proxyInnId, Float pricePattern, Integer innId) {
        if (proxyInnId == null && innId == null) {
            throw new RuntimeException("客栈ID不能为空");
        } else {
            if (proxyInnId == null) {
                ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
                if (proxyInn == null) {
                    throw new RuntimeException("客栈不存在");
                } else {
                    proxyInnId = proxyInn.getId();
                }
            }
        }
        // 请求OMS接口获取全部渠道数据
        List<OtaInfo> otaInfoList = otaInfoService.list();
        if (CollectionsUtil.isEmpty(otaInfoList)) {
            throw new RuntimeException("请求OMS接口获取渠道信息失败");
        }
        if (CollectionUtils.isNotEmpty(otaInfoList)) {
            Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();
            List<Map<String, Object>> proxyInnChannelSaleStrategies = getProxyInnChannel(otaInfoList, proxyInnId, 2, pricePattern);
            proxyInnChannelSaleStrategies.addAll(getProxyInnChannel(otaInfoList, proxyInnId, 3, pricePattern));
            dataMap.put("sale", proxyInnChannelSaleStrategies);
            dataMap.put("base", getProxyInnChannel(otaInfoList, proxyInnId, 1, pricePattern));
            return dataMap;
        }
        return null;
    }

    private List<Map<String, Object>> getProxyInnChannel(List<OtaInfo> otaInfoList, Integer proxyId, Integer strategy, Float pricePattern) {
        // 查询指定价格模式的渠道ID
        List<PriceStrategy> priceStrategyList = priceStrategyDao.selectPriceStrategy(strategy.shortValue());
        if (CollectionsUtil.isNotEmpty(priceStrategyList)) {
            List<Map<String, Object>> data = new ArrayList<>();
            for (PriceStrategy priceStrategy : priceStrategyList) {
                Integer channelId = priceStrategy.getChannel();
                Map<String, Object> map = new HashMap<>();
                for (OtaInfo otaInfo : otaInfoList) {
                    if (channelId.equals(otaInfo.getOtaId())) {
                        map.put("channelName", otaInfo.getName());
                        map.put("channelId", channelId);
                        boolean isOpen = channelInnDao.isOnShelf(proxyId, strategy.shortValue(), channelId);
                        boolean isCanOpen = isCanOpenChannel(proxyId, strategy.shortValue(), channelId, pricePattern);
                        // 如果客栈已经和渠道建立关联，查询最新的匹配能否生效
                        // 判断客栈是否已经开通该渠道
                        map.put("isOpen", isOpen);
                        map.put("isCanOpen", isCanOpen);
                        if (strategy.shortValue() == PriceStrategy.STRATEGY_SALE_BASE_PRICE) {
                            map.put("isSaleBase", true);
                        } else if (strategy.shortValue() == PriceStrategy.STRATEGY_SALE_PRICE) {
                            map.put("isSaleBase", false);
                        }
                        data.add(map);
                    }
                }
            }
            return data;
        }
        return new ArrayList<>();
    }

    /**
     * 根据代销客栈ID、价格策略和渠道ID，判断该客栈是否可以上该渠道
     *
     * @param proxyId   代销客栈ID
     * @param strategy  价格策略
     * @param channelId 渠道ID
     * @return
     */
    private boolean isCanOpenChannel(Integer proxyId, Short strategy, Integer channelId, Float pricePattern) {
        // 判断该渠道是否支持该价格策略
        PriceStrategy priceStrategy = priceStrategyDao.selectPriceStrategy(channelId, strategy);
        if (priceStrategy == null) {
            return false;
        }
        // 如果是普通代销，需要对比客栈的总抽佣是否大于等于渠道的分佣
        if (Objects.equals(strategy, PriceStrategy.STRATEGY_SALE_PRICE) || Objects.equals(strategy, PriceStrategy.STRATEGY_SALE_BASE_PRICE)) {
            if (pricePattern == null) {
                // 获取客栈的总抽佣比例
                PricePattern innPricePattern = pricePatternDao.selectPricePattern(proxyId, PricePattern.PATTERN_SALE_PRICE);
                if (innPricePattern == null) {
                    throw new RuntimeException("获取客栈总抽佣比例异常");
                }
                // 客栈分佣比例
                pricePattern = innPricePattern.getPercentage();
            }

            // 渠道分佣比例
            Float channelPercentage = priceStrategy.getPercentage();
            // 比较客栈的分佣比例和渠道的分佣比例
            if (pricePattern.compareTo(channelPercentage) < 0) {
                return false;
            }
        }
        Channel channel = channelDao.get(channelId);
        if (channel == null) {
            return false;
        }
        Set<Area> saleArea = channel.getSaleArea();
        if (CollectionsUtil.isEmpty(saleArea)) {
            return false;
        }
        List<Integer> channelAreaIdList = new ArrayList<>();
        for (Area area : saleArea) {
            channelAreaIdList.add(area.getId());
        }
        // 如果该渠道的售卖区域不是全国
        if (!channelAreaIdList.contains(1)) {
            // 售卖渠道的销售区域
            ProxyInn proxyInn = proxyInnDao.get(proxyId);
            Area innArea = proxyInn.getArea();
            if (!channelAreaIdList.contains(innArea.getId())) {
                Area innParentArea = innArea.getParent();
                if (!channelAreaIdList.contains(innParentArea.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void offShelfProxyInn(Integer proxyInnId, Short pricePattern, String reason) {
        LOGGER.info("------trace--offShelfProxyInn---------");
        try {
            // 根据ID查询代销客栈对象
            ProxyInn proxyInn = proxyInnDao.get(proxyInnId);
            // 校验客栈状态
            checkProxyInn(proxyInn);
            // 如果是精品代销下架，判断普通代销的上架状态
            if (pricePattern.equals(PricePattern.PATTERN_BASE_PRICE)) {
                if (!proxyInn.isBasePriceOnshelfed()) {
                    LOGGER.info("该客栈精品代销已经下架，不能重复操作" + proxyInnId);
                    throw new RuntimeException("该客栈精品代销已经下架，不能重复操作");
                }
                if (proxyInn.isSalePriceOnshelfed()) {
                    proxyInn.setStatus(ProxyInn.STATUS_BASE_OFFSHELF);
                } else {
                    proxyInn.setStatus(ProxyInn.STATUS_OFFSHELF);
                }
                // 如果是普通代销下架，判断精品代销的上架状态
            } else if (pricePattern.equals(PricePattern.PATTERN_SALE_PRICE)) {
                if (!proxyInn.isSalePriceOnshelfed()) {
                    LOGGER.info("该客栈普通代销已经下架，不能重复操作" + proxyInnId);
                    throw new RuntimeException("该客栈普通代销已经下架，不能重复操作");
                }
                if (proxyInn.isBasePriceOnshelfed()) {
                    proxyInn.setStatus(ProxyInn.STATUS_SALE_OFFSHELF);
                } else {
                    proxyInn.setStatus(ProxyInn.STATUS_OFFSHELF);
                }
            }
            proxyInn.setBasePriceOnOffTime(new Date());
            proxyInn.setOnOffOperator(getCurrentUser());
            // 请求下架推送接口
            syncChannel.syncOnShelf(proxyInn, pricePattern, false);
            proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, pricePattern, getCurrentUser(), ProxyInnOnoff.OFF, reason));
            // 保存操作日志
            financeOperationLogDao.save(new FinanceOperationLog("103", proxyInn.getInnName(), "下架原因:" + reason, getCurrentUser().getSysUserName()));
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public void modifyProxyInnChannel(String jsonData) {
        if (StringUtils.isBlank(jsonData)) {
            throw new RuntimeException("数据异常");
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        // 修改操作的类型，0：设置渠道(普通+精品)，1：精品代销上架，2：普通代销上架
        Short type = jsonObject.getShort("type");
        if (type == null) {
            throw new RuntimeException("修改操作类型异常");
        }
        Integer proxyInnId = jsonObject.getInteger("proxyInnId");
        if (proxyInnId == null) {
            throw new RuntimeException("代销客栈ID不能为空");
        }
        ProxyInn proxyInn = proxyInnDao.get(proxyInnId);
        if (proxyInn == null) {
            throw new RuntimeException("代销客栈不存在");
        }
        // 校验代销客栈是否存在，是否是删除状态
        checkProxyInn(proxyInn);

        // 普通代销渠道ID集合
        String sale = jsonObject.getString("sale");
        List<Integer> saleChannelIdList = JSON.parseArray(sale, Integer.class);
        // 精品代销渠道ID集合
        String base = jsonObject.getString("base");
        List<Integer> baseChannelIdList = JSON.parseArray(base, Integer.class);
        // 卖转滴渠道ID集合
        String saleBase = jsonObject.getString("saleBase");
        List<Integer> saleBaseChannelIdList = JSON.parseArray(saleBase, Integer.class);

        // 用于保存精品代销的客栈渠道关联对象
        List<ProxysaleChannel> baseChannelList = new ArrayList<>();
        // 用于保存普通代销之卖价的客栈渠道关联对象
        List<ProxysaleChannel> saleChannelList = new ArrayList<>();
        // 用于保存普通代销之卖转底的客栈渠道关联对象
        List<ProxysaleChannel> saleBaseChannelList = new ArrayList<>();

        if (CollectionsUtil.isNotEmpty(baseChannelIdList)) {
            for (Integer channelId : baseChannelIdList) {
                baseChannelList.add(buildProxysaleChannel(proxyInn, STRATEGY_BASE_PRICE, channelId));
            }
        }
        if (CollectionsUtil.isNotEmpty(saleChannelIdList)) {
            for (Integer channelId : saleChannelIdList) {
                saleChannelList.add(buildProxysaleChannel(proxyInn, PriceStrategy.STRATEGY_SALE_PRICE, channelId));
            }
        }
        if (CollectionUtils.isNotEmpty(saleBaseChannelIdList)) {
            for (Integer channelId : saleBaseChannelIdList) {
                saleBaseChannelList.add(buildProxysaleChannel(proxyInn, PriceStrategy.STRATEGY_SALE_BASE_PRICE, channelId));
            }
        }
        String operateType = "";
        StringBuilder operateContent = new StringBuilder();
        // type=0，是设置渠道，只会修改客栈渠道关联关系，不会通知上下架接口
        if (type.equals(Short.valueOf("0"))) {
            // 操作类型为0，处理全部两种代销
            // 处理精品代销
            setChannel(proxyInn, baseChannelList, PriceStrategy.STRATEGY_BASE_PRICE, true);
            // 处理普通代销
            setChannel(proxyInn, saleChannelList, PriceStrategy.STRATEGY_SALE_PRICE, true);
            // 处理卖转底
            setChannel(proxyInn, saleBaseChannelList, PriceStrategy.STRATEGY_SALE_BASE_PRICE, true);
            operateType = "105";
            operateContent.append("设置渠道成功后：");
            operateContent.append("精品(活动)的渠道：");
            operateContent.append(baseChannelIdList);
            operateContent.append(";");
            operateContent.append("普通(卖)的渠道：");
            operateContent.append(saleChannelIdList);
            operateContent.append(";");
            operateContent.append("普通(底)的渠道：");
            operateContent.append(saleBaseChannelIdList);
            operateContent.append(";");
        } else {
            operateType = "102";
            if (type.equals(PricePattern.PATTERN_BASE_PRICE)) {
                onShelves(proxyInn, baseChannelList, type);
                operateContent.append("上架成功的精品(活动)的渠道：");
                operateContent.append(baseChannelIdList);
            } else if (type.equals(PricePattern.PATTERN_SALE_PRICE)) {
                saleChannelList.addAll(saleBaseChannelList);
                onShelves(proxyInn, saleChannelList, type);
                operateContent.append("上架成功的普通(卖)的渠道：");
                operateContent.append(saleChannelIdList);
                operateContent.append("上架成功的普通(底)的渠道：");
                operateContent.append(saleBaseChannelIdList);
                // 处理卖转底
//                setChannel(proxyInn, saleBaseChannelList, PriceStrategy.STRATEGY_SALE_BASE_PRICE, false);

                // 获取代销客栈总抽佣比例
                Float percentage = jsonObject.getFloat("pricePattern");
                if (percentage == null) {
                    throw new RuntimeException("客栈抽佣比例不能为空");
                }
                // 修改代销客栈的总抽佣比例
                PricePattern innPricePattern = pricePatternDao.selectPricePattern(proxyInnId, PricePattern.PATTERN_SALE_PRICE);
                if (innPricePattern != null) {
                    innPricePattern.setPercentage(percentage);
                    pricePatternDao.save(innPricePattern);
                }
            } else {
                throw new RuntimeException("操作类型异常");
            }
        }
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog(operateType, proxyInn.getInnName(), operateContent.toString(), getCurrentUser().getSysUserName()));
    }

    /**
     * 设置代销客栈和渠道的关联关系
     *
     * @param proxyInn                代销客栈对象
     * @param newProxySaleChannelList 页面返回的修改后的，客栈关联的渠道集合
     * @param priceStrategy           价格策略
     * @param needPush                是否需要推送
     */
    private void setChannel(ProxyInn proxyInn, List<ProxysaleChannel> newProxySaleChannelList, Short priceStrategy, boolean needPush) {
        LOGGER.info("------trace--setChannel---------");
        Short pricePattern = convertStrategy2Pattern(priceStrategy);
        // 如果取消勾选全部渠道
        if (CollectionsUtil.isEmpty(newProxySaleChannelList)) {
            // 查询客栈和渠道原有的关联关系
            List<ProxysaleChannel> oldProxySaleChannelList = channelInnDao.selectProxySaleChannelList(proxyInn.getId(), priceStrategy);
            if ((pricePattern.equals(PricePattern.PATTERN_BASE_PRICE) && proxyInn.isBasePriceOnshelfed()) || (pricePattern.equals(PricePattern.PATTERN_SALE_PRICE) && proxyInn.isSalePriceOnshelfed())) {
                if (CollectionsUtil.isNotEmpty(oldProxySaleChannelList)) {
                    for (ProxysaleChannel proxysaleChannel : oldProxySaleChannelList) {
                        // 通知接口下线
                        syncChannel.syncOnShelf(proxyInn, pricePattern, false, proxysaleChannel.getChannel());
                    }
                }
            }
            if (CollectionsUtil.isNotEmpty(oldProxySaleChannelList)) {
                // 逻辑删除关联关系
                channelInnDao.removeChannelInn(proxyInn.getId(), priceStrategy);
            }
        } else {
            List<ProxysaleChannel> proxySaleChannelList = null;
            // 根据渠道关联关系的是否删除状态，通知接口进行上架或下架
            if ((pricePattern.equals(PricePattern.PATTERN_BASE_PRICE) && proxyInn.isBasePriceOnshelfed()) || (pricePattern.equals(PricePattern.PATTERN_SALE_PRICE) && proxyInn.isSalePriceOnshelfed())) {
                proxySaleChannelList = merageChannelByStrategy(proxyInn.getId(), newProxySaleChannelList, priceStrategy);
                if (CollectionsUtil.isNotEmpty(proxySaleChannelList)) {
                    for (ProxysaleChannel proxysaleChannel : proxySaleChannelList) {
                        if (needPush) {
                            syncChannel.syncOnShelf(proxyInn, pricePattern, proxysaleChannel.getValid(), proxysaleChannel.getChannel());
                        }
                        channelInnDao.save(proxysaleChannel);
                    }
                }
            } else {
                proxySaleChannelList = getMergeProxySaleChannel(proxyInn.getId(), newProxySaleChannelList, priceStrategy);
                if (CollectionsUtil.isNotEmpty(proxySaleChannelList)) {
                    for (ProxysaleChannel proxysaleChannel : proxySaleChannelList) {
                        channelInnDao.save(proxysaleChannel);
                    }
                }
            }
        }
    }

    private Short convertStrategy2Pattern(Short strategy) {
        if (strategy.equals(STRATEGY_BASE_PRICE)) {
            return PricePattern.PATTERN_BASE_PRICE;
        } else if (strategy.equals(STRATEGY_SALE_PRICE) || strategy.equals(STRATEGY_SALE_BASE_PRICE)) {
        return PricePattern.PATTERN_SALE_PRICE;
        } else {
        throw new RuntimeException("策略异常, strategy=" + strategy);
        }
        }

    /**
     * 上架操作
     *
     * @param proxyInn                代销客栈对象
     * @param newProxySaleChannelList 最新的渠道关联关系
     * @param pricePattern            价格策略，1为精品代销，2位普通代销
     */
    private void onShelves(ProxyInn proxyInn, List<ProxysaleChannel> newProxySaleChannelList, Short pricePattern) {
        LOGGER.info("------trace--onshelves---------");
        // 如果是底价上架操作
        if (pricePattern.equals(PricePattern.PATTERN_BASE_PRICE)) {
            // 判断是否已经是精品代销上架 && 满足上架条件（有底价审核通过的历史及合同已审核通过）
            if (proxyInn.isBasePriceOnshelfed()
                    || !proxyInnBean.isCanOnshelf(proxyInn, PricePattern.PATTERN_BASE_PRICE)) {
                throw new RuntimeException("该客栈已经是精品代销上架状态或不满足手动上架条件（没有底价审核通过的历史）");
            } else {
                // 修改最后一次精品代销上/下架的时间
                proxyInn.setBasePriceOnOffTime(new Date());
            }
        } else if (pricePattern.equals(PricePattern.PATTERN_SALE_PRICE)) {
            if (proxyInn.isSalePriceOnshelfed()
                    || !proxyInnBean.isCanOnshelf(proxyInn, PricePattern.PATTERN_SALE_PRICE)) {
                throw new RuntimeException("该客栈已经是普通代销上架状态或不满足手动上架条件（没有卖价审核通过的历史）");
            } else {
                // 修改最后一次普通代销上/下架的时间
                proxyInn.setSalePriceOnOffTime(new Date());
            }
        } else {
            throw new RuntimeException("价格策略错误");
        }
        List<ProxysaleChannel> mergeProxySaleChannel = getOnShelvesChannel(proxyInn.getId(), newProxySaleChannelList, pricePattern);
        if (CollectionsUtil.isNotEmpty(mergeProxySaleChannel)) {
            for (ProxysaleChannel proxysaleChannel : mergeProxySaleChannel) {
                // 根据渠道关联关系的是否删除状态，通知接口进行上架或下架
                syncChannel.syncOnShelf(proxyInn, pricePattern, proxysaleChannel.getValid(), proxysaleChannel.getChannel());
                // 保存渠道客栈关联关系
                channelInnDao.save(proxysaleChannel);
            }
        }
        // 修改代销客栈状态
        modifyProxyInnStatus(proxyInn, pricePattern, ProxyInnOnoff.ON);
        User currentUser = getCurrentUser();
        // 设置最后修改人
        proxyInn.setEditOperator(currentUser);
        // 设置最后上下架操作人
        proxyInn.setOnOffOperator(currentUser);
        // 设置最后修改时间
        proxyInn.setEditTime(new Date());
        // 记录上架操作日志
        proxyInnOnoffService.save(new ProxyInnOnoff(proxyInn, pricePattern, getCurrentUser(), ProxyInnOnoff.ON, null));
        // 修改代销客栈对象
        proxyInnDao.save(proxyInn);
    }

    /**
     * 根据价格策略和操作类型，修改代销客栈的状态
     *
     * @param proxyInn     代销客栈
     * @param pricePattern 价格策略
     * @param operateType  操作类型
     * @return
     */
    private void modifyProxyInnStatus(ProxyInn proxyInn, Short pricePattern, String operateType) {
        Integer status;
        // 如果是上架操作
        if (ProxyInnOnoff.ON.equals(operateType)) {
            // 如果是精品代销上架操作
            if (pricePattern == 1) {
                if (proxyInn.isSalePriceOnshelfed()) {
                    status = ProxyInn.STATUS_ONSHELF;
                } else {
                    status = ProxyInn.STATUS_SALE_OFFSHELF;
                }
                // 如果是普通代销上架操作
            } else if (pricePattern == 2) {
                if (proxyInn.isBasePriceOnshelfed()) {
                    status = ProxyInn.STATUS_ONSHELF;
                } else {
                    status = ProxyInn.STATUS_BASE_OFFSHELF;
                }
            } else {
                throw new RuntimeException("价格策略错误");
            }
        } else if (ProxyInnOnoff.OFF.equals(operateType)) {
            // 如果是精品代销下架操作
            if (pricePattern == 1) {
                if (proxyInn.isSalePriceOnshelfed()) {
                    status = ProxyInn.STATUS_BASE_OFFSHELF;
                } else {
                    status = ProxyInn.STATUS_OFFSHELF;
                }
                // 如果是普通代销下架操作
            } else if (pricePattern == 2) {
                if (proxyInn.isBasePriceOnshelfed()) {
                    status = ProxyInn.STATUS_SALE_OFFSHELF;
                } else {
                    status = ProxyInn.STATUS_OFFSHELF;
                }
            } else {
                throw new RuntimeException("价格策略错误");
            }
        } else {
            throw new RuntimeException("操作类型错误");
        }
        if (status == null) {
            throw new RuntimeException("代销客栈状态异常");
        }
        proxyInn.setStatus(status);
    }

    /**
     * 为设置渠道使用，合并前端和数据中的客栈渠道关联关系
     *
     * @param proxyInnId
     * @param newProxySaleChannelList
     * @param Strategy
     * @return
     */
    private List<ProxysaleChannel> merageChannelByStrategy(Integer proxyInnId, List<ProxysaleChannel> newProxySaleChannelList, Short Strategy) {
        // 用于保存变更后的客栈渠道关联关系
        List<ProxysaleChannel> mergeList = new ArrayList<>();
        // 查询客栈和渠道原有的关联关系
        List<ProxysaleChannel> oldProxySaleChannelList = channelInnDao.selectProxySaleChannelList(proxyInnId, Strategy);
        // 如果数据库没有存在关联关系
        if (CollectionsUtil.isEmpty(oldProxySaleChannelList)) {
            // 全部都是新增的
            mergeList.addAll(newProxySaleChannelList);
        } else {
            // 查询新增的渠道
            for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                boolean isAdd = true;
                for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                    if (newProxySaleChannel.getChannel().getId().equals(oldProxySaleChannel.getChannel().getId())) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    mergeList.add(newProxySaleChannel);
                }
            }
            // 查询数据库已有的关联关系
            for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                    // 如果已经存在
                    if (oldProxySaleChannel.getChannel().getId().equals(newProxySaleChannel.getChannel().getId())) {
                        mergeList.add(oldProxySaleChannel);
                    }
                }
            }
            // 查询删除的渠道
            for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                boolean isDelete = true;
                for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                    // 如果已经存在
                    if (oldProxySaleChannel.getChannel().getId().equals(newProxySaleChannel.getChannel().getId())) {
                        isDelete = false;
                    }
                }
                if (isDelete) {
                    // 设置逻辑删除标识
                    oldProxySaleChannel.setValid(false);
                    mergeList.add(oldProxySaleChannel);
                }
            }
        }
        return mergeList;
    }

    /**
     * 上架时需要推送接口通知上架的渠道集合
     *
     * @param proxyInnId              代销客栈ID
     * @param newProxySaleChannelList 本次勾选的渠道集合
     * @param pricePattern            价格模式
     * @return 第一次循环查询本次新增的渠道，第二次循环查询勾选关系没有发生变化的渠道，第三次循环查询本次取消勾选的渠道
     */
    private List<ProxysaleChannel> getOnShelvesChannel(Integer proxyInnId, List<ProxysaleChannel> newProxySaleChannelList, Short pricePattern) {
        // 用于保存变更后的客栈渠道关联关系
        List<ProxysaleChannel> mergeList = new ArrayList<>();
        // 查询客栈和渠道原有的关联关系
        List<ProxysaleChannel> oldProxySaleChannelList = null;
        if (pricePattern.equals(PriceStrategy.STRATEGY_BASE_PRICE)) {
            oldProxySaleChannelList = proxysaleChannelDao.findValidByProxyId(proxyInnId, PriceStrategy.STRATEGY_BASE_PRICE);
        } else if (pricePattern.equals(PriceStrategy.STRATEGY_SALE_PRICE)) {
            oldProxySaleChannelList = proxysaleChannelDao.findValidByProxyId(proxyInnId, PriceStrategy.STRATEGY_SALE_PRICE, PriceStrategy.STRATEGY_SALE_BASE_PRICE);
        } else {
            throw new RuntimeException("价格模式异常");
        }
        // 如果数据库没有存在关联关系
        if (CollectionsUtil.isEmpty(oldProxySaleChannelList)) {
            // 全部都是新增的
            mergeList.addAll(newProxySaleChannelList);
        } else {
            // 查询新增的渠道
            for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                boolean isAdd = true;
                for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                    if (newProxySaleChannel.getChannel().getId().equals(oldProxySaleChannel.getChannel().getId())) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    mergeList.add(newProxySaleChannel);
                }
            }
            // 查询数据库已有的关联关系
            for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                    // 如果已经存在
                    if (oldProxySaleChannel.getChannel().getId().equals(newProxySaleChannel.getChannel().getId())) {
                        mergeList.add(oldProxySaleChannel);
                    }
                }
            }
            // 查询删除的渠道
            for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                boolean isDelete = true;
                for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                    // 如果已经存在
                    if (oldProxySaleChannel.getChannel().getId().equals(newProxySaleChannel.getChannel().getId())) {
                        isDelete = false;
                    }
                }
                if (isDelete) {
                    // 设置逻辑删除标识
                    oldProxySaleChannel.setValid(false);
                    mergeList.add(oldProxySaleChannel);
                }
            }
        }
        return mergeList;
    }

    /**
     * 根据客栈ID和价格策略，以及新的客栈渠道关联关系，查询新增的渠道关联和删除的渠道关联
     *
     * @param proxyInnId              代销客栈DI
     * @param newProxySaleChannelList 新增的客栈渠道关联关系集合
     * @param priceStrategy           价格策略
     * @return key=add 新增的客栈渠道关联关系集合，key=delete 删除的客栈渠道关联关系集合
     */
    private List<ProxysaleChannel> getMergeProxySaleChannel(Integer proxyInnId, List<ProxysaleChannel> newProxySaleChannelList, Short priceStrategy) {
        // 用于保存变更后的客栈渠道关联关系
        List<ProxysaleChannel> mergeList = new ArrayList<>();
        // 查询客栈和渠道原有的关联关系
        List<ProxysaleChannel> oldProxySaleChannelList = channelInnDao.selectProxySaleChannelList(proxyInnId, priceStrategy);
        // 如果数据库没有存在关联关系
        if (CollectionsUtil.isEmpty(oldProxySaleChannelList)) {
            // 全部都是新增的
            mergeList.addAll(newProxySaleChannelList);
        } else {
            // 查询删除的渠道
            for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                boolean isDelete = true;
                for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                    // 如果已经存在
                    if (oldProxySaleChannel.getChannel().getId().equals(newProxySaleChannel.getChannel().getId())) {
                        isDelete = false;
                    }
                }
                if (isDelete) {
                    // 设置逻辑删除标识
                    oldProxySaleChannel.setValid(false);
                    mergeList.add(oldProxySaleChannel);
                }
            }
            // 查询新增的渠道
            for (ProxysaleChannel newProxySaleChannel : newProxySaleChannelList) {
                boolean isAdd = true;
                for (ProxysaleChannel oldProxySaleChannel : oldProxySaleChannelList) {
                    if (newProxySaleChannel.getChannel().getId().equals(oldProxySaleChannel.getChannel().getId())) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    mergeList.add(newProxySaleChannel);
                }
            }
        }
        return mergeList;
    }

    /**
     * 根据价格模式和渠道ID构造客栈渠道关联对象
     *
     * @param priceStrategy 价格策略
     * @param channelId     渠道ID
     * @return
     */
    private ProxysaleChannel buildProxysaleChannel(Short priceStrategy, Integer channelId) {
        ProxysaleChannel proxysaleChannel = new ProxysaleChannel();
        proxysaleChannel.setStrategy(priceStrategy);
        proxysaleChannel.setChannel(channelDao.get(channelId));
        proxysaleChannel.setValid(true);
        proxysaleChannel.setCreateTime(new Date());
        proxysaleChannel.setOperator(getCurrentUser());
        return proxysaleChannel;
    }

    /**
     * 根据价格模式和渠道ID构造客栈渠道关联对象
     *
     * @param proxyInn      代销客栈对象
     * @param priceStrategy 价格策略
     * @param channelId     渠道ID
     * @return
     */
    private ProxysaleChannel buildProxysaleChannel(ProxyInn proxyInn, Short priceStrategy, Integer channelId) {
        ProxysaleChannel proxysaleChannel = buildProxysaleChannel(priceStrategy, channelId);
        proxysaleChannel.setProxyInn(proxyInn);
        return proxysaleChannel;
    }

    /**
     * 对代销客栈的状态进行校验
     *
     * @param proxyInn 代销客栈
     */
    private void checkProxyInn(ProxyInn proxyInn) {
        if (proxyInn == null) {
            LOGGER.info("代销客栈不存在");
            throw new RuntimeException("代销客栈不存在");
        }
        // 已经删除
        if (!proxyInn.isValid()) {
            LOGGER.info("不能对已删除的客栈执行操作");
            throw new RuntimeException("不能对已删除的客栈执行操作");
        }
    }

    @Override
    public void batchOnShelfByArea(String jsonData, boolean isOnline) {
        if (StringUtils.isBlank(jsonData)) {
            throw new RuntimeException("数据异常");
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Integer areaId = jsonObject.getInteger("areaId");
        if (areaId == null) {
            // 默认是对全国进行操作
            areaId = 1;
        }
//        Short priceStrategy = jsonObject.getShort("priceStrategy");
//        if (priceStrategy == null) {
//            throw new RuntimeException("未选择需要操作的价格策略");
//        }
        JSONArray dataJsonArray = jsonObject.getJSONArray("datas");
        if (dataJsonArray == null || dataJsonArray.size() <= 0) {
            throw new RuntimeException("请选择批量操作的渠道");
        }
//        List<Integer> channelIds = JSON.parseArray(channelIdArray, Integer.class);
        String operateContent = "批量操作:";
        for (Object dataJson : dataJsonArray) {
            JSONObject json = (JSONObject) dataJson;
            Short priceStrategy = json.getShort("priceStrategy");
            String type = "";
            if (priceStrategy == 1) {
                type = "精品代销";
            } else if (priceStrategy == 2) {
                type = "普通代销(卖)";
            } else if (priceStrategy == 3) {
                type = "普通代销(底)";
            }
            List<Integer> channelIds = JSON.parseArray(json.getJSONArray("channelIds").toJSONString(), Integer.class);
            if (CollectionsUtil.isEmpty(channelIds)) {
                continue;
            }
            operateContent += "【" + type + "，售卖渠道" + channelIds + "】;";
            String status;
            // 如果上架精品代销，需要匹配已经上架或已经精品代销上架的客栈
            if (priceStrategy.equals(PriceStrategy.STRATEGY_BASE_PRICE)) {
                status = "2,3";
                // 如果上架普通代销，需要匹配已经上架和普通代销上架的客栈
            } else if (priceStrategy.equals(PriceStrategy.STRATEGY_SALE_PRICE) || priceStrategy.equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE)) {
                status = "1,3";
            } else {
                throw new RuntimeException("价格策略异常");
            }
            // 根据区域ID查询客栈
            List<ProxyInn> proxyInnList = proxyInnDao.findByAreaId(areaId, status);
            if (CollectionsUtil.isEmpty(proxyInnList)) {
                throw new RuntimeException("所选区域内没有匹配的上架客栈可供操作");
            }
            List<ProxysaleChannel> newProxySaleChannelList = new ArrayList<>();
            if (CollectionsUtil.isNotEmpty(proxyInnList)) {
                for (Integer channelId : channelIds) {
                    newProxySaleChannelList.add(buildProxysaleChannel(priceStrategy, channelId));
                }
            }
            // 遍历全部满足批量操作条件的客栈
            for (ProxyInn proxyInn : proxyInnList) {
                if (isOnline) {
                    batchOnline(proxyInn, newProxySaleChannelList, priceStrategy);
                } else {
                    batchOffline(proxyInn, newProxySaleChannelList, priceStrategy);
                }
            }
        }
        Area area = areaDao.get(areaId);
        String areaName = "";
        if (area != null) {
            areaName = area.getName();
        }
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog(isOnline ? "108" : "109", areaName, operateContent, getCurrentUser().getSysUserName()));
    }

    /**
     * 批量上线渠道
     *
     * @param proxyInn                代销客栈对象
     * @param newProxySaleChannelList 页面传递的客栈渠道关联关系
     * @param priceStrategy           价格策略，1，精品代销，2，普通代销
     */
    private void batchOnline(ProxyInn proxyInn, List<ProxysaleChannel> newProxySaleChannelList, Short priceStrategy) {
        Short pricePattern = convertStrategy2Pattern(priceStrategy);
        Integer proxyInnId = proxyInn.getId();
        for (ProxysaleChannel proxysaleChannel : newProxySaleChannelList) {
            Channel channel = proxysaleChannel.getChannel();
            Integer channelId = channel.getId();
            // 如果该客栈满足上线指定渠道的条件，执行修改客栈渠道关联关系，并通知接口上线
            if (isCanOpenChannel(proxyInnId, priceStrategy, channelId, null)) {
                // 查询数据库是否已经有关联关系
                ProxysaleChannel oldProxysaleChannel = channelInnDao.selectProxySaleChannel(proxyInnId, priceStrategy, channelId);
                // 如果为新增上线渠道，创建客栈渠道关联关系，并通知接口
                if (oldProxysaleChannel == null) {
                    oldProxysaleChannel = buildProxysaleChannel(proxyInn, priceStrategy, channelId);
                    // 根据渠道关联关系的是否删除状态，通知接口进行上架或下架
                    syncChannel.syncOnShelf(proxyInn, pricePattern, true, channel);
                    channelInnDao.save(oldProxysaleChannel);
                }
            }
        }
    }

    /**
     * 批量下线渠道
     *
     * @param proxyInn                代销客栈对象
     * @param newProxySaleChannelList 页面传递的客栈渠道关联关系
     * @param priceStrategy           价格策略，1，精品代销，2，普通代销
     */
    private void batchOffline(ProxyInn proxyInn, List<ProxysaleChannel> newProxySaleChannelList, Short priceStrategy) {
        Short pricePattern = convertStrategy2Pattern(priceStrategy);
        for (ProxysaleChannel proxysaleChannel : newProxySaleChannelList) {
            // 删除渠道关联关系
            channelInnDao.removeChannelInn(proxyInn.getId(), priceStrategy, proxysaleChannel.getChannel().getId());
            // 下线渠道
            syncChannel.syncOnShelf(proxyInn, pricePattern, false, proxysaleChannel.getChannel());
        }
    }


    private void checkAuditPrice(Integer innId, Short pattern) {
        if (!proxyAuditService.hasPriceRecordChecked(innId, pattern)) {
            throw new RuntimeException("历史上没有审核通过的卖价，请先提交价格审核单");
        }
    }

    @Override
    public Float getProxyInnPricePatternByProxyInn(Integer proxyInnId, Short pricePattern, Integer innId) {
        if (innId == null) {
            innId = get(proxyInnId).getInn();
        }
        checkAuditPrice(innId, pricePattern);

        Float percentage = 13f;
        if (pricePattern == null) {
            throw new RuntimeException("价格策略不能为空");
        }
        if (proxyInnId == null && innId == null) {
            throw new RuntimeException("代销客栈ID或客栈ID不能为空");
        } else {
            if (proxyInnId != null) {
                PricePattern innPricePattern = pricePatternDao.selectPricePattern(proxyInnId, pricePattern);
                if (innPricePattern != null) {
                    return innPricePattern.getPercentage();
                }
            }
            if (innId != null) {
                Float innPercentage = pricePatternDao.selectPricePatternByInnId(innId, pricePattern);
                if (innPercentage != null) {
                    return innPercentage;
                }
            }
        }
        return percentage;
    }

    @Override
    public List<Map<String, Object>> getChannelByArea(Integer areaId, Short pricePattern) {
        if (areaId == null) {
            // 默认是对全国范围内操作
            areaId = 1;
        }
        if (pricePattern == null) {
            throw new RuntimeException("价格策略不能为空");
        }
        Area area = areaDao.get(areaId);
        if (area == null) {
            throw new RuntimeException("区域不存在");
        }
        // 请求OMS接口获取全部渠道数据
        List<OtaInfo> otaInfoList = otaInfoService.list();
        if (CollectionsUtil.isEmpty(otaInfoList)) {
            throw new RuntimeException("请求OMS接口获取渠道信息失败");
        }
        if (CollectionUtils.isNotEmpty(otaInfoList)) {
            // 查询指定价格模式的渠道ID
            List<PriceStrategy> priceStrategyList = priceStrategyDao.selectPriceStrategyByArea(pricePattern);
            if (CollectionsUtil.isNotEmpty(priceStrategyList)) {
                List<Map<String, Object>> data = new ArrayList<>();
                for (PriceStrategy priceStrategy : priceStrategyList) {
                    Integer channelId = priceStrategy.getChannel();
                    for (OtaInfo otaInfo : otaInfoList) {
                        Integer otaId = otaInfo.getOtaId();
                        if (channelId.equals(otaId)) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("channelName", otaInfo.getName());
                            map.put("channelId", channelId);
                            map.put("isOpen", false);
                            map.put("isCanOpen", isCanOpenChannel(areaId, channelId));
                            if (priceStrategy.getStrategy() == PriceStrategy.STRATEGY_SALE_BASE_PRICE) {
                                map.put("isSaleBase", true);
                            } else if (priceStrategy.getStrategy() == PriceStrategy.STRATEGY_SALE_PRICE) {
                                map.put("isSaleBase", false);
                            }
                            data.add(map);
                        }
                    }
                }
                return data;
            }
        }
        return null;
    }

    /**
     * 根据区域ID和渠道ID，匹配该渠道是否匹配该区域
     *
     * @param areaId    区域ID
     * @param channelId 渠道ID
     * @return
     */
    private boolean isCanOpenChannel(Integer areaId, Integer channelId) {
        if (areaId == 1) {
            return true;
        } else {
            // 查询渠道对象
            Channel channel = channelDao.get(channelId);
            if (channel == null) {
                return false;
            }
            Set<Area> saleArea = channel.getSaleArea();
            if (CollectionsUtil.isEmpty(saleArea)) {
                return false;
            }
            List<Integer> channelAreaIdList = new ArrayList<>();
            for (Area area : saleArea) {
                channelAreaIdList.add(area.getId());
            }
            if (channelAreaIdList.contains(1)) {
                return true;
            }
            // 售卖渠道的销售区域
            if (!channelAreaIdList.contains(areaId)) {
                Area parentArea = areaDao.selectAreaById(areaId);
                if (parentArea == null) {
                    return false;
                } else {
                    if (!channelAreaIdList.contains(parentArea.getParent().getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private String getUrl(Integer innId) {
        Map<String, String> params = new HashMap<>();
        if (innId != null) {
            params.put("innId", innId.toString());
        }
        return new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.CRM_URL),
                ApiURL.CRM_SIGN_MANAGER_INFO,
                params);
    }

    private Map<Integer, String> getOtas2Map() {
        List<OtaInfo> ots = otaInfoService.list();
        Map<Integer, String> otNames = new HashMap<>();
        for (OtaInfo otaInfo : ots) {
            otNames.put(otaInfo.getOtaId(), otaInfo.getName());
        }
        return otNames;
    }

    private String getPriceModel(ProxyInn p) {
        if (p.isBasePriceValid() && p.isSalePriceValid()) {
            return "精品/普通";
        } else if (!p.isBasePriceValid() && p.isSalePriceValid()) {
            return "普通";
        } else if (p.isBasePriceValid() && !p.isSalePriceValid()) {
            return "精品";
        } else if (!p.isBasePriceValid() && !p.isSalePriceValid()) {
            return "无";
        } else {
            return "价格模式异常，快联系技术";
        }
    }

    @Override
    public void initProxySaleInnChannel(ProxyInn proxyInn, Float pricePattern, List<Integer> saleChannelIdList) {
        // 用于保存普通代销的客栈渠道关联对象
        List<ProxysaleChannel> saleChannelList = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(saleChannelIdList)) {
            for (Integer channelId : saleChannelIdList) {
                saleChannelList.add(buildProxysaleChannel(proxyInn, PricePattern.PATTERN_SALE_PRICE, channelId));
            }
        }
        // 处理普通代销
        setChannel(proxyInn, saleChannelList, PricePattern.PATTERN_BASE_PRICE, true);
        // 修改代销客栈的总抽佣比例
        PricePattern innPricePattern = pricePatternDao.selectPricePattern(proxyInn.getId(), PricePattern.PATTERN_SALE_PRICE);
        if (innPricePattern != null) {
            innPricePattern.setPercentage(pricePattern);
            pricePatternDao.save(innPricePattern);
        }
    }

    @Override
    public void delete(Integer id, String reason) {
        ProxyInn proxyInn = get(id);
        if (!proxyInn.isValid()) {
            return;
        }
        if (!proxyInn.getStatus().equals(ProxyInn.STATUS_OFFSHELF)) {
            throw new RuntimeException("客栈未下架，请先下架客栈再操作移除！");
        }
        proxyInnBean.deleteInCRM(proxyInn.getInn());
        proxyInnBean.deleteInOMS(proxyInn.getInn());

        saveDelLog(proxyInn, reason);
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog("107", proxyInn.getInnName(), "移除代销理由：:" + reason, getCurrentUser().getSysUserName()));
        proxyInn.setValid(false);
    }

    @Override
    public void saveDelLog(ProxyInn proxyInn, String reason) {
        ProxyInnDelLog proxyInnDelLog = new ProxyInnDelLog();
        proxyInnDelLog.setProxyInn(proxyInn);
        proxyInnDelLog.setUser(getCurrentUser());
        proxyInnDelLog.setReason(reason);
        proxyInnDelLog.setDelTime(new Date());
        proxyInnDao.saveDelLog(proxyInnDelLog);
    }

    @Override
    public Page<ProxyInnDelLog> findDelList(Page<ProxyInnDelLog> page) {
        return proxyInnDao.findDelList(page);
    }

    @Override
    public void updateInnerPercentage(Integer id, Float percentage) throws Exception {
        LOGGER.info("------trace--onshelves---------");
        // 更新客栈的价格比例
        PricePattern pp = pricePatternDao.selectPricePattern(id, PriceStrategy.STRATEGY_SALE_PRICE);
        pp.setPercentage(percentage);
        pricePatternDao.save(pp);
        // 获取当前关联的渠道，如果新的比例小于渠道比例。那么就删除渠道客栈和渠道的关联关系
        List<ProxysaleChannel> pcs = this.proxysaleChannelDao.findValidByProxyId(id, PriceStrategy.STRATEGY_SALE_BASE_PRICE, PriceStrategy.STRATEGY_SALE_PRICE);
        for (ProxysaleChannel proxysaleChannel : pcs) {
            if (proxysaleChannel.getValid() && (Objects.equals(proxysaleChannel.getStrategy(), PriceStrategy.STRATEGY_SALE_PRICE) || Objects.equals(proxysaleChannel.getStrategy(), PriceStrategy.STRATEGY_SALE_BASE_PRICE))) {
                PriceStrategy ps = priceStrategyDao.selectPriceStrategy(proxysaleChannel.getChannel().getId(), PriceStrategy.STRATEGY_SALE_PRICE);
                if (null == ps) {
                    ps = priceStrategyDao.selectPriceStrategy(proxysaleChannel.getChannel().getId(), PriceStrategy.STRATEGY_SALE_BASE_PRICE);
                }
                if (null != ps) {
                    if (percentage < ps.getPercentage()) { //  自己的比例 ,小于渠道的比例。就要是删除客栈的渠道的关联关系
                        proxysaleChannel.setValid(false);
                        proxysaleChannelDao.save(proxysaleChannel);
                        // 通知下架
                        syncChannel.syncOnShelf(proxyInnDao.get(id), PricePattern.PATTERN_SALE_PRICE, false, proxysaleChannel.getChannel());
                    }
                }
            }
        }
        // 查询客栈对象
        ProxyInn proxyInn = proxyInnDao.get(id);
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog("104", proxyInn.getInnName(), "修改后的总抽佣比例：" + percentage, getCurrentUser().getSysUserName()));
    }

    @Override
    public Page<FinanceOperationLog> findProxySaleOperationLogList(Page<FinanceOperationLog> page, String innName, String startDate, String endDate, String operateType) {
        return financeOperationLogDao.selectProxySaleOperationLogList(page, innName, startDate, endDate, operateType);
    }

    @Override
    public Page findPriceUpdateInnList(Page page, String innName) {
        /*if (StringUtils.isNoneBlank(innName)) {
            page = proxyInnDao.findPriceUpdateInnList(page, innName);
        } else {
            page = proxyInnDao.findPriceUpdateInnList(page);

        }*/
        if (StringUtils.isBlank(innName)) {
            page.setTotalCount(0);
        } else {
            page = proxyInnDao.findPriceUpdateInnList(page, innName);
        }
        return page;
    }

    @Override
    public String findPriceDetailByChannel(PriceDetailQuery detailQuery) {
        Map<String, String> params = new HashMap<>();
        params.put("accountId", detailQuery.getOuterId().toString());
        params.put("channelId", detailQuery.getChannelId().toString());
        params.put("from", new SimpleDateFormat("yyyy-MM-dd").format(detailQuery.getFrom()));
        params.put("to", new SimpleDateFormat("yyyy-MM-dd").format(detailQuery.getTo()));
        params.put("otaId", Constants.OMS_PROXY_PID.toString());
        long ts = System.currentTimeMillis();
        params.put("timestamp", String.valueOf(ts));
        params.put("signature", MD5.getOMSSignature(ts));
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_QUERY_ROOM_STATUS, params);
        return new HttpUtil().get(url);
    }

    @Override
    public void updatePrice(Integer accountId, String otaList, String roomList, String innName) {
        //操作日志内容
        StringBuilder operateContent = new StringBuilder();
        //接口调用参数
        JSONArray otaListArr = new JSONArray();
        JSONArray otaListJsonArr = JSON.parseArray(otaList);
        for (Object o : otaListJsonArr) {
            JSONObject otaObj = (JSONObject) o;
            Set<String> keys = otaObj.keySet();
            for (String key : keys) {
                otaListArr.add(Integer.valueOf(key));
                operateContent.append(otaObj.get(key)).append(",");
            }
        }
        operateContent = operateContent.replace(operateContent.lastIndexOf(","), operateContent.length(), ";");
        JSONArray roomListJsonArr = JSON.parseArray(roomList);
        for (Object o : roomListJsonArr) {
            JSONObject roomObj = (JSONObject) o;
            operateContent.append(roomObj.getString("otaRoomTypeName")).append("(").append(roomObj.getString("from")).append("-").append(roomObj.getString("to")).append(")");
            Integer extraPrice = roomObj.getInteger("extraPrice");
            if (extraPrice >= 0) {
                operateContent.append("+");
            }
            operateContent.append(extraPrice).append(";");
            roomObj.remove("otaRoomTypeName");//房型名称只用于日志表记录，不用于接口参数
        }
        Map<String, String> params = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", accountId);
        map.put("otaList", otaListArr);
        map.put("roomList", roomListJsonArr);
        try {
            params.put("data", URLEncoder.encode(new JSONObject(map).toJSONString(), DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("调价请求oms接口时参数编码异常", e);
        }
        params.put("otaId", Constants.OMS_PROXY_PID.toString());
        long currentTimeMillis = System.currentTimeMillis();
        params.put("timestamp", String.valueOf(currentTimeMillis));
        String omsSignature = MD5.getOMSSignature(currentTimeMillis);
        params.put("signature", omsSignature);
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_SET_EXTRA_PRICE, params);

        String result = new HttpUtil().get(url);
        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("status") != 200) {
            throw new RuntimeException("oms调价接口调用失败, " + jsonResult.getString("message"));
        }
        financeOperationLogDao.save(new FinanceOperationLog("113", innName, operateContent.toString(), getCurrentUser().getSysUserName()));
    }

    @Override
    public List<Map<String, Object>> getChannelByInnId(Integer innId) {
        Map<String, List<Map<String, Object>>> proxyInnChannel = getProxyInnChannel(null, null, innId);
        List<Map<String, Object>> sale = proxyInnChannel.get("sale");
        List<Map<String, Object>> base = proxyInnChannel.get("base");
        List<Map<String, Object>> channel = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(sale)) {
            for (Map<String, Object> map : sale) {
                if ((boolean) map.get("isOpen")) {
                    channel.add(map);
                }
            }
        }
        if (CollectionsUtil.isNotEmpty(base)) {
            for (Map<String, Object> map : base) {
                if ((boolean) map.get("isOpen")) {
                    int i = 1;
                    if (CollectionsUtil.isNotEmpty(channel)) {
                        //去除重复渠道
                        for (Map<String, Object> m : channel) {
                            if (map.get("channelId").equals(m.get("channelId"))) {
                                i = 0;
                                break;
                            }
                        }
                        if (i == 1) {
                            channel.add(map);
                        }
                    } else {
                        channel.add(map);
                    }
                }
            }
        }
        return channel;
    }

    /**
     * 封装渠道ID，渠道名称
     *
     * @param innId
     * @return
     */
    @Override
    public List<Map<String, Object>> packChannel(Integer innId) {
        List<Map<String, Object>> channelByInnId = getChannelByInnId(innId);
        List<Map<String, Object>> listMap = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(channelByInnId)) {
            for (Map<String, Object> m : channelByInnId) {
                Integer channelId = (Integer) m.get("channelId");
                String channelName = (String) m.get("channelName");
                Map<String, Object> map = new HashMap<>();
                map.put("channelId", channelId);
                map.put("channelName", channelName);
                listMap.add(map);
            }
            return listMap;
        }
        return null;
    }
}
