package com.myproject.systemdemo.thriftDemo;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class thriftTools {

    private TTransport transport = new TSocket("127.0.0.1", 9000);
    private TProtocol protocol = new TBinaryProtocol(transport);
    private format_data.Client client = new format_data.Client(protocol);
    private TransmitMes transmitMes = new TransmitMes();

//    static{
//        TTransport transport;
//        TProtocol protocol;
//        format_data.Client client;
//        TransmitMes transmitMes;
//    }

    public  TTransport getTransport() {
        return transport;
    }

    public  TProtocol getProtocol() {
        return protocol;
    }

    public   format_data.Client getClient() {
        return client;
    }

    public  TransmitMes getTransmitMes() {
        return transmitMes;
    }
}
