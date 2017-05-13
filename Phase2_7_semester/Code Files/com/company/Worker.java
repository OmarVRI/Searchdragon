package com.company;

import java.io.IOException;
import java.sql.SQLException;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Worker extends Thread {
    private static final String user_agent =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    String url = "";
    boolean getLinks;

    public Worker(String website, boolean get_links) {
        getLinks = get_links;
        url = website;

    }

    public void run() {
        Connection connection = Jsoup.connect(url).userAgent(user_agent);
        Document htmlDocument = null;
        Dbhandler db = new Dbhandler();
        try {
            htmlDocument = connection.get();
            System.out.println("\n**Visiting1** Received web page at " + url);
            processDoc(htmlDocument);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error in out HTTP request " + e);
            try {
                this.sleep(3000);
                try {
                    htmlDocument = connection.get();

                    System.out.println("\n**Visiting2** Received web page at " + url);
                    processDoc(htmlDocument);

                } catch (IOException e1) {
                    db.deleteLink(url);
                    System.out.println("Error in out HTTP request " + e);

                }
            } catch (InterruptedException e1) {
                System.out.println("failed to sleep");
            }

        }


    }

    private void processDoc(Document htmlDocument) {
        Dbhandler db = new Dbhandler();
        System.out.println("\n**Visiting** Received web page at " + url);
        String fileName;
        if (getLinks) {
            Elements linksOnPage = htmlDocument.select("a");


            for (Element link : linksOnPage) {


                if (Helper.checkRobot(url))

                {
                    if ((Helper.htmlCheck(link.absUrl("href")))) {

                        System.out.println(link.absUrl("href"));
                        try {

                            db.addSite(Helper.urlNmormalizer(link.absUrl("href")), 1);
                        } catch (ClassNotFoundException e) {

                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        try {
            fileName = Helper.htmlToFile(url, htmlDocument.html().toString(), db.getID(url));
            System.out.println(fileName);
            db.addContent(url, fileName);
            System.out.println("successfully added");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


}
