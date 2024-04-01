import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Player 
{
	boolean isNoob = false;
	int rating;
	String username;
	ArrayList<Game> games = new ArrayList<Game>();
	public Player (String username, int year, String month) throws MalformedURLException, IOException
	{
		this.username = username;
		URLConnection c = new URL("https://api.chess.com/pub/player/" + username + "/games/"+year+"/"+month).openConnection();
		JsonNode e = Json.parse(new String(c.getInputStream().readAllBytes()));
		ArrayNode jsongames = (ArrayNode)(e.get("games"));
		for (int x = 0; x < jsongames.size(); x++)
		{
			JsonNode thisG = jsongames.get(x);
			if (thisG.get("rated").asBoolean() && thisG.get("time_class").asText().equals("blitz"))
			{
				String side = "white";
				if (thisG.get("white").get("username").asText().equals(username))
					side = "black";
//				System.out.println(side);
				JsonNode opp = thisG.get(side);
				int elo = opp.get("rating").asInt();
				double score = 1.0;
				String fen = thisG.get("pgn").asText();
				if (side.equals("white")&&fen.contains("1-0") || side.equals("black")&&fen.contains("0-1"))
					score = 0.0;
				else if (fen.contains("0.5-0.5"))
					score = 0.5;
				if (elo < 150)
					Crawler.addUnchecked(opp.get("username").asText());
				games.add(new Game(elo,score));
			}
		}
//		System.out.println(games);
		rating = performance(games);
		if (games.size()>10 && rating < 0)
			isNoob = true;
		else
			isNoob = false;
	}
	public Player (String username, int year, String month, JsonNode e) throws MalformedURLException, IOException
	{
		try
		{
			this.username = username;
			ArrayNode jsongames = (ArrayNode)(e.get("games"));
			for (int x = 0; x < jsongames.size(); x++)
			{
				JsonNode thisG = jsongames.get(x);
				if (thisG.get("rated").asBoolean() && thisG.get("time_class").asText().equals("blitz"))
				{
					String side = "white";
					if (thisG.get("white").get("username").asText().equals(username))
						side = "black";
//					System.out.println(side);
					JsonNode opp = thisG.get(side);
					int elo = opp.get("rating").asInt();
					double score = 1.0;
					String fen = thisG.get("pgn").asText();
					if (side.equals("white")&&fen.contains("1-0") || side.equals("black")&&fen.contains("0-1"))
						score = 0.0;
					else if (fen.contains("0.5-0.5"))
						score = 0.5;
					if (elo < 150)
						Crawler.addUnchecked(opp.get("username").asText());
					games.add(new Game(elo,score));
				}
			}
			rating = performance(games);
			if (games.size()>10 && rating < 0)
				isNoob = true;
			else
				isNoob = false;
		} catch (Exception eeeeee) {
			isNoob = false;
			rating = 151;
			username = "failure001";
		}
	}
	public Player (String info)
	{
		Scanner s = new Scanner(info);
		username = s.next();
		rating = s.nextInt();
		isNoob = s.nextBoolean();
		s.close();
	}
	public String toString()
	{
		return username+" "+rating+" "+isNoob;
	}
	static final double ELO_CONSTANT = 1.005773063;
	private int performance (ArrayList<Game> results)
	{
		if (results.size()==0)
			return 151;
		ArrayList<Integer> outcomes = new ArrayList<Integer>();
		ArrayList<Integer> elos = new ArrayList<Integer>();
		for (Game g:results)
			if (g.getResult() != 0.5)
			{
				outcomes.add((int)(g.getResult()));
				elos.add(g.getElo());
			}
//		System.out.println(outcomes);
//		System.out.println(elos);
		double bestMatchScore = 0.0;
		int bestMatch = 0;
		long cur = System.currentTimeMillis();
		for (int x = Collections.min(elos)-500; x < Collections.max(elos); x++)
		{
			double thisOdds = 1.0;
			for (int q = 0; q < elos.size(); q++)
			{
				int y = elos.get(q);
				double winChc = (1.0/(1.0+Math.pow(ELO_CONSTANT, y-x)));
				if (outcomes.get(q)==1.0)
					thisOdds*=winChc;
				else
					thisOdds*=(1-winChc);
			}
//			System.out.println(x+" "+thisOdds);
			if (thisOdds > bestMatchScore)
			{
				bestMatchScore = thisOdds;
				bestMatch = x;
			}
		}
//		System.out.println("Time: " + (System.currentTimeMillis()-cur));
		double proportion = 1.0/(2+Math.pow(1.2, bestMatch+350));
//		System.out.println(proportion);
		double ret = bestMatch;
		int lCount = 0;
		int lElo = 0;
		for (int x = 0; x < outcomes.size(); x++)
			if (outcomes.get(x)==0)
			{
				lElo+=elos.get(x);
				lCount++;
			}
//		System.out.println(lElo/lCount);
		if (lCount > 0)
			ret+=proportion*((lElo/lCount)-100);
//		System.out.println(bestMatch + " " + (proportion*((lElo/lCount)-100)));
		return (int)ret;
	}
}
class Game
{
	int oppElo;
	double score;
	public Game(int oppElo, double score)
	{
		this.oppElo = oppElo;
		this.score=score;
	}
	public String toString()
	{
		return ""+score+" "+oppElo;
	}
	public double getResult()
	{
		return score;
	}
	public int getElo()
	{
		return oppElo;
	}
}
