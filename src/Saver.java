import javax.swing.text.StyledEditorKit;
import java.io.*;

public class Saver {
    public static void SaveWallet(wallet w, boolean coinbase){
        try {
            String walletkey;
            if (!coinbase) {
                walletkey = StringUtil.getStringFromKey(w.publicKey).replace("/", "_");
            }else{
                walletkey = "COINBASE";
            }
            FileOutputStream fileOut = new FileOutputStream(String.format("./Wallets/%s", walletkey));

            // Creates an ObjectOutputStream
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

            // Writes objects to the output stream
            objOut.writeObject(w);

            objOut.close();
        } catch (Exception e){
        }
    }

    public static wallet CallWallet(String key, boolean coinbase){
        wallet w;
        try {
            String walletkey;
            if (!coinbase) {
                walletkey = key.replace("/", "_");
            }else{
                walletkey = "COINBASE";
            }
            //System.out.println();
            FileInputStream fileIn = new FileInputStream(String.format("./Wallets/%s", walletkey));
            ObjectInputStream objIn = new ObjectInputStream(fileIn);

            // Reads the objects
            w = (wallet) objIn.readObject();

            objIn.close();

            return w;
        } catch (Exception e){
            return null;
        }
    }


    public static void SaveBlockChain(DoublyLinkedList blockchain){
        try {
            FileOutputStream fileOut = new FileOutputStream("./BlockChain/BC");

            // Creates an ObjectOutputStream
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

            // Writes objects to the output stream
            objOut.writeObject(blockchain);
            objOut.close();
        } catch (Exception e){
        }
    }

    public static DoublyLinkedList CallBlockChain(){
        DoublyLinkedList BC;
        try {
            FileInputStream fileIn = new FileInputStream("./BlockChain/BC");
            ObjectInputStream objIn = new ObjectInputStream(fileIn);

            // Reads the objects
            BC = (DoublyLinkedList) objIn.readObject();

            objIn.close();

            return BC;
        } catch (Exception e){
            return new DoublyLinkedList();
        }
    }

    public static void SaveUTXO(HashMap<String, TransactionOutput> UTXO){
        try {
            FileOutputStream fileOut = new FileOutputStream("./BlockChain/UTXO");

            // Creates an ObjectOutputStream
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

            // Writes objects to the output stream
            objOut.writeObject(UTXO);

            objOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static HashMap<String, TransactionOutput> CallUTXO(){
        HashMap<String, TransactionOutput> UTXO;
        try {
            FileInputStream fileIn = new FileInputStream("./BlockChain/UTXO");
            ObjectInputStream objIn = new ObjectInputStream(fileIn);

            // Reads the objects
            UTXO = (HashMap<String, TransactionOutput>) objIn.readObject();

            objIn.close();

            return UTXO;
        } catch (Exception e){
            return new HashMap<>();
        }
    }
}
