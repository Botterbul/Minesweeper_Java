
public class Driver
{
	public static void main(String [] args)
	{
	 
	 SLL<Integer> myList = new SLL<Integer>();
	 System.out.println("\nInitial list: ");
	 myList.showAll();
	 
	 System.out.println("\nAdd One item: ");// Add item to Empty list
	 myList.intsertSorted(new Integer(3));
	 myList.showAll();
	 
	 System.out.println("\nAdd One smaller than head:");// Add item to Empty list
	 myList.intsertSorted(new Integer(2));
	 myList.showAll();
	 System.out.println("\nAdd One larger than the last one:");// Add item to Empty list
	 myList.intsertSorted(new Integer(9));
	 myList.showAll();
	 System.out.println("\nAdd One in the middle:");// Add item to Empty list
	 myList.intsertSorted(new Integer(5));
	 myList.showAll();
	}
}