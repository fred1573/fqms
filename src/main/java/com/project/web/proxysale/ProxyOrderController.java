package com.project.web.proxysale;

import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyOrder;
import com.project.entity.proxysale.ProxyParentOrder;
import com.project.service.proxysale.PricePatternService;
import com.project.service.proxysale.ProxyInnService;
import com.project.service.proxysale.ProxyOrderService;
import com.project.utils.ProxyOrderIdGenerator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 番茄家族后台下单      用于补录订单
 * Created by Administrator on 2015/8/12.
 */

@Controller
@RequestMapping("/proxysale/order")
public class ProxyOrderController {

    private static final Integer EXCEL_OTA_ID = 1;
    private static final Integer EXCEL_OTA_ORDER_NO = 2;
    private static final Integer EXCEL_PENALTY = 3;
    private static final Integer EXCEL_PER_TIME = 4;
    private static final Integer EXCEL_OMS_STATUS = 5;
    private static final Integer EXCEL_INN_ID = 6;
    private static final Integer EXCEL_ACCOUNT_ID = 7;
    private static final Integer EXCEL_CHECK_IN_AT = 9;
    private static final Integer EXCEL_CHECK_OUT_AT = 10;
    private static final Integer EXCEL_BOOK_PRICE = 11;
    private static final Integer EXCEL_ROOM_TYPE_NAME = 12;
    private static final Integer EXCEL_ROOM_TYPE_NUM = 14;
    private static final Integer EXCEL_ROOM_TYPE_ID = 15;

    private static final Integer EXCEL_OMS_STATUS_RECEIVE = 1;
    private static final Integer EXCEL_OMS_STATUS_CANCEL = 3;

    private final Map<Integer, Integer> STATUS_MAPS = new HashMap<>();
    {
        STATUS_MAPS.put(EXCEL_OMS_STATUS_RECEIVE, ProxyParentOrder.SUC);
        STATUS_MAPS.put(EXCEL_OMS_STATUS_CANCEL, ProxyParentOrder.CANCEL);
    }

    @Autowired
    private ProxyOrderService proxyOrderService;
    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private PricePatternService pricePatternService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(){
        return "/proxysale/order/index";
    }

    @RequestMapping(value = "/batch_create", method = RequestMethod.POST)
    public String batchCreate(@RequestParam MultipartFile file, Model model){

        try {
            XSSFWorkbook wb;
            try {
                wb = new XSSFWorkbook(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            XSSFSheet sh = wb.getSheetAt(0);
            int totalRows = sh.getLastRowNum();
            Map<String, ProxyParentOrder> parentOrderMaps = new HashMap<>();
            ProxyParentOrder parentOrder;
            ProxyOrder childOrder;
            for (int i = 1; i <= totalRows; i++) {
                String otaOrderNo = "";
                try {
                    Row row = sh.getRow(i);
                    otaOrderNo = parseString(row.getCell(EXCEL_OTA_ORDER_NO));
                    if(!parentOrderMaps.containsKey(otaOrderNo)){
                        parentOrder = new ProxyParentOrder();
                        parentOrder.setOtaOrderNo(otaOrderNo);
                        parentOrder.setId(ProxyOrderIdGenerator.generate());
                        parentOrder.setOtaId(parseInt(row.getCell(EXCEL_OTA_ID)));
                        parentOrder.setCreateTime(new Date());
                        parentOrder.setPerTime(parsePerTime(row.getCell(EXCEL_PER_TIME)));
                        Integer status = parseStatus(row.getCell(EXCEL_OMS_STATUS));
                        parentOrder.setStatus(status);
                        if(status.intValue() == ProxyParentOrder.CANCEL.intValue()){
                            parentOrder.setPenalty(parseBigDecimal(row.getCell(EXCEL_PENALTY)));
                        }
                        ProxyInn proxyInn = proxyInnService.findByInnId(parseInt(row.getCell(EXCEL_INN_ID)));
                        if(proxyInn == null){
                            throw new RuntimeException("客栈在代销平台未开通或已被删除, otaOrderNo=" + otaOrderNo);
                        }
                        parentOrder.setProxyInn(proxyInn);
                        parentOrder.setRoomTypeNum(parseInt(row.getCell(EXCEL_ROOM_TYPE_NUM)));
                        parentOrder.setPricePattern(pricePatternService.getPattern(parseInt(row.getCell(EXCEL_ACCOUNT_ID))));
                    }else{
                        parentOrder = parentOrderMaps.get(otaOrderNo);
                    }
                    childOrder = new ProxyOrder();
                    childOrder.setBookRoomPrice(parseBigDecimal(row.getCell(EXCEL_BOOK_PRICE)));
                    childOrder.setCheckInAt(parseBookTime(row.getCell(EXCEL_CHECK_IN_AT)));
                    childOrder.setCheckOutAt(parseBookTime(row.getCell(EXCEL_CHECK_OUT_AT)));
                    childOrder.setParentOrder(parentOrder);
                    childOrder.setRoomTypeId(parseInt(row.getCell(EXCEL_ROOM_TYPE_ID)));
                    childOrder.setRoomTypeName(parseString(row.getCell(EXCEL_ROOM_TYPE_NAME)));
                    childOrder.setId(ProxyOrderIdGenerator.generate());
                    parentOrder.getChildOrders().add(childOrder);
                    parentOrderMaps.put(otaOrderNo, parentOrder);
                } catch (RuntimeException e) {
                    throw new RuntimeException(e.getMessage() + ", channel_order_no=" + otaOrderNo);
                }
            }
            if(parentOrderMaps.size() > 0){
                proxyOrderService.createOrder(parentOrderMaps.values());
            }
            model.addAttribute("info", "录入完毕");
        } catch (RuntimeException e) {
            model.addAttribute("info", e.getMessage());
        }
        return "/proxysale/order/index";
    }

    /**
     * 单条录入
     * @param orderForm
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(OrderForm orderForm){
        return null;
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public void downloadTemplate(HttpServletRequest request, HttpServletResponse response){
        try {
            InputStream inputStream = getClass().getResourceAsStream("/excel/proxysale_order.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=template.xlsx");
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer parseStatus(Cell cell){
        Integer omsStatus = parseInt(cell);
        Integer status = STATUS_MAPS.get(omsStatus);
        if(status == null){
            throw new RuntimeException("订单状态异常");
        }
        return status;
    }

    private Integer parseInt(Cell cell){
        int cellType = cell.getCellType();
        if(Cell.CELL_TYPE_NUMERIC == cellType){
            return new Double(cell.getNumericCellValue()).intValue();
        }else if(Cell.CELL_TYPE_STRING == cellType){
            return Integer.parseInt(cell.getStringCellValue());
        }
        throw new RuntimeException("method:parseInt 数据类型只能是数字或字符串");
    }

    private String parseString(Cell cell){
        int cellType = cell.getCellType();
        if(Cell.CELL_TYPE_NUMERIC == cellType){
            return Double.toString(cell.getNumericCellValue());
        }else if(Cell.CELL_TYPE_STRING == cellType){
            return cell.getStringCellValue();
        }
        throw new RuntimeException("method:parseString 数据类型只能是数字或字符串");
    }

    private Date parsePerTime(Cell cell){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm:ss.SSS"); //下單時間格式
        return parseTime(cell, format);
    }

    private Date parseTime(Cell cell, SimpleDateFormat format) {
        Date time;
        try {
            time = format.parse(cell.getStringCellValue());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return time;
    }

    private Date parseBookTime(Cell cell){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");  //入住退房時間格式
        return parseTime(cell, format);
    }

    private BigDecimal parseBigDecimal(Cell cell){
        return new BigDecimal(parseString(cell));
    }

}
