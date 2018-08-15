package co.usc.core;

/**
 * Created by SerAdmin on 12/5/2017.
 */

import com.google.common.collect.Lists;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.RLP;
import org.ethereum.util.RLPList;
import org.spongycastle.pqc.math.linearalgebra.ByteUtils;
import org.spongycastle.util.BigIntegers;

import java.math.BigInteger;
import java.util.List;

import static org.ethereum.crypto.HashUtil.EMPTY_TRIE_HASH;
import static org.ethereum.util.ByteUtil.toHexString;

/**
 * Block header is a value object containing
 * the basic information of a block
 */
public class FreeBlockHeader {


    /* The SHA3 256-bit hash of the parent block, in its entirety */
    private byte[] parentHash;
    /* The SHA3 256-bit hash of the uncles list portion of this block */
    private byte[] unclesHash;
    /* The 160-bit address to which all fees collected from the
     * successful mining of this block be transferred; formally */
    private byte[] coinbase;
    /* The SHA3 256-bit hash of the root node of the state trie,
     * after all transactions are executed and finalisations applied */
    private byte[] stateRoot;
    /* The SHA3 256-bit hash of the root node of the trie structure
     * populated with each transaction in the transaction
     * list portion, the trie is populate by [key, val] --> [rlp(index), rlp(tx_recipe)]
     * of the block */
    private byte[] txTrieRoot;
    /* The SHA3 256-bit hash of the root node of the trie structure
     * populated with each transaction recipe in the transaction recipes
     * list portion, the trie is populate by [key, val] --> [rlp(index), rlp(tx_recipe)]
     * of the block */
    private byte[] receiptTrieRoot;
    /* The bloom filter for the logs of the block */
    private byte[] logsBloom;
    /* A scalar value corresponding to the difficulty level of this block.
     * This can be calculated from the previous block’s difficulty level
     * and the timestamp */
    private byte[] difficulty;
    /* A scalar value equalBytes to the reasonable output of Unix's time()
     * at this block's inception */
    private byte[] timestamp;
    /* A scalar value equalBytes to the number of ancestor blocks.
     * The genesis block has a number of zero */
    private byte[] number;
    /* A scalar value equalBytes to the current limit of gas expenditure per block */
    private byte[] gasLimit;
    /* A scalar value equalBytes to the total gas used in transactions in this block */
    private byte[] gasUsed;
    /* A scalar value equalBytes to the total paid fees in transactions in this block */
    private byte[] paidFees;

    /* An arbitrary byte array containing data relevant to this block.
    * With the exception of the genesis block, this must be 32 bytes or fewer */
    private byte[] extraData;

    /* The 81-byte ulord block header for merged mining */
    private byte[] ulordMergedMiningHeader;
    /* The ulord merkle proof of coinbase tx for merged mining */
    private byte[] ulordMergedMiningMerkleProof;
    /* The ulord protobuf serialized coinbase tx for merged mining */
    private byte[] ulordMergedMiningCoinbaseTransaction;
    /*The mgp for a tx to be included in the block*/
    private byte[] minimumGasPrice;
    private byte[] uncleCount;

    public FreeBlockHeader(byte[] encoded) {
        this((RLPList) RLP.decode2(encoded).get(0));
    }

    public FreeBlockHeader(RLPList rlpHeader) {
        this.parentHash = rlpHeader.get(0).getRLPData();
        this.unclesHash = rlpHeader.get(1).getRLPData();
        this.coinbase = rlpHeader.get(2).getRLPData();
        this.stateRoot = rlpHeader.get(3).getRLPData();
        if (this.stateRoot == null)
            this.stateRoot = EMPTY_TRIE_HASH;

        this.txTrieRoot = rlpHeader.get(4).getRLPData();
        if (this.txTrieRoot == null)
            this.txTrieRoot = EMPTY_TRIE_HASH;

        this.receiptTrieRoot = rlpHeader.get(5).getRLPData();
        if (this.receiptTrieRoot == null)
            this.receiptTrieRoot = EMPTY_TRIE_HASH;

        this.logsBloom = rlpHeader.get(6).getRLPData();
        this.difficulty = rlpHeader.get(7).getRLPData();

        byte[] nrBytes = rlpHeader.get(8).getRLPData();
        byte[] glBytes = rlpHeader.get(9).getRLPData();
        byte[] guBytes = rlpHeader.get(10).getRLPData();
        byte[] tsBytes = rlpHeader.get(11).getRLPData();

        this.number = (nrBytes);

        this.gasLimit = glBytes;
        this.gasUsed = (guBytes);
        this.timestamp = (tsBytes);

        this.extraData = rlpHeader.get(12).getRLPData();

        byte[] pfBytes = rlpHeader.get(13).getRLPData();
        this.paidFees = (pfBytes);
        this.minimumGasPrice = rlpHeader.get(14).getRLPData();

        int r = 15;

        if ((rlpHeader.size() == 19) || (rlpHeader.size() == 16)) {
            byte[] ucBytes = rlpHeader.get(r++).getRLPData();
            this.uncleCount = (ucBytes);
        }

        if (rlpHeader.size() > r) {
            this.ulordMergedMiningHeader = rlpHeader.get(r++).getRLPData();
            this.ulordMergedMiningMerkleProof = rlpHeader.get(r++).getRLPData();
            this.ulordMergedMiningCoinbaseTransaction = rlpHeader.get(r++).getRLPData();

        }

    }

    private long parseLong(byte[] nrBytes) {

        if (nrBytes == null) return 0;
        BigInteger b = BigIntegers.fromUnsignedByteArray(nrBytes);
        return b.longValueExact();
    }

    private int parseInt(byte[] nrBytes) {

        if (nrBytes == null) return 0;
        BigInteger b = BigIntegers.fromUnsignedByteArray(nrBytes);
        return b.intValueExact();
    }

    public FreeBlockHeader(byte[] parentHash, byte[] unclesHash, byte[] coinbase,
                       byte[] logsBloom, byte[] difficulty, byte[] number,
                       byte[] gasLimit, byte[] gasUsed, byte[] timestamp,
                       byte[] extraData,
                       byte[] minimumGasPrice,
                           byte[] uncleCount) {
        this.parentHash = parentHash;
        this.unclesHash = unclesHash;
        this.coinbase = coinbase;
        this.logsBloom = logsBloom;
        this.difficulty = difficulty;
        this.number = number;
        this.gasLimit = gasLimit;
        this.gasUsed = gasUsed;
        this.timestamp = timestamp;
        this.extraData = extraData;
        this.stateRoot = ByteUtils.clone(EMPTY_TRIE_HASH);
        this.minimumGasPrice = minimumGasPrice;
        this.receiptTrieRoot = ByteUtils.clone(EMPTY_TRIE_HASH);
        this.uncleCount = uncleCount;
    }

    public FreeBlockHeader(byte[] parentHash, byte[] unclesHash, byte[] coinbase,
                       byte[] logsBloom, byte[] difficulty, byte[] number,
                       byte[] gasLimit, byte[] gasUsed, byte[] timestamp,
                       byte[] extraData,
                       byte[] ulordMergedMiningHeader, byte[] ulordMergedMiningMerkleProof,
                       byte[] ulordMergedMiningCoinbaseTransaction,
                       byte[] minimumGasPrice,
                           byte[] uncleCount) {
        this.parentHash = parentHash;
        this.unclesHash = unclesHash;
        this.coinbase = coinbase;
        this.logsBloom = logsBloom;
        this.difficulty = difficulty;
        this.number = number;
        this.gasLimit = gasLimit;
        this.gasUsed = gasUsed;
        this.timestamp = timestamp;
        this.extraData = extraData;
        this.stateRoot = ByteUtils.clone(EMPTY_TRIE_HASH);
        this.ulordMergedMiningHeader = ulordMergedMiningHeader;
        this.ulordMergedMiningMerkleProof = ulordMergedMiningMerkleProof;
        this.ulordMergedMiningCoinbaseTransaction = ulordMergedMiningCoinbaseTransaction;
        this.minimumGasPrice = minimumGasPrice;
        this.receiptTrieRoot = ByteUtils.clone(EMPTY_TRIE_HASH);
        this.uncleCount = uncleCount;
    }


    public byte[] getParentHash() {
        return parentHash;
    }

    public byte[] getUncleCount() {
        return uncleCount;
    }

    public byte[] getUnclesHash() {
        return unclesHash;
    }

    public void setUnclesHash(byte[] unclesHash) {

        this.unclesHash = unclesHash;
    }

    public byte[] getCoinbase() {
        return coinbase;
    }

    public void setCoinbase(byte[] coinbase) {

        this.coinbase = coinbase;
    }

    public byte[] getStateRoot() {
        return stateRoot;
    }

    public void setStateRoot(byte[] stateRoot) {

        this.stateRoot = stateRoot;
    }

    public byte[] getTxTrieRoot() {
        return txTrieRoot;
    }

    public void setReceiptsRoot(byte[] receiptTrieRoot) {

        this.receiptTrieRoot = receiptTrieRoot;
    }

    public byte[] getReceiptsRoot() {
        return receiptTrieRoot;
    }

    public void setTransactionsRoot(byte[] stateRoot) {

        this.txTrieRoot = stateRoot;
    }


    public byte[] getLogsBloom() {
        return logsBloom;
    }

    public byte[] getDifficulty() {
        return difficulty;
    }

    public BigInteger getDifficultyBI() {
        return new BigInteger(1, difficulty);
    }

    public void setDifficulty(byte[] difficulty) {

        this.difficulty = difficulty;
    }

    public byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(byte[] timestamp) {

        this.timestamp = timestamp;
    }

    public byte[] getNumber() {
        return number;
    }

    public void setNumber(byte[] number) {

        this.number = number;
    }

    public byte[] getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(byte[] gasLimit) {

        this.gasLimit = gasLimit;
    }

    public byte[] getGasUsed() {
        return gasUsed;
    }

    public void setPaidFees(byte[] paidFees) {

        this.paidFees = paidFees;
    }

    public byte[] getPaidFees() {
        return this.paidFees;
    }

    public void setGasUsed(byte[] gasUsed) {

        this.gasUsed = gasUsed;
    }

    public byte[] getExtraData() {
        return extraData;
    }

    public void setLogsBloom(byte[] logsBloom) {

        this.logsBloom = logsBloom;
    }

    public void setExtraData(byte[] extraData) {

        this.extraData = extraData;
    }

    public byte[] getHash() {
        return HashUtil.keccak256(getEncoded());
    }

    public byte[] getEncoded() {
        return this.getEncoded(true); // with nonce
    }

    public byte[] getEncodedWithoutNonceMergedMiningFields() {
        return this.getEncoded(false);
    }

    public byte[] getMinimumGasPrice() {
        return this.minimumGasPrice;
    }

    public void setMinimumGasPrice(byte[] minimumGasPrice) {

        this.minimumGasPrice = minimumGasPrice;
    }

    public byte[] getEncoded(boolean withMergedMiningFields) {
        byte[] parentHash = RLP.encodeElement(this.parentHash);

        byte[] unclesHash = RLP.encodeElement(this.unclesHash);
        byte[] coinbase = RLP.encodeElement(this.coinbase);

        byte[] stateRoot = RLP.encodeElement(this.stateRoot);

        if (txTrieRoot == null) {
            this.txTrieRoot = EMPTY_TRIE_HASH;
        }

        byte[] txTrieRoot = RLP.encodeElement(this.txTrieRoot);

        if (receiptTrieRoot == null) {
            this.receiptTrieRoot = EMPTY_TRIE_HASH;
        }

        byte[] receiptTrieRoot = RLP.encodeElement(this.receiptTrieRoot);

        byte[] logsBloom = RLP.encodeElement(this.logsBloom);
        byte[] difficulty = RLP.encodeElement(this.difficulty);
        byte[] number = RLP.encodeElement(this.number);
        byte[] gasLimit = RLP.encodeElement(this.gasLimit);
        byte[] gasUsed = RLP.encodeElement(this.gasUsed);
        byte[] timestamp = RLP.encodeElement(this.timestamp);
        byte[] extraData = RLP.encodeElement(this.extraData);
        byte[] paidFees = RLP.encodeElement(this.paidFees);
        byte[] mgp = RLP.encodeElement(this.minimumGasPrice);
        List<byte[]> fieldToEncodeList = Lists.newArrayList(parentHash, unclesHash, coinbase,
                stateRoot, txTrieRoot, receiptTrieRoot, logsBloom, difficulty, number,
                gasLimit, gasUsed, timestamp, extraData, paidFees, mgp);

        byte[] uncleCount = RLP.encodeElement(this.uncleCount);
        fieldToEncodeList.add(uncleCount);

        if (withMergedMiningFields && hasMiningFields()) {
            byte[] ulordMergedMiningHeader = RLP.encodeElement(this.ulordMergedMiningHeader);
            fieldToEncodeList.add(ulordMergedMiningHeader);
            byte[] ulordMergedMiningMerkleProof = RLP.encodeElement(this.ulordMergedMiningMerkleProof);
            fieldToEncodeList.add(ulordMergedMiningMerkleProof);
            byte[] ulordMergedMiningCoinbaseTransaction = RLP.encodeElement(this.ulordMergedMiningCoinbaseTransaction);
            fieldToEncodeList.add(ulordMergedMiningCoinbaseTransaction);
        }


        return RLP.encodeList(fieldToEncodeList.toArray(new byte[][]{}));
    }

    // Warining: This method does not uses the object's attributes
    public static byte[] getUnclesEncodedEx(List<FreeBlockHeader> uncleList) {

        byte[][] unclesEncoded = new byte[uncleList.size()][];
        int i = 0;
        for (FreeBlockHeader uncle : uncleList) {
            unclesEncoded[i] = uncle.getEncoded();
            ++i;
        }
        return RLP.encodeList(unclesEncoded);
    }

    public boolean hasMiningFields() {
        if (this.ulordMergedMiningCoinbaseTransaction != null && this.ulordMergedMiningCoinbaseTransaction.length > 0)
            return true;

        if (this.ulordMergedMiningHeader != null && this.ulordMergedMiningHeader.length > 0)
            return true;

        if (this.ulordMergedMiningMerkleProof != null && this.ulordMergedMiningMerkleProof.length > 0)
            return true;

        return false;
    }

    public static byte[] getUnclesEncoded(List<FreeBlockHeader> uncleList) {

        byte[][] unclesEncoded = new byte[uncleList.size()][];
        int i = 0;
        for (FreeBlockHeader uncle : uncleList) {
            unclesEncoded[i] = uncle.getEncoded();
            ++i;
        }
        return RLP.encodeList(unclesEncoded);
    }

    public String toString() {
        return toStringWithSuffix("\n");
    }

    private String toStringWithSuffix(final String suffix) {
        StringBuilder toStringBuff = new StringBuilder();
        toStringBuff.append("  parentHash=").append(toHexString(parentHash)).append(suffix);
        toStringBuff.append("  unclesHash=").append(toHexString(unclesHash)).append(suffix);
        toStringBuff.append("  coinbase=").append(toHexString(coinbase)).append(suffix);
        toStringBuff.append("  stateRoot=").append(toHexString(stateRoot)).append(suffix);
        toStringBuff.append("  txTrieHash=").append(toHexString(txTrieRoot)).append(suffix);
        toStringBuff.append("  receiptsTrieHash=").append(toHexString(receiptTrieRoot)).append(suffix);
        toStringBuff.append("  difficulty=").append(toHexString(difficulty)).append(suffix);
        toStringBuff.append("  number=").append(toHexString(number)).append(suffix);
        toStringBuff.append("  gasLimit=").append(toHexString(gasLimit)).append(suffix);
        toStringBuff.append("  gasUsed=").append(toHexString(gasUsed)).append(suffix);
        toStringBuff.append("  timestamp=").append(toHexString(timestamp)).append(")").append(suffix);
        toStringBuff.append("  extraData=").append(toHexString(extraData)).append(suffix);
        toStringBuff.append("  minGasPrice=").append(toHexString(minimumGasPrice)).append(suffix);

        return toStringBuff.toString();
    }

    public String toFlatString() {
        return toStringWithSuffix("");
    }

    public byte[] getRawHash() {
        return getHash();
    }

    public byte[] getEncodedRaw() {
        return getEncoded();
    }

    public byte[] getUlordMergedMiningHeader() {
        return ulordMergedMiningHeader;
    }

    public void setUlordMergedMiningHeader(byte[] ulordMergedMiningHeader) {
        this.ulordMergedMiningHeader = ulordMergedMiningHeader;
    }

    public byte[] getUlordMergedMiningMerkleProof() {
        return ulordMergedMiningMerkleProof;
    }

    public void setUlordMergedMiningMerkleProof(byte[] ulordMergedMiningMerkleProof) {
        this.ulordMergedMiningMerkleProof = ulordMergedMiningMerkleProof;
    }

    public byte[] getUlordMergedMiningCoinbaseTransaction() {
        return ulordMergedMiningCoinbaseTransaction;
    }

    public void setUlordMergedMiningCoinbaseTransaction(byte[] ulordMergedMiningCoinbaseTransaction) {
        this.ulordMergedMiningCoinbaseTransaction = ulordMergedMiningCoinbaseTransaction;
    }

    public String getShortHashForMergedMining() {
        return HashUtil.shortHash(getHashForMergedMining());
    }

    public byte[] getHashForMergedMining() {
        return HashUtil.keccak256(getEncoded(false));
    }

    public String getShortHash() {
        return HashUtil.shortHash(getHash());
    }
}
