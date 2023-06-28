package LinearRegression;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GoCSV {

    private String filePath;
    private BufferedReader bufferedReader;
    private List<String[]> readCSV;

    private int index;

    //This constructor is for read CSV File
    public GoCSV(String filePath) throws IOException {
        this.filePath = filePath;
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath), "UTF-8"));
        readCSV = new ArrayList<>();

        makeList(bufferedReader);
        this.index = 1;     //1열(domain)빼고 반환
    }

    public void makeList(BufferedReader bufferedReader) throws IOException {
        String line = null;
        while((line = bufferedReader.readLine())!=null) {
            String[] lineContents = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",-1);
            String[] arr = {lineContents[8], lineContents[10]};
            //readCSV.add(lineContents);
            readCSV.add(arr);
        }
    }
    //한 행을 읽음
    public String[] nextRead(){
        if(readCSV.size() == index){
            return null;
        }
        return readCSV.get(index++);
    }
}
