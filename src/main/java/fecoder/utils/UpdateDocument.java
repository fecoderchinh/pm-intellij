package fecoder.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class UpdateDocument {
    public void updateDocument(String input, String output, String[] data)
        throws IOException {

        try (XWPFDocument doc = new XWPFDocument(
                Files.newInputStream(Paths.get(input)))
        ) {

            List<XWPFParagraph> xwpfParagraphList = doc.getParagraphs();
            //Iterate over paragraph list and check for the replaceable text in each paragraph
            for (XWPFParagraph xwpfParagraph : xwpfParagraphList) {
                for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                    String docText0 = xwpfRun.getText(0);
                    //replacement and setting position
                    docText0 = docText0.replace("${name}", data[0]);
                    xwpfRun.setText(docText0, 0);

                    String docText1 = xwpfRun.getText(0);
                    //replacement and setting position
                    docText1 = docText1.replace("${id}", data[1]);
                    xwpfRun.setText(docText1, 0);

                    String docText2 = xwpfRun.getText(0);
                    //replacement and setting position
                    docText2 = docText2.replace("${address}", data[2]);
                    xwpfRun.setText(docText2, 0);
                }
            }

            // save the docs
            try (FileOutputStream out = new FileOutputStream(output)) {
                doc.write(out);
            }

        }

    }
}
