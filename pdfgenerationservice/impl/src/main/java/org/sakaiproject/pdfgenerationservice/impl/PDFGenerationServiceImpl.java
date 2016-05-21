package org.sakaiproject.pdfgenerationservice.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.emailtemplateservice.model.EmailTemplate;
import org.sakaiproject.emailtemplateservice.service.EmailTemplateService;
import org.sakaiproject.pdfgenerationservice.api.PDFGenerationService;
import org.sakaiproject.util.ResourceLoader;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.ColumnText;
import java.util.logging.Level;

/**
 * Implementation of {@link PDFGenerationService}
 */
public class PDFGenerationServiceImpl implements PDFGenerationService {

    private static final Logger log = Logger.getLogger(PDFGenerationServiceImpl.class);

    @Setter
    private EmailTemplateService emailTemplateService;
    @Setter
    private ServerConfigurationService serverConfigurationService;

    private List<String> htmlList = new ArrayList<String>();

    /**
     * init - perform any actions required here for when this bean starts up
     */
    public void init() {
        log.info("init");
    }

    @Override
    /**
     * 固定文字列、可変文字列を使用してPDFを作成する
     *
     * @param response
     * @param page
     * @param pages
     * @param templateKey
     * @return
     */
    public boolean outputPdf(HttpServletResponse response, List<String> page, List<List<String>> pages, String templateKey) {

        if (!existsTemplate(templateKey)) {
            log.warn("Template not found. templatePath=" + templateKey);
            return false;
        }
        if (pages == null || pages.size() == 0) {
            outputPdf(response, page, templateKey);
        } else {
            String htmlMessage = getRenderedHtmlMessageFix(page, templateKey);
            if (htmlMessage == null) {
                log.warn("template not correct");
                return false;
            }

            boolean valiableCheck = checkValiableData(pages);
            if (!valiableCheck) {
                log.warn("data not correct");
                return false;
            }

            List<List<String>> matrix = new ArrayList<List<String>>();
            matrix = createTemplateInfo(htmlMessage);

            if (matrix.size() == 0) {
                log.warn("template not correct");
                return false;
            }

            htmlList = getRenderedHtmlMessageValiable(matrix, pages, htmlMessage);
            if (htmlList == null || htmlList.size() < 0) {
                log.warn("data not correct");
                return false;
            }

            if (createPdf(response, htmlList)) {
                return true;
            } else {
                log.warn("pdf creation failure");
                return false;
            }
        }

        return true;
    }

    @Override
    /**
     * 固定用文字列を使用してPDFを作成する
     *
     * @param response
     * @param page
     * @param templateKey
     * @return
     */
    public boolean outputPdf(HttpServletResponse response, List<String> page, String templateKey) {
        if (!existsTemplate(templateKey)) {
            log.warn("Template not found. templatePath=" + templateKey);
            return false;
        }
        String htmlMessage = getRenderedHtmlMessageFix(page, templateKey);
        if (htmlMessage == null) {
            return false;
        }
        String pageNumber = "1/1";
        String pageNumber_template = "\\{\\$pageNumber\\}";
        htmlMessage = htmlMessage.replaceFirst(pageNumber_template, pageNumber);
        htmlList.add(htmlMessage);

        if (createPdf(response, htmlList)) {
            return true;
        } else {
            log.warn("pdf creation failure");
            return false;
        }
    }

    /**
     * templateが存在するかのチェックを行う
     *
     * @param templatePath
     * @return
     */
    private boolean existsTemplate(String templatePath) {
        Locale locale = new ResourceLoader().getLocale();
        return emailTemplateService.templateExists(templatePath, locale);
    }

    /**
     * テンプレート内可変文字列情報を取得する
     *
     * @param htmlMessage
     * @return
     */
    private List<List<String>> createTemplateInfo(String htmlMessage) {

        String regex = "\\{\\$data-\\d\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(htmlMessage);
        List<List<String>> matrixList = new ArrayList<List<String>>();
        List<String> matrixData = new ArrayList<String>();
        int containsCount = 0;
        while (matcher.find()) {

            String findMessage = matcher.group();
            if (matrixData.contains(findMessage)) {
                if (matrixList.size() == 0) {
                    matrixList.add(matrixData);
                    matrixData = new ArrayList<String>();
                    matrixData.add(findMessage);
                } else {
                    if (matrixList.get(0).equals(matrixData)) {
                        matrixList.add(matrixData);
                        matrixData = new ArrayList<String>();
                        matrixData.add(findMessage);
                    } else {
                        matrixList.clear();
                        return matrixList;
                    }
                }
            } else {
                matrixData.add(findMessage);
            }
        }
        if (matrixList.get(0).equals(matrixData)) {
            matrixList.add(matrixData);
        } else {
            matrixList.clear();
            return matrixList;
        }
        return matrixList;
    }

    /**
     * 可変文字列リストのチェックを行う
     *
     * @param pages
     * @return
     */
    private boolean checkValiableData(List<List<String>> pages) {
        for (int page = 0; page < pages.size(); page++) {
            if (page != pages.size() - 1) {
                List<String> list1 = pages.get(page);
                List<String> list2 = pages.get(page + 1);
                if (list1.size() != list2.size()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * テンプレートを固定文字列で置換する
     *
     * @param page
     * @param templateKey
     * @return
     */
    private String getRenderedHtmlMessageFix(List<String> page, String templateKey) {
        Locale locale = new ResourceLoader().getLocale();
        EmailTemplate emailTemplate = emailTemplateService.getEmailTemplate(templateKey, locale);
        String htmlMessage = emailTemplate.getHtmlMessage();

        String regex = "\\{\\$\\d\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(htmlMessage);

        int tem_count = 0;

        while (matcher.find()) {
            tem_count++;
        }

        if (tem_count != page.size()) {
            log.warn("PDFGenerationServiceImpl: The number of parapeters does not match between template and actual data.");
            return null;
        }

        for (int i = 0; i < page.size(); i++) {
            String checkvalue = "{$" + i + "}";
            int exist = htmlMessage.indexOf(checkvalue);
            if (exist == -1) {
                return null;
            } else {
                htmlMessage = htmlMessage.replace("{$" + i + "}", page.get(i));
            }
        }

        int check = page.size();
        String checkvalue = "{$" + check + "}";
        int exist = htmlMessage.indexOf(checkvalue);
        if (exist != -1) {
            return null;
        }

        return htmlMessage;
    }

    /**
     * テンプレートを可変文字列で置換する
     *
     * @param matrix
     * @param pages
     * @param htmlMessage
     * @return
     */
    private List<String> getRenderedHtmlMessageValiable(List<List<String>> matrix, List<List<String>> pages, String htmlMessage) {
        double templateChangeSize = matrix.size();
        double replaceSize = pages.size();
        double pageSize = replaceSize / templateChangeSize;
        int pagesize = (int) Math.ceil(pageSize);
        int pageNo = 0;
        int nextpage = 0;

        String htmlMessage_change = htmlMessage;
        for (int i = 0; i < pages.size(); i++) {
            List<String> replaceData = pages.get(i);
            for (int m = 0; m < replaceData.size(); m++) {
                int n = m + 1;
                String replaceString = "\\{\\$data\\-" + n + "\\}";
                htmlMessage_change = htmlMessage_change.replaceFirst(replaceString, replaceData.get(m));
            }
            nextpage = nextpage + 1;
            if (i == templateChangeSize - 1) {
                pageNo = pageNo + 1;
                String pageNumber = pageNo + "/" + pagesize;
                String pageNumber_template = "\\{\\$pageNumber\\}";
                htmlMessage_change = htmlMessage_change.replaceFirst(pageNumber_template, pageNumber);
                htmlList.add(htmlMessage_change);
                htmlMessage_change = htmlMessage;
                templateChangeSize = templateChangeSize + nextpage;
                nextpage = 0;
            }
            if (i == pages.size() - 1) {
                pageNo = pageNo + 1;
                String pageNumber = pageNo + "/" + pagesize;
                String pageNumber_template = "\\{\\$pageNumber\\}";
                Pattern pattern = Pattern.compile(pageNumber_template);
                Matcher matcher = pattern.matcher(htmlMessage_change);
                if (matcher.find()) {
                    htmlMessage_change = htmlMessage_change.replaceFirst(pageNumber_template, pageNumber);
                }
                htmlList.add(htmlMessage_change);
            }
        }

        List<String> matrix_mes = matrix.get(0);
        for (int j = 0; j < htmlList.size(); j++) {
            String html = htmlList.get(j);
            String regex = "\\{\\$data-\\d\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                html = html.replaceAll(regex, "　");
                htmlList.set(j, html);
            }
        }
        return htmlList;
    }

    /**
     * Pdf作成を行う
     *
     * @param response
     * @param htmllist
     * @return
     */
    private boolean createPdf(HttpServletResponse response, List<String> htmllist) {

        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = sdf.format(date);
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + filename + ".pdf");
            ITextRenderer renderer = new ITextRenderer();
            setAdditionalFonts(renderer);

            for (int i = 0; i < htmlList.size(); i++) {
                renderer.setDocumentFromString(htmlList.get(i));
                renderer.layout();
                if (i == 0) {
                    renderer.createPDF(response.getOutputStream(), false, 0);
                } else {
                    renderer.writeNextDocument();
                }
            }
            renderer.finishPDF();
            htmlList.clear();
            response.getOutputStream().close();
            return true;
        } catch (DocumentException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }

    /*
     * フォント設定
     */
    /**
     * fontの設定を行う
     *
     * @param renderer
     */
    private void setAdditionalFonts(ITextRenderer renderer) {
        String[] fontPaths = serverConfigurationService.getStrings("kyoto_u.pdf.additional.font");
        if (fontPaths == null) {
            return;
        }
        ITextFontResolver fontResolver = renderer.getFontResolver();
        for (String fontPath : fontPaths) {
            try {
                fontResolver.addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } catch (DocumentException e) {
                log.warn(e.getMessage(), e);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

}
