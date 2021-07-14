import com.zeroc.Ice.*;

public class CalcI implements Blobject {
    @Override
    public Ice_invokeResult ice_invoke(byte[] bytes, Current current) {
        System.out.println("\nNew operation [" + current.operation + "] invoked by client");

        Communicator communicator = current.adapter.getCommunicator();
        InputStream in = new InputStream(communicator, bytes);
        OutputStream out = new OutputStream(communicator);
        Ice_invokeResult invokeResult = new Ice_invokeResult();
        String op = current.operation;

        if(op.equals("add") || op.equals("multiply")){
            in.startEncapsulation();
            int x = in.readInt();
            int y = in.readInt();
            in.endEncapsulation();

            System.out.println("\tCalculating " + x + " " + op + " " + y + " ...");

            out.startEncapsulation();
            int result;
            if(op.equals("add"))
                result = x + y;
            else
                result = x * y;

            System.out.println("\tResult: " + result);

            out.writeInt(result);
            out.endEncapsulation();
            invokeResult.outParams = out.finished();
            invokeResult.returnValue = true;
        } else if (op.equals("determinant")){
            in.startEncapsulation();
            int rows = in.readInt();
            int columns = in.readInt();
            System.out.println("rows: " + rows + " columns" + columns);
            Matrix matrix = new Matrix(rows, columns);
            for(int i = 0; i < rows; i++)
                for(int j = 0; j < columns; j++)
                    matrix.val[i][j] = in.readInt();
            in.endEncapsulation();

            System.out.println("\tCalculating " + op + " of " + matrix + " ...");

            out.startEncapsulation();
            int result = matrix.determinant();

            System.out.println("\tResult: " + result);

            out.writeInt(result);
            out.endEncapsulation();
            invokeResult.outParams = out.finished();
            invokeResult.returnValue = true;
        } else {
            OperationNotExistException ex = new OperationNotExistException();
            ex.id = current.id;
            ex.facet = current.facet;
            ex.operation = current.operation;
            System.out.println("\tOperation does not exist - throwing exception");
            throw ex;
        }

        System.out.println("\nReturning value to the client");
        return invokeResult;
    }
}
