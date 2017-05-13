package com.company;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Crawler {
    int threadscount;
    Dbhandler dbhand = new Dbhandler();
   
    Crawler(int tc)
    {
        threadscount = tc;
        Vector<String> seeds=Helper.readFromFile("websites.txt");
        Dbhandler dbhandler=new Dbhandler();
       
        for(int i=0;i<seeds.size();++i)
        {
            try {
                dbhandler.addSite(seeds.get(i), 0);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }
    public void crawl() throws ClassNotFoundException, SQLException{
        ArrayList<String> tobehandled = new ArrayList<String>();
        ExecutorService pool = Executors.newFixedThreadPool(threadscount);
        try {
            tobehandled = dbhand.getNotDownloaded();

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean getLinks;
        if (tobehandled.size() > 0)
        {
            getLinks=true;
            long  count=dbhand.getCount();
            System.out.println(count);
            if(count>5000) {
                getLinks = false;

            }
            for (String name : tobehandled) {
                pool.submit(new Thread(new Worker(name,getLinks)));
            }
            pool.shutdown();
            try {
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (dbhand.getDownloadedCount() < 5)
        {
            crawl();
        }
    }
}
