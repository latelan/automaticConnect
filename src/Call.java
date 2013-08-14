import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.net.URI;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.cookie.Cookie;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.json.JSONException;

public class Call {
	
	private String userName		=""; 
	private String userPwd 		="";
	private String userip 		="";
	private String serialNo 	="";
	private String userDevPort	="";
	private String userStatus	="";
	private CookieStore cookiestore;
	private DefaultHttpClient client;
	//public long hbtime;

	public Call(String userName, String userPwd){
		this.userName = userName;
		this.userPwd = userPwd;
	}
	
	public boolean requestLogin(){

		String root_url = "http://222.24.19.190:8080/portal/index_default.jsp";
		try{
			client = new DefaultHttpClient();

			HttpGet get = new HttpGet(root_url);
			get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
			get.addHeader("UserAgent","Mozilla/5.0 (Windows NT 5.1)");

			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == 200){
				cookiestore = client.getCookieStore();				
				return true;
			}
			else{
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean startLogin(){

		String login_url = "http://222.24.19.190:8080/portal/pws";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("t","li"));
		params.add(new BasicNameValuePair("userName",userName));
		params.add(new BasicNameValuePair("userPwd",userPwd));
		params.add(new BasicNameValuePair("serviceType",""));
		params.add(new BasicNameValuePair("userurl",""));
		params.add(new BasicNameValuePair("userip",""));
		params.add(new BasicNameValuePair("baseip",""));
		params.add(new BasicNameValuePair("language","Chinese"));
		params.add(new BasicNameValuePair("portalProxyIP","222.24.19.190"));
		params.add(new BasicNameValuePair("portalProxyPort","50200"));
		params.add(new BasicNameValuePair("dcPwdNeedEncrypt","1"));
		params.add(new BasicNameValuePair("assignIpType","0"));
		params.add(new BasicNameValuePair("appRootUrl","http://222.24.19.190:8080/portal/"));
		params.add(new BasicNameValuePair("manualUrl",""));
		params.add(new BasicNameValuePair("manualUrlEncryptKey","rTCZGLy2wJkfobFEj0JF8A=="));

		try{
			HttpPost post = new HttpPost(login_url);
			post.addHeader("Accept-Language","zh-CN,zh;q=0.8");
			post.addHeader("UserAgent","Mozilla/5.0");
			post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

			client = new DefaultHttpClient();
			client.setCookieStore(cookiestore);

			HttpResponse response = client.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				String jsonStr = EntityUtils.toString(response.getEntity());
				JSONObject jsonObj = new JSONObject(jsonStr);
				userip 		= jsonObj.getString("clientPrivateIp");
				serialNo 	= jsonObj.getString("serialNo");
				userDevPort = jsonObj.getString("userDevPort");
				userStatus 	= jsonObj.getString("userStatus");
				//hbtime = System.currentTimeMillis();
				return true;
			}
			else{
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean heartBeats(){
		String heartbeat_url = "http://222.24.19.190:8080/portal/pws";

		try{
			HttpPost post = new HttpPost(heartbeat_url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("t","hb"));
			params.add(new BasicNameValuePair("userip",userip));
			params.add(new BasicNameValuePair("baseip",""));
			params.add(new BasicNameValuePair("userDevPort",userDevPort));
			params.add(new BasicNameValuePair("userStatus",userStatus));
			params.add(new BasicNameValuePair("serialNo",serialNo));
			params.add(new BasicNameValuePair("language","Chinese"));
			params.add(new BasicNameValuePair("e_d",""));

			post.addHeader("Accept-Language","zh-CN,zh;q=0.8");
			post.addHeader("UserAgent","Mozilla/5.0");
			post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

			client = new DefaultHttpClient();
			client.setCookieStore(cookiestore);

			HttpResponse response = client.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				//hbtime = System.currentTimeMillis();
				String jsonStr = EntityUtils.toString(response.getEntity());
				JSONObject jsonObj = new JSONObject(jsonStr);
				if(jsonObj.getString("errorNumber").equals("1") == true){
					return true;
				}
				return false;
			}
			return false;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean requestLogout(){

		try{

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http").setHost("222.24.19.190:8080").setPath("/portal/pws")
					.setParameter("t","lo")
					.setParameter("language","Chinese")
					.setParameter("userip",userip)
					.setParameter("baseip","")
					.setParameter("_",Long.toString((new Date()).getTime()));
			URI logout_url = builder.build();
			HttpGet get = new HttpGet(logout_url);

			get.addHeader("Accept-Language","zh-CN,zh;q=0.8");
			get.addHeader("UserAgent","Mozilla/5.0");

			client = new DefaultHttpClient();
			client.setCookieStore(cookiestore);

			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == 200){

				String jsonStr = EntityUtils.toString(response.getEntity());
				JSONObject jsonObj = new JSONObject(jsonStr);
				if(jsonObj.getString("errorNumber").equals("1") == true){
					return true;
				}
			}
			return false;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}