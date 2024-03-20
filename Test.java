package Baactiba.ChessNoobs;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.JsonNode;

public class Test
{
	static public void main (String args[]) throws IOException
	{
		System.out.println(getBlitzRating("VasM1"));
	}
	protected static int getBlitzRating(String name) throws MalformedURLException, IOException
	{
		URLConnection c = new URL("https://api.chess.com/pub/player/" + name + "/stats").openConnection();
		JsonNode e = Json.parse(new String(c.getInputStream().readAllBytes()));
		int ret = (e.get("chess_blitz").get("last").get("rating")).asInt();
		return ret;
	}
	static private ArrayList<String> playersPlayed (String name) throws MalformedURLException, IOException
	{
		URLConnection c = new URL("https://api.chess.com/pub/player/" + name + "/games/2024/03").openConnection();
		
	}
}