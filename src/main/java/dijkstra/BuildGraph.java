package dijkstra;

/**
 * @ClassName wangfei
 * @Description TODO
 * @date 2022/2/20 1:26
 * @Version 1.0
 */

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 此类功能：
 * 1.读取excel文件，获取拓扑结构
 * 2.处理获取过的拓扑结构，使用临接矩阵存储，构建带权无向图
 */
public class BuildGraph {
    /**
     * 读excel获取信息
     * @return
     * @throws Exception
     */
    public List<Node> readTable() throws Exception
    {
        InputStream ips = new FileInputStream("C:\\Users\\wangfei\\Desktop\\路由表.xls");
        HSSFWorkbook wb = new HSSFWorkbook(ips);
        HSSFSheet sheet = wb.getSheetAt(0);
        List nodeList=new ArrayList<>();
        for (Iterator ite = sheet.rowIterator(); ite.hasNext(); ) {
            HSSFRow row = (HSSFRow) ite.next();
            System.out.println();
            Node node=new Node();
            for (Iterator itet = row.cellIterator(); itet.hasNext();) {
                HSSFCell cell = (HSSFCell) itet.next();
                CellType cellTypeEnum = cell.getCellTypeEnum();
                switch (cellTypeEnum) {
                    case NUMERIC:
                         Integer integer= (int)cell.getNumericCellValue();
                         System.out.print(integer + "       ");
                         switch (cell.getColumnIndex())
                         {
                             case 0:
                                 node.setSourceAddress((int)cell.getNumericCellValue());
                                 break;
                             case 1:
                                 node.setDestinationAddress((int)cell.getNumericCellValue());
                                 break;
                             case 2:
                                 node.setPrice((int) cell.getNumericCellValue());
                                 nodeList.add(node);
                                 break;
                         }
                         break;
                    case STRING:
                        //读取String
                            System.out.print(cell.getRichStringCellValue().toString() + " ");
                        break;
                }
            }
        }
        System.out.println(nodeList);
        return nodeList;
    }
    public Map checkDesMultiple(List<Node> nodeList) throws Exception {
        Map desMap=new HashMap();
        for  (Node node : nodeList) {
            if(node.getSourceAddress()==node.getDestinationAddress())
            {
                throw new Exception("存在源地址与目的地址相同的结点，请检查源地址为r"+node.getDestinationAddress()+"的路由器");
            }
            //以所有的源地址为key给相同源地址的路由器建立一个目的地址的Map，用来检查是否从在源地址与目的地址都相同的多个路由路径

            if(!desMap.containsKey(String.valueOf(node.getSourceAddress())))
            {
                ArrayList<Integer> arrayList=new ArrayList();
                arrayList.add(node.getDestinationAddress());
                desMap.put(String.valueOf(node.getSourceAddress()),arrayList);
            }
            else
            {
                ArrayList<Integer> arrayList = (ArrayList) desMap.get(String.valueOf(node.getSourceAddress()));
                if(arrayList.contains(node.getDestinationAddress()))
                {
                    throw new Exception("存在两个相同的源地址与目的地址的结点，请检查源地址和目的地址为r"+node.getDestinationAddress()+"的路由器");
                }
                arrayList.add(node.getDestinationAddress());
                desMap.put(String.valueOf(node.getSourceAddress()),arrayList);
            }
        }
        System.out.println(desMap);
        //插件是否存在循环路由
        Set set = desMap.keySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext())
        {
            String key= (String) iterator.next();
            ArrayList<Integer> desList= (ArrayList) desMap.get(key);
            for (Integer integer : desList) {
                //检查如果目的地址作为另一个结点的源地址中是否包含循环路由
                //比如{1=[2, 7, 6], 2=[3, 4], 3=[4, 6, 7], 4=[5, 7, 8, 9], 5=[6], 6=[7], 7=[8], 8=[9]}
                //需要检查1号路由结点中的目的地址2里面是否也包含了1
                ArrayList<Integer> arrayList= (ArrayList<Integer>) desMap.get(String.valueOf(integer));
                if(arrayList!=null)
                {

                    if(arrayList.contains(Integer.parseInt(key)))
                    {
                        throw new Exception("存在循环依赖，请检查源地址为r"+integer+"或者为r"+key+"的路由器"+"，产生的冲突为r"+integer+"和r"+key);
                    }
                }
            }
        }
        return desMap;
    }

    public ArrayList calculateVertexNum(Map map)
    {
        Iterator iterator = map.keySet().iterator();
        ArrayList<Integer> vertexList=new ArrayList();
        while(iterator.hasNext())
        {
            String key= (String) iterator.next();

            if(!vertexList.contains(Integer.parseInt(key))){
                vertexList.add(Integer.parseInt(key));
            }
            ArrayList<Integer> desList= (ArrayList<Integer>) map.get(key);
            for (Integer integer : desList) {
                if(!vertexList.contains(integer))
                {
                    vertexList.add(integer);
                }
            }
        }

        return vertexList;
    }
    public Map buildMatrix(int vertexNum, ArrayList<Integer> vertex,Map map,List<Node> nodeArrayList)
    {
        Collections.sort(vertex);
        System.out.println(vertex);
        int[][] matrix=new int[vertexNum][vertexNum];
        System.out.println(vertexNum);
        //创建哈希表，给每个元素编号
        Hashtable hashtable=new Hashtable();
        for(int i=0;i<vertex.size();i++)
        {
            hashtable.put(String.valueOf(vertex.get(i)),i);
        }

        System.out.println(hashtable);
        //构建邻接矩阵
        //初始化构建邻接矩阵
       for(int i=0;i<vertexNum;i++)
       {
           for (int j=0;j<vertexNum;j++)
           {
               if(i==j)
               {
                   matrix[i][j]=0;
               }
               else
               {
                   //999代表最大路径，表示道路不通
                   matrix[i][j]=999;
               }
           }
       }
        //构建邻接矩阵
        for(int i=0;i<vertexNum;i++)
        {
            for (Integer integer : vertex) {
               Integer nodeId= (Integer) hashtable.get(String.valueOf(integer));
               ArrayList<Integer> arrayList= (ArrayList<Integer>) map.get(String.valueOf(integer));
               if(arrayList!=null)
               {
                   for (Integer integer1 : arrayList) {
                       for (Node node : nodeArrayList) {
                           if(node.getSourceAddress()==integer&&node.getDestinationAddress()==integer1)
                           {
                               matrix[nodeId][(int) hashtable.get(String.valueOf(integer1))]=node.getPrice();
                               matrix[(int) hashtable.get(String.valueOf(integer1))][nodeId]=node.getPrice();
                           }
                       }
                   }
               }
            }
        }
        Map result=new HashMap();
        result.put("matrix",matrix);
        result.put("vertex",vertex);
        result.put("hashtable",hashtable);
        return result;
    }

    public void buildGraph(Integer routerId) throws Exception {
        List<Node> nodeList = readTable();
        Map map = checkDesMultiple(nodeList);
        int edgeNum=nodeList.size();
        int vertexNum=calculateVertexNum(map).size();
        Map result=buildMatrix(vertexNum,calculateVertexNum(map),map,nodeList);
        int [][]  matrix= (int[][]) result.get("matrix");
        ArrayList<Integer> vertex= (ArrayList<Integer>) result.get("vertex");
        Hashtable hashtable= (Hashtable) result.get("hashtable");
        for(int i=0;i<vertexNum;i++)
        {
            for (int j=0;j<vertexNum;j++)
            {
                System.out.printf("%7d",matrix[i][j]);
            }
            System.out.println();
        }
        dijkstra(matrix,routerId,vertex,hashtable);
    }
    public  int[] dijkstra(int[][]matrix,int routeId,ArrayList<Integer> vertex,Hashtable hashtable) throws Exception {
        int n = matrix.length;
        //存放从start到其他各点的最短路径
        int start=0;
        int[] shortPath = new int[n];
        if(vertex.contains(routeId))
        {
           start= (int) hashtable.get(String.valueOf(routeId));
        }
        else {
            throw  new Exception("您输入的路由器编号不存在，请重试");
        }

        //存放从start到其他各点的最短路径的字符串表示
        String[] path=new String[n];
        for(int i=0;i<n;i++)
        {
            path[i] = "r"+routeId + "-->r" + vertex.get(i);
        }
        //标记当前该顶点的最短路径是否已经求出,1表示已求出
        int[] visited = new int[n];
        //初始化shortPath数组
        for(int i=0;i<matrix.length;i++)
        {
            shortPath[i]=matrix[start][i];
        }
        visited[start] = 1;
        for(int count = 1;count <= n - 1;count++)
        {
            //选出一个距离初始顶点start最近的未标记顶点
            int k = -1;
            int dmin=999;
            for(int i = 0;i < n;i++)
            {
                if(visited[i] == 0 )
                {
                    if(matrix[start][i]<dmin)
                    {
                        dmin = matrix[start][i];
                        k = i;
                    }
                }
            }
            //将新选出的顶点标记为已求出最短路径，且到start的最短路径就是dmin
            shortPath[k] = dmin;
            visited[k] = 1;
            //以k结点开始修正其他结点
            for(int i = 0;i < n;i++)
            {
                if(visited[i] == 0 && matrix[start][k] + matrix[k][i] < matrix[start][i])
                {
                    matrix[start][i] = matrix[start][k] + matrix[k][i];
                    path[i]=path[k]+"-->r"+vertex.get(i);
                }
            }
        }
        for(int i=0;i<n;i++)
        {
            System.out.println("从路由器r"+routeId+"出发到路由器r"+vertex.get(i)+"的最短路径为："+path[i]+"带权路径长度为"+shortPath[i]);
        }
        System.out.println("=====================================");
        //初始化final数组
        return null;
    }
}
