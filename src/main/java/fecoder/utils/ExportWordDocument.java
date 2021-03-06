package fecoder.utils;

import fecoder.DAO.*;
import fecoder.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ExportWordDocument {

    private static final Utils utils = new Utils();
    private static final String imgUrl = utils.getLogo(true);
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
            utils.setHeaderRowforSingleCell(row.getCell(1), "C???NG H??A X?? H???I CH??? NGH??A VI???T NAM", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "?????c l???p - T??? do - H???nh ph??c", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.SINGLE);

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

            run.setText("SEAVINA, ng??y " + _date.getDayOfMonth() + ", th??ng " + _date.getMonthValue() + ", n??m "+ _date.getYear());
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(16);
            run.setText("KI???M TRA BAO B?? "+workOrder.getName());
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

                    utils.setHeaderRowforSingleCell(row.getCell(0), "T??n Bao B??", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(1), "Qui c??ch (CM)", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(2), "??VT", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(3), "SL ?????t", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(4), "T???n", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                    utils.setHeaderRowforSingleCell(row.getCell(5), "Th???c t???", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);

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
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ??? ????a!").showAndWait();
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

        Locale locale  = new Locale("en", "US");
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        df.applyPattern("###,###.###");

        SupplierDAO supplierDAO = new SupplierDAO();
        Supplier supplier = supplierDAO.getDataByCode("SVN");

        String imgFile = ExportWordDocument.imgUrl;
        String _fontFamily = ExportWordDocument.fontFamily;

        ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getProductList(workOrder.getId()+""));
        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
        OrderBySupllier totalOrder = orderBySupplierDAO.getTotalOrder(workOrder.getId()+"");

        boolean hasData = false;

        DecimalFormat formatter = new DecimalFormat("#,###");

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("KIEM TRA");

            sheet.protectSheet("keep-it-un-editable");

            sheet.setZoom(75);
            sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            sheet.getPrintSetup().setHeaderMargin(0.3D);
            sheet.getPrintSetup().setFooterMargin(0.3D);
            sheet.setMargin(Sheet.LeftMargin, 0.25);
            sheet.setMargin(Sheet.RightMargin, 0.25);
            sheet.setMargin(Sheet.TopMargin, 0.2);
            sheet.setMargin(Sheet.BottomMargin, 0.2);

            PrintSetup ps = sheet.getPrintSetup();
            sheet.setAutobreaks(true);
            ps.setFitHeight((short) 0);
            ps.setFitWidth((short) 1);

            workbook.setPrintArea(0, "$C:$I");

            // sets the column with for the column to width*256
            sheet.setColumnWidth(3, 55*256);
            sheet.setColumnWidth(4, 13*256);
            sheet.setColumnWidth(5, 6*256);
            sheet.setColumnWidth(6, 14*256);
            sheet.setColumnWidth(7, 14*256);
            sheet.setColumnWidth(8, 14*256);
            sheet.setColumnWidth(9, 16*256);
            sheet.setColumnWidth(10, 16*256);
            sheet.setColumnWidth(11, 16*256);
            sheet.setDefaultRowHeight((short) 900);

            int rownum = 0;
            Cell cell;
            Row row;

            HSSFCellStyle mainStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 24, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.YELLOW.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
            HSSFCellStyle headerStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 16, IndexedColors.WHITE.getIndex(), true, true, IndexedColors.BLACK.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
            HSSFCellStyle labelStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
            HSSFCellStyle labelStyleLocked = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
            HSSFCellStyle labelStyleLeft = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), true, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
            HSSFCellStyle cellStyle = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), false, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
            HSSFCellStyle cellStyleLocked = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), false, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
            HSSFCellStyle cellStyleLeft = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), false, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
            HSSFCellStyle cellStyleLeftLocked = HSSFUtil.createStyle(workbook, BorderStyle.THIN, (short) 11, IndexedColors.BLACK.getIndex(), false, true, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, true);
            HSSFCellStyle lockedCell = HSSFUtil.createStyle(workbook, BorderStyle.HAIR, (short) 1, IndexedColors.WHITE.getIndex(), false, false, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);

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

            if(totalOrder.getCbm() != null || totalOrder.getpDesireQuantity() > 0) {
                // T???ng s??? kh???i (J)
                cell = row.createCell(9, CellType.STRING);
                cell.setCellValue(df.format(Float.parseFloat(totalOrder.getCbm())) + " (kh???i)");
                cell.setCellStyle(labelStyle);
                // T???ng s??? th??ng (K)
                cell = row.createCell(10, CellType.STRING);
                cell.setCellValue(df.format(Float.parseFloat(totalOrder.getpDesireQuantity()+"")) + " (th??ng)");
                cell.setCellStyle(labelStyle);
            } else {
                utils.alert("info", Alert.AlertType.INFORMATION, "Th??ng b??o", "Kh??ng th??? t??nh to??n ???????c s??? kh???i ho???c t???ng th??ng. Vui l??ng ki???m tra l???i.").showAndWait();
            }

            // wopp.work_order_id (Z)
            cell = row.createCell(25, CellType.STRING);
            cell.setCellValue(totalOrder.getWoID());
            cell.setCellStyle(lockedCell);

            rownum +=1;

            row = sheet.createRow(rownum);
//            row.setHeight((short) 500);

            // NCC (C)
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue("NCC");
            cell.setCellStyle(labelStyle);
            // T??n bao b?? (D)
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue("T??n bao b??");
            cell.setCellStyle(labelStyleLeft);
            // K??ch th?????c (E)
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue("K??ch th?????c");
            cell.setCellStyle(labelStyle);
            // ??VT (F)
            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue("??VT");
            cell.setCellStyle(labelStyle);
            // C???n ?????t (G)
            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue("C???n ?????t");
            cell.setCellStyle(labelStyle);
            // T???n (H)
            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue("T???n");
            cell.setCellStyle(labelStyle);
            // Th???c ?????t (I)
            cell = row.createCell(8, CellType.STRING);
            cell.setCellValue("Th???c ?????t");
            cell.setCellStyle(labelStyle);
            // Th??ng tin in s???n (J)
            cell = row.createCell(9, CellType.STRING);
            cell.setCellValue("Th??ng tin in s???n");
            cell.setCellStyle(labelStyle);
            // S??? kh???i (K)
            cell = row.createCell(10, CellType.STRING);
            cell.setCellValue("S??? kh???i");
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

                    // wopp.product_id (B)
                    cell = row.createCell(1, CellType.STRING);
                    cell.setCellValue(workOrderProductPackagingDAO.getDataByID(productList.get(i).getId()).getProduct_id());
                    cell.setCellStyle(lockedCell);

                    // Th???c ?????t
                    cell = row.createCell(2, CellType.STRING);
                    cell.setCellValue(productList.get(i).getOrdinalNumbers() + "/ " + productList.get(i).getProductName() + " (L???n "+productList.get(i).getOrderTimes()+")");
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
                            // wopp.id (A)
                            cell = row.createCell(0, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getId());
                            cell.setCellStyle(lockedCell);

                            // wopp.work_order_id (Z)
//                            cell = row.createCell(25, CellType.STRING);
//                            cell.setCellValue(workOrderProductPackagingDAO.getDataByID(packagingList.get(j).getId()).getWork_order_id());
//                            cell.setCellStyle(lockedCell);

                            // NCC (C)
                            cell = row.createCell(2, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPackagingSuplier());
                            cell.setCellStyle(cellStyleLocked);
                            // T??n BB (Qui c??ch) (D)
                            cell = row.createCell(3, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPackagingName() + (packagingList.get(j).getPrintStatus() != null? " ("+ packagingList.get(j).getPrintStatus() +")" : "") + (!packagingList.get(j).getPackagingSpecification().equals("") ? " ("+ packagingList.get(j).getPackagingSpecification() +")" : ""));
                            cell.setCellStyle(cellStyleLeftLocked);
                            // K??ch th?????c (E)
                            cell = row.createCell(4, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPackagingDimension());
                            cell.setCellStyle(cellStyleLocked);
                            // ??VT (F)
                            cell = row.createCell(5, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getUnit());
                            cell.setCellStyle(cellStyleLocked);
                            // C???n (G)
                            cell = row.createCell(6, CellType.STRING);
                            cell.setCellValue(Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()+"") > 0 ? formatter.format(Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()+"")) : "");
                            cell.setCellStyle(cellStyleLocked);
                            // T???n (H)
                            cell = row.createCell(7, CellType.STRING);
                            cell.setCellValue(Float.parseFloat(packagingList.get(j).getStock()+"") > 0 ? formatter.format(Float.parseFloat(packagingList.get(j).getStock()+"")) : "");
                            cell.setCellStyle(cellStyle);
                            // Th???c ?????t (I)
                            cell = row.createCell(8, CellType.STRING);
                            cell.setCellValue(Float.parseFloat(packagingList.get(j).getActualQuantity()+"") > 0 ? formatter.format(Float.parseFloat(packagingList.get(j).getActualQuantity()+"")) : "");
                            cell.setCellStyle(cellStyle);
                            // Th??ng tin in s???n (J)
                            cell = row.createCell(9, CellType.STRING);
                            cell.setCellValue(packagingList.get(j).getPrintStatus());
                            cell.setCellStyle(cellStyle);
                            // S??? kh???i (K)
                            if(packagingList.get(j).getMain().equals("1")) {
                                cell = row.createCell(10, CellType.STRING);
                                float mul = 1;
                                String[] listD = packagingList.get(j).getPackagingDimension().replace(" ", "").split("x");
                                if( listD.length > 0) {
                                    for(String d : listD) {
                                        mul *= Float.parseFloat(d);
                                    }
                                }
                                cell.setCellValue(packagingList.get(j).getMain().equals("1") ? df.format(mul * Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()))  : "");
                                cell.setCellStyle(cellStyle);
                            }
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
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ??? ????a!").showAndWait();
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
            if(!woList.contains(orderObservableList.get(i).getWoName())) {
                if(orderObservableList.get(i).getWoName().contains("+")) {
                    woList += orderObservableList.get(i).getWoName().trim().replace("+","") + (Integer.parseInt(orderObservableList.get(i).getOrderTimes()) > 1 ? " (L???n "+orderObservableList.get(i).getOrderTimes()+")" : "");
                } else {
                    woList += orderObservableList.get(i).getWoName() + (Integer.parseInt(orderObservableList.get(i).getOrderTimes()) > 1 ? " (L???n "+orderObservableList.get(i).getOrderTimes()+")" : "");
                }
                woList += (i<orderObservableList.size()-1) ? " " : "";
            }
        }

        ShipAddressDAO shipAddressDAO = new ShipAddressDAO();
        ShipAddress shipAddress = shipAddressDAO.getDataByCode(shippingCode);

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            if (!doc.getDocument().getBody().isSetSectPr()) {
                doc.getDocument().getBody().addNewSectPr();
            }
            CTSectPr section = doc.getDocument().getBody().getSectPr();

            if(!section.isSetPgSz()) {
                section.addNewPgSz();
            }
            CTPageSz pageSize = section.getPgSz();
            // A4 = 595x842 => multiply 20 since BigInteger represents 1/20 Point
            // for more detail about paper sizes, check the below url
            // https://www.gnu.org/software/gv/manual/html_node/Paper-Keywords-and-paper-size-in-points.html
            pageSize.setH(BigInteger.valueOf(16840));
            pageSize.setW(BigInteger.valueOf(11900));
            pageSize.setOrient(STPageOrientation.PORTRAIT);

            CTPageMar pageMar = section.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(620L));
            pageMar.setTop(BigInteger.valueOf(300L));
            pageMar.setRight(BigInteger.valueOf(720L));
            pageMar.setBottom(BigInteger.valueOf(300L));

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

            run.setText("SEAVINA, ng??y " + _date.getDayOfMonth() + ", th??ng " + _date.getMonthValue() + ", n??m "+ _date.getYear());

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            paragraph.setSpacingAfter(0);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("????N ?????T H??NG");

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(false);
            run.setFontSize(10);
            run.setText("(S???: "+supplierCode+".\t\t/"+_date.getYear()+")");

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(11);
            run.setBold(true);
            run.setText("K??nh g???i: "+supplier.getName());
            run.addBreak();
            run.setText("         "+supplier.getDeputy());
            run.addBreak();
            run.setText("?????a ch???: "+supplier.getAddress());
            run.addBreak();
            run.setText((supplier.getPhone() != null ? "??i???n tho???i: "+ supplier.getPhone() : "") + (supplier.getFax() != null ? "          Fax: " + supplier.getFax() : ""));
            run.addBreak();
            run.setText(SEAVINA.getName()+" xin g???i ????N ?????T H??NG ?????n Qu?? C??ng Ty v???i chi ti???t nh?? sau:");

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
            utils.setHeaderRowforSingleCell(row.getCell(1), "T??n Bao B??", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui c??ch (CM)", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(3), "??VT", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL ?????t", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(5), "????n gi?? (Ch??a VAT)", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(6), "M??", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
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
                utils.setHeaderRowforSingleCell(row.getCell(5), "??/"+orderObservableList.get(i).getpUnit(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
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

            utils.setHeaderRowforSingleCell(row.getCell(0), "?????c ??i???m v?? qui c??ch", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Ch???t l?????ng: ?????m b???o ????ng ch???t l?????ng v?? ?????nh l?????ng bao b?? nh?? m???u ch??o h??ng.", 11, true, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- S???n xu???t theo m???u x??c nh???n ng??y: ", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), _date.getDayOfMonth() + "/" + _date.getMonthValue() + "/"+ _date.getYear(), 12, true, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao b?? ph???i l??m ????ng k??ch th?????c, m??u s???c, th??ng tin nh?? ???? x??c nh???n.", 11, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao b?? ph???i ?????t ti??u chu???n h??ng Th???y s???n xu???t kh???u. S??? l?????ng l??m ?????, kh??ng th???a, kh??ng thi???u.", 11, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Ch??ng t??i s??? tr??? l???i c??c l?? h??ng l??m sai qui c??ch v?? kh??ng ????ng c??c y??u c???u tr??n.", 11, true, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Khi giao h??ng vui l??ng li??n h???: ", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), shipAddress.getStocker() + " ("+shipAddress.getStocker_phone()+")", 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            row = table.getRow(1);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Ng??y giao h??ng", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            row = table.getRow(2);
            utils.setHeaderRowforSingleCell(row.getCell(0), "?????a ch??? giao h??ng", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), shipAddress.getName(), 11, true, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), shipAddress.getAddress(), 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

            row = table.getRow(3);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Ch?? ??", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "M???i thay ?????i ho???c c?? v???n ????? g?? ch??a r?? ph???i b??o l???i ngay v???i "+SEAVINA.getName()+" tr?????c khi ti???n h??nh.", 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);

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

            utils.setHeaderRowforSingleCell(row.getCell(0), "B??n b??n h??ng", 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "B??n mua h??ng", 11, false, true, ParagraphAlignment.RIGHT, UnderlinePatterns.NONE);


            if(file != null && orderObservableList.size() > 0) {
                // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/"+ supplierCode + "-"+woList.trim().replace(" ", "+")+ " ("+shipAddress.getCode_address()+")"+ ".docx"))
                {
                    doc.write(out);
//                    utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ??? ????a!").showAndWait();
                }
            }

        } catch (Exception e) {
//            System.out.println(e);
            utils.alert("err", Alert.AlertType.ERROR, "L???i!", e.getMessage()).showAndWait();
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

            if (!doc.getDocument().getBody().isSetSectPr()) {
                doc.getDocument().getBody().addNewSectPr();
            }
            CTSectPr section = doc.getDocument().getBody().getSectPr();

            if(!section.isSetPgSz()) {
                section.addNewPgSz();
            }
            CTPageSz pageSize = section.getPgSz();
            // A4 = 595x842 => multiply 20 since BigInteger represents 1/20 Point
            // for more detail about paper sizes, check the below url
            // https://www.gnu.org/software/gv/manual/html_node/Paper-Keywords-and-paper-size-in-points.html
            pageSize.setH(BigInteger.valueOf(16840));
            pageSize.setW(BigInteger.valueOf(11900));
            pageSize.setOrient(STPageOrientation.PORTRAIT);

            CTPageMar pageMar = section.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(620L));
            pageMar.setTop(BigInteger.valueOf(300L));
            pageMar.setRight(BigInteger.valueOf(720L));
            pageMar.setBottom(BigInteger.valueOf(300L));

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
            utils.setHeaderRowforSingleCell(row.getCell(1), "C???NG H??A X?? H???I CH??? NGH??A VI???T NAM", 10, true, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "?????c l???p - T??? do - H???nh Ph??c", 10, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.SINGLE);
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

            run.setText("SEAVINA, ng??y " + _date.getDayOfMonth() + ", th??ng " + _date.getMonthValue() + ", n??m "+ _date.getYear());

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("????? NGH??? BAO B?? "+utils.getListWorkOrderName(idList) + (orderTimes > 1 ? " (L???N "+orderTimes+")" : ""));

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(11);
            run.setBold(false);
            run.setText("K??nh g???i: Ph??ng Mua H??ng Minh T??m");
            run.addBreak();
            run.setText("??i???n tho???i: 02923. 3600063");
            run.addBreak();
            run.setText("C??ng ty CP SEAVINA (Ph??ng Kinh Doanh) xin g???i Phi???u ????? ngh??? mua h??ng v???i chi ti???t nh?? sau:");

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
            utils.setHeaderRowforSingleCell(row.getCell(1), "T??n Bao B??", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui c??ch (CM)", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(3), "??VT", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL ?????t", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Ghi ch??", 11, false, true, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
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

                float residualNumber = Float.parseFloat(orderObservableList.get(i).getpResidualQuantity()+"") > 0 ? Float.parseFloat(orderObservableList.get(i).getpTotal()+"") + Float.parseFloat(orderObservableList.get(i).getpStock()+"") - Float.parseFloat(orderObservableList.get(i).getpResidualQuantity()+"") - Float.parseFloat(orderObservableList.get(i).getpDesireQuantity()+"") : 0;

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + ( orderObservableList.get(i).getpIsPrinted() != null ? " ("+ orderObservableList.get(i).getpIsPrinted() +")" : "") + ( !orderObservableList.get(i).getpSpecs().equals("") ? " ("+ orderObservableList.get(i).getpSpecs() +")" : ""), 11, false, false, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(5), residualNumber > 0 ? "D?? "+formatter.format(residualNumber) : "", 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 11, false, false, ParagraphAlignment.CENTER, UnderlinePatterns.NONE);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(11);
            run.setBold(false);
            run.setText("L??u ??: M???i thay ?????i hay c?? v???n ????? ch??a r?? ph???i ph???n h???i l???i v???i b??? ph???n li??n quan tr?????c khi th???c hi???n ????? tr??nh sai s??t c?? th??? x???y ra.");

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "\tPh??ng Kinh Doanh", 11, false, true, ParagraphAlignment.LEFT, UnderlinePatterns.NONE);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Ng?????i L???p Bi???u\t", 11, false, true, ParagraphAlignment.RIGHT, UnderlinePatterns.NONE);

            if(orderObservableList.size() > 0) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/DE NGHI BB "+ utils.getListWorkOrderName(idList) + (orderTimes > 1 ? " (L"+orderTimes+")" : "")+".docx"))
                    {
                        doc.write(out);
//                        utils.alert("info", Alert.AlertType.INFORMATION, "Xu???t file th??nh c??ng!", "File ???? ???????c l??u v??o ??? ????a!").showAndWait();
                    }
                }
            }

        } catch (Exception e) {
//            System.out.println(e);
            utils.alert("err", Alert.AlertType.ERROR, "L???i!", e.getMessage()).showAndWait();
        }
    }
}
