package com.express.core.spider;

import java.util.Collections;

import org.apache.http.Header;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.express.core.constant.ExpressConstant;
import com.express.core.http.HttpUtils;
import com.express.core.response.ResponseMsg;
import com.express.core.util.StringUtils;
import com.tgb.ccl.http.common.HttpHeader;

/**
 * 中通爬虫
 * @author yuwenjin
 * @date 2016年5月10日
 */
public class ZtoSpider {
	private static final Logger log = Logger.getLogger(ZtoSpider.class);
	private static final String URL = "http://www.zto.com/GuestService/Bill";
	//http://www.kuaidi100.com/query?type=zhongtong&postid=768404530475&id=1&valicode=&temp=0.25235222186893225
	@SuppressWarnings("unchecked")
	public ResponseMsg spider_officalwebsite(String number){
		ResponseMsg msg = new ResponseMsg();
		msg.setCode("zhongtong");
		msg.setNumber(number);
		try {
			String body = new StringBuffer("txtBill=").append(number).toString();
			Header[] head =HttpHeader.custom().contentType("application/x-www-form-urlencoded").referer("http://www.zto.com/GuestService/").build();
			String responseText = HttpUtils.doPost(URL,body,head);
			if(StringUtils.isEmpty(responseText)){
				msg.setStatu(-2);
				msg.setStatus("接口异常");
				msg.setMessage("API请求超时");
			}else {
				Element status = Jsoup.parse(responseText).getElementById("Status");
				if(status==null){
					msg.setStatu(ExpressConstant.EXPRESS_NOEXIST);
					msg.setMessage("抱歉，此单号无记录。<br>如确认运单号无误，可能是快件信息尚未发出，或有延迟，请稍后再试。");
				}else {
					String statustr = status.val();
					if("无运单信息".equals(statustr)){
						msg.setStatu(ExpressConstant.EXPRESS_NOEXIST);
						msg.setMessage("抱歉，此单号无记录。<br>如确认运单号无误，可能是快件信息尚未发出，或有延迟，请稍后再试。");						
					}
					if("".equals(statustr)){
						
					}
					System.out.println(statustr);
				}
			}	
		} catch (Exception e) {
			log.error("func[spider]-ZtoSpider-excetion[{}]",e);
			msg.setNumber(number);
			msg.setStatu(-2);
			msg.setStatus("接口异常");
			msg.setMessage(e.getMessage());
			msg.setData(Collections.EMPTY_LIST);
		}
		return msg;
	}
	
	public static void main(String[] args){
		ZtoSpider spider = new ZtoSpider();
		spider.spider_officalwebsite("768404530475");//531307476851
	}
	
}
