package edu.neu.coe.info6205.union_find;


import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import edu.neu.coe.info6205.util.Benchmark_Timer;

public class client {

    private static final int values = 15;
    private static final int runs = 10;
    private static final int n = 64;

    public client() {

    }
    private static int[] randomPairGen(int n) {
        int[] res = new int[2];
        res[0] = (int)(Math.random()*n);
        res[1] = (int)(Math.random()*n);
        return res;
    }

    private static int counts(int n, int runs, int pathCompression) {
        int len = n;
        double ven = 0;
        for(int i=0;i<runs;i++) {
            UF bi = null;
            if (pathCompression==0) bi = new UF_HWQUPC(len,false);
            if (pathCompression==1) bi = new UF_HWQUPC(len,true);
            if (pathCompression==2) bi = new WQUPC(len);
            int prs=0;
            while(bi.components()>1) {
                int[] tem = randomPairGen(len);
                bi.connect(tem[0], tem[1]);
                prs++;
            }
            ven+= prs;
        }
        return (int)(ven/runs);
    }

    private static double[] union_find_s(int n, int pathCompression) {
        int len = n;
        double[] res = new double[2];
        double dpt = 0;
        double prs = 0;
        for(int i = 0; i< runs; i++) {
            UF bi = null;
            if (pathCompression==0) bi = new UF_HWQUPC(len,false);
            if (pathCompression==1) bi = new UF_HWQUPC(len,true);
            if (pathCompression==2) bi = new WQUPC(len);
            while(bi.components()>1) {
                int[] tem = randomPairGen(len);
                bi.connect(tem[0], tem[1]);
                prs++;
            }
            if(pathCompression==2) dpt +=((WQUPC) bi).averageDepth();
            else dpt +=((UF_HWQUPC) bi).averageDepth();
        }
        res[0] = dpt/ runs;
        res[1] = prs/ runs;
        return res;
    }

    public static void main (String[] args) {
        UnaryOperator<Integer> pr = inp -> inp;
        Consumer<Integer> fn1 = inp -> counts(inp,1,0);
        Consumer<Integer> fn2 = inp -> counts(inp,1,1);
        Consumer<Integer> fn3 = inp -> counts(inp,1,2);
        Consumer<Integer> pst = inp -> System.out.print("");

        Benchmark_Timer<Integer> timer1 = new Benchmark_Timer<>("WQU Benchmark",pr,fn1,pst);
        Benchmark_Timer<Integer> timer2 = new Benchmark_Timer<>("WQUPH Benchmark",pr,fn2,pst);
        Benchmark_Timer<Integer> timer3 = new Benchmark_Timer<>("WQUPC Benchmark",pr,fn3,pst);

        try {
            FileWriter wrt = new FileWriter("Assignment4_data.csv");
            wrt.write("values,uncompressed_time,uncompressed_depth,uncompressed_pair, pathhalved_time, pathhalved_depth,pathhalved_pairs,compressed_time,compressed_depth,compressed_pairs,\n");

            for(int i = 0; i< values; i++) {
                System.out.println(i);
                int k = (int)Math.pow(2, i);
                k = k* n;
                double[] temp;
                wrt.write(k+",");
                wrt.write(timer1.run(k, runs)+",");
                temp = union_find_s(k,0);
                wrt.write(temp[0]+",");
                wrt.write(temp[1]+",");
                wrt.write(timer2.run(k, runs)+",");
                temp = union_find_s(k,1);
                wrt.write(temp[0]+",");
                wrt.write(temp[1]+",");
                wrt.write(timer3.run(k, runs)+",");
                temp = union_find_s(k,2);
                wrt.write(temp[0]+",");
                wrt.write(temp[1]+"\n");
            }
            wrt.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}