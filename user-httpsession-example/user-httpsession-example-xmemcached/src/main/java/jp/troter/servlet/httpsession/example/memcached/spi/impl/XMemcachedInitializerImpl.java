package jp.troter.servlet.httpsession.example.memcached.spi.impl;

import java.io.IOException;

import jp.troter.servlet.httpsession.spi.XMemcachedInitializer;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class XMemcachedInitializerImpl extends XMemcachedInitializer {

    MemcachedClient c = null;

    @Override
    public MemcachedClient getMemcachedClient() {
        return getMemcachedClientInternal();
    }

    public synchronized MemcachedClient getMemcachedClientInternal() {
        if (c == null) {
            try {
                c = getXMemcachedClientBuilder().build();
            } catch (IOException e) {
                // ignore
            }
        }
        return c;
    }

    public XMemcachedClientBuilder getXMemcachedClientBuilder() {
        XMemcachedClientBuilder builder = new net.rubyeye.xmemcached.XMemcachedClientBuilder(
            AddrUtil.getAddresses("localhost:11211"),
            new int[] {1});
        BinaryCommandFactory commandFactory = new BinaryCommandFactory();
        KetamaMemcachedSessionLocator sessionLocator = new KetamaMemcachedSessionLocator();
        SerializingTranscoder transcoder = new SerializingTranscoder();
        builder.setCommandFactory(commandFactory);
        builder.setSessionLocator(sessionLocator);
        builder.setTranscoder(transcoder);
        return builder;
    }
}
