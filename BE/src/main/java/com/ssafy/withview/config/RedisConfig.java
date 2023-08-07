package com.ssafy.withview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ssafy.withview.service.pubsub.RedisSubscriber;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private Integer port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public ChannelTopic channelChattingTopic() {
		return new ChannelTopic("channelChatting");
	}

	@Bean
	public ChannelTopic channelValueChannelTopic() {
		return new ChannelTopic("channelValue");
	}

	@Bean
	public ChannelTopic friendsChattingTopic() {
		return new ChannelTopic("friendsChatting");
	}

	@Bean
	public MessageListenerAdapter channelChattingListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendChannelMessage");
	}

	@Bean
	public MessageListenerAdapter friendsChattingListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendFriendsMessage");
	}

	@Bean
	public MessageListenerAdapter channelValueListenerAdapter(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendChannelValue");
	}

	// redis pub/sub 메시지를 처리하는 listener 설정
	@Bean
	public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
		MessageListenerAdapter channelChattingListenerAdapter, MessageListenerAdapter channelValueListenerAdapter,
		MessageListenerAdapter friendsChattingListenerAdapter, ChannelTopic channelChattingTopic,
		ChannelTopic channelValueChannelTopic, ChannelTopic friendsChattingTopic) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(channelChattingListenerAdapter, channelChattingTopic);
		container.addMessageListener(friendsChattingListenerAdapter, friendsChattingTopic);
		container.addMessageListener(channelValueListenerAdapter, channelValueChannelTopic);
		return container;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		// redisTemplate.setKeySerializer(new StringRedisSerializer());
		// redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
		// redisTemplate.setConnectionFactory(redisConnectionFactory());
		// redisTemplate.setEnableTransactionSupport(true);

		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
		return redisTemplate;
	}
}
