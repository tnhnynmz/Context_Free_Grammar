
public class Node {
	
	private int xCoordinate;
	private int yCoordinate;
	private String variable;
	private Node leftChild;
	private Node rightChild;
	
	public Node(int x,int y, String variable){
		xCoordinate=x;
		yCoordinate=y;
		this.variable=variable;
		leftChild=null;
		rightChild=null;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}
	
	
	
	
	
	
}
