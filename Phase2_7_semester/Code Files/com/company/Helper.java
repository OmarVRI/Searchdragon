package com.company;

import com.panforge.robotstxt.RobotsTxt;
import crawlercommons.filters.basic.BasicURLNormalizer;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import opennlp.tools.tokenize.TokenizerME;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerModel;


public class Helper {
    public static Integer numLinks = null;
    public static String actual_link = null;

    public static String urlNmormalizer(String url) {
        BasicURLNormalizer normalizer = new BasicURLNormalizer();
        url = normalizer.filter(url);
        System.out.println(url);
        return url;
    }

    public static String htmlToFile(String link, String content, long id) throws SQLException, ClassNotFoundException {


        File f = null;
        String fileName = "pages/" + "page" + new Long(id) + ".html";
        String path = "";
        Helper.writetofile(content, fileName);
        f = new File(fileName);
        path = (f.getAbsoluteFile().getParentFile().getAbsolutePath() + '/' + f.getName());
        return path;


    }

    public static void writetofile(String content, String fileName) {
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(fileName);
            writer = new BufferedWriter(fWriter);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String[] tokenise(String text) {


        //Loading the Tokenizer model
        InputStream inputStream = null;
        //
        String tokens[] = null;
        try {
            inputStream = new FileInputStream("en-token.bin");
            TokenizerModel tokenModel = null;
            try {
                tokenModel = new TokenizerModel(inputStream);
                TokenizerME tokenizer = new TokenizerME(tokenModel);

                //Tokenizing the given raw text
                tokens = tokenizer.tokenize(text);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public static String getBaseUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        String base = url.getProtocol() + "://" + url.getHost();
        System.out.println(base);
        return base;

    }

    public static String normaliser(String url) {
        BasicURLNormalizer normalizer = new BasicURLNormalizer();
        url = normalizer.filter(url);
        System.out.println(url);
        return url;
    }

    public static boolean htmlCheck(String urlString) {
        URL url = null;
        try {
            System.out.println(urlString);
            url = new URL(urlString);
            URLConnection u = url.openConnection();
            String type = u.getHeaderField("Content-Type");
            if (type.contains("text/html")) {
                return true;
            }

        } catch (IOException e) {
            System.out.println("here " + urlString);
            //  e.printStackTrace();
        }

        return false;


    }

    public static boolean checkRobot(String link) {
        URL url = null;
        boolean hasAccess = false;
        try {
            url = new URL(link);
            try (InputStream robotsTxtStream = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt").openStream()) {
                RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtStream);
                hasAccess = robotsTxt.query(null, url.getPath());

            } catch (IOException e) {
                e.getStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return hasAccess;
    }

    public static Vector<String> readFromFile(String fileName) {
        Vector<String> lines = new Vector<>();
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                lines.add(line);


                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
        return lines;
    }

    public String getTitle(String url) {
        String ret = "";
        InputStream response = null;
        try {
            response = new URL(url).openStream();


            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            ret = responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }


}
