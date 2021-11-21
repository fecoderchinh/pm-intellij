package fecoder.utils;

import fecoder.DAO.OrderBySupplierDAO;
import fecoder.DAO.SupplierDAO;
import fecoder.DAO.WorkOrderProductPackagingDAO;
import fecoder.DAO.WorkProductionDAO;
import fecoder.models.OrderBySupllier;
import fecoder.models.Supplier;
import fecoder.models.WorkOrder;
import fecoder.models.WorkProduction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportWordDocument {

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
        String _fontFamily = "Arial";

        ObservableList<WorkProduction> productList = FXCollections.observableArrayList(workProductionDAO.getProductList(workOrder.getId()+""));

        boolean hasData = false;

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

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
            utils.setHeaderRowforSingleCell(row.getCell(1), "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Độc lập - Tự do - Hạnh phúc", 10, true, true, ParagraphAlignment.CENTER);

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

                    utils.setHeaderRowforSingleCell(row.getCell(0), ("#"+_product.getOrdinalNumbers() + "/ " + _product.getProductName()).toUpperCase(), 14, false, true, ParagraphAlignment.LEFT);

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

                    utils.setHeaderRowforSingleCell(row.getCell(0), "Tên Bao Bì", 10, false, true, ParagraphAlignment.CENTER);
                    utils.setHeaderRowforSingleCell(row.getCell(1), "Qui cách (CM)", 10, false, true, ParagraphAlignment.CENTER);
                    utils.setHeaderRowforSingleCell(row.getCell(2), "ĐVT", 10, false, true, ParagraphAlignment.CENTER);
                    utils.setHeaderRowforSingleCell(row.getCell(3), "SL Đặt", 10, false, true, ParagraphAlignment.CENTER);
                    utils.setHeaderRowforSingleCell(row.getCell(4), "Tồn", 10, false, true, ParagraphAlignment.CENTER);
                    utils.setHeaderRowforSingleCell(row.getCell(5), "Thực tế", 10, false, true, ParagraphAlignment.CENTER);

                    ObservableList<WorkProduction> packagingList = FXCollections.observableArrayList(
                            workProductionDAO.getPackagingList(
                                    workOrder.getId()+"", // work_order_product.work_order_id
                                    workOrderProductPackagingDAO.getDataByID(_product.getId()).getProduct_id(), // work_order_product.product_id
                                    _product.getOrdinalNumbers() // work_order_product.ordinal_num
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

                            utils.setHeaderRowforSingleCell(row.getCell(0), packagingList.get(j).getPackagingName() + "("+packagingList.get(j).getPrintStatus()+")", 10, false, false, ParagraphAlignment.LEFT);
                            utils.setHeaderRowforSingleCell(row.getCell(0), "("+ packagingList.get(j).getPackagingSpecification() +")", 10, false, false, ParagraphAlignment.LEFT);
                            utils.setHeaderRowforSingleCell(row.getCell(1), packagingList.get(j).getPackagingDimension(), 10, false, false, ParagraphAlignment.CENTER);
                            utils.setHeaderRowforSingleCell(row.getCell(2), packagingList.get(j).getUnit(), 10, false, false, ParagraphAlignment.CENTER);
                            utils.setHeaderRowforSingleCell(row.getCell(3), formatter.format(Float.parseFloat(packagingList.get(j).getWorkOrderQuantity()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
                            utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(packagingList.get(j).getStock()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
                            utils.setHeaderRowforSingleCell(row.getCell(5), formatter.format(Float.parseFloat(packagingList.get(j).getActualQuantity()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
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
     * Exporting Order Document which separate by Supplier
     *
     * @param file File
     * @param idList list or single id from work_order_product_packaging.work_order_id
     * @param code list or single id from suppliers.code
     * */
    public static void data2DocOfOrderBySupplier(File file, String idList, String code, String date) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";
        SupplierDAO supplierDAO = new SupplierDAO();
        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();
        Utils utils = new Utils();

        ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getListBySupplierCode(idList, code));
        Supplier supplier = supplierDAO.getDataByCode(code);

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

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
            utils.setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc I,P.Trà Nóc, Q. Bình Thủy, TP. Cần Thơ ,Việt Nam", 10, true, false, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "1801141886", 10, true, false, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tel: 0292.3744950  Fax: 0292.3743678", 10, true, false, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "(Ms. Nhung: 0946.886 868, Ms Trinh: 0918.755729)", 10, true, false, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "TT5.6.1/ KD2-BM3", 10, false, false, ParagraphAlignment.CENTER);

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
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("ĐƠN ĐẶT HÀNG");

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(10);
            run.setText("(Số: "+code+".\t\t/"+_date.getYear()+")");
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setBold(true);
            run.setText("Kính gửi: "+supplier.getName());
            run.addBreak();
            run.setText("Đại diện: "+supplier.getDeputy());
            run.addBreak();
            run.setText("Địa chỉ: "+supplier.getAddress());
            run.addBreak();
            run.setText("Điện thoại: "+ supplier.getPhone() + (!supplier.getFax().equals("") ? "\t\t"+"Fax: " + supplier.getFax() : ""));
            run.addBreak();
            run.setText("Công ty CP SEAVINA xin gửi ĐƠN ĐẶT HÀNG đến Quý Công Ty với chi tiết như sau:");

            table = doc.createTable(1, 8);
            table.setWidth("100%");

            row = table.getRow(0);
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

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(3), "ĐVT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Mã", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(6), "Đơn giá (Chưa VAT)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(7), "LSX", 10, false, true, ParagraphAlignment.CENTER);

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

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + (orderObservableList.get(i).getpIsPrinted() != null ? (orderObservableList.get(i).getpIsPrinted() != null ? "("+orderObservableList.get(i).getpIsPrinted()+")" : "") : ""), 10, false, false, ParagraphAlignment.LEFT);
                utils.setHeaderRowforSingleCell(row.getCell(1), " ("+ orderObservableList.get(i).getpSpecs() +")", 10, false, false, ParagraphAlignment.LEFT);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(5), orderObservableList.get(i).getpCode(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(6), "đ/"+orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(7), orderObservableList.get(i).getWoName(), 10, false, false, ParagraphAlignment.CENTER);
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

            utils.setHeaderRowforSingleCell(row.getCell(0), "Đặc điểm và qui cách", 10, false, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Chất lượng: Đảm bảo đúng chất lượng và định lượng bao bì như mẫu chào hàng.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Sản xuất theo mẫu xác nhận ngày: " + _date.getDayOfMonth() + "/" + _date.getMonthValue() + "/"+ _date.getYear(), 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải làm đúng kích thước, màu sắc, thông tin như đã xác nhận.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Bao bì phải đạt tiêu chuẩn hàng Thủy sản xuất khẩu. Số lượng làm đủ, không thừa, không thiếu.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Chúng tôi sẽ trả lại các lô hàng làm sai qui cách và không đúng các yêu cầu trên.", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "- Khi giao hàng vui lòng liên hệ: Mr. Duy (0932.830.900).", 10, false, false, ParagraphAlignment.LEFT);

            row = table.getRow(1);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Ngày giao hàng", 10, false, false, ParagraphAlignment.LEFT);

            row = table.getRow(2);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Địa chỉ giao hàng", 10, false, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Công ty CP SEAVINA", 10, true, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Lô 16A-18, KCN Trà Nóc 1, P. Trà Nóc, Q. Bình Thủy, TP. Cần Thơ", 10, false, false, ParagraphAlignment.LEFT);

            row = table.getRow(3);
            utils.setHeaderRowforSingleCell(row.getCell(0), "Chú ý", 10, false, false, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Mọi thay đổi hoặc có vấn đề gì chưa rõ phải báo lại ngay với Công Ty CP SEAVINA trước khi tiến hành.", 10, false, false, ParagraphAlignment.LEFT);

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

            utils.setHeaderRowforSingleCell(row.getCell(0), "Bên bán hàng", 10, false, true, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Bên mua hàng", 10, false, true, ParagraphAlignment.RIGHT);


            if(file != null) {
                // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                try (FileOutputStream out = new FileOutputStream(file.getPath()+"/"+ code + "-"+utils.getListWorkOrderName(idList)+".docx"))
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
     * */
    public static void data2DocOfOrderList(File file, String idList, String date) {
        String imgFile = "e:\\java_platform\\docs-data\\logo.jpg";
        String _fontFamily = "Arial";

        Utils utils = new Utils();
        OrderBySupplierDAO orderBySupplierDAO = new OrderBySupplierDAO();

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph paragraph;
            XWPFRun run;
            XWPFTable table;
            XWPFTableRow row;

            /*
             * Word Header
             * */
            XWPFHeader header = doc.createHeader(HeaderFooterType.DEFAULT);

            table = header.createTable(1, 3);
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
            run.setText("Tel: 0292.3744950");
            run.addBreak();
            run.setText("Fax: 0292.3743678");
            utils.setHeaderRowforSingleCell(row.getCell(1), "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 10, true, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Độc lập - Tự do - Hạnh Phúc", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "", 10, false, false, ParagraphAlignment.CENTER);

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
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setBold(true);
            run.setFontSize(14);
            run.setText("ĐỀ NGHỊ BAO BÌ "+utils.getListWorkOrderName(idList));
            run.addBreak();

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
//            run.setBold(true);
            run.setFontSize(10);
            run.setBold(true);
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

            utils.setHeaderRowforSingleCell(row.getCell(0), "STT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Tên Bao Bì", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(2), "Qui cách (CM)", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(3), "ĐVT", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(4), "SL Đặt", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(5), "Ghi chú", 10, false, true, ParagraphAlignment.CENTER);
            utils.setHeaderRowforSingleCell(row.getCell(6), "LSX", 10, false, true, ParagraphAlignment.CENTER);

            ObservableList<OrderBySupllier> orderObservableList = FXCollections.observableArrayList(orderBySupplierDAO.getList(idList));
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

                utils.setHeaderRowforSingleCell(row.getCell(0), (i+1)+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(1), orderObservableList.get(i).getpName() + "("+ orderObservableList.get(i).getpIsPrinted() +")" + " ("+ orderObservableList.get(i).getpSpecs() +")", 10, false, false, ParagraphAlignment.LEFT);
                utils.setHeaderRowforSingleCell(row.getCell(2), orderObservableList.get(i).getpDimension(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(3), orderObservableList.get(i).getpUnit(), 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(4), formatter.format(Float.parseFloat(orderObservableList.get(i).getpTotal()+""))+"", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(5), residualNumber > 0 ? "Dư "+formatter.format(residualNumber) : "", 10, false, false, ParagraphAlignment.CENTER);
                utils.setHeaderRowforSingleCell(row.getCell(6), orderObservableList.get(i).getWoName(), 10, false, false, ParagraphAlignment.CENTER);
            }

            paragraph = doc.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
            run = paragraph.createRun();
            run.setFontFamily(_fontFamily);
            run.setFontSize(10);
            run.setBold(false);
            run.setText("Lưu ý: Mọi thay đổi hay có vấn đề chưa rõ phải phản hồi lại với bộ phận liên quan trước khi thực hiện để tránh sai sót có thể xảy ra.");
            run.addBreak();

            table = doc.createTable(1, 2);
            table.setWidth("100%");
            table.removeBorders();
            row = table.getRow(0);
            row.getCell(0).setWidth("50%");
            row.getCell(1).setWidth("50%");
            row.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            row.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            utils.setHeaderRowforSingleCell(row.getCell(0), "Phòng Kinh Doanh", 10, false, true, ParagraphAlignment.LEFT);
            utils.setHeaderRowforSingleCell(row.getCell(1), "Người Lập Biểu", 10, false, true, ParagraphAlignment.RIGHT);

            if(orderObservableList.size() > 0) {
                if(file != null) {
                    // save it to .docx file
//                try (FileOutputStream out = new FileOutputStream(file.getPath())) // for fileChooser
                    try (FileOutputStream out = new FileOutputStream(file.getPath()+"/DE NGHI BB "+ utils.getListWorkOrderName(idList) +".docx"))
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
