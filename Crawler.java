import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
public class Crawler extends Thread
{
	static HashSet<String> worstPlayers = new HashSet<String>();
	static HashSet<String> playersToCheck = new HashSet<String>();
	static HashSet<String> playersChecked = new HashSet<String>();
	static PrintWriter wpOut;
	static PrintWriter pttOut;
	static PrintWriter cOut;
	static int year;
	static String month;
	public Crawler(int y, String m)
	{
		year = y;
		month = m;
	}
	public void run()
	{
		try {
			start (year,month);
		} catch (IOException e) {} catch (InterruptedException e) {}
	}
	public void start(int year, String month) throws IOException, InterruptedException
	{
		BufferedReader fileIn = new BufferedReader(new FileReader("noobList.txt"));
		String line = "";
		while ((line = fileIn.readLine()) != null)
			worstPlayers.add(line);
		wpOut = new PrintWriter(new File("noobList.txt"));
		for (String p:worstPlayers)
			wpOut.println(p);
		wpOut.flush();
		fileIn = new BufferedReader(new FileReader("ptt.txt"));
		while ((line = fileIn.readLine()) != null)
			playersToCheck.add(line);
		fileIn = new BufferedReader(new FileReader("checked.txt"));
		while ((line = fileIn.readLine()) != null)
			playersChecked.add(line);
		cOut = new PrintWriter(new File("checked.txt"));
		for (String p:playersChecked)
			cOut.println(p);
		cOut.flush();
		fileIn.close();
		pttOut = new PrintWriter(new File("ptt.txt"));
		while (true)
		{
			Object[] ptt = playersToCheck.toArray();
			System.out.println(ptt.length);
			playersToCheck.clear();
			for (Object o:ptt)
			{
				if (!(playersChecked.contains((String)o)))
				{
					int rating = 151;
					try 
					{
						rating = Test.getBlitzRating((String)o);
					}
					catch (Exception e) {}
					if (rating < 150)
					{
						boolean failed = false;
						JsonNode e = null;
						try 
						{
							URLConnection c = new URL("https://api.chess.com/pub/player/" + (String)o + "/games/"+year+"/"+month).openConnection();
							e = Json.parse(new String(c.getInputStream().readAllBytes()));
						}
						catch (Exception ex)
						{
							System.out.println("Fail: " + (String)o);
							failed = true;
							Thread.sleep(5000);
						}
						if (!failed)
						{	
							Player p = new Player ((String)o,year,month,e);
							playersChecked.add(p.username);
							cOut.println(p.username);
							cOut.flush();
							if (p.isNoob)
							{
								wpOut.println((String)o+" "+p.rating);
								wpOut.flush();
								System.out.println(p.username+" "+p.rating);
							}
							for (String s:Test.playersPlayed((String)o,e))
							{
								playersToCheck.add(s);
								pttOut.println(s);
								pttOut.flush();
							}
						}
					}
				}
			}
		}
	}
	static void addUnchecked(String name)
	{
		playersToCheck.add(name);
	}
}
