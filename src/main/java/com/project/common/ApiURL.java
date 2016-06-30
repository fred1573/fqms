package com.project.common;

/**
 * 接口调用常量
 *
 * @author Hunhun
 *         2015-11-06 17:52
 */
public class ApiURL {

    /**
     * *************************************************
     *  ************     小站相关URL     **************
     * *************************************************
     */
    /**  */
    public static final String XZ_LEADIN_QQ = "Home/InterfaceService/meiyouDataPage";
    /**  */
    public static final String XZ_LEADIN_SEARCH = "Home/interfaceService/meiyouDataSelect";
    /**
     * 查询权重
     */
    public static final String XZ_LEADIN_RANKING = "Home/InterfaceService/queryWeight";
    /**  */
    public static final String XZ_LEADIN_UPDATE = "Home/InterfaceService/meiyouDataUpdate";

    /**
     * *************************************************
     *  ************     PMS相关URL     **************
     * *************************************************
     */
    /**
     * 获取客栈详情
     */
    public static final String PMS_INN_INFO = "api/client/getBaseInfo/";

    /**
     * *************************************************
     *  ************     OMS相关URL     **************
     * *************************************************
     */
    /**
     * 获取ota信息
     */
    public static final String OMS_OTA_INFO = "proxy/otaInfo";
    /**
     * 获取子分销商信息
     */
    public static final String OMS_CHILD_OTA = "api/greenQueryOtaChild";
    /**
     * 获取房型
     */
    public static final String OMS_ROOM_TYPE = "api/getRoomType";
    /**
     * 查询代销订单
     */
    public static final String OMS_QUERY_ORDER = "api/greenOrder";
    /**
     * 导出代销订单Excel
     */
    public static final String OMS_QUERY_ALL_ORDER = "api/greenAllOrder";
    /**
     * 查询直连订单
     */
    public static final String OMS_QUERY_ORDER_OTA = "api/greenQueryOta";
    /**
     * 分销商批量锁房接口
     */
    public static final String CHANNEL_OFF_ROOM = "api/hotel/special";
    public static final String OMS_OFF_ROOM = "api/inn/specialDateCloseInn";

    /**
     * OMS抓取订单接口
     */
    public static final String OMS_FINANCE_ORDER = "api/newGreenOrder";
    /**
     * 查询价格审核列表
     */
    public static final String OMS_PRICE_LIST = "price/find_price_record_list";
    /**
     * 更新价格审核单
     */
    public static final String OMS_PRICE_CHECKOUT = "price/update_price_record";
    /**
     * 查询最后一条价格审核单
     */
    public static final String OMS_PRICE_LAST = "price/find_last_price_record";
    /**
     * 代销公司比例与OMS同步
     */
    public static final String OMS_OTA_COMMISSION = "api/proxy/updateCompanyInfo";
    /**
     * 删除代销客栈
     */
    public static final String OMS_INN_DEL = "account/removeAccount";
    /**
     * 同步OMS上下架客栈
     */
    public static final String OMS_UPDATE_INN_SELL_STATUS = "api/updateInnSellStatus";
    /**
     * 渠道房态信息
     */
    public static final String OMS_QUERY_ROOM_STATUS = "api/fqms/queryRoomStatus";
    /**
     * 渠道房型加减价设置
     */
    public static final String OMS_SET_EXTRA_PRICE = "api/fqms/setExtraPrice";
    /**
     * 取消订单
     */
    public static final String OMS_CANCEL_ORDER = "api/cancelOrderByFq";
    /**
     * 获取客栈房型
     */
    public static final String OMS_INN_ROOM_TYPE = "web/getOtaRoomType";
    /**
     * 下架房型
     */
    public static final String DOWN_ROOM_TYPE = "tomatoOmsOtaRoomtype/downRoom";

    /**
     * *************************************************
     *  ************     CRM相关URL     **************
     * *************************************************
     */
    /**
     * 获取代销合同列表
     */
    public static final String CRM_CONTRACT_LIST = "api/Internal/queryCheckList";
    /**
     * 获取合同图片
     */
    public static final String CRM_CONTRACT_IMAGE = "api/Internal/queryContractList";
    /**
     * 更新crm合同
     */
    public static final String CRM_AUDIT = "api/Internal/updateInfo";
    /**
     * 更新ota链接
     */
    public static final String CRM_OTA_LINK = "api/proxy/updateOtaLink";
    /**  */
    public static final String CRM_SIGN_MANAGER_INFO = "api/Internal/querySignManager";
    /**
     * 删除客栈调CRM接口
     */
//    public static final String CRM_INN_DEL = "api/Internal/DeleteByPmsInnId";
    public static final String CRM_INN_DEL = "api/Internal/DeleteCompelPmsInnId";

    /**
     * *************************************************
     *  ************     TOMS相关URL     **************
     * *************************************************
     */
    /**
     * 代销公司比例与TOMS同步
     */
    public static final String TOMS_OTA_COMMISSION = "api/commission/update.json";

    /**
     * 图片上传
     */
    public static final String IMG_UPLOAD = "upload/img";

    /**
     * 图片上传临时文件目录
     */
    public final static String IMG_TEMP_FOLDER = "imgTempFolder";
    public final static String IMG_FTP_FOLDER = "imgFtpFolder";

    /**
     * 批量调价
     */
    public final static String BATCH_UPDATE_PRICE = "api/fqms/saveInnsExtraPrice";


}
