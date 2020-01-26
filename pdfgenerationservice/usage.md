##PDFの日本語フォント指定
PDF生成に日本語フォントを使用するときは、テンプレートHTMLのCSSにフォント名を指定しなければなりません。日本語フォントが指定されていない場合、日本語部分が空白のPDFが生成されます。

####設定例
```
<style type="text/css">
* {
  font-family: 'MS Gothic'
}
</style>
```

その他のフォントを使用する場合は、sakai.propertiesにフォントファイルのパスを設定します.
####設定例
```
# MS Gothic
kyoto_u.pdf.additional.font = C:/Windows/Fonts/msgothic.ttc
# Meiryo
kyoto_u.pdf.additional.font = C:/Windows/Fonts/meiryo.ttc
```
この設定により`MS Gothic`、`Meiryo`が使用できるようになります。
※テンプレートHTMLの`font-family`には「ＭＳ　ゴシック」等の日本語名は使用できません。


##PDF生成サービスAPI使用例

pom.xml
```
<dependency>
    <groupId>org.sakaiproject</groupId>
    <artifactId>pdf-generation-service-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

APIをコントローラから呼び出す場合は`WEB-INF/springapp-servlet.xml`のbeanに以下を追記.
```
<property name="pdfGenerationService" ref="org.sakaiproject.pdfgenerationservice.api.PDFGenerationService"/>
```

Controller
```[java]
package org.sakaiproject.tool;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.sakaiproject.pdfgenerationservice.api.PDFGenerationService;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HelloWorldController implements Controller {
    @Setter
    private PDFGenerationService pdfGenerationService;
    
    public ModelAndView handleRequest(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        String action = ServletRequestUtils.getStringParameter(req, "action");

        // 固定用文字列変換用list
        List<String> page = new ArrayList<String>();
        // 可変用文字列変換用list
        List<List<String>> pages = new ArrayList<List<String>>();

        // テンプレートサンプル：修了証.html
	if ("syuryo".equals(action)) {
            // Email Templatesツールで設定したテンプレートのkey
            String templateKey= "certificate_template_single";
            // 固定文字列変換用文字列 {$0},{$1},{$2},{$3},
            page.add("1");
            page.add("111111");
            page.add("工学部");
            page.add("学生1");
            page.add("研修1");
            page.add("2021年6月23日");

            boolean succeed = pdfGenerationService.outputPdf(res,page,pages,templateKey);

            if(succeed) {
            	return null;
            }
	}
        // テンプレートサンプル：トレーニング記録.html
	if ("training".equals(action)) {
            // Email Templatesツールで設定したテンプレートのkey
            String templateKey= "certificate_template_training"; ←Email Templatesツールで設定したテンプレートのkey
            // 固定文字列変換用文字列 {$0},{$1},{$2},{$3},
            page.add("111111");
            page.add("工学部");
            page.add("学生1");
            page.add("2015年1月1日");
            // 可変文字列変換用文字列 {$data-1},{$data-2}
            pages.add(Arrays.asList("研修1","2015年1月1日"));
            pages.add(Arrays.asList("研修2","2015年1月2日"));
            pages.add(Arrays.asList("研修3","2015年1月3日"));
            pages.add(Arrays.asList("研修4","2015年1月4日"));
            pages.add(Arrays.asList("研修5","2015年1月5日"));
            pages.add(Arrays.asList("研修6","2015年1月6日"));
            pages.add(Arrays.asList("研修7","2015年1月7日"));
            pages.add(Arrays.asList("研修8","2015年1月8日"));
            pages.add(Arrays.asList("研修9","2015年1月9日"));
            pages.add(Arrays.asList("研修10","2015年1月10日"));
            pages.add(Arrays.asList("研修11","2015年1月11日"));
            pages.add(Arrays.asList("研修12","2015年1月12日"));
            pages.add(Arrays.asList("研修13","2015年1月13日"));
            pages.add(Arrays.asList("研修14","2015年1月14日"));
            pages.add(Arrays.asList("研修15","2015年1月15日"));

            boolean succeed = pdfGenerationService.outputPdf(res,page,pages, templateKey);

            if(succeed) {
            	return null;
            }
	}
        // テンプレートサンプル：受講研修一覧.html
        if ("juko".equals(action)) {
            // Email Templatesツールで設定したテンプレートのkey
            String templateKey= "certificate_template_juko"; ←Email Templatesツールで設定したテンプレートのkey
            // 固定文字列変換用文字列 {$0},{$1},{$2},{$3},
            page.add("111111");
            page.add("工学部");
            page.add("学生1");
            // 可変文字列変換用文字列 {$data-1},{$data-2}
            pages.add(Arrays.asList("研修1","2015年1月1日"));
            pages.add(Arrays.asList("研修2","2015年1月2日"));
            pages.add(Arrays.asList("研修3","2015年1月3日"));
            pages.add(Arrays.asList("研修4","2015年1月4日"));
            pages.add(Arrays.asList("研修5","2015年1月5日"));
            pages.add(Arrays.asList("研修6","2015年1月6日"));
            pages.add(Arrays.asList("研修7","2015年1月7日"));
            pages.add(Arrays.asList("研修8","2015年1月8日"));
            pages.add(Arrays.asList("研修9","2015年1月9日"));
            pages.add(Arrays.asList("研修10","2015年1月10日"));
            pages.add(Arrays.asList("研修11","2015年1月11日"));
            pages.add(Arrays.asList("研修12","2015年1月12日"));
            pages.add(Arrays.asList("研修13","2015年1月13日"));
            pages.add(Arrays.asList("研修14","2015年1月14日"));
            pages.add(Arrays.asList("研修15","2015年1月15日"));

            boolean succeed = pdfGenerationService.outputPdf(res,page,pages,templateKey);

            if(succeed) {
            	return null;
            }
        }

        return new ModelAndView("index");
    }
}
```
