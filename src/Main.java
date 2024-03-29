import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

import org.bouncycastle.*;

import java.security.Security;

import java.io.File;


public class Main {
    public static HashMap<String, TransactionOutput> UTXOs =  new HashMap<>();
    public static DoublyLinkedList blockchain = new DoublyLinkedList();
    public static int difficulty = 5;
    public static int minimumTransaction = 1;
    public static boolean EXIT = false;
    private static Scanner entry = new Scanner(System.in);
    public static wallet sender;
    public static int Sender_Balance;
    public static wallet coinbase = Saver.CallWallet("", true);


    public static void main(String[] args) {
        UTXOs = Saver.CallUTXO();
        blockchain = Saver.CallBlockChain();

        if (blockchain.IsEmpty()) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            wallet walletA = new wallet();
            coinbase = new wallet();
            //create genesis transaction, making a Coinbase that holds 10000000 amount of coins:
            Transaction genesisTransaction = new Transaction(walletA.publicKey, coinbase.publicKey, 10000000, null);
            genesisTransaction.generateSignature(walletA.privateKey);     //manually sign the genesis transaction
            genesisTransaction.transactionId = "0"; //manually set the transaction id
            genesisTransaction.Output.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
            UTXOs.put(genesisTransaction.Output.get(0).id, genesisTransaction.Output.get(0)); //its important to store our first transaction in the UTXOs list.
            Block genesis = new Block("0");
            genesis.addTransaction(genesisTransaction);
            addBlock(genesis);

            Saver.SaveWallet(coinbase, true);
        }

        while(!EXIT){
            Start();
        }

    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.addLast(newBlock);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.getSize(); i++) {
            currentBlock = blockchain.getElement(i);
            previousBlock = blockchain.getElement(i - 1);
            // compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            // compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            // check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

    private static void Start(){
        System.out.println("MSCOINS.COM");
        System.out.println("1. Register (new user get 2 MS for free!)");
        System.out.println("2. Send Money (whole number)");
        System.out.println("3. Balance enquiry");
        System.out.println("4. Quit");

        int option = entry.nextInt();
        entry.nextLine(); // added line
        switch (option) {
            case 1:
                Register();
                break;
            case 2:
                SendMoney();
                break;
            case 3:
                BalanceEnquiry();
                break;
            case 4:
                Quit();
                break;
            default:
                break;
        }
    }

    private static void Register(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Create the new wallets
        wallet w = new wallet();
        System.out.println("Generated public key and private key, if you lose your private key, then you lose access to your assets.");
        System.out.println("Your wallet keys are saved in MyKeys.txt file.");
        System.out.println("Your wallet is inserted into the database Successfully!");
        System.out.println("<PUBLIC KEY: "+StringUtil.getStringFromKey(w.publicKey)+", PRIVATE KEY: "+StringUtil.getStringFromKey(w.privateKey)+">");


        Sender_Balance = coinbase.getBalance();
        Block b = blockchain.getElement(blockchain.getSize() - 1);
        Block newBlock = new Block(b.hash);
        newBlock.addTransaction(coinbase.sendFunds(w.publicKey, 2));
        addBlock(newBlock);


        System.out.println("Your Balance: " + w.getBalance() + " MS");
        Saver.SaveWallet(w, false);
        Generate_TXT_file_Account(StringUtil.getStringFromKey(w.publicKey), StringUtil.getStringFromKey(w.privateKey));
        System.out.println("");
    }

    private static void SendMoney() {

        System.out.println("Enter Your Public Key:");

        String Sender_Public = entry.nextLine();

        sender = Saver.CallWallet(Sender_Public, false);

        if (sender != null) {
            System.out.println("Enter Your Private Key:");

            String Sender_Private = entry.nextLine();

            if (Sender_Private.equals(StringUtil.getStringFromKey(sender.privateKey))) {
                System.out.println("Your Balance Is: ");
                System.out.println(sender.getBalance());
                System.out.println("Enter The amount you want to send:");
                int amount = entry.nextInt();
                entry.nextLine(); // added line
                System.out.println("Enter The Receiver Public Key:");
                String Receiver_Public = entry.nextLine();
                if (amount >= minimumTransaction) {
                    wallet receiver = Saver.CallWallet(Receiver_Public, false);
                    if (receiver != null) {
                        Sender_Balance = sender.getBalance();

                        Block b = blockchain.getElement(blockchain.getSize() - 1);

                        Block newBlock = new Block(b.hash);

                        newBlock.addTransaction(sender.sendFunds(receiver.publicKey, amount));
                        addBlock(newBlock);
                        System.out.println("Transaction approved. Your remaining balance: " + sender.getBalance() + " MS");
                    } else {
                        System.out.println("You have entered an Invalid Public Key!");
                    }
                } else {
                    System.out.println("You have Entered a less than the Minimum Amount!");
                }
            } else {
                System.out.println("The Private Key You Entered Is Incorrect!");
            }
        }else{
            System.out.println("Your Public Key is Invalid!");
        }
    }

    private static void BalanceEnquiry() {
        System.out.println("Enter Your Public Key:");

        String Public_Key = entry.nextLine();

        wallet w = Saver.CallWallet(Public_Key, false);
        if (w != null) {
            System.out.println("Your Balance:");
            System.out.println(w.getBalance() + " MS");
        } else {
            System.out.println("The Public Key Is Invalid!");
        }
    }

    private static void Quit() {
        EXIT = true;
        System.out.println("Data saved");
        Saver.SaveBlockChain(blockchain);
        Saver.SaveUTXO(UTXOs);
    }

    private static void Generate_TXT_file_Account(String publick, String privatek){
        try {
            File keys = new File("MyKeys.txt");
            if (keys.createNewFile()) {
                FileWriter writer = new FileWriter(keys, true);
                writer.write("<PUBLIC KEY: "+publick+", PRIVATE KEY: "+privatek+">\n");
                writer.close();
            } else {
                FileWriter writer = new FileWriter(keys, true);
                writer.write("<PUBLIC KEY: "+publick+", PRIVATE KEY: "+privatek+">\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
