import java.io.Serializable;
import java.security.*;

public class Transaction implements Serializable {

    public String transactionId; // this is also the hash of the transaction.
    public PublicKey sender; // senders address/public key.
    public PublicKey reciepient; // Recipients address/public key.
    public int value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    public ArrayList<TransactionInput> Input;
    public ArrayList<TransactionOutput> Output= new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, int value, ArrayList Input) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.Input = Input;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calulateHash() {
        sequence++; // increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence);
    }

    // Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {

        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    // Verifies the data we signed hasnt been tampered with
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
                + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    //Returns true if new transaction could be created.
    public boolean processTransaction() {

        if(verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }


        Object[] items = Input.getItems();
        TransactionInput[] inputs = new TransactionInput[items.length];
        for (int i = 0; i < items.length; i++) {
            inputs[i] = (TransactionInput) items[i];
            if (inputs[i] != null) {
                inputs[i].UTXO = Main.UTXOs.get(inputs[i].transactionOutputId);
            }
        }



        //generate transaction outputs:
        int leftOver = Main.Sender_Balance - value; //get value of inputs then the left over change:
        transactionId = calulateHash();
        Output.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
        Output.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender



        Object[] outputArray = Output.getItems();
        for(Object obj : outputArray) {
            if (obj instanceof TransactionOutput) {
                TransactionOutput o = (TransactionOutput) obj;
                Main.UTXOs.put(o.id, o);
            }
        }

        Object[] inputArray = Input.getItems();
        for(Object obj : inputArray) {
            if (obj instanceof TransactionInput) {
                TransactionInput i = (TransactionInput) obj;
                if (i.UTXO == null) continue; //if Transaction can't be found skip it
                Main.UTXOs.remove(i.UTXO.id);
            }
        }

        return true;
    }

    public int getInputsValue() {
        int total = 0;
        Object[] items = Input.getItems();
        TransactionInput[] inputs = new TransactionInput[items.length];
        for (int i = 0; i < items.length; i++) {
            inputs[i] = (TransactionInput) items[i];
        }
        for (TransactionInput i : inputs) {
            if (i == null || i.UTXO == null) continue; //if TransactionInput or UTXO is null, skip it
            total += i.UTXO.value;
        }
        return total;
    }


    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : Output.getItems()) {
            total += o.value;
        }
        return total;
    }
}