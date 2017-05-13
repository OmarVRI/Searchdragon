package com.company;

//import opennlp.tools.stemmer.PorterStemmer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by yomna on 3/23/17.
 */
public class Indexer {

    public void startIndexing() {

        Dbhandler dbhandler = new Dbhandler();
        Vector<pair<Integer, String>> all = dbhandler.getNotIndexed();
        Vector<String> stopWords = Helper.readFromFile("stopwords.txt");
        HashMap<String, String> invertedIndex;
        HashMap<String, Boolean> found = new HashMap<>();
        String htmlpath;
        File f = null;
        String plainText = null;
        Document doc = null;
        String[] tokens;
        String path;
        IndexerDbhandler indexerDbhandler = new IndexerDbhandler();
        indexerDbhandler.createTable();
        int docId;
        int mxlength = -1;
        long count = 0;
        String value;
        String title;
        String metatagcontent;
        for (int i = 0; i < Math.min(5000, all.size()); ++i) {

            docId = all.get(i).first;
            System.out.println(i);
            invertedIndex = new HashMap<>();
            htmlpath = all.get(i).second;
            f = new File(htmlpath);
            try {
                doc = Jsoup.parse(f, null);
                Elements headers = doc.select("h1, h2, h3, h4, h5, h6");
                int elementsNum = 0;
                for (Element header : headers) {
                    plainText = header.text();
                    process(plainText, invertedIndex, elementsNum + "h", stopWords);
                    elementsNum = elementsNum + 1;

                }
                headers.remove();
                title = doc.title();
                if (!title.isEmpty() && title != null) {
                    process(title, invertedIndex, "t", stopWords);
                }
                Elements metalinks = doc.select("meta[name=description]");
                if (!metalinks.isEmpty()) {
                    metatagcontent = metalinks.first().attr("content");
                    process(metatagcontent, invertedIndex, "d", stopWords);
                }
                metalinks = doc.select("meta[name=keywords]");
                if (!metalinks.isEmpty()) {
                    metatagcontent = metalinks.first().attr("content");
                    process(metatagcontent, invertedIndex, "k", stopWords);
                }
                Elements ogTags = doc.select("meta[property^=og:]");
                for (Element tag : ogTags) {
                    String text = tag.attr("property");
                    if ("og:description".equals(text)) {
                        process(tag.attr("content"), invertedIndex, "od", stopWords);
                    } else if ("og:title".equals(text)) {
                        process(tag.attr("content"), invertedIndex, "ot", stopWords);

                    }
                }
                plainText = doc.body().text();
                process(plainText, invertedIndex, "p", stopWords);
                for (String key : invertedIndex.keySet()) {
                    if (key.length() <= 100) {

                        path = indexerDbhandler.isFound(key, docId);
                        value = invertedIndex.get(key);
                        if (path.isEmpty() || path == null) {

                            indexerDbhandler.addContent(key, docId, value);

                        } else {
                            indexerDbhandler.update(key, docId, value);

                        }


                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            dbhandler.setIndexed(docId);
//        break;

        }

        indexerDbhandler.deleteNotUpdated();
        indexerDbhandler.setNotUpdated();
    }

    public void process(String text, HashMap<String, String> invertedIndex, String refPos, Vector<String> stopWords) {

        //lower casing
        text = text.toLowerCase();
        //removing non-alphanumeric
        text = text.replaceAll("[^a-zA-Z0-9\\s]", " ");
        //tokenisation;
        String[] tokens = Helper.tokenise(text);
        // PorterStemmer stemmer = new PorterStemmer();
        String temp;
        for (int j = 0; j < tokens.length; ++j) {
            if (!stopWords.contains(tokens[j])) {
                tokens[j] = Stemmer.stem(tokens[j]);
                temp = invertedIndex.get(tokens[j]);
                if (temp == null || temp.equals("")) {
                    invertedIndex.put(tokens[j], refPos + new Integer(j).toString() + " ");
                } else {


                    invertedIndex.put(tokens[j], invertedIndex.get(tokens[j]) + refPos + new Integer(j).toString() + " ");
                }
            }

        }

    }


}
