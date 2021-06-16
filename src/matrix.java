import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.*;
public class matrix {
    private double[][] array;
    private int rowAmount, columnAmount;
    private double epsilon;
    private double [] sums;
    private int[] notNullCombination;

    private void create(int k, int l) {
        this.array = new double[k][];
        int i;
        for (i = 0; i < k; i++)
            this.array[i] = new double[l];
    }

    public void print() {
        int i, j;
        for (i = 0; i < rowAmount; i++) {
            for (j = 0; j < columnAmount; j++)
                System.out.printf("%15.6E", array[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    public void init(String s) throws FileNotFoundException {
        File file = new File(s);
        Scanner scan = new Scanner(file);
        Pattern pat = Pattern.compile("[ \t]+");
        String str = scan.nextLine();
        String[] sn = pat.split(str);
        rowAmount = Integer.parseInt(sn[0]);
        columnAmount = Integer.parseInt(sn[1]) + 1;
        epsilon = Math.pow(10, -Double.parseDouble(sn[2]) - 1);
        notNullCombination = new int[rowAmount];
        this.create(rowAmount, columnAmount);
        int i, j;
        for (i = 0; i < rowAmount; i++) {
            str = scan.nextLine();
            sn = pat.split(str);
            notNullCombination[i] = i;
            for (j = 0; j < columnAmount; j++)
                array[i][j] = Double.parseDouble(sn[j]);
        }
        scan.close();
        sums = sumOfLines(array);

    }

    //comparing with 0, considering accuracy
    private boolean compareToZero(double a){
        return (Math.abs(a) < epsilon);
    }


    public boolean checkForZeros(int[] combination) {
        for (int i = 0; i < rowAmount; i++)
            if (compareToZero(array[combination[i]][i]))
                return false;
        return true;
    }

    //проверка ДУС нашей изначальной системы
    public boolean checkSCC(int[] combination) {
        boolean strictlyMore = false;
        boolean haveZeroes = false;
        boolean SCCworked = true;
        for (int i = 0; i < rowAmount; i++) {
            if (compareToZero(array[combination[i]][i])) haveZeroes = true;
            double sum = Math.abs(sums[combination[i]]) - Math.abs(array[combination[i]][i]) - Math.abs(array[combination[i]][i]);
            if (sum <= 0) {
                if (sum < 0)
                    strictlyMore = true;
            } else SCCworked = false;
        }
        if (!haveZeroes){
            if (rowAmount >= 0) System.arraycopy(combination, 0, notNullCombination, 0, rowAmount);
        }


        return strictlyMore && SCCworked;
    }


    //поиск сумм строк матрицы
    private double[] sumOfLines(double[][] matrix){
        double[] temp = new double[matrix.length];
        for (int i = 0; i < matrix.length; i ++) {
            for (int j = 0; j < matrix.length; j++)
                temp[i] += Math.abs(matrix[i][j]);
        }
        return temp;
    }

    //решение без контроля
    public double[] solveByIterations(){
        double[] result = new double[rowAmount];
        double x, summary;
        do {
            x = result[0];
            for (int i = 0; i < rowAmount; i++){
                summary = array[i][columnAmount - 1];
                for (int j = 0; j < rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * array[i][j];
                result[i] = summary/array[i][i];
            }
        } while (Math.abs(result[0] - Math.abs(x)) >= epsilon);
        return result;
    }


    //решение с контролем
    public double[] solveByIterationsWithControl(){
        double[] result = new double[rowAmount];
        double x, summary;
        double delta;
        double localMaximum = Double.MIN_VALUE;
        int i, j;
        //проверка системы на сходимость через проверку первых 10 итераций
        for (int q = 0; q < 10; q++){
            x = result[0];
            for (i = 0; i < rowAmount; i++){
                summary = array[i][columnAmount - 1];
                for (j = 0; j < rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * array[i][j];
                result[i] = summary/array[i][i];
            }
            delta = Math.abs(Math.abs(x) - Math.abs(result[0]));
            if (q > 5){
                if (delta > localMaximum)
                    localMaximum = delta;
            }
        }
        //если система не сходится, то возвращаем null
        if (localMaximum > epsilon) return null;
        //если система сходится - решаем дальше
        do {
            x = result[0];
            for (i = 0; i < rowAmount; i++){
                summary = array[i][columnAmount - 1];
                for (j = 0; j < rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * array[i][j];
                result[i] = summary/array[i][i];
            }
        } while (Math.abs(result[0] - Math.abs(x)) >= epsilon);
        return result;
    }


    //убираем 0 с диагонали
    public boolean removeZeroesFromDiagonal(int diag, int[] combination){
        //если можно сделать перестановку - делаем
        if(checkSCC(combination))
            return true;
        else if(diag >= rowAmount) return false;
        if(removeZeroesFromDiagonal(diag + 1,combination))
            return true;

        for (int i = diag + 1; i < rowAmount; i++) {
            swapElements(i, diag,combination);
            if(removeZeroesFromDiagonal(diag + 1,combination))
                return true;
            swapElements(diag,i,combination);
        }

        return false;
    }


    public int checkAnswer(int[] combination) {
        if(removeZeroesFromDiagonal(0,combination)) {
            array = replaceWithCombination(combination);
            return 2;
        }

        if(checkForZeros(notNullCombination)) {
            array = replaceWithCombination(notNullCombination);
            return 3;
        }
        return 1;
    }

    public double[][] replaceWithCombination(int[] combination){
        double[][] matrix = new double[rowAmount][];
        for (int i = 0; i < matrix.length; i ++){
            matrix[i] = array[combination[i]];
        }
        this.array = matrix;
        return matrix;
    }


    private void swapElements(int i, int j, int[] combination){
        int temp = combination[i];
        combination[i] = combination[j];
        combination[j] = temp;
    }

    public int[] getCombination(){
        int[] result = new int[rowAmount];
        for (int i = 0; i < rowAmount; i++){
            result[i] = i;
        }
        return result;
    }

}
