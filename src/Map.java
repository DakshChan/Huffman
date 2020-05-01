public class Map {
    
    private Node head;
    private int size;

    public Map() {
	this.head = null;
	this.size = 0;
    }

    public boolean put(String key, char value) {
	if (head == null) {
	    head = new Node(key, value, null);
	    size++;
	    return true;
	}
	Node currentNode = this.head;
	while(currentNode.getNext() != null) {
	    currentNode = currentNode.getNext();
	}
	currentNode.setNext(key, value);
	size++;
	return true;
    }

    public Node get(int index) {
	Node currentNode = this.head;
	for (int i = 0; i < index; i++) {
	    currentNode = currentNode.getNext();
	}
	return currentNode;
    }

    public boolean containsKey(String key) {
	for (int i = 0; i < size; i++) {
	    Node node = this.get(i);
	    if (node.key.equals(key)) {
		return true;
	    }
	}
	return false;
    }
    
    public char get(String key) {
	for (int i = 0; i < size; i++) {
	    Node node = this.get(i);
	    if (node.key.equals(key)) {
		return node.value;
	    }
	}
	return '|';
    }

    public String toString() {
	if (this.head == null) {return "";}
	Node currentNode = this.head;
	String str = "{ " + currentNode.key + ": " + currentNode.value;
	while(currentNode.getNext() != null) {
	    currentNode = currentNode.getNext();
	    str += ", " + currentNode.key + ": " + currentNode.value;
	}
	return str += " }";
    }

    private class Node {
	final String key;
	final char value;
	Node next;

	Node(String key, char value, Node next) {
	    this.key = key;
	    this.value = value;
	    this.next = next;
	}

	public Node getNext() {
	    return this.next;
	}

	public void setNext(String key, char value) {
	    this.next = new Node(key, value, null);
	}
	
    }
    
}
