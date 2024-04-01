import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
public class Controller
{
	static public void main (String args[]) throws IOException, InterruptedException
	{
//		Crawler c = new Crawler();
//		System.out.println(new Player("ylylgoblinmuncher",2024,"02").rating);
//		c.start(2024, "02");
		sort("noobList.txt");
	}
	static public long sort(String fileName) throws IOException
	{
		Scanner fileIn = new Scanner(new File(fileName));
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<String> nWords = new ArrayList<String>();
		ArrayList<Double> nValues = new ArrayList<Double>();
		while (fileIn.hasNext())
		{
			words.add(fileIn.next());
			if (fileIn.hasNextDouble())
				values.add(fileIn.nextDouble());
			else if (fileIn.hasNextInt())
				values.add(0.0+fileIn.nextInt());
			if (fileIn.hasNextLine())
				fileIn.nextLine();
		}
		long startTime = System.nanoTime();
		PrintWriter fileOut = new PrintWriter(new File(fileName));
		while (words.size()>0)
		{
			double minimum = Collections.min(values);
			for (int x = 0; x < words.size(); x++)
			{
				if (!(values.get(x)==minimum))
				{
					nWords.add(words.get(x));
					nValues.add(values.get(x));
				}
				if (values.get(x)==minimum)
					fileOut.println(words.get(x)+" "+values.get(x));
			}
//			System.out.println(nValues.size());
			words.clear();
			values.clear();
			words = (ArrayList<String>) nWords.clone();
			values = (ArrayList<Double>) nValues.clone();
			nWords.clear();
			nValues.clear();
		}
		fileOut.close();
		return startTime;
	}
}
