//Evan Amster
//Edmond Wu

package graph;

import java.io.*;

public class Friends
{
	public static void main(String[] args) throws IOException 
	{
		BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter document name:");
		String file = scan.readLine();
		FriendsGraph friendships = new FriendsGraph(file);
		friendships.initialize();
		
		System.out.print("The schools listed are: ");
		String listOfSchools = "";
		for (String school : friendships.schools.keySet())
		{
			listOfSchools += school + ", ";
		}
		
		listOfSchools = listOfSchools.substring(0, listOfSchools.length() - 2);
		System.out.println(listOfSchools);
		System.out.println();
		printOptions();
		
		int x = Integer.parseInt(scan.readLine());
		
		while (x < 1 || x > 5)
		{
			System.out.println("Invalid input. Try again:");
			x = Integer.parseInt(scan.readLine());
		}
		
		while (x != 5)
		{
			friendships = new FriendsGraph(file);
			friendships.initialize();
			
			if (x == 1)
			{
				System.out.println("Enter a school:");
				String school = scan.readLine();
				if (friendships.schools.containsKey(school))
				{
					friendships.printSchoolGraph(school);
				}
				else
				{
					System.out.println("Invalid school.");
				}
			}
		
			else if (x == 2)
			{
				System.out.println("Enter first student:");
				String student1 = scan.readLine();
				System.out.println("Enter second student:");
				String student2 = scan.readLine();
				friendships.printShortestPath(student1, student2);
			}
			
			else if (x == 3)
			{
				System.out.println("Enter a school:");
				String school = scan.readLine();
				friendships.printCliques(school);
			}
			
			else if (x == 4)
			{
				System.out.println("The connectors are:"); //michele, nick, aparna, jane, tom
				friendships.printConnectors(friendships.dfs());
			}
			else
			{
				System.out.println("Invalid input. Try again:");
			}
			System.out.println();
			System.out.println();
			printOptions();
			x = Integer.parseInt(scan.readLine());
		}
	}
	
	public static void printOptions()
	{
		System.out.println("1. Find students and their friends at a particular school");
		System.out.println("2. Find the shortest chain between two friends");
		System.out.println("3. Find the cliques at particular school");
		System.out.println("4. Find connectors");
		System.out.println("5. Quit");
		System.out.println();
	}
}
