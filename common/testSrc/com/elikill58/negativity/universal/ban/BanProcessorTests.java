package com.elikill58.negativity.universal.ban;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import com.elikill58.negativity.testFramework.BanProcessorTestInvocationContextProvider;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

@ExtendWith(BanProcessorTestInvocationContextProvider.class)
public class BanProcessorTests {
	
	@TestTemplate
	public void sameBanTwice(BanProcessor processor) {
		UUID playerId = UUID.randomUUID();
		long executionTime = System.currentTimeMillis();
		long expirationTime = executionTime + 10_000;
		Ban originalBan = new Ban(playerId, "Ban reason", "Ban source", BanType.MOD, expirationTime, "Cheat 1, Cheat 2", null, BanStatus.ACTIVE, executionTime);
		BanResult result = processor.executeBan(originalBan);
		Assertions.assertEquals(new BanResult(BanResult.BanResultType.DONE, originalBan), result);
		BanResult result2 = processor.executeBan(originalBan);
		Assertions.assertEquals(new BanResult(BanResult.BanResultType.ALREADY_BANNED, null), result2);
	}
}
