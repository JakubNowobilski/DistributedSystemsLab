#include <iostream>
#include <Ice/Ice.h>

using namespace std;

class Matrix{
public:
    int rows;
    int columns;
    int **val;

    Matrix(int rows, int columns){
        this->rows = rows;
        this->columns = columns;
        this->val = new int*[rows];
        for(int i = 0; i < rows; i++){
            this->val[i] = new int[columns];
        }
    }

    void setSampleVal(){
        int count = 0;
        for(int i = 0; i < this->rows; i++)
            for(int j = 0; j < this->columns; j++)
                this->val[i][j] = count++;
    }

    friend ostream& operator<<(ostream& os, const Matrix& matrix);
};

ostream &operator<<(ostream &os, const Matrix& matrix) {
    os << "\n";
    for(int i = 0; i < matrix.rows; i++){
        os << "[";
        for(int j = 0; j < matrix.columns; j++)
            os << matrix.val[i][j] << " ";
        os << "]\n";
    }
    return os;
}

IceInternal::Handle<Ice::Communicator> communicator;

int main(int argc, char* argv[]) {
    Ice::CtrlCHandler ctrlCHandler;
    Ice::CommunicatorHolder ich(argc, argv, "../config.client");
    communicator = ich.communicator();
    ctrlCHandler.setCallback([](int){
        cout << "\nClosing connection to the server";
        communicator->destroy();
    });

    Ice::ObjectPrx calcPrx = communicator->propertyToProxy("Calc.Proxy");
    if(calcPrx == NULL){
        cerr << "Invalid proxy" << endl;
        return 1;
    }

    cout << "Connected to the server: " << endl;
    cout << calcPrx->ice_getConnection()->toString() << endl;

    do {
        vector<Ice::Byte> inParams, outParams;
        Ice::OutputStream out(communicator);
        string line;
        string op;
        cout << "==> ";
        getline(cin, line);
        if(line == "ADD" || line == "MUL"){
            int x;
            int y;
            if(line == "ADD"){
                x = 5;
                y = 10;
                op = "add";
            }
            else{
                x = 12;
                y = 14;
                op = "multiply";
            }
            out.startEncapsulation();;
            out.write(x);
            out.write(y);
            out.endEncapsulation();
            out.finished(inParams);

            cout << "\nSending request to the server: " << x << " " << op << " " << y << " ..." << endl;
        }else if(line == "DET"){
            out.startEncapsulation();
            int rows = 4;
            int columns = 6;
            out.write(rows);
            out.write(columns);
            Matrix m(rows, columns);
            m.setSampleVal();
            for(int i = 0; i < m.rows; i++)
                for(int j = 0; j < m.columns; j++)
                    out.write(m.val[i][j]);
            out.endEncapsulation();
            out.finished(inParams);
            op = "determinant";

            cout << "Sending request to the server: " << op << " of " << m << " ..." << endl;
        }else if(line == "BREAK"){
            int x = 12;
            int y = 14;
            op = "break";
            out.startEncapsulation();;
            out.write(x);
            out.write(y);
            out.endEncapsulation();
            out.finished(inParams);

            cout << "\nSending request to the server: " << x << " " << op << " " << y << " ..." << endl;
        }else if(line == "EXIT"){
            break;
        }else {
            continue;
        }

        try {
            if(calcPrx->ice_invoke(op, Ice::OperationMode::Normal, inParams, outParams)){
                Ice::InputStream in(communicator, outParams);
                in.startEncapsulation();
                int result;
                in.read(result);
                in.endEncapsulation();

                cout << "\nReceived response from the server.\nResult: " << result << endl;
            }
        }catch (const Ice::OperationNotExistException& ex){
            cerr << ex.what() << endl;
            continue;
        }
    } while (true);

    communicator->destroy();

    return 0;
}
