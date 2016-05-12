package com.express.core.spider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.express.core.constant.ExpressConstant;
import com.express.core.http.HttpUtils;
import com.express.core.response.ResponseData;
import com.express.core.response.ResponseMsg;
import com.express.core.util.StringUtils;
/**
 * 快递100爬虫
 * @author jaciyu
 *
 */
public class Kuaidi100Spider {
	private static final Logger log = Logger.getLogger(Kuaidi100Spider.class);
	private static String URL_KUAIDI100 = "http://www.kuaidi100.com/query?type={type}&postid={numberId}&id=1&valicode=&temp={tem}";
	/**
	 * 快递100抓取
	 * @param number
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ResponseMsg spider(String number,String type){
		ResponseMsg msg = new ResponseMsg();
		msg.setCode("shentong");
		msg.setNumber(number);
		try {
			String url = URL_KUAIDI100.replace("{type}", type).replace("{numberId}", number).replace("{tem}",Math.random()+"");
			String responseText = HttpUtils.doGet(url);
			msg.setData(Collections.EMPTY_LIST);
			if(StringUtils.isEmpty(responseText)){
				msg.setStatu(-2);
				msg.setStatus("接口异常");
				msg.setMessage("API请求超时");				
			}else {
				JSONObject obj = JSON.parseObject(responseText);
				if(obj.getIntValue("status")!=200){
					msg.setStatu(ExpressConstant.EXPRESS_NOEXIST);
					msg.setStatus("无记录");
					msg.setMessage("抱歉，此单号无记录。<br>如确认运单号无误，可能是快件信息尚未发出，或有延迟，请稍后再试。");
				}else {
					int state = obj.getIntValue("state");
					msg.setStatu(state);
					msg.setStatus(ExpressConstant.getStsteText(state));
					msg.setMessage("查询成功");
					JSONArray jsonArray = obj.getJSONArray("data");
					List<ResponseData> list = new ArrayList<ResponseData>();
					for (Object oj: jsonArray) {
						JSONObject object = (JSONObject) oj;
						ResponseData data = new ResponseData();
						data.setContext(object.getString("context"));
						data.setTime(object.getDate("time"));
						list.add(data);
					}
					msg.setData(list);
				}
			}
		} catch (Exception e) {
			log.error("func[spider]-StoSpider-excetion[{}]",e);
			msg.setNumber(number);
			msg.setStatu(-2);
			msg.setStatus("接口异常");
			msg.setMessage(e.getMessage());
			msg.setData(Collections.EMPTY_LIST);
		}
		return msg;
	}	
}
