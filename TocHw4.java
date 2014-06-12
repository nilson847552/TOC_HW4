/**
 * Author:		�����h
 * ID:			F74996170
 * Description:	Ū�JJSON�榡�ɮסA�è̷�Cmd args ��X
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
	 * ���o�������e
	 * 
	 * @param httpAddress
	 *            ���}
	 * @return �Ӻ������e
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

	// �^�� ��,��,��,�a�� ���e����r
	public static String getRoadName(String input) {
		char[] strings = { '��', '��', '��' };
		int index = 0;
		for (char s : strings)
			for (index = 0; index < input.length(); index++)
				if (input.charAt(index) == s)
					return input.substring(0, index + 1);
		// �a��������
		String pattern = "�a��";
		KMPMatch c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		// �j�D������
		pattern = "�j�D";
		c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		// ���r������
		pattern = "���r";
		c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());
		// �ˤl�}
		pattern = "�ˤl�}";
		c = new KMPMatch(input, pattern);
		if (c.match(0))
			return input.substring(0, c.getMatchPoint() + pattern.length());

		return null;
	}

	/**
	 * ����H��
	 * @author Wang
	 *
	 */
	private static class TransactionInformation {
		// ���P������ƶq
		int distinct_month = 0;
		
		/**
		 * ���������M��
		 */
		ArrayList<Integer> TransactionsMonthList = new ArrayList<Integer>();
		/**
		 * �̤j������B
		 */
		int maxTransactionPrice = 0;
		/**
		 * �̧C������B
		 */
		int minTransactionPrice = -1;

		/**
		 * �W�[Ķ�@�����
		 * @param price
		 * ������B
		 * @param month
		 * ������
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
		// hashMap <"�a�}",�Ӧa�}������H��>
		HashMap<String, TransactionInformation> hashMap = new HashMap<String, TransactionInformation>();
		//String[] args = in.split(" ");
		// �榡����
		if (args.length != 1) {
			System.out.println("�榡���~");
			return;
		}
		// �ӷ�URL
		String URL = args[0];
		// �������e
		String contents = getPageData(URL);
		// �নJSONArray
		JSONArray JSONArray = new JSONArray(contents);
		// ���P����������
		int max_distinct_month = 0;
		// winners �̦h���������a�}�� (�i�঳�h��,�N��Array)
		ArrayList<String> winners = new ArrayList<String>();
		Integer arriveTime = 0;
		final HashMap<String , Integer> time = new HashMap<String , Integer>();

		// ��C��JSONObject
		for (int index = 0; index < JSONArray.length(); index++) {
			JSONObject c = JSONArray.getJSONObject(index);
			
			TransactionInformation transactionInfo = null;
			// �D���W��
			String roadname = getRoadName(c.getString("�g�a�Ϭq��m�Ϋت��Ϫ��P"));
			// ����
			int price = c.getInt("�`����");
			// ����~��
			int month = c.getInt("����~��");
			try {
				// �p�GhashMap�̭������a�}������H��
				if ((transactionInfo = hashMap.get(roadname)) != null) {
					// �ӥ���H���s�W�@�����
					transactionInfo.addTransaction(price, month);
				}
				// hashMap���S���Ӧa�}������H��
				else {
					// �s�W�@�ӥ���H��
					hashMap.put(roadname, transactionInfo = new TransactionInformation());
					// �s�W�ɶ�
					arriveTime++;
					time.put(roadname, arriveTime);
					// �ӥ���H���s�W�@�����
					transactionInfo.addTransaction(price, month);
				}
				// �o�Ӧa�}������H�������P��������̤j���P��������h����
				if (transactionInfo.distinct_month > max_distinct_month) {
					winners.clear();
					winners.add(roadname);
					max_distinct_month = transactionInfo.distinct_month;
				}
				// �@�˦h����
				else if (transactionInfo.distinct_month == max_distinct_month)
					if (!winners.contains(roadname))
						winners.add(roadname);
				

			} catch (Exception e) {
			}
		}
		// �Ƨ�
		Collections.sort(winners, new Comparator() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return time.get(arg0)-time.get(arg1);
			}
		});

		// �K�X���G
		for (String s : winners)
			System.out.println(s + ", �̰������:" + hashMap.get(s).maxTransactionPrice + ", �̧C�����:" + hashMap.get(s).minTransactionPrice);

	}
}
