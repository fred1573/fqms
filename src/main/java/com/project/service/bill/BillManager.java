package com.project.service.bill;

import com.project.bean.bill.*;
import com.project.core.orm.Page;
import com.project.dao.bill.BillDao;
import com.project.utils.time.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by xiamaoxuan on 2014/7/29.
 */
@Component
@Transactional
public class BillManager {

	@Autowired
	private BillDao billDao;


	public Page<BillDetailBean> getXzPayRecords(BillSearchBean billSearchBean
			, Map<String, Object> paramMap, Page<BillDetailBean> page){
		return billDao.getXzPayRecords(billSearchBean, paramMap, page);
	}

	public Page<BillDetailBean> getPayRecords(BillSearchBean billSearchBean
			, Map<String, Object> paramMap, Page<BillDetailBean> page){
		return billDao.getPayRecords(billSearchBean, paramMap, page);
	}

	public Page<BillDetailBean> getXzNoPayRecords(BillSearchBean billSearchBean
			, Map<String, Object> paramMap, Page<BillDetailBean> page){
		return billDao.getXzNoPayRecords(billSearchBean, paramMap, page);
	}

	public Page<Map<String, Object>> getFastCheckPayRecords(BillSearchBean billSearchBean, Page<Map<String, Object>> page) {
		return billDao.getFastCheckPayRecords(billSearchBean, page);
	}

	/**
	 * 根据主订单的ID来确认主订单
	 *
	 * @param code
	 */
	public void changeIsBalance(String code) {
		billDao.updateIsBalance(code);
	}

	/**
	 * 获取InnDetail
	 *
	 * @param innId
	 * @return
	 */
	public InnDetail getInnDetail(Integer innId) {
		return billDao.getInnDetailByInnId(innId);
	}

	public void setPayRecBalance(int id, String code) {
		String sql = "update tomato_pay_record set is_balance = 1 where id = ? and order_code = ? ";
		this.billDao.createSqlQuery(sql).setInteger(0, id).setString(1, code).executeUpdate();
	}

	/**
	 * 获取分页后的客栈ids
	 * @param billSearchBean
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map<String, Object>> getFinancialAccountInnIds(BillSearchBean billSearchBean, Page<Map<String, Object>> page) {
		StringBuilder sb = new StringBuilder();
		String from = DateUtil.format(billSearchBean.getStartDate())+ " 00:00:00";
		String to = DateUtil.format(billSearchBean.getEndDate())+ " 23:59:59";
		sb.append("select t.id as id,sum(t.payPrice) ");
		sb.append("from ( ");
		sb.append("select p.inn_id as id");
		sb.append(",case when p.product_code = 'xz_order' then p.order_code else p.main_order_no end as orderNo ");
		sb.append(",p.pay_price as payPrice ");
		sb.append("from tomato_pay_record p  ");
		sb.append("inner join tomato_inn i on i.id = p.inn_id ");
		sb.append("where p.pay_at >= '"+from+"' and p.pay_at <= '"+to+"' ");
		sb.append(" and p.pay_status = '1' ");
		if(StringUtils.isNoneBlank(billSearchBean.getKeyWord())){
			switch(billSearchBean.getSearchCondition()){
				case 0:
					sb.append("and i.name like '%"+billSearchBean.getKeyWord()+"%' ");
					break;
				case 1:
					sb.append("and case when p.product_code = 'xz_order' then p.order_code else p.main_order_no end like '%"+billSearchBean.getKeyWord()+"%' ");
					break;
			}
		}
		if(StringUtils.isNotBlank(billSearchBean.getPayMode()) && Integer.parseInt(billSearchBean.getPayMode()) > 0){
			sb.append("and p.pay_mode = '"+billSearchBean.getPayMode()+"' ");
		}
		if(billSearchBean.getIsBalance() != null && billSearchBean.getIsBalance() >= 0){
			sb.append("and p.is_balance = "+billSearchBean.getIsBalance()+" ");
		}
		sb.append("and p.product_code IN ('xz_order','xz_fast_checkIn','checkstand') ");
		sb.append(") t ");
		sb.append("group by t.id ");
		sb.append("order by t.id desc ");
		page.setPageNo(billSearchBean.getNowPage());
		page = billDao.findListMapPageWithSql(page, sb.toString());
		sb.delete(0, sb.length());
		List<Map<String, Object>> result = (List<Map<String, Object>>) page.getResult();
		for (Map<String, Object> map : result) {
			sb.append(map.get("id")).append(",");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		page.setIds(sb.toString());
		return page;
	}

	public List<Map<String, Object>> getFinancialAccounts(String innIds,
														  BillSearchBean billSearchBean) {
		if(StringUtils.isBlank(innIds)){
			return null;
		}
		String from = DateUtil.format(billSearchBean.getStartDate())+ " 00:00:00";
		String to = DateUtil.format(billSearchBean.getEndDate())+ " 23:59:59";
		StringBuilder sb = new StringBuilder();
		sb.append("select i.id,i.name as innName");
		sb.append(",i.contact  as contacts");
		sb.append(",i.alipay_code || '/' || i.alipay_user as aliAccount");
		sb.append(",i.tenpay_code || '/' || i.tenpay_user as tenpayAccount");
		sb.append(",case when i.bank_type = 1 then '个人:' || COALESCE(i.bank_code,'暂无') || '/' || COALESCE(i.bank_account,'暂无') || '\n' || COALESCE(i.bank_name,'暂无') || '(' || COALESCE(i.bank_province,'暂无') || '/' || COALESCE(i.bank_city,'暂无') || '/' || COALESCE(i.bank_region,'暂无') || ')' ");
		sb.append("when i.bank_type = 2 then '公司:' || COALESCE(i.bank_code,'暂无') || '/' || COALESCE(i.bank_account,'暂无') || '\n' || COALESCE(i.bank_name,'暂无') || '(' || COALESCE(i.bank_province,'暂无') || '/' || COALESCE(i.bank_city,'暂无') || '/' || COALESCE(i.bank_region,'暂无') || ')' ");
		sb.append("else '无' end as bankAccount");
		sb.append(",p.pay_price as accountFee");
		sb.append(",p.id as payId");
		sb.append(",cast(p.is_balance as integer) as status");
		sb.append(",case when p.main_order_no is null then ' ' || p.order_code else ' ' || p.main_order_no end as orderNo");
		sb.append(",case when p.pay_mode = '2' then p.pay_price * 0.003 else 0 end as poundage");
		sb.append(",cast(p.pay_mode as varchar) as inComeType ");
		sb.append("from tomato_inn i ");
		sb.append("inner join tomato_inn_admin a on a.inn_id = i.id and a.parent_id is null ");
		sb.append("left join tomato_pay_record p on p.inn_id = i.id ");
		sb.append("and p.pay_at >= '"+from+"' and p.pay_at <= '"+to+"' ");
		sb.append("and p.product_code IN ('xz_order','xz_fast_checkIn','checkstand') ");
		sb.append("where i.id IN ("+innIds+") ");
		sb.append(" and p.pay_status = '1' ");
		if(billSearchBean.getIsBalance() != null && billSearchBean.getIsBalance() >= 0){
			sb.append("and p.is_balance = "+billSearchBean.getIsBalance()+" ");
		}
		if(billSearchBean.getPayMode() != null && !"-1".equals(billSearchBean.getPayMode())){
			sb.append("and p.pay_mode = '"+billSearchBean.getPayMode()+"' ");
		}
		sb.append("order by i.id desc,status desc ");
		return billDao.findListMapWithSql(sb.toString());
	}

	public BillCountBean getGetBillCountBean(BillSearchBean billSearchBean) {
		BillCountBean bean = new BillCountBean();
		String from = DateUtil.format(billSearchBean.getStartDate())+ " 00:00:00";
		String to = DateUtil.format(billSearchBean.getEndDate())+ " 23:59:59";
		String sql = getGetBillCountSql(from, to, null, billSearchBean.getPayMode(), billSearchBean.getSearchCondition()
				, billSearchBean.getKeyWord());
		Map<String, Object> result = billDao.findMapWithSql(sql);
		if(result != null){
			BigInteger num = (BigInteger) result.get("num");
			Double money = (Double) result.get("pay");
			bean.setTotalOrders(num.intValue());
			bean.setTotalAmount(money);
		}
		sql = getGetBillCountSql(from, to, 0, billSearchBean.getPayMode(), billSearchBean.getSearchCondition()
				, billSearchBean.getKeyWord());
		result = billDao.findMapWithSql(sql);
		if(result != null){
			BigInteger num = (BigInteger) result.get("num");
			Double money = (Double) result.get("pay");
			bean.setNotBalanceOrders(num.intValue());
			bean.setNotBalanceAmount(money);
		}
		billSearchBean.setCountBean(bean);
		return bean;
	}

	private String getGetBillCountSql(String from, String to, Integer isBalance
			, String payMode, Integer type, String keyWord){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(p.id) as num,sum(p.pay_price) as pay ");
		sb.append("from tomato_pay_record p  ");
		sb.append("inner join tomato_inn i on i.id = p.inn_id ");
		sb.append("where p.pay_at >= '"+from+"' and p.pay_at <= '"+to+"' ");
		sb.append(" and p.pay_status = '1' ");
		if(StringUtils.isNoneBlank(keyWord)){
			switch(type){
				case 0:
					sb.append("and i.name like '%"+keyWord+"%' ");
					break;
				case 1:
					sb.append("and case when p.product_code = 'xz_order' then p.order_code else p.main_order_no end like '%"+keyWord+"%' ");
					break;
			}
		}
		if(isBalance != null){
			sb.append("and p.is_balance = "+isBalance+" ");
		}
		if(payMode != null && !"-1".equals(payMode)){
			sb.append("and p.pay_mode = '"+payMode+"' ");
		}
		sb.append("and p.product_code IN ('xz_order','xz_fast_checkIn','checkstand') ");
		return sb.toString();
	}

	public void balanceOrders(String payIds) {
		String sql = "update tomato_pay_record set is_balance = 1 where id in ("+payIds+") ";
		this.billDao.createSqlQuery(sql).executeUpdate();
	}

}

