/**
 * Author:		王景逸
 * ID:			F74996170
 * Description:	讀入JSON格式檔案，並依照Cmd args 輸出
 * 				
 * */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.json.*;


public class TocHw4 {
	
	
	/**
	 * 取得網頁內容
	 * 
	 * @param httpAddress
	 *            網址
	 * @return 該網頁內容
	 */
	public static String getPageData(String httpAddress) {

		URL u = null;
		InputStream in = null;
		InputStreamReader r = null;
		BufferedReader br = null;
		StringBuffer message = null;

		try {

			u = new URL(httpAddress);
			in = u.openStream();

			r = new InputStreamReader(in, "UTF-8");
			br = new BufferedReader(r);

			String tempstr = null;
			message = new StringBuffer();

			while ((tempstr = br.readLine()) != null) {
				message.append(tempstr);
			}

		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			try {
				u = null;
				in.close();
				r.close();
				br.close();
			} catch (Exception e) {

			}

		}
		return message.toString();
	}

	public static String getFileData(String filename) {
		StringBuffer message = new StringBuffer();
		try {
			// open file (coding UTF-8)
			InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			try {
				do {
					String buffer = reader.readLine();
					if (buffer == null)
						break;
					message.append(buffer);
				} while (true);
			} catch (Exception e) {
			} finally {
				reader.close();
			}
		} catch (Exception e) {
		}
		return message.toString();
	}

	// 擷取 路,街,巷,地號 之前的文字
	public static String getRoadName(String input) {
		char[] strings = { '路', '街' };
		int index = 0;
		for (char s : strings)
			for (index = 0; index < input.length(); index++)
				if (input.charAt(index) == s)
					return input.substring(0, index + 1);
		// 大道的情形
		String pattern = "大道";
		KMPMatch c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		
		strings = new char[]{ '巷' };
		index = 0;
		for (char s : strings)
			for (index = 0; index < input.length(); index++)
				if (input.charAt(index) == s)
					return input.substring(0, index + 1);

		// 地號的情形
		pattern = "地號";
		c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		
		// 坐駕的情形
		pattern = "坐駕";
		c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		// 竹子腳
		pattern = "竹子腳";
		c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		
		

		return null;
	}

	/**
	 * 交易信息
	 * @author Wang
	 *
	 */
	private static class TransactionInformation {
		// 不同的月份數量
		int distinct_month = 0;
		
		/**
		 * 交易的月份清單
		 */
		ArrayList<Integer> TransactionsMonthList = new ArrayList<Integer>();
		/**
		 * 最大交易金額
		 */
		int maxTransactionPrice = 0;
		/**
		 * 最低交易金額
		 */
		int minTransactionPrice = -1;

		/**
		 * 增加譯一筆交易
		 * @param price
		 * 交易金額
		 * @param month
		 * 交易月份
		 * @throws Exception
		 */
		public void addTransaction(int price, int month) throws Exception {
			if (maxTransactionPrice < price)
				maxTransactionPrice = price;
			if (minTransactionPrice > price || minTransactionPrice == -1)
				minTransactionPrice = price;
			if (!TransactionsMonthList.contains(new Integer(month))) {
				TransactionsMonthList.add(new Integer(month));
				distinct_month++;
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws MalformedURLException, JSONException {
		// hashMap <"地址",該地址的交易信息>
		HashMap<String, TransactionInformation> hashMap = new HashMap<String, TransactionInformation>();
		//String[] args = in.split(" ");
		// 格式不對
		if (args.length != 1) {
			System.out.println("格式錯誤");
			return;
		}
		// 來源URL
		String URL = args[0];
		// 網頁內容
		String contents = getPageData(URL);
		// 轉成JSONArray
		JSONArray JSONArray = new JSONArray(contents);
		// 不同的交易月份數
		int max_distinct_month = 0;
		// winners 最多交易月份的地址們 (可能有多筆,就用Array)
		ArrayList<String> winners = new ArrayList<String>();
		Integer arriveTime = 0;
		final HashMap<String , Integer> time = new HashMap<String , Integer>();

		// 對每個JSONObject
		for (int index = 0; index < JSONArray.length(); index++) {
			JSONObject c = JSONArray.getJSONObject(index);
			
			TransactionInformation transactionInfo = null;
			// 道路名稱
			String roadname = getRoadName(c.getString("土地區段位置或建物區門牌"));
			// 價格
			int price = c.getInt("總價元");
			// 交易年月
			int month = c.getInt("交易年月");
			try {
				// 如果hashMap裡面有此地址的交易信息
				if ((transactionInfo = hashMap.get(roadname)) != null) {
					// 該交易信息新增一筆交易
					transactionInfo.addTransaction(price, month);
				}
				// hashMap內沒有該地址的交易信息
				else {
					// 新增一個交易信息
					hashMap.put(roadname, transactionInfo = new TransactionInformation());
					// 新增時間
					arriveTime++;
					time.put(roadname, arriveTime);
					// 該交易信息新增一筆交易
					transactionInfo.addTransaction(price, month);
				}
				// 這個地址的交易信息之不同交易月份比最大不同交易月份更多的話
				if (transactionInfo.distinct_month > max_distinct_month) {
					winners.clear();
					winners.add(roadname);
					max_distinct_month = transactionInfo.distinct_month;
				}
				// 一樣多的話
				else if (transactionInfo.distinct_month == max_distinct_month)
					if (!winners.contains(roadname))
						winners.add(roadname);
				

			} catch (Exception e) {
			}
		}
		// 排序
		Collections.sort(winners, new Comparator() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return time.get(arg0)-time.get(arg1);
			}
		});

		// 貼出結果
		for (String s : winners)
			System.out.println(s + ", 最高成交價:" + hashMap.get(s).maxTransactionPrice + ", 最低成交價:" + hashMap.get(s).minTransactionPrice);

	}
}
/**
 * KMP小程式
 */
class KMPMatch {

	private String string;
	private String pattern;
	private int[] failure;
	private int matchPoint;
	private int ptr = 0;

	public KMPMatch(String string, String pattern) {
		this.string = string;
		this.pattern = pattern;
		failure = new int[pattern.length()];
		computeFailure();
	}

	public int getMatchPoint() {
		return matchPoint;
	}
	
	public int countMatch(){
		int count=0;
		while(continueMatch())count++;
		return count;
	}
	
	public int countMatchIgnoreCase(){
		int count=0;
		while(continueMatchIgnoreCase())count++;
		return count;
	}

	/**
	 * Note:Use getMatchPoint to find match point. Continuously call this
	 * function to find the match point for several match patterns. Once there
	 * is no match pattern , it will return false and reset this function, at
	 * next call, you will get the first match point.
	 * 
	 * @return
	 */
	public boolean continueMatchIgnoreCase() {
		// Tries to find an occurence of the pattern in the string
		if (matchIgnoreCase(ptr)) {
			ptr = matchPoint + pattern.length();
			return true;
		}
		ptr = 0;
		return false;
	}
	
	public boolean continueMatch() {
		// Tries to find an occurence of the pattern in the string
		if (match(ptr)) {
			ptr = matchPoint + pattern.length();
			return true;
		}
		ptr = 0;
		return false;
	}

	/**
	 * Find if string after index match pattern
	 * 
	 * @param index
	 * @return
	 */
	public boolean match(int index) {
		// Tries to find an occurence of the pattern in the string

		int j = 0;
		if (string.length() == 0)
			return false;

		for (int i = index; i < string.length(); i++) {
			while (j > 0 && pattern.charAt(j) != string.charAt(i)) {
				j = failure[j - 1];
			}
			if (pattern.charAt(j) == string.charAt(i)) {
				j++;
			}
			if (j == pattern.length()) {
				matchPoint = i - pattern.length() + 1;
				return true;
			}
		}
		return false;
	}

	public boolean matchIgnoreCase(int index) {
		// Tries to find an occurence of the pattern in the string

		int j = 0;
		if (string.length() == 0)
			return false;
		for (int i = index; i < string.length(); i++) {
			while (j > 0 && !compareIgnoreCase(pattern.charAt(j),string.charAt(i))
					
					//pattern.charAt(j) != string.charAt(i)
					
					) {
				j = failure[j - 1];
			}
			if (compareIgnoreCase(pattern.charAt(j),string.charAt(i))
					//pattern.charAt(j) == string.charAt(i)
					) {
				j++;
			}
			if (j == pattern.length()) {
				matchPoint = i - pattern.length() + 1;
				return true;
			}
		}
		return false;
	}

	private boolean compareIgnoreCase(char a,char b){
		if(a==b)
			return true;
		if(a>b){
			if(a-32==b)
				return true;
			return false;
		}
		else{
			if(b-32==a)
				return true;
			return false;
		}
	}
	
	public boolean match1() {

		int i = 0;
		int j = 0;
		if (string.length() == 0)
			return false;

		while (i + pattern.length() - j <= string.length()) {
			if (j >= pattern.length()) {
				matchPoint = i - pattern.length();
				return true;
			}
			if (string.charAt(i) == pattern.charAt(j)) {
				i++;
				j++;
			} else {
				if (j > 0) {
					j = failure[j - 1];
				} else {
					i++;
				}
			}
		}
		return false;
	}

	/**
	 * Computes the failure function using a boot-strapping process, where the
	 * pattern is matched against itself.
	 */
	private void computeFailure() {

		int j = 0;
		for (int i = 1; i < pattern.length(); i++) {
			while (j > 0 && pattern.charAt(j) != pattern.charAt(i)) {
				j = failure[j - 1];
			}
			if (pattern.charAt(j) == pattern.charAt(i)) {
				j++;
			}
			failure[i] = j;
		}
	}
}
