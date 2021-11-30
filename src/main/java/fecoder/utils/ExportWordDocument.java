package fecoder.utils;

import fecoder.DAO.*;
import fecoder.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportWordDocument {

    private static final Utils utils = new Utils();
    private static final String imgUrl = utils.getLogo(false);
    private static final String fontFamily = utils.setExportFont(null);

    /**
     * Exporting Draft Document for pre-check before processing
     *
     * @param file File
     * @param workOrder WorkOrder
     * */
    public static void data2DocOfOrderListDraft(File file, WorkOrder workOrder, String date) {
        WorkProductionDAO workProductionDAO = new WorkProductionDAO();
        WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
        Utils utils = new Utils();

        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Calibri";

        ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getProductList(workOrder.getId()+""));

        boolean hasData = false;

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(0));
            pageMar.setTop(BigInteger.valueOf(0));
            pageMar.setRight(BigInteger.valueOf(0));
            pageMar.setBottom(BigInteger.valueOf(0));

            /*
             * Word Header
             * */
            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);

            table = header.createTable(1, 3);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("22%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("65%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("13%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);

            paragraph = row.getCell(0).getParagraphArray(0);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            run = paragraph.createRun();
            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                run.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(100),
                        Units.toEMU(45));            // 100x35 pixels
            }
            run.addBreak();
            run.setText("Tel: 0292.3744950");
            run.addBreak();
            run.setText("Fax: 0292.3743678");
            utils.setHeaderRowforSingleCell(row.getCell(1), "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Độc lập - Tự do - Hạnh phúc", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.SINGLE);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime now = LocalDateTime.now();
            LocalDateTime _date = LocalDateTime.parse(date);

            run.setText("SEAVINA, ngày " + _date.getDayOfMonth() + ", tháng " + _date.getMonthValue() + ", năm "+ _date.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(16);
            run.setText("KIỂM TRA BAO BÌ "+workOrder.getName());
            run.addBreak();

            DecimalFormat formatter = new DecimalFormat("#,###");

            if(productList.size() > 0) {
                for(WorkProduction _product : productList) {

                    table = doc.createTable(1, 1);
                    table.setWidth("100%");
                    utils.spanCellsAcrossRow(table, 0,0,6);
                    row = table.getRow(0);
                    row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    utils.setHeaderRowforSingleCell(row.getCell(0), ("#"+_product.getOrdinalNumbers() + "/ " + _product.getProductName()).toUpperCase(), 14, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

                    table = doc.createTable(1, 6);
                    table.setWidth("100%");

                    row = table.getRow(0);
                    row.getCell(0).setWidth("41%");
                    row.getCell(1).setWidth("15%");
                    row.getCell(2).setWidth("8%");
                    row.getCell(3).setWidth("12%");
                    row.getCell(4).setWidth("12%");
                    row.getCell(5).setWidth("12%");
                    row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    utils.setHeaderRowforSingleCell(row.getCell(0), "Tên Bao Bì", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(1), "Qui cách (CM)", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(2), "ĐVT", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(3), "SL Đặt", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(4), "Tồn", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(5), "Thực tế", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

                    ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(
                            workProductionDAO.getPackagingList(
                                    workOrder.getId()+"", // work_order_product.work_order_id
                                    workOrderProductPackagingDAO.getDataByID(_product.getId()).getProduct_id(), // work_order_product.product_id
                                    _product.getOrdinalNumbers(), // work_order_product.ordinal_num
                                    _product.getOrderTimes() // work_order_product.order_times
                            )
                    );
                    hasData = packagingList.size() > 0;
                    if(packagingList.size()>0) {
                        table = doc.createTable(packagingList.size(), 6);
                        table.setWidth("100%");
                        for(int j=0;j<packagingList.size(); j++) {
//                            System.out.println(packagingList.get(j).getOrdinalNumbers() + " | " + packagingList.get(j).getProductName() + " | " +packagingList.get(j).getPackagingName() + " | " + packagingList.get(j).getActualQuantity() + " | " + packagingList.get(j).getPackagingSuplier());
                            row = table.getRow(j);
                            row.getCell(0).setWidth("41%");
                            row.getCell(1).setWidth("15%");
                            row.getCell(2).setWidth("8%");
                            row.getCell(3).setWidth("12%");
                            row.getCell(4).setWidth("12%");
                            row.getCell(5).setWidth("12%");
                            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                            utils.setHeaderRowforSingleCell(row.getCell(0), packagingList.get(j).getPackagingName() + ( packagingList.get(j).getPrintStatus() != null ? "("+packagingList.get(j).getPrintStatus()+")" : ""), 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                            utils.setHeaderRowforSingleCell(row.getCell(0), packagingList.get(j).getPackagingSpecification() != null ? "("+ packagingList.get(j).getPackagingSpecification() +")" : "", 10, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                            utils.setHeaderRowforSingleCell(row.getCell(1), packagingList.get(j).getPackagingDimension(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                            utils.setHeaderRowforSingleCell(row.getCell(2), packagingList.get(j).getUnit(), 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                            utils.setHeaderRowforSingleCell(row.getCell(3), formatter.format(Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()+""))+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                            utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(packagingList.get(j).getStock()+""))+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                            utils.setHeaderRowforSingleCell(row.getCell(5), formatter.format(Float.parseFloat(packagingList.get(j).getActualQuantity()+""))+"", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                        }
//                        System.out.println("---------------------------------------------");
                    }
                }
            }

            if(hasData) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/KIEM TRA BB "+ workOrder.getName() +".docx"))
                    {
                        doc.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Exporting Draft Worksheet for pre-check before processing
     *
     * @param file File
     * @param workOrder WorkOrder
     * */
    public static void data2WorksheetOfOrderListDraft(File file, WorkOrder workOrder, String date) {
        WorkProductionDAO workProductionDAO = new WorkProductionDAO();
        WorkOrderProductPackagingDAO workOrderProductPackagingDAO = new WorkOrderProductPackagingDAO();
        Utils utils = new Utils();

        SupplierDAO supplierDAO = new SupplierDAO();
        Supplier supplier = supplierDAO.getDataByCode("SVN");

        String imgFile = ExportWordDocument.imgUrl;
        String _fontFamily = ExportWordDocument.fontFamily;

        ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getProductList(workOrder.getId()+""));

        boolean hasData = false;

        DecimalFormat formatter = new DecimalFormat("#,###");

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("KIEM TRA");

            // sets the column with for the column to width*256
            sheet.setColumnWidth(3, 55*256);
            sheet.setColumnWidth(4, 13*256);
            sheet.setColumnWidth(5, 6*256);
            sheet.setColumnWidth(6, 14*256);
            sheet.setColumnWidth(7, 14*256);
            sheet.setColumnWidth(8, 14*256);
            sheet.setDefaultRowHeight((short) 900);

            int rownum = 0;
            Cell cell;
            Row row;

            HSSFCellStyle mainStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 20, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.YELLOW.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
            HSSFCellStyle headerStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 14, IndexedColors.WHITE.getIndex(), true, true, IndexedColors.BLACK.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
            HSSFCellStyle labelStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            HSSFCellStyle labelStyleLeft = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
            HSSFCellStyle cellStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), false, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            HSSFCellStyle cellStyleLeft = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), false, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);

            row = sheet.createRow(rownum);
//            row.setHeight((short) 500);

            // LSX
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(productList.get(0).getWorkOrderName());
            cell.setCellStyle(mainStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    2, //first column (0-based)
                    8  //last column  (0-based)
            ));

            rownum +=1;

            row = sheet.createRow(rownum);
//            row.setHeight((short) 500);

            // NCC (C)
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue("NCC");
            cell.setCellStyle(labelStyle);
            // Tên bao bì (D)
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue("Tên bao bì");
            cell.setCellStyle(labelStyleLeft);
            // Kích thước (E)
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue("Kích thước");
            cell.setCellStyle(labelStyle);
            // ĐVT (F)
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue("ĐVT");
            cell.setCellStyle(labelStyle);
            // Cần đặt (G)
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue("Cần đặt");
            cell.setCellStyle(labelStyle);
            // Tồn (H)
            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue("Tồn");
            cell.setCellStyle(labelStyle);
            // Thực đặt (I)
            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue("Thực đặt");
            cell.setCellStyle(labelStyle);

            if(productList.size() > 0) {
                for(int i=0;i<productList.size();i++) {
                    rownum++;
                    sheet.addMergedRegion(new CellRangeAddress(
                            rownum, //first row (0-based)
                            rownum, //last row  (0-based)
                            2, //first column (0-based)
                            8  //last column  (0-based)
                    ));
                    row = sheet.createRow(rownum);
//                    row.setHeight((short) 700);

                    // Thực đặt
                    cell = row.createCell(2, CellType.STRING);
                    cell.setCellValue(productList.get(i).getOrdinalNumbers() + "/ " + productList.get(i).getProductName() + " (Lần "+productList.get(i).getOrderTimes()+")");
                    cell.setCellStyle(headerStyle);

                    ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(
                            workProductionDAO.getPackagingList(
                                    workOrder.getId()+"", // work_order_product.work_order_id
                                    workOrderProductPackagingDAO.getDataByID(productList.get(i).getId()).getProduct_id(), // work_order_product.product_id
                                    productList.get(i).getOrdinalNumbers(), // work_order_product.ordinal_num
                                    productList.get(i).getOrderTimes() // work_order_product.order_times
                            )
                    );
                    hasData = packagingList.size() > 0;
                    if(packagingList.size()>0) {
                        for(int j=0;j<packagingList.size(); j++) {
                            rownum++;
                            row = sheet.createRow(rownum);
//                            row.setHeight((short) -1);
                            // NCC (C)
                            cell = row.createCell(2, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPackagingSuplier());
                            cell.setCellStyle(cellStyle);
                            // Tên BB (Qui cách) (D)
                            cell = row.createCell(3, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPackagingName() + (packagingList.get(j).getPrintStatus() != null ? " ("+ packagingList.get(j).getPrintStatus() +")" : "") + (!packagingList.get(j).getPackagingSpecification().equals("") ? " ("+ packagingList.get(j).getPackagingSpecification() +")" : ""));
                            cell.setCellStyle(cellStyleLeft);
                            // Kích thước (E)
                            cell = row.createCell(4, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPackagingDimension());
                            cell.setCellStyle(cellStyle);
                            // ĐVT (F)
                            cell = row.createCell(5, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getUnit());
                            cell.setCellStyle(cellStyle);
                            // Cần (G)
                            cell = row.createCell(6, CellType.STRING);
                            cell.setCellValue(Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()+"") > 0 ? formatter.format(Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()+"")) : "");
                            cell.setCellStyle(cellStyle);
                            // Tồn (H)
                            cell = row.createCell(7, CellType.STRING);
                            cell.setCellValue(Float.parseFloat(packagingList.get(j).getStock()+"") > 0 ? formatter.format(Float.parseFloat(packagingList.get(j).getStock()+"")) : "");
                            cell.setCellStyle(cellStyle);
                            // Thực đặt (I)
                            cell = row.createCell(8, CellType.STRING);
                            cell.setCellValue(Float.parseFloat(packagingList.get(j).getActualQuantity()+"") > 0 ? formatter.format(Float.parseFloat(packagingList.get(j).getActualQuantity()+"")) : "");
                            cell.setCellStyle(cellStyle);
                        }
                    }
                }
            }

            if(hasData) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/KIEM TRA BB "+ workOrder.getName() +".xls"))
                    {
                        workbook.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                    }
                }
            }
        } catch(Exception ex) {
            utils.alert("err", Alert.AlertType.ERROR, "Error", ex.getMessage()).showAndWait();
        }
    }

    /**
     * Exporting Order Document which separate by Supplier
     *
     * @param file File
     * @param idList list or single id from work_order_product_packaging.work_order_id
     * @param supplierCode list or single id from suppliers.code
     * */
    public static void data2DocOfOrderBySupplier(File file, String idList, String supplierCode, String shippingCode, String date, int orderTimes) {
        String imgFile = ExportWordDocument.imgUrl;
        String _fontFamily = ExportWordDocument.fontFamily;
        SupplierDAO supplierDAO = new SupplierDAO();
        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
        Utils utils = new Utils();

        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getListBySupplierCode(idList, supplierCode, shippingCode, orderTimes));
        Supplier supplier = supplierDAO.getDataByCode(supplierCode);
        Supplier SEAVINA = supplierDAO.getDataByCode("SVN");

        String woList = "";
        for(int i=0;i<orderObservableList.size();i++) {
            woList += orderObservableList.get(i).getWoName() + (Integer.parseInt(orderObservableList.get(i).getOrderTimes()) > 1 ? " (Lần "+orderObservableList.get(i).getOrderTimes()+")" : "");
            woList += (i<orderObservableList.size()-1) ? " + " : "";
        }

        ShipAddressDAO shipAddressDAO = new ShipAddressDAO();
        ShipAddress shipAddress = shipAddressDAO.getDataByCode(shippingCode);

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(820L));
            pageMar.setTop(BigInteger.valueOf(500L));
            pageMar.setRight(BigInteger.valueOf(820L));
            pageMar.setBottom(BigInteger.valueOf(500L));

            /*
             * Word Header
             * */
            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);

            table = header.createTable(1, 3);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("22%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("23%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);

            paragraph = row.getCell(0).getParagraphArray(0);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            run = paragraph.createRun();

            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                run.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(100),
                        Units.toEMU(45));            // 100x35 pixels
            }
            utils.setHeaderRowforSingleCell(row.getCell(1), SEAVINA.getName(), 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), SEAVINA.getAddress(), 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "1801141886", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), SEAVINA.getPhone() + " - " + SEAVINA.getFax(), 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), SEAVINA.getDeputy(), 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime now = LocalDateTime.now();
            LocalDateTime _date = LocalDateTime.parse(date);

            run.setText("SEAVINA, ngày " + _date.getDayOfMonth() + ", tháng " + _date.getMonthValue() + ", năm "+ _date.getYear());

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            paragraph.setSpacingAfter(0);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("ĐƠN ĐẶT HÀNG");

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(false);
            run.setFontSize(10);
            run.setText("(Số: "+supplierCode+".\t\t/"+_date.getYear()+")");

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(11);
            run.setBold(true);
            run.setText("Kính gửi: "+supplier.getName());
            run.addBreak();
            run.setText("         "+supplier.getDeputy());
            run.addBreak();
            run.setText("Địa chỉ: "+supplier.getAddress());
            run.addBreak();
            run.setText((supplier.getPhone() != null ? "Điện thoại: "+ supplier.getPhone() : "") + (supplier.getFax() != null ? "          Fax: " + supplier.getFax() : ""));
            run.addBreak();
            run.setText(SEAVINA.getName()+" xin gửi ĐƠN ĐẶT HÀNG đến Quý Công Ty với chi tiết như sau:");

            table = doc.createTable(1, 8);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("6%");
            row.getCell(1).setWidth("20%");
            row.getCell(2).setWidth("15%");
            row.getCell(3).setWidth("10%");
            row.getCell(4).setWidth("12%");
            row.getCell(5).setWidth("14%");
            row.getCell(6).setWidth("10%");
            row.getCell(7).setWidth("13%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(3), "ĐVT", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Đơn giá (Chưa VAT)", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(6), "Mã", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(7), "LSX", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(orderObservableList.size(), 8);
            table.setWidth("100%");

            for(int i=0;i<orderObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("6%");
                row.getCell(1).setWidth("20%");
                row.getCell(2).setWidth("15%");
                row.getCell(3).setWidth("10%");
                row.getCell(4).setWidth("12%");
                row.getCell(5).setWidth("10%");
                row.getCell(6).setWidth("14%");
                row.getCell(7).setWidth("13%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + (orderObservableList.get(i).getpIsPrinted() != null ? (orderObservableList.get(i).getpIsPrinted() != null ? " ("+orderObservableList.get(i).getpIsPrinted()+")" : "") : ""), 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpSpecs() != null ? " ("+ orderObservableList.get(i).getpSpecs() +")" : "", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(5), "đ/"+orderObservableList.get(i).getpUnit(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(6), (orderObservableList.get(i).getPackagingCustomCode() != null) ? orderObservableList.get(i).getPackagingCustomCode() : orderObservableList.get(i).getpCode(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(7), orderObservableList.get(i).getWoName(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            }

            table = doc.createTable(4, 2);
            table.setWidth("100%");
            utils.spanCellsAcrossRow(table, 0,0,2);
            utils.spanCellsAcrossRow(table, 0,1,7);
            utils.spanCellsAcrossRow(table, 1,0,2);
            utils.spanCellsAcrossRow(table, 1,1,7);
            utils.spanCellsAcrossRow(table, 2,0,2);
            utils.spanCellsAcrossRow(table, 2,1,7);
            utils.spanCellsAcrossRow(table, 3,0,2);
            utils.spanCellsAcrossRow(table, 3,1,7);
            row = table.getRow(0);
            row.getCell(0).setWidth("40%");
            row.getCell(1).setWidth("60%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Đặc điểm và qui cách", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Chất lượng: Đảm bảo đúng chất lượng và định lượng bao bì như mẫu chào hàng.", 11, true, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Sản xuất theo mẫu xác nhận ngày: ", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), _date.getDayOfMonth() + "/" + _date.getMonthValue() + "/"+ _date.getYear(), 12, true, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải làm đúng kích thước, màu sắc, thông tin như đã xác nhận.", 11, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải đạt tiêu chuẩn hàng Thủy sản xuất khẩu. Số lượng làm đủ, không thừa, không thiếu.", 11, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Chúng tôi sẽ trả lại các lô hàng làm sai qui cách và không đúng các yêu cầu trên.", 11, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Khi giao hàng vui lòng liên hệ: ", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), shipAddress.getStocker() + " ("+shipAddress.getStocker_phone()+")", 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            row = table.getRow(1);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Ngày giao hàng", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            row = table.getRow(2);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Địa chỉ giao hàng", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), shipAddress.getName(), 11, true, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), shipAddress.getAddress(), 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            row = table.getRow(3);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Chú ý", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Mọi thay đổi hoặc có vấn đề gì chưa rõ phải báo lại ngay với "+SEAVINA.getName()+" trước khi tiến hành.", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            table = doc.createTable(1, 2);
            utils.spanCellsAcrossRow(table, 0,0,3);
            utils.spanCellsAcrossRow(table, 0,1,7);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Bên bán hàng", 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Bên mua hàng", 11, false, true, ParagraphAlignment.RIGHT, UnderlinePatterns.NONE);


            if(file != null && orderObservableList.size() > 0) {
                // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/"+ supplierCode + "-"+woList+ " ("+shipAddress.getCode_address()+")"+ ".docx"))
                {
                    doc.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Exporting Work Order Document
     *
     * @param file File
     * @param idList list or single id from work_order_product_packaging.work_order_id
     * @param orderTimes work_order_product_packaging.order_times
     * */
    public static void data2DocOfOrderList(File file, String idList, String date, int orderTimes) {
        String imgFile = ExportWordDocument.imgUrl;
        String _fontFamily = ExportWordDocument.fontFamily;

        Utils utils = new Utils();
        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(820L));
            pageMar.setTop(BigInteger.valueOf(500L));
            pageMar.setRight(BigInteger.valueOf(820L));
            pageMar.setBottom(BigInteger.valueOf(500L));

            /*
             * Word Header
             * */
//            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);
//
//            table = header.createTable(1, 3);
            table = doc.createTable(1, 3);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("25%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setWidth("55%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
            row.getCell(2).setWidth("20%");
            row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);

            paragraph = row.getCell(0).getParagraphArray(0);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            run = paragraph.createRun();
            // add png image
            try (FileInputStream is = new FileInputStream(imgFile)) {
                run.addPicture(is,
                        Document.PICTURE_TYPE_PNG,    // png file
                        imgFile,
                        Units.toEMU(100),
                        Units.toEMU(45));            // 100x35 pixels
            }
            run.addBreak();
            run.setText("Tel: 0292.3744950");
            run.addBreak();
            run.setText("Fax: 0292.3743678");
            utils.setHeaderRowforSingleCell(row.getCell(1), "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Độc lập - Tự do - Hạnh Phúc", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.SINGLE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "", 10, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

            /*
             * Word content title
             * */

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime now = LocalDateTime.now();
            LocalDateTime _date = LocalDateTime.parse(date);

            run.setText("SEAVINA, ngày " + _date.getDayOfMonth() + ", tháng " + _date.getMonthValue() + ", năm "+ _date.getYear());

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("ĐỀ NGHỊ BAO BÌ "+utils.getListWorkOrderName(idList) + (orderTimes > 1 ? " (LẦN "+orderTimes+")" : ""));

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(11);
            run.setBold(false);
            run.setText("Kính gửi: Phòng Mua Hàng Minh Tâm");
            run.addBreak();
            run.setText("Điện thoại: 02923. 3600063");
            run.addBreak();
            run.setText("Công ty CP SEAVINA (Phòng Kinh Doanh) xin gửi Phiếu đề nghị mua hàng với chi tiết như sau:");

            table = doc.createTable(1, 7);
            table.setWidth("100%");

            row = table.getRow(0);
            row.getCell(0).setWidth("5%");
            row.getCell(1).setWidth("35%");
            row.getCell(2).setWidth("15%");
            row.getCell(3).setWidth("8%");
            row.getCell(4).setWidth("12%");
            row.getCell(5).setWidth("10%");
            row.getCell(6).setWidth("15%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(3), "ĐVT", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Ghi chú", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(6), "LSX", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

            ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getListByOrderTimes(idList, orderTimes));
            DecimalFormat formatter = new DecimalFormat("#,###");

            table = doc.createTable(orderObservableList.size(), 7);
            table.setWidth("100%");

            for(int i=0;i<orderObservableList.size();i++) {
                row = table.getRow(i);
                row.getCell(0).setWidth("5%");
                row.getCell(1).setWidth("35%");
                row.getCell(2).setWidth("15%");
                row.getCell(3).setWidth("8%");
                row.getCell(4).setWidth("12%");
                row.getCell(5).setWidth("10%");
                row.getCell(6).setWidth("15%");
                row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                row.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                float residualNumber = Float.parseFloat(orderObservableList.get(i).getpTotal()+"") + Float.parseFloat(orderObservableList.get(i).getpStock()+"") - Float.parseFloat(orderObservableList.get(i).getpResidualQuantity()+"") - Float.parseFloat(orderObservableList.get(i).getpDesireQuantity()+"");

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + ( orderObservableList.get(i).getpIsPrinted() != null ? " ("+ orderObservableList.get(i).getpIsPrinted() +")" : "") + ( !orderObservableList.get(i).getpSpecs().equals("") ? " ("+ orderObservableList.get(i).getpSpecs() +")" : ""), 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(5), residualNumber > 0 ? "Dư "+formatter.format(residualNumber) : "", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(11);
            run.setBold(false);
            run.setText("Lưu ý: Mọi thay đổi hay có vấn đề chưa rõ phải phản hồi lại với bộ phận liên quan trước khi thực hiện để tránh sai sót có thể xảy ra.");

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "\tPhòng Kinh Doanh", 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Người Lập Biểu\t", 11, false, true, ParagraphAlignment.RIGHT, UnderlinePatterns.NONE);

            if(orderObservableList.size() > 0) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/DE NGHI BB "+ utils.getListWorkOrderName(idList) + (orderTimes > 1 ? " (L"+orderTimes+")" : "")+".docx"))
                    {
                        doc.write(out);
//                        utils.alert("info", Alert.AlertType.INFORMATION, "Xuất file thành công!", "File đã được lưu vào ổ đĩa!").showAndWait();
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
