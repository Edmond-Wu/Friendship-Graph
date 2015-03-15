package graph;

import java.util.*;
import java.io.*;

class FriendNode
{
	Person person;
	FriendNode friend;
	
	public FriendNode(Person p)
	{
		person = p;
		friend = null;
	}
}

class Person
{
	private String name;
	private String school;
	private int dfsNum;
	private int backNum;
	FriendNode friendsList;
	
	public Person(String n, int v)
	{
		name = n;
		backNum = 0;
		dfsNum = 0;
	}
	
	public Person(String n, String s, int v)
	{
		name = n;
		school = s;
		backNum = 0;
		dfsNum = 0;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getSchool()
	{
		return school;
	}
	
	public int getBackNum()
	{
		return backNum;
	}
	
	public int getDfsNum()
	{
		return dfsNum;
	}
	
	public void setDfsNum(int x)
	{
		dfsNum = x;
	}
	
	public void setBackNum(int x)
	{
		backNum = x;
	}
}

public class FriendsGraph
{
	HashMap<String, Person> graph;
	HashMap<String, ArrayList<Person>> schools;
	HashMap<Integer, String> vertexGraph;
	HashMap<String, Integer> reverseVertexGraph;
	private int numPeople;
	boolean visited[];
	int prev[];
	boolean visitedCliques[];
	boolean visitedDFS[];
	
	public int getNumPeople()
	{
		return numPeople;
	}
	
	//builds new FriendsGraph out of a file
	public FriendsGraph(String file) throws FileNotFoundException 
	{		
		if (file == null)
		{
			throw new FileNotFoundException();
		}
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(new File(file));
		numPeople = Integer.parseInt(scan.nextLine());
		
		graph = new HashMap<String, Person>(1000, 5.0f);
		schools = new HashMap<String, ArrayList<Person>>(1000, 5.0f);
		vertexGraph = new HashMap<Integer, String>(1000, 5.0f); //vertex numbers go from 0 to (numPeople - 1)
		reverseVertexGraph = new HashMap<String, Integer>(1000, 5.0f);
		
		int count = 0;
		
		//adds people to the graphs
		while (scan.hasNextLine() && count < numPeople)
		{
			String word = scan.nextLine();
			
			//if person is a student
			if (word.length() - word.replace("|", "").length() == 2)
			{
				String name = word.substring(0, word.indexOf('|'));
				word = word.substring(word.indexOf('|') + 1);
				String school = word.substring(word.indexOf('|') + 1);
				Person student = new Person(name, school, count);
				graph.put(name, student);
				vertexGraph.put(count, student.getName());
				reverseVertexGraph.put(student.getName(), count);
				if (!schools.containsKey(school))
				{
					ArrayList<Person> studentsAtSchool = new ArrayList<Person>();
					studentsAtSchool.add(student);
					schools.put(school, studentsAtSchool);
				}
				else
				{
					ArrayList<Person> studentsAtSchool = schools.get(school);
					studentsAtSchool.add(student);
					schools.put(school, studentsAtSchool);
				}
			}
			
			//not a student
			else
			{
				String name = word.substring(0, word.indexOf('|'));
				Person nonStudent = new Person(name, count);
				graph.put(name, nonStudent);
				vertexGraph.put(count, nonStudent.getName());
				reverseVertexGraph.put(nonStudent.getName(), count);
			}
			count++;
		}
		
		//creates friendships from the listed friendships in the doc
		while (scan.hasNextLine())
		{
			String word = scan.nextLine();
			String friend1 = word.substring(0, word.indexOf('|'));
			String friend2 = word.substring(word.indexOf('|') + 1);
			
			//if there are no friends, create a new FriendNode
			if (graph.get(friend1).friendsList == null)
			{
				graph.get(friend1).friendsList = new FriendNode(graph.get(friend2));
			}
			
			//otherwise add on another FriendNode
			else
			{
				FriendNode temp = graph.get(friend1).friendsList;
				FriendNode prev = null;
				while (temp != null)
				{
					prev = temp;
					temp = temp.friend;
				}
				prev.friend = new FriendNode(graph.get(friend2));
			}
			
			//same thing for the second friend
			if (graph.get(friend2).friendsList == null)
			{
				graph.get(friend2).friendsList = new FriendNode(graph.get(friend1));
			}
			else
			{
				FriendNode temp = graph.get(friend2).friendsList;
				FriendNode prev = null;
				while (temp != null)
				{
					prev = temp;
					temp = temp.friend;
				}
				prev.friend = new FriendNode(graph.get(friend1));
			}
		}
	}
	
	//returns hash table of students at school; to be used in the print
	public HashMap<String, Person> studentsAtSchool(String school) throws IOException 
	{	
		HashMap<String,Person> result = new HashMap<String,Person>();
		ArrayList<Person> students = schools.get(school);
		
		for(int i = 0; i < students.size(); i++)
		{
			result.put(students.get(i).getName(), students.get(i));
			FriendNode ptr = result.get(students.get(i).getName()).friendsList;
			FriendNode prev = null;
			
			while(ptr != null)
			{
				if(ptr.person.getSchool() == null || !(ptr.person.getSchool().equals(school)))
				{
					if(prev == null)
					{
						students.get(i).friendsList = ptr.friend;		
						ptr = ptr.friend;
					}
					else
					{
						prev.friend = ptr.friend;
						ptr = ptr.friend;
					}
				}
				else
				{
					prev = ptr;
					ptr = ptr.friend;
				}
			}
		}
		return result;
	}
	
	//prints subgraph of students at school
	public void printSchoolGraph(String school) throws IOException 
	{
		HashMap<String, Person> result = studentsAtSchool(school);
		System.out.println(result.size());
		for(Map.Entry<String, Person> entry : result.entrySet())
		{
			//prints out in the same format as the doc for students and non-students
			if(entry.getValue().getSchool() != null)
			{
				System.out.println(entry.getValue().getName() + "|" + "y" + "|" + entry.getValue().getSchool());
			}
			else
			{
				System.out.println(entry.getValue().getName() + "|" + "n");
			}
		}
		
		for(Map.Entry<String, Person> entry : result.entrySet())
		{
			FriendNode ptr = entry.getValue().friendsList;
			
			while(ptr != null)
			{
				if(result.containsKey(ptr.person.getName()))
				{
					System.out.println(entry.getValue().getName() + "|" + ptr.person.getName());
					FriendNode ptr2 = result.get(ptr.person.getName()).friendsList;
					FriendNode prev2 = null;
					while(ptr2 != null)
					{
						if(ptr2.person.getName().equals(entry.getValue().getName()))
						{
							if(prev2 == null)
							{
								result.get(ptr.person.getName()).friendsList = ptr2.friend;
							}
							else
							{
								prev2.friend = ptr2.friend;
							}
							break;
						}
						prev2 = ptr2;
						ptr2 = ptr2.friend;
					}
				}
				ptr = ptr.friend;		
			}
		}
	}
	
	public HashMap<Integer, String> numSubGraph (String school)
	{
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		ArrayList<Person> students = schools.get(school);
		int count = 0;
		for(Person student : students)
		{
			result.put(count, student.getName());
			count++;
		}
		return result;
	}
	
	public HashMap<String, Integer> reverseNumSubGraph (String school)
	{
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		ArrayList<Person> students = schools.get(school);
		int count = 0;
		for(Person student : students)
		{
			result.put(student.getName(), count);
			count++;
		}
		return result;
	}
	
	//prints out the cliques at school
	public void printCliques(String school) throws IOException 
	{
		HashMap<String, Person> result = studentsAtSchool(school);
		HashMap<Integer, String> result2 = numSubGraph(school);
		HashMap<String, Integer> result3 = reverseNumSubGraph(school);
		
		Queue<Person> queue = new LinkedList<Person>();
		for(int i = 0; i < numPeople; i++)
		{
			if(result.containsKey(result2.get(i)))
			{
				visitedCliques[i] = false;
			}
			else
			{
				visitedCliques[i] = true;
			}	
		}
		
		int count = 0;
		int count2 = 0;
		while(count != numPeople)
		{	
			queue.add(result.get(result2.get(count)));
			if(!visitedCliques[count])
			{
				HashMap<String,Person> temp = new HashMap<String,Person>();
				visitedCliques[count] = true;
				while(queue.peek() != null)
				{
					Person t = queue.remove();
					temp.put(t.getName(),t);
					FriendNode ptr = t.friendsList;
					
					while(ptr != null)
					{
						if(!visitedCliques[result3.get(ptr.person.getName())])
						{
							visitedCliques[result3.get(ptr.person.getName())] = true;
							queue.add(ptr.person);
						}	
						ptr = ptr.friend;
					}
				}
				count2++;
				System.out.println("");
				System.out.println("Clique " + count2 + ":");
				System.out.println();
				printGraph(temp);
				
				count++;
			}
			else
			{
				queue.remove();
				count++;
			}
		}
	}
	
	//prints out subgraph
	public void printGraph(HashMap<String, Person>subgraph) throws IOException 
	{
		System.out.println(subgraph.size());
		for(Map.Entry<String,Person> entry:subgraph.entrySet())
		{
			if(entry.getValue().getSchool() != null)
			{
				System.out.println(entry.getValue().getName() + "|" + "y" + "|" + entry.getValue().getSchool());
			}
			else
			{
				System.out.println(entry.getValue().getName() + "|" + "n");
			}
		}
		
		for(Map.Entry<String, Person> entry : subgraph.entrySet())
		{
			FriendNode ptr = entry.getValue().friendsList;
			while(ptr != null)
			{
				System.out.println(entry.getValue().getName() + "|" + ptr.person.getName());
				if (subgraph.get(ptr.person.getName()) != null)
				{
					FriendNode ptr2=subgraph.get(ptr.person.getName()).friendsList;
					FriendNode prev2 = null;
					while(ptr2 != null)
					{
						if(ptr2.person.getName().equals(entry.getValue().getName()))
						{
							if(prev2 == null)
							{
								subgraph.get(ptr.person.getName()).friendsList = ptr2.friend;
							}
							else
							{
								prev2.friend = ptr2.friend;				
							}
							break;
						}
					
						prev2 = ptr2;
						ptr2 = ptr2.friend;
					}
				}
				
				ptr = ptr.friend;	
			}
		}
	}
	
	//initializes instance array variables
	public void initialize() 
	{
		visited = new boolean[numPeople];
		prev = new int[numPeople];
		visitedCliques = new boolean[numPeople];
		visitedDFS = new boolean[numPeople];
	}
	
	//shortest path between two people
	public void printShortestPath(String p1, String p2) 
	{
		//sets every visited value to false
		for(int i = 0; i < numPeople; i++)
		{
			visited[i] = false;
			prev[i] = -1;
		}
		
		//stores people in path in a queue
		//adds p1 as the first person, and sets his/her visited value to true
		Queue<Person> queue = new LinkedList<Person>();
		visited[reverseVertexGraph.get(p1)] = true;
		queue.add(graph.get(p1));
		
		while(queue.peek() != null)
		{
			Person t = queue.remove();
			FriendNode ptr = t.friendsList;
			while(ptr != null)
			{
				if(!visited[reverseVertexGraph.get(ptr.person.getName())]) //if person is not visited
				{
					visited[reverseVertexGraph.get(ptr.person.getName())] = true;
					prev[reverseVertexGraph.get(ptr.person.getName())] = reverseVertexGraph.get(t.getName());
					queue.add(ptr.person);
				}
				ptr = ptr.friend;
			}
		}
		
		//stores people in a stack for printing purposes
		Stack<Person> stack = new Stack<Person>();
		int i = reverseVertexGraph.get(p2);
		stack.add(graph.get(p2));
		while(prev[i] != -1)
		{
			i = prev[i];
			stack.add(graph.get(vertexGraph.get(i)));
		}
		
		if(!(stack.peek().getName().equals(p1)))
		{
			System.out.println("No path found.");
			return;
		}
		
		while(!stack.empty())
		{
			Person k = stack.pop();
			if(stack.empty())
			{
				System.out.print(k.getName());
			}
			else
			{
				System.out.print(k.getName() + "--");
			}
		}
	}
	
	//prints out connectors in entire graph
	public void printConnectors(ArrayList<Person> connectors) 
	{
		String list = "";
		System.out.println(connectors.size());
		ArrayList<Person> result = new ArrayList<Person>();
		
		if (connectors.size() == 0)
		{
			System.out.println("No connectors.");
		}
		
		else
		{
			for (int i = 0; i < connectors.size(); i++)
			{
				if (!result.contains(connectors.get(i)))
				{
					result.add(connectors.get(i));
				}
			}
		
			for (int i = 0; i < result.size(); i++)
			{
				list += (result.get(i).getName() + ", ");
			}
			
			list = list.substring(0, list.length() - 2);
			System.out.println(list);
		}
	}
	
	//dfs for connectors
	public ArrayList<Person> dfs()
	{
		ArrayList<Person> current = new ArrayList<Person>();
		
		for (int v = 0; v < visitedDFS.length; v++) 
		{
			visitedDFS[v] = false;
		}
		
		for (int v = 0; v < visitedDFS.length; v++) 
		{
			if (!visitedDFS[v]) 
			{	
				Person p = graph.get(vertexGraph.get(v));
				cooldfs(p, p, 0, current);
			}
		}
		return current;
	}
	
	//recursive dfs
	private void cooldfs(Person x, Person starter, int num, ArrayList<Person> current)
	{
		x.setDfsNum(num + 1);
		x.setBackNum(num + 1);
		visitedDFS[reverseVertexGraph.get(x.getName())] = true;
		FriendNode ptr = x.friendsList;
		
		while(ptr != null)
		{
			//if the index has not been visited already
			if(!visitedDFS[reverseVertexGraph.get(ptr.person.getName())])
			{
				cooldfs(ptr.person, starter, num + 1, current);
				
				if(x.getDfsNum() <= ptr.person.getBackNum() && !x.equals(starter))
				{
					if(!current.contains(x))
					{
						current.add(x);
					}
				}
				else if(x.getDfsNum() > ptr.person.getBackNum())
				{
					x.setBackNum(Math.min(x.getBackNum(), ptr.person.getBackNum()));
				}
				else if(x.equals(starter) && x.getDfsNum() <= ptr.person.getBackNum())
				{
					
					if(ptr.person.getDfsNum() > 2 && ptr.person.getBackNum() == 1)
					{
						if(!current.contains(x))
						{
							current.add(x);
						}
					}
				}
			}
			
			else
			{
				x.setBackNum(Math.min(x.getBackNum(), ptr.person.getDfsNum()));	
			}
			
			ptr = ptr.friend;
			
			if(x.equals(starter))
			{
				num++;
			}
		}
	}
}



