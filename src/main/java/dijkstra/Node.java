package dijkstra;

/**
 * @ClassName wangfei
 * @Description TODO
 * @date 2022/2/20 1:43
 * @Version 1.0
 */
public class Node {

    int  sourceAddress;
    int destinationAddress;
    int price;

    public Node() {
    }

    public Node(int sourceAddress, int destinationAddress, int price) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Node{" +
                "sourceAddress=" + sourceAddress +
                ", destinationAddress=" + destinationAddress +
                ", price=" + price +
                '}';
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(int sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(int destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
