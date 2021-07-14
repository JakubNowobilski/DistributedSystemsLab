public class Matrix {
    private int rows;
    private int columns;
    public int[][] val;

    public Matrix(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.val = new int[rows][columns];
    }

    public int determinant(){
        int det = 0;
        for(int i = 0; i < this.rows; i++)
            for(int j = 0; j < this.columns; j++)
                det += this.val[i][j];
        return det;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for(int i = 0; i < this.rows; i++){
            builder.append("[");
            for(int j = 0; j < this.columns; j++)
                builder.append(this.val[i][j]).append(" ");
            builder.append("]\n");
        }
        return builder.toString();
    }
}
