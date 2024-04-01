import java.io.IOException;
import java.net.*;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Test
{
	static public void main (String args[]) throws IOException
	{
		Player baac = new Player("baactiba",2024,"02");
		System.out.println(baac);
	}
	protected static int getBlitzRating(String name) throws MalformedURLException, IOException
	{
		URLConnection c = new URL("https://api.chess.com/pub/player/" + name + "/stats").openConnection();
		JsonNode e = Json.parse(new String(c.getInputStream().readAllBytes()));
		int ret = (e.get("chess_blitz").get("last").get("rating")).asInt();
		return ret;
	}
	static protected HashSet<String> playersPlayed (String name, int year, String month) throws MalformedURLException, IOException // String month - "01" - "12"
	{
		URLConnection c = new URL("https://api.chess.com/pub/player/" + name + "/games/"+year+"/"+month).openConnection();
		JsonNode e = Json.parse(new String(c.getInputStream().readAllBytes()));
		ArrayNode games = (ArrayNode)(e.get("games"));
		HashSet<String> opponents = new HashSet<String>();
		for (int x = 0; x < games.size(); x++)
		{
			JsonNode game = games.get(x);
			if (game.get("rated").asBoolean() && game.get("time_class").asText().equals("blitz"))
			{
				String w = game.get("white").get("username").asText();
				if (w.equals(name))
					opponents.add(game.get("black").get("username").asText());
				else
					opponents.add(name);
			}
		}
		return opponents;
	}
	static protected HashSet<String> playersPlayed (String name, JsonNode e) throws MalformedURLException, IOException // String month - "01" - "12"
	{
		ArrayNode games = (ArrayNode)(e.get("games"));
		HashSet<String> opponents = new HashSet<String>();
		for (int x = 0; x < games.size(); x++)
		{
			JsonNode game = games.get(x);
			if (game.get("rated").asBoolean() && game.get("time_class").asText().equals("blitz"))
			{
				String w = game.get("white").get("username").asText();
				if (w.equals(name))
					opponents.add(game.get("black").get("username").asText());
				else
					opponents.add(name);
			}
		}
		return opponents;
	}
}
