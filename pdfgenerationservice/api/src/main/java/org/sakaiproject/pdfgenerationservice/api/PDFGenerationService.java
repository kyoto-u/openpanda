package org.sakaiproject.pdfgenerationservice.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

/**
 * An example logic interface
 */
public interface PDFGenerationService {
    /**
     * PDFを生成してresponseに書き込む.
     * @param pages PDFに埋め込む値
     * @param templateKey テンプレートのキー
     * @param response HttpServletResponse
     * @return PDF生成および出力ストリームへの出力が正常に終了したときtrueを返す.
     */
    //public boolean outputPdf(HttpServletResponse response,ArrayList<ArrayList<Object>> pages, String templateKey);

    public boolean outputPdf(HttpServletResponse response,List<String> page,String templateKey);
    public boolean outputPdf(HttpServletResponse response,List<String> page,List<List<String>> pages,String templateKey);



}
