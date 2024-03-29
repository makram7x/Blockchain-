import java.io.Serializable;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
public class wallet implements Serializable {

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public wallet() {
        generateKeyPair();
    }

    public wallet(String publick, String privatek){
        try {
            Security.addProvider(new BouncyCastleProvider());
            // Parse the public key string
            byte[] publicKeyBytes = Base64.getDecoder().decode(publick);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
            this.publicKey = keyFactory.generatePublic(publicKeySpec);

            // Parse the private key string
            byte[] privateKeyBytes = Base64.getDecoder().decode(privatek);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("The specified algorithm is not available: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.err.println("The key specification is invalid: " + e.getMessage());
        }
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    //returns balance and stores the UTXO's owned by this wallet in this.UTXOs
    public int getBalance() {
        int total = 0;
        for (HashMap.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                Main.UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }
    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient,int value ) {
        if(getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        int total = 0;
        for (HashMap.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        Object[] inputArray = inputs.getItems();
        TransactionInput[] transactionInputs = new TransactionInput[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            transactionInputs[i] = (TransactionInput) inputArray[i];
        }
        for (TransactionInput input : transactionInputs) {
            if (input != null) {
                Main.UTXOs.remove(input.transactionOutputId);
            }
        }
        return newTransaction;
    }
}
