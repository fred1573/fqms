package com.project.service.proxysale;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.project.bean.proxysale.OrderComplaintSearch;
import com.project.dao.proxysale.ProxySaleOrderComplaintDao;
import com.project.entity.account.User;
import com.project.entity.proxysale.OrderComplaintProcessLog;
import com.project.entity.proxysale.ProxySaleOrderComplaint;
import com.project.entity.proxysale.ProxySaleSubOrder;
import com.project.enumeration.EnumDescription;
import com.project.enumeration.ProxySaleOrderComplaintStatus;
import com.project.service.CurrentUserHolder;
import com.project.service.excel.ExcelCellConfig;
import com.project.service.excel.ExcelHeader;
import com.project.service.excel.ExcelService;
import com.project.service.excel.ExcelServiceImpl;
import com.project.utils.ExcelExportUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author yuneng.huang on 2016/6/13.
 */
@Service
@Transactional("mybatisTransactionManager")
public class ProxySaleOrderComplaintServiceImpl implements ProxySaleOrderComplaintService {

    private static final int MAX_ROW = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxySaleOrderComplaintServiceImpl.class);
    @Resource
    private OrderComplaintProcessLogService orderComplaintProcessLogService;
    @Resource
    private ProxySaleOrderComplaintDao proxySaleOrderComplaintDao;
    @Resource
    private CurrentUserHolder currentUserHolder;

    @Override
    public boolean isExistComplaint(String orderNo) {
        return false;
    }

    @Override
    public void save(ProxySaleOrderComplaint orderComplaint) {
        Assert.notNull(orderComplaint);
        Assert.hasText(orderComplaint.getOrderNo(), "OMS订单号不能为空");
        OrderComplaintProcessLog processLog = orderComplaint.getProcessLog();
        Assert.notNull(processLog);
        orderComplaint.setComplaintType(processLog.getComplaintType());


        ProxySaleOrderComplaint complaint = proxySaleOrderComplaintDao.selectByOrderNo(orderComplaint.getOrderNo());
        if (complaint == null) {
            proxySaleOrderComplaintDao.insert(orderComplaint);
            List<ProxySaleSubOrder> channelOrderList = orderComplaint.getChannelOrderList();
            if (!CollectionUtils.isEmpty(channelOrderList)) {
                for (ProxySaleSubOrder proxySaleSubOrder : channelOrderList) {
                    proxySaleSubOrder.setOrderComplaintId(orderComplaint.getId());
                }
                proxySaleOrderComplaintDao.insertSubOrderList(channelOrderList);
            }
        }else {
            orderComplaint.setId(complaint.getId());
            if (!ProxySaleOrderComplaintStatus.FINISH.equals(complaint.getComplaintStatus())) {
                if (ProxySaleOrderComplaintStatus.FINISH.equals(orderComplaint.getComplaintStatus())) {
                    User currentUser = currentUserHolder.getUser();
                    orderComplaint.setFinishTime(new Date());
                    orderComplaint.setFinishUserId(currentUser.getId());
                    orderComplaint.setFinishUserName(currentUser.getSysUserCode());
                }
                proxySaleOrderComplaintDao.update(orderComplaint);
            }
        }

        processLog.setComplaintStatus(orderComplaint.getComplaintStatus());
        processLog.setOrderComplaintId(orderComplaint.getId());
        orderComplaintProcessLogService.save(processLog);
    }


    @Override
    public PageList<ProxySaleOrderComplaint> findByPage(PageBounds pageBounds, OrderComplaintSearch complaintSearch) {
        return proxySaleOrderComplaintDao.selectByPage(pageBounds, complaintSearch);
    }

    @Override
    public int findInnCountBySearch(OrderComplaintSearch complaintSearch) {
        Integer integer = proxySaleOrderComplaintDao.selectInnCountBySearch(complaintSearch);
        if (integer == null) {
            return 0;
        }
        return integer;
    }

    @Override
    public void exportExcel(OutputStream os, OrderComplaintSearch complaintSearch) {
        ExcelHeader header = new ExcelHeader();
        HSSFWorkbook wb = new HSSFWorkbook();
        header.setStyle(ExcelExportUtil.buildTitleCellStyle(wb));
        /**
         * 分销商、子分销商、目的地、客栈名称、客栈ID、代销经理、分销商订单号、OMS订单号、入住时间、离店时间、下单日期、客诉类型、处理完成人（点击处理完成的人）、处理完成时长
         */

        Converter<EnumDescription, String> enumDescriptionStringConverter = new Converter<EnumDescription, String>() {
            @Override
            public String convert(EnumDescription source) {
                if (source == null) {
                    return "";
                }
                return source.getDescription();
            }
        };
        HSSFCellStyle cellStyle = ExcelExportUtil.buildNormalCellStyle(wb);
        header.put("分销商",new ExcelCellConfig("channelName",cellStyle));
        header.put("子分销商",new ExcelCellConfig("channelCodeName",cellStyle));
        header.put("目的地",new ExcelCellConfig("regionName",cellStyle));
        header.put("客栈名称", new ExcelCellConfig("innName",cellStyle));
        header.put("客栈ID", new ExcelCellConfig("innId",cellStyle));
        header.put("代销经理", new ExcelCellConfig("customerManager",cellStyle));
        header.put("分销商订单号", new ExcelCellConfig("channelOrderNo",cellStyle));
        header.put("OMS订单号", new ExcelCellConfig("orderNo",cellStyle));
        header.put("住离时间", new ExcelCellConfig("checkInAndOutStr",cellStyle));
        header.put("下单日期", new ExcelCellConfig("orderTimeStr",cellStyle));
        ExcelCellConfig<EnumDescription> typeCfg = new ExcelCellConfig("complaintType",cellStyle);
        typeCfg.setConverter(enumDescriptionStringConverter);
        header.put("客诉类型", typeCfg);
        header.put("处理完成人", new ExcelCellConfig("finishUserName",cellStyle));
        header.put("处理完成时长", new ExcelCellConfig("finishTimeMinutes",cellStyle));


        ExcelService excelService = new ExcelServiceImpl();
        PageList<ProxySaleOrderComplaint> proxySaleOrderComplaints = proxySaleOrderComplaintDao.selectByPage(new PageBounds(MAX_ROW), complaintSearch);
        try {
            excelService.createHeaderSheet("客诉列表", header, wb,proxySaleOrderComplaints );
            wb.write(os);
        } catch (Exception e) {
            throw new RuntimeException("导出客诉列表失败",e);
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
    }
}
