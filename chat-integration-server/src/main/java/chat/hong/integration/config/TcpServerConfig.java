package chat.hong.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayStxEtxSerializer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
public class TcpServerConfig {

    @Bean
    public AbstractServerConnectionFactory serverConnectionFactory() {
        return new TcpNetServerConnectionFactory(1234);
    }

    @Bean
    public TcpInboundGateway tcpInboundGateway(AbstractServerConnectionFactory serverConnectionFactory) {
        TcpInboundGateway gateway = new TcpInboundGateway();
        gateway.setConnectionFactory(serverConnectionFactory);
        gateway.setRequestChannel(receiveChannel());
        return gateway;
    }

    @Bean
    public DirectChannel receiveChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "receiveChannel")
    public MessageHandler messagehandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                // 处理收到的消息
            }
        };
    }

    @Bean
    @Transformer(inputChannel = "receiveChannel", outputChannel = "tcpReplyChannel")
    public ByteArrayCrLfSerializer byteArrayCrLfSerializer() {
        return new ByteArrayCrLfSerializer();
    }

    @Bean
    public DirectChannel tcpReplyChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "tcpReplyChannel")
    public ByteArrayStxEtxSerializer byteArrayStxEtxSerializer() {
        return new ByteArrayStxEtxSerializer();
    }

    @ServiceActivator(inputChannel = "receiveChannel")
    public void handleMessage(byte[] messageBytes) {
        String message = new String(messageBytes);
        //处理接收到的消息
        String reply = "Reply: " + message;
        tcpReplyChannel().send(MessageBuilder.withPayload(reply.getBytes()).build());
    }

}