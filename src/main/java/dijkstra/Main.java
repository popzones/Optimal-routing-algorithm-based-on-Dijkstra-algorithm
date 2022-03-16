package dijkstra;

import java.util.Scanner;

public class Main {
    public static void main(String s []) throws Exception {
        BuildGraph readExcelData=new BuildGraph();
        Scanner scanner=new Scanner(System.in);
        while(true)
        {
            System.out.println("请输入要查看的路由器编号");
            int routerId= scanner.nextInt();
            readExcelData.buildGraph(routerId);
        }
    }
}
