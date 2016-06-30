package com.project.dao.bill;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.project.bean.bill.BillDetailBean;
import com.project.bean.bill.BillSearchBean;
import com.project.bean.bill.InnDetail;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.bill.TomatoInn;
import com.project.entity.inn.Inn;
import com.project.utils.ListUtil;
import com.project.utils.time.DateUtil;

/**
 * Created by wang on 2014/7/29.
 */
@Component
public class BillDao extends HibernateDao<Inn, Integer>{
    @Autowired
    private SessionFactory sf;
    
    /**
     * 获取支付记录详情列表的分页实体
     * @param param
     * @param paramMap
     * @param page
     * @return
     */
    public Page<BillDetailBean> getPayRecords(BillSearchBean param, Map<String, Object> paramMap
    		, Page<BillDetailBean> page){
    	String from = DateUtil.format(param.getStartDate()) + " 00:00:00";
    	String to = DateUtil.format(param.getEndDate()) + " 23:59:59";
    	StringBuilder sb = new StringBuilder();
    	sb.append("select i.name,i.id AS innId");
    	sb.append(",p.pay_at AS pay_at ");
    	sb.append(",a.mobile AS inncontact ");
    	sb.append(",p.id as payid,p.order_code ");
    	sb.append(",p.pay_price AS paid ");
    	sb.append(",p.pay_desc AS pay_desc ");
    	sb.append(",p.product_name AS product_name ");
    	sb.append(",case when p.is_balance = 0 then '0' else '1' end AS status ");
    	sb.append("from tomato_pay_record p ");
    	sb.append("inner join tomato_inn i on i.id = p.inn_id ");
    	sb.append("inner join tomato_inn_admin a on a.inn_id = i.id and a.parent_id is null ");
    	sb.append("where p.pay_status = '1' and p.pay_at >= '"+from+"' and p.pay_at <= '"+to+"' ");
    	sb.append("and p.receipt_type = '3' ");
    	sb.append("and p.product_code in ('xz_order', 'xz_fast_checkIn', 'checkstand') ");
    	if(param.getIsBalance() != null && param.getIsBalance() >= 0){
    		sb.append("and p.is_balance = "+param.getIsBalance()+" ");
    	}
    	if(StringUtils.isNotBlank(param.getProductCode())){
    		sb.append("and p.product_code = '"+param.getProductCode()+"' ");
    	}
    	appendSearchSql(sb, param);
    	if(param.getInnId() != null){
    		sb.append("and i.id =  ").append(param.getInnId()).append(" ");
    	}
    	page.setPageNo(param.getNowPage());
    	page.setPageSize(param.getPageSize());
    	this.setTotalCount(sb.toString(), page);
    	sb.append("order by p.pay_at desc ");
    	this.setPageResult(sb.toString(), page.getPageNo(), page);
    	getSearchCount(param, paramMap);
    	return page;
    }
    
    private void appendSearchSql(StringBuilder sb, BillSearchBean param){
    	switch(param.getSearchCondition()){
    	case 0:
    		sb.append("and i.name like '%"+param.getKeyWord()+"%' ");
    		break;
    	case 1:
    		sb.append("and ( p.order_code like '%"+param.getKeyWord()+"%' or p.main_order_no like '%"+param.getKeyWord()+"%' ) ");
    		break;
    	}
    }
    
    public Page<BillDetailBean> getXzPayRecords(BillSearchBean billSearchBean
    		, Map<String, Object> paramMap, Page<BillDetailBean> page){
    	String from = DateUtil.format(billSearchBean.getStartDate()) + " 00:00:00";
    	String to = DateUtil.format(billSearchBean.getEndDate()) + " 23:59:59";
    	StringBuilder sb = new StringBuilder();
    	sb.append("select i.name,i.id AS innId");
    	sb.append(",case when s.alipay_account is null then wg.contract_phone when s.alipay_account = '' then wg.contract_phone else s.alipay_account end AS inncontact");
    	sb.append(",m.contact,pr.id as payid,pr.order_code,m.id,m.user_real_name");
    	sb.append(",pr.pay_at");
    	sb.append(",myStringAgg_1(to_char(o.check_in_at,'MM-dd') || '至' || to_char(o.check_out_at,'MM-dd')) AS inout");
    	sb.append(",myStringAgg_1(rt.name) AS roomTypes");
    	sb.append(",pr.pay_price AS paid");
    	sb.append(",case when m.is_balance is null and pr.is_balance = 0 then '0' else '1' end AS status ");
    	sb.append("from tomato_pay_record pr ");
    	sb.append("inner join tomato_inn i on i.id = pr.inn_id ");
    	sb.append("inner join wg_inn_wei_shop s on s.inn_id = i.id ");
    	sb.append("inner join wg_inn_wei_contract wg on wg.inn_wei_shop_id = s.id ");
    	sb.append("left join tomato_inn_room_main_order m on m.ota_order_no = pr.order_code ");
    	sb.append("left join tomato_inn_room_order o on o.main_id = m.id ");
    	sb.append("left join tomato_inn_room r on r.id = o.room_id ");
    	sb.append("left join tomato_inn_room_type rt on rt.id = r.room_type_id ");
    	sb.append("where pr.pay_at >= '"+from+"' and pr.pay_at <= '"+to+"' ");
    	sb.append("and pr.pay_status = '1' ");
    	sb.append("and pr.product_code = 'xz_order' ");
    	appendSearch(billSearchBean, sb);
    	if(billSearchBean.getInnId() != null){
    		sb.append("and i.id =  ").append(billSearchBean.getInnId()).append(" ");
    	}
    	sb.append("group by i.name,i.id,wg.contract_phone,pr.id,pr.order_code,m.id,m.user_real_name");
    	sb.append(",pr.pay_at,pr.pay_price,m.is_balance,s.alipay_account,pr.is_balance ");
    	sb.append("order by pr.pay_at desc ");
    	paramMap = getOrderCount(from, to, billSearchBean.getInnId(), paramMap, billSearchBean);
    	page.setPageNo(billSearchBean.getNowPage());
    	page.setPageSize(billSearchBean.getPageSize());
    	this.setPageResult(sb.toString(), page.getPageNo(), page);
    	this.setTotalCount(sb.toString(), page);
    	return page;
    }

	private void appendSearch(BillSearchBean billSearchBean, StringBuilder sb) {
		if(billSearchBean.getSearchCondition() == 0){
    		sb.append("and i.name like '%"+billSearchBean.getKeyWord()+"%' ");
    	}else if(billSearchBean.getSearchCondition() == 1){
    		sb.append("and m.ota_order_no like '%"+billSearchBean.getKeyWord()+"%' ");
    	}else if(billSearchBean.getSearchCondition() == 2){
    		sb.append("and m.contact like '%"+billSearchBean.getKeyWord()+"%' ");
    	}
	}
	
	private void getSearchCount(BillSearchBean param, Map<String, Object> paramMap){
		String from = DateUtil.format(param.getStartDate()) + " 00:00:00";
    	String to = DateUtil.format(param.getEndDate()) + " 23:59:59";
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(p.id) as num");
    	sb.append(",COALESCE(sum(p.pay_price), 0.0) AS money ");
    	sb.append("from tomato_pay_record p ");
    	sb.append("inner join tomato_inn i on i.id = p.inn_id ");
    	sb.append("where p.pay_status = '1' and p.pay_at >= '"+from+"' and p.pay_at <= '"+to+"' ");
    	sb.append("and p.receipt_type = '3' ");
    	sb.append("and p.product_code in ('xz_order', 'xz_fast_checkIn', 'checkstand') ");
    	if(param.getIsBalance() != null && param.getIsBalance() >= 0){
    		sb.append("and p.is_balance = "+param.getIsBalance()+" ");
    	}
    	if(StringUtils.isNotBlank(param.getProductCode())){
    		sb.append("and p.product_code = '"+param.getProductCode()+"' ");
    	}
    	appendSearchSql(sb, param);
    	if(param.getInnId() != null){
    		sb.append("and i.id =  ").append(param.getInnId()).append(" ");
    	}
    	sb.append("union all ");
    	sb.append("select count(p.id) as num");
    	sb.append(",COALESCE(sum(p.pay_price), 0.0) AS money ");
    	sb.append("from tomato_pay_record p ");
    	sb.append("inner join tomato_inn i on i.id = p.inn_id ");
    	sb.append("where p.pay_status = '1' and p.pay_at >= '"+from+"' and p.pay_at <= '"+to+"' ");
    	sb.append("and p.receipt_type = '3' ");
    	sb.append("and p.product_code in ('xz_order', 'xz_fast_checkIn', 'checkstand') ");
    	if(param.getIsBalance() != null && param.getIsBalance() >= 0){
    		sb.append("and p.is_balance = "+param.getIsBalance()+" ");
    	}
    	sb.append("and p.is_balance = 0 ");
    	if(StringUtils.isNotBlank(param.getProductCode())){
    		sb.append("and p.product_code = '"+param.getProductCode()+"' ");
    	}
    	appendSearchSql(sb, param);
    	if(param.getInnId() != null){
    		sb.append("and i.id =  ").append(param.getInnId()).append(" ");
    	}
    	List<Map<String, Object>> result = Lists.newArrayList();
    	result = this.findListMapWithSql(sb.toString());
    	if(ListUtil.isNotEmpty(result)){
    		paramMap.put("orderNum", result.get(0).get("num"));
    		paramMap.put("allMoney", result.get(0).get("money"));
    		paramMap.put("unbalanceNum", result.get(1).get("num"));
    		paramMap.put("unbalanceMoney", result.get(1).get("money"));
    	}
	}
    
    public Page<BillDetailBean> getXzNoPayRecords(BillSearchBean billSearchBean
    		, Map<String, Object> paramMap, Page<BillDetailBean> page){
    	String from = DateUtil.format(billSearchBean.getStartDate()) + " 00:00:00";
    	String to = DateUtil.format(billSearchBean.getEndDate()) + " 23:59:59";
    	StringBuilder sb = new StringBuilder();
    	sb.append("select i.name,i.id AS innId");
    	sb.append(",pr.id as payid,m.contact,m.ota_order_no as order_code,m.id,m.user_real_name");
    	sb.append(",m.ordered_at as pay_at");
    	sb.append(",myStringAgg_1(to_char(o.check_in_at,'MM-dd') || '至' || to_char(o.check_out_at,'MM-dd')) AS inout");
    	sb.append(",myStringAgg_1(rt.name) AS roomTypes");
    	sb.append(",m.total_amount as paid ");
    	sb.append("from tomato_inn_room_main_order m ");
    	sb.append("left join tomato_pay_record pr on pr.order_code = m.ota_order_no ");
    	sb.append("inner join tomato_inn_room_order o on o.main_id = m.id and m.come_from = '101' ");
    	sb.append("inner join tomato_inn_room r on r.id = o.room_id ");
    	sb.append("inner join tomato_inn_room_type rt on rt.id = r.room_type_id ");
    	sb.append("inner join tomato_inn i on i.id = r.inn_id ");
    	sb.append("where pr.id is null ");
    	sb.append("and m.ordered_at >= '"+from+"' and m.ordered_at <= '"+to+"' ");
    	if(StringUtils.isNotBlank(billSearchBean.getKeyWord())){
    		appendSearch(billSearchBean, sb);
    	}
    	if(billSearchBean.getInnId() != null){
    		sb.append("and i.id =  ").append(billSearchBean.getInnId()).append(" ");
    	}
    	sb.append("group by i.name,pr.id,m.contact,m.ota_order_no,m.id,m.user_real_name,m.ordered_at,paid,pr.order_code,i.id ");
    	paramMap = getNoPayOrderCount(from, to, billSearchBean.getInnId(), paramMap, sb.toString());
    	sb.append("order by m.ordered_at desc ");
    	page.setPageNo(billSearchBean.getNowPage());
    	page.setPageSize(billSearchBean.getPageSize());
    	this.setPageResult(sb.toString(), page.getPageNo(), page);
    	this.setTotalCount(sb.toString(), page);
    	return page;
    }
    
    public Map<String, Object> getNoPayOrderCount(String from, String to
    		,Integer innId, Map<String, Object> paramMap, String sql){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(*) as num,COALESCE(sum(foo.paid), 0.0) as money from ( ");
    	sb.append(sql);
    	sb.append(" ) foo");
    	Map<String, Object> result = this.findMapWithSql(sb.toString());
    	paramMap.put("orderNum", result.get("num"));
		paramMap.put("allMoney", result.get("money"));
    	return paramMap;
    }
    
    public Map<String, Object> getOrderCount(String from, String to
    		,Integer innId, Map<String, Object> paramMap, BillSearchBean billSearchBean){
    	StringBuilder sb = new StringBuilder();
    	sb.append("select count(*) AS num,COALESCE(sum(foo.money), 0.0) AS money from ( ");
    	sb.append("select m.id,pr.id as payid,pr.pay_price AS money ");
    	sb.append("from tomato_pay_record pr ");
    	sb.append("inner join tomato_inn i on i.id = pr.inn_id ");
    	sb.append("left join tomato_inn_room_main_order m on m.ota_order_no = pr.order_code ");
    	sb.append("where pr.pay_at >= '"+from+"' and pr.pay_at <= '"+to+"' ");
    	sb.append("and pr.product_code = 'xz_order' ");
    	if(innId != null){
    		sb.append("and i.id =  ").append(innId).append(" ");
    	}
    	appendSearch(billSearchBean, sb);
    	sb.append(") foo ");
    	sb.append("UNION ALL ");
    	sb.append("select count(*) AS num,COALESCE(sum(foo.money), 0.0) AS money from ( ");
    	sb.append("select m.id,pr.id as payid,pr.pay_price AS money ");
    	sb.append("from tomato_pay_record pr ");
    	sb.append("inner join tomato_inn i on i.id = pr.inn_id ");
    	sb.append("inner join tomato_inn_room_main_order m on m.ota_order_no = pr.order_code and m.is_balance is null ");
    	sb.append("where pr.pay_at >= '"+from+"' and pr.pay_at <= '"+to+"' ");
    	sb.append("and pr.product_code = 'xz_order' ");
    	if(innId != null){
    		sb.append("and i.id =  ").append(innId).append(" ");
    	}
    	appendSearch(billSearchBean, sb);
    	sb.append(") foo ");
    	sb.append("UNION ALL ");
    	sb.append("select count(*) AS num,COALESCE(sum(foo.money), 0.0) AS money from ( ");
    	sb.append("select m.id,pr.id as payid,pr.pay_price AS money ");
    	sb.append("from tomato_pay_record pr ");
    	sb.append("inner join tomato_inn i on i.id = pr.inn_id ");
    	sb.append("left join tomato_inn_room_main_order m on m.ota_order_no = pr.order_code ");
    	sb.append("where pr.pay_at >= '"+from+"' and pr.pay_at <= '"+to+"' ");
    	sb.append("and pr.product_code = 'xz_order' ");
    	sb.append("and pr.is_balance = 0 and m.id is null ");
    	if(innId != null){
    		sb.append("and i.id =  ").append(innId).append(" ");
    	}
    	appendSearch(billSearchBean, sb);
    	sb.append(") foo ");
    	List<Map<String, Object>> result = Lists.newArrayList();
    	result = this.findListMapWithSql(sb.toString());
    	if(ListUtil.isNotEmpty(result)){
    		paramMap.put("orderNum", result.get(0).get("num"));
    		paramMap.put("allMoney", result.get(0).get("money"));
    		BigInteger num0 = (BigInteger) result.get(1).get("num");
    		BigInteger num1 = (BigInteger) result.get(2).get("num");
    		Double money = (Double) result.get(1).get("money") + (Double) result.get(2).get("money");
    		paramMap.put("unbalanceNum", num0.longValue() + num1.longValue());
    		paramMap.put("unbalanceMoney", money);
    	}
    	return paramMap;
    }
    
    public List<BillDetailBean> rows2Obj(List<Map<String, Object>> rows) {
    	List<BillDetailBean> list = Lists.newArrayList();
    	if(ListUtil.isNotEmpty(rows)){
    		for (Map<String, Object> row : rows) {
    			BillDetailBean bean = new BillDetailBean();
    			bean.setContact((String) row.get("contact"));
    			bean.setInnId((Integer) row.get("innid"));
    			bean.setInnName((String) row.get("name"));
    			bean.setIsBalance((String) row.get("status"));
    			bean.setOrderNo((String) row.get("order_code"));
    			bean.setPayId((Integer) row.get("payid"));
    			bean.setPayAt((Date) row.get("pay_at"));
    			bean.setName((String) row.get("user_real_name"));
    			bean.setTotalAmount((Double) row.get("paid"));
    			String inouts = (String) row.get("inout");
    			if(StringUtils.isNoneBlank(inouts)){
    				inouts = inouts.replace(",", "<br/><hr style='border:1;margin: 0'>");
    			}
    			bean.setInouts(inouts);
    			String roomTypes = (String) row.get("roomtypes");
    			if(StringUtils.isNoneBlank(roomTypes)){
    				roomTypes = roomTypes.replace(",", "<br/><hr style='border:1;margin: 0'>");
    			}
    			
    			bean.setOrderInfos((String) row.get("pay_desc"));
    			bean.setProductName((String) row.get("product_name"));
    			bean.setRoomTypes(roomTypes);
    			bean.setZFBName((String) row.get("inncontact"));
    			list.add(bean);
			}
    	}
		return list;
	}
    

    /**
     * 获取小站订单的支付记录的支付时间
     *
     * @return
     */
    public Date getXZPayRecordsPayAtByOrderCode(String orderCode) {
        //打开一个Session连接
        Session session = sf.getCurrentSession();
        Date payAt = (Date) session.createQuery("select payRecord.payAt from TomatoPayRecord payRecord where payRecord.orderCode = ?")
                .setString(0, orderCode)
                .uniqueResult();
        return payAt;
    }

    /**
     * 获取小站订单的支付的金额
     *
     * @param orderCode
     * @return
     */
    public Double getXZPayRecordsPriceByOrderCode(String orderCode) {
        Session session = sf.getCurrentSession();
        Double payPrice = (Double) session.createQuery("select payRecord.payPrice from TomatoPayRecord payRecord where payRecord.orderCode = ?")
                .setString(0, orderCode)
                .uniqueResult();
        return payPrice;
    }

    /**
     * 根据已经付款的订单编号获取客栈
     *
     * @param otaOrderNo
     * @return
     */
    public TomatoInn getInnByOtaOrderNo(String otaOrderNo) {
        //打开一个Session连接
        Session session = sf.getCurrentSession();
        //获取客栈名
        TomatoInn inn = (TomatoInn) session.createQuery("select inn from TomatoInn as inn,TomatoPayRecord as record where record.orderCode=? and inn.id=record.innId").setString(0, otaOrderNo).uniqueResult();
        return inn;
    }

    /**
     * 根据房型ID获取房型名称
     *
     * @param roomTypeId
     * @return
     */
    public String getRoomTypeNameByRoomTypeId(Integer roomTypeId) {
        //打开一个Session连接
        Session session = sf.getCurrentSession();
        //获取房型名称
        String roomTypeName = session.createQuery("select name from TomatoInnRoomType as roomType where roomType.id = ?").setInteger(0, roomTypeId).uniqueResult().toString();
        return roomTypeName;
    }

    /**
     * 根据主订单的ID把IsBalance设置为1
     *
     * @param mainOrderId
     */
    public void updateIsBalance(String code) {
        Session session = sf.getCurrentSession();
        session.createQuery("update TomatoInnRoomMainOrder mainOrder set mainOrder.isBalance=1 where mainOrder.otaOrderNo=?").setString(0, code).executeUpdate();
    }

    /**
     * 根据客栈ID获取支付宝
     *
     * @param innId
     * @return
     */
    public String getZFBNameByInnId(Integer innId) {
        Session session = sf.getCurrentSession();
        Query q = session.createSQLQuery("select alipay_account_agent from wg_inn_wei_shop where inn_id=?").setInteger(0, innId);
        String ZFBName = "";
        if (q.uniqueResult() != null) {
            ZFBName = (String) q.uniqueResult();
        }
        return ZFBName;
    }

    /**
     * 根据客栈Id获取InnDetail
     *
     * @param innId
     * @return
     */
    public InnDetail getInnDetailByInnId(Integer innId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT i.id,i.name,i.bank_name,i.bank_account,i.bank_code");
        sqlBuilder.append(",a.mobile,i.alipay_code as ali,i.bank_region");
        sqlBuilder.append(",i.alipay_code,i.alipay_user,i.tenpay_code,i.tenpay_user ");
        sqlBuilder.append("FROM tomato_inn i ");
        sqlBuilder.append("INNER JOIN tomato_inn_admin a ON a.inn_id = i.id AND a.parent_id is null ");
        sqlBuilder.append("WHERE i.id = ? ");
        Map<String, Object> result = this.findMapWithSql(sqlBuilder.toString(), innId);
        InnDetail innDetail = new InnDetail();
        if(result != null){
        	innDetail.setInnName((String) result.get("name"));
        	innDetail.setBankName((String) result.get("bank_name"));
        	innDetail.setBankCardHolder((String) result.get("bank_account"));
        	innDetail.setBankCard((String) result.get("bank_code"));
        	innDetail.setContact((String) result.get("mobile"));
//        	innDetail.setContactName((String) result.get("contract_person"));
        	innDetail.setZFBName((String) result.get("ali"));
        	innDetail.setBankArea((String) result.get("bank_region"));
        	innDetail.setAlipayCode((String) result.get("alipay_code"));
        	innDetail.setAlipayUser((String) result.get("alipay_user"));
        	innDetail.setTenpayCode((String) result.get("tenpay_code"));
        	innDetail.setTenpayUser((String) result.get("tenpay_user"));
        }
        return innDetail;
    }

    /**
     * 根据Key获取银行名称
     *
     * @param key
     * @return
     */
    public String getBankName(Integer key) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT\n" +
                "tomato_sys_dictionary.connam\n" +
                "FROM\n" +
                "tomato_sys_dictionary\n" +
                "WHERE\n" +
                "tomato_sys_dictionary.contyp='bank_type'\n");
        Session session = sf.getCurrentSession();
        Query q = session.createSQLQuery(sqlBuilder.append("\nAND tomato_sys_dictionary.conval=?").toString()).setString(0, key.toString());
        if (null != q.uniqueResult())
            return q.uniqueResult().toString();
        else
            return null;
    }
    //获取子订单的价格
    public List<Double> getOrderRoomPriceByMainOrderId(Integer mainOrderId){
        Session session = sf.getCurrentSession();
        String sql="SELECT\n" +
                "tomato_inn_room_order.book_room_price\n" +
                "FROM\n" +
                "tomato_inn_room_main_order\n" +
                "INNER JOIN tomato_inn_room_order ON tomato_inn_room_order.main_id = tomato_inn_room_main_order.id \n" +
                "WHERE tomato_inn_room_order.main_id=? \n"+
                "AND tomato_inn_room_order.status!=?";
        SQLQuery sqlQuery=session.createSQLQuery(sql);
        List<Double> orders= sqlQuery.setInteger(0,mainOrderId).setInteger(1,Constants.ORDER_STATUS_CANCEL).list();
        return orders;
    }

	public Page<Map<String, Object>> getFastCheckPayRecords(BillSearchBean billSearchBean, Page<Map<String, Object>> page) {
		String from = DateUtil.format(billSearchBean.getStartDate()) + " 00:00:00";
    	String to = DateUtil.format(billSearchBean.getEndDate()) + " 23:59:59";
    	StringBuilder sb = new StringBuilder();
    	sb.append("select i.name,i.id AS innid,a.mobile");
    	sb.append(",pr.id as payid,pr.order_code,pr.pay_at,pr.pay_price");
    	sb.append(",case when pr.is_balance = 0 then '0' else '1' end AS pay_status ");
    	sb.append("from tomato_pay_record pr ");
    	sb.append("inner join tomato_inn i on i.id = pr.inn_id ");
    	sb.append("inner join tomato_inn_admin a on i.id = a.inn_id ");
    	sb.append("where a.parent_id is null and pr.pay_at >= '"+from+"' and pr.pay_at <= '"+to+"' ");
    	sb.append("and pr.pay_status = '1' ");
    	sb.append("and pr.product_code = '").append(billSearchBean.getType()).append("' ");
    	if(billSearchBean.getSearchCondition() == 0){
    		sb.append("and i.name like '%"+billSearchBean.getKeyWord()+"%' ");
    	}else if(billSearchBean.getSearchCondition() == 1){
    		sb.append("and pr.order_code like '%"+billSearchBean.getKeyWord()+"%' ");
    	}else if(billSearchBean.getSearchCondition() == 2){
    		sb.append("and a.mobile like '%"+billSearchBean.getKeyWord()+"%' ");
    	}
    	if(billSearchBean.getInnId() != null){
    		sb.append("and i.id =  ").append(billSearchBean.getInnId()).append(" ");
    	}
    	sb.append("order by pr.pay_at desc ");
    	page.setPageNo(billSearchBean.getNowPage());
    	page = this.findListMapPageWithSql(page, sb.toString());
    	return page;
	}
}
