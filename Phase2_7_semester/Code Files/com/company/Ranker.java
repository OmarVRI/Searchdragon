package com.company;


import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Created by maverick on 06/05/17.
 */
public class Ranker {
    private String[] words;
    private boolean phrase;
    private RankerDbhandler rdb;
    private Stemmer stemmer;
    private int N = 5010;
    private SortedMap<Double, Vector<Integer>> mp;
    private Double[] keys;
    private double[] result;
    private Vector<Integer> out;
    private int MAXW = 2500;
    private boolean[] vis;
    private String[][] posArr;
    private int[] frq;

    public Ranker(String in) {
        rdb = new RankerDbhandler();
        stemmer = new Stemmer();
        mp = new TreeMap<>();
        frq = new int[N];

        rdb.createTableTF();
        rdb.createTableIDF();
        phrase = in.charAt(0) == '\"' && in.charAt(in.length() - 1) == '\"';
        in = in.replace("\"", "");
        words = in.split(" ");
        work();
    }

    public void work() {
        if (phrase)
            phraseProcess();
        else
            queryProcess();
        finalizeResult();
    }

    public void phraseProcess() {
        if (words.length == 0) return;
        String stemmedSeed = stemmer.stem(words[0]);
        vis = new boolean[words.length];
        posArr = new String[words.length][2];
        vis[0] = true;
        int docs[] = rdb.getDocs(stemmedSeed);
        for (int i = 0; i < docs.length; ++i) {
            String seedPos = rdb.getPositionsString(stemmedSeed, docs[i]);
            String[] seedPosArr = seedPos.split(" ");
            for (String curPosWord : seedPosArr) {
                int pos = getPos(curPosWord);
                int j;
                for (j = 1; j < words.length; ++j) {
                    String stemmedStr = stemmer.stem(words[j]);
                    if (!vis[j]) {
                        posArr[j] = rdb.getPositionsString(stemmedStr, docs[i]).split(" ");
                        Arrays.sort(posArr[j]);
                    }
                    vis[j] = true;
                    String reqStr = prepare(curPosWord, pos + j);
                    int idx = Arrays.binarySearch(posArr[j], reqStr);
                    if (idx < 0 || idx == posArr[j].length || !posArr[j][idx].equals(reqStr)) break;
                }
                if (j == words.length)
                    rdb.updateResult(docs[i]);
            }
        }
        for (int i = 1; i < N; ++i) rdb.divideResult(i);
        finalizeResult();
    }

    public int getPos(String str) {
        int posH = str.indexOf("h");
        int ret;
        if (posH != -1)
            ret = Integer.parseInt(str.substring(0, posH));
        else ret = Integer.parseInt(str.substring(1));
        return ret;
    }

    private String prepare(String str, int pos) {
        String ret;
        int posH = str.indexOf("h");
        if (posH != -1)
            ret = Integer.toString(pos) + str.substring(posH);
        else {
            int i;
            for (i = 0; ; i++) if (str.charAt(i) >= '0' && str.charAt(i) <= '9') break;
            ret = str.substring(0, i) + Integer.toString(pos);
        }
        return ret;
    }

    private void finalizeResult() {
        rdb.addPopularity();
        result = rdb.getResult();
        N = result.length;
        for (int i = 1; i < N; ++i)
            if (mp.containsKey(result[i])) {
                Vector<Integer> V = mp.get(result[i]);
                V.add(i);
                mp.put(result[i], V);
            } else {
                Vector<Integer> V = new Vector<>();
                V.add(i);
                mp.put(result[i], V);
            }
        keys = new Double[mp.size()];
        mp.keySet().toArray(keys);
        out = new Vector<>();
        for (int i = keys.length - 1; i > -1; --i) {
            double key = keys[i];
            Vector<Integer> V = mp.get(key);
            for (int j : V)
                out.add(j);
        }
    }

    public void queryProcess() {
        for (String currentWord : words) {
            String stemmedWord = stemmer.stem(currentWord);
            rdb.calcIDF(stemmedWord);
            rdb.calcTF(stemmedWord);
        }
    }

    public Vector<Integer> getResults() {
        return out;
    }
}
