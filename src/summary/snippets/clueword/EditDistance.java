package summary.snippets.clueword;

public class EditDistance {
	public static double PRECISION = 0.2;
	public static boolean isSimilar(String w1, String w2){
		int dis = minDistance(w1, w2);
		if(dis > 0){
			int len = w1.length() + w2.length();
//			if(w1.length() > 20) w1 = w1.substring(0,20);
//			if(w2.length() > 20) w2 = w2.substring(0,20);
//			if( dis*1.0/len < PRECISION)
//				System.out.println(w1 + "\t"+ w2 + "\t");

			if(dis*1.0/len < PRECISION){
				return true;
			}
			return false;
		}
		return true;
	}
    public static int minDistance(String word1, String word2) {
        if(word1 == null || word2 == null) return 0;
        if(word1.length() == 0 && word2.length() == 0) return 0;
        if(word1.length() == 0) return word2.length();
        if(word2.length() == 0) return word1.length();
        
        if(word1.length() < word2.length()) {
        		String word = word2;
        		word2 = word1;
        		word1 = word;
        }
        	
        // allocate length+1 space to deal with the last-letter distance
        int[][] dis = new int[word1.length()+1][word2.length()+1];
        for(int i = 0; i < dis.length; i++)
            dis[i][0] = i;
        for(int j = 0; j < dis[0].length; j++)
            dis[0][j] = j;
        
        for(int i = 1; i < dis.length; i++){
            for(int j = 1; j < dis[0].length; j++){
                if(word1.charAt(i-1) == word2.charAt(j-1)){
                    dis[i][j] = dis[i-1][j-1];
                }
                else{
                    dis[i][j] = Math.min(dis[i-1][j] + 1,// delete word1[i]
                            Math.min(dis[i][j-1] + 1, // add word2[i]
                                    dis[i-1][j-1] + 1) // substitution
                                    );
                }
            }
        }
        return dis[dis.length-1][dis[0].length-1];
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(isSimilar("abcdefdddddddddddddddddddddddddddddddd","abcdefddddddddddddddddd"));
	}

}
