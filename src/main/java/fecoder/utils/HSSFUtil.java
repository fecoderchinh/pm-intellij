package fecoder.utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class HSSFUtil {
    public static HSSFCellStyle createStyle(HSSFWorkbook workbook, BorderStyle borderStyle, short fontSize, short textColor, boolean boldText, boolean wrapText, short backgroundColor, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        HSSFFont font = workbook.createFont();
        font.setBold(boldText);
        font.setFontName("Calibri");
        font.setFontHeightInPoints(fontSize);
        font.setColor(textColor);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setWrapText(wrapText);
        style.setBorderBottom(borderStyle);
        style.setBorderTop(borderStyle);
        style.setBorderRight(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setFillForegroundColor(backgroundColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(hAlign);
        style.setVerticalAlignment(vAlign);
        return style;
    }
}
