import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Encoder {
	public static void main(String[] args) throws IOException {
		FileInputStream original = null;
		String fileName = null;
		try {
			Scanner userIn = new Scanner(System.in);
			System.out.println("Enter a file name to encode: ");
			fileName = userIn.next();
			original = new FileInputStream(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(e.hashCode());
		}

		DoubleLinkedList freqList = new DoubleLinkedList();

		byte rep;
		while ((rep = (byte) original.read()) != -1) {
			freqList.incrementFreq(rep);
		}

		System.out.println(freqList);
		freqList.sort();
		System.out.println(freqList);
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
		this.right= null;
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

class DoubleLinkedList {
	//Uses Node Left and Right for linking
	Node head;
	Node tail;
	int size;

	public int getSize() {
		return size;
	}

	DoubleLinkedList() {
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
		tail.setRight(sentinel);
		sentinel.setLeft(tail);
		tail = sentinel;
		while (curNode.getRep() != rep) {
			curNode = curNode.getRight();
		}
		if (curNode == tail) {
			return;
		}
		curNode.setData(curNode.getData() + 1);
		tail = sentinel.getLeft();
		tail.setRight(null);
		sentinel.setLeft(null);
		size--;
	}

	//Sort least data to greatest using merge sort
	public void sort() {
		if (this.size >= 2) {
			sortRecurse(this);
		}
	}

	private void sortRecurse(DoubleLinkedList list) {
		if (list.getSize() == 2) {
			if (list.tail.getData() < list.head.getData()) {
				Node tail = list.tail;
				list.tail = list.head;
				list.head = tail;
				list.tail.setRight(null);
				list.tail.setLeft(tail);
				list.head.setRight(list.tail);
				list.head.setLeft(null);
			}
			return;
		} else if (list.getSize() == 1) {
			return;
		}
		int middle = list.getSize()/2;
		DoubleLinkedList one = new DoubleLinkedList();
		DoubleLinkedList two = new DoubleLinkedList();
		Node curNode = list.head;
		for (int i = 0; i < middle - 1; i++) {
			curNode = curNode.getRight();
		}
		one.head = list.head;
		one.tail = curNode;
		one.size = middle;
		two.head = curNode.getRight();
		two.tail = list.tail;
		two.size = list.getSize()-one.size;

		one.tail.setRight(null);
		two.head.setLeft(null);

		if (one.size >= 2) {
			sortRecurse(one);
		}
		if (two.size >= 2) {
			sortRecurse(two);
		}
		if (one.head.getData() < two.head.getData()) {
			list.head = one.head;
			one.head = one.head.getRight();
			list.head.setRight(null);
			one.head.setLeft(null);
			list.tail = list.head;
			one.size--;
		} else {
			list.head = two.head;
			two.head = two.head.getRight();
			list.head.setRight(null);
			two.head.setLeft(null);
			list.tail = list.head;
			two.size--;
		}
		while (one.size > 0 && two.size > 0) {
			if (one.head.getData() < two.head.getData()) {
				list.tail.setRight(one.head);
				one.head.setLeft(list.tail);
				list.tail = one.head;
				one.head = one.head.getRight();
				if (one.head != null) {
					one.head.setLeft(null);
				}
				list.tail.setRight(null);
				one.size--;
			} else {
				list.tail.setRight(two.head);
				two.head.setLeft(list.tail);
				list.tail = two.head;
				two.head = two.head.getRight();
				if (two.head != null) {
					two.head.setLeft(null);
				}
				list.tail.setRight(null);
				two.size--;
			}
		}
		if (one.size > 0) {
			list.tail.setRight(one.head);
			one.head.setLeft(list.tail);
			list.tail = one.tail;
		} else if (two.size > 0) {
			list.tail.setRight(two.head);
			two.head.setLeft(list.tail);
			list.tail = two.tail;
		}
	}

	@Override
	public String toString() {
		Node curNode = head;
		String out = "";
		while (curNode != null) {
			out += (char) curNode.getRep() + " " + curNode.getData() + ", ";
			curNode = curNode.getRight();
		}
		return out;
	}

	public Node pop() {
		if (head == null) {
			return null;
		}
		Node first = head;
		head = first.getRight();
		if (head != null) {
			head.setLeft(null);
			first.setRight(null);
		} else {
			head = null;
			tail = null;
		}
		return first;
	}
}

class InversePriorityQueue {
	Node head;
	Node tail;
	int size;

	InversePriorityQueue() {
		head = null;
		tail = null;
		size = 0;
	}

	public void enqueue(Node node) {
		tail.setNext(node);
		if (size == 0) {
			head = tail;
		}
		size++;
	}

	public Node dequeue() {
		if (size == 0) {
			return null;
		}
		Node minNode = head;
		Node curNode = head;
		Node priorToMinNode = head;
		while (curNode.getNext() != null) {
			if (curNode.getNext().getData() < minNode.getData()) {
				priorToMinNode = curNode;
				minNode = curNode.getNext();
			}
			curNode = curNode.getNext();
		}
		priorToMinNode.setNext(minNode.getNext());
		return minNode;
	}
}

class TreeBuilder {
	InversePriorityQueue queue;
	TreeBuilder(DoubleLinkedList list) {
		queue = new InversePriorityQueue();
		while (list.size > 0) {
			Node temp = list.pop();
			temp.setLeft(null);
			temp.setRight(null);
			queue.enqueue(temp);
		}
	}
}