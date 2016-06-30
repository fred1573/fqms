package com.project.common;

import com.project.utils.ResourceBundleUtil;


/**
 * @author mowei 
 * 系统常量存放位置
 */
public class Constants {

	//验证码
	public static final String SESSION_CAPTCHA = "session.captcha_";
	
	//注册手机以及手机找回密码session
	public static final String SESSION_MOBILE = "session.mobile_";
	
	//注册时用户实体session
	public static final String SESSION_MEMBER = "session.member_";
	
	//用户名称session
	public static final String SESSION_USER_NAME = "session.user.name_";
	
	//防止篡改form的随机数的session
	public static final String SESSION_FORM_RAMDOM = "session.form.ramdom_";
	
	//form随机数参数名称
	public static final String FORM_RAMDOM_PARAMETER = "_LHLYJGKH_";
		
	//条件“与”
	public static final String RELATION_AND = "and";

	//条件“或”
	public static final String RELATION_OR = "or";
	
	//上传文件类型
	public static final String ATTACHMENT_TYPE_IMAGE = "image";
	public static final String ATTACHMENT_TYPE_DOCUMENT = "document";
	public static final String ATTACHMENT_TYPE_OTHER = "other";
	
	// 数据类型 
	public static final String DATA_TYPE_FLOAT = "float";
	public static final String DATA_TYPE_DOUBLE = "double";
	public static final String DATA_TYPE_STRING = "string";
	public static final String DATA_TYPE_DATE = "date";
	public static final String DATA_TYPE_DATE_TIME = "datetime";
	public static final String DATA_TYPE_XML = "xml";
	public static final String DATA_TYPE_JSON = "json";
	
	//ajax返回key值类型
	public final static String STATUS = "status";
	public final static String ERRORS = "errors";
	public final static String RESULT = "result";
	
	public final static int HTTP_OK = 200;
	public final static int HTTP_400 = 400;
	public final static int HTTP_401 = 401;
	public final static int HTTP_500 = 500;
	
    //时间轴日期
	public final static String[] CHINESE_WEEK_DAYS = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	
	
	// 性别标志：性别:0、女 1、男
	public static final String SEX_FLAG_WOMEN = "0";
	public static final String SEX_FLAG_MEN = "1";
	
	// 用户类型：用户类型（0：系统，1：后台注册用户）
	public static final String USER_TYPE_SYS = "0";
	public static final String USER_TYPE_MEMBER = "1";
	
	//日志类型（0：登陆 1：登出 2：用户管理 3：角色管理 4：权限管理 ）
	public static final String LOG_TYPE_LOGIN = "0";
	public static final String LOG_TYPE_LOGOUT = "1";
	public static final String LOG_TYPE_USER = "2";
	public static final String LOG_TYPE_ROLE = "3";
	public static final String LOG_TYPE_AUTH = "4";
	/** 日志类型：退房 */
	public static final int LOG_TYPE_CHECK_OUT = 5;
	/** 日志类型：房间管理 */
	public static final int LOG_TYPE_ROOM = 6;
	/** 日志类型：锁定屏幕 */
	public static final int LOG_TYPE_LOCK = 7;
	/** 日志类型：客栈账本 */
	public static final int LOG_TYPE_MONEY_IN_OUT = 8;
	/** 日志类型：通知 */
	public static final int LOG_TYPE_ADVICE = 9;
	/** 日志类型：换房 */
	public static final int LOG_TYPE_MOVE_ORDER = 10;
	/** 日志类型：客人资料 */
	public static final int LOG_TYPE_CUSTOMER_INFO = 11;
	/** 日志类型：财务记录 */
	public static final int LOG_TYPE_FINANCE = 12;
	
	//用户初始密码
	public static final String INIT_PASSWORD = "000000";
	
	//超级管理员用户id/角色id/菜单id/
	public static final long SUPERVISOR_ID = 1;
	
	//未登录时的用户名
	public static final String USER_NAME_NOT_LOGIN = "anonymousUser";
	
	//角色类型 会员角色：ROLE_MEMBER 后台用户角色：ROLE_USER 未登录角色：ROLE_ANONYMOUS
	public static final String PORTAL_USER_ROLE_MEMBER = "ROLE_MEMBER";
	public static final String PORTAL_USER_ROLE_USER = "ROLE_USER";
	public static final String PORTAL_USER_ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	
	// 系统 servlet命令标示
	public static final String SYSTEM_SERVLET_COMMAND = "_COMMAND_";
	
	//返回标示（0：失败，1：成功）
	public static final String RETURN_STATUS_FAIL = "0";
	public static final String RETURN_STATUS_SUCCESS = "1";	
		
	//cookies有效期
	public static final Integer COOKIES_VALID_TIME = 60*60*24*31;
	
	// 缓存key标示（所有客栈：_ALL_INN_,所有区域：_ALL_REGION_,所有权限：_ALL_AUTHORITY_）
	public static final String CACHE_FLAG_ALL_INN = "_ALL_INN_";
	public static final String CACHE_FLAG_ALL_REGION = "_ALL_REGION_";
	public static final String CACHE_FLAG_ALL_AUTHORITY = "_ALL_AUTHORITY_";
	public static final String CACHE_FLAG_ALL_CHANNEL = "_ALL_CHANNEL_";
	public static final String CACHE_FLAG_WG_ROOM_TYPE = "_WG_ROOM_TYPE_";
	
	/**
	 * 权限key值
	 * "wait_operate";"今日代办" "customer_info";"客户资料" "reset_pwd";"重置密码"
	 * "inn_check";"客栈审核" "user_manager";"成员管理" "role_manager";"部门管理"
	 * "inn_notice";"客栈通知" "active_report";"活跃报表"
	 */
	public static final String AUTHORITY_KEY_WAIT_OPERATE = "wait_operate";
	public static final String AUTHORITY_KEY_CUSTOMER_INFO = "customer_info";
	public static final String AUTHORITY_KEY_RESET_PWD = "reset_pwd";
	public static final String AUTHORITY_KEY_INN_CHECK = "inn_check";
	public static final String AUTHORITY_KEY_USER_MANAGER = "user_manager";
	public static final String AUTHORITY_KEY_ROLE_MANAGER = "role_manager";
	public static final String AUTHORITY_KEY_INN_NOTICE = "inn_notice";
	public static final String AUTHORITY_KEY_ACTIVE_REPORT = "active_report";
	public static final String AUTHORITY_KEY_API_SALE = "api_sale_manager";
	public static final String AUTHORITY_KEY_ARTICLE_MANAGER = "article_manager";
	
	// 客栈账号状态(1：待审核、2：已审核、3：已拒绝、4：已删除)
	public static final int MEMBER_STATE_CHECKPENDING = 1;
	public static final int MEMBER_STATE_AUDITED = 2;
	public static final int MEMBER_STATE_CANCELED = 3;
	public static final int MEMBER_STATE_DELETED = 4;
	
	/** 标识当前客栈的房态样式的key，值为：1[默认房态坐标]，2[xy的房态],4[excel房态] */
	public static final String ROOM_STATUS_STYLE_DEFAULT = "1";
	public static final String ROOM_STATUS_STYLE_XY = "2";
	public static final String ROOM_STATUS_STYLE_EXCEL = "4";
	
	/** 报表统计中零散的功能 */
	/** 锁房 */
	public static final int REPORT_ITEM_TYPE_LOCK = 10001;
	/** 通知 */
	public static final int REPORT_ITEM_TYPE_NOTICE = 10002;
	/** 房型排序 */
	public static final int REPORT_ITEM_TYPE_ROOM_SORT = 10003;
	/** 活跃用户 */
	public static final int REPORT_ITEM_TYPE_ACTIVE = 10004;
	/** 连锁运营 */
	public static final int REPORT_ITEM_TYPE_LINK_INN = 10005;
	
	public static final int REPORT_DETAIL_TYPE_PLUG_STAMP = 1;
	public static final int REPORT_DETAIL_TYPE_XZ = 2;
	
	public static final int REPORT_DETAIL_PAGE_SIZE = 15;
	
	// 报表 截取精度  保留小数点的位数
	public final static int REPORT_MONEY_ACCURATE_LENGTH = 1;
	public final static int REPORT_RATIO_ACCURATE_LENGTH = 2;
	
	/** 短信发送方法返回值 0：没有定义智能发送模板 */
	public static final int MSG_SEND_NO_AUTO = 0;
	/** 短信发送方法返回值 1：没有可用的短信余额 */
	public static final int MSG_SEND_NO_NUMS = 1;
	/** 短信发送方法返回值 2：短信发送成功 */
	public static final int MSG_SEND_OK = 2;
	/** 短信发送方法返回值 3：短信发送失败 （短信平台错误） */
	public static final int MSG_SEND_ERORR = 3;
	
	// http获取响应类型(all:所有，responseStr:网页字符串，cookies：网页cookies)
	public final static String HTTP_GET_TYPE_ALL = "all";
	public final static String HTTP_GET_TYPE_STRING = "responseStr";
	public final static String HTTP_GET_TYPE_COOKIES = "cookies";
	
	public static final int MSGLOG_FLAG_UP = 1;
	public static final int MSGLOG_FLAG_DOWN = 2;
	
	// 短信是否发送成功： 0：否 1：是
	public static final int MSG_SEND_YES = 1;
	public static final int MSG_SEND_NO = 0;
	
	/** mobile */
	public final static String MOBILE = "mobile";

	public static int SYS_RESOURCE_SUPER_INN = 2;
	
	public static final String REGISTER_TITLE = "客栈注册通知";
	
	public static final String SYS_RESOURCE_REPORT_TEMP_PATH = ResourceBundleUtil.getString("export.Report.rootPath");
	public static final String RESOURCE_SERVER_ROOT_PATH = ResourceBundleUtil.getString("resource.server.rootPath");
	public static final String SYS_RESOURCE_REPORT_BOOK_ACTIVE_PATH = ResourceBundleUtil.getString("template.bookActiveReport.rootPath");
	public static final String SYS_RESOURCE_REPORT_CHECK_IN_ACTIVE_PATH = ResourceBundleUtil.getString("template.checkActiveReport.rootPath");
	public static final String SYS_WEIXIN_MSG_URL = ResourceBundleUtil.getString("weixin.msg.server.url");

	/** 加入库存 */
	public static final String INN_JOIN_MARKET = "1";
	/** 移出库存 */
	public static final String INN_NOT_JOIN_MARKET = "0";
	
	public static final String ORDER_IS_BALANCE = "1";
	
	public static final String ORDER_IS_NOT_BALANCE = "0";
	
	public static final String SYS_RESOURCE_IP = ResourceBundleUtil.getString("sys.resource.ip");

    // 预定状态(0已失效 1预订状态 2 预订用户已入住(已完成) 3已取消)
    public final static int ORDER_STATUS_FAILURE = 0;
    public final static int ORDER_STATUS_ORDERED = 1;
    public final static int ORDER_STATUS_CHECKED = 2;
    public final static int ORDER_STATUS_CANCEL = 3;

	/** 财务结算默认开始时间 */
	public static final String FINANCE_BEGIN_TIME = "2015-09";

	/** oms中代销平台ID */
	public static final String OMS_PROXY_PID = "102";
	/** oms中信用住平台ID */
	public static final String OMS_PROXY_CREDIT_PID = "106";
	/** oms中代销平台代号 */
	public static final String OMS_DX_NAME = "DX";
	/** oms中代销平台密码 */
	public static final String OMS_DX_PWD = "dx43646";
	/** oms中信用住代号 */
	public static final String OMS_XYZ_NAME = "XYZ";
	/** oms中信用住密码 */
	public static final String OMS_XYZ_PWD = "xyz106";

	/** 渠道策略----精品 */
	public static final String PRICE_STRATEGY_BASE = "DI";
	/** 渠道策略----卖价 */
	public static final String PRICE_STRATEGY_SALE = "MAI";
	/** 渠道策略----卖转底价 */
	public static final String PRICE_STRATEGY_SALE_BASE = "MAI2DI";
	/** 抓取当前月订单截止时间*/
	public  static  final  String GRAB_FINANCEORDER_END_TIME="9999-12-31";
	/** 客栈确认账单小时数*/
	public static  final Integer CONFIRM_HOURS = 24;
	
	/** FQMS 缓存 start */
	public final static String FQMS_CACHE_PROVINCE = "_province_";
	public final static String FQMS_CACHE_CITY = "_city_";
	public final static String FQMS_CACHE_SYS = "SysConstantCache";
	/** FQMS 缓存 end   */
	
	/** PMS 缓存 start */
	// 和用户绑定的缓存
    public final static String CACHE_USER_ADMIN = "_user_admin_";
    public final static String CACHE_ADMIN_AUTHORITY = "_admin_authority_";
    // 和用户以及客栈绑定的缓存
    public final static String CACHE_ADMIN_INN_AUTHORITY = "_admin_inn_authority_";
    // 和客栈绑定的缓存
    public final static String CACHE_INN_INFO = "_inn_info_";
    public final static String CACHE_INN_ATTACH = "_inn_customer_attach_";
    public final static String CACHE_INN_CUSTOMER_FROM = "_inn_customer_from_";
    public final static String CACHE_INN_PAY_CHANNEL = "_inn_pay_channel_";
    public final static String CACHE_INN_ROOMS = "_inn_room_";
    public final static String CACHE_INN_ROOM_TYPE = "_inn_room_type_";
    public final static String CACHE_INN_GOOD = "_inn_good_";
    public final static String CACHE_INN_BOOK = "_inn_book_";
    public final static String CACHE_INN_ROOM_CLOSE = "_inn_room_close_";
    public final static String CACHE_INN_ROOM_CLOSE_SAME_FLAG = "_inn_room_close_same_flag_";
    public final static String CACHE_INN_ROOM_TYPE_PRICE = "_inn_room_type_price_";
    public final static String CACHE_INN_ROOM_TYPE_PRICE_SAME_FLAG = "_inn_room_type_price_same_flag_";
    public final static String CACHE_INN_ROOM_WEEK_PRICE = "_inn_room_week_price_";
    public final static String CACHE_INN_CHANNEL_COMMISSION = "_inn_channel_commission_";
    public final static String CACHE_MSG_ATUO = "_msg_auto_";
    public final static String CACHE_MSG_TEMPLATE = "_msg_template_";
    public final static String CACHE_INN_PRINT_TEMPLATE = "_inn_print_template_";
    /** PMS 缓存 end */


	/** 消息服务事件来源：fqms */
	public final static String MQ_PROJECT_IDENTIFICATION = "fqms";
	public final static String MQ_EVENT_ROOM_STATUS_SWITCH = "ROOM_STATUS_SWITCH";
	public final static String MQ_EVENT_BIZTYPE_PROXY_INN_STATUS = "proxy_inn_status";
	public final static String MQ_EVENT_BIZTYPE_PROXY_INN_ONSHELF = "proxy_inn_onshelf";


	public final static String TOMS_REDIS_QUEUE_KEY = "TOMS_INN_UP_DOWN";

	public final static int DEFAULT_PWD_LENGTH = 6;
}