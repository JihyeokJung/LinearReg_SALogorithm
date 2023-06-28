package LinearRegression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class MetamarterialAntenna {
    static ArrayList<Double> vswr;
    static ArrayList<Double> s11;
    static double a, b;

    public static void main(String[] args) throws IOException {
        StringTokenizer st;
        GoCSV goCSV = new GoCSV("/Users/jihyeok/정지혁 프로젝트/study/Algorithm/src/LinearRegression/antenna.csv");
        String[] line = null;
        vswr = new ArrayList<>();
        s11 = new ArrayList<>();
        while ((line = goCSV.nextRead()) != null) {
            vswr.add(Double.parseDouble(line[0]));
            s11.add(Double.parseDouble(line[1]));
            /*for(String a : line){
                System.out.print(a+" ");
            }
            System.out.println();
            */
        }
        Collections.sort(vswr);

        calcRegLine();
        System.out.println("Regression Line : y = " + a + "x + (" + b + ")");
        System.out.println("MSE : " + calcMSE(a, b));

        gradientDescent(0.03, 2001);
        System.out.println("MSE : " + calcMSE(a, b));

        calcRegLine();
        SimulatedAnnealing();
    }

    static void calcRegLine() {
        double mx = 0, my = 0;
        int p = vswr.size();
        for (int i = 0; i < p; i++) {
            mx += vswr.get(i);
            my += s11.get(i);
        }
        mx = mx / p;
        my = my / p;

        double x = 0;
        double y = 0;

        for (int i = 0; i < p; i++) {
            x += Math.pow((vswr.get(i) - mx), 2);   // ((x - x의 평균)의 제곱)의 합
            y += (vswr.get(i) - mx) * (s11.get(i) - my);
        }
        a = x / y;
        b = my - a * mx;

        //소수점 2자리에서 반올림
        a = Math.round(a * 10) / 10.0;
        b = Math.round(b * 10) / 10.0;
    }

    static double calcMSE(double a, double b) {
        double y_hat;
        int p = vswr.size();
        double mse = 0.0;

        for (int i = 0; i < p; i++) {
            y_hat = a * vswr.get(i) + b;
            mse += Math.pow((y_hat - s11.get(i)), 2);
        }
        mse = mse / p;
        //return mse;
        return Math.round(mse * 10) / 10.0;
    }

    static void gradientDescent(double lr, int epochs) {
        a = 0.0;
        b = 0.0;
        ArrayList<Double> err;

        for (int i = 0; i < epochs; i++) {
            err = new ArrayList<>();
            double sum = 0.0;
            double sum_err = 0.0;

            for (int j = 0; j < vswr.size(); j++) {
                double y_hat = a * vswr.get(j) + b;
                err.add(y_hat - s11.get(j));

                sum_err += err.get(j);

                sum += err.get(j) * vswr.get(j);
            }

            //오차함수를 a, b로 미분한 값, 학습률에 따라 이동한 a, b의 변화량을 구하기 위함.

            double a_diff = (2.0 / vswr.size()) * sum;
            double b_diff = (2.0 / vswr.size()) * sum_err;

            a -= lr * a_diff;
            b -= lr * b_diff;
            if (i % 100 == 0) {
                System.out.println("epochs: " + i + ", 기울기: " + a + ", 절편: " + b);
            }
        }
    }

    //Simaulated Annelaring
    public static void SimulatedAnnealing() {
        double temperature = 3000;
        double coolingFactor = 0.4;

        //수식으로 Regression Line의 a와 b에서 bset가 어떻게 나오는지 알기 위해 a와 b로 초기화
        double cur_a = a, cur_b = b;
        double best_a= 0.0, best_b = 0.0;


        for (double t = temperature; t > 1; t *= coolingFactor) {
            double next_a = cur_a * Math.random();
            double next_b = cur_b * Math.random();

            if (Math.random() < probability(calcMSE(cur_a, cur_b), calcMSE(next_a, next_b), t)) {
                cur_a = next_a;
                cur_b = next_b;
            }

            if (calcMSE(cur_a, cur_b) < calcMSE(best_a, best_b)) {
                best_a = cur_a;
                best_b = cur_b;
            }

        }
        System.out.println("Final a(=w): "+best_a+", Final b: "+best_b+", Final MSE: " + calcMSE(best_a, best_b));
    }

    //두 번째 MSE가 첫 번째 MSE보다 짧은 경우 첫 번째 MSE를 유지. 그렇지 않으면 두 번째 둘러보기를 수락할 확률을 반환.

    public static double probability(double f1, double f2, double temp) {
        if (f2 < f1) return 1;
        //System.out.println(Math.exp((f1 - f2) / temp));
        return Math.exp((f1 - f2) / temp);
    }
}
