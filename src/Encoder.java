import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Encoder {
	public static void main(String[] args) throws IOException {
		FileInputStream original = null;
		String fileName = null;
		try {
			Scanner userIn = new Scanner(System.in);
			//System.out.println("Enter a file name to encode: ");
			//fileName = userIn.next();
			fileName = "src/ORIGINAL.TXT";
			original = new FileInputStream(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(e.hashCode());
		}

		AllInOneButTree allButTree = new AllInOneButTree();

		byte rep;
		while ((rep = (byte) original.read()) != -1) {
			allButTree.incrementFreq(rep);
		}

		System.out.println(allButTree);
		allButTree.sort();
		System.out.println(allButTree);

		Tree huffman = new Tree(allButTree);
		original.close();
	}
}

class Node {
	private byte rep;
	private int data;
	private Node left;
	private Node right;
	private Node next;

	Node (byte rep, int data) {
		this.rep = rep;
		this.data = data;
		this.left = null;
		this.right = null;
		this.next = null;
	}

	public byte getRep() {
		return rep;
	}

	public void setRep(byte rep) {
		this.rep = rep;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}
}

class AllInOneButTree {
	//Uses Node Left and Right for linking
	Node head;
	Node tail;
	int size;

	public int getSize() {
		return size;
	}

	AllInOneButTree() {
		head = null;
		tail = null;
		size = 0;
	}

	//Modified Sentinel search to either increment the freq or add a new Node with freq of 1
	public void incrementFreq(byte rep) {
		size++;
		if (head == null) {
			head = new Node(rep, 1);
			tail = head;
			return;
		}
		Node curNode = head;
		Node sentinel = new Node(rep, 1);
		tail.setNext(sentinel);
		while (curNode.getRep() != rep) {
			curNode = curNode.getNext();
		}
		if (curNode == sentinel) {
			tail = sentinel;
			return;
		}
		curNode.setData(curNode.getData() + 1);
		tail.setNext(null);
		size--;
	}

	//Sort least data to greatest using merge sort
	public void sort() {
		if (this.size >= 2) {
			sortRecurse(this);
		}
	}

	private void sortRecurse(AllInOneButTree list) {
		if (list.getSize() == 2) {
			if (list.tail.getData() < list.head.getData()) {
				Node tail = list.tail;
				list.tail = list.head;
				list.head = tail;
				list.tail.setNext(null);
				list.head.setNext(list.tail);
			}
			return;
		} else if (list.getSize() == 1) {
			return;
		}
		int middle = list.getSize()/2;
		AllInOneButTree one = new AllInOneButTree();
		AllInOneButTree two = new AllInOneButTree();
		Node curNode = list.head;
		for (int i = 0; i < middle - 1; i++) {
			curNode = curNode.getNext();
		}
		one.head = list.head;
		one.tail = curNode;
		one.size = middle;
		two.head = curNode.getNext();
		two.tail = list.tail;
		two.size = list.getSize()-one.size;

		one.tail.setNext(null);

		if (one.size >= 2) {
			sortRecurse(one);
		}
		if (two.size >= 2) {
			sortRecurse(two);
		}
		if (one.head.getData() < two.head.getData()) {
			list.head = one.head;
			one.head = one.head.getNext();
			list.head.setNext(null);
			list.tail = list.head;
			one.size--;
		} else {
			list.head = two.head;
			two.head = two.head.getNext();
			list.head.setNext(null);
			list.tail = list.head;
			two.size--;
		}
		while (one.size > 0 && two.size > 0) {
			if (one.head.getData() < two.head.getData()) {
				list.tail.setNext(one.head);
				list.tail = one.head;
				one.head = one.head.getNext();
				list.tail.setNext(null);
				one.size--;
			} else {
				list.tail.setNext(two.head);
				list.tail = two.head;
				two.head = two.head.getNext();
				list.tail.setNext(null);
				two.size--;
			}
		}
		if (one.size > 0) {
			list.tail.setNext(one.head);
			list.tail = one.tail;
		} else if (two.size > 0) {
			list.tail.setNext(two.head);
			list.tail = two.tail;
		}
	}

	@Override
	public String toString() {
		Node curNode = head;
		String out = "";
		while (curNode != null) {
			out += (char) curNode.getRep() + " " + curNode.getData() + ", ";
			curNode = curNode.getNext();
		}
		return out;
	}

	public void enqueue(Node node) {
		if (size == 0) {
			tail = node;
			head = tail;
			size ++;
			return;
		}

		tail.setNext(node);

		tail = node;
		size++;
	}

	//dequeue min first
	public Node dequeue() {
		if (size == 0) {
			return null;
		}
		if (size == 1) {
			size = 0;
			Node ret = head;
			head = null;
			tail = null;
			return ret;
		}
		Node minNode = head;
		Node curNode = head;
		Node priorToMinNode = null;
		while (curNode.getNext() != null) {
			if (curNode.getNext().getData() < minNode.getData()) {
				priorToMinNode = curNode;
				minNode = curNode.getNext();
			}
			curNode = curNode.getNext();
		}
		if (priorToMinNode != null) {
			priorToMinNode.setNext(minNode.getNext());
		} else {
			head = minNode.getNext();
		}
		minNode.setNext(null);
		size--;
		return minNode;
	}
}

class Tree {
	AllInOneButTree queue;
	Tree (AllInOneButTree queue){
		this.queue = queue;
		huffmanBuilder(queue);
	}

	private void huffmanBuilder(AllInOneButTree queue) {
		while (queue.size > 1) {
			System.out.println(queue + " 1 " + queue.getSize());

			Node left = queue.dequeue();
			System.out.println(queue + " 2 " + queue.getSize());

			Node right = queue.dequeue();
			System.out.println(queue + " 3 " + queue.getSize());
			int data = 0;
			if (left != null) {
				data += left.getData();
			}
			if (right != null) {
				data += right.getData();
			}
			Node parent = new Node((byte) -1 , data);
			parent.setLeft(left);
			parent.setRight(right);
			queue.enqueue(parent);
			System.out.println(queue + " 4 " + queue.getSize());
		}
		System.out.println(this);
	}

	@Override
	public String toString() {
		return recPrint(queue.head);
	}

	private String recPrint(Node e) {
		if (e.getRep() == (byte) -1){
			return "(" + recPrint(e.getLeft()) + " " + recPrint(e.getRight()) + ")";
		}
		return ""+(e.getRep());
	}
}