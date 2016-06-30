package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.proxysale.InnCloseDate;
import com.project.bean.proxysale.OmsInnCloseDate;
import com.project.bean.vo.AjaxResult;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.area.AreaDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.CloseDateDao;
import com.project.dao.proxysale.CloseLogDao;
import com.project.dao.proxysale.CloseTaskDao;
import com.project.dao.proxysale.ProxyInnDao;
import com.project.entity.account.User;
import com.project.entity.area.Area;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.proxysale.CloseDate;
import com.project.entity.proxysale.CloseLog;
import com.project.entity.proxysale.CloseTask;
import com.project.entity.proxysale.ProxyInn;
import com.project.enumeration.CloseType;
import com.project.service.CurrentUserHolder;
import com.project.service.account.AccountService;
import com.project.utils.CollectionsUtil;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.PassWordUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by sam on 2015/12/10.
 */
@Component
@Transactional
public class OnOffRoomServiceImpl implements OnOffRoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OnOffRoomServiceImpl.class);
    private static final int BATCH_REQUEST_SIZE = 500;
    @Autowired
    private CloseDateDao closeDateDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private FinanceOperationLogDao financeOperationLogDao;
    @Autowired
    private ProxyInnDao proxyInnDao;
    @Autowired
    private CloseLogDao closeLogDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CloseTaskDao closeTaskDao;
    @Resource
    private CurrentUserHolder currentUserHolder;

    @Override
    public AjaxResult getInnCloseInfo(Integer innId) {
        if (innId == null) {
            return new AjaxResult(Constants.HTTP_500, "客栈ID不能为空");
        }
        List<CloseDate> innCloseDateList;
        try {
            innCloseDateList = closeDateDao.selectCloseDateByInnId(innId);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, innCloseDateList);
    }

    @Override
    public AjaxResult areaOff(String closeInfo) {
        JSONObject jsonObject = JSON.parseObject(closeInfo);
        Integer areaId = jsonObject.getInteger("areaId");
        // 关房类型,1为OMS关房，2为分销商锁房
        Integer offType = jsonObject.getInteger("offType");
        if (offType == null) {
            return new AjaxResult(Constants.HTTP_500, "关房类型不能为空");
        }
        // 关房开始日期
        String closeBeginDate = jsonObject.getString("closeBeginDate");
        // 关房结束日期
        String closeEndDate = jsonObject.getString("closeEndDate");
        if (StringUtils.isBlank(closeBeginDate) || StringUtils.isBlank(closeEndDate)) {
            return new AjaxResult(Constants.HTTP_500, "关房日期不能为空");
        }
        LOGGER.info("开始批量关房【" + areaId + "】" + closeBeginDate + "至" + closeEndDate);
        try {
            // 查询本次关房需要操作的客栈
            List<ProxyInn> proxyInnList = getProxyInnListByAreaId(areaId);
            // 只有分销商锁房操作才进行数据库操作
            if (offType == 1) {
                omsOffRoom(areaId, proxyInnList, closeBeginDate, closeEndDate);
            } else if (offType == 2) {
                // 保存区域关房对象
                saveAreaCloseLog(areaId, closeBeginDate, closeEndDate);
                if (CollectionsUtil.isNotEmpty(proxyInnList)) {
                    List<CloseLog> innCloseLogs = new ArrayList<>();
                    LOGGER.info("本次批量关房客栈数量：" + proxyInnList.size());
                    long begin = System.currentTimeMillis();
                    for (ProxyInn proxyInn : proxyInnList) {
                        Integer innId = proxyInn.getInn();
                        List<CloseDate> innOldCloseDates = closeDateDao.selectCloseDateByInnId(innId);
                        Set<CloseDate> innCloseDates = mergeCloseDate(new HashSet<>(innOldCloseDates), closeBeginDate, closeEndDate);
                        CloseLog innCloseLog = processInnOffRoom(innId, innCloseDates);
                        innCloseLogs.add(innCloseLog);
                    }
                    String offTypeTask = CloseTask.AREA_CLOSE;
                    if(areaId == null) {
                        offTypeTask = CloseTask.ALL_CLOSE;
                    }
                    batchChannelOffRoom(areaId, offTypeTask, innCloseLogs);
                    LOGGER.info("关房用时：" + (System.currentTimeMillis() - begin));
                }
            } else {
                return new AjaxResult(Constants.HTTP_500, "关房类型异常");
            }
        } catch (Exception e) {
            LOGGER.error("批量关房失败，原因：" + e);
            return new AjaxResult(Constants.HTTP_500, "操作失败：" + e.getMessage());
        }
        saveOperationLog(areaId, offType, closeBeginDate, closeEndDate);
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }

    private void saveAreaCloseLog(Integer areaId, String closeBeginDate, String closeEndDate) {
        CloseLog closeLog = getCloseLogByAreaId(areaId);
        // 保存关房记录
        closeLogDao.save(closeLog);
        CloseDate closeDate = new CloseDate();
        closeDate.setCloseBeginDate(closeBeginDate);
        closeDate.setCloseEndDate(closeEndDate);
        closeDate.setCloseLog(closeLog);
        // 保存关房日期对象
        closeDateDao.save(closeDate);
    }

    /**
     * OSM批量关房
     *
     * @param proxyInnList   需要关房的客栈集合
     * @param closeBeginDate 关房的开始日期
     * @param closeEndDate   关房的结束日期
     */
    private void omsOffRoom(Integer areaId, List<ProxyInn> proxyInnList, String closeBeginDate, String closeEndDate) {
        if (CollectionsUtil.isNotEmpty(proxyInnList)) {
            List<OmsInnCloseDate> omsInnCloseDates = new ArrayList<>();
            for (ProxyInn proxyInn : proxyInnList) {
                Integer innId = proxyInn.getInn();
                omsInnCloseDates.add(new OmsInnCloseDate(innId, closeBeginDate, closeEndDate));
            }
            List<List<OmsInnCloseDate>> lists = CollectionsUtil.splitList(omsInnCloseDates, BATCH_REQUEST_SIZE);
            for (List<OmsInnCloseDate> list : lists) {
                String offType = CloseTask.AREA_CLOSE;
                if(areaId == null) {
                    offType = CloseTask.ALL_CLOSE;
                }
                CloseTask closeTask = saveCloseTask(areaId, null, offType, closeBeginDate, closeEndDate);
                batchOmsOffRoom(closeTask.getId(), list);
            }
        }
    }

    /**
     * 请求OMS批量关房接口
     *
     * @param taskId
     * @param omsInnCloseDates
     */
    private void batchOmsOffRoom(Integer taskId, List<OmsInnCloseDate> omsInnCloseDates) {
        Map<String, Object> baseParamMap = PassWordUtil.getBaseParamMap();
        baseParamMap.put("closeInnJson", JSON.toJSONString(omsInnCloseDates));
        baseParamMap.put("taskId", taskId);
        String url = SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL) + ApiURL.OMS_OFF_ROOM;
        String result = new HttpUtil().httpPost(url, baseParamMap, false);
        if (result == null) {
            LOGGER.error("OMS接口无响应");
            throw new RuntimeException("OMS接口无响应");
        }
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (!"200".equals(status)) {
                LOGGER.error("关房请求关房接口失败");
                throw new RuntimeException("请求关房接口失败");
            }
        }
    }

    /**
     * 保存批量关房任务对象
     *
     * @param areaId
     * @param offType
     * @param innId
     * @param closeBeginDate
     * @param closeEndDate
     * @return
     */
    private CloseTask saveCloseTask(Integer areaId, Integer innId, String offType, String closeBeginDate, String closeEndDate) {
        CloseTask closeTask = new CloseTask();
        closeTask.setCreator(getCurrentUserName());
        closeTask.setModifior(getCurrentUserName());
        closeTask.setOffType(offType);
        closeTask.setAreaId(areaId);
        closeTask.setInnId(innId);
        closeTask.setBeginDate(closeBeginDate);
        closeTask.setEndDate(closeEndDate);
        // 默认是第一次执行
        closeTask.setExecuteTime(1);
        closeTaskDao.save(closeTask);
        return closeTask;
    }

    /**
     * 保存区域关房的操作记录
     *
     * @param areaId         区域ID，如果为空是全国关房操作
     * @param offType        1为oms关房，2为分销商锁房
     * @param closeBeginDate 关房的开始日期
     * @param closeEndDate   关房的结束日期
     */
    private void saveOperationLog(Integer areaId, Integer offType, String closeBeginDate, String closeEndDate) {
        String operateObject = "全国";
        if (areaId != null) {
            Area area = areaDao.get(areaId);
            if (area != null) {
                operateObject = area.getName();
            }
        }
        String content = "";
        if (offType == 1) {
            content = "OMS关房";
        } else if (offType == 2) {
            content = "分销商锁房";
        }
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog("110", operateObject, "本次" + content + "日期：" + "[" + closeBeginDate + "至" + closeEndDate + "]", getCurrentUserName()));
    }

    /**
     * 获取关房日期字符串
     *
     * @param closeDates
     * @return
     */
    private String getCloseDateString(Set<CloseDate> closeDates) {
        StringBuilder result = new StringBuilder();
        if (CollectionsUtil.isNotEmpty(closeDates)) {
            for (CloseDate closeDate : closeDates) {
                result.append("[");
                result.append(closeDate.getCloseBeginDate());
                result.append("到");
                result.append(closeDate.getCloseEndDate());
                result.append("]");
            }
        } else {
            result.append("清空了全部关房记录");
        }
        return result.toString();
    }

    /**
     * 根据区域ID查询关房对象
     *
     * @param areaId
     * @return
     */
    private CloseLog getCloseLogByAreaId(Integer areaId) {
        CloseLog closeLog;
        // 获取当前登录用户ID
        Integer currentUserId = getCurrentUserId();
        if (areaId == null) {
            closeLog = closeLogDao.selectCloseLogOfAll();
            if (closeLog == null) {
                closeLog = new CloseLog();
                closeLog.setCloseType(CloseType.ALL);
                closeLog.setCreator(currentUserId);
            }
        } else {
            closeLog = closeLogDao.selectCloseLogByAreaId(areaId);
            if (closeLog == null) {
                closeLog = new CloseLog();
                closeLog.setCloseType(CloseType.AREA);
                closeLog.setAreaId(areaId);
                closeLog.setCreator(currentUserId);
            }
        }
        closeLog.setDateUpdated(new Date());
        closeLog.setModifior(currentUserId);
        return closeLog;
    }

    /**
     * 根据区域ID查询需要进行关房操作的客栈集合
     *
     * @param areaId
     * @return
     */
    private List<ProxyInn> getProxyInnListByAreaId(Integer areaId) {
        List<ProxyInn> proxyInnList = null;
        if (areaId == null) {
            proxyInnList = proxyInnDao.findAll();
        } else {
            proxyInnList = proxyInnDao.findByAreaId(areaId);
        }
        return proxyInnList;
    }

    @Override
    public AjaxResult innOff(String closeInfo) {
        try {
            JSONObject jsonObject = JSON.parseObject(closeInfo);
            // 获取客栈ID
            Integer innId = jsonObject.getInteger("innId");
            // 关房类型，1为OMS关房，2为分销商锁房
//            Integer closeType = jsonObject.getInteger("closeType");
            LOGGER.info("开始客栈【" + innId + "】关房操作");
            if (innId == null) {
                return new AjaxResult(Constants.HTTP_500, "客栈ID不能为空");
            }
            // 获取关房日期集合，可以为空（即清除全部关房记录）
            Set<CloseDate> closeDates = getCloseDateFromJson(closeInfo);
            LOGGER.info("关房时间：" + getCloseDateString(closeDates));
            CloseLog closeLog = processInnOffRoom(innId, closeDates);
            innOffRoom(closeLog);
            ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
            // 保存操作日志
            financeOperationLogDao.save(new FinanceOperationLog("106", proxyInn.getInnName(), "本次关房操作：" + getCloseDateString(closeDates), getCurrentUserName()));
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, "操作失败：");
        }
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }

    /**
     * 上架前的检查
     *
     * @param proxyInn
     * @return
     */
    @Override
    public boolean preOnShelf(ProxyInn proxyInn) {
        LOGGER.info("================================开始上架前关房操作");
        Date basePriceOnOffTime = proxyInn.getBasePriceOnOffTime();
        Date salePriceOnOffTime = proxyInn.getSalePriceOnOffTime();
        // 没有上架和下架时间记录，为新上架客栈，需要继承全国和该客栈所属地区的关房记录
        if (basePriceOnOffTime == null && salePriceOnOffTime == null) {
            // 用于保存全国和地区的关房记录
            List<CloseDate> closeDates = new ArrayList<>();
            // 全国的关房记录
            List<CloseDate> allCloseDates = closeDateDao.selectAllCloseDate();
            if (CollectionsUtil.isNotEmpty(allCloseDates)) {
                closeDates.addAll(allCloseDates);
            }
            // 区域的关房记录
            List<CloseDate> areaCloseLogs = closeDateDao.selectCloseDateByAreaId(proxyInn.getArea().getId());
            if (CollectionsUtil.isNotEmpty(areaCloseLogs)) {
                closeDates.addAll(areaCloseLogs);
            }
            if (!CollectionsUtil.isEmpty(closeDates)) {
                CloseLog closeLogOld = closeLogDao.selectCloseLogByInnId(proxyInn.getInn());
                if (closeLogOld != null) {
                    closeLogDao.deleteCloseDate(closeLogOld.getId());
                } else {
                    closeLogOld = new CloseLog();
                    closeLogOld.setInnId(proxyInn.getInn());
                    closeLogOld.setCloseType(CloseType.INN);
                }
                closeLogOld.setCloseDates(getNewCloseDates(closeLogOld, new HashSet<>(closeDates)));
                // 构造关房对象的拓展属性
                buildCloseLog(closeLogOld);
                // 批量关房
                innOffRoom(closeLogOld);
                // 保存新增的关房对象
                closeLogDao.save(closeLogOld);
            }
        }
        LOGGER.info("================================上架前关房操作成功");
        return true;
    }

    @Override
    public AjaxResult batchOpenRoom(String closeBeginDate, String closeEndDate) {
        if (StringUtils.isBlank(closeBeginDate)) {
            throw new RuntimeException("关房开始日期不能为空");
        }
        if (StringUtils.isBlank(closeEndDate)) {
            throw new RuntimeException("关房结束日期不能为空");
        }
        List<CloseLog> closeLogList = closeLogDao.selectCloseDateByIntervalTime(closeBeginDate, closeEndDate);
        if (CollectionsUtil.isNotEmpty(closeLogList)) {
            for (CloseLog closeLog : closeLogList) {
                Set<CloseDate> closeDates = closeLog.getCloseDates();
                if (CollectionsUtil.isNotEmpty(closeDates)) {
                    for (Iterator<CloseDate> iterator = closeDates.iterator(); iterator.hasNext(); ) {
                        CloseDate closeDate = iterator.next();
                        if (closeBeginDate.equals(closeDate.getCloseBeginDate()) && closeEndDate.equals(closeDate.getCloseEndDate())) {
                            iterator.remove();
                            closeDateDao.deleteCloseDateById(closeDate.getId());
                        }
                    }
                }
                innOffRoom(closeLog);
            }
        }
        return new AjaxResult(Constants.HTTP_OK, "操作成功");
    }

    /**
     * 根据客栈ID和关房日期对象进行关房操作
     *
     * @param innId      客栈ID
     * @param closeDates 关房日期对象集合
     */
    private CloseLog processInnOffRoom(Integer innId, Set<CloseDate> closeDates) {
        // 查询客栈原有的关房记录
        CloseLog closeLogOld = closeLogDao.selectCloseLogByInnId(innId);
        // 获取当前登录用户ID
        Integer currentUserId = getCurrentUserId();
        if (closeLogOld == null) {
            closeLogOld = new CloseLog();
            closeLogOld.setInnId(innId);
            closeLogOld.setCloseType(CloseType.INN);
            closeLogOld.setCreator(currentUserId);
        } else {
            // 清除全部关房记录
            closeLogDao.deleteCloseDate(closeLogOld.getId());
        }
        closeLogOld.setModifior(currentUserId);
        closeLogOld.setDateUpdated(new Date());
        // 保存关房记录对象
        closeLogDao.save(closeLogOld);
        if (CollectionsUtil.isNotEmpty(closeDates)) {
            for (CloseDate closeDate : closeDates) {
                closeDate.setCloseLog(closeLogOld);
                closeDateDao.save(closeDate);
            }
        }
        closeLogOld.setCloseDates(closeDates);
        return closeLogOld;
    }


    /**
     * 获取当前登录用户的ID
     *
     * @return
     */
    private Integer getCurrentUserId() {
        com.project.entity.account.User currentUser = currentUserHolder.getUser();
        if (currentUser == null) {
            throw new RuntimeException("登录超时，请重新登录");
        }
        return currentUser.getId().intValue();
    }

    /**
     * 根据前端的json数据解析日期集合
     *
     * @param closeInfo
     * @return
     */
    private Set<CloseDate> getCloseDateFromJson(String closeInfo) {
        JSONObject jsonObject = JSON.parseObject(closeInfo);
        JSONArray closeDate = jsonObject.getJSONArray("closeDate");
        Set<CloseDate> closeDates = new HashSet<>();
        if (!CollectionsUtil.isEmpty(closeDate)) {
            for (Object object : closeDate) {
                CloseDate areaCloseDate = new CloseDate();
                JSONObject dateJson = JSON.parseObject(String.valueOf(object));
                areaCloseDate.setCloseBeginDate(dateJson.getString("closeBeginDate"));
                areaCloseDate.setCloseEndDate(dateJson.getString("closeEndDate"));
                areaCloseDate.setStatus("0");
                closeDates.add(areaCloseDate);
            }
        }
        return closeDates;
    }


    /**
     * 获得当前登录用户的用户名
     *
     * @return
     */
    private String getCurrentUserName() {
        User currentUser = SpringSecurityUtil.getCurrentUser();
        return currentUser == null ? "系统" : currentUser.getUsername();
    }


    /**
     * 请求oms接口关房
     *
     * @param innCloseLog
     * @return
     */
    public void innOffRoom(CloseLog innCloseLog) {
        if (innCloseLog == null) {
            throw new RuntimeException("请求关房接口失败");
        }
        List<CloseLog> innCloseLogs = new ArrayList<>();
        innCloseLogs.add(innCloseLog);
        batchChannelOffRoom(null, CloseTask.INN_CLOSE, innCloseLogs);
    }

    /**
     * 批量关房
     * @param areaId 区域ID
     * @param offType 关房类型：0为全国，1为区域，2为客栈
     * @param innCloseLogs
     * @return
     */
    private void batchChannelOffRoom(Integer areaId, String offType, List<CloseLog> innCloseLogs) {
        if (!CollectionsUtil.isEmpty(innCloseLogs)) {
            List<InnCloseDate> innInfoList = new ArrayList<>();
            List<List<CloseLog>> lists = CollectionsUtil.splitList(innCloseLogs, BATCH_REQUEST_SIZE);
            for (List<CloseLog> list : lists) {
                for (CloseLog closeLog : list) {
                    InnCloseDate innCloseDate = new InnCloseDate(closeLog);
                    innInfoList.add(innCloseDate);
                }
                Integer innId = null;
                if(CloseTask.INN_CLOSE.equals(offType)) {
                    innId = innInfoList.get(0).getInnId();
                }
                CloseTask closeTask = saveCloseTask(areaId, innId, offType, null, null);
                channelOffRoom(closeTask.getId(), innInfoList);
            }
        }
    }

    /**
     * 调用OMS接口
     *
     * @param taskId      关房任务ID
     * @param innInfoList 封装的数据集合
     */
    private void channelOffRoom(Integer taskId, List<InnCloseDate> innInfoList) {
        Map<String, Object> baseParamMap = PassWordUtil.getBaseParamMap();
        baseParamMap.put("innInfoJson", JSON.toJSONString(innInfoList));
        baseParamMap.put("taskId", taskId);
        String url = SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL) + ApiURL.CHANNEL_OFF_ROOM;
        String result = new HttpUtil().httpPost(url, baseParamMap, false);
        if (result == null) {
            LOGGER.error("OMS接口无响应");
            throw new RuntimeException("OMS接口无响应");
        }
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (!"200".equals(status)) {
                LOGGER.error("关房请求关房接口失败");
                throw new RuntimeException("请求关房接口失败");
            }
        }
    }


    /**
     * 构造关房记录对象
     * 更新创建人（如果是新增操作）
     * 更新最后修改人
     * 更新最后修改时间
     *
     * @param closeLog
     */
    private void buildCloseLog(CloseLog closeLog) {
        com.project.entity.account.User currentUser = currentUserHolder.getUser();
        if (currentUser == null) {
            throw new RuntimeException("登录超时，请重新登录");
        }
        int currentUserId = currentUser.getId().intValue();
        Integer creator = closeLog.getCreator();
        if (creator == null) {
            closeLog.setCreator(currentUserId);
        }
        // 设置当前时间为最后更新时间
        closeLog.setDateUpdated(new Date());
        // 设置当前登录用户为最后操作人
        closeLog.setModifior(currentUserId);
        Set<CloseDate> closeDates = closeLog.getCloseDates();
        if (!CollectionsUtil.isEmpty(closeDates)) {
            for (CloseDate closeDate : closeDates) {
                closeDate.setCloseLog(closeLog);
            }
        }
    }


    /**
     * 获取新的关房日期对象
     *
     * @param innCloseLog
     * @param closeDatesBatch
     * @return
     */
    private Set<CloseDate> getNewCloseDates(CloseLog innCloseLog, Set<CloseDate> closeDatesBatch) {
        Set<CloseDate> closeDatesInn = new HashSet<>();
        for (CloseDate closeDateBatch : closeDatesBatch) {
            closeDatesInn.add(getNewCloseDate(innCloseLog, closeDateBatch));
        }
        return closeDatesInn;
    }

    /**
     * 拷贝批量关房对象到客栈关房对象
     *
     * @param innCloseLog
     * @param closeDateBatch
     * @return
     */
    private CloseDate getNewCloseDate(CloseLog innCloseLog, CloseDate closeDateBatch) {
        CloseDate closeDate = new CloseDate();
        closeDate.setCloseLog(innCloseLog);
        closeDate.setCloseBeginDate(closeDateBatch.getCloseBeginDate());
        closeDate.setCloseEndDate(closeDateBatch.getCloseEndDate());
        closeDate.setStatus(closeDateBatch.getStatus());
        return closeDate;
    }

    /**
     * 合并客栈原有关房记录和批量关房记录
     *
     * @param innCloseDates  数据库原有关房记录对象
     * @param closeBeginDate 新增的关房开始日期
     * @param closeEndDate   新增的关房结束日期
     * @return
     */
    private Set<CloseDate> mergeCloseDate(Set<CloseDate> innCloseDates, String closeBeginDate, String closeEndDate) {
        CloseDate newCloseDate = new CloseDate();
        newCloseDate.setCloseBeginDate(closeBeginDate);
        newCloseDate.setCloseEndDate(closeEndDate);
        newCloseDate.setStatus("0");
        innCloseDates.add(newCloseDate);
        return innCloseDates;
    }


    @Override
    public void processChannelFailRoomOff(String content) {
        if (StringUtils.isNotBlank(content)) {
            JSONObject jsonObject = JSON.parseObject(content);
            String status = jsonObject.getString("status");
            // 获取任务ID
            Integer taskId = jsonObject.getInteger("taskId");
            CloseTask closeTask = closeTaskDao.get(taskId);
            Integer successNum = jsonObject.getInteger("successNum");
            // 如果执行失败
            if (!"1".equals(status)) {
                if (closeTask != null) {
                    String failInfoStr = jsonObject.getString("failInfo");
                    JSONObject failInfo = JSON.parseObject(failInfoStr);
                    // 失败客栈集合
                    JSONArray failInns = failInfo.getJSONArray("failInns");
                    // 获取已经执行次数
                    int executeTime = closeTask.getExecuteTime();
                    if (executeTime < 4) {
                        // 分销商锁房
                        List<CloseLog> innCloseLogs = new ArrayList<>();
                        for (Object object : failInns) {
                            JSONObject dateJson = JSON.parseObject(String.valueOf(object));
                            Integer innId = dateJson.getInteger("innId");
                            CloseLog closeLog = closeLogDao.selectCloseLogByInnId(innId);
                            innCloseLogs.add(closeLog);
                        }
                        updateCloseTask(closeTask);
                        // 调用接口分销商锁房
                        batchChannelOffRoom(closeTask.getAreaId(), closeTask.getOffType(), innCloseLogs);
                    } else {
                        saveOperationLog(closeTask, failInfo, failInns, "1");
                    }
                }
            } else {
                saveSuccessOperationLog(closeTask, successNum, "1");
            }
        }
    }

    @Override
    public void processOmsFailRoomOff(String content) {
        if (StringUtils.isNotBlank(content)) {
            JSONObject jsonObject = JSON.parseObject(content);
            String status = jsonObject.getString("status");
            // 获取任务ID
            Integer taskId = jsonObject.getInteger("taskId");
            CloseTask closeTask = closeTaskDao.get(taskId);
            Integer successNum = jsonObject.getInteger("successNum");
            // 如果执行失败
            if (!"1".equals(status)) {
                if (closeTask != null) {
                    String failInfoStr = jsonObject.getString("failInfo");
                    JSONObject failInfo = JSON.parseObject(failInfoStr);
                    // 失败客栈集合
                    JSONArray failInns = failInfo.getJSONArray("failInns");
                    // 获取已经执行次数
                    int executeTime = closeTask.getExecuteTime();
                    LOGGER.info("================================TASK:" + closeTask.getId() + ",第" + executeTime + "次执行");
                    if (executeTime < 4) {
                        // OMS关房
                        List<OmsInnCloseDate> omsInnCloseDates = new ArrayList<>();
                        String beginDate = closeTask.getBeginDate();
                        String endDate = closeTask.getEndDate();
                        for (Object object : failInns) {
                            JSONObject dateJson = JSON.parseObject(String.valueOf(object));
                            Integer innId = dateJson.getInteger("innId");
                            omsInnCloseDates.add(new OmsInnCloseDate(innId, beginDate, endDate));
                        }
                        updateCloseTask(closeTask);
                        batchOmsOffRoom(taskId, omsInnCloseDates);
                    } else {
                        saveOperationLog(closeTask, failInfo, failInns, "2");
                    }
                }
            } else {
                saveSuccessOperationLog(closeTask, successNum, "2");
            }
        }
    }

    /**
     * 保存批量关房全部成功日志
     * @param closeTask
     * @param successNum
     */
    private void saveSuccessOperationLog(CloseTask closeTask, Integer successNum, String type) {
        String offType = closeTask.getOffType();
        String operateObject = "";
        if(CloseTask.ALL_CLOSE.equals(offType)) {
            operateObject = "全国";
        } else if(CloseTask.AREA_CLOSE.equals(offType)) {
            Integer areaId = closeTask.getAreaId();
            if (areaId != null) {
                Area area = areaDao.selectAreaById(areaId);
                operateObject = area.getName();
            }
        } else if(CloseTask.INN_CLOSE.equals(offType)) {
            Integer innId = closeTask.getInnId();
            if (innId != null) {
                ProxyInn proxyInn = proxyInnDao.selectProxyInnByInnId(innId);
                if (proxyInn != null) {
                    operateObject = proxyInn.getInnName();
                }
            }
        }
        String contentType = "OMS关房";
        if ("1".equals(type)) {
            contentType = "分销商锁房";
        }
        String operateContent = "本次" + contentType + "全部成功，共" + successNum + "家客栈";
        // 达到最大执行次数，不在重复执行，记录操作记录
        FinanceOperationLog financeOperationLog = new FinanceOperationLog("114", operateObject, operateContent, getCurrentUserName());
        financeOperationLogDao.save(financeOperationLog);
    }

    /**
     * 保存关房执行结果的操作日志
     *
     * @param closeTask
     * @param failInfo
     * @param failInns
     * @param type 1:分销商锁房，2：OMS关房
     */
    private void saveOperationLog(CloseTask closeTask, JSONObject failInfo, JSONArray failInns, String type) {
        String offType = closeTask.getOffType();
        String operateObject = "";
        if(CloseTask.ALL_CLOSE.equals(offType)) {
            operateObject = "全国";
        } else if(CloseTask.AREA_CLOSE.equals(offType)) {
            Integer areaId = closeTask.getAreaId();
            if (areaId != null) {
                Area area = areaDao.selectAreaById(areaId);
                operateObject = area.getName();
            }
        } else if(CloseTask.INN_CLOSE.equals(offType)) {
            Integer innId = closeTask.getInnId();
            if (innId != null) {
                ProxyInn proxyInn = proxyInnDao.selectProxyInnByInnId(innId);
                if (proxyInn != null) {
                    operateObject = proxyInn.getInnName();
                }
            }
        }
        String contentType = "OMS关房";
        if ("1".equals(type)) {
            contentType = "分销商锁房";
        }
        // 失败条数
        Integer fialNum = failInfo.getInteger("fialNum");
        String operateContent = "本次" + contentType + "共失败" + fialNum + "家客栈,[" + getFailInnIdString(failInns) + "]";
        // 达到最大执行次数，不在重复执行，记录操作记录
        FinanceOperationLog financeOperationLog = new FinanceOperationLog("114", operateObject, operateContent, getCurrentUserName());
        financeOperationLogDao.save(financeOperationLog);
    }

    /**
     * 更新关房任务对象
     *
     * @param closeTask
     */
    private void updateCloseTask(CloseTask closeTask) {
        int executeTime = closeTask.getExecuteTime();
        closeTask.setExecuteTime(++executeTime);
        closeTask.setDateUpdated(new Date());
        closeTask.setModifior(getCurrentUserName());
        closeTaskDao.save(closeTask);
    }

    /**
     * 拼接请求关房失败的客栈ID集合
     *
     * @param failInns
     * @return
     */
    private String getFailInnIdString(JSONArray failInns) {
        String result = "";
        if (failInns != null) {
            for (Object object : failInns) {
                JSONObject dateJson = JSON.parseObject(String.valueOf(object));
                Integer innId = dateJson.getInteger("innId");
                result += innId + ",";
            }
        }
        return result;
    }
}
