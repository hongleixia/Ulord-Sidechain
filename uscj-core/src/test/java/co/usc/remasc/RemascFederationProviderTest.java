package co.usc.remasc;

import co.usc.ulordj.store.BlockStoreException;
import co.usc.blockchain.utils.BlockGenerator;
import co.usc.config.TestSystemProperties;
import co.usc.peg.BridgeSupport;
import co.usc.test.builders.BlockChainBuilder;
import co.usc.blockchain.utils.BlockGenerator;
import co.usc.peg.BridgeSupport;
import org.ethereum.core.Blockchain;
import org.ethereum.core.Genesis;
import org.ethereum.vm.PrecompiledContracts;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 14/11/2017.
 */
public class RemascFederationProviderTest {
    @Test
    public void getDefaultFederationSize() throws IOException, BlockStoreException {
        RemascFederationProvider provider = getRemascFederationProvider();
        Assert.assertEquals(3, provider.getFederationSize());
    }

    @Test
    public void getFederatorAddress() throws IOException, BlockStoreException {
        RemascFederationProvider provider = getRemascFederationProvider();

        byte[] address = provider.getFederatorAddress(0).getBytes();

        Assert.assertNotNull(address);
        Assert.assertEquals(20, address.length);
    }

    private static RemascFederationProvider getRemascFederationProvider() throws IOException, BlockStoreException {
        Genesis genesisBlock = new BlockGenerator().getGenesisBlock();
        BlockChainBuilder builder = new BlockChainBuilder().setTesting(true).setGenesis(genesisBlock);
        Blockchain blockchain = builder.build();

        BridgeSupport bridgeSupport = new BridgeSupport(new TestSystemProperties(), blockchain.getRepository(),
                                                        null,
                                                        PrecompiledContracts.BRIDGE_ADDR,
                                                        null);
        RemascFederationProvider provider = null;

        try {
            provider = new RemascFederationProvider(bridgeSupport);
        } catch (BlockStoreException | IOException e) {
            e.printStackTrace();
        }

        return provider;
    }
}