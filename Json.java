package Baactiba.ChessNoobs;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
public class Json
{
	static ObjectMapper mapper = getDefaultObjectMapper();
	
	private static ObjectMapper getDefaultObjectMapper()
	{
		ObjectMapper d = new ObjectMapper();
		
		return d;
	}
	
	static public JsonNode parse(String s) throws IOException
	{
		return mapper.readTree(s);
	}
}