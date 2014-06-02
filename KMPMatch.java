/**
	 * KMP¤pµ{¦¡
	 */
	public class KMPMatch {

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
	