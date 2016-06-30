/**
* @Title: HotelReviewManager.java
* @Package com.project.service.audit
* @Description: 
* @author Administrator
* @date 2014年3月27日 上午11:57:56
*/

/**
 * 
 */
package com.project.service.inn;

import com.google.common.collect.Lists;
import com.project.bean.SearchInnBean;
import com.project.core.orm.Page;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.inn.InnAdminDao;
import com.project.dao.inn.InnDao;
import com.project.dao.wg.WgRoomTypeDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.inn.Inn;
import com.project.entity.inn.InnAdmin;
import com.project.entity.wg.WgRoomType;
import com.project.entity.wg.WgRoomTypeToInnRoom;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyc
 *
 */
@Component
@Transactional
public class InnManager {

	@Autowired
	private InnAdminDao innAdminDao;
	
	@Autowired
	private InnMsgManager innMsgManager;
	
	@Autowired
	private WgRoomTypeDao wgRoomTypeDao;
	
	@Resource
	private FinanceOperationLogDao financeOperationLogDao;
	
	@Autowired
	private InnDao innDao;

	//审核查询申请客栈数据
	public Page<Inn> getPage(Page<Inn> page, String keyWord, boolean isFilt) {		
		return innDao.getPage(page, keyWord, isFilt);
	}
	
	public Inn findById(int id){
		return innDao.findUniqueBy("id", id);
	}
	
	public InnAdmin findByMobile(String mobile){
		return innAdminDao.findUniqueBy("mobile", mobile);
	}
	
	//添加第三方的客栈库存
	public void update(Inn inn, String sysUserCode){
		inn.setInMarketCreatedUser(sysUserCode);
		inn.setJoinMarketTime(new Date());
		innDao.save(inn);
	}
	
	//移除库存
	public void removeUpdate(Inn inn, String sysUserCode){
		inn.setInMarketCreatedUser(sysUserCode);
		innDao.save(inn);
	}
	
	/**
	 * 保存/更新
	 * @param inn
	 */
	public void saveOrUpdate(Inn inn){
		Date now = new Date();
		FinanceOperationLog log = null;
		if(inn.getId() != null){
			Inn oldInn = findById(inn.getId());
			inn.setUpdatedAt(now);
			log = keepInfo(inn, oldInn);
			innDao.save(oldInn);
		}else{
			inn.setRegisteredAt(now);
			innDao.save(inn);
		}
		 // 记录操作日志
		if(log != null){
			financeOperationLogDao.save(log);
		}
	}
	
	/**
	 * 拼接操作记录
	 * @param now
	 * @param innName 
	 * @return
	 */
	private FinanceOperationLog getUpdateInnLog(Date now, Inn proInn, Inn preInn) {
		FinanceOperationLog log = new FinanceOperationLog();
		log.setOperateTime(now);
		log.setOperateUser(SpringSecurityUtil.getCurrentUser().getUsername());
		log.setOperateType("201");
		log.setOperateObject(proInn.getName());
		StringBuilder sb = new StringBuilder("已确认收到：");
		sb.append("客栈id: "+proInn.getId()+"\r\n");
		sb.append("修改前: 支付宝："+preInn.getAlipayCode()+"；"+preInn.getAlipayUser()+"。财付通："+preInn.getTenpayCode()+"，"+preInn.getTenpayUser()+"；银行账户："+preInn.getBankAccount()+"；"+preInn.getBankCode()+"；"+preInn.getBankName()+"；"+preInn.getBankRegion()+"\r\n ");
		sb.append("修改后: 支付宝："+proInn.getAlipayCode()+"；"+proInn.getAlipayUser()+"。财付通："+proInn.getTenpayCode()+"，"+proInn.getTenpayUser()+"；银行账户："+proInn.getBankAccount()+"；"+proInn.getBankCode()+"；"+proInn.getBankName()+"；"+proInn.getBankRegion()+"\r\n ");
		log.setOperateContent(sb.toString());
		log.setInnId(proInn.getId());
		log.setInnName(proInn.getName());
		return log;
	}
	
	private FinanceOperationLog keepInfo(Inn inn, Inn oldInn) {
		FinanceOperationLog log = getUpdateInnLog(new Date(), inn, oldInn);
		oldInn.setAddress((StringUtils.isBlank(inn.getAddress()))?oldInn.getAddress():inn.getAddress());
		oldInn.setAlipayCode((StringUtils.isBlank(inn.getAlipayCode())?oldInn.getAlipayCode():inn.getAlipayCode()));
		oldInn.setAlipayUser((StringUtils.isBlank(inn.getAlipayUser())?oldInn.getAlipayUser():inn.getAlipayUser()));
		oldInn.setAppId((StringUtils.isBlank(inn.getAppId())?oldInn.getAppId():inn.getAppId()));
		oldInn.setAppKey((StringUtils.isBlank(inn.getAppKey())?oldInn.getAppKey():inn.getAppKey()));
		oldInn.setAuditor((StringUtils.isBlank(inn.getAuditor())?oldInn.getAuditor():inn.getAuditor()));
		oldInn.setBankAccount((StringUtils.isBlank(inn.getBankAccount())?oldInn.getBankAccount():inn.getBankAccount()));
		oldInn.setBankCode((StringUtils.isBlank(inn.getBankCode())?oldInn.getBankCode():inn.getBankCode()));
		oldInn.setBankName((StringUtils.isBlank(inn.getBankName())?oldInn.getBankName():inn.getBankName()));
		oldInn.setBankRegion((StringUtils.isBlank(inn.getBankRegion())?oldInn.getBankRegion():inn.getBankRegion()));
		oldInn.setBankType((inn.getBankType() == null)?oldInn.getBankType():inn.getBankType());
		oldInn.setContact((StringUtils.isBlank(inn.getContact())?oldInn.getContact():inn.getContact()));
		oldInn.setHasBrand((inn.getHasBrand() == null)?oldInn.getHasBrand():inn.getHasBrand());
		oldInn.setInMarket((StringUtils.isBlank(inn.getInMarket())?oldInn.getInMarket():inn.getInMarket()));
		oldInn.setBankCity((StringUtils.isBlank(inn.getBankCity())?oldInn.getBankCity():inn.getBankCity()));
		oldInn.setBankProvince((StringUtils.isBlank(inn.getBankProvince())?oldInn.getBankProvince():inn.getBankProvince()));
		oldInn.setInMarketCreatedUser((StringUtils.isBlank(inn.getInMarketCreatedUser())?oldInn.getInMarketCreatedUser():inn.getInMarketCreatedUser()));
		oldInn.setJoinMarketTime((inn.getJoinMarketTime() == null)?oldInn.getJoinMarketTime():inn.getJoinMarketTime());
		oldInn.setMarketRooms((inn.getMarketRooms() == null)?oldInn.getMarketRooms():inn.getMarketRooms());
		oldInn.setName((StringUtils.isBlank(inn.getName())?oldInn.getName():inn.getName()));
		oldInn.setPricePolicy((StringUtils.isBlank(inn.getPricePolicy())?oldInn.getPricePolicy():inn.getPricePolicy()));
		oldInn.setTenpayCode((StringUtils.isBlank(inn.getTenpayCode())?oldInn.getTenpayCode():inn.getTenpayCode()));
		oldInn.setTenpayUser((StringUtils.isBlank(inn.getTenpayUser())?oldInn.getTenpayUser():inn.getTenpayUser()));
		oldInn.setTotalCommissionRatio((inn.getTotalCommissionRatio() == null)?oldInn.getTotalCommissionRatio():inn.getTotalCommissionRatio());
		oldInn.setUpdatedAt((inn.getUpdatedAt() == null)?oldInn.getUpdatedAt():inn.getUpdatedAt());
		oldInn.setRegisteredAt((inn.getRegisteredAt() == null)?oldInn.getRegisteredAt():inn.getRegisteredAt());
		return log;
	}

	//重置密码时查询用户
	public Page<InnAdmin> searchUser(Page<InnAdmin> page,String condition) {
		return innAdminDao.findPageByPar(page,condition);
	}
	
	// 重置密码
	public String resetPassword(int id) {		
		return innAdminDao.resetPassword(id);
	}
	
	public int getInnAmount(){
		return innAdminDao.getInnAmount();
	}
	
	public int getAdminAmount(Integer innId){
		return innAdminDao.getAdminAmount(innId);
	}

	public void searchMarketInn(Page<Inn> page, SearchInnBean searchInnBean) {
		innDao.searchMarketInn(page, searchInnBean);
	}
	
	public Map<String, Object> getAccountMap(int innId){
		return innDao.getAccountMap(innId);
	}
	
	@SuppressWarnings("unchecked")
	public void getMarketRooms(Page<Inn> page, SearchInnBean searchInnBean){
		for (Inn inn : (List<Inn>)page.getResult()) {
			List<WgRoomType> roomTypes = wgRoomTypeDao.getByInnId(inn.getId());
			getRealRoomNum4Api(inn, roomTypes);
		}
	}
	
	public void saveRegionReports(String day){
		innDao.getRegionCounts(day);
	}
	
	private void getRealRoomNum4Api(Inn inn, List<WgRoomType> roomTypes){
		List<WgRoomTypeToInnRoom> rooms = Lists.newArrayList();
		for (WgRoomType roomType : roomTypes) {
			rooms.addAll(roomType.getWgRoomTypeToInnRooms());
		}
		Map<Integer, String> map = new HashMap<>();
		int num = 0;
		for (WgRoomTypeToInnRoom room : rooms) {
			if(map.get(room.getInnRoomId()) == null){
				num++;
			}else{
				map.put(room.getInnRoomId(), "");
			}
		}
		inn.setMarketRooms(num);
	}

	public void updateInnBrand(Integer innId, boolean brand) {
		if (innId!=null) {
			innDao.updateInnBrand(innId,brand);
		}
	}
}
