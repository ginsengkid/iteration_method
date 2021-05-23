import java.io.FileNotFoundException;

public class test {
    public static void main(String[] args) {

        matrix mat = new matrix();
        try {
            mat.init("D:\\file.txt");
        }
        catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!!!");
        }
        mat.print();

        int result;
        double[] array;

        /*mat.setCombination(new int[]{1,3,2,0});
        System.out.println(mat.checkForZeros());
        mat.replaceWithCombination();
        mat.print();*/

        if (mat.checkForZeros()) {
            if (mat.checkSCC()) {
                array = mat.solveByIterations();
                printArray(array);
            }
            else {
                array = mat.solveByIterationsWithControl();
                if (array != null) printArray(array);
            }
        }

        else {
            result = mat.checkAnswer();
            getResult(result);
            mat.print();
            if (result == 2){
                array = mat.solveByIterations();
                printArray(array);
            }
            if (result == 3){
                array = mat.solveByIterationsWithControl();
                if (array == null) getResult(1);
                else printArray(array);
            }
        }
    }


    public static void getResult(int result) {
        switch (result) {
            case 1 -> System.out.println("Систему нельзя решить итерационным методом \n");
            case 2 -> System.out.println("Система больше не имеет 0 на диагонали и соблюдается ДУС \n");
            case 3 -> System.out.println("Система больше не имеет 0 на диагонали, но не соблюдается ДУС \n");
        }
    }

    public static void printArray(double [] matrix) {
        System.out.println("Результат: ");
        for (double v : matrix) System.out.printf("%15.6E", v);
        System.out.println();
    }

}