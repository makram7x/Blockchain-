import java.io.Serializable;

class Node implements Serializable {

    private Block data; // node storing int data
    private Node nextNode; // the next pointer node, the arrow in drawing
    private Node prevNode;

    // don't forget the class constructor
    public Node(Block data2, Node prevNode) {
        this.data = data2;
        this.prevNode = prevNode;
        if (prevNode!=null) {
            prevNode.setNextNode(this);
        }
    }

    // since we made variable private
    // to access them we need setters and getters
    public Block getData() {
        return this.data;
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public Node getPrevNode() {
        return this.prevNode;
    }

    public Block setData(Block data) {
        return this.data = data;
    }

    public Node setNextNode(Node nextNode) {
        return this.nextNode = nextNode;
    }

    public Node setpervNode(Node prevNode) {
        return this.prevNode = prevNode;
    }

    @Override
    public String toString() {
        return "Data: " + this.data;
    }


}


public class DoublyLinkedList implements Serializable {

    private Node head;
    private Node tail;
    private int size = 0;


    public void DoublyLinkedList() {

    }

    public int getSize() {
        return this.size;
    }

    public boolean IsEmpty(){
        return size == 0;
    }

    public void addLast(Block block) {
        tail = new Node(block, tail);
        if (head == null)
            head = tail;
        size++;
    }



    public Node removeFirst() {
        Node removed = this.head;
        this.head = this.head.getNextNode();
        this.size--;
        return removed;
    }

    @Override
    public String toString() {
        String output = " ";
        Node fromHead = this.head;
        while (fromHead != null) {
            output = output + fromHead.getData();
            if (fromHead != this.tail)
                output = output + " >> ";
            fromHead = fromHead.getNextNode();
        }
        output += "\n";
        Node fromTail = this.tail;
        while (fromTail != null) {
            output = output + fromTail.getData();
            if (fromTail != this.head)
                output = output + " << ";
            fromTail = fromTail.getPrevNode();
        }
        return output;
    }

    public Block getElement(int index) {
        Node current = head;
        int count = 0;
        while (current != null) {
            if (count == index) {
                return current.getData();
            }
            current = current.getNextNode();
            count++;
        }
        return null;
    }

    public boolean contains(Block data) {
        Node current = this.head;
        while (current != null) {
            if (current.getData() == data) {
                return true;
            }
            current = current.getNextNode();
        }
        return false;
    }

    public void clear() {
        while (this.head != null) {
            this.removeFirst();
        }
        System.out.println("List Is Cleared!");
    }


}

